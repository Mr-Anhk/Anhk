package com.anhk.common.enums;

import io.swagger.models.auth.In;
import lombok.Getter;

import java.util.ArrayList;

/**
 * @Description: StatusEnum
 * @Author: Anhk丶
 * @Date: 2020/10/29  21:31
 * @Version: 1.0
 */
@Getter
public enum StatusEnum {
    OK(1,"是"),
    NO(0,"否");
    private Integer value;
    private String name;

    StatusEnum(Integer value, String name) {
        this.value = value;
        this.name = name;
    }

    /**
     * 根据value值获取相应的名称
     * @param value
     * @return
     */
    public static String getNameByValue(Integer value) {
        for (StatusEnum statusEnum : StatusEnum.values()) {
            if (statusEnum.getValue().equals(value)) {
                return statusEnum.getName();
            }
        }
        return null;
    }

    /**
     * 判断是否包含该值
     * @param value
     * @return
     */
    public static boolean contains(Integer value) {
        for (StatusEnum statusEnum : StatusEnum.values()) {
            if (statusEnum.getValue().equals(value)) {
                return true;
            }
        }
        return false;
    }
}
