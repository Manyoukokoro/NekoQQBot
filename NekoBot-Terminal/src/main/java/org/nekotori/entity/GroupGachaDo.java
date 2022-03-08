package org.nekotori.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author: JayDeng
 * @date: 2022/3/3 下午3:57
 * @description: GroupGachaDo
 * @version: {@link }
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TableName("group_gacha")
public class GroupGachaDo {

    @TableId(type = IdType.AUTO)
    private Integer id;

    private Long groupId;

    private Integer urP;

    private Integer ssrP;

    private Integer srP;

    private Integer rP;

    private Integer nP;

    private Date createTime;

    private Date updateTime;

    private String pollName;

}
