/*******************************************************************************
 * Copyright 2017 Bstek
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package com.bstek.ureport.console.res;

import java.io.*;
import java.util.zip.GZIPOutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.StrUtil;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.bstek.ureport.console.ServletAction;
import com.bstek.ureport.console.UReportServlet;
import org.springframework.http.HttpHeaders;

/**
 * @author Jacky.gao
 * @since 2016年6月6日
 */
public class ResourceLoaderServletAction implements ServletAction,ApplicationContextAware{
	public static final String URL="/res";
	private ApplicationContext applicationContext;
	@Override
	public void execute(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String path=req.getContextPath()+UReportServlet.URL+URL;
		String uri=req.getRequestURI();
		String resPath=uri.substring(path.length()+1);
		String p="classpath:"+resPath;
		byte[] outBytes = StrUtil.EMPTY.getBytes();
		if(p.endsWith(".js")){
			resp.setContentType("text/javascript");	
		}else if(p.endsWith(".css")){
			resp.setContentType("text/css");
		}else if(p.endsWith(".png")){
			resp.setContentType("image/png");
		}else if(p.endsWith(".jpg")){
			resp.setContentType("image/jpeg");
		}else if(p.endsWith(".svg")){
			resp.setContentType("image/svg+xml");			
		}else{
			resp.setContentType("application/octet-stream");
		}
		InputStream input = applicationContext.getResource(p).getInputStream();
		outBytes = IOUtils.toByteArray(input);

		ByteArrayOutputStream bout=new ByteArrayOutputStream();
		GZIPOutputStream gzip=new GZIPOutputStream(bout);
		gzip.write(outBytes);
		//一般输出都要flush
		gzip.flush();
		gzip.close();
		outBytes = bout.toByteArray();
		//注明压缩类型
		resp.setHeader("content-encoding", "gzip");
		resp.setHeader("content-length",outBytes.length+"");
		// Http 1.0 header, set a fix expires date.
		resp.setDateHeader(HttpHeaders.EXPIRES, System.currentTimeMillis() + 30*24*60*60 * 1000);
		// Http 1.1 header, set a time after now.
		resp.setHeader(HttpHeaders.CACHE_CONTROL, "private, max-age=" + 30*24*60*60);

		InputStream input0 = new ByteArrayInputStream(outBytes);
		OutputStream output= resp.getOutputStream();
		try{
			IoUtil.copy(input0,output);
		}finally{
			IoUtil.close(input);
			IoUtil.close(input0);
			IoUtil.close(output);
		}
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext=applicationContext;
	}
	
	@Override
	public String url() {
		return URL;
	}
}
