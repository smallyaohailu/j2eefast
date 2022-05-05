/*
 * All content copyright http://www.j2eefast.com, unless
 * otherwise indicated. All rights reserved.
 * No deletion without permission
 */
package com.j2eefast.common.core.io;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.parser.ParserConfig;
import org.springframework.boot.env.OriginTrackedMapPropertySource;
import org.springframework.boot.env.PropertiesPropertySourceLoader;
import org.springframework.boot.env.PropertySourceLoader;
import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.core.Ordered;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * 系统启动FastApplication
 * 初始化加载配置文件
 * @author huanzhou
 */
public class PropertyLoader implements PropertySourceLoader, Ordered {

    private PropertiesPropertySourceLoader propertiesPropertySourceLoader = new PropertiesPropertySourceLoader();
    private YamlPropertySourceLoader yamlPropertySourceLoader = new YamlPropertySourceLoader();
    private static boolean isLoadSysPropertySource = false;

    @Override
    public int getOrder() {
        return Integer.MIN_VALUE;
    }

    @Override
    public String[] getFileExtensions() {
        return new String[] { "properties", "yml", "yaml"};
    }

    @Override
    public List<PropertySource<?>> load(String name, Resource resource) throws IOException {
        List<PropertySource<?>> propertySources = new ArrayList<>();
        if (!isLoadSysPropertySource) {
            isLoadSysPropertySource = true;
            // 开启 FastJSON 安全模式
            ParserConfig.getGlobalInstance().setSafeMode(true);
            Properties properties = PropertiesUtils.getInstance().getProperties();
            propertySources.add(new OriginTrackedMapPropertySource("j2eefast", properties));
        } else {
            String ext = FileUtil.getSuffix(resource.getFilename());
            if (StrUtil.containsAny(ext, propertiesPropertySourceLoader.getFileExtensions())) {
                propertySources.addAll(propertiesPropertySourceLoader.load(name, resource));
            }else if (StrUtil.containsAny(ext, yamlPropertySourceLoader.getFileExtensions())) {
                propertySources.addAll(yamlPropertySourceLoader.load(name, resource));
            }
        }
        return propertySources;
    }
}
