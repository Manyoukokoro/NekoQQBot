package org.nekotori.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.nekotori.utils.DateUtil;

import java.time.LocalDate;
import java.util.Date;

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
@TableName("chat_member")
public class ChatMemberDo {

    @TableId(type = IdType.AUTO)
    private Integer id;

    private Long memberId;

    private Long groupId;

    private String nickName;

    private Boolean isBlocked;

    private String lastCommand;

    private Integer level;

    private Boolean todayWelcome;

    private Long exp;

    private Integer totalSign;

    private Date lastSign;

    private String backgroundUri;

    private Integer gold;

    public boolean checkTodaySign() {
        LocalDate localDate = DateUtil.date2LocalDate(lastSign);
        return LocalDate.now().equals(localDate);
    }
}
    