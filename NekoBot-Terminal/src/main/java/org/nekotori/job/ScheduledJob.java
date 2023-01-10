package org.nekotori.job;


import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.XmlUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.contact.ContactList;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.message.data.Image;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import org.nekotori.BotSimulator;
import org.nekotori.atme.impl.ChatGPTResponse;
import org.nekotori.chain.ChainMessageSelector;
import org.nekotori.chain.channel.GroupCommandChannel;
import org.nekotori.dao.ChatGroupMapper;
import org.nekotori.entity.ChatGroupDo;
import org.nekotori.utils.ImageUtil;
import org.nekotori.utils.JsonUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import javax.annotation.Resource;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.Map;
import java.util.Objects;

@Component
public class ScheduledJob {

    @Resource
    private ChainMessageSelector chainMessageSelector;

    @Resource
    private ChatGroupMapper chatGroupMapper;

    @Scheduled(cron = "0 0 8 * * ?")
    public void moyuCalendar(){
        Bot bot = BotSimulator.getBot();
        HttpRequest get = HttpUtil.createGet("https://api.j4u.ink/v1/store/other/proxy/remote/moyu.json");
        get = get.setConnectionTimeout(5000).setReadTimeout(10000);
        HttpResponse execute = get.execute();
        String body = execute.body();
        JSONObject jsonObject = JSONUtil.parseObj(body);
        JSONObject data = jsonObject.getJSONObject("data");
        String moyuUrl = data.getStr("moyu_url");
        String redirect = HttpUtil.createGet(moyuUrl).setConnectionTimeout(5000).setReadTimeout(10000).execute().body();
        String[] split = redirect.split("<title>Redirecting to ");
        String s = split[1];
        String[] trueUrl = s.split("</title>");
        String location = HttpUtil.createGet(trueUrl[0]).setConnectionTimeout(5000).setReadTimeout(10000).execute().header("location");
        HttpUtil.downloadFile(location, new File("temp.png"));
        ContactList<Group> groups = bot.getGroups();
        groups.parallelStream().forEach(group->{
            BufferedInputStream inputStream = FileUtil.getInputStream(new File("temp.png"));
            group.sendMessage(new MessageChainBuilder().append(Contact.uploadImage(group,inputStream)).build());
        });
    }

    //@Scheduled(cron = "0 0 4 * * ?")
    public void deleteChatGPTCache(){
        ChatGPTResponse.deleteCache();
    }

    public static void main(String[] args) {
    }

    @Scheduled(cron = "0/10 * * * * ?")
    public void removeExpireChannel(){
        final Map<String, GroupCommandChannel> channels = chainMessageSelector.getChannels();
        if (channels == null){
            return;
        }
        channels.forEach((key, value) -> {
            if(ObjectUtils.isEmpty(value)){
                return;
            }
            if (value.getExpireTime() < System.currentTimeMillis()) {
                chainMessageSelector.removeChannel(key);
            }
        });
    }

    @Scheduled(cron = "0/10 * * * * ?")
    public void reminder() {

    }
}
