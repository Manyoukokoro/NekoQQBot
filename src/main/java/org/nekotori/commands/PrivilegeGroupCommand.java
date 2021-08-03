package org.nekotori.commands;

import net.mamoe.mirai.event.events.GroupMessageEvent;
import org.nekotori.service.GroupService;
import org.nekotori.utils.CommandUtils;

import javax.annotation.Resource;

/**
 * @author: JayDeng
 * @date: 03/08/2021 09:27
 * @description:
 * @version: {@link }
 */
public abstract class PrivilegeGroupCommand implements Command {

    protected final String command;

    public PrivilegeGroupCommand(String command){
        this.command = command;
    }

    @Override
    public boolean checkCommand(GroupMessageEvent event) {
        if(CommandUtils.resolveCommand(event.getMessage().contentToString()).getCommand().equals(command))
            return true;
        return false;
    }

    @Resource
    private GroupService groupService;

    @Override
    public boolean checkAuthorization(GroupMessageEvent event) {
        final long id = event.getGroup().getId();
        return groupService.checkPrivilege(id);
    }
}
    