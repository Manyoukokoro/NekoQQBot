package org.nekotori.atme.impl;

import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.message.data.At;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import net.mamoe.mirai.message.data.PlainText;
import org.nekotori.annotations.AtMe;
import org.nekotori.atme.KeyWordsAtMeResponse;

/**
 * @author: JayDeng
 * @date: 25/08/2021 14:44
 * @description:
 * @version: {@link }
 */
@AtMe(description = {"hello","喂","你好","在？"})
public class DemoAtMeResponse extends KeyWordsAtMeResponse {

    @Override
    public MessageChain response(GroupMessageEvent groupMessageEvent) {
        return new MessageChainBuilder().append(new At(groupMessageEvent.getSender().getId())).append(new PlainText(" 呼んだ？")).build();
    }
}
    