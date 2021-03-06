package org.nekotori.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TableName("chat_history")
public class ChatHistoryDo {

    @TableId(type = IdType.AUTO)
    private Integer id;

    private Long groupId;

    private Long senderId;

    private String content;

    private Date time;

    private Boolean isCommand;
}
