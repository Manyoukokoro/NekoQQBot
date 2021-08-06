package org.nekotori.events;

import net.mamoe.mirai.event.EventHandler;
import net.mamoe.mirai.event.EventPriority;
import net.mamoe.mirai.event.ListeningStatus;
import net.mamoe.mirai.event.SimpleListenerHost;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import org.jetbrains.annotations.NotNull;
import org.nekotori.annotations.Event;
import org.nekotori.commands.GlobalCommandHandler;
import org.nekotori.service.GroupService;
import org.springframework.scheduling.annotation.Async;

import javax.annotation.Resource;

/**
 * @author: JayDeng
 * @date: 02/08/2021 14:08
 * @description:
 * @version: {@link }
 */

@Event
public class GroupCommandEvents extends SimpleListenerHost {

  @Resource
  private GlobalCommandHandler globalCommandHandler;

  @Resource
  private GroupService groupService;

  @EventHandler(priority = EventPriority.NORMAL)
  public ListeningStatus onMessage(@NotNull GroupMessageEvent groupMessageEvent) {
    handleCommand(groupMessageEvent);
    doRecord(groupMessageEvent);
    return ListeningStatus.LISTENING;
  }

  @Async
  protected void handleCommand(GroupMessageEvent groupMessageEvent){
    globalCommandHandler.handle(groupMessageEvent);
  }

  @Async
  protected void doRecord(GroupMessageEvent groupMessageEvent){

  }
}
