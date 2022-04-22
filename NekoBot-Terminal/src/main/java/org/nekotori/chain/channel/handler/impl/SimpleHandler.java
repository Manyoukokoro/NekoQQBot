package org.nekotori.chain.channel.handler.impl;

import net.mamoe.mirai.event.events.GroupMessageEvent;
import org.nekotori.annotations.HandlerId;
import org.nekotori.chain.channel.GroupCommandChannel;
import org.nekotori.chain.channel.handler.ChannelHandler;

import java.util.Deque;
import java.util.List;

/**
 * @author: JayDeng
 * @date: 31/08/2021 15:55
 * @description:
 * @version: {@link }
 */
@HandlerId("73767478")
public class SimpleHandler implements ChannelHandler {
    @Override
    public List<String> getStages() {
        return null;
    }

    @Override
    public void handleMessage(GroupCommandChannel channel, GroupMessageEvent groupMessageEvent) {
        Deque<GroupMessageEvent> his = channel.getMessageHisQueue();
        if (his.size() == 0) return;
        final GroupMessageEvent last = his.getLast();
        final String s = last.getMessage().serializeToMiraiCode();
        if (groupMessageEvent.getMessage().serializeToMiraiCode().equals(s)) {
            groupMessageEvent.getGroup().sendMessage(groupMessageEvent.getMessage());
        }
    }
}
    