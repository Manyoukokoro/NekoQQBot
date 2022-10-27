package org.nekotori.events;


import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.event.EventHandler;
import net.mamoe.mirai.event.EventPriority;
import net.mamoe.mirai.event.ListeningStatus;
import net.mamoe.mirai.event.SimpleListenerHost;
import net.mamoe.mirai.event.events.FriendMessageEvent;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import org.jetbrains.annotations.NotNull;
import org.nekotori.annotations.Event;
import org.nekotori.common.InnerConstants;
import org.nekotori.job.AsyncJob;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author: JayDeng
 * @date: 02/08/2021 14:08
 * @description:
 * @version: {@link }
 */
@Event
public class FriendMessageEvents extends SimpleListenerHost {

    @NotNull
    @EventHandler(priority = EventPriority.HIGH)
    public ListeningStatus onMessage(@NotNull FriendMessageEvent friendMessageEvent) {
        //广播信息
        if (friendMessageEvent.getFriend().getId() == InnerConstants.admin){
            if (friendMessageEvent.getMessage().contentToString().startsWith("#")) {
                friendMessageEvent.getBot().getGroups().forEach(
                        group -> group.sendMessage(friendMessageEvent.getMessage().contentToString().substring(1))
                );
            }
            else if (friendMessageEvent.getMessage().contentToString().startsWith("groups")){
                List<String> collect = friendMessageEvent.getBot().getGroups().stream().map(g-> ""+g.getId()+g.getName()).collect(Collectors.toList());

                friendMessageEvent.getFriend().sendMessage(String.join("\n",collect));
            }
            else if (friendMessageEvent.getMessage().contentToString().startsWith("sub")){
                String s = friendMessageEvent.getMessage().contentToString();
                String sub = s.replace("sub", "").trim();
                AsyncJob.nowDispatchGroup = Long.parseLong(sub);
                friendMessageEvent.getFriend().sendMessage("success");
            }else {

                if(AsyncJob.nowDispatchGroup != 0L){
                    Group groupOrFail = friendMessageEvent.getBot().getGroupOrFail(AsyncJob.nowDispatchGroup);
                    MessageChainBuilder singleMessages = new MessageChainBuilder();
                    singleMessages.addAll(friendMessageEvent.getMessage());
                    groupOrFail.sendMessage(singleMessages.build());
                }
            }
        }
        return ListeningStatus.LISTENING;
    }
}
