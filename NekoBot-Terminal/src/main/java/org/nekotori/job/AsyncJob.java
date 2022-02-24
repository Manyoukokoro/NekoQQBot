package org.nekotori.job;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.Member;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.message.data.At;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import net.mamoe.mirai.message.data.PlainText;
import org.nekotori.chain.ChainMessageSelector;
import org.nekotori.dao.ChatMemberMapper;
import org.nekotori.entity.ChatMemberDo;
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

    @Resource
    private ChatMemberMapper chatMemberMapper;

    @Resource
    private ChainMessageSelector chainMessageSelector;


    @Async
    public void handleCommand(GroupMessageEvent groupMessageEvent){
        globalCommandHandler.handle(groupMessageEvent);
    }

    @Async
    public void handleAtMe(GroupMessageEvent groupMessageEvent){
        globalAtMeHandler.handle(groupMessageEvent);
    }

    public void messageSelect(GroupMessageEvent groupMessageEvent){
        chainMessageSelector.selectMessage(groupMessageEvent);
    }

    @Async
    public void doRecord(GroupMessageEvent groupMessageEvent){
        groupService.saveHistory(groupMessageEvent);
    }

    @Async
    public void everyDayWelcome(GroupMessageEvent groupMessageEvent){
        Group group = groupMessageEvent.getGroup();
        Member sender = groupMessageEvent.getSender();
        ChatMemberDo chatMemberDo = chatMemberMapper.selectOne(new QueryWrapper<ChatMemberDo>().eq("group_id", group.getId()).eq("member_id", sender.getId()));
        if(chatMemberDo==null ||!chatMemberDo.getTodayWelcome())
        {
            sender.nudge().sendTo(group);
            group.sendMessage(new MessageChainBuilder().append(new At(sender.getId())).append(new PlainText(" Hi,新的一天看到你真开心")).build());
        }
        if(chatMemberDo==null){
            chatMemberDo = ChatMemberDo.builder()
                    .memberId(sender.getId())
                    .groupId(group.getId())
                    .isBlocked(false)
                    .level(0)
                    .nickName(sender.getNameCard())
                    .todaySign(false)
                    .todayWelcome(true)
                    .totalSign(0)
                    .exp(0L)
                    .build();
        }
        chatMemberDo.setTodayWelcome(true);
        chatMemberMapper.updateById(chatMemberDo);
    }
}
