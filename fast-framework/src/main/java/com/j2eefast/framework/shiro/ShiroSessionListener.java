/*
 * All content copyright http://www.j2eefast.com, unless
 * otherwise indicated. All rights reserved.
 * No deletion without permission
 */
package com.j2eefast.framework.shiro;

import org.apache.shiro.session.Session;
import org.apache.shiro.session.SessionListener;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 
 * @author zhouzhou
 * @date 2020-03-12 20:56
 */
public class ShiroSessionListener implements SessionListener {

	/**
	 * 统计在线人数 juc包下线程安全自增
	 */
	private final AtomicInteger sessionCount = new AtomicInteger(0);

	/**
	 * 会话创建时触发
	 * 
	 * @param session
	 */
	@Override
	public void onStart(Session session) {
		System.out.println("-------------------------会话创建:session:" + session.getId());
		// 会话创建，在线人数加一
		sessionCount.incrementAndGet();
	}

	/**
	 * 退出会话时触发
	 * 
	 * @param session
	 */
	@Override
	public void onStop(Session session) {
		System.out.println("-------------------------会话退出:session:" + session.getId());
		// 会话退出,在线人数减一
		sessionCount.decrementAndGet();
	}

	/**
	 * 会话过期时触发
	 * 
	 * @param session
	 */
	@Override
	public void onExpiration(Session session) {
		System.out.println("-------------------------会话过期,:session:" + session.getId());
		// 会话过期,在线人数减一
		//SpringUtil.getBean(RedisUtil.class).delSession(ConfigConstant.SHIRO_SESSION_ID + session.getId());
		sessionCount.decrementAndGet();
	}

	/**
	 * 获取在线人数使用
	 * 
	 * @return
	 */
	public AtomicInteger getSessionCount() {
		return sessionCount;
	}

}
