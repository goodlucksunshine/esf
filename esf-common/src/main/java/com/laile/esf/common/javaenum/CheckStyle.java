package com.laile.esf.common.javaenum;

/**
 * Created by sunshine on 16/7/25.
 */
public enum CheckStyle {
    PAGE("page"), TIPS("tips"), ERROR("error");
    private String style;

    CheckStyle(String style) {
        this.style = style;
    }

    public String getStyle() {
        return style;
    }

}
