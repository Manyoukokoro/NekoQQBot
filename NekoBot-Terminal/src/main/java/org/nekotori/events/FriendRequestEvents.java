package org.nekotori.events;

import net.mamoe.mirai.event.EventHandler;
import net.mamoe.mirai.event.EventPriority;
import net.mamoe.mirai.event.ListeningStatus;
import net.mamoe.mirai.event.SimpleListenerHost;
import net.mamoe.mirai.event.events.NewFriendRequestEvent;
import org.jetbrains.annotations.NotNull;
import org.nekotori.annotations.Event;

@Event
public class FriendRequestEvents extends SimpleListenerHost {
    @NotNull
    @EventHandler(priority = EventPriority.HIGH)
    public ListeningStatus onMessage(@NotNull NewFriendRequestEvent friendRequestEvent) {
        friendRequestEvent.accept();
        return ListeningStatus.LISTENING;
    }
}
