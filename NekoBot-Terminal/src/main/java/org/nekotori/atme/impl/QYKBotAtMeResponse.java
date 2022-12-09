package org.nekotori.atme.impl;

import cn.hutool.core.net.url.UrlBuilder;
import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import org.nekotori.annotations.AtMe;
import org.nekotori.atme.NoAuthAtMeResponse;

import java.nio.charset.Charset;

//@AtMe
public class QYKBotAtMeResponse extends NoAuthAtMeResponse {

    @Override
    public MessageChain response(GroupMessageEvent groupMessageEvent) {
        UrlBuilder of = UrlBuilder.of("http://api.qingyunke.com/api.php", Charset.defaultCharset());
        String build = of.addQuery("key", "free").addQuery("appid", "0").addQuery("msg", groupMessageEvent.getMessage().contentToString()).build();
        String body = HttpRequest.get(build).execute().body();
        try {
            JSONObject jsonObject = JSONUtil.parseObj(body);
            int result = jsonObject.getInt("result");
            if (result != 0) {
                return null;
            }
            String content = jsonObject.getStr("content");
            content = content.replace("菲菲", "NekoBot");
            content = content.replace("{br}", "\n");
            return new MessageChainBuilder().append(content).build();
        } catch (Exception e) {
            return null;
        }
    }
}