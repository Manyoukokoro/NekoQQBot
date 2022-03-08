package org.nekotori.commands.impl;

import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.Member;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import org.nekotori.annotations.HandlerId;
import org.nekotori.annotations.IsCommand;
import org.nekotori.chain.ChainMessageSelector;
import org.nekotori.chain.channel.GroupCommandChannel;
import org.nekotori.chain.channel.handler.impl.FiveChessHandler;
import org.nekotori.commands.NoAuthGroupCommand;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.Map;

/**
 * @author: JayDeng
 * @date: 2022/3/8 下午3:42
 * @description: StartFiveChessCommand
 * @version: {@link }
 */

@IsCommand(name = {"五子棋"},description = "")
public class StartFiveChessCommand extends NoAuthGroupCommand {

    @Resource
    private ChainMessageSelector chainMessageSelector;

    @Resource
    private FiveChessHandler fiveChessHandler;


    @Override
    public MessageChain execute(Member sender, MessageChain messageChain, Group subject) {
        Map<String, GroupCommandChannel> channels = chainMessageSelector.getChannels();
        GroupCommandChannel groupCommandChannel =
                channels.get(subject.getId() + "@" + FiveChessHandler.class.getAnnotation(HandlerId.class).value());
        if(ObjectUtils.isEmpty(groupCommandChannel)) {
            chainMessageSelector.registerChannel(subject.getId(),fiveChessHandler);
            chainMessageSelector.joinChannel(subject.getId(),fiveChessHandler,sender.getId());
            fiveChessHandler.init(sender.getId());
            subject.sendMessage(new MessageChainBuilder().append("创建对局成功!").append(Contact.uploadImage(subject,
                    fiveChessHandler.drawMap())).build());
        }else {
            if(fiveChessHandler.isFull()){
                return new MessageChainBuilder().append("对局已满").build();
            }
            fiveChessHandler.join(sender.getId());
            chainMessageSelector.joinChannel(subject.getId(),fiveChessHandler,sender.getId());
            subject.sendMessage(new MessageChainBuilder().append("加入对局成功!").build());
        }
        return null;
    }
}
