package org.nekotori.commands.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.Member;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import org.nekotori.annotations.IsCommand;
import org.nekotori.commands.ManagerGroupCommand;
import org.nekotori.dao.ChatMemberMapper;
import org.nekotori.entity.ChatMemberDo;

import javax.annotation.Resource;
import java.util.Comparator;
import java.util.List;

/**
 * @author: JayDeng
 * @date: 2022/2/28 下午2:59
 * @description: CustomComCommand
 * @version: {@link }
 */

@IsCommand(name = {"查询等级","level"},description = "查询群友等级，格式:(!/-/#)查询等级")
public class QueryLevelCommand extends ManagerGroupCommand {

    @Resource
    private ChatMemberMapper chatMemberMapper;

    @Override
    public MessageChain execute(Member sender, MessageChain messageChain, Group subject) {
        List<ChatMemberDo> memberDos = chatMemberMapper.selectList(new QueryWrapper<ChatMemberDo>().eq("group_id", subject.getId()));
        MessageChainBuilder singleMessages = new MessageChainBuilder();
        singleMessages.append("本群成员等级排行如下：");
        memberDos.stream().sorted(Comparator.comparing(ChatMemberDo::getLevel).reversed()).forEach(member->{
            singleMessages.append("\n").append(member.getNickName()).append(":").append(String.valueOf(member.getLevel())).append("级");
        });
        return singleMessages.build();
    }
}
