package org.nekotori.events;

import net.mamoe.mirai.event.EventHandler;
import net.mamoe.mirai.event.EventPriority;
import net.mamoe.mirai.event.ListeningStatus;
import net.mamoe.mirai.event.SimpleListenerHost;
import net.mamoe.mirai.event.events.MemberJoinEvent;
import org.jetbrains.annotations.NotNull;
import org.nekotori.annotations.Event;

@Event
public class MemberJoinEvents extends SimpleListenerHost {

    @EventHandler(priority = EventPriority.NORMAL)
    public ListeningStatus onMessage(@NotNull MemberJoinEvent memberJoinEvent) {
        return ListeningStatus.LISTENING;
    }
}
