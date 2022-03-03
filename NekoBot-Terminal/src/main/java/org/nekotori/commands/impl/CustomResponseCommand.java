package org.nekotori.commands.impl;

import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.Member;
import net.mamoe.mirai.message.data.Dice;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import net.mamoe.mirai.message.data.PlainText;
import org.nekotori.annotations.IsCommand;
import org.nekotori.commands.NoAuthGroupCommand;
import org.nekotori.common.InnerConstants;
import org.nekotori.entity.CommandAttr;
import org.nekotori.utils.CommandUtils;
import org.springframework.util.CollectionUtils;

import java.util.List;


@IsCommand(name = {"回复","response"},description = "自定义回复，格式:(!/-/#)回复 触发方式 触发文 回复文")
public class CustomResponseCommand extends NoAuthGroupCommand {
    @Override
    public MessageChain execute(Member sender, MessageChain messageChain, Group subject) {
        String s = messageChain.contentToString();
        CommandAttr commandAttr = CommandUtils.resolveCommand(s);
        List<String> param = commandAttr.getParam();
        if(param.size()<3){
            return new MessageChainBuilder().append(new PlainText("参数过少，请检查一下哦")).build();
        }

        return null;
    }
}
