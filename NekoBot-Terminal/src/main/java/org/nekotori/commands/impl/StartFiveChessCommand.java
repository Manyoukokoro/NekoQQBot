package org.nekotori.commands.impl;

import cn.hutool.core.collection.CollectionUtil;
import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.Member;
import net.mamoe.mirai.message.MessageReceipt;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import org.nekotori.annotations.HandlerId;
import org.nekotori.annotations.IsCommand;
import org.nekotori.chain.ChainMessageSelector;
import org.nekotori.chain.channel.GroupCommandChannel;
import org.nekotori.chain.channel.handler.impl.FiveChessHandler;
import org.nekotori.commands.NoAuthGroupCommand;
import org.nekotori.commands.PrivilegeGroupCommand;
import org.nekotori.common.InnerConstants;
import org.nekotori.entity.CommandAttr;
import org.nekotori.utils.CommandUtils;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import static org.nekotori.common.InnerConstants.*;

/**
 * @author: JayDeng
 * @date: 2022/3/8 下午3:42
 * @description: StartFiveChessCommand
 * @version: {@link }
 */

@IsCommand(name = {"五子棋"}, description = "")
public class StartFiveChessCommand extends PrivilegeGroupCommand {
    @Resource
    private ChainMessageSelector chainMessageSelector;

    @Resource
    private FiveChessHandler fiveChessHandler;


    @Override
    public MessageChain execute(Member sender, MessageChain messageChain, Group subject) {
        Map<String, GroupCommandChannel> channels = chainMessageSelector.getChannels();
        GroupCommandChannel groupCommandChannel =
                channels.get(subject.getId() + "@" + FiveChessHandler.class.getAnnotation(HandlerId.class).value());
        if (ObjectUtils.isEmpty(groupCommandChannel)) {
            chainMessageSelector.registerChannel(subject.getId(), fiveChessHandler);
            chainMessageSelector.joinChannel(subject.getId(), fiveChessHandler, sender.getId());
            Set<Integer> fs = new HashSet<>();
            String s = messageChain.contentToString();
            CommandAttr commandAttr = CommandUtils.resolveTextCommand(s);
            if (CollectionUtil.isNotEmpty(commandAttr.getParam())) {
                commandAttr.getParam().forEach(ss -> {
                    if (ss.equals("T")) {
                        fs.add(0);
                    }
                    if (ss.equals("F")) {
                        fs.add(1);
                    }
                    if (ss.equals("L")) {
                        fs.add(2);
                    }
                });
            }
            FiveChessHandler.init(sender.getId(), subject.getId(), fs);
            subject.sendMessage(new MessageChainBuilder().append(sender.getNameCard()).append("创建对局成功!执黑棋先行，").append(
                    STRING0).build());
            StringBuilder stringBuilder = new StringBuilder();
            if (fs.isEmpty()) {
                stringBuilder.append(STRING1);
            } else {
                stringBuilder.append(STRING2);
                fs.forEach(ss -> {
                    if (ss.equals(0)) {
                        stringBuilder.append(STRING3);
                    }
                    if (ss.equals(1)) {
                        stringBuilder.append(STRING4);
                    }
                    if (ss.equals(2)) {
                        stringBuilder.append(InnerConstants.STRING5);
                    }
                });
            }
            subject.sendMessage(new MessageChainBuilder().append(stringBuilder.toString()).build());
            MessageReceipt<Group> groupMessageReceipt = subject.sendMessage(new MessageChainBuilder().append(Contact.uploadImage(subject,
                    Objects.requireNonNull(FiveChessHandler.drawMap(subject.getId(), 0, 0)))).build());
            FiveChessHandler.putImage(subject.getId(), groupMessageReceipt);
        } else {
            if (FiveChessHandler.isFull(subject.getId())) {
                return new MessageChainBuilder().append(STRING6).build();
            }
            FiveChessHandler.join(sender.getId(), subject.getId());
            chainMessageSelector.joinChannel(subject.getId(), fiveChessHandler, sender.getId());
            subject.sendMessage(new MessageChainBuilder().append(STRING7).build());
        }
        return null;
    }
}
