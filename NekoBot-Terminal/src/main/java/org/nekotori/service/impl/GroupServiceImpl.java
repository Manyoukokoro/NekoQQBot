package org.nekotori.service.impl;

import cn.hutool.core.lang.TypeReference;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import org.nekotori.dao.ChatGroupMapper;
import org.nekotori.dao.ChatHistoryMapper;
import org.nekotori.entity.ChatGroupDo;
import org.nekotori.entity.ChatHistoryDo;
import org.nekotori.entity.CustomResponse;
import org.nekotori.service.GroupService;
import org.nekotori.utils.CommandUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

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
    public boolean checkPrivilege(Long groupId, String command) {
        ChatGroupDo chatGroupDo = chatGroupMapper.selectOne(new QueryWrapper<ChatGroupDo>().eq("group_id", groupId));
        if (ObjectUtils.isEmpty(chatGroupDo)) return false;
        String commands = chatGroupDo.getCommands();
        return CommandUtils.IsCommandRegistered(commands, command);
    }

    @Override
    public void saveHistory(GroupMessageEvent groupMessageEvent) {
        List<ChatGroupDo> chatGroupDos = chatGroupMapper.selectList(Wrappers.<ChatGroupDo>lambdaQuery().eq(ChatGroupDo::getGroupId, groupMessageEvent.getGroup().getId()));
        if (CollectionUtils.isEmpty(chatGroupDos)) {
            registerGroup(groupMessageEvent.getGroup());
        }
        ChatHistoryDo build = ChatHistoryDo.builder()
                .groupId(groupMessageEvent.getGroup().getId())
                .senderId(groupMessageEvent.getSender().getId())
                .time(new Date())
                .content(groupMessageEvent.getMessage().serializeToMiraiCode())
                .isCommand(CommandUtils.isCommand(groupMessageEvent))
                .build();
        chatHistoryMapper.insert(build);
    }

    public boolean IsGroupRegistered(Group group) {
        long id = group.getId();
        List<Long> longs = chatGroupMapper.selectList(new QueryWrapper<>()).stream().map(ChatGroupDo::getGroupId).collect(Collectors.toList());
        if (longs.contains(id)) {
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
                .commands("签到")
                .build();
        return chatGroupMapper.insert(build);
    }

    @Override
    public void updateGroupCommand(Long groupId, String command) {
        ChatGroupDo chatGroupDo = chatGroupMapper.selectOne(new QueryWrapper<ChatGroupDo>().eq("group_id", groupId));
        chatGroupDo.setCommands(command);
        chatGroupMapper.updateById(chatGroupDo);
    }

    @Override
    public String getGroupCommands(Long groupId) {
        ChatGroupDo chatGroupDo = chatGroupMapper.selectOne(new QueryWrapper<ChatGroupDo>().eq("group_id", groupId));
        return Optional.ofNullable(chatGroupDo).orElse(new ChatGroupDo()).getCommands();
    }

    @Override
    public Map<Long, List<CustomResponse>> getGroupCustomResponses() {
        List<ChatGroupDo> chatGroupDo = chatGroupMapper.selectList(new QueryWrapper<>());
        return chatGroupDo.stream().collect(Collectors.toMap(
                ChatGroupDo::getGroupId, v -> v.getCustomResponse() == null ? new ArrayList<>() : JSONUtil.toBean(v.getCustomResponse(),
                        new TypeReference<>() {
                        }, true)
        ));
    }
}
    