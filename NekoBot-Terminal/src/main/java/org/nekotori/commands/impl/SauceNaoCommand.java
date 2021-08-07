package org.nekotori.commands.impl;

import cn.hutool.core.io.IORuntimeException;
import cn.hutool.http.HttpRequest;
import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.Member;
import net.mamoe.mirai.message.data.*;
import org.nekotori.annotations.Command;
import org.nekotori.commands.PrivilegeGroupCommand;
import org.nekotori.entity.SauceNaoData;
import org.nekotori.utils.HibiApiUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.io.InputStream;
import java.util.List;

@Command(name = {"sauce"})
public class SauceNaoCommand extends PrivilegeGroupCommand {

    @Resource
    private HibiApiUtils hibiApiUtils;


    @Override
    public MessageChain execute(Member sender, MessageChain messageChain, Group subject) {

        String imageUrl = "";
        for(SingleMessage s:messageChain){
            if(s instanceof Image){
                imageUrl = Image.queryUrl((Image)s);
            }
        }
        if(StringUtils.isEmpty(imageUrl)) return null;
        byte[] bytes = HttpRequest.get(imageUrl).execute().bodyBytes();
        List<SauceNaoData> sauceNaoDataList = hibiApiUtils.queryImage(bytes);
        MessageChainBuilder append = new MessageChainBuilder().append(new At(sender.getId()));
        if(CollectionUtils.isEmpty(sauceNaoDataList)) return append.append(new PlainText("\nNekoBot找不到关于此图片的信息")).build();
        append.append(new PlainText("\nNekoBot找到以下信息:\n"));
        for (SauceNaoData s:sauceNaoDataList){
            String thumbnailUrl = s.getThumbnailUrl();
            try {
                InputStream inputStream = HttpRequest.get(thumbnailUrl).setConnectionTimeout(5 * 1000).setReadTimeout(5 * 1000).execute().bodyStream();
                append.append(Contact.uploadImage(subject, inputStream));
                append.append(new PlainText("\n相似度:" +
                        s.getSimilarity() +
                        "\n源地址:" +
                        s.getExtUrls() +
                        "\n标签:" +
                        s.getTittle()));
            }catch (IORuntimeException e){
                e.printStackTrace();
            }
        }
        return append.build();
    }
}
