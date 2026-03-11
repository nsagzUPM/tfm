package com.upm.library;

import com.upm.library.domain.Penalty;
import com.upm.library.domain.Reservation;
import com.upm.library.domain.ReservationStatus;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.Collections;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@ActiveProfiles("aws")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class LoanTest extends BaseE2ETest {


    @Test
    void prestamoSinSanciónConReserva() {
        openHome();
        loginCognito();
        reservationService.createReservation(USER, 8L);
        assertThat(
                penaltyRepository.findByUserIdAndActiveIsTrue(2L),
                is(Collections.emptyList())
        );
        assertThat(
                reservationRepository.findByUserIdAndCopyIdAndStatus(2L, 8L, ReservationStatus.ACTIVE).isEmpty(),
                is(false)
        );
        assertThat(
                loanRepository.findByUserIdAndCopyIdAndClosed(2L, 8L, false).isEmpty(),
                is(true)
        );
        click(By.cssSelector(".three > .card:nth-child(1) > .p"));
        type(By.name("userQ"), "a");
        click(By.cssSelector(".primary"));

        click(By.cssSelector(".item:nth-child(2) > .right span"));

        type(By.name("copyCode"), "8");
        click(By.cssSelector(".btn:nth-child(5)"));
        click(By.cssSelector(".btn:nth-child(6)"));

        var loan = loanRepository.findByUserIdAndCopyIdAndClosed(2L, 8L, false);
        assertThat(
                loan.isPresent(),
                is(true)
        );
        var l = loan.get();
        l.close();
        loanRepository.save(l);
    }
    @Test
    void prestamoSinSanciónSinReserva() {
        openHome();
        loginCognito();
        assertThat(
                penaltyRepository.findByUserIdAndActiveIsTrue(2L),
                is(Collections.emptyList())
        );
        assertThat(
                reservationRepository.findByUserIdAndCopyIdAndStatus(2L, 8L, ReservationStatus.ACTIVE).isEmpty(),
                is(true)
        );
        assertThat(
                loanRepository.findByUserIdAndCopyIdAndClosed(2L, 8L, false).isEmpty(),
                is(true)
        );
        click(By.cssSelector(".three > .card:nth-child(1) > .p"));
        type(By.name("userQ"), "a");
        click(By.cssSelector(".primary"));

        click(By.cssSelector(".item:nth-child(2) > .right span"));

        type(By.name("copyCode"), "8");
        click(By.cssSelector(".btn:nth-child(5)"));
        click(By.cssSelector(".btn:nth-child(6)"));

        var loan = loanRepository.findByUserIdAndCopyIdAndClosed(2L, 8L, false);
        assertThat(
                loan.isPresent(),
                is(true)
        );
        var l = loan.get();
        loanService.registerReturn(l.getId());
    }

    @Test
    void prestamoSinSanciónConReservaOtraPersona() {
        openHome();
        loginCognito();
        reservationService.createReservation(OTHER_USER, 1L);
        assertThat(
                penaltyRepository.findByUserIdAndActiveIsTrue(2L),
                is(Collections.emptyList())
        );
        assertThat(
                reservationRepository.findByUserIdAndCopyIdAndStatus(2L, 1L, ReservationStatus.ACTIVE).isEmpty(),
                is(true)
        );
        assertThat(
                loanRepository.findByUserIdAndCopyIdAndClosed(2L, 1L, false).isEmpty(),
                is(true)
        );
        click(By.cssSelector(".three > .card:nth-child(1) > .p"));
        type(By.name("userQ"), "a");
        click(By.cssSelector(".primary"));

        click(By.cssSelector(".item:nth-child(2) > .right span"));

        type(By.name("copyCode"), "1");
        click(By.cssSelector(".btn:nth-child(5)"));
        click(By.cssSelector(".btn:nth-child(6)"));

        assertThat(
                loanRepository.findByUserIdAndCopyIdAndClosed(2L, 1L, false).isEmpty(),
                is(true)
        );
        var reservation = reservationRepository.findByUserIdAndCopyIdAndStatus(1L, 1L, ReservationStatus.ACTIVE).get();
        reservation.cancel();
        reservationRepository.saveAndFlush(reservation);
    }

    @Test
    void prestamoConSanciónSinReserva() {
        openHome();
        loginCognito();
        var user = userRepository.findByExternalId(USER).get();
        Penalty penalty = new Penalty(user, LocalDate.now(), LocalDate.now(), "prueba con sanción");
        penaltyRepository.save(penalty);
        assertThat(
                penaltyRepository.findByUserIdAndActiveIsTrue(2L).isEmpty(),
                is(false)
        );
        assertThat(
                loanRepository.findByUserIdAndCopyIdAndClosed(2L, 5L, false).isEmpty(),
                is(true)
        );
        click(By.cssSelector(".three > .card:nth-child(1) > .p"));
        type(By.name("userQ"), "a");
        click(By.cssSelector(".primary"));

        click(By.cssSelector(".item:nth-child(2) > .right span"));

        type(By.name("copyCode"), "5");
        click(By.cssSelector(".btn:nth-child(5)"));
        click(By.cssSelector(".btn:nth-child(6)"));

        var loan = loanRepository.findByUserIdAndCopyIdAndClosed(2L, 5L, false);
        assertThat(
                loan.isPresent(),
                is(false)
        );
        var penalties = penaltyRepository.findByUserIdAndActiveIsTrue(2L);
        penalties.forEach(Penalty::deactivate);
        penaltyRepository.saveAllAndFlush(penalties);
    }
}