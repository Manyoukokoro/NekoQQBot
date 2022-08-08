package org.nekotori.chain.channel.handler;

import net.mamoe.mirai.event.events.GroupMessageEvent;
import org.nekotori.chain.channel.GroupCommandChannel;

import java.util.List;

/**
 * @author: JayDeng
 * @date: 31/08/2021
 * @time: 14:51
 */
public interface ChannelHandler {

    default List<String> getStages(){
        return null;
    };

    void handleMessage(GroupCommandChannel channel, GroupMessageEvent groupMessageEvent);
}
