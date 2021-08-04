package org.nekotori.events;

import net.mamoe.mirai.event.EventHandler;
import net.mamoe.mirai.event.EventPriority;
import net.mamoe.mirai.event.ListeningStatus;
import net.mamoe.mirai.event.SimpleListenerHost;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import org.jetbrains.annotations.NotNull;
import org.nekotori.annotations.Event;
import org.nekotori.commands.GlobalCommandHandler;
import org.springframework.stereotype.Component;

/**
 * @author: JayDeng
 * @date: 02/08/2021 14:08
 * @description:
 * @version: {@link }
 */

@Event
public class GroupCommandEvents extends SimpleListenerHost {

  private GlobalCommandHandler globalCommandHandler = new GlobalCommandHandler();

  @EventHandler(priority = EventPriority.NORMAL)
  public ListeningStatus onMessage(@NotNull GroupMessageEvent groupMessageEvent) {
    globalCommandHandler.init();
    globalCommandHandler.handle(groupMessageEvent);
    return ListeningStatus.LISTENING;
  }
}
