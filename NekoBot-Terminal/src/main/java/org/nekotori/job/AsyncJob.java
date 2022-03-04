package org.nekotori.job;

import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.message.code.MiraiCode;
import org.nekotori.chain.ChainMessageSelector;
import org.nekotori.dao.ChatMemberMapper;
import org.nekotori.entity.CustomResponse;
import org.nekotori.handler.CustomCommandHandler;
import org.nekotori.handler.GlobalAtMeHandler;
import org.nekotori.handler.GlobalCommandHandler;
import org.nekotori.service.GroupService;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.regex.Pattern;

@Component
public class AsyncJob {

    public static Map<Long, List<CustomResponse>> localCache;

    @Resource
    private GlobalCommandHandler globalCommandHandler;

    @Resource
    private CustomCommandHandler customCommandHandler;

    @Resource
    private GlobalAtMeHandler globalAtMeHandler;

    @Resource
    private GroupService groupService;

    @Resource
    private ChatMemberMapper chatMemberMapper;

    @Resource
    private ChainMessageSelector chainMessageSelector;


    @Bean(name = "groupCusRes")
    public Map<Long, List<CustomResponse>> getGroupCustomResponse(){
        localCache = groupService.getGroupCustomResponses();
        return localCache;
    }

    @Async
    public void handleCustomResponse(GroupMessageEvent groupMessageEvent){
        List<CustomResponse> customResponses = localCache.get(groupMessageEvent.getGroup().getId());
        String message = groupMessageEvent.getMessage().contentToString();
        List<String> probResponses = new ArrayList<>();
        if(CollectionUtils.isEmpty(customResponses )){
            return;
        }
        customResponses.stream().filter(customResponse -> {
            CustomResponse.WAY way = customResponse.getWay();
            switch (way){
                case BEGIN: return message.startsWith(customResponse.getKeyWord());
                case CONTAINS: return message.contains(customResponse.getKeyWord());
                case END: return message.endsWith(customResponse.getKeyWord());
                case REGEX: return Pattern.compile(customResponse.getKeyWord()).matcher(message).matches();
                case FULL_CONTEXT: return message.equals(customResponse.getKeyWord());
                default: return false;
            }
        }).forEach(v->
                probResponses.add(v.getResponse())
        );
        if(probResponses.size()>0){
            int i = new Random().nextInt(probResponses.size());
            groupMessageEvent.getSubject().sendMessage(MiraiCode.deserializeMiraiCode(probResponses.get(i)));
        }
    }

    public void repeat(GroupMessageEvent groupMessageEvent){
        int randomInt = new Random().nextInt(100);
        if(randomInt<5){
            groupMessageEvent.getSubject().sendMessage(groupMessageEvent.getMessage());
        }

    }

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

//    @Async
//    public void everyDayWelcome(GroupMessageEvent groupMessageEvent){
//        Group group = groupMessageEvent.getGroup();
//        Member sender = groupMessageEvent.getSender();
//        ChatMemberDo chatMemberDo = chatMemberMapper.selectOne(new QueryWrapper<ChatMemberDo>().eq("group_id", group.getId()).eq("member_id", sender.getId()));
//        if(chatMemberDo==null ||!chatMemberDo.getTodayWelcome())
//        {
//            sender.nudge().sendTo(group);
//            group.sendMessage(new MessageChainBuilder().append(new At(sender.getId())).append(new PlainText(" Hi,新的一天看到你真开心")).build());
//        }
//        if(chatMemberDo==null){
//            chatMemberDo = ChatMemberDo.builder()
//                    .memberId(sender.getId())
//                    .groupId(group.getId())
//                    .isBlocked(false)
//                    .level(0)
//                    .nickName(sender.getNameCard())
//                    .todaySign(false)
//                    .todayWelcome(true)
//                    .totalSign(0)
//                    .exp(0L)
//                    .build();
//        }
//        chatMemberDo.setTodayWelcome(true);
//        chatMemberMapper.updateById(chatMemberDo);
//    }
}
