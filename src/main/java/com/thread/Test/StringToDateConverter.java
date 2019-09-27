package com.thread.Test;

import org.springframework.core.convert.converter.Converter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 字符串转换成日期
 */
public class StringToDateConverter implements Converter<String, Date> {

    @Override
    public Date convert(String source) {
        try{
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            return df.parse(source);
        }catch (Exception e){
            throw new RuntimeException("数据类型转换出现错误");
        }
    }
}
