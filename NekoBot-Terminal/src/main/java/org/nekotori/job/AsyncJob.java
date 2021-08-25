package org.nekotori.job;

import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.Member;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import org.nekotori.handler.GlobalAtMeHandler;
import org.nekotori.handler.GlobalCommandHandler;
import org.nekotori.service.GroupService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class AsyncJob {

    @Resource
    private GlobalCommandHandler globalCommandHandler;

    @Resource
    private GlobalAtMeHandler globalAtMeHandler;

    @Resource
    private GroupService groupService;


    @Async
    public void handleCommand(GroupMessageEvent groupMessageEvent){
        globalCommandHandler.handle(groupMessageEvent);
    }

    @Async
    public void handleAtMe(GroupMessageEvent groupMessageEvent){
        globalAtMeHandler.handle(groupMessageEvent);
    }

    @Async
    public void doRecord(GroupMessageEvent groupMessageEvent){
        groupService.saveHistory(groupMessageEvent);
    }

    @Async
    public void everyDayWelcome(GroupMessageEvent groupMessageEvent){
        Group group = groupMessageEvent.getGroup();
        Member sender = groupMessageEvent.getSender();
//    sender.nudge().sendTo(group);
    }
}
