package org.nekotori.commands.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.Member;
import net.mamoe.mirai.message.data.At;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import net.mamoe.mirai.message.data.PlainText;
import org.nekotori.annotations.IsCommand;
import org.nekotori.commands.NoAuthGroupCommand;
import org.nekotori.dao.ChatMemberMapper;
import org.nekotori.entity.ChatMemberDo;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.Date;
import java.util.Random;

/**
 * @author: JayDeng
 * @date: 31/08/2021 10:44
 * @description:
 * @version: {@link }
 */
@IsCommand(name = {"签到","sign"}, description = "签到")
public class SignCommand extends NoAuthGroupCommand {

    @Resource
    private ChatMemberMapper chatMemberMapper;

    @Override
    public MessageChain execute(Member sender, MessageChain messageChain, Group subject) {
        final long member = sender.getId();
        final long group = subject.getId();
        ChatMemberDo chatMemberDo = chatMemberMapper.selectOne(new QueryWrapper<ChatMemberDo>().eq("group_id", group).eq("member_id", member));
        if(!ObjectUtils.isEmpty(chatMemberDo) && chatMemberDo.checkTodaySign()){
            return  new MessageChainBuilder().append(new At(sender.getId()))
                    .append(" ")
                    .append(new PlainText("老板今天已经签到过了哦"))
                    .build();
        }
        if(chatMemberDo == null){
            chatMemberDo = ChatMemberDo.builder()
                    .memberId(member)
                    .groupId(group)
                    .isBlocked(false)
                    .level(0)
                    .nickName(sender.getNick())
                    .lastSign(new Date())
                    .todayWelcome(false)
                    .totalSign(0)
                    .exp(0L)
                    .build();
            final int id = chatMemberMapper.insert(chatMemberDo);
            chatMemberDo.setId(id);
        }
        final Random random = new Random();
        final int rank = random.nextInt(5)+1;
        int incomeExp = random.nextInt((int) Math.pow(10d, rank));
        if(incomeExp == 0){
            return new MessageChainBuilder().append("叮~~~~~~~，恭喜亲获得了零经验，这边送您重签卡一张~，您真是太欧了呢").build();
        }
        if(incomeExp < 10){
            incomeExp = random.nextInt(500)+500;
        }
        if(incomeExp > 99900){
            try{
                for(int i=10;i>0;i--){
                    subject.sendMessage(String.valueOf(i));
                    Thread.sleep(1000);
                }
                subject.sendMessage("毁天灭地！");
            }catch (InterruptedException e){
                return new MessageChainBuilder().append("出现了预料之外的事故>x<").build();
            }
            incomeExp = 99999;
        }
        final ChatMemberDo chatMemberDoNew = calLevel(chatMemberDo, incomeExp+chatMemberDo.getExp());
        chatMemberDoNew.setNickName(sender.getNameCard());
        chatMemberDoNew.setLastSign(new Date());
        chatMemberDoNew.setTotalSign(chatMemberDoNew.getTotalSign()+1);
        chatMemberMapper.updateById(chatMemberDoNew);
        return new MessageChainBuilder().append(new At(sender.getId()))
                .append(" ")
                .append(new PlainText("签到成功！获得经验"+incomeExp+","+"当前等级"+chatMemberDoNew.getLevel()+"距离下一级还差："+nextLevelExp(chatMemberDoNew.getLevel(),chatMemberDoNew.getExp())+"经验," +
                        "真是太幸运了呢"))
                .build();
    }

    private long nextLevelExp(int level, long exp){
        if(level<100){
            return (long) (Math.pow(2d,(int)((double) level/10))*10) -  exp;
        }
        return 10240L -exp;
    }

//    public static void main(String[] args) {
//        System.out.println(calLevel(ChatMemberDo.builder().level(1001).build(),32443));
//    }

    private static ChatMemberDo calLevel(ChatMemberDo chatMemberDo,long incomeExp){
        if (chatMemberDo.getLevel()<100 && incomeExp >= (Math.pow(2d,(int)((double) chatMemberDo.getLevel()/10))*10)){
            chatMemberDo.setLevel(chatMemberDo.getLevel()+1);
            return calLevel(chatMemberDo,incomeExp-(long)(Math.pow(2d,(int)((double) chatMemberDo.getLevel()/10))*10));
        }
        if(chatMemberDo.getLevel()>=100 && incomeExp>=10240){
            chatMemberDo.setLevel(chatMemberDo.getLevel()+1);
            return calLevel(chatMemberDo,incomeExp-10240);
        }
        chatMemberDo.setExp(incomeExp);
        return chatMemberDo;
    }
}
    