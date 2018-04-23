package com.laile.esf.web.interceptor;

import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.laile.esf.common.exception.ResultCode;
import com.laile.esf.common.exception.SystemException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

/**
 * Created by sunshine on 16/8/24.
 */
public class FileCheckInterceptor extends HandlerInterceptorAdapter {
    Logger logger = LoggerFactory.getLogger(FileCheckInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpServletRequest req = (HttpServletRequest) request;
        MultipartResolver res = new org.springframework.web.multipart.commons.CommonsMultipartResolver();
        if (res.isMultipart(req)) {
            MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) req;
            Map<String, MultipartFile> files = multipartRequest.getFileMap();
            Iterator<String> iterator = files.keySet().iterator();
            while (iterator.hasNext()) {
                String formKey = (String) iterator.next();
//				System.out.println("表单key:"+formKey);
                MultipartFile multipartFile = multipartRequest.getFile(formKey);
                if (!StringUtils.isEmpty(multipartFile.getOriginalFilename())) {
                    String filename = multipartFile.getOriginalFilename();
//					System.out.println("我是文件"+multipartFile.getOriginalFilename());
                    if (checkFile(filename)) {
                        return true;
                    } else {
                        logger.error("非法的文件上传类型,请及时确定原因");
                        throw new SystemException(ResultCode.COMMON_ERROR, "非法的文件类型");
                    }
                }
            }
            return true;
        } else {
            return true;
        }
    }

    private boolean checkFile(String fileName) {
        boolean flag = false;
        String suffixList = "doc,docx,xls,xlsx,jpg,gif,png,ico,bmp,jpeg";
        //获取文件后缀
        String suffix = fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length());

        if (suffixList.contains(suffix.trim().toLowerCase())) {
            flag = true;
        }
        return flag;
    }
}
