package com.upm.library;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.time.Duration;

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
public class ReturnTest {

  private WebDriver driver;
  private WebDriverWait wait;

  private static final String BASE_URL = "http://localhost:8080";
  private static final String USER = "prueba@prueba.com";
  private static final String PASS = "1234567aA!";

  @BeforeEach
  void setUp() {
    driver = new FirefoxDriver();
    driver.manage().window().setSize(new Dimension(1936, 1056));
    wait = new WebDriverWait(driver, Duration.ofSeconds(30));
  }

  @AfterEach
  void tearDown() {
    if (driver != null) driver.quit();
  }

  @Test
  void devolverYPrestar() {
    // --- 1) Ir a la app + login (robusto como tu test anterior) ---
    driver.get(BASE_URL);
    loginCognito(USER, PASS);

    // --- 2) DEVOLUCIÓN (según lo grabado) ---
    // Card "devolución" (grabaste: .three > .card:nth-child(2))
    click(By.cssSelector(".three > .card:nth-child(2)"));

    type(By.name("copyId"), "2");
    click(By.cssSelector(".primary"));

    // botón acción (grabaste: .btn:nth-child(5))
    click(By.cssSelector(".btn:nth-child(5)"));

    Alert alert = wait.until(ExpectedConditions.alertIsPresent());
    assertThat(alert.getText(), is("¿Confirmar devolución del ejemplar?"));
    alert.accept();

    click(By.linkText("← Volver"));

    // Volver a home / perfil
    click(By.cssSelector(".two > .card:nth-child(2) > .p"));
    click(By.cssSelector(".ghost"));

    // --- 3) PRÉSTAMO (lo que grabaste después) ---
    // En tu grabación volviste a abrir / cerrar navegador. Aquí no hace falta:
    // simplemente volvemos al home y seguimos.
    driver.get(BASE_URL);
    loginCognito(USER, PASS);

    click(By.cssSelector(".three > .card:nth-child(1) > .p"));
    type(By.name("userQ"), "a");
    click(By.cssSelector(".primary"));

    click(By.cssSelector(".item:nth-child(2) > .right span"));

    type(By.name("copyCode"), "1");
    click(By.cssSelector(".btn:nth-child(5)"));
    click(By.cssSelector(".btn:nth-child(6)"));

    click(By.linkText("BIBLIOTECA UNIVERSIDAD POLITÉCNICA DE MADRID"));
    click(By.cssSelector(".two > .card:nth-child(2) > .p"));
    click(By.cssSelector(".ghost"));
  }

  // ==== Login robusto (copiado del patrón que te funciona) ====
  private void loginCognito(String username, String password) {
    // Si ya estás logueado (sesión viva), puede que no aparezca Cognito.
    // En ese caso, simplemente no hacemos nada.
    if (driver.getCurrentUrl().contains("amazoncognito.com")) {
      // estamos en Cognito, seguimos
    } else {
      // si el home ya cargó y no hay inputs de Cognito, salimos
      if (driver.findElements(By.cssSelector("input[name='username'], input[type='email']")).isEmpty()
              && driver.findElements(By.cssSelector("input[name='password'], input[type='password']")).isEmpty()) {
        return;
      }
    }

    WebElement email = wait.until(ExpectedConditions.elementToBeClickable(
            By.cssSelector("input[name='username'], input[type='email']")
    ));
    email.clear();
    email.sendKeys(username);

    WebElement submitEmail = wait.until(ExpectedConditions.elementToBeClickable(
            By.cssSelector("button[type='submit'], input[type='submit']")
    ));
    submitEmail.click();

    WebElement pwd = wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.cssSelector("input[name='password'], input[type='password']")
    ));
    pwd.clear();
    pwd.sendKeys(password);

    // Submit "real" (Cognito suele ir mejor así)
    pwd.submit();

    // Espera a salir de Cognito y entrar en tu app
    wait.until(ExpectedConditions.not(ExpectedConditions.urlContains("amazoncognito.com")));
    wait.until(ExpectedConditions.urlContains("localhost:8080"));
    wait.until(ExpectedConditions.not(ExpectedConditions.urlContains("/login/oauth2/code/")));
  }

  // ==== Helpers ====
  private void click(By locator) {
    wait.until(ExpectedConditions.elementToBeClickable(locator)).click();
  }

  private void type(By locator, String text) {
    WebElement el = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    el.clear();
    el.sendKeys(text);
  }
}