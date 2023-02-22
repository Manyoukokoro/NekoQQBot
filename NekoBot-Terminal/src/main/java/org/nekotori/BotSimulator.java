package org.nekotori;

import cn.hutool.core.util.NumberUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import discord4j.common.ReactorResources;
import discord4j.common.util.Snowflake;
import discord4j.core.DiscordClient;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.Channel;
import discord4j.core.object.entity.channel.MessageChannel;
import discord4j.core.object.entity.channel.TextChannel;
import discord4j.discordjson.Id;
import discord4j.discordjson.json.MemberData;
import discord4j.discordjson.json.UserData;
import discord4j.gateway.GatewayReactorResources;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.BotFactory;
import net.mamoe.mirai.contact.ContactList;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.event.ListenerHost;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import net.mamoe.mirai.utils.BotConfiguration;
import org.nekotori.annotations.Event;
import org.nekotori.common.SpringStyleBotLogger;
import org.nekotori.handler.ThreadSingleton;
import org.nekotori.utils.SpringContextUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import reactor.core.publisher.Flux;
import reactor.netty.http.client.HttpClient;
import reactor.netty.transport.ProxyProvider;

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

    private static GatewayDiscordClient dcBot;


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

    public static GatewayDiscordClient getDcBot(){
        return dcBot;
    }
    public static void runDc(String token,String proxyHost,Integer port) {
        ThreadSingleton.run(()->{
            HttpClient secure = HttpClient.create()
                    .proxy(addressSpec -> addressSpec.type(ProxyProvider.Proxy.HTTP).host(proxyHost).port(port))
                    .compress(true);
            final DiscordClient client = DiscordClient.builder(token)
                    .setReactorResources(
                            ReactorResources.builder().httpClient(secure).build()
                    ).build();
            final GatewayDiscordClient gateway = client.gateway()
                    .setGatewayReactorResources(resources->new GatewayReactorResources(ReactorResources.builder().httpClient(secure).build()))
                    .login().block();
            dcBot = gateway;
            gateway.on(MessageCreateEvent.class).subscribe(event->{
                long id = event.getGuild().map(Guild::getId).block().asLong();
                Member member = event.getMember().get();
                String s = member.getUserData().username();
                if("NekoBot".equals(s)){
                    return;
                }
                if(id == 1057152339997372456L){
                    Message message = event.getMessage();
                    String content = message.getContent();
                    Bot bot = BotSimulator.getBot();
                    Group group = bot.getGroup(541483336L);
                    group.sendMessage(new MessageChainBuilder()
                            .append(content)
                            .append("\n")
                            .append("-----来自Discord:")
                            .append(event.getMember()
                                    .map(Member::getUserData)
                                    .map(UserData::username)
                                    .orElse("未知"))
                            .append("的消息")
                            .build());
                }
            });
            gateway.onDisconnect().block();
        });
    }
}
    