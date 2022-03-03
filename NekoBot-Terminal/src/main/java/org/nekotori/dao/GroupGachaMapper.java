package org.nekotori.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.nekotori.entity.ChatGroupDo;
import org.nekotori.entity.GroupGachaDo;

@Mapper
public interface GroupGachaMapper extends BaseMapper<GroupGachaDo> {
}
