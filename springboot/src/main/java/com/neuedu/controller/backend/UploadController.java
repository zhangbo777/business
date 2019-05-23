package com.neuedu.controller.backend;


import com.neuedu.common.ResponseCode;
import com.neuedu.common.ServerResponse;
import com.neuedu.vo.ImageVo;
import org.apache.catalina.Server;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.lang.invoke.MutableCallSite;
import java.util.UUID;

@Controller
@RequestMapping("/manage/")
public class UploadController {

    @Value("${business.imageHost}")
    private String imageHost;
    @GetMapping("/upload")
    public String upload(){
        return "upload";
    }

    @PostMapping("/upload")
    @ResponseBody
    public ServerResponse upload(@RequestParam("uploadfile")MultipartFile uploadfile){

        if(uploadfile==null||uploadfile.getOriginalFilename().equals("")){
            return ServerResponse.ServerResponsecreateByFail(ResponseCode.PICTURE_NOT_NULL,"图片必须上传");
        }
        //获取上传图片的名称
        String oldFileName=uploadfile.getOriginalFilename();
        //获取文件扩展名
        String extendName=oldFileName.substring(oldFileName.lastIndexOf("."));
        //生成新的文件名
        String newFileName= UUID.randomUUID().toString()+extendName;


        File mkdir =new File("F:/upload");
        if(!mkdir.exists()){
            mkdir.mkdirs();
        }
        File newFile=new File(mkdir,newFileName);
        try {
            uploadfile.transferTo(newFile);
            ImageVo imageVo=new ImageVo(newFileName,imageHost+newFileName);
            return ServerResponse.ServerResponsecreateBySucess("成功",imageVo);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ServerResponse.ServerResponsecreateByFail(ResponseCode.ERROR,"失败");
    }


}
