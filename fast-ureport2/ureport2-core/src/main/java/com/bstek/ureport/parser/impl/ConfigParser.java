/**
 * All content copyright http://www.j2eefast.com, unless
 * otherwise indicated. All rights reserved.
 * No deletion without permission
 */
package com.bstek.ureport.parser.impl;

import com.bstek.ureport.definition.Config;
import com.bstek.ureport.parser.Parser;
import org.dom4j.Element;


public class ConfigParser implements Parser<Config> {

    @Override
    public Config parse(Element element) {
        Config config = new Config();
        for(Object obj:element.elements()){
            if(!(obj instanceof Element)){
                continue;
            }
            Element ele=(Element)obj;
            String text = ele.getTextTrim();
            if("name".equals(ele.getName())){
                config.setName(text);
            }else if("downName".equals(ele.getName())){
                config.setDownName(text);
            }else if("func".equals(ele.getName())){
                config.setFunc(text);
            }else if("roleKeys".equals(ele.getName())){
                config.setRoleKeys(text);
            }else if("tableAlias".equals(ele.getName())){
                config.setTableAlias(text);
            }else if("remark".equals(ele.getName())){
                config.setRemark(text);
            }
        }
        return config;
    }

}
