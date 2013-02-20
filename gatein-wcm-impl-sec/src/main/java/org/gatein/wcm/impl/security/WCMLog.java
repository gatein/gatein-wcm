package org.gatein.wcm.impl.security;

import java.util.Locale;

import org.modeshape.common.i18n.I18nResource;

public class WCMLog implements I18nResource {

    String msg;

    public WCMLog(String msg) {
        this.msg = msg;
    }

    public String text(Object... arguments) {
        return msg;
    }

    public String text(Locale locale, Object... arguments) {
        return msg;
    }

}
