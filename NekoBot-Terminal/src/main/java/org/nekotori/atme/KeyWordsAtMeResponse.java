package org.nekotori.atme;

import net.mamoe.mirai.event.events.GroupMessageEvent;
import org.nekotori.annotations.AtMe;

/**
 * @author: JayDeng
 * @date: 30/08/2021 12:15
 * @description:
 * @version: {@link }
 */
public abstract class KeyWordsAtMeResponse implements AtMeResponse {

    @Override
    public boolean checkAuthorization(GroupMessageEvent groupMessageEvent){
        final String content = groupMessageEvent.getMessage().contentToString();
        final String[] description = this.getClass().getAnnotation(AtMe.class).description();
        for(String s: description){
            if(content.contains(s)){
                return true;
            }
        }
        return false;
    }
}
    