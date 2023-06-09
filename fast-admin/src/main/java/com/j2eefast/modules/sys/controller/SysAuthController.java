/*
 * All content copyright http://www.j2eefast.com, unless
 * otherwise indicated. All rights reserved.
 * No deletion without permission
 */
package com.j2eefast.modules.sys.controller;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.j2eefast.common.core.controller.BaseController;
import com.j2eefast.common.core.utils.AuthStateRedisCache;
import com.j2eefast.common.core.utils.ToolUtil;
import com.j2eefast.framework.shiro.token.OtherToken;
import com.j2eefast.framework.sys.entity.SysAuthUserEntity;
import com.j2eefast.framework.sys.entity.SysUserEntity;
import com.j2eefast.framework.sys.mapper.SysUserMapper;
import com.j2eefast.framework.sys.service.SysAuthUserService;
import com.j2eefast.framework.utils.Constant;
import com.j2eefast.framework.utils.DictConfig;
import com.j2eefast.framework.utils.UserUtils;
import lombok.extern.slf4j.Slf4j;
import me.zhyd.oauth.model.AuthCallback;
import me.zhyd.oauth.model.AuthResponse;
import me.zhyd.oauth.model.AuthUser;
import me.zhyd.oauth.request.AuthRequest;
import me.zhyd.oauth.utils.AuthStateUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 授权登录
 * @author zhouzhou
 * @date 2020-07-24 13:12:10
 */
@Slf4j
@Controller
public class SysAuthController extends BaseController {

    @Autowired
    private AuthStateRedisCache stateRedisCache;

    @Autowired
    private DictConfig dictConfig;

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private SysAuthUserService sysAuthUserService;

    @RequestMapping("/auth/{source}")
    @ResponseBody
    public void renderAuth(@PathVariable("source") String source) throws IOException {
        log.info("进入render：" + source);
        String dictLabel = dictConfig.getLabel("sys_oauth",source);
        if(ToolUtil.isEmpty(dictLabel)){
            super.getHttpServletResponse().sendRedirect("../404.html");
            return;
        }
        //获取租户号码
        String tenantId = super.getPara(Constant.TENANT_PARAMETER, Constant.SUPER_TENANT);
        String rememberMe = super.getPara("rememberMe", "false");
        
        JSONObject json =  JSONUtil.parseObj(dictLabel);
        AuthRequest authRequest = ToolUtil.getAuthRequest(source,
                json.getStr("clientId"),json.getStr("clientSecret"),json.getStr("redirectUri"),stateRedisCache);
        String state = AuthStateUtils.createState();
        //前端是否勾选记住我
        state = state.substring(0,state.length() - 7) +(rememberMe.equals("true")?"1":"0")+ tenantId;
        String authorizeUrl = authRequest.authorize(state);
        log.info("authorizeUrl:"+authorizeUrl);
        super.getHttpServletResponse().sendRedirect(authorizeUrl);
    }

    /**
     * oauth平台中配置的授权回调地址，以本项目为例，在创建github授权应用时的回调地址应为：http://127.0.0.1:8443/oauth/callback/github
     */
    @RequestMapping("/auth/callback/{source}")
    public ModelAndView login(@PathVariable("source") String source, AuthCallback callback, HttpServletRequest request) {
        String dictLabel = dictConfig.getLabel("sys_oauth",source);
        if(ToolUtil.isEmpty(dictLabel)){
            Map<String, Object> map = new HashMap<>(1);
            map.put("errorMsg", "50001");
            return new ModelAndView("error", map);
        }
        JSONObject json =  JSONUtil.parseObj(dictLabel);
        AuthRequest authRequest = ToolUtil.getAuthRequest(source,
                json.getStr("clientId"),json.getStr("clientSecret"),json.getStr("redirectUri"),stateRedisCache);
        @SuppressWarnings("unchecked")
		AuthResponse<AuthUser> response = authRequest.login(callback);
        System.out.println(JSONUtil.parse(response).toString());
        
        if (response.ok()) {
        	
            if(source.equals("gitee")){
                response.getData().getUuid();
            }
            
            if(UserUtils.isLogin()){
            	
                SysUserEntity user = sysUserMapper.findAuthByUuid(source+ response.getData().getUuid(), UserUtils.getTenantId());
                if(!ToolUtil.isEmpty(user)){
                    return new ModelAndView(REDIRECT+"/index#sys/user/profile/oauth2?err=5001#_sysInfo");
                }
                //若已经登录则直接绑定系统账号
                SysAuthUserEntity authUser = new SysAuthUserEntity();
                authUser.setAvatar(response.getData().getAvatar());
                authUser.setCreateTime(new Date());
                authUser.setUuid(source+ response.getData().getUuid());
                authUser.setUserId(UserUtils.getUserId());
                authUser.setNickname(response.getData().getNickname());
                authUser.setUsername(response.getData().getUsername());
                authUser.setEmail(response.getData().getEmail());
                authUser.setSource(source);
                sysAuthUserService.saveAuthUser(authUser);
                return new ModelAndView(REDIRECT+"/index#sys/user/profile/oauth2#_sysInfo");
            }
            
            String state = super.getPara("state");
        	state = state.substring(state.length() - 7);
        	String r  = state.substring(0,1);
        	String tenantId = state.substring(1);
        	boolean rememberMe = r.equals("1")?true:false;
        	
        	//生产正式环境
//        	try {
//        		 OtherToken otherToken = new OtherToken(source+ response.getData().getUuid(),tenantId,rememberMe);
//            	 //绑定用户登录
//               Subject subject = null;
//            	 subject = UserUtils.getSubject();
//               subject.login(otherToken);
//               return new ModelAndView(REDIRECT+"/index");
//        	}catch (Exception e) {
//        		Map<String, Object> map = new HashMap<>(1);
//              map.put("errorMsg", response.getMsg());
//              return new ModelAndView("404");
//			}
        	
        	//演示环境
            SysUserEntity user = sysUserMapper.findAuthByUuid(source+ response.getData().getUuid(),tenantId);
            if(!ToolUtil.isEmpty(user)){
                //绑定用户登录
       		   OtherToken otherToken = new OtherToken(source+ response.getData().getUuid(),tenantId,rememberMe);
           	   //绑定用户登录
               Subject subject = null;
           	   subject = UserUtils.getSubject();
               subject.login(otherToken); 
               return new ModelAndView(REDIRECT+"/index");
            }else{
                //游客登录 演示网站暂时定死账号登录
            	OtherToken otherToken = new OtherToken("gitee1816537","000000",rememberMe);
                Subject subject = null;
            	subject = UserUtils.getSubject();
                subject.login(otherToken);
                return new ModelAndView(REDIRECT+"/index");
            }
        }
        Map<String, Object> map = new HashMap<>(1);
        map.put("errorMsg", response.getMsg());
        return new ModelAndView("404");
    }
}
