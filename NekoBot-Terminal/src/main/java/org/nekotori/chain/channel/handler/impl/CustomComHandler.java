package org.nekotori.chain.channel.handler.impl;

import net.mamoe.mirai.event.events.GroupMessageEvent;
import org.nekotori.annotations.HandlerId;
import org.nekotori.chain.channel.GroupCommandChannel;
import org.nekotori.chain.channel.handler.ChannelHandler;
import org.nekotori.commands.CustomCommand;

import java.util.Deque;
import java.util.List;

/**
 * @author: JayDeng
 * @date: 2022/2/28 下午3:02
 * @description: CustomComHandler
 * @version: {@link }
 */
@HandlerId("32146571")
public class CustomComHandler implements ChannelHandler {

private static final CustomCommand.STAGES[] stages = CustomCommand.STAGES.values();

    @Override
    public List<String> getStages() {
        return null;
    }

    @Override
    public void handleMessage(GroupCommandChannel channel, GroupMessageEvent groupMessageEvent) {

    }
}
