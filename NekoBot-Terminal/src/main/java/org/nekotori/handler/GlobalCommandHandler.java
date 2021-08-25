package org.nekotori.handler;

import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.message.data.MessageChain;
import org.nekotori.commands.Command;
import org.nekotori.utils.CommandUtils;
import org.nekotori.utils.SpringContextUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

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
            ThreadSingleton.run(
                () -> {
                    MessageChain execute = command.execute(
                            groupMessageEvent.getSender(),
                            groupMessageEvent.getMessage(),
                            groupMessageEvent.getGroup());
                    if(!ObjectUtils.isEmpty(execute)){
                        groupMessageEvent
                                .getGroup()
                                .sendMessage(execute);
                    }
                });
          }
      }
  }

}
