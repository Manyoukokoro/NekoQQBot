package org.nekotori.commands.impl;

import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.net.url.UrlBuilder;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.extern.slf4j.Slf4j;
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
import org.nekotori.entity.CommandAttr;
import org.nekotori.entity.YandereData;
import org.nekotori.entity.YandereTag;
import org.nekotori.utils.JsonUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.CollectionUtils;

import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

//@IsCommand(name = {"AniS","anis"},description = "使用yandere进行图片检索,此指令参数格式比较复杂，有兴趣可以参阅yandere的api文档\n格式:\n    (!/-/#)anis ...[参数]")
@Slf4j
public class YandereGroupCommand extends PrivilegeGroupCommand {

    @Value("${img.yandere-post}")
    private String yanderePost;

    @Value("${img.yandere-tag}")
    private String yandereTag;

    @Override
    public MessageChain execute(Member sender, Group subject, CommandAttr commandAttr, MessageChain messageChain) {
        List<String> param = commandAttr.getParam();
        String build =
                UrlBuilder.of(yanderePost, StandardCharsets.UTF_8)
                        .addQuery("limit", "50")
                        .addQuery("tags", CollectionUtils.isEmpty(param) ? "" : String.join(" ", param))
                        .build();
        MessageChainBuilder singleMessages = new MessageChainBuilder();
        Proxy proxy = new Proxy(Proxy.Type.HTTP,new InetSocketAddress("127.0.0.1", 7890));
        singleMessages.append(new QuoteReply(messageChain));
        try {
            String body = HttpUtil.createGet(build).setProxy(proxy).setReadTimeout(5 * 1000).setConnectionTimeout(5 * 1000).executeAsync().body();
            List<YandereData> yandereData =
                    JsonUtils.json2Object(body, new TypeReference<>() {
                    });
            if (CollectionUtils.isEmpty(yandereData)) {
                singleMessages.append("找不到对象,试试以下tag:\n");
                String tag =
                        UrlBuilder.of(yandereTag, StandardCharsets.UTF_8)
                                .addQuery("limit", "50")
                                .addQuery("name", CollectionUtils.isEmpty(param) ? "" : String.join(" ", param))
                                .build();
                String body1 = HttpRequest.get(tag).setProxy(proxy).executeAsync().body();
                List<YandereTag> yandereTags = JsonUtils.json2Object(body1, new TypeReference<>() {
                });
                if (!CollectionUtils.isEmpty(yandereTags)) {
                    for (YandereTag t : yandereTags) {
                        singleMessages.append(new PlainText(t.getName() + "\n"));
                    }
                }
            } else {
                Set<YandereData> imgs = new HashSet<>();
                if (yandereData.size() > 5) {
                    while (imgs.size() < 3) {
                        Random rand = new Random();
                        imgs.add(yandereData.get(rand.nextInt(yandereData.size())));
                    }
                } else {
                    imgs.addAll(yandereData);
                }

                for (YandereData y : imgs) {
                    log.info("request img url: {}", y.getSample_url());
                    InputStream inputStream =
                            HttpUtil.createGet(y.getSample_url()).setProxy(proxy).setReadTimeout(20 * 1000).setConnectionTimeout(10 * 1000).execute().bodyStream();
                    singleMessages.append(Contact.uploadImage(subject, inputStream));
                    singleMessages.append(new PlainText("源地址:" + y.getSource() + "\n"));
                }
            }
        } catch (IORuntimeException e) {
            log.error(e.getMessage(), e);
            singleMessages.append(new PlainText("\n访问超时"));
        }
        return singleMessages.build();
    }
}
