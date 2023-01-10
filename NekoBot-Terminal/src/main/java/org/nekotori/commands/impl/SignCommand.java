package org.nekotori.commands.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Pair;
import cn.hutool.core.util.HexUtil;
import cn.hutool.crypto.digest.MD5;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.Member;
import net.mamoe.mirai.message.data.At;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import net.mamoe.mirai.message.data.PlainText;
import net.mamoe.mirai.message.data.QuoteReply;
import org.nekotori.annotations.IsCommand;
import org.nekotori.commands.PrivilegeGroupCommand;
import org.nekotori.dao.ChatHistoryMapper;
import org.nekotori.dao.ChatMemberMapper;
import org.nekotori.entity.ChatHistoryDo;
import org.nekotori.entity.ChatMemberDo;
import org.nekotori.entity.CommandAttr;
import org.nekotori.utils.CommandUtils;
import org.nekotori.utils.GachaUtils;
import org.nekotori.utils.ImageUtil;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.io.InputStream;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author: JayDeng
 * @date: 31/08/2021 10:44
 * @description:
 * @version: {@link }
 */
@IsCommand(name = {"签到","签到图片"}, description = "签到和获取当前签到图片\n格式:\n    (!/-/#)签到/签到图片")
public class SignCommand extends PrivilegeGroupCommand {
    private static final Map<String,String> imgCache = new HashMap<>();
    public static String getSignImg(Long senderId,Long groupId){
        return imgCache.get(groupId+":"+senderId);
    }
    @Resource
    private ChatMemberMapper chatMemberMapper;
    @Resource
    private ChatHistoryMapper chatHistoryMapper;
    @Override
    public MessageChain execute(Member sender, Group subject, CommandAttr commandAttr, MessageChain messageChain) {
        if("签到图片".equals(CommandUtils.resolveCommand(messageChain).getCommand())){
            String signImg = getSignImg(sender.getId(), subject.getId());
            if(!StringUtils.hasLength(signImg)){
                signImg = "找不到对象";
            }
            return new MessageChainBuilder().append(new QuoteReply(messageChain)).append("\n").append(signImg)
                    .build();
        }
        final long member = sender.getId();
        final long group = subject.getId();
        ChatMemberDo chatMemberDo = chatMemberMapper.selectOne(new QueryWrapper<ChatMemberDo>().eq("group_id", group).eq("member_id", member));
        if (!ObjectUtils.isEmpty(chatMemberDo) && chatMemberDo.checkTodaySign()) {
            return new MessageChainBuilder().append(new QuoteReply(messageChain))
                    .append(" ")
                    .append(new PlainText("老板今天已经签到过了哦"))
                    .build();
        }
        Long msgCount = chatHistoryMapper.selectCount(Wrappers.<ChatHistoryDo>lambdaQuery()
                .eq(ChatHistoryDo::getSenderId, sender.getId())
                .gt(ChatHistoryDo::getTime, DateUtil.yesterday())
                .eq(ChatHistoryDo::getGroupId, subject.getId()));

        Long imgCount = chatHistoryMapper.selectCount(Wrappers.<ChatHistoryDo>lambdaQuery()
                .eq(ChatHistoryDo::getSenderId, sender.getId())
                .gt(ChatHistoryDo::getTime, DateUtil.yesterday())
                .like(ChatHistoryDo::getContent, "[mirai:image:{")
                .eq(ChatHistoryDo::getGroupId, subject.getId()));
        System.out.println("msg count = "+msgCount);
        System.out.println("img count = "+imgCount);
        int signLevel = 0;
        if(msgCount==0){
        }else if (msgCount<5){
            signLevel = 1;
        }else if (msgCount<10){
            signLevel = 2;
        }else if  (msgCount<50){
            signLevel = 3;
        }else if (msgCount<100){
            signLevel = 4;
        }else if (msgCount<500){
            signLevel = 5;
        }else {
            signLevel = 6;
        }
        int incomeExp = GachaUtils.gachaExp(signLevel);
        incomeExp+=imgCount*500;
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
        Pair<InputStream, String> inputStreamStringPair = ImageUtil.drawSignPic(sender, chatMemberDo, incomeExp,msgCount.intValue(),imgCount.intValue());
        imgCache.put(subject.getId()+":"+sender.getId(),inputStreamStringPair.getValue());
        return new MessageChainBuilder().append(new QuoteReply(messageChain))
                .append(Contact.uploadImage(subject, inputStreamStringPair.getKey()))
                .build();
    }
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
    public long getSeed(long id){
        String nowDate = new SimpleDateFormat("yyyyMMdd").format(new Date());
        String nowDateMD5Hex = MD5.create().digestHex(nowDate);
        BigInteger nowDateMD5 = HexUtil.toBigInteger(nowDateMD5Hex);
        int nowDateMD5Mod = nowDateMD5.mod(BigInteger.valueOf(Integer.MAX_VALUE)).intValueExact();
        return id ^ nowDateMD5Mod;
    }
}
    