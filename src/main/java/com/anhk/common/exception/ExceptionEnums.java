package com.anhk.common.exception;

/**
 * TODO - anhk
 *
 * @author Anhk丶
 * @version 1.0
 * @mail Anhk_Fei@163.com
 * @date 2021/9/25 18:47 星期六
 */
public enum ExceptionEnums {
    ;

    private int status;
    private String message;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
