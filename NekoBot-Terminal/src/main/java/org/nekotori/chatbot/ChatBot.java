package org.nekotori.chatbot;

public interface ChatBot {
    String getReply(String userInput);

    boolean refresh();

}
