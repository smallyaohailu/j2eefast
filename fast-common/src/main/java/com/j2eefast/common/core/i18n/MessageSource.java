/*
 * All content copyright http://www.j2eefast.com, unless
 * otherwise indicated. All rights reserved.
 * No deletion without permission
 */
package com.j2eefast.common.core.i18n;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.CharUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.setting.dialect.Props;
import com.j2eefast.common.config.entity.SysLangEntity;
import com.j2eefast.common.config.service.SysLangService;
import com.j2eefast.common.core.constants.ConfigConstant;
import com.j2eefast.common.core.utils.CookieUtil;
import com.j2eefast.common.core.utils.ServletUtil;
import com.j2eefast.common.core.utils.ToolUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.support.AbstractMessageSource;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.RequestContextUtils;
import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

/**
 * 系统国际化配置
 * 1.可在后台语言配置设置国际化语言
 * 2.可在i18n文件文件夹下添加国际化语言
 * 3.两者都配置已数据库为准
 * @author huanzhou
 */
@Service("messageSource")
public class MessageSource extends AbstractMessageSource implements ResourceLoaderAware {


    private static final String UTF8 = "UTF-8";
    private static final String PREFIX = "i18n_";
    private static final String I18N = "i18n";

    /**
     * 缓存国际化配置信息
     */
    private static final Map<String, Map<String, String>> LOCAL_CACHE = new ConcurrentHashMap<>(256);

    ResourceLoader resourceLoader;

    @Value("#{ @environment['fast.messages.basename'] ?: 'i18n/' }")
    private String basename;

    @Autowired
    private SysLangService sysLangService;

    /**
     * 初始化
     * Java中该注解的说明：@PostConstruct该注解被用来修饰一个非静态的void（）方法。
     * 被@PostConstruct修饰的方法会在服务器加载Servlet的时候运行，并且只会被服务器执行一次。
     * PostConstruct在构造函数之后执行，init（）方法之前执行。
     */
    @PostConstruct
    public void init() {
        this.reload();
    }
    
  
    /**
     * 重新将数据库中的国际化配置加载
     */
    public void reload() {
    	// 刷新前端变动拉取标志
    	ConfigConstant.I18N_ATG = IdUtil.nanoId();
    	// 情况国际化缓存
        LOCAL_CACHE.clear();
        // 重新加载到内存
        LOCAL_CACHE.putAll(loadAllMessageResourcesFromDB());
    }

    /**
     * 通过语言编码获取对应信息
     * @param locale
     * @return
     */
    public Map<String, String> getLocaleMap(String locale){
        return LOCAL_CACHE.get(locale);
    }

