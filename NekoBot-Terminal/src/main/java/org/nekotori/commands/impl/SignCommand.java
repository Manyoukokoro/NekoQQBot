package org.nekotori.commands.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.Member;
import net.mamoe.mirai.message.data.At;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import net.mamoe.mirai.message.data.PlainText;
import org.nekotori.annotations.Command;
import org.nekotori.commands.NoAuthGroupCommand;
import org.nekotori.dao.ChatMemberMapper;
import org.nekotori.entity.ChatMemberDo;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.Random;

/**
 * @author: JayDeng
 * @date: 31/08/2021 10:44
 * @description:
 * @version: {@link }
 */
@Command(name = {"签到"}, description = "签到")
public class SignCommand extends NoAuthGroupCommand {

    @Resource
    private ChatMemberMapper chatMemberMapper;

    @Override
    public MessageChain execute(Member sender, MessageChain messageChain, Group subject) {
        final long menber = sender.getId();
        final long group = subject.getId();
        ChatMemberDo chatMemberDo = chatMemberMapper.selectOne(new QueryWrapper<ChatMemberDo>().eq("group_id", group).eq("member_id", menber));
        if(!ObjectUtils.isEmpty(chatMemberDo) && chatMemberDo.getTodaySign()){
            return  new MessageChainBuilder().append(new At(sender.getId()))
                    .append(" ")
                    .append(new PlainText("老板今天已经签到过了哦"))
                    .build();
        }
        if(chatMemberDo == null){
            final ChatMemberDo build = ChatMemberDo.builder()
                    .memberId(menber)
                    .groupId(group)
                    .isBlocked(false)
                    .level(0)
                    .nickName(sender.getNameCard())
                    .todaySign(true)
                    .todayWelcome(false)
                    .totalSign(0)
                    .exp(0L)
                    .build();
            final int id = chatMemberMapper.insert(build);
            build.setId(id);
            chatMemberDo = build;
        }
        final Random random = new Random();
        final int rank = random.nextInt(5);
        final int incomeExp = random.nextInt((int) Math.pow(10d, rank));
        final ChatMemberDo chatMemberDoNew = calLevel(chatMemberDo, incomeExp+chatMemberDo.getExp());
        chatMemberDoNew.setTodaySign(true);
        chatMemberDoNew.setTotalSign(chatMemberDoNew.getTotalSign()+1);
        chatMemberMapper.updateById(chatMemberDoNew);
        return new MessageChainBuilder().append(new At(sender.getId()))
                .append(" ")
                .append(new PlainText("签到成功！获得经验"+incomeExp+","+"当前等级"+chatMemberDoNew.getLevel()+",真是太幸运了呢"))
                .build();
    }

    private ChatMemberDo calLevel(ChatMemberDo chatMemberDo,long incomeExp){
        if(chatMemberDo.getLevel()<10 && incomeExp>=10) {
            chatMemberDo.setLevel(chatMemberDo.getLevel()+1);
            return calLevel(chatMemberDo,incomeExp-10);
        }
        if(chatMemberDo.getLevel()<20 && incomeExp>=20){
            chatMemberDo.setLevel(chatMemberDo.getLevel()+1);
            return calLevel(chatMemberDo,incomeExp-20);
        }
        if(chatMemberDo.getLevel()<30 && incomeExp>=40){
            chatMemberDo.setLevel(chatMemberDo.getLevel()+1);
            return calLevel(chatMemberDo,incomeExp-40);
        }
        if(chatMemberDo.getLevel()<40 && incomeExp>=80){
            chatMemberDo.setLevel(chatMemberDo.getLevel()+1);
            return calLevel(chatMemberDo,incomeExp-80);
        }
        if(chatMemberDo.getLevel()<50 && incomeExp>=160){
            chatMemberDo.setLevel(chatMemberDo.getLevel()+1);
            return calLevel(chatMemberDo,incomeExp-160);
        }
        if(chatMemberDo.getLevel()<60 && incomeExp>=320){
            chatMemberDo.setLevel(chatMemberDo.getLevel()+1);
            return calLevel(chatMemberDo,incomeExp-320);
        }
        if(chatMemberDo.getLevel()<70 && incomeExp>=640){
            chatMemberDo.setLevel(chatMemberDo.getLevel()+1);
            return calLevel(chatMemberDo,incomeExp-640);
        }
        if(chatMemberDo.getLevel()<80 && incomeExp>=1280){
            chatMemberDo.setLevel(chatMemberDo.getLevel()+1);
            return calLevel(chatMemberDo,incomeExp-1280);
        }
        if(chatMemberDo.getLevel()<90 && incomeExp>=2560){
            chatMemberDo.setLevel(chatMemberDo.getLevel()+1);
            return calLevel(chatMemberDo,incomeExp-2560);
        }
        if(chatMemberDo.getLevel()<100 && incomeExp>=5120){
            chatMemberDo.setLevel(chatMemberDo.getLevel()+1);
            return calLevel(chatMemberDo,incomeExp-5120);
        }
        if(chatMemberDo.getLevel()>=100 && incomeExp>=10240){
            chatMemberDo.setLevel(chatMemberDo.getLevel()+1);
            return calLevel(chatMemberDo,incomeExp-10240);
        }
        chatMemberDo.setExp(incomeExp);
        return chatMemberDo;
    }
}
    