package org.nekotori.events;

import net.mamoe.mirai.event.EventHandler;
import net.mamoe.mirai.event.EventPriority;
import net.mamoe.mirai.event.ListeningStatus;
import net.mamoe.mirai.event.SimpleListenerHost;
import org.jetbrains.annotations.NotNull;
import org.nekotori.annotations.Event;

/**
 * @author: JayDeng
 * @date: 04/08/2021 15:05
 * @description:
 * @version: {@link }
 */

@Event
public class GroupMessageEvents extends SimpleListenerHost {

    @EventHandler(priority = EventPriority.NORMAL)
    public ListeningStatus onMessage(@NotNull GroupMessageEvents groupMessageEvents){
        return null;
    }
}
    