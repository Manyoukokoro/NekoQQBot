package org.nekotori.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.nekotori.entity.ChatGroupDo;

import java.util.List;

@Mapper
public interface ChatGroupMapper extends BaseMapper<ChatGroupDo> {
}
