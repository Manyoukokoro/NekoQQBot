package org.nekotori.atme.impl;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import net.mamoe.mirai.contact.Member;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.message.data.At;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import net.mamoe.mirai.message.data.QuoteReply;
import net.mamoe.mirai.message.data.SingleMessage;
import org.nekotori.annotations.AtMe;
import org.nekotori.atme.NoAuthAtMeResponse;
import org.nekotori.chatbot.ChatBot;
import org.nekotori.chatbot.ChatGptAdaptor;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

@AtMe
public class ChatBotResponse extends NoAuthAtMeResponse {


//    private static Map<Member,ChatBot> targetMap = new HashMap<>();
//
//    public static void deleteCache(){
//        targetMap = new HashMap<>();
//    }

    @Override
    public MessageChain response(GroupMessageEvent groupMessageEvent) {
        Iterator<SingleMessage> iterator = groupMessageEvent.getMessage().iterator();
        StringBuilder stringBuilder = new StringBuilder();
        while (iterator.hasNext()){
            SingleMessage next = iterator.next();
            if (!(next instanceof At) && !next.contentToString().startsWith("@")){
                stringBuilder.append(next.contentToString());
            }
        }
        String input = stringBuilder.toString();
        JSONObject jsonObject = new JSONObject();
        jsonObject.putOnce("message",input);
        String body = jsonObject.toString();
        String response;
        try {
        String resp = HttpUtil.createPost("http://127.0.0.1:3000/conversation")
                .body(body)
                .execute()
                .body();
        JSONObject respJson = JSONUtil.parseObj(resp);
        System.out.printf(respJson.toStringPretty());
        response = respJson.getStr("response");
        }catch (Exception e){
            e.printStackTrace();
            return new MessageChainBuilder().append(new QuoteReply(groupMessageEvent.getMessage())).append("ops! please contact administrator, the error info is gpt error: ").append(e.getCause().getMessage()).build();
        }
        return new MessageChainBuilder().append(new QuoteReply(groupMessageEvent.getMessage())).append(response).build();
    }

//    @Override
//    public MessageChain response(GroupMessageEvent groupMessageEvent) {
//        if(targetMap.get(groupMessageEvent.getSender())== null){
//            ChatGptAdaptor chatGptAdaptor = new ChatGptAdaptor();
//            targetMap.put(groupMessageEvent.getSender(), chatGptAdaptor);
//        }
//        ChatBot chatBot = targetMap.get(groupMessageEvent.getSender());
//        Iterator<SingleMessage> iterator = groupMessageEvent.getMessage().iterator();
//        StringBuilder stringBuilder = new StringBuilder();
//        while (iterator.hasNext()){
//            SingleMessage next = iterator.next();
//            if (!(next instanceof At) && !next.contentToString().startsWith("@")){
//                stringBuilder.append(next.contentToString());
//            }
//        }
//        String reply = "";
//        try {
//            reply = chatBot.getReply(stringBuilder.toString());
//        }catch (Exception e){
//            e.printStackTrace();
//            return new MessageChainBuilder().append(new QuoteReply(groupMessageEvent.getMessage())).append("ops! please contact administrator, the error info is gpt error: ").append(e.getCause().getMessage()).build();
//        }
//        return new MessageChainBuilder().append(new QuoteReply(groupMessageEvent.getMessage())).append(reply).build();
//    }
}
