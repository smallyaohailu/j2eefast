//package com.j2eefast.common.core.page;
//
//import java.util.Map;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.stereotype.Component;
//import org.springframework.web.servlet.view.freemarker.FreeMarkerView;
//
//@Component
//public class LoggingFreeMarkerView extends FreeMarkerView { 
//
//    private static final transient Logger log = LoggerFactory.getLogger(LoggingFreeMarkerView.class); 
//
//    @Override 
//    protected void doRender(Map<String, Object> model, HttpServletRequest request,
//    		HttpServletResponse response) throws Exception { 
//    	
//	     long start = System.currentTimeMillis(); 
//	     super.doRender(model, request, response); 
//	     log.debug("Freemarker rendered " + request.getRequestURI() + " in " + (System.currentTimeMillis() - start) + " ms"); 
//    }
//
//}
