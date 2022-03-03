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
 * @date: 2022/2/28 下午3:31
 * @description: CustomCommandDo
 * @version: {@link }
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TableName("custom_command")
public class CustomCommandDo {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long groupId;

    private Long creatorId;

    private Date createTime;

    private String authority;

    private String commandBody;

    private Date updateTime;

}
