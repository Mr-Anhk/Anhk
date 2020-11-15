package com.anhk.common.utils;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @ClassName: DownLoadUtil
 * @Description TODO 下载工具类
 * @Author: Anhk丶
 * @Date: 2020/10/29  21:54
 * @Version: 1.0
 */
public class DownLoadUtil {

    /**
     * 下载文件
     *
     * @param byteArrayOutputStream 文件的字节输出流
     * @param returnName            文件名
     * @param response
     * @throws IOException
     */
    public static void download(ByteArrayOutputStream byteArrayOutputStream, String returnName, HttpServletResponse response) throws IOException {
        response.setContentType("application/octet-stream;charset=utf-8");
        //保存的文件名必须和页面编码一致，否则乱码
        returnName = response.encodeURL(new String(returnName.getBytes(), "iso8859-1"));
        response.addHeader("Content-Disposition", "attachment;filename=" + returnName);
        response.setContentLength(byteArrayOutputStream.size());
        //获取输出流
        ServletOutputStream outputStream = response.getOutputStream();
        //写进输出流
        byteArrayOutputStream.writeTo(outputStream);
        //刷新数据
        byteArrayOutputStream.close();
        outputStream.flush();
    }
}

