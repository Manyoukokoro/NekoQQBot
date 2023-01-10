package org.nekotori.commands.impl;

import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.Member;
import net.mamoe.mirai.message.data.At;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import net.mamoe.mirai.message.data.QuoteReply;
import org.nekotori.annotations.IsCommand;
import org.nekotori.commands.Command;
import org.nekotori.commands.ManagerGroupCommand;
import org.nekotori.commands.NoAuthGroupCommand;
import org.nekotori.entity.CommandAttr;
import org.nekotori.service.GroupService;
import org.nekotori.utils.CommandUtils;
import org.nekotori.utils.SpringContextUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@IsCommand(name = {"help"}, description = "显示帮助文档\n格式:\n    (!/-/#)help [指令名称]")
public class HelpCommand extends NoAuthGroupCommand {

    @Resource
    private GroupService groupService;

    @Override
    public MessageChain execute(Member sender, Group subject, CommandAttr commandAttr, MessageChain messageChain) {
        if(!CollectionUtils.isEmpty(commandAttr.getParam())){
            String singleHelpDoc = getSingleHelpDoc(commandAttr.getParam().get(0));
            return StringUtils.hasLength(singleHelpDoc)?new MessageChainBuilder().append(new QuoteReply(messageChain)).append(singleHelpDoc).build():null;
        }
        String groupCommands = groupService.getGroupCommands(subject.getId());
        List<String> strings = CommandUtils.resolveRegisteredCommand(groupCommands);
        String s = buildHelpDoc(strings);
        return new MessageChainBuilder().append(new QuoteReply(messageChain)).append(s).build();
    }

    private String getSingleHelpDoc(String name){
        Map<String, Command> beansOfType = SpringContextUtils.getContext().getBeansOfType(org.nekotori.commands.Command.class);
        List<Command> collect = new ArrayList<>(beansOfType.values());
        for (Command command : collect) {
            IsCommand annotation = command.getClass().getAnnotation(IsCommand.class);
            if(List.of(annotation.name()).contains(name)){
                return annotation.description();
            }
        }
        return null;
    }

    private String buildHelpDoc(List<String> commands) {
        Map<String, Command> beansOfType = SpringContextUtils.getContext().getBeansOfType(org.nekotori.commands.Command.class);
        List<Command> collect = new ArrayList<>(beansOfType.values());
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("\n本群已经注册指令(注意:多关键词指令可能只注册了其中部分): ");
        for (Command command : collect) {
            IsCommand annotation = command.getClass().getAnnotation(IsCommand.class);
            String[] name = annotation.name();
            String flag = "未授权";
            if (command instanceof NoAuthGroupCommand) {
                flag = "任意";
            }
            if (command instanceof ManagerGroupCommand) {
                flag = "管理员";
            }
            for (String n : name) {
                if (commands.contains(n)) {
                    flag = "已授权";
                    break;
                }
            }
            stringBuilder.append("\n")
                    .append("[")
                    .append(flag)
                    .append("]")
                    .append(Arrays.toString(annotation.name()));
        }
        stringBuilder.append("\n更多详细信息请浏览：https://github.com/Manyoukokoro/NekoQQBot/blob/master/README.md");
        return stringBuilder.toString();
    }
}
