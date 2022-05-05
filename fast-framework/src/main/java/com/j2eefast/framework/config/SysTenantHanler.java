/*
 * All content copyright http://www.j2eefast.com, unless
 * otherwise indicated. All rights reserved.
 * No deletion without permission
 */
package com.j2eefast.framework.config;

import com.j2eefast.common.core.datasources.CustomTenantLineHandler;
import com.j2eefast.common.core.utils.ServletUtil;
import com.j2eefast.common.core.utils.ToolUtil;
import com.j2eefast.framework.utils.UserUtils;
import lombok.extern.slf4j.Slf4j;

/**
 * @author huanzhou
 */
@Slf4j
public class SysTenantHanler extends CustomTenantLineHandler {

    @Override
    public  String getSysTenantId(){

        try{

            //获取系统租户号
            if(UserUtils.isLogin()){
                return UserUtils.getTenantId();
            }

            String tenantId = ServletUtil.getParameter("tenantId");
            if(ToolUtil.isNotEmpty(tenantId)){
                return  tenantId;
            }

            tenantId = (String) ServletUtil.getRequest().getAttribute("tenantId");
            if(ToolUtil.isNotEmpty(tenantId)){
                return  tenantId;
            }

            tenantId = ServletUtil.getRequest().getHeader("tenantId");
            if(ToolUtil.isNotEmpty(tenantId)){
                return  tenantId;
            }

        }catch (Exception e){
            log.error("异常",e);
        }
        log.error("获取租户异常");
        return "000000";
    }
}
