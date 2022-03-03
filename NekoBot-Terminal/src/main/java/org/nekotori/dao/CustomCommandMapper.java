package org.nekotori.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.nekotori.entity.CustomCommandDo;

/**
 * @author: JayDeng
 * @date: 2022/2/28 下午3:33
 * @description: CustomCommandMapper
 * @version: {@link }
 */

@Mapper
public interface CustomCommandMapper extends BaseMapper<CustomCommandDo> {
}
