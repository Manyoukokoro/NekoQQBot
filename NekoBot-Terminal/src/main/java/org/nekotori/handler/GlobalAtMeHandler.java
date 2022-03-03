package org.nekotori.handler;

import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.message.data.At;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.SingleMessage;
import org.nekotori.atme.AtMeResponse;
import org.nekotori.utils.SpringContextUtils;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @author: JayDeng
 * @date: 25/08/2021 14:25
 * @description:
 * @version: {@link }
 */
@Component
public class GlobalAtMeHandler {

    private static Map<String, AtMeResponse> innerAtMes = new HashMap<>();

    public static void init() {
        innerAtMes = SpringContextUtils.getContext().getBeansOfType(AtMeResponse.class);
    }

    public void handle(GroupMessageEvent groupMessageEvent){
        MessageChain message = groupMessageEvent.getMessage();
        boolean isAtMe = false;
        for(SingleMessage s:message){
            if(s instanceof At && ((At)s).getTarget()==groupMessageEvent.getBot().getId()){
                isAtMe = true;
            }
            if(s.contentToString().contains("@"+ groupMessageEvent.getBot().getNick())){
                isAtMe = true;
            }
        }
        if(isAtMe){
            for (AtMeResponse atMe:innerAtMes.values()){
                if(atMe.checkAuthorization(groupMessageEvent)){
                    ThreadSingleton.run(()->
                            groupMessageEvent.getGroup().sendMessage(atMe.response(groupMessageEvent)));
                }
            }
        }
    }
}
    