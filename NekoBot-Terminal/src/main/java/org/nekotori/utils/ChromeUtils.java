package org.nekotori.utils;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.http.HttpUtil;
import com.github.binarywang.java.emoji.EmojiConverter;
import lombok.extern.slf4j.Slf4j;
import net.dreamlu.mica.core.result.R;
import org.jetbrains.annotations.NotNull;
import org.nekotori.common.NikkeInfoType;
import org.nekotori.exception.ChromeDriverInUseException;
import org.nekotori.exception.ElementNoFoundException;
import org.nekotori.handler.ThreadSingleton;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.RemoteWebElement;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StopWatch;
import org.springframework.util.StringUtils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;

@Slf4j

public class ChromeUtils {

    public static ChromeDriver driver = null;

    public static volatile AtomicBoolean inUse = new AtomicBoolean(false);
    private static String lv10Skill;

    @NotNull
    private static ChromeDriver initChrome() {
        if (driver == null){
            System.getProperties().setProperty("webdriver.chrome.driver", "chromedriver");
            ChromeOptions chromeOptions = new ChromeOptions();
            chromeOptions.addArguments("--headless");
            Proxy proxy = new Proxy();
            proxy.setAutodetect(false);
            proxy.setHttpProxy("127.0.0.1:19192");
            proxy.setSslProxy("127.0.0.1:19192");
            chromeOptions.addArguments("--no-sandbox");
            chromeOptions.addArguments("--disable-gpu");
            chromeOptions.addArguments("--window-size=1920,2560");
            chromeOptions.addArguments("--start-fullscreen");
            chromeOptions.addArguments("disable-infobars"); // disabling infobars
            chromeOptions.addArguments("--disable-extensions"); // disabling extensions
            chromeOptions.addArguments("--disable-dev-shm-usage"); // overcome limited resource problems
//            chromeOptions.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});
            chromeOptions.setPageLoadStrategy(PageLoadStrategy.EAGER);
            ChromeDriver chromeDriver = new ChromeDriver(chromeOptions);
            chromeOptions.setCapability(CapabilityType.PROXY,proxy);
            chromeDriver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
            driver = chromeDriver;
        }
        return driver;
    }

    private static void destroyChrome(){
        if(driver != null){
            driver.quit();
            driver = null;
        }
    }

    private static <R> R chromeExecute(Function<ChromeDriver, R> supplier){
        if_driver_is_in_use:if(inUse.get()){
            for (int i = 0; i < 10; i++) {
                tWait(1000);
                if(!inUse.get()) {
                    break if_driver_is_in_use;
                }
            }
            System.out.println("driver is in use for 5s!");
            throw new ChromeDriverInUseException();
        }
        inUse.set(true);
        ChromeDriver chromeDriver = initChrome();
        try{
            AtomicBoolean taskFinish = new AtomicBoolean(false);
            ThreadSingleton.run(()->{
                tWait(120*1000);
                if(!taskFinish.get()){
                    System.out.println("chrome task run over 2min! force stop it!");
                    destroyChrome();
                    inUse.set(false);
                }
            });
            R apply = supplier.apply(chromeDriver);
            taskFinish.set(true);
            return apply;
        } finally {
            destroyChrome();
            inUse.set(false);
        }
    }
    private static final String simplifyChatInput = "//textarea";
    private static final String simplifyChatButton = "//div[@class='max-w-xl w-full']/button";
    private static final String simplifyChatOutput = "//div[starts-with(@class,'bg-white')]";
    private static final String nikkeCDKEntryButton = "//a[@title='cdk收集']";
    private static final String nikkeCdkChartXpath = "//div[@class='wiki-detail-body']//table[@class='mould-table selectItemTable col-group-table']";
    private static final String nikkeListXpath = "//div[@class='item-wrapper icon-size-7 pc-item-group']/*";

    private static final String lv10SkillXpath = "//div[text()='LV10']";
    private static final String nikkeCharaChartsXpath = "//table[@class='mould-table selectItemTable col-group-table']";


    public static void main(String[] args) {
        System.out.println(summaryChat(RandomUtil.randomString(300)));
    }

