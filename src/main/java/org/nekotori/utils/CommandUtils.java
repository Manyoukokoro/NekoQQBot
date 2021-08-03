package org.nekotori.utils;

import org.nekotori.common.Constants;
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
        commandAttr.setHeader(Arrays.stream(Constants.commandHeader).filter(message::startsWith).findFirst().orElse(""));
        message = message.replaceFirst(commandAttr.getHeader(),"");
        final String[] s = message.split(" ");
        if(s.length>0) commandAttr.setCommand(s[0]);
        if(s.length>1) commandAttr.setParam(List.of(Arrays.copyOfRange(s,1,s.length)));
        return commandAttr;
    }
}
    