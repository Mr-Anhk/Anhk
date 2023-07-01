package com.anhk;

import java.util.Arrays;

/**
 * TODO - Maopao
 *
 * @author Anhk丶
 * @version 1.0
 * @date 2022/6/15 1:37 星期三
 */
public class Maopao {
    public static void main(String[] args) {
        int[] array = new int[]{55, 33, 22, 66, 11};
        System.out.println(array.length);
        System.out.println("排序前 -》 " + Arrays.toString(array));
        for (int i = 0; i < array.length - 1; i++) {
            for (int j = 0; j < array.length - 1 - i; j++) {
                if (array[j] > array[j + 1]) {
                    int temp = array[j];
                    array[j] = array[j + 1];
                    array[j + 1] = temp;
                }
            }
        }
        System.out.println("排序后 -》 " + Arrays.toString(array));
    }
}
