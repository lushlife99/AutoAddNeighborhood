package org.example;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class Main {

    public static String WEB_DRIVER_ID = "webdriver.chrome.driver";
    public static String WEB_DRIVER_PATH = "chromedriver.exe";
    public static int delay = 1000;
    public static WebElement element = null;

    public static String searchKey = "일본 IT 취업";

    public static String comment = "님. 포스트 잘 읽었습니다~~\n 오늘도 좋은하루 보내세요!!";

    public static String neighborAddMessage = "님 안녕하세요 :) 디지털&IT 취업에 관한 다양한 정보와 소식을 공유하는 블로그입니다. 취업 즐겨찾기와 함께 디지털&IT분야 취업에 대해 더 많이 공유하면 좋겠습니다.";
    public static List<String> idList = new ArrayList<>();
    public static int currentPage = 1;

    public static int count = 0;

    public static void main(String[] args) throws InterruptedException {

        // TODO Auto-generated method stub
        ChromeDriver driver = driverConfig();
        driver = login(driver);
        try {
            start(driver);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static ChromeDriver driverConfig() {
        System.setProperty(WEB_DRIVER_ID, WEB_DRIVER_PATH);
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-popup-blocking");
        options.addArguments("--start-maximized");
        options.addArguments("--remote-allow-origins=*");
        options.addArguments("--window-size=1920,1080");
        ChromeDriver driver = new ChromeDriver(options);
        return driver;
    }

    static void start(ChromeDriver driver) throws InterruptedException
    {
        searchKeyword(driver);
        Thread.sleep(delay);
        prefixPaging(driver, currentPage);
        mainLogic(driver);

    }

    private static void mainLogic(ChromeDriver driver) throws InterruptedException {
        for (; count<100 ; currentPage++) {
            List<WebElement> elements = driver.findElements(By.className("title_post"));
            for (WebElement webElement : elements) {
                if(count > 100)
                    break;

                String parentWindowHandle = driver.getWindowHandle();
                try {
                    webElement.click();
                    switchChildHandle(driver, parentWindowHandle);
                    driver.switchTo().frame("mainFrame");
                    String name = getName(driver);
                    if (addBuddy(driver, parentWindowHandle, name)) {
                        driver.close();
                        driver.switchTo().window(parentWindowHandle);
                        count++;
                    } else{
                        pushLikeBtn(driver);
                        pushComment(driver, name);
                    }
                }
                catch (Exception e){
                    driver.close();
                    driver.switchTo().window(parentWindowHandle);
                }
            }
            currentPage = getNextPAge(driver, currentPage);
        }
    }

    /**
     * 로그인은 자동화가 안됨. 네이버에서 봇감지를 하기 때문
     * 때문에 로그인은 수동으로 해주어야 하며 10초의 딜레이를 둠.
     *
     * @param driver
     * @throws InterruptedException
     */
    private static ChromeDriver login(ChromeDriver driver) throws InterruptedException {
        driver.get("https://nid.naver.com/nidlogin.login?url=http%3A%2F%2Fwww.naver.com");
        Thread.sleep(delay*10);

        String url = "https://blog.naver.com";
        driver.get(url);
        Thread.sleep(delay);
        return driver;
    }

    private static void switchChildHandle(ChromeDriver driver, String parentWindowHandle) {
        Set<String> allWindowHandles = driver.getWindowHandles();
        for (String handle : allWindowHandles) {
            if (!handle.equals(parentWindowHandle)) {
                driver.switchTo().window(handle);
                break;
            }
        }
    }

    /**
     * 만약 중간에 멈췄을 경우.. 마지막에 보낸 페이지를 이동.
     * @param driver
     * @param currentPage
     * @throws InterruptedException
     */
    private static void prefixPaging(ChromeDriver driver, int currentPage) throws InterruptedException {
        if(currentPage / 10 > 0){
            for(int i = 1; i < currentPage / 10 + 1; i ++){
                element = driver.findElement(By.className("button_next"));
                element.click();
                Thread.sleep(delay);
            }
        }
    }

    private static int getNextPAge(ChromeDriver driver, int currentPage) throws InterruptedException {
        if(currentPage % 10 == 0){
            element = driver.findElement(By.className("button_next"));
            element.click();
            Thread.sleep(delay);
            currentPage++;
        }
        else {
            JavascriptExecutor jsExecutor = driver;
            element = (WebElement) jsExecutor.executeScript("return document.querySelector('a[ng-if=\"currentPage!=page\"][aria-label=\"" + currentPage + "페이지\"]')");
            element.click();
            Thread.sleep(delay);
        }
        return currentPage;
    }

    private static boolean addBuddy(ChromeDriver driver, String parentWindowHandle, String name) throws InterruptedException {
        boolean isAlertPresent;
        element = driver.findElement(By.className("btn_addbuddy"));
        if(element != null) {
            element.click();

            Thread.sleep(delay);
            String currentWindow = driver.getWindowHandle();
            Set<String> windowHandles = driver.getWindowHandles();
            for (String windowHandle : windowHandles) {
                if (!windowHandle.equals(parentWindowHandle) && !windowHandle.equals(currentWindow)) {
                    driver.switchTo().window(windowHandle);
                    break;
                }
            }
            element = driver.findElement(By.className("notice"));
            if (isAlreadyBuddy()){
                driver.close();
                driver.switchTo().window(currentWindow);

            }
            else {
                element = driver.findElement(By.className("radio_bothbuddy"));
                element.click();
                element = driver.findElement(By.className("button_next"));
                element.click();
                Thread.sleep(delay);
                isAlertPresent = isAlertPresent(driver);
                if (isAlertPresent) {
                    Alert alert = driver.switchTo().alert();
                    alert.accept();
                    driver.switchTo().window(currentWindow);
                    driver.close();
                    driver.switchTo().window(parentWindowHandle);
                    return false;
                }

                element = driver.findElement(By.id("message"));
                element.sendKeys(name + neighborAddMessage);
                element = driver.findElement(By.className("button_next"));
                element.click();

                Thread.sleep(delay);
                driver.close();
                driver.switchTo().window(currentWindow);
            }
        }
        return true;
    }

    private static boolean isAlreadyBuddy() {
        return element.getText().contains("받지 않는");
    }

    private static void pushComment(ChromeDriver driver, String name) throws InterruptedException {

        element = driver.findElement(By.className("btn_comment"));
        if(element != null) {
            element.sendKeys(Keys.ENTER);
            Thread.sleep(delay);
            element = driver.findElement(By.cssSelector(".u_cbox_text.u_cbox_text_mention"));
            element.sendKeys("안녕하세요 " + name + comment);
            element = driver.findElement(By.className("u_cbox_btn_upload"));
            element.click();
        }
        element = null;
    }

    private static void pushLikeBtn(ChromeDriver driver) throws InterruptedException {
        element = driver.findElement(By.className("u_likeit_list_btn"));
        element.sendKeys(Keys.ENTER);
        Thread.sleep(delay);
        boolean isAlertPresent = isAlertPresent(driver);

        if (isAlertPresent) {
            Alert alert = driver.switchTo().alert();
            alert.accept();
        }
        element = null;
    }

    private static String getName(ChromeDriver driver) {
        element = driver.findElement(By.className("blog_domain"));
        idList.add(element.getText());
        element = driver.findElement(By.id("nickNameArea"));
        String name = element.getText();
        return name;
    }

    private static void searchKeyword(ChromeDriver driver) throws InterruptedException {
        System.out.println(driver.getCurrentUrl());
        element = driver.findElement(By.name("sectionBlogQuery"));
        element.clear();
        element.sendKeys(searchKey + Keys.ENTER);
        Thread.sleep(delay);
    }

    public static boolean isAlertPresent(WebDriver driver) {
        try {
            driver.switchTo().alert();
            System.out.println("true");
            return true;
        } catch (org.openqa.selenium.NoAlertPresentException e) {
            System.out.println("false");
            return false;
        }
    }



}