 package com.laile.esf.integrate.exception;
 
 
 
 public class ServiceCreateException
   extends RuntimeException
 {
   private static final long serialVersionUID = 1394531186786239311L;
   
 
 
   public ServiceCreateException() {}
   
 
   public ServiceCreateException(String message, Throwable cause)
   {
     super(message, cause);
   }
   
 
 
   public ServiceCreateException(String message)
   {
     super(message);
   }
   
 
 
   public ServiceCreateException(Throwable cause)
   {
     super(cause);
   }
 }