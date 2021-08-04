package org.nekotori.commands;

import net.mamoe.mirai.event.events.GroupMessageEvent;
import org.nekotori.utils.CommandUtils;

/**
 * @author: JayDeng
 * @date: 02/08/2021 15:01
 * @description:
 * @version: {@link }
 */

public abstract class NoAuthGroupCommand implements Command {

    protected final String command;

    public NoAuthGroupCommand(String command){
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
        return true;
    }

}
    