package org.nekotori;

import net.mamoe.mirai.Bot;
import net.mamoe.mirai.BotFactory;
import net.mamoe.mirai.event.ListenerHost;
import net.mamoe.mirai.event.SimpleListenerHost;
import net.mamoe.mirai.utils.BotConfiguration;
import org.nekotori.annotations.Event;
import org.nekotori.commands.GlobalCommandHandler;
import org.nekotori.common.SpringStyleBotLogger;
import org.nekotori.events.GroupCommandEvents;
import org.nekotori.service.GroupService;
import org.nekotori.utils.SpringContextUtils;

import java.util.Collection;
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


    public static int run(Long qq, String password, String deviceFile/*, GlobalCommandHandler globalCommandHandler, GroupService groupService*/) {
        BotConfiguration botConfiguration = new BotConfiguration();
        botConfiguration.fileBasedDeviceInfo(deviceFile);
        botConfiguration.setProtocol(BotConfiguration.MiraiProtocol.ANDROID_PAD);
        botConfiguration.setBotLoggerSupplier(b->new SpringStyleBotLogger());
        botConfiguration.setNetworkLoggerSupplier(b-> new SpringStyleBotLogger());
        nekoBot = BotFactory.INSTANCE.newBot(qq, password,botConfiguration);
        nekoBot.login();
        Map<String, Object> beansWithAnnotation = SpringContextUtils.getContext().getBeansWithAnnotation(Event.class);
        beansWithAnnotation.values().forEach((event)->{
            if(event instanceof ListenerHost){
                nekoBot.getEventChannel().registerListenerHost((ListenerHost)event);
            }
        });
        Executors.newSingleThreadExecutor().execute(nekoBot::join);
        return 0;
    }

    public static Bot getBot(){
        return nekoBot;
    }

}
    