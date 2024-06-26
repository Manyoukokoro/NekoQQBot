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

@IsCommand(name = {"指令"}, description = "注册指令，管理员可以采用此命令注册需要授权的指令\n格式:\n    (!/-/#)指令 ...[指令名]")
public class RegisterComCommand extends ManagerGroupCommand {

    @Resource
    GroupService groupService;

    @Override
    public MessageChain execute(Member sender, Group subject, CommandAttr commandAttr, MessageChain messageChain) {
        String s = messageChain.serializeToMiraiCode();
        List<String> param = commandAttr.getParam();
        for (String p : param) {
            String groupCommands = groupService.getGroupCommands(subject.getId());
            String newCommand = CommandUtils.addCommand(groupCommands, p);
            groupService.updateGroupCommand(subject.getId(), newCommand);
        }
        return new MessageChainBuilder().append(new PlainText("更新群指令成功")).build();
    }
}
