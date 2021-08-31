package org.nekotori.chain.channel.handler;

import net.mamoe.mirai.event.events.GroupMessageEvent;

import java.util.Deque;

/**
 * @author: JayDeng
 * @date: 31/08/2021
 * @time: 14:51
 */
public interface ChannelHandler {

    void handleMessage(Deque<GroupMessageEvent> his, GroupMessageEvent groupMessageEvent);
}
