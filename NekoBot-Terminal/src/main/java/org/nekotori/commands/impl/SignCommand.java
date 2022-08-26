package org.nekotori.commands.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.Member;
import net.mamoe.mirai.message.data.At;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import net.mamoe.mirai.message.data.PlainText;
import org.nekotori.annotations.IsCommand;
import org.nekotori.commands.PrivilegeGroupCommand;
import org.nekotori.dao.ChatGroupMapper;
import org.nekotori.dao.ChatMemberMapper;
import org.nekotori.entity.ChatMemberDo;
import org.nekotori.utils.ImageUtil;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

/**
 * @author: JayDeng
 * @date: 31/08/2021 10:44
 * @description:
 * @version: {@link }
 */
@IsCommand(name = {"签到", "sign"}, description = "签到")
public class SignCommand extends PrivilegeGroupCommand {

    @Resource
    private ChatMemberMapper chatMemberMapper;

    @Resource
    private ChatGroupMapper chatGroupMapper;

    @Override
    public MessageChain execute(Member sender, MessageChain messageChain, Group subject) {
        final long member = sender.getId();
        final long group = subject.getId();
        ChatMemberDo chatMemberDo = chatMemberMapper.selectOne(new QueryWrapper<ChatMemberDo>().eq("group_id", group).eq("member_id", member));
        if (!ObjectUtils.isEmpty(chatMemberDo) && chatMemberDo.checkTodaySign()) {
            return new MessageChainBuilder().append(new At(sender.getId()))
                    .append(" ")
                    .append(new PlainText("老板今天已经签到过了哦"))
                    .build();
        }
        long seed = (sender.getId() | Integer.parseInt(new SimpleDateFormat("yyyyMMdd").format(new Date()))) % 559;
        final Random random = new Random(seed);
        final int rank = random.nextInt(5) + 1;
        int incomeExp = random.nextInt((int) Math.pow(10d, rank));
        if (incomeExp == 0) {
            return new MessageChainBuilder().append("叮~~~~~~~，恭喜亲获得了零经验，这边送您重签卡一张~，您真是太欧了呢").build();
        }
        if (incomeExp < 100) {
            incomeExp = random.nextInt(500) + 500;
        }
        if (incomeExp > 99900) {
            subject.sendMessage("今日好运爆棚哦~");
            incomeExp = 99999;
        }
        if (chatMemberDo == null) {
            chatMemberDo = ChatMemberDo.builder()
                    .memberId(member)
                    .groupId(group)
                    .isBlocked(false)
                    .level(0)
                    .nickName(StringUtils.hasLength(sender.getNameCard()) ? sender.getNameCard() : sender.getNick())
                    .lastSign(new Date())
                    .todayWelcome(false)
                    .totalSign(1)
                    .exp(0L)
                    .build();
            chatMemberDo = calLevel(chatMemberDo, incomeExp);
            final int id = chatMemberMapper.insert(chatMemberDo);
            chatMemberDo.setId(id);
        } else {
            final ChatMemberDo chatMemberDoNew = calLevel(chatMemberDo, incomeExp + chatMemberDo.getExp());
            chatMemberDoNew.setNickName(StringUtils.hasLength(sender.getNameCard()) ? sender.getNameCard() : sender.getNick());
            chatMemberDoNew.setLastSign(new Date());
            chatMemberDoNew.setTotalSign(chatMemberDoNew.getTotalSign() + 1);
            chatMemberMapper.updateById(chatMemberDoNew);
        }
        return new MessageChainBuilder().append(new At(sender.getId()))
                .append(Contact.uploadImage(subject, ImageUtil.drawSignPic(sender, chatMemberDo, incomeExp)))
                .build();


    }

    private long nextLevelExp(int level, long exp) {
        if (level < 100) {
            return (long) (Math.pow(2d, (int) ((double) level / 10)) * 10) - exp;
        }
        return 10240L - exp;
    }

//    public static void main(String[] args) {
//        System.out.println(calLevel(ChatMemberDo.builder().level(1001).build(),32443));
//    }

    private static ChatMemberDo calLevel(ChatMemberDo chatMemberDo, long incomeExp) {
        if (chatMemberDo.getLevel() < 100 && incomeExp >= (Math.pow(2d, (int) ((double) chatMemberDo.getLevel() / 10)) * 10)) {
            chatMemberDo.setLevel(chatMemberDo.getLevel() + 1);
            return calLevel(chatMemberDo, incomeExp - (long) (Math.pow(2d, (int) ((double) chatMemberDo.getLevel() / 10)) * 10));
        }
        if (chatMemberDo.getLevel() >= 100 && incomeExp >= 10240) {
            chatMemberDo.setLevel(chatMemberDo.getLevel() + 1);
            return calLevel(chatMemberDo, incomeExp - 10240);
        }
        chatMemberDo.setExp(incomeExp);
        return chatMemberDo;
    }
}
    