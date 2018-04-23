package com.laile.esf.web.handler;

import com.alibaba.fastjson.JSON;
import com.laile.esf.common.exception.BusinessException;
import com.laile.esf.common.exception.ResultCode;
import com.laile.esf.common.exception.SystemException;
import com.laile.esf.common.exception.TipsBusinessException;
import com.laile.esf.common.javaenum.CheckStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ui.ModelMap;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

/**
 * Created by sunshine on 16/7/25.
 */
public class CustomExceptionHandler implements HandlerExceptionResolver {
    private static final Logger logger = LoggerFactory.getLogger(CustomExceptionHandler.class);
    @Value("${isPrintSysException}")
    private String isPrintSysEx = "false";
    @Value("${isPrintBusinessErrorCode}")
    private String isPrintErrorCode = "false";

    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        ModelMap model = new ModelMap();
        String result = "failture";

        TipsInfo tips = null;
        if (ex instanceof TipsBusinessException) {
            TipsBusinessException tipE = (TipsBusinessException) ex;
            String showEx = ex.getMessage();
            if ((!"true".equalsIgnoreCase(this.isPrintErrorCode)) &&
                    (showEx.startsWith("["))) {
                showEx = showEx.substring(showEx.indexOf("]") + 1, showEx.length());
            }
            tips = new TipsInfo(tipE.getCode(), showEx, CheckStyle.ERROR.getStyle(), tipE.returnExvalue());
            logger.warn("提示异常", ex.getMessage());
        } else if ((ex instanceof BusinessException)) {
            BusinessException bEx = (BusinessException) ex;
            String showEx = ex.getMessage();
            if ((!"true".equalsIgnoreCase(this.isPrintErrorCode)) &&
                    (showEx.startsWith("["))) {
                showEx = showEx.substring(showEx.indexOf("]") + 1, showEx.length());
            }
            tips = new TipsInfo(bEx.getCode(), showEx, CheckStyle.ERROR.getStyle());
            logger.warn("业务异常", ex.getMessage());
        } else if ((ex instanceof MultipartException)) {
            result = "failture";
            tips = new TipsInfo(ResultCode.COMMON_ERROR, "上传文件失败,请重新尝试", CheckStyle.ERROR.getStyle());
            logger.warn("客户端上传文件中断", ex.getMessage());
        } else {
            String code = ResultCode.COMMON_ERROR;
            String errmsg = ex.getMessage();
            if ((ex instanceof SystemException)) {
                SystemException se = (SystemException) ex;
                code = se.getCode();
            }
            if (!"true".equalsIgnoreCase(this.isPrintSysEx)) {
                errmsg = "系统繁忙，请稍后尝试";
            }
            tips = new TipsInfo(code, errmsg, CheckStyle.ERROR.getStyle());
            logger.error("系统异常", ex);
        }
        model.put("tipsMsg", tips);
        model.put("result", result);
        String accept = request.getHeader("accept");
        if (accept != null && !(accept.indexOf("application/json") > -1
                || (request.getHeader("X-Requested-With") != null
                && request.getHeader("X-Requested-With").indexOf("XMLHttpRequest") > -1))) {
            response.setContentType("application/json;charset=UTF-8");
            PrintWriter out = null;
            try {
                out = response.getWriter();
                out.write(JSON.toJSONString(model));
                out.flush();
            } catch (IOException e) {
                logger.error("输出json异常" + e);
            } finally {
                out.close();
            }
            return null;
        }
        return new ModelAndView("/WEB-INF/500.jsp", model);
    }


    class TipsInfo {
        private String msg;
        private String code;
        private String style;
        private Map more;

        TipsInfo(String code, String msg, String style, Map more) {
            this.msg = msg;
            this.code = code;
            this.style = style;
            this.more = more;
        }

        TipsInfo(String code, String msg, String style) {
            this.msg = msg;
            this.code = code;
            this.style = style;
            this.more = more;
        }

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getStyle() {
            return style;
        }

        public void setStyle(String style) {
            this.style = style;
        }

        public Map getMore() {
            return more;
        }

        public void setMore(Map more) {
            this.more = more;
        }
    }
}
