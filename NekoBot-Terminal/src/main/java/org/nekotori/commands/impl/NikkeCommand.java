package org.nekotori.commands.impl;

import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.Member;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import net.mamoe.mirai.message.data.QuoteReply;
import org.jetbrains.annotations.NotNull;
import org.nekotori.annotations.IsCommand;
import org.nekotori.commands.NoAuthGroupCommand;
import org.nekotori.common.NikkeInfoType;
import org.nekotori.entity.CommandAttr;
import org.nekotori.exception.ElementNoFoundException;
import org.nekotori.utils.ChromeUtils;
import org.springframework.util.CollectionUtils;

import java.io.InputStream;
import java.util.List;

@IsCommand(name = {"nk","nikke","NK"})
public class NikkeCommand extends NoAuthGroupCommand {

    private static final List<String> cdkCom = List.of("cdk","CDK","今日cdk","今日CDK");
    private static final List<String> queryGeneral = List.of("查询角色","查询","角色");
    private static final List<String> querySkill = List.of("查询技能","技能");
    private static final List<String> queryPanel = List.of("查询面板","面板");


    @Override
    public MessageChain execute(Member sender, Group subject, CommandAttr commandAttr, MessageChain messageChain) {
        if (CollectionUtils.isEmpty(commandAttr.getParam())){
            return null;
        }
        String subCommand = commandAttr.getParam().get(0);
        if(cdkCom.contains(subCommand)) {
            InputStream nikkeCDK = ChromeUtils.getNikkeCDK();
            return new MessageChainBuilder().append(new QuoteReply(messageChain)).append(Contact.uploadImage(subject, nikkeCDK)).build();
        }
        if (commandAttr.getParam().size()<2){
            return null;
        }
        String target = commandAttr.getParam().get(1);
        if(queryGeneral.contains(subCommand)){
            return queryNikke(subject, messageChain, target,NikkeInfoType.GENERAL);
        }
        if (querySkill.contains(subCommand)){
            return queryNikke(subject, messageChain, target,NikkeInfoType.SKILL);
        }
        if(queryPanel.contains(subCommand)){
            return queryNikke(subject, messageChain, target,NikkeInfoType.PANEL);
        }
        return null;
    }

    @NotNull
    private static MessageChain queryNikke(Group subject, MessageChain messageChain, String target,NikkeInfoType type) {
        try {
            InputStream inputStream = ChromeUtils.queryNikkeInfo(target, type);
            return new MessageChainBuilder()
                    .append(new QuoteReply(messageChain))
                    .append("查询结果如下：\n")
                    .append(Contact.uploadImage(subject, inputStream))
                    .build();
        }catch (ElementNoFoundException e){
            return new MessageChainBuilder()
                    .append(new QuoteReply(messageChain))
                    .append("查询无结果")
                    .build();
        }
    }
}
