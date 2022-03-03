package org.nekotori.chain.channel.handler;

import net.mamoe.mirai.event.events.GroupMessageEvent;
import org.nekotori.chain.channel.GroupCommandChannel;

import java.util.Deque;
import java.util.List;

/**
 * @author: JayDeng
 * @date: 31/08/2021
 * @time: 14:51
 */
public interface ChannelHandler {

    List<String> getStages();

    void handleMessage(GroupCommandChannel channel, GroupMessageEvent groupMessageEvent);
}
