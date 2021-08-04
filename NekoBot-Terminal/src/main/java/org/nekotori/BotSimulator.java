package org.nekotori;

import net.mamoe.mirai.Bot;
import net.mamoe.mirai.BotFactory;
import net.mamoe.mirai.event.SimpleListenerHost;
import net.mamoe.mirai.utils.BotConfiguration;
import org.nekotori.events.GroupCommandEvents;
import org.nekotori.utils.SpringContextUtils;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.Executors;


/**
 * @author: JayDeng
 * @date: 2021/6/10 13:43
 * @description:
 * @version: {@link }
 */

public class BotSimulator {

    private static Bot nekoBot;


    public static int run(Long qq, String password, String deviceFile) {
        BotConfiguration botConfiguration = new BotConfiguration();
        botConfiguration.fileBasedDeviceInfo(deviceFile);
        botConfiguration.setProtocol(BotConfiguration.MiraiProtocol.ANDROID_PAD);
//        nekoBot = BotFactory.INSTANCE.newBot(qq, password,botConfiguration);
//        nekoBot.login();
        final Collection<SimpleListenerHost> values = SpringContextUtils.getContext().getBeansOfType(SimpleListenerHost.class).values();
        for(SimpleListenerHost host:values){
            nekoBot.getEventChannel().registerListenerHost(host);
        }
        Executors.newSingleThreadExecutor().execute(nekoBot::join);
        return 0;
    }

    public static Bot getBot(){
        return nekoBot;
    }

}
    