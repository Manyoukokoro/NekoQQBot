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
import org.nekotori.job.AsyncJob;

import javax.annotation.Resource;


@Event
public class AtBotEvents extends SimpleListenerHost {

    @Resource
    private AsyncJob asyncJob;

    @NotNull
    @EventHandler(priority = EventPriority.NORMAL)
    public ListeningStatus onMessage(@NotNull GroupMessageEvent groupMessageEvent) {
        asyncJob.handleAtMe(groupMessageEvent);
        return ListeningStatus.LISTENING;
    }

}
