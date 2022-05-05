/**
 * Copyright (c) 2016-Now http://www.j2eefast.com All rights reserved.
 * No deletion without permission
 */
package com.j2eefast.framework.shiro.service;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.date.DateUtil;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.support.DefaultSubjectContext;

import com.anji.captcha.model.vo.CaptchaVO;
import com.anji.captcha.service.CaptchaService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.j2eefast.common.core.enums.LogType;
import com.j2eefast.common.core.enums.LoginType;
import com.j2eefast.common.core.auth.AuthService;
import com.j2eefast.common.core.base.entity.LoginUserEntity;
import com.j2eefast.common.core.constants.ConfigConstant;
import com.j2eefast.common.core.crypto.SoftEncryption;
import com.j2eefast.common.core.utils.*;
import com.j2eefast.framework.log.entity.SysLoginInfoEntity;
import com.j2eefast.framework.sys.constant.factory.ConstantFactory;
import com.j2eefast.framework.sys.entity.*;
import com.j2eefast.framework.sys.factory.UserFactory;
import com.j2eefast.framework.sys.mapper.*;
import com.j2eefast.framework.utils.UserUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import cn.hutool.core.util.HexUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import com.j2eefast.common.core.exception.RxcException;
import com.j2eefast.common.core.exception.ServiceException;
import com.j2eefast.common.core.manager.AsyncManager;
import com.j2eefast.common.core.shiro.RedisSessionDAO;
import com.j2eefast.framework.manager.factory.AsyncFactory;
import com.j2eefast.framework.utils.Constant;
import com.j2eefast.framework.utils.RedisKeys;
import javax.annotation.Resource;
import java.io.Serializable;
import java.util.*;

/**
 *
 * @Description:登陆验证
 * @author zhouzhou loveingowp@163.com
 * @time 2019-04-09 15:10
 * @version V1.0
 *
 */
@Component
@Slf4j
public class SysLoginService implements AuthService {

	public static final String DEFAULT_KICKOUT_CACHE_KEY_PREFIX = "shiro:cache:kickout:";
	private String keyPrefix = DEFAULT_KICKOUT_CACHE_KEY_PREFIX;

	@Resource
	private RedisUtil redisUtil;

	@Resource
	private SysUserMapper sysUserMapper;

	@Resource
	private SysRoleMapper sysRoleMapper;

	@Resource
	private SysMenuMapper sysMenuMapper;

	@Resource
	private SysTenantMapper sysTenantMapper;

	@Resource
	private CaptchaService captchaService;

	@Resource
	private RedisSessionDAO sessionDAO;

	/**
	 * 系统是否开启多租户模式
	 */
	@Value("#{ @environment['fast.tenantModel.enabled'] ?: false }")
	private boolean enabled;

	@Value("#{ @environment['shiro.session.kickoutAfter'] ?: false }")
	private boolean kickoutAfter;

	@Override
	public Integer loginBeforeVerify(String username, String password) {
		//获取登陆错误次数
		Integer number = redisUtil.get(RedisKeys.getUserLoginKey(username),Integer.class);

		//用户错误次数大于设定数值直接拒绝
		if( ToolUtil.isNotEmpty(number) && number >= Global.getLoginMaxCount()) {
			AsyncManager.me().execute(AsyncFactory.recordLogininfor(username,-1L,-1L, "50003","账户被锁定,"+Global.getLockTime()+" 分钟后解锁!"));
			throw new RxcException(ToolUtil.message("sys.login.failedNumLock",Global.getLockTime()),"50003");
		}

		//获取验证码
		String captcha = ServletUtil.getRequest().getParameter("captcha");
		boolean verityFlag = (number != null  && number >= Global.getLoginNumCode()) || ToolUtil.isNotEmpty(captcha);

		//如果错误次数大于设定次数必须验证 验证码
		if(verityFlag){
			//获取系统验证码
			String sysCaptcha = UserUtils.getKaptcha(Constant.KAPTCHA_SESSION_KEY);
			if (ToolUtil.isEmpty(captcha) || !captcha.equalsIgnoreCase(sysCaptcha)) {
				throw new RxcException(ToolUtil.message("sys.login.code.error"),"50004");
			}
		}

		//租户是否开启
		if(enabled){
			String tenantId = ServletUtil.getRequest().getParameter("tenantId");
			if(ToolUtil.isEmpty(tenantId)){
				throw new RxcException(ToolUtil.message("sys.login.tenant.error"),"50004");
			}
			SysTenantEntity sysTenantEntity = sysTenantMapper.findSysTenantByIdTenantId(tenantId);
			if(ToolUtil.isEmpty(sysTenantEntity)){
				throw new RxcException(ToolUtil.message("sys.login.tenant.error"),"50004");
			}
			if(sysTenantEntity.getStatus().equals("1")){
				throw new RxcException(ToolUtil.message("sys.login.tenant.error"),"50004");
			}
		}

		//用户名或者密码为空
		if(StrUtil.isBlankOrUndefined(username) || StrUtil.isBlankOrUndefined(password) ) {
			AsyncManager.me().execute(AsyncFactory.recordLogininfor(username,-1L,-1L, "50005","账号或密码错误,请重试."));
			throw new RxcException(ToolUtil.message("sys.login.failure"),"50005");
		}

		return number;
	}

