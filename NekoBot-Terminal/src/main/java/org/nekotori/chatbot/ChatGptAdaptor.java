package org.nekotori.chatbot;


import cn.hutool.http.ContentType;
import cn.hutool.http.Header;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.LinkedList;

public class ChatGptAdaptor implements ChatBot {

    private LinkedList<HISTORY> history;
    private String privateKey = "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";

    private static final String END_POINT = "https://api.openai.com/v1/chat/completions";
    private static final String MODEL = "model";
    private static final String MODEL_VALUE = "gpt-3.5-turbo";
    private static final String MESSAGE = "messages";
    public ChatGptAdaptor(){
        history = new LinkedList<>();
    }

    private ChatGptAdaptor(String key){
        history = new LinkedList<>();
        privateKey = key;
    }

    public static void main(String[] args) {
        ChatGptAdaptor chatGptAdaptor = new ChatGptAdaptor();
        System.out.println(chatGptAdaptor.getReply("你好"));
    }

    @Override
    public String getReply(String userInput) {
        history.add(new HISTORY(HISTORY.USER,userInput));
        if (history.size()>3){
            history.removeFirst();
        }
        JSONObject body = new JSONObject();
        body.putOnce(MODEL,MODEL_VALUE);
        body.putOnce(MESSAGE,history);
        String response = getResponse(body);
        return response;
    }

    @NotNull
    private String getResponse(JSONObject body) {
        String res = HttpUtil.createPost(END_POINT)
                .setProxy(new Proxy(Proxy.Type.HTTP, InetSocketAddress.createUnresolved("127.0.0.1",19192)))
                .header(Header.CONTENT_TYPE, ContentType.JSON.getValue())
                .header(Header.AUTHORIZATION, "Bearer " + privateKey)
                .body(body.toString())
                .execute()
                .body();
        JSONArray choices = JSONUtil.parseObj(res).getJSONArray("choices");
        JSONObject choice = choices.getJSONObject(0);
        JSONObject message = choice.getJSONObject("message");
        return message.getStr("content").trim();
    }

    @Override
    public boolean refresh() {
        history = new LinkedList<>();
        return true;
    }

    @AllArgsConstructor
    @Getter
    private static class HISTORY{
        static final String USER = "user";

        static final String ASSISTANT = "assistant";

        private String role;

        private String content;
    }
}
