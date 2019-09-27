package com.mmall.Param;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Setter
@Getter
@ToString
public class AclParam {

    private Integer id;

    @NotBlank(message = "权限点名称不可以为空")
    @Length(min = 2,max = 20,message = "权限点名称长度需要在2-64个字符之间")
    private String name;

    @NotNull(message = "必须制定权限模块")
    private Integer aclModuleId;

    @Length(min = 6,max = 100,message = "权限点URL长度需要在6-100字符之间")
    private String url;

    @NotNull(message = "必须制定权限点的类型")
    @Min(value = 1,message = "权限点类型不合法")
    @Max(value = 3,message = "权限点类型不合法")
    private Integer type;

    @NotNull(message = "必须制定权限点的状态")
    @Min(value = 0,message = "权限点状态不合法")
    @Max(value = 1,message = "权限点状态不合法")
    private Integer status;

    @NotNull(message = "必须制定权限点的展示顺序")
    private Integer seq;

    @Length(min = 0,max = 200,message = "备注长度在0-200之间")
    private String remark;
}
