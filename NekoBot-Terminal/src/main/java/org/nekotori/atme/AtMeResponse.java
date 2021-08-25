package org.nekotori.atme;

import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.message.data.MessageChain;

/**
 * @author: JayDeng
 * @date: 25/08/2021
 * @time: 14:30
 */
public interface AtMeResponse {

    boolean checkAuthorization(GroupMessageEvent groupMessageEvent);

    MessageChain response(GroupMessageEvent groupMessageEvent);

}
