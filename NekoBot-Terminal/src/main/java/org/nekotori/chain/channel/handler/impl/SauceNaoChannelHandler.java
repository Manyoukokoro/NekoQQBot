package org.nekotori.chain.channel.handler.impl;

import cn.hutool.core.io.IORuntimeException;
import cn.hutool.http.HttpRequest;
import lombok.extern.slf4j.Slf4j;
import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.Member;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.message.data.*;
import org.nekotori.annotations.HandlerId;
import org.nekotori.chain.ChainMessageSelector;
import org.nekotori.chain.channel.GroupCommandChannel;
import org.nekotori.chain.channel.handler.ChannelHandler;
import org.nekotori.entity.SauceNaoData;
import org.nekotori.utils.HibiApiUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.io.InputStream;
import java.util.List;

/**
 * @author: JayDeng
 * @date: 31/08/2021 16:43
 * @description:
 * @version: {@link }
 */

@Slf4j
@Deprecated
public class SauceNaoChannelHandler implements ChannelHandler {

    @Resource
    private HibiApiUtils hibiApiUtils;

    @Resource
    private ChainMessageSelector chainMessageSelector;


    @Override
    public void handleMessage(GroupCommandChannel channel, GroupMessageEvent groupMessageEvent) {
        final MessageChain message = groupMessageEvent.getMessage();
        final Member sender = groupMessageEvent.getSender();
        final Group group = groupMessageEvent.getGroup();
        String imageUrl = "";
        for (SingleMessage s : message) {
            if (s instanceof Image) {
                imageUrl = Image.queryUrl((Image) s);
            }
        }
        if (StringUtils.isEmpty(imageUrl)) {
            group.sendMessage(new MessageChainBuilder().append(new QuoteReply(message)).append(new PlainText(" 这看起来不是一张图片...")).build());
            return;
        }
        byte[] bytes = HttpRequest.get(imageUrl).execute().bodyBytes();
        List<SauceNaoData> sauceNaoDataList = hibiApiUtils.queryImage(bytes);
        MessageChainBuilder append = new MessageChainBuilder().append(new QuoteReply(message));
        if (CollectionUtils.isEmpty(sauceNaoDataList)) {
            group.sendMessage(append.append(new PlainText("\nNekoBot找不到关于此图片的信息")).build());
            return;
        }
        append.append(new PlainText("\nNekoBot找到以下信息:\n"));
        try {
            for (SauceNaoData s : sauceNaoDataList) {
                String thumbnailUrl = s.getThumbnailUrl();
                InputStream inputStream = HttpRequest.get(thumbnailUrl).setConnectionTimeout(5 * 1000).setReadTimeout(5 * 1000).execute().bodyStream();

                
                if (Float.parseFloat(s.getSimilarity()) > 60) {
                    append.append(Contact.uploadImage(group, inputStream));
                }
                append.append(new PlainText("\n相似度:" + s.getSimilarity() + "\n源地址:" + s.getExtUrls() + "\n标签:" + s.getTittle()));
            }
        } catch (IORuntimeException e) {
            log.error("sauceNao query error:", e);
        } finally {
            group.sendMessage(append.build());
            chainMessageSelector.unregisterChannel(group.getId(), this.getClass().getAnnotation(HandlerId.class).value());
        }
    }
}
    