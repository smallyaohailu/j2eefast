package com.j2eefast.common.core.shiro;

import com.j2eefast.common.core.utils.ToolUtil;
import org.apache.shiro.session.InvalidSessionException;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.UnknownSessionException;
import org.apache.shiro.session.mgt.SessionContext;
import org.apache.shiro.session.mgt.SessionKey;
import org.apache.shiro.web.servlet.ShiroHttpServletRequest;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.apache.shiro.web.session.mgt.WebSessionKey;
import org.apache.shiro.web.util.WebUtils;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;

/**
 * @author: wangsaichao/J2eeFAST
 * @description: 解决单次请求需要多次访问redis, 降低Session更新的频率
 * 支持sId访问
 */
public class ShiroSessionManager extends DefaultWebSessionManager {

    private static final long DEFAULT_SESSION_IN_MEMORY_TIMEOUT = 1000L * 60 * 3;
    private static final String HEADER_SESSION_NAME = "auth-token";
    private static final String HEADER_TAG = "__ajax";
    private static final String HEADER_JSON = "json";

    public ShiroSessionManager(){
    }

    @Override
    protected Session createExposedSession(Session session, SessionContext context) {
        return super.createExposedSession(session, context);
    }

    @Override
    protected Session createExposedSession(Session session, SessionKey key) {
        return super.createExposedSession(session, key);
    }


    @Override
    public Serializable getSessionId(SessionKey key) {
        ServletRequest servletRequest = WebUtils.getRequest(key);
        String tag  = ((HttpServletRequest) servletRequest).getHeader(HEADER_TAG);
        if(ToolUtil.isNotEmpty(tag) && tag.equals(HEADER_JSON)){
            ServletResponse response = WebUtils.getResponse(key);
            Serializable id = getHeaderSessionId(servletRequest, response);
            if(id == null){
                id = super.getSessionId(key);
            }
            return id;
        }
        Serializable id = super.getSessionId(key);
        return id;
    }

    protected Serializable getHeaderSessionId(ServletRequest request, ServletResponse response) {
        String id= getHeaderSessionId((HttpServletRequest)request);
        if (id != null) {
            request.setAttribute(ShiroHttpServletRequest.REFERENCED_SESSION_ID, id);
            request.setAttribute(ShiroHttpServletRequest.REFERENCED_SESSION_ID_IS_VALID, Boolean.TRUE);
        }
        request.setAttribute(ShiroHttpServletRequest.SESSION_ID_URL_REWRITING_ENABLED, isSessionIdUrlRewritingEnabled());
        return id;
    }

    /**
     * 获取请求的token
     */
    private String getHeaderSessionId(HttpServletRequest httpRequest){
        //从header中获取session
        String sessionId = httpRequest.getHeader(HEADER_SESSION_NAME);
        return sessionId;
    }

    /**
     * 获取session
     * 优化单次请求需要多次访问redis的问题
     * @param sessionKey
     * @return
     * @throws UnknownSessionException
     */
    @Override
    protected Session retrieveSession(SessionKey sessionKey) throws UnknownSessionException {
        Serializable sessionId = getSessionId(sessionKey);
        ServletRequest request = null;
        if (sessionKey instanceof WebSessionKey) {
            request = ((WebSessionKey) sessionKey).getServletRequest();
        }
        //尝试从request 获取
        if (request != null && null != sessionId) {
            Object sessionObj = request.getAttribute(sessionId.toString());
            if (sessionObj != null) {
                return (Session) sessionObj;
            }
        }

        //从缓存获取
        Session session = super.retrieveSession(sessionKey);

        if (request != null && null != sessionId) {
            //保存
            request.setAttribute(sessionId.toString(), session);
        }
        return session;
    }

    /**
     * 重写touch()方法，降低Session更新的频率
     * @param key
     * @throws InvalidSessionException
     */
    @Override
    public void touch(SessionKey key) throws InvalidSessionException {
        Session session = doGetSession(key);
        if (session != null) {
            long oldTime = session.getLastAccessTime().getTime();
            //更新访问时间
            session.touch();
            long newTime = session.getLastAccessTime().getTime();
            // 如果两次访问的时间间隔大于3分钟，主动持久化Session
            if (newTime - oldTime > DEFAULT_SESSION_IN_MEMORY_TIMEOUT) {
                onChange(session);
            }
        }
    }
}