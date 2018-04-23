package com.laile.esf.integrate.proxy;

import java.lang.reflect.InvocationTargetException;

public abstract interface ServiceInvokingProxy
{
  public abstract Object invoke(String paramString1, String paramString2, Class<?>[] paramArrayOfClass, Object[] paramArrayOfObject)
    throws ClassNotFoundException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException;
}