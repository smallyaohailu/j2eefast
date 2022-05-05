/*
 * All content copyright http://www.j2eefast.com, unless 
 * otherwise indicated. All rights reserved.
 * No deletion without permission
 */
package com.j2eefast.framework.sys.controller;

import com.j2eefast.common.core.constants.ConfigConstant;
import com.j2eefast.common.core.controller.BaseController;
import com.j2eefast.common.core.utils.ResponseData;
import com.j2eefast.framework.utils.ServerUtil;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * @Description:服务监控控制类
 * @author zhouzhou loveingowp@163.com
 * @time 2019-04-03 12:40
 * @version V1.0 
 *
 */
@Controller
@RequestMapping("/sys/server")
public class ServiceController extends BaseController {
	
	private final static Logger 					LOG 					= LoggerFactory.getLogger(ServiceController.class);
	private String 									urlPrefix 				= "modules/sys/monitor";
	@Value("${fast.demoMode.enabled: false}")
	private Boolean 								demoMode;

	@Autowired
	private RedisTemplate redisTemplate;

	@RequiresPermissions("sys:server:view")
	@GetMapping()
	public String server(){
		return urlPrefix + "/server";
	}
 
	@RequestMapping(value = "/info/{init}" ,method = RequestMethod.POST)
	@ResponseBody
	public ResponseData info(@PathVariable("init") String init) {

		ServerUtil server = new ServerUtil();

		try {
			server.copyTo(redisTemplate,init);
		} catch (Exception e) {
			LOG.error("报错:",e);
			return error("获取服务器信息异常");
		}

		if(demoMode) {

			server.getSys().setComputerIp(ConfigConstant.DEOM_MODE_SHOW);
			server.getSys().setComputerName(ConfigConstant.DEOM_MODE_SHOW);
			server.getSys().setUserDir(ConfigConstant.DEOM_MODE_SHOW);
			server.getJvm().setHome(ConfigConstant.DEOM_MODE_SHOW);
			server.getJvm().setVersion(ConfigConstant.DEOM_MODE_SHOW);

			// ----- 屏蔽Redis 敏感信息
			server.getRedis().setRunPath(ConfigConstant.DEOM_MODE_SHOW);
			server.getRedis().setConfigPath(ConfigConstant.DEOM_MODE_SHOW);
			// -----
		}
		return success().put("server", server);
	}
}
