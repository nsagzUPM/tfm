// src/main/resources/static/js/app.js

(function () {
    // ------- Helpers -------
    const $ = (sel, root = document) => root.querySelector(sel);
    const $$ = (sel, root = document) => Array.from(root.querySelectorAll(sel));

    function debounce(fn, ms = 250) {
        let t;
        return (...args) => {
            clearTimeout(t);
            t = setTimeout(() => fn(...args), ms);
        };
    }

    function toast(message, type = "info") {
        let host = $("#toast-host");
        if (!host) {
            host = document.createElement("div");
            host.id = "toast-host";
            host.style.position = "fixed";
            host.style.right = "16px";
            host.style.bottom = "16px";
            host.style.display = "grid";
            host.style.gap = "10px";
            host.style.zIndex = "9999";
            document.body.appendChild(host);
        }

        const el = document.createElement("div");
        el.textContent = message;
        el.style.padding = "12px 14px";
        el.style.borderRadius = "12px";
        el.style.border = "1px solid rgba(255,255,255,.18)";
        el.style.background = "rgba(15,26,47,.92)";
        el.style.backdropFilter = "blur(10px)";
        el.style.boxShadow = "0 18px 45px rgba(0,0,0,.35)";
        el.style.maxWidth = "340px";

        if (type === "ok") el.style.outline = "2px solid rgba(52,211,153,.35)";
        if (type === "bad") el.style.outline = "2px solid rgba(251,113,133,.35)";
        if (type === "warn") el.style.outline = "2px solid rgba(251,191,36,.35)";

        host.appendChild(el);
        setTimeout(() => el.remove(), 2800);
    }

    async function confirmDialog(message) {
        // Simple y efectivo; puedes sustituirlo por un modal luego.
        return window.confirm(message);
    }

    async function api(url, { method = "GET", body, headers } = {}) {
        const opts = { method, headers: headers || {} };

        // Si mandas JSON:
        if (body && !(body instanceof FormData)) {
            opts.headers["Content-Type"] = "application/json";
            opts.body = JSON.stringify(body);
        } else if (body instanceof FormData) {
            opts.body = body;
        }

        // Si usas Spring Security + CSRF, puedes añadir el token aquí (si lo pintas en meta tags)
        const csrf = $('meta[name="_csrf"]')?.content;
        const csrfHeader = $('meta[name="_csrf_header"]')?.content;
        if (csrf && csrfHeader) opts.headers[csrfHeader] = csrf;

        const res = await fetch(url, opts);
        const contentType = res.headers.get("content-type") || "";
        const data = contentType.includes("application/json") ? await res.json() : await res.text();

        if (!res.ok) {
            const msg = typeof data === "string" ? data : (data.message || "Error");
            throw new Error(msg);
        }
        return data;
    }

    // ------- Feature: Catálogo live search -------
    (function initCatalogSearch() {
        const form = document.querySelector("[data-catalog-search]");
        if (!form) return;

        const input = form.querySelector("[data-q]");
        const results = document.querySelector("[data-results]");
        if (!input || !results) return;

        const render = (books) => {
            results.innerHTML = "";
            if (!books.length) {
                results.innerHTML = `<div class="card pad"><p class="p">No hay resultados.</p></div>`;
                return;
            }

            for (const b of books) {
                const el = document.createElement("article");
                el.className = "item";
                el.innerHTML = `
          <div class="thumb" aria-hidden="true"></div>
          <div class="meta">
            <p class="title"></p>
            <p class="sub"></p>
          </div>
          <div class="right">
            <a class="btn">Ver ejemplares →</a>
          </div>
        `;
                el.querySelector(".title").textContent = b.title;
                el.querySelector(".sub").textContent = b.author;
                el.querySelector("a").href = `/books/${b.id}`;
                results.appendChild(el);
            }
        };

        const doSearch = debounce(async () => {
            const q = input.value.trim();
            // Si está vacío, puedes dejar el server-render o mostrar vacío
            if (q.length === 0) return;

            try {
                // Endpoint recomendado: GET /api/catalog?q=...
                const data = await api(`/api/catalog?q=${encodeURIComponent(q)}`);
                // Espera algo como: [{id,title,author}, ...]
                render(Array.isArray(data) ? data : (data.items || []));
            } catch (e) {
                toast(e.message, "bad");
            }
        }, 250);

        input.addEventListener("input", doSearch);
    })();

    // ------- Feature: Reservar (fetch + toast) -------
    (function initReserve() {
        const form = document.querySelector("[data-reserve-form]");
        if (!form) return;

        form.addEventListener("submit", async (ev) => {
            ev.preventDefault();

            const copyId = form.getAttribute("data-copy-id");
            const ok = await confirmDialog(`¿Confirmar reserva del ejemplar ${copyId}?`);
            if (!ok) return;

            try {
                // Endpoint recomendado: POST /api/reservations  {copyId}
                await api("/api/reservations", { method: "POST", body: { copyId } });
                toast("Reserva confirmada ✅", "ok");
                // Redirige a “Mi área” o recarga
                window.location.href = "/my";
            } catch (e) {
                toast(e.message, "bad");
            }
        });
    })();

    // ------- Feature: Renovar préstamo / Cancelar reserva -------
    (function initMyAreaActions() {
        const renewBtns = $$("[data-renew-loan]");
        renewBtns.forEach((btn) => {
            btn.addEventListener("click", async () => {
                const loanId = btn.getAttribute("data-renew-loan");
                const ok = await confirmDialog("¿Renovar este préstamo?");
                if (!ok) return;

                try {
                    // Endpoint recomendado: POST /api/loans/{id}/renew
                    await api(`/api/loans/${encodeURIComponent(loanId)}/renew`, { method: "POST" });
                    toast("Préstamo renovado ✅", "ok");
                    window.location.reload();
                } catch (e) {
                    toast(e.message, "bad");
                }
            });
        });

        const cancelBtns = $$("[data-cancel-reservation]");
        cancelBtns.forEach((btn) => {
            btn.addEventListener("click", async () => {
                const resId = btn.getAttribute("data-cancel-reservation");
                const ok = await confirmDialog("¿Cancelar esta reserva?");
                if (!ok) return;

                try {
                    // Endpoint recomendado: DELETE /api/reservations/{id}
                    await api(`/api/reservations/${encodeURIComponent(resId)}`, { method: "DELETE" });
                    toast("Reserva cancelada", "ok");
                    window.location.reload();
                } catch (e) {
                    toast(e.message, "bad");
                }
            });
        });
    })();

    // ------- Feature: Bibliotecario scan (enter = enviar) -------
    (function initScanUX() {
        const input = document.querySelector("[data-scan-input]");
        if (!input) return;

        input.addEventListener("keydown", (ev) => {
            if (ev.key === "Enter") {
                // deja que el form haga submit normal (fallback)
                // o podrías interceptar y hacer fetch si te interesa
            }
        });
    })();
})();
