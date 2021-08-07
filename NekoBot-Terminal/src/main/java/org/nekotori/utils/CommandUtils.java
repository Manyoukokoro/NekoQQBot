package org.nekotori.utils;

import net.mamoe.mirai.event.events.GroupMessageEvent;
import org.nekotori.commands.Command;
import org.nekotori.common.InnerConstants;
import org.nekotori.entity.CommandAttr;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
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
        message = message.replace("["," ");
        message = message.replace("]","");
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

    public static boolean isCommand(GroupMessageEvent groupMessageEvent){
        String s = groupMessageEvent.getMessage().contentToString();
        for (String c : InnerConstants.commandHeader) {
            if(s.startsWith(c))return true;
        }
        return false;
    }

    public static List<String> resolveRegisteredCommand(String command){
        if(StringUtils.isEmpty(command))return new ArrayList<>();
        String[] commands = command.trim().split("#");
        List<String> strings = Arrays.asList(commands);
        return new ArrayList<>(strings);
    }

    public static boolean IsCommandRegistered(String registeredCommand, String command){
        List<String> commands = resolveRegisteredCommand(registeredCommand);
        for(String c:commands){
            if(c.equals(command)){
                return true;
            }
        }
        return false;
    }

    public static String addCommand(String registeredCommand, String command){
        if(IsCommandRegistered(registeredCommand,command)) return registeredCommand;
        List<String> commands = resolveRegisteredCommand(registeredCommand);
        commands.add(command);
        String join = String.join("#", commands);
        return join;
    }

    public static String removeCommand(String registeredCommand, String command){
        if(!IsCommandRegistered(registeredCommand,command)) return registeredCommand;
        List<String> commands = resolveRegisteredCommand(registeredCommand);
        commands.remove(command);
        String join = String.join("#", commands);
        return join;
    }
}
    