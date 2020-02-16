package com.hito.seckill.domain.vo;

import com.hito.seckill.validator.IsMobile;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import javax.validation.constraints.NotNull;

/**
 * 登录表单
 *
 * @author HitoM
 * @date 2020/2/16 16:25
 **/
@Data
public class LoginVo {
    @NotNull
    @IsMobile
    private String mobile;

    @NotNull
    @Length(min = 6)
    private String password;
}
