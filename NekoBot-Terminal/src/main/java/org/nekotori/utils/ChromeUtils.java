package org.nekotori.utils;

import cn.hutool.core.io.FileUtil;
import cn.hutool.http.HttpUtil;
import com.github.binarywang.java.emoji.EmojiConverter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.util.StopWatch;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j

public class ChromeUtils {

    private static final String simplifyChatLanguageButton = "/html/body/div/div/header/a[2]";
    private static final String simplifyChatInput = "/html/body/div/div/main/div[1]/textarea";
    private static final String simplifyChatButton = "/html/body/div/div/main/div[1]/button";
    private static final String simplifyChatOutput = "/html/body/div/div/main/div[3]/div/div/div[2]/div/p";
    private static final String nikkeCDKEntryButton = "/html/body/div[1]/div[1]/div[1]/div[2]/div/div/div[4]/div[1]/div[2]/div[4]/div[2]/div/div[2]/a[1]";
    private static final String nikkeCdkChartXpath = "//div[@class='wiki-detail-body']//table[@class='mould-table selectItemTable col-group-table']";

    private static final String nikkeCharaListId = "menu-64581";
    private static final String nikkeListXpath = "//div[@class='item-wrapper icon-size-7 pc-item-group']/*";

    private static final String nikkeCharaChartsXpath = "//table[@class='mould-table selectItemTable col-group-table']";

    public static void main(String[] args) {
        ChromeDriver chromeDriver = initChrome();
        chromeDriver.get("https://nikke.gamekee.com");
        List<WebElement> elementsByXPath = chromeDriver.findElementsByXPath(nikkeListXpath);
        elementsByXPath.forEach(ele->{
            String src = ((RemoteWebElement) ele).findElementByXPath("./img").getAttribute("data-src");
            byte[] bytes = HttpUtil.downloadBytes("https:"+src);
            FileUtil.writeFromStream(new ByteArrayInputStream(bytes),new File("nikke/thumbnail/"+ele.getText()+".png"));
        });
    }

    public static void printElement(WebElement element){
        byte[] screenshotAs = element.getScreenshotAs(OutputType.BYTES);
        saveTemp(new ByteArrayInputStream(screenshotAs));
    }

    public static void waitUntilVisible(WebDriver webDriver, By locator) {
        WebDriverWait driverWait = new WebDriverWait(webDriver, 10);
        WebElement el = webDriver.findElement(locator);
        driverWait.until(driver-> ((JavascriptExecutor)driver).executeScript("return document.images[0].complete", el));
    }

    public static InputStream getNikkeCDK(){
        ChromeDriver chromeDriver = initChrome();
        chromeDriver.get("https://nikke.gamekee.com");
        WebElement eB = chromeDriver.findElementByXPath(nikkeCDKEntryButton);
        eB.click();
        WebElement elementByXPath = chromeDriver.findElementByXPath(nikkeCdkChartXpath);
        byte[] screenshotAs = elementByXPath.getScreenshotAs(OutputType.BYTES);
        return new ByteArrayInputStream(screenshotAs);
    }

    @NotNull
    private static ChromeDriver initChrome() {
        System.getProperties().setProperty("webdriver.chrome.driver", "chromedriver");
        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.addArguments("--headless");
        chromeOptions.addArguments("--no-sandbox");
        chromeOptions.addArguments("--disable-gpu");
        chromeOptions.addArguments("--window-size=1920,1080");
        chromeOptions.addArguments("disable-infobars"); // disabling infobars
        chromeOptions.addArguments("--disable-extensions"); // disabling extensions
        chromeOptions.addArguments("--disable-dev-shm-usage"); // overcome limited resource problems
        chromeOptions.setPageLoadStrategy(PageLoadStrategy.EAGER);
        ChromeDriver chromeDriver = new ChromeDriver(chromeOptions);
        chromeDriver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        return chromeDriver;
    }


    private static void tWait(long mil){
        try{
            Thread.sleep(mil);
        }catch (InterruptedException ignore){};
    }

    public static String summaryChat(String content) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        content = EmojiConverter.getInstance().toAlias(content);
        System.out.println("input:" + content.substring(0, 200) + "------total letters:" + content.length());
        ChromeDriver chromeDriver = initChrome();
        chromeDriver.get("https://chat-simplifier.imzbb.cc/");
        WebElement language = chromeDriver.findElementByXPath(simplifyChatLanguageButton);
        language.click();
        chromeDriver.findElementByXPath(simplifyChatInput).sendKeys(content);
        chromeDriver.findElementByXPath(simplifyChatButton).click();
        boolean hasOutput = false;
        String text = "";
        while (!hasOutput) {
            if(stopWatch.getTotalTimeMillis()>5*60*1000L){
                chromeDriver.quit();
                stopWatch.stop();
                return "课代表也开小差了";
            }
            try {
                Thread.sleep(2000L);
                String tempText = chromeDriver.findElementByXPath(simplifyChatOutput).getText();
                System.out.println("now reply letters:"+ tempText.length());
                if(tempText.length()==text.length()&&text.length()!=0) {
                    hasOutput = true;
                }
                text = tempText;
            }catch (Exception ignore){}
        }
        chromeDriver.quit();
        return text;
    }


    public static boolean isUrl(String content){
        return content.matches("^((http|https)://)?(([A-Za-z0-9]+-[A-Za-z0-9]+|[A-Za-z0-9]+)\\.)+([A-Za-z]+)[/?:]?.*$");
    }


    public static InputStream getScreenShotStream(String url){
        ChromeDriver chromeDriver = initChrome();
        chromeDriver.get(url);
        try {
            Thread.sleep(3000L);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        byte[] screenshotAs = chromeDriver.getScreenshotAs(OutputType.BYTES);
        chromeDriver.quit();
        return new ByteArrayInputStream(screenshotAs);
    }

    public static void  saveTemp(InputStream inputStream){
        FileUtil.writeFromStream(inputStream,new File("temp.png"));
    }
}