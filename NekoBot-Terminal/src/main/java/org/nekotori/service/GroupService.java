package org.nekotori.service;

import org.nekotori.entity.ChatHistoryDo;

/**
 * @author: JayDeng
 * @date: 03/08/2021
 * @time: 09:29
 */
public interface GroupService {

    boolean checkPrivilege(Long groupId);

    void saveHistory(ChatHistoryDo chatHistoryDo);
}
