package com.upm.library;

import static org.awaitility.Awaitility.await;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import com.upm.library.domain.Loan;
import com.upm.library.domain.Penalty;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@ActiveProfiles("aws")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class ReturnTest extends BaseE2ETest {

    @Test
    void devolverConSancion() {
        openHome();
        loginCognito();
        assertThat(
                penaltyRepository.findByUserIdAndActiveIsTrue(2L),
                is(Collections.emptyList())
        );
        // Devolver
        click(By.cssSelector(".three > .card:nth-child(2)"));
        type(By.name("copyId"), "2");
        click(By.cssSelector(".primary"));
        click(By.cssSelector(".btn:nth-child(5)"));

        assertThat(waitAlert().getText(), is("¿Confirmar devolución del ejemplar?"));
        waitAlert().accept();
        await()
                .atMost(10, TimeUnit.SECONDS)
                .pollInterval(200, TimeUnit.MILLISECONDS)
                .untilAsserted(() -> {
                    var penalties = penaltyRepository.findByUserIdAndActiveIsTrue(2L);
                    assertThat("Debe existir una sanción activa", penalties.isEmpty(), is(false));
                    var penalty = penalties.getFirst();
                    assertThat(
                            penalty.getStartDate(),
                            is(LocalDate.now())
                    );
                    long daysLate = ChronoUnit.DAYS.between(LocalDate.of(2026, 1, 24), LocalDate.now());
                    LocalDate expectedEnd = LocalDate.now().plusDays(systemConfigService.getPenaltyDays() * daysLate);
                    assertThat(penalty.getEndDate(), is(expectedEnd));
                    penalties.forEach(Penalty::deactivate);
                    penaltyRepository.saveAllAndFlush(penalties);
                });

    }

    @Test
    void devolverSinSancion() {
        loanService.createLoan(2L, 5L);
        openHome();
        loginCognito();

        assertThat(
                penaltyRepository.findByUserIdAndActiveIsTrue(2L),
                is(Collections.emptyList())
        );
        Optional<Loan> loan= loanRepository.findByCopyIdAndClosed(5L, false);
        assertThat(loan.isEmpty(), is(false));
        assertThat(
                loan.get().getUser().getId(),
                is(2L)
        );
        // Devolver
        click(By.cssSelector(".three > .card:nth-child(2)"));
        type(By.name("copyId"), "5");
        click(By.cssSelector(".primary"));
        click(By.cssSelector(".btn:nth-child(5)"));

        assertThat(waitAlert().getText(), is("¿Confirmar devolución del ejemplar?"));
        waitAlert().accept();
        await()
                .atMost(10, TimeUnit.SECONDS)
                .pollInterval(200, TimeUnit.MILLISECONDS)
                .untilAsserted(() -> {
                    assertThat(
                            penaltyRepository.findByUserIdAndActiveIsTrue(2L),
                            is(Collections.emptyList())
                    );
                    Optional<Loan> loan1= loanRepository.findByCopyIdAndClosed(5L, false);
                    assertThat(loan1.isEmpty(), is(true));

                });
    }

}