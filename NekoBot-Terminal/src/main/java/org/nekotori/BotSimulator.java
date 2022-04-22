package org.nekotori;

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

    public static void main(String[] args) {
        String body = HttpRequest.get("https://api-simulator-toolbox.ghzs.com/v1d0/web/simulator/princess_connect_re_dive/roles").execute().body();
        JSONArray objects = JSONUtil.parseArray(body);
        objects.stream().map(o -> {
            JSONObject card = JSONUtil.parseObj(o);
            String pic_url = card.get("pic_url").toString();
            String name = card.get("name").toString();
            String grade = card.get("grade").toString();
            JSONArray card_pool_belonged = card.getJSONArray("card_pool_belonged");
            List<String> belonged = card_pool_belonged.stream().map(Object::toString).collect(Collectors.toList());
            String id = card.get("_id").toString();
            File file = new File("jpg/" + id.toString() + ".jpg");
            HttpUtil.downloadFile(pic_url.toString(), file);
            return null;
        });
    }

    public static final String update = "NekoBot升级完成！\n" + "1.略微修改了五子棋棋盘的显示效果\n" + "2.极大降低了签到获得个位数经验的概率\n";


    public static void run(Long qq, String password, String deviceFile) {
        BotConfiguration botConfiguration = new BotConfiguration();
        botConfiguration.fileBasedDeviceInfo(deviceFile);
        //https://github.com/mamoe/mirai/issues/1209 : 当协议选择ANDROID_PAD/WATCH时，概率出现被腾讯风控而发不出消息的异常
        botConfiguration.setProtocol(BotConfiguration.MiraiProtocol.ANDROID_PHONE);
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
        ContactList<Group> groups = nekoBot.getGroups();
//        groups.forEach(group->{
//            group.sendMessage(update);
//        });
    }

    public static Bot getBot() {
        return nekoBot;
    }

}
    