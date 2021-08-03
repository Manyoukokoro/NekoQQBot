package org.nekotori;

import net.mamoe.mirai.Bot;
import net.mamoe.mirai.BotFactory;
import net.mamoe.mirai.utils.BotConfiguration;
import org.nekotori.events.GroupCommandEvents;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executors;


/**
 * @author: JayDeng
 * @date: 2021/6/10 13:43
 * @description:
 * @version: {@link }
 */

@Component
public class BotSimulator {

    private Bot nekoBot;


    public int run(Long qq, String password, String deviceFile) {
        BotConfiguration botConfiguration = new BotConfiguration();
        botConfiguration.fileBasedDeviceInfo(deviceFile);
        botConfiguration.setProtocol(BotConfiguration.MiraiProtocol.ANDROID_PAD);
        nekoBot = BotFactory.INSTANCE.newBot(qq, password,botConfiguration);
        nekoBot.login();
        nekoBot.getEventChannel().registerListenerHost(new GroupCommandEvents());
        Executors.newSingleThreadExecutor().execute(nekoBot::join);
        return 0;
    }

    public Bot getBot(){
        return nekoBot;
    }

}
    