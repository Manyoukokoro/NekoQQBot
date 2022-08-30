package org.nekotori.events;

import net.mamoe.mirai.event.EventHandler;
import net.mamoe.mirai.event.EventPriority;
import net.mamoe.mirai.event.ListeningStatus;
import net.mamoe.mirai.event.SimpleListenerHost;
import net.mamoe.mirai.event.events.BotInvitedJoinGroupRequestEvent;
import org.jetbrains.annotations.NotNull;

public class GroupInviteEvents extends SimpleListenerHost {

    @NotNull
    @EventHandler(priority = EventPriority.HIGH)
    public ListeningStatus onMessage(@NotNull BotInvitedJoinGroupRequestEvent botInvitedJoinGroupRequestEvent) {
        botInvitedJoinGroupRequestEvent.accept();
        return ListeningStatus.LISTENING;
    }
}
