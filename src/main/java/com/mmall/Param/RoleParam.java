package com.mmall.Param;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.beans.IntrospectionException;

@Setter
@Getter
@ToString
public class RoleParam {

    private Integer id;

    @NotBlank(message = "角色名称不可以为空")
    @Length(min = 2,max = 20,message = "角色名称长度需要在2-20之间")
    private String name;

    @Min(value = 1,message = "角色类型不合法")
    @Max(value =2,message = "角色类型不合法")
    private Integer type = 1;

    @NotNull(message = "角色状态不可以为空")
    @Min(value = 0,message = "角色状态不合法")
    @Max(value = 1,message = "角色状态不合法")
    private Integer status;

    @Length(min = 0,max = 200,message = "角色的备注长度不可以超过200个字符内")
    private String remark;
}
