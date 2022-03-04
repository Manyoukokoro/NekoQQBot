package org.nekotori.utils;

import net.mamoe.mirai.event.events.GroupMessageEvent;
import org.nekotori.annotations.IsCommand;
import org.nekotori.commands.Command;
import org.nekotori.common.InnerConstants;
import org.nekotori.entity.CommandAttr;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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
        final String[] s = message.split("[ \\n]");
        List<String> collect = Arrays.stream(s).filter(single -> !single.equals("")).collect(Collectors.toList());
        if(collect.size()>0) commandAttr.setCommand(collect.get(0));
        if(collect.size()>1){
            List<String> strings = collect.subList(1,collect.size());
            commandAttr.setParam(strings);
        }

        return commandAttr;
    }

    public static boolean checkCommand(Command command, GroupMessageEvent event) {
        if(List.of(command.getClass().getAnnotation(IsCommand.class).name())
                .contains( CommandUtils.resolveCommand(event.getMessage().serializeToMiraiCode()).getCommand())){
            return true;
        }
        return false;
    }

    public static boolean isCommand(GroupMessageEvent groupMessageEvent){
        String s = groupMessageEvent.getMessage().serializeToMiraiCode();
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
        return String.join("#", commands);
    }

    public static String removeCommand(String registeredCommand, String command){
        if(!IsCommandRegistered(registeredCommand,command)) return registeredCommand;
        List<String> commands = resolveRegisteredCommand(registeredCommand);
        commands.remove(command);
        return String.join("#", commands);
    }
}
    