/*
 * All content copyright http://www.j2eefast.com, unless
 * otherwise indicated. All rights reserved.
 * No deletion without permission
 */
package com.j2eefast.common.core.page;

import org.springframework.web.servlet.view.AbstractTemplateViewResolver;
import org.springframework.web.servlet.view.AbstractUrlBasedView;
import org.springframework.web.servlet.view.freemarker.FreeMarkerView;

/**
 * FreeMarker 模板引擎
 * @author huanzhou
 */
public class JFreeMarkerViewResolver  extends AbstractTemplateViewResolver {

    /**
     * Sets the default {@link #setViewClass view class} to {@link #requiredViewClass}:
     * by default {@link FreeMarkerView}.
     */
    public JFreeMarkerViewResolver() {
        setViewClass(requiredViewClass());
    }

    /**
     * A convenience constructor that allows for specifying {@link #setPrefix prefix}
     * and {@link #setSuffix suffix} as constructor arguments.
     * @param prefix the prefix that gets prepended to view names when building a URL
     * @param suffix the suffix that gets appended to view names when building a URL
     * @since 4.3
     */
    public JFreeMarkerViewResolver(String prefix, String suffix) {
        this();
        setPrefix(prefix);
        setSuffix(suffix);
    }


    /**
     * Requires {@link FreeMarkerView}.
     */
    @Override
    protected Class<?> requiredViewClass() {
        return JFreeMarkerView.class;
    }

    @Override
    protected AbstractUrlBasedView instantiateView() {
        return (getViewClass() == JFreeMarkerView.class ? new JFreeMarkerView() : super.instantiateView());
    }
}
