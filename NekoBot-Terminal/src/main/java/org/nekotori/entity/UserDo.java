package org.nekotori.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author: JayDeng
 * @date: 05/08/2021 10:08
 * @description:
 * @version: {@link }
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TableName("user")
public class UserDo {

    private Integer id;

    private Long account;

    private String nickName;

    private Boolean isBlock;

    private Integer userLevel;
}
    