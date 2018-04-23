package com.laile.esf.web.controler;

import java.io.IOException;
import java.io.Writer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.laile.esf.web.util.UuidUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.ModelAndView;

public class BaseController {

    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    private static final long serialVersionUID = 6357869213649815390L;

    /**
     * 得到ModelAndView
     *
     * @return
     */
    public ModelAndView getModelAndView() {
        return new ModelAndView();
    }

    /**
     * 得到request对象
     *
     * @return
     */
    public HttpServletRequest getRequest() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
                .getRequest();
        return request;
    }

    /**
     * 获取out输出流
     * 
     * @return
     */
    public Writer getWriter() {
        HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
                .getResponse();
        Writer writer = null;
        try {
            writer = response.getWriter();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return writer;
    }

    /**
     * 得到32位的uuid
     *
     * @return
     */
    public String get32UUID() {
        return UuidUtil.get32UUID();
    }

    /**
     * 获取HTTP SESSION
     *
     * @return
     */
    public HttpSession getSession() {
        return getRequest().getSession(true);
    }

}
