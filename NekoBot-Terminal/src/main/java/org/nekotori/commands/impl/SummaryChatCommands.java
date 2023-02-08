package org.nekotori.commands.impl;

import cn.hutool.core.collection.ListUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.Member;
import net.mamoe.mirai.message.code.MiraiCode;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import net.mamoe.mirai.message.data.PlainText;
import net.mamoe.mirai.message.data.QuoteReply;
import org.nekotori.annotations.IsCommand;
import org.nekotori.commands.ManagerGroupCommand;
import org.nekotori.dao.ChatHistoryMapper;
import org.nekotori.entity.ChatHistoryDo;
import org.nekotori.entity.CommandAttr;
import org.nekotori.handler.ThreadSingleton;
import org.nekotori.utils.ChromeUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.stream.Collectors;

@IsCommand(name = {"来个课代表","总结一下","课代表"})
public class SummaryChatCommands extends ManagerGroupCommand {

    @Resource
    private ChatHistoryMapper chatHistoryMapper;

    @Override
    public MessageChain execute(Member sender, Group subject, CommandAttr commandAttr, MessageChain messageChain) {
        List<ChatHistoryDo> chatHistoryDos = chatHistoryMapper.selectList(Wrappers.<ChatHistoryDo>lambdaQuery().eq(ChatHistoryDo::getGroupId, subject.getId()).orderByDesc(ChatHistoryDo::getTime).last("limit 60"));
        List<String> sum = chatHistoryDos.stream().map(ch -> {
            String content = ch.getContent();
            MessageChain mc = MiraiCode.deserializeMiraiCode(content);
            List<String> collect = mc.stream().filter(m -> m instanceof PlainText).map(m -> ((PlainText) m).getContent()).collect(Collectors.toList());
            String replaceAll =String.join("",collect);
            System.out.println(replaceAll);
            if (StringUtils.hasLength(replaceAll)) {
                Long senderId = ch.getSenderId();
                String s = subject.getMembers().stream().filter(m -> m.getId() == senderId).findAny().map(m -> StringUtils.hasLength(m.getNameCard()) ? m.getNameCard() : m.getNick()).orElseGet(() -> String.valueOf(senderId));
                String time = new SimpleDateFormat("HH:mm").format(ch.getTime());
                return s + "在" + time + "说:" + ch.getContent() + "\n";
            } else {
                return "";
            }
        }).collect(Collectors.toList());
        ListUtil.reverse(sum);
        sum.remove(sum.size()-1);
        String join = String.join("", sum);
        ThreadSingleton.run(()->{
            String summary = ChromeUtils.summaryChat(join);
            subject.sendMessage(new MessageChainBuilder()
                    .append(new QuoteReply(messageChain))
                    .append("总结最近聊天信息如下：")
                    .append(summary).build());
        });
        return new MessageChainBuilder()
                .append("NekoBot正在奋笔疾书中...").build();
    }
}
