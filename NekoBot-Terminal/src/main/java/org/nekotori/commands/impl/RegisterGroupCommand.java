package org.nekotori.commands.impl;

import lombok.extern.slf4j.Slf4j;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.Member;
import net.mamoe.mirai.message.data.At;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import net.mamoe.mirai.message.data.PlainText;
import org.nekotori.annotations.IsCommand;
import org.nekotori.commands.ManagerGroupCommand;
import org.nekotori.service.GroupService;

import javax.annotation.Resource;

@IsCommand(name = {"注册", "注册本群", "reg"}, description = "将本群注册进bot，格式:(!/-/#)reg")
@Slf4j
public class RegisterGroupCommand extends ManagerGroupCommand {

    @Resource
    private GroupService groupService;

    @Override
    public MessageChain execute(Member sender, MessageChain messageChain, Group subject) {
        if (groupService.IsGroupRegistered(subject)) {
            return new MessageChainBuilder().append(new At(sender.getId())).append(new PlainText("\n本群已经注册")).build();
        }
        try {
            groupService.registerGroup(subject);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return new MessageChainBuilder().append(new At(sender.getId())).append(new PlainText("\n注册失败，请前往艾泽拉斯联系管理员")).build();
        }
        return new MessageChainBuilder().append(new At(sender.getId())).append(new PlainText("\n注册成功")).build();
    }
}
