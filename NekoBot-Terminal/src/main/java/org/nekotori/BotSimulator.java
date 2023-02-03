package org.nekotori;

import cn.hutool.core.util.NumberUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.BotFactory;
import net.mamoe.mirai.contact.ContactList;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.event.ListenerHost;
import net.mamoe.mirai.utils.BotConfiguration;
import org.nekotori.annotations.Event;
import org.nekotori.common.SpringStyleBotLogger;
import org.nekotori.utils.SpringContextUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;


/**
 * @author: JayDeng
 * @date: 2021/6/10 13:43
 * @description:
 * @version: {@link }
 */

public class BotSimulator {
    private static Bot nekoBot;
    public static void run(Long qq, String password, String deviceFile) {
        BotConfiguration botConfiguration = new BotConfiguration();
        botConfiguration.fileBasedDeviceInfo(deviceFile);
        //https://github.com/mamoe/mirai/issues/1209 : 当协议选择ANDROID_PAD/WATCH时，概率出现被腾讯风控而发不出消息的异常
        botConfiguration.setProtocol(BotConfiguration.MiraiProtocol.ANDROID_PAD);
        botConfiguration.setBotLoggerSupplier(b -> new SpringStyleBotLogger());
        botConfiguration.setNetworkLoggerSupplier(b -> new SpringStyleBotLogger());
        nekoBot = BotFactory.INSTANCE.newBot(qq, password, botConfiguration);
        nekoBot.login();
        Map<String, Object> beansWithAnnotation = SpringContextUtils.getContext().getBeansWithAnnotation(Event.class);
        beansWithAnnotation.values().forEach((event) -> {
            if (event instanceof ListenerHost) {
                nekoBot.getEventChannel().registerListenerHost((ListenerHost) event);
            }
        });
        Executors.newSingleThreadExecutor().execute(nekoBot::join);
    }

    public static Bot getBot() {
        return nekoBot;
    }
}
    