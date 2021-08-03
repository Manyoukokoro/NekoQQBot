package org.nekotori.commands;

import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.Member;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.message.data.MessageChain;

/**
 * @author: JayDeng
 * @date: 02/08/2021
 * @time: 14:59
 */
public interface Command {

  /**
   * 检查是否拥有执行指令的权限
   *
   * @return
   */
  boolean checkAuthorization(GroupMessageEvent event);

  /** 执行指令 */
  MessageChain execute(Member sender, MessageChain messageChain, Group subject);
}