	/**
	 * 验证登陆
	 * @param username
	 * @param password
	 * @return
	 */
	@Override
	public LoginUserEntity loginVerify(UsernamePasswordToken taken) {

		//前端传入账号密码
		String username = taken.getUsername();
		String password = new String(taken.getPassword());

		if(ToolUtil.isEmpty(username) || ToolUtil.isEmpty(password)) {
			throw new RxcException("账号密码不能为空!", "50001");
		}

		//建议图形验证码
		if(Global.getDbKey(ConfigConstant.SYS_LOGIN_VERIFICATION,Constant.SYS_DEFAULT_VALUE_ONE)
				.equals(Constant.SYS_DEFAULT_VALUE_ONE)){
			//后台二次认证 图形验证码
			CaptchaVO captchaVO = new CaptchaVO();
			captchaVO.setCaptchaVerification(ServletUtil.getRequest().getParameter("__captchaVerification"));
			if(!captchaService.verification(captchaVO).isSuccess()){
				throw new RxcException("图形验证码不正确!","50004");
			}
		}

		//解密数据
		String kg4 = ServletUtil.getRequest().getParameter("kg4");
		log.debug("kg4:{}",kg4);
		String sign =ServletUtil.getRequest().getParameter("sign");
		log.debug("sign:{}",sign);

		if(ToolUtil.isEmpty(kg4) || ToolUtil.isEmpty(sign)){
			throw new RxcException("登录确实必要参数kg4/sing","50002");
		}
		//校验签名
		String _sign = SoftEncryption.genSM3Keys((kg4 + username + password).getBytes()).getStr("b64") ;
		if(!_sign.equals(sign)){
			//校验签名失败
			throw new RxcException("加密校验失败","50001");
		}

		//获得明文kg4
		kg4 = SoftEncryption.decryptBySM2(Base64.decode(kg4),
				ConfigConstant.PRIVKEY).getStr("hex");

		//前端账号密码解密
		username =new String(SoftEncryption.decryptBySM4(Base64.decode(username),
				HexUtil.decodeHex(kg4)).get("bytes",byte[].class)).trim();
		password =new String(SoftEncryption.decryptBySM4(Base64.decode(password),
				HexUtil.decodeHex(kg4)).get("bytes",byte[].class)).trim();

		//赋值回去
		taken.setPassword(password.toCharArray());

		Integer number = this.loginBeforeVerify(username,password);

		//获取请求租户号
		String tenantId = StrUtil.EMPTY;
		if(enabled){
			tenantId  = StrUtil.blankToDefault(ServletUtil.getRequest()
					.getParameter(Constant.TENANT_PARAMETER),StrUtil.EMPTY);
		}

		SysUserEntity user = this.sysUserMapper.findUserByUserName(username,
				tenantId);

		//手机号码登陆
		if(ToolUtil.isEmpty(user) && ReUtil.isMatch(Constant.MOBILE_PHONE_NUMBER_PATTERN, username)) {
			user = this.sysUserMapper.findUserByMobile(username,tenantId);
		}

		//邮箱登陆
		if(ToolUtil.isEmpty(user) && ReUtil.isMatch(Constant.EMAIL_PATTERN,username)) {
			user = this.sysUserMapper.findUserByEmail(username,tenantId);
		}

		if(ToolUtil.isEmpty(user)){
			AsyncManager.me().execute(AsyncFactory.recordLogininfor(username,-1L,-1L, "50001","账号或密码错误,请重试."));
			throw new RxcException(ToolUtil.message("sys.login.failure"),"50001");
		}

		//user status : 1 disable
		if ("1".equals(user.getStatus())) {
			throw new RxcException(ToolUtil.message("sys.login.accountDisabled"),"50001");
		}

		//多租户模式判断租户是否正常
		//判断密码是否正确
		if(!UserUtils.sha256(password, user.getSalt()).equals(user.getPassword())) {
			if(number == null) {
				number = 1;
				redisUtil.set(RedisKeys.getUserLoginKey(user.getUsername()), number, RedisUtil.MINUTE * Global.getLockTime());
			}else {
				number++;
				redisUtil.set(RedisKeys.getUserLoginKey(user.getUsername()), number, RedisUtil.MINUTE * Global.getLockTime());
			}
			AsyncManager.me().execute(AsyncFactory.recordLogininfor(username,user.getCompId(),user.getDeptId(), "50004","账号或密码不正确,输入错误"+number+" 次!"));
			//错误次数大于设定
			if(number >= Global.getLoginNumCode()) {
				throw new RxcException(ToolUtil.message("sys.login.password.retry.limit.count",Global.getLoginMaxCount()),"50004");
			}
			throw new RxcException(ToolUtil.message("sys.login.password.retry.limit.count",Global.getLoginMaxCount()),"50005");
		}

		//清空错误登陆标志
		redisUtil.delete(RedisKeys.getUserLoginKey(user.getUsername()));

		//转换成用户登录信息
		LoginUserEntity loginUser = UserFactory.createLoginUser(user);

		//检测是否排挤登录
		this.excludeLogin(loginUser);

		//设置授权
		this.authorization(loginUser,user.getId());

		//设置登陆
		this.setLoginDetails(loginUser,user.getId(),LogType.SYS.getVlaue());

		//设置登录方式
		loginUser.setLoginType(LoginType.USERNAME);

		return loginUser;
	}

