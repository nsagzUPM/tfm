package com.upm.library;

import java.time.Duration;

import com.upm.library.repository.LoanRepository;
import com.upm.library.repository.PenaltyRepository;
import com.upm.library.repository.ReservationRepository;
import com.upm.library.repository.UserRepository;
import com.upm.library.service.LoanService;
import com.upm.library.service.ReservationService;
import com.upm.library.service.SystemConfigService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import org.openqa.selenium.*;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class BaseE2ETest {

    protected static final String BASE_URL = "http://localhost:8080";
    protected static final Duration TIMEOUT = Duration.ofSeconds(30);

    // Mejor por variables de entorno, pero lo dejo como lo tienes
    protected static final String USER = "prueba@prueba.com";
    protected static final String PASS = "1234567aA!";
    protected static final String OTHER_USER = "local-user-001";

    protected WebDriver driver;
    protected WebDriverWait wait;

    @Autowired
    protected PenaltyRepository penaltyRepository;

    @Autowired
    protected SystemConfigService systemConfigService;

    @Autowired
    protected ReservationRepository reservationRepository;

    @Autowired
    protected ReservationService reservationService;

    @Autowired
    protected LoanRepository loanRepository;

    @Autowired
    protected UserRepository userRepository;

    @Autowired
    protected LoanService loanService;

    @BeforeEach
    void baseSetUp() {
        driver = new FirefoxDriver();
        driver.manage().window().setSize(new Dimension(1936, 1056));
        wait = new WebDriverWait(driver, TIMEOUT);
    }

    @AfterEach
    void baseTearDown() {
        if (driver != null) driver.quit();
    }

    protected void openHome() {
        driver.get(BASE_URL);
    }

    protected void loginCognito() {
        // Si ya estás en la app y no hay inputs de Cognito, no hagas nada
        boolean hayUsername = !driver.findElements(By.cssSelector("input[name='username'], input[type='email']")).isEmpty();
        boolean hayPassword = !driver.findElements(By.cssSelector("input[name='password'], input[type='password']")).isEmpty();
        if (!driver.getCurrentUrl().contains("amazoncognito.com") && !hayUsername && !hayPassword) {
            return;
        }

        WebElement email = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("input[name='username'], input[type='email']")
        ));
        email.clear();
        email.sendKeys(USER);

        wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("button[type='submit'], input[type='submit']")
        )).click();

        WebElement pwd = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("input[name='password'], input[type='password']")
        ));
        pwd.clear();
        pwd.sendKeys(PASS);

        // Cognito suele ir mejor con submit()
        pwd.submit();

        // vuelve a tu app y sale del callback
        wait.until(ExpectedConditions.not(ExpectedConditions.urlContains("amazoncognito.com")));
        wait.until(ExpectedConditions.urlContains("localhost:8080"));
        wait.until(ExpectedConditions.not(ExpectedConditions.urlContains("/login/oauth2/code/")));
    }

    protected void click(By locator) {
        wait.until(ExpectedConditions.elementToBeClickable(locator)).click();
    }

    protected void type(By locator, String text) {
        WebElement el = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
        el.clear();
        el.sendKeys(text);
    }

    protected Alert waitAlert() {
        return wait.until(ExpectedConditions.alertIsPresent());
    }
}