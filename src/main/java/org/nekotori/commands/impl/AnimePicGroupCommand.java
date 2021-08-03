package org.nekotori.commands.impl;

import cn.hutool.http.HttpUtil;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.Member;
import net.mamoe.mirai.message.data.At;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import net.mamoe.mirai.message.data.PlainText;
import net.mamoe.mirai.utils.ExternalResource;
import org.nekotori.annotations.Command;
import org.nekotori.commands.PrivilegeGroupCommand;

import java.io.IOException;
import java.io.InputStream;
import java.net.SocketTimeoutException;

/**
 * @author: JayDeng
 * @date: 02/08/2021 16:51
 * @description:
 * @version: {@link }
 */
@Command
public class AnimePicGroupCommand extends PrivilegeGroupCommand {
  @Override
  public MessageChain execute(Member sender, MessageChain messageChain, Group subject) {
    String imgUrl = "acg.yanwz.cn/api.php";
    MessageChain echo = null;
    try {
      InputStream inputStream =
          HttpUtil.createGet(imgUrl).setReadTimeout(10 * 1000).execute().bodyStream();
      echo =
          new MessageChainBuilder()
              .append(new At(sender.getId()))
              .append(subject.uploadImage(ExternalResource.create(inputStream)))
              .build();
    } catch (SocketTimeoutException e) {
      echo =
          new MessageChainBuilder()
              .append(new At(sender.getId()))
              .append(new PlainText("  time out!"))
              .build();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return echo;
  }
}
