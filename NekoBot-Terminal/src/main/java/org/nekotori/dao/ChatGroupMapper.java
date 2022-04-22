package org.nekotori.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.nekotori.entity.ChatGroupDo;

@Mapper
public interface ChatGroupMapper extends BaseMapper<ChatGroupDo> {
}
