package com.mmall.Param;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class SearchLogParam {
    private Integer type;
    private String beforeSeq;
    private String afterSeq;
    private String operator;
    private String fromTime;// yyyy-MM-dd HH:mm:ss
    private String toTime;
}
