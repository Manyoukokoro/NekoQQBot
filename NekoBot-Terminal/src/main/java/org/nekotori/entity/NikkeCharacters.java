package org.nekotori.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("nikke_characters")
public class NikkeCharacters {

    @TableId(type = IdType.AUTO)
    private Integer Id;

    private String name;

    private String slug;

    private String rarity;

    public Integer getId(){
        return this.Id;
    }

    public void setId(Integer id){
        this.Id = id;
    }
}
