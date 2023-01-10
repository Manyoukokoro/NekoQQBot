package org.nekotori.chatbot;

public interface ChatBot {

    String getReply(String userInput,String conversationId);

    boolean refresh();

}
