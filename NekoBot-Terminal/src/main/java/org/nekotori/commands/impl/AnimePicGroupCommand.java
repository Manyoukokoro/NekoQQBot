package org.nekotori.commands.impl;

import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.net.url.UrlBuilder;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.http.HttpUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.extern.slf4j.Slf4j;
import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.Member;
import net.mamoe.mirai.message.data.*;
import org.nekotori.commands.PrivilegeGroupCommand;
import org.nekotori.entity.CommandAttr;
import org.nekotori.entity.LoliconApiResponse;
import org.nekotori.entity.LoliconData;
import org.nekotori.utils.CommandUtils;
import org.nekotori.utils.JsonUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.CollectionUtils;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * @author: JayDeng
 * @date: 02/08/2021 16:51
 * @description:
 * @version: {@link }
 */
//@Command(name = {"色图","setu"},description = "使用loliApi检索插画图片，格式:(!/-/#)setu ...[参数]")
@Slf4j
@Deprecated
public class AnimePicGroupCommand extends PrivilegeGroupCommand {

  @Value("${img.loli-api}")
  private String loliApi;

  @Value("${img.loli-key}")
  private String loliKey;

  @Override
  public MessageChain execute(Member sender, MessageChain messageChain, Group subject) {
    String s = messageChain.serializeToMiraiCode();
    CommandAttr commandAttr = CommandUtils.resolveCommand(s);
    String keyword = CollectionUtils.isEmpty(commandAttr.getParam())?"":commandAttr.getParam().get(0);
    String imgUrl = "";
    String build =
        UrlBuilder.of(loliApi, StandardCharsets.UTF_8)
            .addQuery("apikey", loliKey)
            .addQuery("r18","0")
            .addQuery("keyword", keyword)
            .addQuery("size1200", "true")
            .build();
    MessageChain echo;
    try {
      String body = HttpUtil.createGet(build).setReadTimeout(5 * 1000).setConnectionTimeout(5 * 1000).executeAsync().body();
      LoliconApiResponse loliconApiResponse =
              JsonUtils.json2Object(body, new TypeReference<>() {
              });
      List<LoliconData> loliconData = new ArrayList<>();
      if (ObjectUtil.isNotNull(loliconApiResponse)) {
//        if (loliconApiResponse.getError().equals(429))
//          return new MessageChainBuilder()
//                  .append(new At(sender.getId()).plus(new PlainText("我的身体已经菠萝菠萝哒")))
//                  .build();
        loliconData = loliconApiResponse.getData();
      }
      if (ObjectUtil.isNull(loliconData) || loliconData.isEmpty())
        return new MessageChainBuilder()
                .append(new At(sender.getId()).plus(new PlainText("您找不到对象")))
                .build();
      Collection<Object> values = loliconData.get(0).getUrls().values();
      Optional<Object> firstUrl = values.stream().findFirst();
      imgUrl = firstUrl.orElseGet(String::new).toString();
      log.info("request img url: {}", imgUrl);
      InputStream inputStream =
              HttpUtil.createGet(imgUrl).setReadTimeout(20 * 1000).setConnectionTimeout(10 * 1000).execute().bodyStream();
      echo =
              new MessageChainBuilder()
                      .append(FlashImage.from(Contact.uploadImage(subject, inputStream)))
                      .build();
    }catch (IORuntimeException e){
      log.error(e.getMessage(),e);
      echo = new MessageChainBuilder()
              .append(new At(sender.getId()))
              .append(new PlainText("\n来到了电波到达不到的地方，请问是异次元吗"))
              .build();
    }
    return echo;
  }
}
