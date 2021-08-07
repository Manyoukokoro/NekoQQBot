package org.nekotori.events;

import net.mamoe.mirai.event.EventHandler;
import net.mamoe.mirai.event.ListeningStatus;
import net.mamoe.mirai.event.SimpleListenerHost;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import org.jetbrains.annotations.NotNull;
import org.nekotori.commands.GlobalCommandHandler;
import org.nekotori.entity.ChatHistoryDo;
import org.nekotori.service.GroupService;
import org.springframework.scheduling.annotation.Async;

/**
 * @author: JayDeng
 * @date: 02/08/2021 14:08
 * @description:
 * @version: {@link }
 */

public class GroupCommandEvents extends SimpleListenerHost {

  private GlobalCommandHandler globalCommandHandler;

  private GroupService groupService;

  public GroupCommandEvents(GlobalCommandHandler globalCommandHandler,GroupService groupService){
    this.globalCommandHandler = globalCommandHandler;
    this.groupService = groupService;
  }





  @NotNull
  @EventHandler
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
    groupService.saveHistory(groupMessageEvent);
  }
}
