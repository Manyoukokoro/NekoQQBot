package org.nekotori.job;


import cn.hutool.core.io.FileUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.contact.ContactList;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.message.data.At;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import org.nekotori.BotSimulator;
import org.nekotori.atme.impl.ChatGPTResponse;
import org.nekotori.chain.ChainMessageSelector;
import org.nekotori.chain.channel.GroupCommandChannel;
import org.nekotori.dao.ChatGroupMapper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.io.BufferedInputStream;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@Component
public class ScheduledJob {

    @Resource
    private ChainMessageSelector chainMessageSelector;

    @Resource
    private ChatGroupMapper chatGroupMapper;

    @Scheduled(cron = "0 0 * * * ?")
    public void urlMonitor(){
        List<String> urls = FileUtil.readLines(new File("monitor.info"), StandardCharsets.UTF_8);
        if(CollectionUtils.isEmpty(urls)){
            return;
        }
        urls.parallelStream().forEach(url->{
            String[] split = url.split("#sp#");
            Group group = BotSimulator.getBot().getGroup(Long.parseLong(split[0]));
            if(group == null){
                return;
            }
            String body;
            try {
                 body= HttpUtil.createGet(split[2]).setConnectionTimeout(2000).setReadTimeout(2000).execute().body();
            }catch (Exception e){
                group.sendMessage(new MessageChainBuilder().append(new At(Long.parseLong(split[1]))).append("检测到地址").append(split[2]).append("无响应").build());
                return;
            }
            try{
                JSONObject jsonObject = JSONUtil.parseObj(body);
                Integer code = jsonObject.getInt("code");
                String message = jsonObject.getStr("message");
                Boolean display = jsonObject.getBool("display");
                if(display){
                    MessageChain build = new MessageChainBuilder().append(new At(Long.parseLong(split[1]))).append("\n来自").append(split[2]).append("的服务器告警:").append("\n状态码: ").append("").append(String.valueOf(code)).append("\n消息: ").append(message).build();
                    group.sendMessage(build);
                }
            }catch (Exception ignore){
            }
        });
    }

    //@Scheduled(cron = "0 0 8 * * ?")
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
