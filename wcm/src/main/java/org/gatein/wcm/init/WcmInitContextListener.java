/*
 * JBoss, a division of Red Hat
 * Copyright 2010, Red Hat Middleware, LLC, and individual
 * contributors as indicated by the @authors tag. See the
 * copyright.txt in the distribution for a full listing of
 * individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.gatein.wcm.init;

import org.gatein.wcm.Wcm;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Logger;

/**
 * Init class to load properties from Wcm.CONFIGURATION_FILE from ${jboss.server.config.dir}
 *
 * @see Wcm
 * @author <a href="mailto:lponce@redhat.com">Lucas Ponce</a>
 */
@WebListener
public class WcmInitContextListener implements ServletContextListener {
    private static final Logger log = Logger.getLogger(WcmInitContextListener.class.getName());

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        Properties props = new Properties();
        String jbossCfgDir = System.getProperty("jboss.server.config.dir");
        try {
            props.load(new FileInputStream(jbossCfgDir + File.separator + Wcm.CONFIGURATION_FILE));
        } catch (Exception e) {
            log.warning("Error reading " + Wcm.CONFIGURATION_FILE + " file.");
        }
        Set<String> names = props.stringPropertyNames();
        for (String name : names) {
            String value = props.getProperty(name);
            value = processSystemProperty(value);
            System.setProperty(name, value);
            log.info("Setting " + name + " = " + value);
        }
    }

    /*
        Resolve values like:
            /tmp/${system.variable}/tmp2/${system.variable2}/${system.variable3}/${system.variable4}
       or
            /tmp/${system.variable:defaultvalue}/tmp2/${system.variable2}/${system.variable3}/${system.variable4}

     */
    private String processSystemProperty(String value) {
        if (value == null || value.equals("")) return value;
        String out = value.trim();
        int first = 0;
        while (first > -1) {
            first = out.indexOf("${", 0);
            int next = out.indexOf("}", first);
            if (next > -1) {
                String variable = out.substring(first, next+1);
                String result = processVariable(variable);
                out = out.replace(variable, result);
            }
        }
        return out;
    }

    /*
        Process a system variable with format:
            ${system.variable}
        or
            ${system.variable:defaultvalue}
     */
    private String processVariable(String variable) {
        if (variable == null || variable.equals("")) return variable;
        String clean = variable.substring(2, variable.length() - 1); // Cleaning ${ and }
        String out = null;
        if (clean.split(":").length == 2) {
            out = System.getProperty(clean.split(":")[0]);
            if (out == null) out = clean.split(":")[1];
        } else {
            out = System.getProperty(clean.split(":")[0]);
        }
        return out;
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        // Nothing to do here
    }
}
