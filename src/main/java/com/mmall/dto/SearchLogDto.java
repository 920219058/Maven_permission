package com.mmall.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Setter
@Getter
@ToString
public class SearchLogDto {

    private Integer type; // LogType

    private String beforeSeq;

    private String beforeSeg;

    private String afterSeq;

    private String operator;

    private Date fromTime; // yyyy-MM-dd HH:mm:ss

    private Date toTime; // yyyy-MM-dd HH:mm:ss
}
