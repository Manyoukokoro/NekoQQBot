package org.nekotori.chain.channel.handler;

import net.mamoe.mirai.event.events.GroupMessageEvent;
import org.nekotori.annotations.TaskHash;

import java.util.Deque;

/**
 * @author: JayDeng
 * @date: 31/08/2021 15:55
 * @description:
 * @version: {@link }
 */
@TaskHash("73767478")
public class SimpleHandler implements ChannelHandler {
    @Override
    public void handleMessage(Deque<GroupMessageEvent> his, GroupMessageEvent groupMessageEvent) {
        if(his.size()==0) return;
        final GroupMessageEvent last = his.getLast();
        final String s = last.getMessage().contentToString();
        if(groupMessageEvent.getMessage().contentToString().equals(s)){
            groupMessageEvent.getGroup().sendMessage(groupMessageEvent.getMessage());
        }
    }
}
    