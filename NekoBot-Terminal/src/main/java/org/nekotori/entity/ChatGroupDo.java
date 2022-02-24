package org.nekotori.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TableName("chat_group")
public class ChatGroupDo {

    private Integer id;

    private Long groupId;

    private String groupName;

    private String commands;

    private Integer groupLevel;

    private Boolean isBlock;
}
