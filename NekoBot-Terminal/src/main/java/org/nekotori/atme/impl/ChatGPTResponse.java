package org.nekotori.atme.impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import net.mamoe.mirai.contact.Member;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.message.data.At;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import net.mamoe.mirai.message.data.QuoteReply;
import org.nekotori.chatbot.ChatBot;
import org.nekotori.chatbot.ChatGptAdaptor;
import org.nekotori.annotations.AtMe;
import org.nekotori.atme.NoAuthAtMeResponse;
import org.nekotori.common.InnerConstants;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

//@AtMe
public class ChatGPTResponse extends NoAuthAtMeResponse {


    private static Map<Member,ChatBot> targetMap = new HashMap<>();
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
            return new MessageChainBuilder().append(new QuoteReply(groupMessageEvent.getMessage()))
                    .append("\n")
                    .append("我检测到您曾经在不到1分钟之前于本群或其他群向我提问，请减缓频率防止给服务器带来过大压力").build();
        }
        if(targetMap.get(sender)==null){
            ChatBot chatbot = new ChatGptAdaptor(key);
            targetMap.put(sender,chatbot);
        }
        if(lastQues.get(sender.getId())!=null){
            lastQues.replace(sender.getId(),System.currentTimeMillis());
        }else {
            lastQues.put(sender.getId(),System.currentTimeMillis());
        }
        ChatBot chatbot = targetMap.get(sender);
        MessageChain messageChain = groupMessageEvent.getMessage();
        String s1 = messageChain.serializeToMiraiCode();
        String trim = s1.replaceAll("\\[.*]", "").trim();
        trim = trim.replaceFirst(".* ", "");
        if("重置".equals(trim)){
            return new MessageChainBuilder().append(chatbot.refresh() ? "重置成功" : "重置失败").build();
        }
        try {
            String message = chatbot.getReply(trim, sender.getGroup() + String.valueOf(sender.getId() + sender.getId()));
            return new MessageChainBuilder().append(new QuoteReply(groupMessageEvent.getMessage())).append("\n").append(message).build();
        }catch (Exception ignore){
        }
        return new MessageChainBuilder().append(new QuoteReply(groupMessageEvent.getMessage())).append("\n").append("ChatGPT服务暂时离线，请稍后再试。或@我并发送“重置”来重置会话").build();
    }
}
