package org.nekotori.events;

import net.mamoe.mirai.event.EventHandler;
import net.mamoe.mirai.event.EventPriority;
import net.mamoe.mirai.event.ListeningStatus;
import net.mamoe.mirai.event.SimpleListenerHost;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.message.data.At;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.SingleMessage;
import org.jetbrains.annotations.NotNull;
import org.nekotori.annotations.Event;


@Event
public class AtBotEvents extends SimpleListenerHost {

    @NotNull
    @EventHandler(priority = EventPriority.NORMAL)
    public ListeningStatus onMessage(@NotNull GroupMessageEvent groupMessageEvent) {
        MessageChain message = groupMessageEvent.getMessage();
        boolean isAtMe = false;
        for(SingleMessage s:message){
            if(s instanceof At && ((At)s).getTarget()==groupMessageEvent.getBot().getId()){
                isAtMe = true;
            }
        }
        if(isAtMe) atMeResponse(groupMessageEvent);
        return ListeningStatus.LISTENING;
    }

    private void atMeResponse(GroupMessageEvent groupMessageEvent){

    }
}
