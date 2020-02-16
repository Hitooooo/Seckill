package com.hito.seckill.common.exception;

import com.hito.seckill.result.CodeMsg;
import lombok.Getter;

/**
 * TODO
 *
 * @author HitoM
 * @date 2020/2/16 16:32
 **/
@Getter
public class GlobalException extends RuntimeException {
    private CodeMsg cm;

    public GlobalException(CodeMsg cm) {
        super(cm.toString());
        this.cm = cm;
    }
}
