package org.nekotori.commands;

import net.mamoe.mirai.event.events.GroupMessageEvent;
import org.nekotori.common.Constants;
import org.nekotori.utils.CommandUtils;
import org.nekotori.utils.SpringContextUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author: JayDeng
 * @date: 02/08/2021 14:58
 * @description:
 * @version: {@link }
 */
public class GlobalCommandHandler {

  private static final ExecutorService service = Executors.newFixedThreadPool(20);

  private Map<String, Command> innerCommands = new HashMap<>();

  public void init() {
    this.innerCommands = SpringContextUtils.getContext().getBeansOfType(Command.class);
  }

  public void handle(GroupMessageEvent groupMessageEvent) {
    final String s = groupMessageEvent.getMessage().contentToString();
    for (String c : Constants.commandHeader) {
      if (s.startsWith(c)) {
        for (Command command : innerCommands.values()) {
          if (command.checkAuthorization(groupMessageEvent) && CommandUtils.checkCommand(command,groupMessageEvent)) {
            service.execute(
                () -> groupMessageEvent
                    .getGroup()
                    .sendMessage(command.execute(
                            groupMessageEvent.getSender(),
                            groupMessageEvent.getMessage(),
                            groupMessageEvent.getGroup())));
          }
        }
      }
    }
  }
}
