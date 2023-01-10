package org.nekotori.commands.impl;

import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.Member;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import net.mamoe.mirai.message.data.QuoteReply;
import org.nekotori.annotations.IsCommand;
import org.nekotori.commands.ManagerGroupCommand;
import org.nekotori.dao.GroupSyncMapper;
import org.nekotori.entity.CommandAttr;
import org.nekotori.entity.GroupSyncDo;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;


@IsCommand(name = {"消息同步"},description = "将本群消息同步至目标群[施工中...]")
public class MessageSyncRequestCommand extends ManagerGroupCommand {

    @Resource
    private GroupSyncMapper groupSyncMapper;

    @Override
    public MessageChain execute(Member sender, Group subject, CommandAttr commandAttr, MessageChain messageChain) {
        List<String> param = commandAttr.getParam();
        if(CollectionUtils.isEmpty(param)){
            return null;
        }
        if(param.size()>1){
            return new MessageChainBuilder().append(new QuoteReply(messageChain)).append("一次只能最多指定1个群").build();
        }
        for (String s : param) {
            GroupSyncDo groupSyncDo = new GroupSyncDo();
            groupSyncDo.setSourceGroupId(subject.getId());
            groupSyncDo.setTargetGroups(s);
            groupSyncMapper.insert(groupSyncDo);
        }
        return new MessageChainBuilder().append(new QuoteReply(messageChain)).append("发起同步申请成功").build();
    }
}