	/**
	 * 免密授权登录
	 * @param openId
	 * @return
	 */
	@Override
	public LoginUserEntity freeLoginVerify(String openId, String tenantId) {

		//检查第三方账号是否有绑定用户ID
		SysUserEntity user = this.sysUserMapper.findAuthByUuid(openId,tenantId);

		if(ToolUtil.isEmpty(user)){
			AsyncManager.me().execute(AsyncFactory.recordLogininfor(openId,-1L,-1L, "60001","第三方授权登录,系统没有绑定用户."));
			throw new RxcException(ToolUtil.message("sys.login.failure"),"60001");
		}

		//获取登陆错误次数
		Integer number = redisUtil.get(RedisKeys.getUserLoginKey(user.getUsername()),Integer.class);

		//用户错误次数大于设定数值直接拒绝
		if( ToolUtil.isNotEmpty(number) && number >= Global.getLoginMaxCount()) {
			AsyncManager.me().execute(AsyncFactory.recordLogininfor(user.getUsername(),-1L,-1L, "50003","账户被锁定,"+Global.getLockTime()+" 分钟后解锁!"));
			throw new RxcException(ToolUtil.message("sys.login.failedNumLock",Global.getLockTime()),"50003");
		}

		//清空错误登陆标志
		redisUtil.delete(RedisKeys.getUserLoginKey(user.getUsername()));

		//转换成用户登录信息
		LoginUserEntity loginUser = UserFactory.createLoginUser(user);

		//检测是否排挤登录
		this.excludeLogin(loginUser);

		//设置授权
		this.authorization(loginUser,user.getId());


		//设置登陆
		this.setLoginDetails(loginUser,user.getId(),user.getSource());

		//设置登录方式
		loginUser.setLoginType(LoginType.OTHER);

		return loginUser;
	}

	/**
	 * 手机验证码登录
	 */
	@Override
	public LoginUserEntity valideCodeLoginVerify(String mobile,String valideCode) {

		//获取请求租户号
		String tenantId = StrUtil.EMPTY;
		if(enabled){
			tenantId  = StrUtil.blankToDefault(ServletUtil.getRequest()
					.getParameter(Constant.TENANT_PARAMETER),StrUtil.EMPTY);
		}

		//校验 手机号码 与 验证码是否匹配
		String sysValidCode = redisUtil.get(RedisKeys.getLoginValidKey(mobile),String.class);

		if(ToolUtil.isEmpty(valideCode) || !StrUtil.equals(sysValidCode, valideCode)) {
			AsyncManager.me().execute(AsyncFactory.recordLogininfor(mobile,-1L,-1L, "60002","验证码错误."));
			throw new RxcException("验证码错误!","60002");
		}

		//校验通过删除验证码
		redisUtil.del(RedisKeys.getLoginValidKey(mobile));

		//通过手机获取用户信息
		SysUserEntity user = this.sysUserMapper.findUserByMobile(mobile, tenantId);

		if(ToolUtil.isEmpty(user)){
			AsyncManager.me().execute(AsyncFactory.recordLogininfor(mobile,-1L,-1L, "60003","手机验证码登录失败,手机号码不存在系统."));
			throw new RxcException("手机验证码登录失败,手机号码不存在系统","60001");
		}

		//转换成用户登录信息
		LoginUserEntity loginUser = UserFactory.createLoginUser(user);

		//检测是否排挤登录
		this.excludeLogin(loginUser);

		//设置授权
		this.authorization(loginUser,user.getId());

		//设置登陆
		this.setLoginDetails(loginUser,user.getId(),LogType.MOBILE.getVlaue());

		//设置登录方式
		loginUser.setLoginType(LoginType.MOBILE);

		return loginUser;
	}


