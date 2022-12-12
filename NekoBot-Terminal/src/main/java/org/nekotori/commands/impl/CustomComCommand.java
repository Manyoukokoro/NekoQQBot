package org.nekotori.commands.impl;

import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.Member;
import net.mamoe.mirai.message.data.MessageChain;
import org.nekotori.commands.ManagerGroupCommand;
import org.nekotori.entity.CommandAttr;

/**
 * @author: JayDeng
 * @date: 2022/2/28 下午2:59
 * @description: CustomComCommand
 * @version: {@link }
 */
public class CustomComCommand extends ManagerGroupCommand {
    @Override
    public MessageChain execute(Member sender, Group subject, CommandAttr commandAttr, MessageChain messageChain) {
        return null;
    }
}
