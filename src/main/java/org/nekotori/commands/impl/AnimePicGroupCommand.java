package org.nekotori.commands.impl;

import cn.hutool.core.img.Img;
import cn.hutool.core.net.url.UrlBuilder;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.http.HttpUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.Member;
import net.mamoe.mirai.message.data.*;
import net.mamoe.mirai.utils.ExternalResource;
import org.nekotori.annotations.Command;
import org.nekotori.commands.PrivilegeGroupCommand;
import org.nekotori.common.MessageConstants;
import org.nekotori.entity.LoliconApiResponse;
import org.nekotori.entity.LoliconData;
import org.nekotori.utils.JsonUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * @author: JayDeng
 * @date: 02/08/2021 16:51
 * @description:
 * @version: {@link }
 */
@Command
public class AnimePicGroupCommand extends PrivilegeGroupCommand {

  public AnimePicGroupCommand() {
    super("");
  }

  @Override
  public MessageChain execute(Member sender, MessageChain messageChain, Group subject) {
    String s = messageChain.contentToString();
    if (!s.contains(MessageConstants.ANIME_PIC)) {
      return null;
    }
    String imgUrl = "";
    String build =
        UrlBuilder.of("https://api.lolicon.app/setu/", StandardCharsets.UTF_8)
            .addQuery("apikey", "451300156108adca112029")
            .addQuery("r18", "0")
//            .addQuery("keyword", args.get(0))
            .addQuery("size1200", "true")
            .build();
    MessageChain echo = null;
    try {
      String body = HttpUtil.createGet(build).setReadTimeout(5 * 1000).executeAsync().body();
      LoliconApiResponse loliconApiResponse =
          JsonUtils.json2Object(body, new TypeReference<>() {
          });
      List<LoliconData> loliconData = new ArrayList<>();
      if (ObjectUtil.isNotNull(loliconApiResponse)) {
        if (loliconApiResponse.getCode().equals(429))
          return new MessageChainBuilder()
              .append(new At(sender.getId()).plus(new PlainText("今日Api300次调用已耗尽")))
              .build();
        loliconData = loliconApiResponse.getData();
      }
      if (ObjectUtil.isNull(loliconData) || loliconData.isEmpty())
        return new MessageChainBuilder()
            .append(new At(sender.getId()).plus(new PlainText("找不到对象")))
            .build();
      imgUrl = loliconData.get(0).getUrl();
      InputStream inputStream =
          HttpUtil.createGet(imgUrl).setReadTimeout(10 * 1000).execute().bodyStream();
      echo =
          new MessageChainBuilder()
              .append(new At(sender.getId()))
              .append(FlashImage.from(subject.uploadImage(ExternalResource.create(inputStream))))
              .build();
    } catch (SocketTimeoutException e) {
      echo =
          new MessageChainBuilder()
              .append(new At(sender.getId()))
              .append(new PlainText("  time out! 请尝试自行访问:"+imgUrl))
              .build();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return echo;
  }
}
