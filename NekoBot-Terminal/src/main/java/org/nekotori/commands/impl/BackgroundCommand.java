package org.nekotori.commands.impl;

import cn.hutool.http.HttpUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.Member;
import net.mamoe.mirai.message.data.At;
import net.mamoe.mirai.message.data.Image;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import net.mamoe.mirai.message.data.PlainText;
import net.mamoe.mirai.message.data.QuoteReply;
import net.mamoe.mirai.message.data.SingleMessage;
import org.nekotori.annotations.IsCommand;
import org.nekotori.commands.NoAuthGroupCommand;
import org.nekotori.commands.PrivilegeGroupCommand;
import org.nekotori.dao.ChatMemberMapper;
import org.nekotori.entity.ChatMemberDo;
import org.nekotori.entity.CommandAttr;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.io.InputStream;
import java.util.List;

@IsCommand(name = {"背景"},description = "设置用户签到卡片背景\n格式:\n    (!/-/#)背景 <ImgUrl/图片>")
public class BackgroundCommand extends PrivilegeGroupCommand {

    @Resource
    private ChatMemberMapper memberMapper;

    @Override
    public MessageChain execute(Member sender, Group subject, CommandAttr commandAttr, MessageChain messageChain) {
        List<String> param = commandAttr.getParam();
        MessageChain res = null;
        String imgUrl = null;
        if(!CollectionUtils.isEmpty(commandAttr.getExtMessage())){
            SingleMessage singleMessage = commandAttr.getExtMessage().get(0);
            imgUrl = Image.queryUrl((Image) singleMessage);
        }
        if (!CollectionUtils.isEmpty(param)) {
            imgUrl = param.get(0);
        }
        if(imgUrl == null){
            return new MessageChainBuilder()
                    .append(new QuoteReply(messageChain))
                    .append(new PlainText("请输入图片url或直接发送图片"))
                    .build();
        }
        try (InputStream inputStream = HttpUtil.createGet(imgUrl)
                             .setConnectionTimeout(5000)
                             .setReadTimeout(5000)
                             .execute()
                             .bodyStream()) {
            ChatMemberDo chatMemberDo = memberMapper.selectOne(Wrappers.<ChatMemberDo>lambdaQuery().eq(ChatMemberDo::getGroupId, subject.getId()).eq(ChatMemberDo::getMemberId, sender.getId()));
            if(chatMemberDo == null){
                throw new Exception();
            }
            chatMemberDo.setBackgroundUri(imgUrl);
            memberMapper.updateById(chatMemberDo);
            return new MessageChainBuilder()
                    .append(new QuoteReply(messageChain))
                    .append(new PlainText("背景设置成功"))
                    .append(Contact.uploadImage(subject, inputStream))
                    .build();
        } catch (Exception e) {
            return new MessageChainBuilder()
                    .append(new QuoteReply(messageChain))
                    .append(new PlainText("背景设置失败"))
                    .build();
        }
    }
}
