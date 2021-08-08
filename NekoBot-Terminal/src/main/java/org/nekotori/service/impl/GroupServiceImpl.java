package org.nekotori.service.impl;

import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.message.code.MiraiCode;
import org.nekotori.dao.ChatGroupMapper;
import org.nekotori.dao.ChatHistoryMapper;
import org.nekotori.entity.ChatGroupDo;
import org.nekotori.entity.ChatHistoryDo;
import org.nekotori.service.GroupService;
import org.nekotori.utils.CommandUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * @author: JayDeng
 * @date: 03/08/2021 10:04
 * @description:
 * @version: {@link }
 */
@Service
public class GroupServiceImpl implements GroupService {

    @Resource
    private ChatHistoryMapper chatHistoryMapper;

    @Resource
    private ChatGroupMapper chatGroupMapper;

    @Override
    public boolean checkPrivilege(Long groupId,String command) {
        ChatGroupDo chatGroupDo = chatGroupMapper.selectGroupById(groupId);
        if(ObjectUtils.isEmpty(chatGroupDo)) return false;
        String commands = chatGroupDo.getCommands();
        return CommandUtils.IsCommandRegistered(commands,command);
    }

    @Override
    public void saveHistory(GroupMessageEvent groupMessageEvent) {
        ChatHistoryDo build = ChatHistoryDo.builder()
                .groupId(groupMessageEvent.getGroup().getId())
                .senderId(groupMessageEvent.getSender().getId())
                .time(new Date())
                .content(groupMessageEvent.getMessage().serializeToMiraiCode())
                .isCommand(CommandUtils.isCommand(groupMessageEvent))
                .build();
        chatHistoryMapper.insertChatHistory(build);
    }

    public boolean IsGroupRegistered(Group group){
        long id = group.getId();
        List<Long> longs = chatGroupMapper.selectRegisteredGroup();
        if(longs.contains(id)){
            return true;
        }
        return false;
    }

    @Override
    public int registerGroup(Group group) {
        ChatGroupDo build = ChatGroupDo.builder()
                .groupId(group.getId())
                .groupName(group.getName())
                .groupLevel(0)
                .isBlock(false)
                .commands("")
                .build();
        return chatGroupMapper.insertChatGroup(build);
    }

    @Override
    public void updateGroupCommand(Long groupId, String command) {
        ChatGroupDo chatGroupDo = chatGroupMapper.selectGroupById(groupId);
        chatGroupDo.setCommands(command);
        chatGroupMapper.updateChatGroup(chatGroupDo);
    }

    @Override
    public String getGroupCommands(Long groupId) {
        ChatGroupDo chatGroupDo = chatGroupMapper.selectGroupById(groupId);
        return Optional.ofNullable(chatGroupDo).orElse(new ChatGroupDo()).getCommands();
    }
}
    