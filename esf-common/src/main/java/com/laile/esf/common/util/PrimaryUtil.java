package com.laile.esf.common.util;

import java.util.Date;

public class PrimaryUtil {

	public static String primaryId(String prefix){
		
		return new StringBuilder(prefix)
		     .append(DateUtil.format(new Date(),DateUtil.YYYYMMDDHHMMSS))
		     .append(RandomUtil.randomStr(14)).toString();		
		
	}
}
