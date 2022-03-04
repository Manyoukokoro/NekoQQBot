package org.nekotori.atme;

import net.mamoe.mirai.event.events.GroupMessageEvent;

public abstract class NoAuthAtMeResponse implements AtMeResponse {
    public NoAuthAtMeResponse() {
    }

    public boolean checkAuthorization(GroupMessageEvent groupMessageEvent) {
        return true;
    }
}
