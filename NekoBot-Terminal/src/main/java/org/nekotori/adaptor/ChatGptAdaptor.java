package org.nekotori.adaptor;


import com.github.plexpt.chatgpt.Chatbot;
import cn.hutool.core.io.FileUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;

import java.io.File;
import java.nio.charset.StandardCharsets;

public class ChatGptAdaptor implements ChatBot {

    private Chatbot chatbot;

    public ChatGptAdaptor(String token){
        this.chatbot = ChatGptBotFactory.NEW_INSTANCE(token);
    }

    @Override
    public String getReply(String userInput, String conversationId) {
        String sessionToken = chatbot.getSessionToken();
        return chatbot.getChatResponse(userInput).get("message").toString();

    }

    @Override
    public boolean refresh() {
        try {
            String chatGptConfS = FileUtil.readString(new File("chat-gpt.conf"), StandardCharsets.UTF_8);
            String key = "";
            try {
                JSONObject chatGptConf = JSONUtil.parseObj(chatGptConfS);
                key = chatGptConf.getStr("api-key");
            }catch (Exception e){
                return false;
            }
            this.chatbot = ChatGptBotFactory.NEW_INSTANCE(key);
        }catch (Exception e){
            return false;
        }
        return true;
    }
}
