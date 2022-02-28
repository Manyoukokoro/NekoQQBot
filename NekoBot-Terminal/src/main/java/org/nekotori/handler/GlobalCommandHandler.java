package org.nekotori.handler;

import lombok.extern.slf4j.Slf4j;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.message.data.MessageChain;
import org.nekotori.annotations.IsCommand;
import org.nekotori.commands.Command;
import org.nekotori.utils.CommandUtils;
import org.nekotori.utils.SpringContextUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author: JayDeng
 * @date: 02/08/2021 14:58
 * @description:
 * @version: {@link }
 */

@Component
@Slf4j
public class GlobalCommandHandler {

  private static Map<String, Command> innerCommands = new HashMap<>();

  public static void init() {
    innerCommands = SpringContextUtils.getContext().getBeansOfType(Command.class);
    log.info("注册了以下指令:{}",
            innerCommands.values().stream().map(v->v.getClass().getName()).collect(Collectors.toList()));
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
