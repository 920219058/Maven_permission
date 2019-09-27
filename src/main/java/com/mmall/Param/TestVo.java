package com.mmall.Param;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.*;
import java.util.List;

@Setter
@Getter
public class TestVo {
    @NotBlank
    private String msg;

    @Max(value=10,message = "id 不能大于10")
    @Min(value=0,message = "id不能小于0")
    @NotNull
    private Integer id;

    @NotEmpty
    private List<String> str;
}
