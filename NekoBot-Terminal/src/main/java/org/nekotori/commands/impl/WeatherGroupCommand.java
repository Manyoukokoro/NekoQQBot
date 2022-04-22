package org.nekotori.commands.impl;

import cn.hutool.core.net.url.UrlBuilder;
import cn.hutool.http.HttpRequest;
import com.fasterxml.jackson.core.type.TypeReference;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.Member;
import net.mamoe.mirai.message.data.At;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import net.mamoe.mirai.message.data.PlainText;
import org.nekotori.annotations.IsCommand;
import org.nekotori.commands.NoAuthGroupCommand;
import org.nekotori.entity.*;
import org.nekotori.utils.CommandUtils;
import org.nekotori.utils.JsonUtils;
import org.springframework.beans.factory.annotation.Value;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

/**
 * @author: JayDeng
 * @date: 03/08/2021 16:14
 * @description:
 * @version: {@link }
 */
@IsCommand(name = {"查询天气", "天气"}, description = "查询天气，格式:(!/-/#)天气 [位置]")
public class WeatherGroupCommand extends NoAuthGroupCommand {


    @Value("${weather.location-api}")
    private String location;

    @Value("${weather.weather-api}")
    private String weather;

    @Value("${weather.key}")
    private String key;

    @Override
    public MessageChain execute(Member sender, MessageChain messageChain, Group subject) {
        String s = messageChain.serializeToMiraiCode();
        final CommandAttr commandAttr = CommandUtils.resolveTextCommand(s);
        final List<String> param = commandAttr.getParam();
        MessageChainBuilder singleMessages = new MessageChainBuilder().append(new At(sender.getId()));
        for (String p : param) {
            try {
                final String build =
                        UrlBuilder.of(location, StandardCharsets.UTF_8)
                                .addQuery("location", p)
                                .addQuery("key", key)
                                .build();
                final String body = HttpRequest.get(build).setReadTimeout(10 * 1000).execute().body();
                WeatherLocationResponse weatherLocationResponse =
                        JsonUtils.json2Object(body, new TypeReference<>() {
                        });
                if (weatherLocationResponse != null && weatherLocationResponse.getCode().equals("200")) {
                    final Optional<WeatherLocationData> first =
                            weatherLocationResponse.getLocation().stream().findFirst();
                    if (first.isPresent()) {
                        final String id = first.get().getId();
                        final String build1 =
                                UrlBuilder.of(weather, StandardCharsets.UTF_8)
                                        .addQuery("location", id)
                                        .addQuery("key", key)
                                        .build();
                        final String body1 = HttpRequest.get(build1).setReadTimeout(10 * 1000).execute().body();
                        final WeatherResponse weatherResponse =
                                JsonUtils.json2Object(body1, new TypeReference<>() {
                                });
                        if (weatherResponse != null && weatherResponse.getCode().equals("200")) {
                            final Optional<WeatherDailyData> first1 =
                                    weatherResponse.getDaily().stream().findFirst();
                            first1.ifPresent(
                                    (w) -> singleMessages
                                            .append(
                                                    String.valueOf(
                                                            new PlainText("\n" +
                                                                    first.get().getName()
                                                                    + "今日天气:\n"
                                                                    + "  天气:"
                                                                    + w.getTextDay()
                                                                    + "\n  最高温度:"
                                                                    + w.getTempMax()
                                                                    + "\n  最低温度:"
                                                                    + w.getTempMin()
                                                                    + "\n  降水量:"
                                                                    + w.getPrecip()
                                                                    + "毫米")))
                                            .append("\n"));
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        MessageChain build = singleMessages.build();
        if (build.size() <= 1) {
            singleMessages.append(new PlainText("\n请不要查询异次元信息"));
            build = singleMessages.build();
        }
        return build;
    }
}
