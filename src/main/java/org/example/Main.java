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
    public static WebElement element;

    public static List<String> idList = new ArrayList<>();

    public static int count = 0;

    public static void main(String[] args) throws InterruptedException, IOException {


        // TODO Auto-generated method stub
        System.setProperty(WEB_DRIVER_ID, WEB_DRIVER_PATH);


        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-popup-blocking");
        options.addArguments("--start-maximized");
        options.addArguments("--window-size=1920,1080");
        ChromeDriver driver = new ChromeDriver(options);

        try {
            start(driver);

        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        finally{
        }
    }



    static void start(ChromeDriver driver) throws InterruptedException
    {



        driver.get("https://nid.naver.com/nidlogin.login?url=http%3A%2F%2Fwww.naver.com");
        Thread.sleep(10000);

        driver.get("https://blog.naver.com");
        String url = "https://blog.naver.com";
        driver.get(url);

        WebElement sectionBlogQuery = driver.findElement(By.name("sectionBlogQuery"));
        sectionBlogQuery.clear();
        sectionBlogQuery.sendKeys("광주 it" + Keys.ENTER);

        Thread.sleep(delay);



        int totalPage = 2;

        if(totalPage / 10 > 0){
            for(int i = 1; i < totalPage / 10 + 1; i ++){
                element = driver.findElement(By.className("button_next"));
                element.click();
                Thread.sleep(delay);
            }
        }


        for (; count<100 ; totalPage++) {
            List<WebElement> elements = driver.findElements(By.className("title_post"));
            for (WebElement webElement : elements) {
                String parentWindowHandle = driver.getWindowHandle();
                try {
                    if(count > 100)
                        break;

                    webElement.click();
                    Set<String> allWindowHandles = driver.getWindowHandles();
                    for (String handle : allWindowHandles) {
                        if (!handle.equals(parentWindowHandle)) {
                            driver.switchTo().window(handle);
                            break;
                        }
                    }

                    driver.switchTo().frame("mainFrame");
                    Thread.sleep(delay);

                    element = driver.findElement(By.className("blog_domain"));
                    idList.add(element.getText());
                    element = driver.findElement(By.id("nickNameArea"));
                    String name = element.getText();

                    element = driver.findElement(By.className("u_likeit_list_btn"));
                    element.sendKeys(Keys.ENTER);
                    Thread.sleep(delay);
                    boolean isAlertPresent = isAlertPresent(driver);

                    if (isAlertPresent) {
                        Alert alert = driver.switchTo().alert();
                        alert.accept();
                    }
                    element = null;
                    element = driver.findElement(By.className("btn_comment"));
                    if(element != null) {
                        element.sendKeys(Keys.ENTER);


                        Thread.sleep(delay);
//                    element = driver.findElement(By.cssSelector(".u_cbox_text.u_cbox_text_mention"));
//                    element.sendKeys("안녕하세요 " + name + "님. 포스트 잘 읽었습니다~~\n 오늘도 좋은하루 보내세요!!");
//                    element = driver.findElement(By.className("u_cbox_btn_upload"));
//                    element.click();
                    }
                    element = null;
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
                        if (element.getText().contains("받지 않는")){
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
                                continue;
                            }

                            element = driver.findElement(By.id("message"));

                            element.sendKeys(name + "님 안녕하세요 :) 디지털&IT 취업에 관한 다양한 정보와 소식을 공유하는 블로그입니다. 취업 즐겨찾기와 함께 디지털&IT분야 취업에 대해 더 많이 공유하면 좋겠습니다. ");
                            element = driver.findElement(By.className("button_next"));
                            element.click();

                            Thread.sleep(delay);
                            driver.close();
                            driver.switchTo().window(currentWindow);
                        }
                    }
                    driver.close();
                    driver.switchTo().window(parentWindowHandle);
                    count++;

                }
                catch (Exception e){
                    driver.close();
                    driver.switchTo().window(parentWindowHandle);
                }
            }
            if(totalPage % 10 == 0){
                element = driver.findElement(By.className("button_next"));
                element.click();
                totalPage++;
                Thread.sleep(delay);
                continue;
            }
            JavascriptExecutor jsExecutor = (JavascriptExecutor) driver;
            element = (WebElement) jsExecutor.executeScript("return document.querySelector('a[ng-if=\"currentPage!=page\"][aria-label=\""+totalPage+"페이지\"]')");
            element.click();
            Thread.sleep(delay);
        }




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

    static void getList(ChromeDriver driver) throws InterruptedException
    {
        WebElement element;

        //목록찾기
        List<WebElement> elements = driver.findElements(By.cssSelector("a.button_buddy.button_buddy_add"));
        Thread.sleep(delay);

        for(WebElement el : elements)
        {
            el.click();
            Thread.sleep(delay);

            clickAddNeighbor(driver);
            Thread.sleep(delay);
        }

    }



    static void clickAddNeighbor(ChromeDriver driver) throws InterruptedException
    {
        String MainWindow = driver.getWindowHandle();
        Set<String> s1= driver.getWindowHandles();
        Iterator<String> i1= s1.iterator();

        while(i1.hasNext()){
            String ChildWindow = i1.next();

            if(!MainWindow.equalsIgnoreCase(ChildWindow)){
                driver.switchTo().window(ChildWindow);
                String name=null;
                try {
                    element = driver.findElement(By.cssSelector("#content > div > form > fieldset > div.popup_text > div.buddy_state > p.text_buddy_add > strong"));
                    name=element.getAttribute("innerHTML");

                    element = driver.findElement(By.cssSelector("#content > div > form > fieldset > div.popup_text > div.buddy_state > p > span.wrap_radio.radio_bothbuddy > label"));
                    element.click();
                    Thread.sleep(delay);

                    element = driver.findElement(By.cssSelector("#content > div > form > fieldset > div.area_button > a.button_next._buddyAddNext"));
                    element.click();
                    Thread.sleep(delay);

                    element = driver.findElement(By.cssSelector("#message"));
                    if(name!=null)
                    {
                        element.sendKeys(name+"님! 글 재밌게봤습니다^^ 서로이웃 추가 해요 ㅎㅎ");
                    }
                    else
                    {
                        element.sendKeys("글 재밌게봤습니다^^ 서로이웃 추가 해요 ㅎㅎ");
                    }
                    Thread.sleep(delay);


                    element = driver.findElement(By.cssSelector("body > form > div > div > fieldset > div.area_button > a.button_next._addBothBuddy"));
                    element.click();
                    Thread.sleep(delay);

                    System.out.println("["+name+"] 서로이웃추가 신청완료");
                    driver.close();
                }
                catch(UnhandledAlertException e)
                {
                    System.out.println("["+name+"] 이미 서로이웃추가신청된 계정.");
                }
                catch(Exception e)
                {
                    System.out.println("["+name+"] 서로이웃추가 비활성 계정");
                    driver.close();
                }
                finally
                {

                }



            }
        }


        driver.switchTo().window(MainWindow);


    }

}