package com.diy.computer.controller;

import com.diy.computer.common.R;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/common")
public class CommonController {
    @Value("${diy.path}")
    private String basePath;

    /**
     * 上传文件，file必须为file
     *
     * @param file
     * @return
     */
    @PostMapping("/upload")
    public R<String> upload(MultipartFile file) throws IOException {
//        原文件名
        String originalFilename=file.getOriginalFilename();
//        生产随机文件名
        String name=UUID.randomUUID().toString();
//        截取后缀
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
//        拼接
        String fileName=name+suffix;

        File dir = new File(basePath);
        if (!dir.exists()) {
//           目录不存在
            dir.mkdir();
        }

//        file是一个临时文件，需要转存到指定位置
        file.transferTo(new File(basePath+fileName));
        return R.success(fileName);
    }

    /**
     * 文件下载
     *
     * @param name
     * @param response
     */
    @GetMapping("/download")
    public void downLoad(String name, HttpServletResponse response) {
        try {
            //        输入流，读取文件内容
            FileInputStream fileInputStream = new FileInputStream(new File(basePath + name));
            //        输出流，将文件写会浏览器
            ServletOutputStream outputStream=response.getOutputStream();
//            设置文件 类型
            response.setContentType("image/jpeg");

            int len=0;
            byte[] bytes = new byte[1024];
            while ((len = fileInputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, len);
//                刷新
                outputStream.flush();
            }
            outputStream.close();
            fileInputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
