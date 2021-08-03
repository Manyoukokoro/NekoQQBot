package org.nekotori.commands;

import net.mamoe.mirai.contact.MemberPermission;
import net.mamoe.mirai.event.events.GroupMessageEvent;

/**
 * @author: JayDeng
 * @date: 02/08/2021 16:24
 * @description:
 * @version: {@link }
 */
public abstract class ManagerGroupCommand implements Command {
  @Override
  public boolean checkAuthorization(GroupMessageEvent event) {
    if (event != null)
      return event
              .getSender()
              .getPermission()
              .equals(MemberPermission.ADMINISTRATOR)
          || event.getSender().getPermission().equals(MemberPermission.OWNER);
    return false;
  }
}
