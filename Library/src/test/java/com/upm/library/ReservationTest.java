package com.upm.library;

import static org.awaitility.Awaitility.await;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import com.upm.library.domain.Penalty;
import com.upm.library.domain.ReservationStatus;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

@ActiveProfiles("aws")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class ReservationTest extends BaseE2ETest {


    @Test
    void reservarSinSancion() {
        openHome();
        loginCognito();
        assertThat(
                penaltyRepository.findByUserIdAndActiveIsTrue(2L).isEmpty(),
                is(true)
        );
        assertThat(
                reservationRepository.findByUserIdAndCopyIdAndStatus(2L, 1L, ReservationStatus.ACTIVE).isEmpty(),
                is(true)
        );

        click(By.cssSelector(".two > .card:nth-child(1) > .p"));
        click(By.name("q"));
        type(By.name("q"), "a");
        click(By.cssSelector(".primary"));
        click(By.linkText("Ver ejemplares →"));
        click(By.linkText("Reservar ejemplar"));
        click(By.cssSelector(".primary"));

        await()
                .atMost(10, TimeUnit.SECONDS)
                .pollInterval(200, TimeUnit.MILLISECONDS)
                .untilAsserted(() -> {
                    var reservations = reservationRepository.findByUserIdAndCopyIdAndStatus(2L, 1L, ReservationStatus.ACTIVE);
                    assertThat("Debe existir una reserva activa", reservations.isEmpty(), is(false));
                    var reservation = reservations.get();

                    var reservationDays = systemConfigService.getReservationDays();
                    LocalDate expectedDeadLine = LocalDate.now().plusDays(reservationDays);
                    assertThat(
                            reservation.getDeadline(),
                            is(expectedDeadLine)
                    );
                    reservationService.cancelReservation(USER, reservation.getId());
                });

        assertThat(
                reservationRepository.findByUserIdAndCopyIdAndStatus(2L, 1L, ReservationStatus.ACTIVE).isEmpty(),
                is(true)
        );
    }

    @Test
    void reservarConSancion() {
        openHome();
        loginCognito();

        var user = userRepository.findByExternalId(USER).get();
        Penalty  penalty = new Penalty(user, LocalDate.now(), LocalDate.now(), "prueba con sanción");
        penaltyRepository.save(penalty);
        assertThat(
                penaltyRepository.findByUserIdAndActiveIsTrue(2L).isEmpty(),
                is(false)
        );
        assertThat(
                reservationRepository.findByUserIdAndCopyIdAndStatus(2L, 1L, ReservationStatus.ACTIVE).isEmpty(),
                is(true)
        );

        click(By.cssSelector(".two > .card:nth-child(1) > .p"));
        click(By.name("q"));
        type(By.name("q"), "a");
        click(By.cssSelector(".primary"));
        click(By.linkText("Ver ejemplares →"));
        click(By.linkText("Reservar ejemplar"));
        click(By.cssSelector(".primary"));

        await()
                .atMost(10, TimeUnit.SECONDS)
                .pollInterval(200, TimeUnit.MILLISECONDS)
                .untilAsserted(() -> {
                    var reservations = reservationRepository.findByUserIdAndCopyIdAndStatus(2L, 1L, ReservationStatus.ACTIVE);
                    assertThat("No debe existir una reserva activa", reservations.isEmpty(), is(true));
                });
        var penalties = penaltyRepository.findByUserIdAndActiveIsTrue(2L);
        penalties.forEach(Penalty::deactivate);
        penaltyRepository.saveAllAndFlush(penalties);
    }
}