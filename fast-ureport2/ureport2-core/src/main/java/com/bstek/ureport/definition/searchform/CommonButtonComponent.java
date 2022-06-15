package com.bstek.ureport.definition.searchform;

/**
 * 一般按钮
 * @author huanzhou
 */
public class CommonButtonComponent extends ButtonComponent{

    //ID
    private String butId;

    @Override
    public String toHtml(RenderContext context) {
        return "<button type=\"button\" id=\""+butId+"\" class=\"btn "+getStyle()+" btn-sm\">"+getLabel()+"</button>";
    }

    public String getButId() {
        return butId;
    }

    public void setButId(String butId) {
        this.butId = butId;
    }

    @Override
    public String initJs(RenderContext context) {
        return "";
    }
}
