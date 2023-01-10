package org.nekotori.utils;

import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * Created by fengshuo
 */


@Slf4j

public class ChromeUtils {



    public static void main(String[] args) throws Exception {
        System.out.println(isUrl("wwwcom"));
    }

    public static boolean isUrl(String content){
        return content.matches("^((http|https)://)?(([A-Za-z0-9]+-[A-Za-z0-9]+|[A-Za-z0-9]+)\\.)+([A-Za-z]+)[/?:]?.*$");
    }


    public static InputStream getScreenShotStream(String url){
        System.getProperties().setProperty("webdriver.chrome.driver", "chromedriver");
        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.addArguments("--headless");
        chromeOptions.addArguments("--no-sandbox");
        chromeOptions.addArguments("--disable-gpu");
        chromeOptions.addArguments("--window-size=1920,1080");
        chromeOptions.setPageLoadStrategy(PageLoadStrategy.EAGER);
        ChromeDriver chromeDriver = new ChromeDriver(chromeOptions);
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
}