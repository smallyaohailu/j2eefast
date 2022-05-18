package com.j2eefast.common.core.shiro;

import com.j2eefast.common.core.utils.ServletUtil;
import com.j2eefast.common.core.utils.ToolUtil;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.SessionContext;
import org.apache.shiro.session.mgt.SessionFactory;
import org.apache.shiro.session.mgt.SimpleSession;
import org.apache.shiro.web.session.mgt.DefaultWebSessionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;

public class ShiroSessionFactory  implements SessionFactory {

    private static final Logger logger = LoggerFactory.getLogger(ShiroSessionFactory.class);

    @Override
    public Session createSession(SessionContext initData) {
        SimpleSession session = new SimpleSession();
        //HttpServletRequest request = (HttpServletRequest)initData.get(DefaultWebSessionContext.class.getName() + ".SERVLET_REQUEST");
        session.setHost(ServletUtil.getIp());
        return session;
    }

//    public static String getIpAddress(HttpServletRequest request) {
//        String localIP = "127.0.0.1";
//        String ip = request.getHeader("x-forwarded-for");
//        if (ToolUtil.isEmpty(ip) || (ip.equalsIgnoreCase(localIP)) || "unknown".equalsIgnoreCase(ip)) {
//            ip = request.getHeader("Proxy-Client-IP");
//        }
//        if (ToolUtil.isEmpty(ip) || (ip.equalsIgnoreCase(localIP)) || "unknown".equalsIgnoreCase(ip)) {
//            ip = request.getHeader("WL-Proxy-Client-IP");
//        }
//        if (ToolUtil.isEmpty(ip) || (ip.equalsIgnoreCase(localIP)) || "unknown".equalsIgnoreCase(ip)) {
//            ip = request.getRemoteAddr();
//        }
//        return ip;
//    }
}
