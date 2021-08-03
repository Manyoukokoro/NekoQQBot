package org.nekotori.commands.impl;

import cn.hutool.core.net.url.UrlBuilder;
import cn.hutool.http.HttpUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.Member;
import net.mamoe.mirai.message.data.At;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import net.mamoe.mirai.message.data.PlainText;
import net.mamoe.mirai.utils.ExternalResource;
import org.nekotori.annotations.Command;
import org.nekotori.commands.PrivilegeGroupCommand;
import org.nekotori.entity.CommandAttr;
import org.nekotori.entity.YandereData;
import org.nekotori.utils.CommandUtils;
import org.nekotori.utils.JsonUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Command
public class YandereGroupCommand extends PrivilegeGroupCommand {
    public YandereGroupCommand() {
        super("AniS");
    }

    @Override
    public MessageChain execute(Member sender, MessageChain messageChain, Group subject) {
        CommandAttr commandAttr = CommandUtils.resolveCommand(messageChain.contentToString());
        List<String> param = commandAttr.getParam();
        String build =
                UrlBuilder.of("https://yande.re/post.json", StandardCharsets.UTF_8)
                        .addQuery("limit", "50")
                        .addQuery("tags", CollectionUtils.isEmpty(param)?"":String.join(" ",param))
                        .build();
        MessageChainBuilder singleMessages = new MessageChainBuilder();
        singleMessages.append(new At(sender.getId()));
        try {
            String body = HttpUtil.createGet(build).setReadTimeout(5 * 1000).executeAsync().body();
            List<YandereData> yandereData =
                    JsonUtils.json2Object(body, new TypeReference<>() {
                    });
            if (CollectionUtils.isEmpty(yandereData)){
                singleMessages.append("找不到对象");
            }
            else {
                Set<YandereData> imgs = new HashSet<>();
                if(yandereData.size()>5){
                    while(imgs.size()<5){
                        Random rand = new Random();
                        imgs.add(yandereData.get(rand.nextInt(yandereData.size())));
                    }
                }
                else {
                    imgs.addAll(yandereData);
                }

                for(YandereData y:imgs){
                    InputStream inputStream =
                            HttpUtil.createGet(y.getSample_url()).setReadTimeout(10 * 1000).execute().bodyStream();
                    singleMessages.append(subject.uploadImage(ExternalResource.create(inputStream)));
                    singleMessages.append(new PlainText("源地址:"+y.getSource()+"\n"+"画师:"+y.getAuthor()));
                }
            }
        } catch (SocketTimeoutException e) {
            singleMessages.append("访问超时");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return singleMessages.build();
    }
}
