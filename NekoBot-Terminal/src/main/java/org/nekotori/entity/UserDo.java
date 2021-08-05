package org.nekotori.entity;

import lombok.AllArgsConstructor;
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
public class UserDo {

    private Integer id;

    private Long account;

    private String nickName;

    private Boolean isBlock;

    private Integer userLevel;
}
    