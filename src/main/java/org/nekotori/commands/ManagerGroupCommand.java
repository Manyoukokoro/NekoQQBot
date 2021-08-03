package org.nekotori.commands;

import net.mamoe.mirai.contact.MemberPermission;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import org.nekotori.common.Constants;
import org.nekotori.utils.CommandUtils;

/**
 * @author: JayDeng
 * @date: 02/08/2021 16:24
 * @description:
 * @version: {@link }
 */
public abstract class ManagerGroupCommand implements Command {

  protected final String command;

  public ManagerGroupCommand(String command){
    this.command = command;
  }

  @Override
  public boolean checkCommand(GroupMessageEvent event) {
    if(CommandUtils.resolveCommand(event.getMessage().contentToString()).getCommand().equals(command))
      return true;
    return false;
  }

  @Override
  public boolean checkAuthorization(GroupMessageEvent event) {
    if(event ==null) return false;
    if(event.getSender().getId()== Constants.admin) return true;
    final MemberPermission permission = event.getSender().getPermission();
    return permission.equals(MemberPermission.ADMINISTRATOR)
        || permission.equals(MemberPermission.OWNER);
  }
}
