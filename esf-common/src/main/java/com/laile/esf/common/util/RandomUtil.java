package com.laile.esf.common.util;

import java.util.Random;

/**
 * @DateTime 2014年12月22日 上午11:04:29
 * @Company 华视传媒
 * @Author 刘兴密
 * @QQ 63972012
 * @Desc 用户sid生成类
 */
public class RandomUtil {
	
	
	private static final String allChar = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
	public static final String NUM_CHAR = "0123456789";
	
	/**
	 * @DateTime 2014年12月22日 上午11:05:00
	 * @Author 刘兴密
	 * @QQ 63972012
	 * @Desc sid为MD5(32位随机数+服务器当前毫秒数 +用户账号)  
	 * @return
	 * String
	 */
	public static String getSid(String account){
		
		String rs = randomStr(32);
		rs += System.currentTimeMillis() + account;
		return MD5Util.MD5(rs) + System.currentTimeMillis();
	}
	
	public static String randomStr(int len) {
		StringBuffer buffer = new StringBuffer();
		Random random = new Random();
		for (int i = 0; i < len; i++) {
			buffer.append(allChar.charAt(random.nextInt(allChar.length())));
		}
		return buffer.toString();
	}
	
	public static String randomStr(int len, String charStr) {
		StringBuffer buffer = new StringBuffer();
		Random random = new Random();
		for (int i = 0; i < len; i++) {
			buffer.append(charStr.charAt(random.nextInt(charStr.length())));
		}
		return buffer.toString();
	}

}
