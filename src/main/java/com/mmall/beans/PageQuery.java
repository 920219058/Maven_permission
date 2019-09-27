package com.mmall.beans;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Min;

public class PageQuery {
    @Setter
    @Getter
    @Min(value = 1,message = "当前页面不合法")
    private int pageNo;

    @Setter
    @Getter
    @Min(value = 1, message = "每页展示数量不合法")
    private int pageSize;

    @Setter
    private int offset;

    public int getOffset(){
        return (pageNo -1) * pageSize;
    }
}
