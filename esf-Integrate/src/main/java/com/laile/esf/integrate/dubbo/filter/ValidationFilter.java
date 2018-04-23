package com.laile.esf.integrate.dubbo.filter;

import java.util.Iterator;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import com.alibaba.dubbo.common.extension.Activate;
import com.alibaba.dubbo.common.utils.ConfigUtils;
import com.alibaba.dubbo.rpc.*;
import com.alibaba.dubbo.validation.Validation;
import com.alibaba.dubbo.validation.Validator;
import com.laile.esf.common.exception.ErrorMessageResource;
import com.laile.esf.common.exception.SystemErrorCodes;
import com.laile.esf.common.exception.SystemException;

@Activate(group = { "provider" }, value = { "validation" }, after = { "exception" })
public class ValidationFilter implements Filter {
    private Validation validation;

    public void setValidation(Validation validation) {
        this.validation = validation;
    }

    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        if ((this.validation != null) && (!invocation.getMethodName().startsWith("$")) && (ConfigUtils
                .isNotEmpty(invoker.getUrl().getMethodParameter(invocation.getMethodName(), "validation")))) {
            try {
                Validator validator = this.validation.getValidator(invoker.getUrl());
                if (validator != null) {
                    validator.validate(invocation.getMethodName(), invocation.getParameterTypes(),
                            invocation.getArguments());
                }
            } catch (ConstraintViolationException ce) {
                Set<ConstraintViolation<?>> violations = ce.getConstraintViolations();
                Iterator i$ = violations.iterator();
                if (i$.hasNext()) {
                    ConstraintViolation<?> violation = (ConstraintViolation) i$.next();
                    String template = violation.getMessageTemplate();
                    if ((template != null) && (template.equals(violation.getMessage()))) {
                        String msg = ErrorMessageResource.getInstance().getMessage(template);
                        return new RpcResult(new SystemException(SystemErrorCodes.INVALID_PARAM_VALUE2,
                                new Object[] { msg, template }));
                    }
                    return new RpcResult(new SystemException(SystemErrorCodes.INVALID_PARAM_VALUE,
                            new Object[] { violation.getRootBeanClass().getName(), violation.getPropertyPath(),
                                    violation.getMessage(), ce }));
                }

            } catch (RpcException e) {
                throw e;
            } catch (Throwable t) {
                throw new RpcException(t.getMessage(), t);
            }
        }
        return invoker.invoke(invocation);
    }
}