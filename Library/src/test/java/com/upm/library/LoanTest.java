package com.upm.library;

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
public class LoanTest {

  private WebDriver driver;
  private WebDriverWait wait;

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
  void loanTest() {
    driver.get("http://localhost:8080");

    // 1) Login Cognito (igual que tu test que funciona)
    loginCognito("prueba@prueba.com", "1234567aA!");

    // 2) Espera a estar ya en tu app (mejor ancla: algo que exista sí o sí)
    // Si en esta pantalla existe userQ, perfecto:
    wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("body")));

    // Si este click es necesario en tu flujo, mantenlo.
    // (Es frágil, pero lo dejamos de momento porque es lo que grabaste)
    click(By.cssSelector(".three > .card:nth-child(1) > .p"));

    // 3) Buscar usuario / item (según lo grabado)
    type(By.name("userQ"), "a");
    click(By.cssSelector(".primary"));

    // 4) Seleccionar item (esto es frágil: nth-child)
    // Ideal: cambiar por data-testid o link/texto estable
    click(By.cssSelector(".item:nth-child(2) > .right span"));

    // 5) Introducir código de ejemplar y prestar
    type(By.name("copyCode"), "3");
    click(By.cssSelector(".btn:nth-child(5)"));
    click(By.cssSelector(".btn:nth-child(6)"));

    // 6) Volver a home y salir/volver (según grabación)
    click(By.linkText("BIBLIOTECA UNIVERSIDAD POLITÉCNICA DE MADRID"));
    click(By.cssSelector(".two > .card:nth-child(2) > .p"));
    click(By.cssSelector(".ghost"));
  }

  private void loginCognito(String username, String password) {
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

    // submit “real”
    pwd.submit();

    // espera redirect fuera de Cognito y dentro de localhost
    wait.until(ExpectedConditions.not(ExpectedConditions.urlContains("amazoncognito.com")));
    wait.until(ExpectedConditions.urlContains("localhost:8080"));

    // opcional: espera a salir del callback
    wait.until(ExpectedConditions.not(ExpectedConditions.urlContains("/login/oauth2/code/")));
  }

  private void click(By locator) {
    wait.until(ExpectedConditions.elementToBeClickable(locator)).click();
  }

  private void type(By locator, String text) {
    WebElement el = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    el.clear();
    el.sendKeys(text);
  }
}