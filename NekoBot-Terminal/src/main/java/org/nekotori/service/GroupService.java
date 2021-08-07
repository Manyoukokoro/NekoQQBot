package org.nekotori.service;

import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.event.events.GroupMessageEvent;

/**
 * @author: JayDeng
 * @date: 03/08/2021
 * @time: 09:29
 */
public interface GroupService {

    boolean checkPrivilege(Long groupId,String command);

    void saveHistory(GroupMessageEvent groupMessageEvent);

    boolean IsGroupRegistered(Group group);

    int registerGroup(Group group);

    void updateGroupCommand(Long groupId, String command);

    String getGroupCommands(Long groupId);
}
