package org.nekotori.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.nekotori.entity.UserDo;

/**
 * @author: JayDeng
 * @date: 05/08/2021 10:11
 * @description:
 * @version: {@link }
 */
@Mapper
public interface UserMapper extends BaseMapper<UserDo> {
}
    