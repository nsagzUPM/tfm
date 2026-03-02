-- =========================
-- USERS
-- =========================
INSERT INTO users (id, external_id, name, role)
VALUES (1, 'local-user-001', 'Local User', 'USER'),
       (2, 'prueba@prueba.com', 'Prueba Prueba', 'LIBRARIAN');

-- =========================
-- BOOKS
-- =========================
INSERT INTO books (id, title, author, subject)
VALUES (1, 'Clean Code', 'Robert C. Martin', 'Software Engineering'),
       (2, 'Domain-Driven Design', 'Eric Evans', 'Software Architecture'),
       (3, 'The Pragmatic Programmer', 'Andrew Hunt; David Thomas', 'Programming'),
       (4, 'Introduction to Algorithms', 'Cormen; Leiserson; Rivest; Stein', 'Algorithms'),
       (5, 'Design Patterns', 'Gamma; Helm; Johnson; Vlissides', 'Object-Oriented Design');

-- =========================
-- COPIES
-- status: AVAILABLE, RESERVED, LOANED
-- reference_only: true/false
-- =========================
INSERT INTO copies (id, book_id, status, reference_only, location)
VALUES (1, 1, 'AVAILABLE', FALSE, 'A1-01'),
       (2, 1, 'LOANED', FALSE, 'A1-02'),
       (3, 2, 'AVAILABLE', FALSE, 'A2-01'),
       (4, 2, 'RESERVED', FALSE, 'A2-02'),
       (5, 3, 'AVAILABLE', FALSE, 'A3-01'),
       (6, 3, 'UNAVAILABLE', TRUE, 'REF-01'),
       (7, 4, 'LOANED', FALSE, 'A4-01'),
       (8, 5, 'AVAILABLE', FALSE, 'A5-01');

-- =========================
-- LOANS
-- closed: true/false
-- (Copy 2 and Copy 7 are LOANED)
-- =========================
INSERT INTO loans (user_id, copy_id, start_date, due_date, renewals, closed)
VALUES (2, 2, DATE '2026-01-10', DATE '2026-01-24', 0, FALSE),
       (1, 7, DATE '2026-01-01', DATE '2026-02-15', 1, TRUE),
       (2, 7, DATE '2026-01-01', DATE '2026-02-15', 1, FALSE);

-- =========================
-- RESERVATIONS
-- status: ACTIVE, CANCELED, FULFILLED, EXPIRED
-- (Copy 4 is RESERVED)
-- =========================
INSERT INTO reservations (user_id, copy_id, status, created_at, deadline)
VALUES (1, 4, 'ACTIVE', TIMESTAMP '2026-01-20 10:00:00', DATE '2026-01-14'),
       (1, 3, 'CANCELED', TIMESTAMP '2026-01-18 09:00:00', DATE '2026-02-14'),
       (2, 3, 'CANCELED', TIMESTAMP '2026-01-18 09:00:00', DATE '2026-02-14');

-- =========================
-- PENALTIES
-- active: true/false
-- =========================
INSERT INTO penalties (user_id, start_date, end_date, active, reason)
VALUES (1, DATE '2026-01-05', DATE '2026-02-09', TRUE, 'Old penalty (expired)'),
       (1, DATE '2026-01-16', DATE '2026-01-23', FALSE, 'Late return'),
       (2, DATE '2026-01-16', DATE '2026-02-09', TRUE, 'Late return');

INSERT INTO system_config (id, student_loan_days, professor_loan_days, penalty_days_per_late_day, reservation_days, version)
VALUES (1, 15, 30, 2, 3, 0);
