package com.j2eefast.generator.gen.controller;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.DynaBean;
import cn.hutool.core.codec.Base64;
import cn.hutool.core.io.FileUtil;
import com.j2eefast.common.core.constants.ConfigConstant;
import com.j2eefast.common.core.controller.BaseController;
import com.j2eefast.common.core.utils.ResponseData;
import com.j2eefast.framework.utils.UserUtils;
import com.j2eefast.generator.gen.entity.GenEditCodeEntity;
import com.j2eefast.generator.gen.service.GenEditCodeService;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.support.RequestContext;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author huanzhou
 */
@Slf4j
@Controller
@RequestMapping("/gen/view")
public class GenViewController  extends BaseController {

    private String urlPrefix = "modules/tool/view";
    @Autowired
    private FreeMarkerConfigurer freeMarkerConfigurer;
    @Autowired
    private GenEditCodeService genEditCodeService;


    @RequiresPermissions("tool:gen:view")
    @PostMapping()
    public String view(ModelMap mmap) throws IOException, TemplateException {
        String  base64 = super.getPara("edit");
        String temp = new String(Base64.decode(base64));
        File file = FileUtil.touch("templates/modules/tool/view/tempView.html");
        FileUtil.writeUtf8String(temp,file);
        //生成静态文件
        Configuration configuration = freeMarkerConfigurer.getConfiguration();
        // 加载模板对象
        Template template = configuration.getTemplate("modules/tool/view/tempView.html");

        // 创建一个数据集
        Map<String, Object> data = new HashMap<>();

        String SPRING_MACRO_REQUEST_CONTEXT_ATTRIBUTE = "springMacroRequestContext";
        data.put(SPRING_MACRO_REQUEST_CONTEXT_ATTRIBUTE,
                new RequestContext(getHttpServletRequest(), null, null, null));
        String path = getHttpServletRequest().getContextPath();
        data.put(ConfigConstant.CTX_STATIC,path);
        data.put(ConfigConstant.FAST_LOGIN_CSRF_TOKEN,
                getHttpServletRequest().getAttribute(ConfigConstant.FAST_LOGIN_CSRF_TOKEN));

        //
        Map<String,Object>  o = new HashMap<>();
        Map<String,Object>  a = new HashMap<>();
        Map<String,Object>  c = new HashMap<>();
        a.put("name","name");
        c.put("name","name");
        o.put("id","0000000000");
        o.put("name","");
        o.put("code","");
        o.put("email","");
        o.put("phone","");
        o.put("sex","");
        o.put("age","");
        o.put("joinDate",new Date());
        o.put("leaveDate",new Date());
        o.put("compId","1");
        o.put("company",a);
        o.put("organization",c);
        o.put("addr","");
        o.put("addrinfo","");
        o.put("remark","");
        o.put("tManDd","");

        data.put("exampleTest", o);

        // 指定文件输出路径以及文件名
        Writer out = new FileWriter(FileUtil.touch("templates/modules/tool/view/temp.html"));
        // 输出文件
        template.process(data,out);
        // 关闭流
        out.close();

        log.info(new String(Base64.decode(base64)));
        return urlPrefix + "/temp";
    }

    @RequiresPermissions("tool:gen:view")
    @PostMapping("/updateView")
    @ResponseBody
    public ResponseData updateView(GenEditCodeEntity genEditCode){
        genEditCode.setTemplate(Base64.decode(genEditCode.getTemplateValue()));
        return  genEditCodeService.updateById(genEditCode)?success():error("保存异常");
    }
}
