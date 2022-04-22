package org.nekotori.commands;

import net.mamoe.mirai.event.events.GroupMessageEvent;

/**
 * @author: JayDeng
 * @date: 02/08/2021 15:01
 * @description:
 * @version: {@link }
 */

public abstract class NoAuthGroupCommand implements Command {


    @Override
    public boolean checkAuthorization(GroupMessageEvent event) {
        return true;
    }

}
    