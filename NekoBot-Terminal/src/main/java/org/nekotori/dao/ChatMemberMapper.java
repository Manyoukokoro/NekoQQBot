package org.nekotori.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
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
public interface ChatMemberMapper extends BaseMapper<ChatMemberDo> {

    void updateAllEveryDayWelcome();

}
    