    public static InputStream queryNikkeInfo(String name, NikkeInfoType type) {
        return chromeExecute((chromeDriver) -> {
            chromeDriver.get("https://nikke.gamekee.com");
            List<WebElement> elementsByXPath = chromeDriver.findElementsByXPath(nikkeListXpath);
            Optional<WebElement> anyMappedChara = elementsByXPath.stream()
                    .filter(ele -> StringUtils.hasLength(ele.getText()))
                    .filter(ele -> ele.getText().contains(name))
                    .findAny();
            anyMappedChara.ifPresent(ele-> driver.executeScript("arguments[0].scrollIntoView()",ele));
            anyMappedChara.ifPresent(WebElement::click);
            tWait(5000);
            List<WebElement> nikkeCharts = chromeDriver.findElementsByXPath(nikkeCharaChartsXpath);
            for (WebElement nikkeChart : nikkeCharts) {
                if (nikkeChart.getSize().getWidth() == 0 || nikkeChart.getSize().getHeight() == 0) {
                    continue;
                }
                if (nikkeChart.getText().startsWith(type.getInfo())) {
                    nikkeChart.findElements(By.xpath(lv10SkillXpath)).forEach(WebElement::click);
                    return new ByteArrayInputStream(nikkeChart.getScreenshotAs(OutputType.BYTES));
                }
            }
            throw new ElementNoFoundException();
        });
    }

    public static void downloadNikkeCharaThumbnail() {
        chromeExecute(chromeDriver -> {
            chromeDriver.get("https://nikke.gamekee.com");
            List<WebElement> elementsByXPath = chromeDriver.findElementsByXPath(nikkeListXpath);
            elementsByXPath.forEach(ele -> {
                String src = ((RemoteWebElement) ele).findElementByXPath("./img").getAttribute("data-src");
                byte[] bytes = HttpUtil.downloadBytes("https:" + src);
                FileUtil.writeFromStream(new ByteArrayInputStream(bytes), new File("nikke/thumbnail/" + ele.getText() + ".png"));
            });
            return null;
        });

    }

    public static InputStream getNikkeCDK(){
        return chromeExecute(chromeDriver -> {
            chromeDriver.get("https://nikke.gamekee.com");
            WebElement eB = chromeDriver.findElementByXPath(nikkeCDKEntryButton);
            eB.click();
            tWait(2000);
            WebElement elementByXPath = chromeDriver.findElementByXPath(nikkeCdkChartXpath);
            byte[] screenshotAs = elementByXPath.getScreenshotAs(OutputType.BYTES);
            return new ByteArrayInputStream(screenshotAs);
        });
    }

    public static String summaryChat(String OContent) {
        return chromeExecute(chromeDriver -> {
            String content = OContent;
            StopWatch stopWatch = new StopWatch();
            stopWatch.start();
            content = EmojiConverter.getInstance().toAlias(content);
            System.out.println("input:\n" + content.substring(0, 200) +
                    "\n------total letters:" + content.length());
            chromeDriver.get("https://chat-simplifier.imzbb.cc/zh/");
            chromeDriver.findElementByXPath(simplifyChatInput).sendKeys(content);
            chromeDriver.findElementByXPath(simplifyChatButton).click();
            boolean hasOutput = false;
            String text = "";
            while (!hasOutput) {
                if (stopWatch.getTotalTimeMillis() > 5 * 60 * 1000L) {
                    stopWatch.stop();
                    return "课代表也开小差了";
                }
                try {
                    Thread.sleep(2000L);
                    String tempText = chromeDriver.findElementByXPath(simplifyChatOutput).getText();
                    System.out.println("now reply letters:" + tempText.length());
                    if (tempText.length() == text.length() && text.length() != 0) {
                        hasOutput = true;
                    }
                    text = tempText;
                } catch (Exception ignore) {
                }
            }
            return text;
        });
    }

    public static InputStream getScreenShotStream(String url){
        return chromeExecute(chromeDriver -> {
            chromeDriver.get(url);
            try {
                Thread.sleep(3000L);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            byte[] screenshotAs = chromeDriver.getScreenshotAs(OutputType.BYTES);
            return new ByteArrayInputStream(screenshotAs);
        });
    }



    private static void printElement(WebElement element,String name){
        byte[] screenshotAs = element.getScreenshotAs(OutputType.BYTES);
        saveTemp(new ByteArrayInputStream(screenshotAs),name);
    }

    private static void tWait(long mil){
        try{
            Thread.sleep(mil);
        }catch (InterruptedException ignore){};
    }
    public static boolean isUrl(String content){
        return content.matches("^((http|https)://)?(([A-Za-z0-9]+-[A-Za-z0-9]+|[A-Za-z0-9]+)\\.)+([A-Za-z]+)[/?:]?.*$");
    }
    public static void  saveTemp(InputStream inputStream,String name){
        FileUtil.writeFromStream(inputStream,new File(name));
    }
}