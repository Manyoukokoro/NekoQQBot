package org.nekotori.entity;

import cn.hutool.json.JSONUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.nekotori.utils.JsonUtils;

/**
 * @author: JayDeng
 * @date: 2022/3/3 上午11:37
 * @description: CustomRespose
 * @version: {@link }
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomResponse {

    public static void main(String[] args) {
        CustomResponse dasasds = CustomResponse.builder().keyWord("1111").way(WAY.BEGIN).response("dasasds").build();
        String s = JSONUtil.toJsonStr(dasasds);
        System.out.println(s);
        CustomResponse customResponse = JSONUtil.toBean(s, CustomResponse.class);
        System.out.println(customResponse);
    }

    public enum WAY{
        FULL_CONTEXT("全文"),
        CONTAINS("包含"),
        BEGIN("开头"),
        END("结尾");

        private final String description;

        WAY(String description) {
            this.description = description;
        }

        public static WAY of(String description){
            for(WAY way:WAY.values()){
                if(way.description.equals(description)){
                    return way;
                }
            }
            return null;
        }


        @Override
        public String toString() {
            return this.description;
        }
    }

    private String keyWord;

    private WAY way;

    private String response;
}
