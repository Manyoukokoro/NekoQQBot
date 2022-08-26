package org.nekotori.commands.impl;

import cn.hutool.core.util.NumberUtil;
import net.mamoe.mirai.contact.ContactList;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.Member;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import net.mamoe.mirai.message.data.PlainText;
import net.mamoe.mirai.message.data.SingleMessage;
import org.nekotori.annotations.IsCommand;
import org.nekotori.commands.NoAuthGroupCommand;
import org.nekotori.entity.CommandAttr;
import org.nekotori.utils.CommandUtils;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;


@IsCommand(name = { "k", "跨", "聊天列表"}, description = "跨群发送消息")
public class CrossGroupMessageCommand extends NoAuthGroupCommand {
    @Override
    public MessageChain execute(Member sender, MessageChain messageChain, Group subject) {
        CommandAttr commandAttr = CommandUtils.resolveCommand(messageChain);
        if ("聊天列表".equals(commandAttr.getCommand())) {

            List<String> collect = subject.getBot()
                    .getGroups()
                    .stream()
                    .map(Group::getId)
                    .map(String::valueOf)
                    .map(s -> {
                        if (s.equals(String.valueOf(subject.getId()))) {
                            return "" + s.charAt(0) + s.charAt(1) + s.charAt(3) + s.charAt(5) + "（本群）";
                        }
                        return "" + s.charAt(0) + s.charAt(1) + s.charAt(3) + s.charAt(5);
                    }).collect(Collectors.toList());
            String join = String.join("\n", collect);
            return new MessageChainBuilder().append(new PlainText("可发送消息的对象\n" + join)).build();
        }

        if (CollectionUtils.isEmpty(commandAttr.getParam()) && CollectionUtils.isEmpty(commandAttr.getExtMessage())) {
            return new MessageChainBuilder().append(new PlainText("... ... ...")).build();
        }
        String s = String.valueOf(subject.getId());
        String tag = "" + s.charAt(0) + s.charAt(1) + s.charAt(3) + s.charAt(5);
        int target = -1;
        Group targetgroup = null;
        if (commandAttr.getParam().size() > 0) {
            if (NumberUtil.isInteger(commandAttr.getParam().get(0))) {
                target = Integer.parseInt(commandAttr.getParam().get(0));
            }
        }

        if (target == -1) {
            ContactList<Group> groups = subject.getBot().getGroups();
            List<Group> collect = new ArrayList<>(groups);
            do {
                int i = new Random().nextInt(groups.size());
                targetgroup = collect.get(i);
            } while (subject.getId() == targetgroup.getId());
        } else {
            ContactList<Group> groups = subject.getBot().getGroups();
            for (Group group : groups) {
                String ss = String.valueOf(group.getId());
                String sstag = "" + ss.charAt(0) + ss.charAt(1) + ss.charAt(3) + ss.charAt(5);
                if (sstag.equals(String.valueOf(target))) {
                    targetgroup = group;
                }
            }
        }
        String join = "";
        if (target == -1 && !CollectionUtils.isEmpty(commandAttr.getParam())) {
            join = String.join(" ", commandAttr.getParam());
        } else if (!CollectionUtils.isEmpty(commandAttr.getParam())) {
            join = String.join(" ", commandAttr.getParam().subList(1, commandAttr.getParam().size()));
        }

        MessageChainBuilder response = new MessageChainBuilder();
        response.append(new PlainText(tag +"=>User:" +sender.getId()%10000 + ":\n"));
        response.append(new PlainText(join));
        if (!CollectionUtils.isEmpty(commandAttr.getExtMessage())) {
            for (SingleMessage singleMessage : commandAttr.getExtMessage()) {
                response.append(singleMessage);
            }
        }
        if (targetgroup == null) {
            return new MessageChainBuilder().append(new PlainText("找不到目标")).build();
        }
        Objects.requireNonNull(targetgroup).sendMessage(response.build());
        String ss = String.valueOf(targetgroup.getId());
        String sstag = "" + ss.charAt(0) + ss.charAt(1) + ss.charAt(3) + ss.charAt(5);
        return new MessageChainBuilder().append(new PlainText("消息发送至" + sstag + "成功")).build();

    }
}
