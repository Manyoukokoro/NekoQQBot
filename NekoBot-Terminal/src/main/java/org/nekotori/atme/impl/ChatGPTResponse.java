package org.nekotori.atme.impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.github.plexpt.chatgpt.Chatbot;
import net.mamoe.mirai.contact.Member;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.message.data.At;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import org.nekotori.annotations.AtMe;
import org.nekotori.atme.NoAuthAtMeResponse;
import org.nekotori.common.InnerConstants;
import org.springframework.util.ObjectUtils;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@AtMe
public class ChatGPTResponse extends NoAuthAtMeResponse {


    private static Map<Member,Chatbot> targetMap = new HashMap<>();
    private static Map<Long, Long> lastQues = new HashMap<>();

    public static void deleteCache(){
        targetMap = new HashMap<>();
    }

    @Override
    public MessageChain response(GroupMessageEvent groupMessageEvent) {
        String chatGptConfS = FileUtil.readString(new File("chat-gpt.conf"), StandardCharsets.UTF_8);
        String key = "";
        try {
            JSONObject chatGptConf = JSONUtil.parseObj(chatGptConfS);
            key = chatGptConf.getStr("api-key");
        }catch (Exception e){
            return null;
        }
        Member sender = groupMessageEvent.getSender();
        long nowTime = System.currentTimeMillis();
        if (sender.getId() != InnerConstants.admin && lastQues.get(sender.getId())!=null && (nowTime-lastQues.get(sender.getId()))<60*1000L){
            return new MessageChainBuilder().append(new At(sender.getId()))
                    .append("\n")
                    .append("我检测到您曾经在不到1分钟之前于本群或其他群向我提问，请减缓频率防止给服务器带来过大压力").build();
        }
        if(targetMap.get(sender)==null){
            Chatbot chatbot = new Chatbot(key);
            targetMap.put(sender,chatbot);
        }
        if(lastQues.get(sender.getId())!=null){
            lastQues.replace(sender.getId(),System.currentTimeMillis());
        }else {
            lastQues.put(sender.getId(),System.currentTimeMillis());
        }
        Chatbot chatbot = targetMap.get(sender);
        MessageChain messageChain = groupMessageEvent.getMessage();
        String s1 = messageChain.serializeToMiraiCode();
        String trim = s1.replaceAll("\\[.*]", "").trim();
        Map<String, Object> chatResponse = chatbot.getChatResponse(trim);
        Object message = chatResponse.get("message");
        if(ObjectUtils.isEmpty(message)){
            return new MessageChainBuilder().append(new At(sender.getId())).append("\n").append("ChatGPT服务已离线").build();
        }
        return new MessageChainBuilder().append(new At(sender.getId())).append("\n").append(String.valueOf(message)).build();
    }
}