    /**
     * 从数据库\配置文件 获取所有国际化配置
     * 配置文件与数据库都配置已数据库为准
     */
    public Map<String, Map<String, String>> loadAllMessageResourcesFromDB() {
        Map<String, Map<String, String>> _LOCAL_CACHE = new HashMap<>();

        String path = FileUtil.getAbsolutePath(basename);

        if(!FileUtil.isDirectory(path)){
            // 获取Jar默认 内部Jar i18n
            path = FileUtil.normalize(this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath()
                    +File.separator + I18N + File.separator);
            System.out.println(path);
            int index = path.lastIndexOf(FileUtil.JAR_FILE_EXT);
            // jar文件中的路径
            index = index + FileUtil.JAR_FILE_EXT.length();
            JarFile jarFile = null;
            try {
                jarFile = new JarFile(path.substring(0, index));
                String dir = StrUtil.removePrefix(path.substring(index + 1), "/");
                if (StrUtil.isNotBlank(dir)) {
                    // 目录尾部添加"/"
                    dir = StrUtil.addSuffixIfNot(dir, StrUtil.SLASH);
                }
                String name;
                for (ZipEntry entry : Collections.list(jarFile.entries())) {
                    name = entry.getName();
                    if (StrUtil.isEmpty(dir) || name.startsWith(dir)) {
                        final String nameSuffix = StrUtil.removePrefix(name, dir);
                        if (StrUtil.isNotEmpty(nameSuffix) && false == StrUtil.contains(nameSuffix, CharUtil.SLASH)) {
                            String fileName =  nameSuffix.substring(0,nameSuffix.indexOf("."));
                            if(fileName.equals(I18N)){
                                continue;
                            }
                            URL f = MessageSource.class.getResource(File.separator+ I18N + File.separator  + nameSuffix);
                            loadPropsFile(fileName,new Props(f,UTF8),_LOCAL_CACHE);
                        }
                    }
                }
            } catch (IOException e) {
                throw new IORuntimeException(StrUtil.format("Can not read file path of [{}]", path), e);
            } finally {
                IoUtil.close(jarFile);
            }
        }else{
            // 获取配置文件
            File[] files = FileUtil.ls(path);
            for(File f: files){
                String fileName =  FileUtil.getPrefix(f);
                if(fileName.equals(I18N)){
                    continue;
                }
                loadPropsFile(fileName,new Props(f,UTF8),_LOCAL_CACHE);
            }
        }

        // 获取数据库配置
        List<SysLangEntity> list = sysLangService.list();
        if (ToolUtil.isNotEmpty(list)) {
            for(SysLangEntity leng :list){
                String langType = leng.getLangType();
                Map<String, String> temp = _LOCAL_CACHE.get(langType);
                if(ToolUtil.isEmpty(temp)){
                    temp = new HashMap<>(list.size());
                    temp.put(leng.getLangCode(),leng.getLangText());
                    _LOCAL_CACHE.put(langType,temp);
                }
                temp.put(leng.getLangCode(),leng.getLangText());
            }
        }

        return _LOCAL_CACHE;
    }

    
    /**
     * 加载系统properties文件
     * @param fileName 文件名
     * @param props    资源文件
     * @param _LOCAL_CACHE
     */
    private void loadPropsFile(String fileName,
                               Props props,
                               Map<String, Map<String, String>> _LOCAL_CACHE ){
        String langType = StrUtil.replaceIgnoreCase(fileName,PREFIX,StrUtil.EMPTY);
        Map<String, String> temp = _LOCAL_CACHE.get(langType);
        if(ToolUtil.isEmpty(temp)){
            temp = new HashMap<>(props.size());
            for (String key : props.stringPropertyNames()) {
                temp.put(key,props.getStr(key));
            }
            _LOCAL_CACHE.put(langType,temp);
        }else{
            for (String key : props.stringPropertyNames()) {
                temp.put(key,props.getStr(key));
            }
        }
    }

    /**
     * 从缓存中取出国际化配置对应的数据 或者从父级获取
     *
     * @param code
     * @param locale 可以为null, 表示从当前HttpServletRequest中获取语言
     * @return
     */
    public String getSourceFromCache(String code, Locale locale) {

        String language = CookieUtil.getCookie(ServletUtil.getRequest(),"_lang");

        if(ToolUtil.isEmpty(language)){
            // 获取系统设置的语言编码
            language = locale == null ? RequestContextUtils.getLocale(ServletUtil.getRequest()).getLanguage() + "_"
                    + RequestContextUtils.getLocale(ServletUtil.getRequest()).getCountry()
                    : locale.getLanguage() + "_" + locale.getCountry();
        }

        // 获取缓存中对应语言的所有数据项
        Map<String, String> props = LOCAL_CACHE.get(language);
        if (null != props && props.containsKey(code)) {
            // 如果对应语言中能匹配到数据项，那么直接返回
            return props.get(code);
        } else {
            // 如果对应语言中不能匹配到数据项，从上级获取返回
            try {
                if (null != this.getParentMessageSource()) {
                    return this.getParentMessageSource().getMessage(code, null, locale);
                }
            } catch (Exception ex) {
                logger.error(ex.getMessage(), ex);
            }
            // 如果上级也没有找到，那么返回请求键值
            return code;
        }
    }


    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = (resourceLoader == null ? new DefaultResourceLoader() : resourceLoader);
    }

    // 重写resolveCode 方法
    @Override
    protected MessageFormat resolveCode(String code, Locale locale) {
        String msg = getSourceFromCache(code, locale);
        MessageFormat messageFormat = new MessageFormat(msg, locale);
        return messageFormat;
    }

    // 重写resolveCodeWithoutArguments 方法
    @Override
    protected String resolveCodeWithoutArguments(String code, Locale locale) {
        return getSourceFromCache(code, locale);
    }
}
