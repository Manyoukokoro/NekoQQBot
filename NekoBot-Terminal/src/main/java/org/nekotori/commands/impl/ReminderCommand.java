package org.nekotori.commands.impl;

import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.Member;
import net.mamoe.mirai.message.data.MessageChain;
import org.nekotori.commands.NoAuthGroupCommand;
import org.nekotori.entity.CommandAttr;

public class ReminderCommand extends NoAuthGroupCommand {
    @Override
    public MessageChain execute(Member sender, Group subject, CommandAttr commandAttr, MessageChain messageChain) {
        return null;
    }
}
