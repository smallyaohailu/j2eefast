package com.j2eefast.common.core.config;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import cn.hutool.core.util.StrUtil;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * https 转换 、重定向请求转换
 * 注意: 前端采用nginx使用https, 后端J2eeFAST 服务还是普通请求以后出现的重定向（redirect）的问题。
 * 建议开启 其他情况不建议开启
 * @author huanzhou
 * @date 2021-05-04 20:41
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(prefix = "server", name = "schemeHttps", havingValue="true", matchIfMissing=false)
public class SchemeHttpsConfig {

	@Bean
	public FilterRegistrationBean<Filter> schemeFilterRegistrationBean() {
		FilterRegistrationBean<Filter> bean = new FilterRegistrationBean<>();
		bean.setOrder(Ordered.HIGHEST_PRECEDENCE);
		bean.setFilter(new Filter() {
			
			@Override
			public void init(FilterConfig filterConfig) throws ServletException {}

			@Override
			public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) 
					throws IOException, ServletException {
					
				chain.doFilter(new HttpServletRequestWrapper((HttpServletRequest) request) {

					@Override
					public String getScheme() {
						return "https";
					}

					@Override
					public StringBuffer getRequestURL() {
						StringBuffer sb = super.getRequestURL();
						if ("http:".equals(sb.substring(0, 5))){
							return sb.replace(0, 5, "https:");
						}else{
							return sb;
						}
					}

				}, response);
			}



			@Override
			public void destroy() {}
		});
		bean.addUrlPatterns("/*");
		return bean;
	}


	@Bean
	public FilterRegistrationBean registFilter() {

		FilterRegistrationBean registration = new FilterRegistrationBean();
		registration.setFilter(new AbsoluteSendRedirectFilter());
		registration.setName("filterRegistrationBean");
		registration.setOrder(Ordered.HIGHEST_PRECEDENCE - 1);
		registration.addUrlPatterns("/*");
		return registration;
	}

	public class AbsoluteSendRedirectFilter extends OncePerRequestFilter {

		@Override
		protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
			RedirectResponseWrapper redirectResponseWrapper = new RedirectResponseWrapper(request, response);
			filterChain.doFilter(request, redirectResponseWrapper);
		}

	}

	public class RedirectResponseWrapper extends HttpServletResponseWrapper {

		private final HttpServletRequest request;

		public RedirectResponseWrapper(final HttpServletRequest inRequest, final HttpServletResponse response) {
			super(response);
			this.request = inRequest;
		}

		/**
		 * 强制重定向转https
		 * @param pLocation
		 * @throws IOException
		 */
		@Override
		public void sendRedirect(final String pLocation) throws IOException {

			if (StrUtil.isBlank(pLocation)) {
				super.sendRedirect(pLocation);
				return;
			}

			try {
				final URI uri = new URI(pLocation);
				if (uri.getScheme() != null) {
					super.sendRedirect(pLocation);
					return;
				}
			} catch (URISyntaxException ex) {
				super.sendRedirect(pLocation);
			}

			// !!! FIX Scheme  !!!
			String finalurl = "https://" + this.request.getServerName();
			if (request.getServerPort() != 80 && request.getServerPort() != 443) {
				finalurl += ":" + request.getServerPort();
			}
			finalurl += pLocation;
			super.sendRedirect(finalurl);
		}
	}
}
