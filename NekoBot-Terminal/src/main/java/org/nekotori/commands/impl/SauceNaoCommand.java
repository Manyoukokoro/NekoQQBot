package org.nekotori.commands.impl;

import cn.hutool.core.io.IORuntimeException;
import cn.hutool.http.HttpRequest;
import lombok.extern.slf4j.Slf4j;
import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.Member;
import net.mamoe.mirai.message.data.*;
import org.nekotori.annotations.Command;
import org.nekotori.annotations.HandlerId;
import org.nekotori.chain.ChainMessageSelector;
import org.nekotori.chain.channel.handler.SauceNaoChannelHandler;
import org.nekotori.commands.PrivilegeGroupCommand;
import org.nekotori.entity.SauceNaoData;
import org.nekotori.utils.HibiApiUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.io.InputStream;
import java.util.List;

@Command(name = {"sauce","检索图片"},description = "使用sauceNao进行图片检索，格式:(!/-/#)sauce [上传图片]")
@Slf4j
public class SauceNaoCommand extends PrivilegeGroupCommand {

    @Resource
    private HibiApiUtils hibiApiUtils;

    @Resource
    private ChainMessageSelector chainMessageSelector;

    @Resource
    private SauceNaoChannelHandler sauceNaoChannelHandler;

    @Override
    public MessageChain execute(Member sender, MessageChain messageChain, Group subject) {

        String imageUrl = "";
        for(SingleMessage s:messageChain){
            if(s instanceof Image){
                imageUrl = Image.queryUrl((Image)s);
            }
        }
        if(StringUtils.isEmpty(imageUrl)) {
            try {
                chainMessageSelector.registerChannel(subject.getId(), sauceNaoChannelHandler);
                subject.sendMessage(new PlainText("请直接发送图片给NekoBot"));
            }catch (RuntimeException e){
                subject.sendMessage(new PlainText("已经在查询队列中哦，请直接发送图片给NekoBot"));
            }
            chainMessageSelector.joinChannel(subject.getId(),SauceNaoChannelHandler.class.getAnnotation(HandlerId.class).value(),sender.getId());
            return null;
        }
        byte[] bytes = HttpRequest.get(imageUrl).execute().bodyBytes();
        List<SauceNaoData> sauceNaoDataList = hibiApiUtils.queryImage(bytes);
        MessageChainBuilder append = new MessageChainBuilder().append(new At(sender.getId()));
        if(CollectionUtils.isEmpty(sauceNaoDataList)) return append.append(new PlainText("\nNekoBot找不到关于此图片的信息")).build();
        append.append(new PlainText("\nNekoBot找到以下信息:\n"));
        for (SauceNaoData s:sauceNaoDataList){
            String thumbnailUrl = s.getThumbnailUrl();
            try {
                InputStream inputStream = HttpRequest.get(thumbnailUrl).setConnectionTimeout(5 * 1000).setReadTimeout(5 * 1000).execute().bodyStream();
                if(Float.parseFloat(s.getSimilarity())>60){
                    append.append(Contact.uploadImage(subject, inputStream));
                }
                append.append(new PlainText("\n相似度:" +
                        s.getSimilarity() +
                        "\n源地址:" +
                        s.getExtUrls() +
                        "\n标签:" +
                        s.getTittle()));
            }catch (IORuntimeException e){
                log.error("saucenao query error:",e);
            }
        }
        return append.build();
    }
}
