package org.nekotori.events;

import net.mamoe.mirai.event.EventHandler;
import net.mamoe.mirai.event.EventPriority;
import net.mamoe.mirai.event.ListeningStatus;
import net.mamoe.mirai.event.SimpleListenerHost;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import org.jetbrains.annotations.NotNull;
import org.nekotori.annotations.Event;
import org.nekotori.job.AsyncJob;

import javax.annotation.Resource;


@Event
public class GroupChainCommandEvents extends SimpleListenerHost {

    @Resource
    private AsyncJob asyncJob;

    @NotNull
    @EventHandler(priority = EventPriority.HIGH)
    public ListeningStatus onMessage(@NotNull GroupMessageEvent groupMessageEvent) {
        asyncJob.messageSelect(groupMessageEvent);
        return ListeningStatus.LISTENING;
    }

}
