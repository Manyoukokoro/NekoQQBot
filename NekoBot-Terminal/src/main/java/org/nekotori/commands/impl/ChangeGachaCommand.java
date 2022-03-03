package org.nekotori.commands.impl;

import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.Member;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import net.mamoe.mirai.message.data.PlainText;
import org.nekotori.annotations.IsCommand;
import org.nekotori.chain.ChainMessageSelector;
import org.nekotori.chain.channel.handler.impl.ChangeCachaHandler;
import org.nekotori.chain.channel.handler.impl.SauceNaoChannelHandler;
import org.nekotori.commands.ManagerGroupCommand;
import org.nekotori.commands.NoAuthGroupCommand;
import org.nekotori.entity.CommandAttr;
import org.nekotori.utils.CommandUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;


@IsCommand(name = {"修改概率","gachap"},description = "修改抽卡概率，格式:(!/-/#)修改概率")
public class ChangeGachaCommand extends ManagerGroupCommand {

    @Resource
    private ChainMessageSelector chainMessageSelector;

    @Resource
    private ChangeCachaHandler changeCachaHandler;

    @Override
    public MessageChain execute(Member sender, MessageChain messageChain, Group subject) {
        chainMessageSelector.registerChannel(subject.getId(), changeCachaHandler);
        subject.sendMessage(new PlainText("请输入"+ChangeCachaHandler.stages.get(0)+"概率"));
        chainMessageSelector.joinChannel(subject.getId(),ChangeCachaHandler.class,sender.getId());
        return null;
        }
}
