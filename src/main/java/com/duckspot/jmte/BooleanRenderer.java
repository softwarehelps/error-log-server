package com.duckspot.jmte;

import com.floreysoft.jmte.NamedRenderer;
import com.floreysoft.jmte.RenderFormatInfo;
import java.util.Locale;

public class BooleanRenderer implements NamedRenderer {
    
    
    public static String renderBoolean(boolean b, String format) {
        if ("selected".equals(format) || "checked".equals(format)) {
            if (b)
                return format;
            else
                return "";
        } else {
            return String.valueOf(b);
        }
    }
    
    public String getName() {
        return "boolean";
    }

    public RenderFormatInfo getFormatInfo() {
        return null;
    }

    public Class<?>[] getSupportedClasses() {
        return new Class<?>[] { Boolean.class };
    }
    
    public String render(Object o, String format, Locale locale) {

        return renderBoolean((Boolean)o, format);
    }
}
