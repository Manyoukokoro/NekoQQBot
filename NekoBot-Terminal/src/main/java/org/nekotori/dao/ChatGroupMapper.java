package org.nekotori.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.nekotori.entity.ChatGroupDo;

import java.util.List;

@Mapper
public interface ChatGroupMapper {

    int insertChatGroup(ChatGroupDo chatGroupDo);

    List<Long> selectRegisteredGroup();

    ChatGroupDo selectGroupById(@Param("gid") Long groupId);

    void updateChatGroup(ChatGroupDo chatGroupDo);
}
