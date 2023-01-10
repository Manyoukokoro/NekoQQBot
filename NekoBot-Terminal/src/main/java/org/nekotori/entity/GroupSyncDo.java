package org.nekotori.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TableName("group_sync")
public class GroupSyncDo {

    @TableId(type = IdType.AUTO)
    private Integer id;

    private Long sourceGroupId;

    private String targetGroups;
}
