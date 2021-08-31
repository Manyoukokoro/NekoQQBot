package org.nekotori.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author: JayDeng
 * @date: 31/08/2021 09:51
 * @description:
 * @version: {@link }
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMemberDo {

    private Integer id;

    private Long memberId;

    private Long groupId;

    private String nickName;

    private Boolean isBlocked;

    private String lastCommand;

    private Integer level;

    private Boolean todayWelcome;

    private Long exp;

    private Boolean todaySign;

    private Integer totalSign;
}
    