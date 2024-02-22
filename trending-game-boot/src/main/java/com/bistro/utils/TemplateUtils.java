package com.bistro.utils;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;

import java.io.StringWriter;

public class TemplateUtils {

    public static   String getContent(String template, Object data){
        VelocityEngine ve = new VelocityEngine();
        ve.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
        ve.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());

        ve.init();

        Template t = ve.getTemplate(template);
        VelocityContext ctx = new VelocityContext();
        ctx.put("data",data);
        StringWriter sw = new StringWriter();
        t.merge(ctx, sw);
        return  sw.toString();
    }
}
