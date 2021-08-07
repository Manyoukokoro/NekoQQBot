package org.nekotori.commands;

import net.mamoe.mirai.event.events.GroupMessageEvent;
import org.nekotori.common.InnerConstants;
import org.nekotori.utils.CommandUtils;
import org.nekotori.utils.SpringContextUtils;
import org.springframework.stereotype.Component;

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

@Component
public class GlobalCommandHandler {

  private static final ExecutorService service = Executors.newFixedThreadPool(20);

  private static Map<String, Command> innerCommands = new HashMap<>();

  public static void init() {
    innerCommands = SpringContextUtils.getContext().getBeansOfType(Command.class);
  }

  public void handle(GroupMessageEvent groupMessageEvent) {
      if(!CommandUtils.isCommand(groupMessageEvent)) {
        return;
      }
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
