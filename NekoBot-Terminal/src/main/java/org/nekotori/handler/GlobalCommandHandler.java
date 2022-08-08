package org.nekotori.handler;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.message.data.MessageChain;
import org.nekotori.commands.Command;
import org.nekotori.dao.ChatMemberMapper;
import org.nekotori.entity.ChatMemberDo;
import org.nekotori.utils.CommandUtils;
import org.nekotori.utils.SpringContextUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * @author: JayDeng
 * @date: 02/08/2021 14:58
 * @description:
 * @version: {@link }
 */

@Component
@Slf4j
public class GlobalCommandHandler {

    private static Map<String, Command> innerCommands = new HashMap<>();

    @Resource
    private ChatMemberMapper chatMemberMapper;

    public static void init() {
        innerCommands = SpringContextUtils.getContext().getBeansOfType(Command.class);
        log.info("注册了以下指令:{}",
                innerCommands.keySet());
    }

    public void handle(GroupMessageEvent groupMessageEvent) {
        if (!CommandUtils.isCommand(groupMessageEvent)) {
            return;
        }
        for (Command command : innerCommands.values()) {
            if (command.checkAuthorization(groupMessageEvent) && CommandUtils.checkCommand(command, groupMessageEvent)) {
                ThreadSingleton.run(
                        () -> {
                            ChatMemberDo chatMemberDo = chatMemberMapper.selectOne(new QueryWrapper<ChatMemberDo>().eq("group_id",
                                    groupMessageEvent.getGroup().getId()).eq(
                                    "member_id", groupMessageEvent.getSender().getId()));
                            if (ObjectUtils.isEmpty(chatMemberDo)) {
                                chatMemberDo = ChatMemberDo.builder()
                                        .memberId(groupMessageEvent.getSender().getId())
                                        .groupId(groupMessageEvent.getGroup().getId())
                                        .isBlocked(false)
                                        .level(0)
                                        .nickName(groupMessageEvent.getSender().getNick())
                                        .todayWelcome(false)
                                        .totalSign(0)
                                        .exp(0L)
                                        .build();
                                final int id = chatMemberMapper.insert(chatMemberDo);
                                chatMemberDo.setId(id);
                            }
                            chatMemberDo.setLastCommand(groupMessageEvent.getMessage().serializeToMiraiCode());
                            chatMemberMapper.updateById(chatMemberDo);

                            MessageChain execute = command.execute(
                                    groupMessageEvent.getSender(),
                                    groupMessageEvent.getMessage(),
                                    groupMessageEvent.getGroup());
                            if (!ObjectUtils.isEmpty(execute)) {
                                groupMessageEvent
                                        .getGroup()
                                        .sendMessage(execute);
                            }
                        });
            }
        }
    }

}
