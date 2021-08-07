package org.nekotori.dao;

import org.apache.ibatis.annotations.Mapper;
import org.nekotori.entity.ChatHistoryDo;

@Mapper
public interface ChatHistoryMapper {
    int insertChatHistory(ChatHistoryDo chatHistoryDo);
}
