package com.minicollege;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.URL;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MiniCollegeSeleniumTest {

    private WebDriver driver;

    // MiniCollege running on EC2
    private final String BASE_URL = "http://172.31.38.150:8081";

    // Selenium Docker/Grid
    private final String SELENIUM_URL = "http://127.0.0.1:4444";

    @BeforeEach
    void setUp() throws Exception {

        ChromeOptions options = new ChromeOptions();

        options.addArguments("--headless=new");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1920,1080");

        driver = new RemoteWebDriver(
                new URL(SELENIUM_URL),
                options
        );

        // Prevent driver.get() from hanging indefinitely
        driver.manage()
                .timeouts()
                .pageLoadTimeout(Duration.ofSeconds(15));
    }

    @AfterEach
    void tearDown() {

        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    void testHomePage() {

        driver.get(BASE_URL + "/index.html");

        assertTrue(
                driver.getTitle().contains("MiniCollege"),
                "Home page title should contain MiniCollege"
        );
    }

    @Test
    void testLoginPage() {

        driver.get(BASE_URL + "/login.html");

        assertTrue(
                driver.findElement(By.id("username")).isDisplayed()
        );

        assertTrue(
                driver.findElement(By.id("password")).isDisplayed()
        );

        assertTrue(
                driver.findElement(By.id("loginButton")).isDisplayed()
        );
    }

    @Test
    void testValidLogin() {

        driver.get(BASE_URL + "/login.html");

        driver.findElement(By.id("username"))
                .sendKeys("student");

        driver.findElement(By.id("password"))
                .sendKeys("student123");

        driver.findElement(By.id("loginButton"))
                .click();

        assertTrue(
                driver.getCurrentUrl().contains("dashboard.html")
        );

        assertEquals(
                "Rakesh",
                driver.findElement(By.id("studentName")).getText()
        );
    }

    @Test
    void testInvalidLogin() {

        driver.get(BASE_URL + "/login.html");

        driver.findElement(By.id("username"))
                .sendKeys("student");

        driver.findElement(By.id("password"))
                .sendKeys("wrongpassword");

        driver.findElement(By.id("loginButton"))
                .click();

        String errorMessage =
                driver.findElement(By.id("errorMessage")).getText();

        assertEquals(
                "Invalid username or password!",
                errorMessage
        );
    }

    @Test
    void testDashboard() {

        driver.get(BASE_URL + "/dashboard.html");

        assertEquals(
                "Rakesh",
                driver.findElement(By.id("studentName")).getText()
        );

        assertEquals(
                "Computer Science Engineering",
                driver.findElement(By.id("course")).getText()
        );

        assertEquals(
                "3rd Year",
                driver.findElement(By.id("year")).getText()
        );

        assertEquals(
                "MiniCollege",
                driver.findElement(By.id("college")).getText()
        );
    }

    @Test
    void testLogout() {

        driver.get(BASE_URL + "/dashboard.html");

        driver.findElement(By.id("logoutLink"))
                .click();

        assertTrue(
                driver.getCurrentUrl().contains("login.html")
        );
    }

    @Test
    void testAboutPage() {

        driver.get(BASE_URL + "/about.html");

        assertTrue(
                driver.getTitle().contains("MiniCollege")
        );
    }
}