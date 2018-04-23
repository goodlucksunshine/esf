package com.laile.esf.common.util;

import java.util.concurrent.TimeUnit;

import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;

/**
 * @Title FileListenerUtil.java
 * @Description:文件监控工具
 * @author: liuxingmi
 * @date(创建日期)：2014-4-22 下午6:16:51
 * @company(公司)：深圳市彩讯科技有限公司
 * @version 1.0
 * @History(修改历史):
 */
public class FileListenerUtil {


	/**
	 * @Date 2014-4-22 下午6:17:29
	 * @Author liuxingmi
	 * @Description 
	 * @param adaptor 观察者实现类
	 * @param dirPath 目录路径
	 * @throws Exception void
	 */
	public static void runFileListenter(FileAlterationListenerAdaptor adaptor, String dirPath) throws Exception{
		// 轮询间隔 5 秒  
        long interval = TimeUnit.SECONDS.toMillis(10);  
        
        // 创建一个文件观察器用于处理文件的格式          
        FileAlterationObserver observer = new FileAlterationObserver(dirPath);
        
//        FileAlterationObserver observer = new FileAlterationObserver(  
//                                              rootDir,   
//                                              FileFilterUtils.and(  
//                                               FileFilterUtils.fileFileFilter(),  
//                                               FileFilterUtils.suffixFileFilter(".doc")),  //过滤文件格式  
//                                              null);  
        observer.addListener(adaptor); //设置文件变化监听器  
        
        //创建文件变化监听器  
        FileAlterationMonitor monitor = new FileAlterationMonitor(interval,observer);  
        // 开始监控  
        monitor.start();
	}	
	
	
}
