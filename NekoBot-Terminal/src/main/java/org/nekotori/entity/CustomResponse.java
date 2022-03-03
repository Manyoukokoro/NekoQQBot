package org.nekotori.entity;

import lombok.Data;

/**
 * @author: JayDeng
 * @date: 2022/3/3 上午11:37
 * @description: CustomRespose
 * @version: {@link }
 */

@Data
public class CustomResponse {

    public enum WAY{
        FULL_CONTEXT("全文"),
        CONTAINS("包含"),
        BEGIN("开头"),
        END("结尾");

        private String description;

        WAY(String description) {
            this.description = description;
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
