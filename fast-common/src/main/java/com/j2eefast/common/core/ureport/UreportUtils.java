/*
 * All content copyright http://www.j2eefast.com, unless 
 * otherwise indicated. All rights reserved.
 * No deletion without permission
 */
package com.j2eefast.common.core.ureport;

import cn.hutool.core.util.StrUtil;
import com.bstek.ureport.provider.report.ReportProvider;
import com.j2eefast.common.core.utils.SpringUtil;
import lombok.extern.slf4j.Slf4j;
import java.lang.reflect.Method;
import java.util.Collection;

/**
 * Ureport2表报扩展工具类
 * @author huanzhou
 */
@Slf4j
public class UreportUtils {

	/**
	 * 获取默认下载报表名称
	 * @author ZhouZhou
	 * @date 2021-09-17
	 * @param fileName
	 * @return
	 */
    public static String getDefaultName(String fileName){
        Collection<ReportProvider> reportProviders = SpringUtil.getBeansOfType(ReportProvider.class).values();
        for(ReportProvider provider:reportProviders){
            if(fileName.startsWith(provider.getPrefix())){
                try{
                    Method method= provider.getClass().getMethod("getFileName", new Class<?>[]{String.class});
                    fileName = (String) method.invoke(provider, new Object[]{fileName});
                    break;
                }catch (Exception e){
                    log.error("获取默认下载文件名失败!",e);
                }
            }
        }
        return fileName;
    }

    /**
     * 校验权限
     * @author ZhouZhou
     * @date 2021-09-17
     * @param fileName
     * @return
     */
    public static boolean isPermissions(String fileName){
        boolean flag = false;
        Collection<ReportProvider> reportProviders = SpringUtil.getBeansOfType(ReportProvider.class).values();
        for(ReportProvider provider:reportProviders){
            if(fileName.startsWith(provider.getPrefix())){
                try{
                    Method method= provider.getClass().getMethod("ishasRole", new Class<?>[]{String.class});
                    flag = (boolean) method.invoke(provider, new Object[]{fileName});
                    break;
                }catch (Exception e){
                    log.error("获取默认下载文件名失败!",e);
                }
            }
        }
        return flag;
    }

    /**
     * 开放功能性判断
     * @param fileName
     * @param func
     * @return
     */
    public static boolean isFunc(String fileName,String func){
        boolean flag = false;
        Collection<ReportProvider> reportProviders = SpringUtil.getBeansOfType(ReportProvider.class).values();
        for(ReportProvider provider:reportProviders){
            if(fileName.startsWith(provider.getPrefix())){
                try{
                    Method method= provider.getClass().getMethod("isFuncs", new Class<?>[]{String.class,String.class});
                    flag = (boolean) method.invoke(provider, new Object[]{fileName,func});
                    break;
                }catch (Exception e){
                    log.error("获取默认下载文件名失败!",e);
                }
            }
        }
        return flag;
    }

    /**
     * 获取数据过滤SQL
     * @author ZhouZhou
     * @date 2021-09-21 21:34
     * @param fileName
     * @return
     */
    public static String getSQLFilter(String fileName){
        String sqlFilter = StrUtil.EMPTY;
        Collection<ReportProvider> reportProviders = SpringUtil.getBeansOfType(ReportProvider.class).values();
        for(ReportProvider provider:reportProviders){
            if(fileName.startsWith(provider.getPrefix())){
                try{
                    Method method= provider.getClass().getMethod("getSQLFilter", new Class<?>[]{String.class});
                    sqlFilter = (String) method.invoke(provider, new Object[]{fileName});
                    break;
                }catch (Exception e){
                    log.error("获取默认下载文件名失败!",e);
                }
            }
        }

        return StrUtil.replaceIgnoreCase(sqlFilter,"'","\\'");
    }


}
