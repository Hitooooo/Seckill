package com.hito.seckill.domain;

import lombok.Data;

import java.util.Date;

/**
 * TODO
 *
 * @author HitoM
 * @date 2020/2/16 16:19
 **/
@Data
public class SeckillUser {
    private Long id;
    private String nickname;
    private String password;
    private String salt;
    private String head;
    private Date registerDate;
    private Date lastLoginDate;
    private Integer loginCount;
}
