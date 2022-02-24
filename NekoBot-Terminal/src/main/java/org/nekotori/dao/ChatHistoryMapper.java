package org.nekotori.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.nekotori.entity.ChatHistoryDo;

@Mapper
public interface ChatHistoryMapper extends BaseMapper<ChatHistoryDo> {
}
