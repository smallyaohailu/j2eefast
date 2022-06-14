package com.bstek.ureport.parser.impl.searchform;

import com.bstek.ureport.definition.searchform.CommonButtonComponent;
import org.dom4j.Element;

/**
 * @author huanzhou
 */
public class CommonButtonParser implements FormParser<CommonButtonComponent>{

    @Override
    public boolean support(String name) {
        return "button-common".equals(name);
    }

    @Override
    public CommonButtonComponent parse(Element element) {
        CommonButtonComponent btn = new CommonButtonComponent();
        btn.setLabel(element.attributeValue("label"));
        btn.setButId(element.attributeValue("butId"));
        btn.setStyle(element.attributeValue("style"));
        btn.setType(element.attributeValue("type"));
        return btn;
    }
}
