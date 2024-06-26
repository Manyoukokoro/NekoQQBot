package org.nekotori.commands;

import net.mamoe.mirai.event.events.GroupMessageEvent;
import org.nekotori.entity.CommandAttr;
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
    @Resource
    private GroupService groupService;

    @Override
    public boolean checkAuthorization(GroupMessageEvent event) {
        final long id = event.getGroup().getId();
        String s = CommandUtils.transMessageEventToText(event);
        CommandAttr commandAttr = CommandUtils.resolveTextCommand(s);
        return groupService.checkPrivilege(id, commandAttr.getCommand());
    }
}
    