package org.nekotori.adaptor;

public interface ChatBot {

    String getReply(String userInput,String conversationId);

    boolean refresh();

}
