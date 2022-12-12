package org.nekotori.commands.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.Member;
import net.mamoe.mirai.contact.MemberPermission;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import net.mamoe.mirai.message.data.PlainText;
import org.nekotori.annotations.IsCommand;
import org.nekotori.commands.NoAuthGroupCommand;
import org.nekotori.dao.ChatMemberMapper;
import org.nekotori.entity.ChatMemberDo;
import org.nekotori.entity.CommandAttr;
import org.nekotori.utils.CommandUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.Comparator;
import java.util.List;

/**
 * @author: JayDeng
 * @date: 2022/2/28 下午2:59
 * @description: CustomComCommand
 * @version: {@link }
 */

@IsCommand(name = {"查询等级", "level"}, description = "查询群友等级，格式:(!/-/#)查询等级")
public class QueryLevelCommand extends NoAuthGroupCommand {

    @Resource
    private ChatMemberMapper chatMemberMapper;

    @Override
    public MessageChain execute(Member sender, Group subject, CommandAttr commandAttr, MessageChain messageChain) {
        MessageChainBuilder singleMessages = new MessageChainBuilder();
        String s = messageChain.serializeToMiraiCode();
        if ((sender.getPermission().equals(MemberPermission.ADMINISTRATOR) ||
                sender.getPermission().equals(MemberPermission.OWNER))
                && !CollectionUtils.isEmpty(commandAttr.getParam())
                && "all".equals(commandAttr.getParam().get(0))) {
            List<ChatMemberDo> memberDos = chatMemberMapper.selectList(new QueryWrapper<ChatMemberDo>().eq("group_id", subject.getId()));
            singleMessages.append("本群成员等级排行如下：");
            memberDos.stream().sorted(Comparator.comparing(ChatMemberDo::getLevel).reversed()).forEach(member -> {
                singleMessages.append("\n").append(member.getNickName()).append(":").append(String.valueOf(member.getLevel())).append("级");
            });
        } else {
            ChatMemberDo chatMemberDo = chatMemberMapper.selectOne(new QueryWrapper<ChatMemberDo>().eq("group_id", subject.getId()).eq("member_id",
                    sender.getId()));
            if (ObjectUtils.isEmpty(chatMemberDo)) {
                singleMessages.append(new PlainText("您还没有签到过哦"));
            } else {
                singleMessages.append(new PlainText("您现在为：" + chatMemberDo.getLevel() + "级,累计签到:" + chatMemberDo.getTotalSign() +
                        "天"));
            }
        }
        return singleMessages.build();
    }
}
