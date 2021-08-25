package org.nekotori.atme.impl;

import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.message.data.At;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import net.mamoe.mirai.message.data.PlainText;
import org.nekotori.annotations.AtMe;
import org.nekotori.atme.AtMeResponse;

/**
 * @author: JayDeng
 * @date: 25/08/2021 14:44
 * @description:
 * @version: {@link }
 */
@AtMe(description = "hello")
public class DemoAtMeResponse implements AtMeResponse {
    @Override
    public boolean checkAuthorization(GroupMessageEvent groupMessageEvent) {
        return true;
    }

    @Override
    public MessageChain response(GroupMessageEvent groupMessageEvent) {
        return new MessageChainBuilder().append(new At(groupMessageEvent.getSender().getId())).append(new PlainText("呼んだ？")).build();
    }
}
    