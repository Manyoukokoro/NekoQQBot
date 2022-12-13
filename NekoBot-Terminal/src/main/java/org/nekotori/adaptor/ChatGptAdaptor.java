package org.nekotori.adaptor;


import com.github.plexpt.chatgpt.Chatbot;

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
            this.chatbot = ChatGptBotFactory.NEW_INSTANCE(this.chatbot.getSessionToken());
        }catch (Exception e){
            return false;
        }
        return true;
    }
}
