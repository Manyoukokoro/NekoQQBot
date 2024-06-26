package org.nekotori.commands.impl;

import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.Member;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import net.mamoe.mirai.message.data.PlainText;
import org.nekotori.annotations.IsCommand;
import org.nekotori.commands.ManagerGroupCommand;
import org.nekotori.entity.CommandAttr;
import org.nekotori.service.GroupService;
import org.nekotori.utils.CommandUtils;

import javax.annotation.Resource;
import java.util.List;


@IsCommand(name = {"取消指令", "取消"}, description = "取消指令注册\n格式:\n    (!/-/#)取消 ...[指令名]")
public class UnRegisterComCommand extends ManagerGroupCommand {
    @Resource
    GroupService groupService;

    @Override
    public MessageChain execute(Member sender, Group subject, CommandAttr commandAttr, MessageChain messageChain) {
        String s = messageChain.serializeToMiraiCode();
        List<String> param = commandAttr.getParam();
        for (String p : param) {
            String groupCommands = groupService.getGroupCommands(subject.getId());
            String newCommand = CommandUtils.removeCommand(groupCommands, p);
            groupService.updateGroupCommand(subject.getId(), newCommand);
        }
        return new MessageChainBuilder().append(new PlainText("更新群指令成功")).build();
    }
}
