package com.upm.library;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.openqa.selenium.*;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("aws")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class ReservationTest {
    private WebDriver driver;
    private Map<String, Object> vars;
    JavascriptExecutor js;

    @BeforeEach
    public void setUp() {
        driver = new FirefoxDriver();
        js = (JavascriptExecutor) driver;
        vars = new HashMap<String, Object>();
    }


    @AfterEach
    public void tearDown() {
        driver.quit();
    }

    @Test
    public void test1() {
        driver.get("http://localhost:8080");
        driver.manage().window().setSize(new Dimension(1936, 1056));
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));

        WebElement email = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("input[name='username'], input[type='email']")));
        email.clear();
        email.sendKeys("prueba@prueba.com");

        // botón "next/sign in" (puede ser button o input)
        WebElement submitEmail = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.cssSelector("button[type='submit'], input[type='submit']")
        ));
        wait.until(ExpectedConditions.elementToBeClickable(submitEmail)).click();

        WebDriverWait wait2 = new WebDriverWait(driver, Duration.ofSeconds(30));

        WebElement password = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("input[name='password'], input[type='password']")
        ));
        password.clear();
        password.sendKeys("1234567aA!");

        // Enviar el formulario "de verdad"
        password.submit();
        wait2.until(ExpectedConditions.not(ExpectedConditions.urlContains("amazoncognito.com")));
        wait2.until(ExpectedConditions.urlContains("localhost:8080"));

        // DEBUG
        System.out.println("URL tras submit password: " + driver.getCurrentUrl());
        System.out.println("TITLE tras submit password: " + driver.getTitle());
        // Espera a que cambie a tu app
        WebDriverWait wait1 = new WebDriverWait(driver, Duration.ofSeconds(30));
        try {
            wait1.until(ExpectedConditions.not(ExpectedConditions.urlContains("amazoncognito.com")));
            wait1.until(ExpectedConditions.urlContains("localhost:8080"));
        } catch (TimeoutException e) {
            // Si no sales de Cognito, imprime info útil y falla el test aquí mismo
            System.out.println("NO SALIÓ DE COGNITO. URL: " + driver.getCurrentUrl());
            System.out.println("TITLE: " + driver.getTitle());

            // Intenta sacar mensaje de error típico de Cognito
            var alerts = driver.findElements(By.cssSelector("[role='alert'], .text-error, .error, .alert, .banner"));
            for (var a : alerts) {
                String t = a.getText().trim();
                if (!t.isEmpty()) System.out.println("ALERT: " + t);
            }

            // Detecta MFA / código
            boolean hayCode = !driver.findElements(By.cssSelector("input[autocomplete='one-time-code'], input[name*='code'], input[id*='code']")).isEmpty();
            System.out.println("¿Pide código/MFA?: " + hayCode);

            throw e;
        }

        // espera a que estés en tu app (URL localhost)
        driver.findElement(By.cssSelector(".two > .card:nth-child(1) > .p")).click();
        driver.findElement(By.name("q")).click();
        driver.findElement(By.name("q")).sendKeys("a");
        driver.findElement(By.cssSelector(".primary")).click();
        driver.findElement(By.linkText("Ver ejemplares →")).click();
        driver.findElement(By.linkText("Reservar ejemplar")).click();
        driver.findElement(By.cssSelector(".primary")).click();
        driver.findElement(By.cssSelector(".danger")).click();
        assertThat(driver.switchTo().alert().getText(), is("¿Cancelar esta reserva?"));
        driver.switchTo().alert().accept();
        driver.findElement(By.cssSelector(".ghost")).click();
    }
}

