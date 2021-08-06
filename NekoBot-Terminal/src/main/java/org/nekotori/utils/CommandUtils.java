package org.nekotori.utils;

import net.mamoe.mirai.event.events.GroupMessageEvent;
import org.nekotori.commands.Command;
import org.nekotori.common.InnerConstants;
import org.nekotori.entity.CommandAttr;

import java.util.Arrays;
import java.util.List;

/**
 * @author: JayDeng
 * @date: 03/08/2021 16:15
 * @description:
 * @version: {@link }
 */
public class CommandUtils {

    public static CommandAttr resolveCommand(String message){
        CommandAttr commandAttr = new CommandAttr();
        commandAttr.setHeader(Arrays.stream(InnerConstants.commandHeader).filter(message::startsWith).findFirst().orElse(""));
        message = message.replaceFirst(commandAttr.getHeader(),"");
        final String[] s = message.split(" ");
        if(s.length>0) commandAttr.setCommand(s[0]);
        if(s.length>1) commandAttr.setParam(List.of(Arrays.copyOfRange(s,1,s.length)));
        return commandAttr;
    }

    public static boolean checkCommand(Command command, GroupMessageEvent event) {
        if(List.of(command.getClass().getAnnotation(org.nekotori.annotations.Command.class).name())
                .contains( CommandUtils.resolveCommand(event.getMessage().contentToString()).getCommand())){
            return true;
        }
        return false;
    }
}
    