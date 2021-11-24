package org.nekotori.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.nekotori.entity.ChatMemberDo;

/**
 * @author: JayDeng
 * @date: 25/08/2021 13:53
 * @description:
 * @version: {@link }
 */
@Mapper
public interface ChatMemberMapper {

    int insertChatMember(ChatMemberDo chatMemberDo);

    ChatMemberDo selectByMemberIdAndGroupId(@Param("groupId") Long groupId, @Param("memberId") Long orgId);

    void updateChatMember(ChatMemberDo chatMemberDo);

    void updateAllEveryDayWelcome();

}
    