	@Override
	public void authorization(LoginUserEntity loginUser, Long userId) {

		//登录清理缓存
		ConstantFactory.me().clearUser(loginUser.getId());

		if(!userId.equals(Constant.SUPER_ADMIN)){
			//获取用户角色列表
			List<SysRoleEntity> roleList = ConstantFactory.me().getRoles(userId);
			List<Object> reles = new ArrayList<>();
			roleList.forEach(r->{
				reles.add(r);
			});
			loginUser.setRoles(reles);
		}
		else{
			//根居角色ID获取模块列表
			SysRoleEntity role = sysRoleMapper.selectOne(new QueryWrapper<SysRoleEntity>()
					.eq("role_key",Constant.SU_ADMIN));
			List<Object> reles = new ArrayList<>();
			reles.add(role);
			loginUser.setRoles(reles);
		}

		//获取用户模块
		List<Map<String, Object>> modules = ConstantFactory.me().getModules(loginUser.getId());
		for(Map<String, Object> s: modules){
			ConstantFactory.me().getMenuByUserIdModuleCode(loginUser.getId(),
					(String) s.get("moduleCode"),loginUser);
		}

		//多租户模式设置
		if(enabled){
			//如果是最大管理员租户且为ADMIN则多租户显示标志为true
			if(loginUser.getId().equals(Constant.SUPER_ADMIN)){
				loginUser.setSuperTenant(true);
			}
		}else{
			loginUser.setSuperTenant(false);
		}
	}

	@Override
	public void setLoginDetails(LoginUserEntity loginUser, Long userId, String source) {

		SysLoginInfoEntity loginInfo = ConstantFactory.me().getFirstLoginInfo(loginUser.getUsername());

		//上次登陆地点
		loginUser.setLoginLocation(loginInfo.getLoginLocation());
		//上次登陆时间
		loginUser.setLoginTime(loginInfo.getLoginTime());

		//插入登陆表
		AsyncManager.me().execute(AsyncFactory.recordLogininfor(loginUser.getUsername(),
				loginUser.getCompId(),loginUser.getDeptId(), "00000","登陆成功!",
				DateUtil.date(),
				source));

	}

	private String getRedisKickoutKey(String username) {
		return this.keyPrefix + username;
	}

	@Override
	public void excludeLogin(LoginUserEntity loginUser) {
		//判断是否登录排挤
		int maxSession = -1;
		//获取系统配置参数
		try{
			maxSession = Integer.parseInt(Global.getDbKey(ConfigConstant.SYS_IS_LOGIN));
		}catch (Exception e){
			log.error("error getting system parameter [SYS_IS_LOGIN]",e);
		}

		if(maxSession != -1 && kickoutAfter) {
			Deque<Serializable> deque = (Deque<Serializable>) redisUtil.getSession(getRedisKickoutKey(loginUser.getUsername()));
			if(deque == null){
				return;
			}
			if(deque.size() == maxSession) {
				int tmepNum = deque.size();
				//检测之前用户是否为登录状态
				for (Iterator<Serializable> q = deque.iterator(); q.hasNext();) {
					try{
						Serializable s = q.next();
						Session session = sessionDAO.readSession(s);
						if(ToolUtil.isEmpty(session)) {
							q.remove();
						}else {
							PrincipalCollection existingPrincipals =
									(PrincipalCollection) session.getAttribute(DefaultSubjectContext.PRINCIPALS_SESSION_KEY);
							if(existingPrincipals == null){
								q.remove();
							}
						}
					}catch (Exception e){
						q.remove();
					}
				}
				if(tmepNum != deque.size()) {
					redisUtil.setSession(getRedisKickoutKey(loginUser.getUsername()), deque);
				}
			}
			if(deque.size() == maxSession) {
				AsyncManager.me().execute(AsyncFactory.recordLogininfor(loginUser.getUsername(),loginUser.getCompId(),loginUser.getDeptId(), "50006","用户已经登录,禁止登录"));
				throw new RxcException(ToolUtil.message("用户已经登录,禁止登录"),"50006");
			}
		}

	}
}
