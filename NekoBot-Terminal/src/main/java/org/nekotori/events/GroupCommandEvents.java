package org.nekotori.events;

import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.Member;
import net.mamoe.mirai.event.EventHandler;
import net.mamoe.mirai.event.EventPriority;
import net.mamoe.mirai.event.ListeningStatus;
import net.mamoe.mirai.event.SimpleListenerHost;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import org.jetbrains.annotations.NotNull;
import org.nekotori.annotations.Event;
import org.nekotori.commands.GlobalCommandHandler;
import org.nekotori.job.AsyncJob;
import org.nekotori.service.GroupService;
import org.springframework.context.annotation.Lazy;
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
  @Lazy
  private AsyncJob asyncJob;


  @NotNull
  @EventHandler(priority = EventPriority.HIGH)
  public ListeningStatus onMessage(@NotNull GroupMessageEvent groupMessageEvent) {
    asyncJob.handleCommand(groupMessageEvent);
    asyncJob.doRecord(groupMessageEvent);
//    asyncJob.everyDayWelcome(groupMessageEvent);
    return ListeningStatus.LISTENING;
  }


}
