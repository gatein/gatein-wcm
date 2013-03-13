/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2012, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
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

package org.gatein.wcm.cmis;

import javax.annotation.PostConstruct;
import javax.ejb.Startup;
import javax.inject.Singleton;

import org.jboss.logging.Logger;
import org.modeshape.cmis.JcrServiceFactory;

/**
 * An unsuccessful attempt to do some module initialization.
 *
 * @author <a href="mailto:ppalaga@redhat.com">Peter Palaga</a>
 *
 */
@Singleton
@Startup
public class JcrServiceFactoryWrapper {
    private static final Logger log = Logger.getLogger(JcrServiceFactoryWrapper.class);

    static {
        log.info("static");
    }

    private JcrServiceFactory jcrServiceFactory;

    @PostConstruct
    public void initialize() {
        log.info("About to init");
        jcrServiceFactory = new JcrServiceFactory();
        jcrServiceFactory.init();
        log.info("Just initialized");
    }
}
