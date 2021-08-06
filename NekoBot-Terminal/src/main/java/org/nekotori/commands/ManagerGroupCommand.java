package org.nekotori.commands;

import net.mamoe.mirai.contact.MemberPermission;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import org.nekotori.common.InnerConstants;

/**
 * @author: JayDeng
 * @date: 02/08/2021 16:24
 * @description:
 * @version: {@link }
 */
public abstract class ManagerGroupCommand implements Command {


  @Override
  public boolean checkAuthorization(GroupMessageEvent event) {
    if(event ==null) return false;
    if(event.getSender().getId()== InnerConstants.admin) return true;
    final MemberPermission permission = event.getSender().getPermission();
    return permission.equals(MemberPermission.ADMINISTRATOR)
        || permission.equals(MemberPermission.OWNER);
  }
}
