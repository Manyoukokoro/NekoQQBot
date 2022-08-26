package org.nekotori.commands.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.Member;
import net.mamoe.mirai.message.data.At;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import org.nekotori.annotations.IsCommand;
import org.nekotori.commands.NoAuthGroupCommand;
import org.nekotori.dao.ChatHistoryMapper;
import org.nekotori.entity.ChatHistoryDo;
import org.nekotori.entity.CommandAttr;
import org.nekotori.utils.CommandUtils;

import javax.annotation.Resource;
import java.util.List;


@IsCommand(name = {"浓度"},description = "用户聊天话题浓度查询")
public class ChatPotencyCommand extends NoAuthGroupCommand {

    @Resource
    private ChatHistoryMapper mapper;

    @Override
    public MessageChain execute(Member sender, MessageChain messageChain, Group subject) {
        CommandAttr commandAttr = CommandUtils.resolveCommand(messageChain);
        List<String> param = commandAttr.getParam();
        subject.sendMessage("话题浓度查询中......");
        List<ChatHistoryDo> chatHistoryDos = mapper.selectList(Wrappers.<ChatHistoryDo>lambdaQuery().eq(ChatHistoryDo::getGroupId, subject.getId()));
        int size = chatHistoryDos.size();
        long count = chatHistoryDos.stream().filter(o -> {
            for (String p : param) {
                if (o.getContent().contains(p)) {
                    return true;
                }
            }
            return false;
        }).count();
        return new MessageChainBuilder().append(new At(sender.getId()))
                .append(" ")
                .append("本群关于关键词")
                .append(String.join("/", param))
                .append("的话题浓度为:")
                .append(String.valueOf(count))
                .append("/")
                .append(String.valueOf(size))
                .build();
    }
}
