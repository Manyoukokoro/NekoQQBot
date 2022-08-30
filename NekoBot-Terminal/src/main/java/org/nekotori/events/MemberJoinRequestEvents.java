package org.nekotori.events;

import net.mamoe.mirai.contact.Stranger;
import net.mamoe.mirai.event.EventHandler;
import net.mamoe.mirai.event.EventPriority;
import net.mamoe.mirai.event.ListeningStatus;
import net.mamoe.mirai.event.SimpleListenerHost;
import net.mamoe.mirai.event.events.MemberJoinRequestEvent;
import org.jetbrains.annotations.NotNull;
import org.nekotori.BotSimulator;

public class MemberJoinRequestEvents extends SimpleListenerHost {

    @NotNull
    @EventHandler(priority = EventPriority.HIGH)
    public ListeningStatus onMessage(@NotNull MemberJoinRequestEvent event) {
        long fromId = event.getFromId();
        String message = event.getMessage();
        Stranger stranger = BotSimulator.getBot().getStrangerOrFail(fromId);
        int qLevel = stranger.queryProfile().getQLevel();
        //event.accept();
        return ListeningStatus.LISTENING;
    }

}
