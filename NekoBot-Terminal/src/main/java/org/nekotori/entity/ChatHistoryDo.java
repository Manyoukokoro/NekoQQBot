package org.nekotori.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatHistoryDo {

    private Integer id;

    private Long groupId;

    private Long senderId;

    private String content;

    private Date time;

    private Boolean isCommand;
}
