package org.nekotori.commands;

import net.mamoe.mirai.event.events.GroupMessageEvent;
import org.nekotori.service.GroupService;

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
        return groupService.checkPrivilege(id);
    }
}
    