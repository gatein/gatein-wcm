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

import java.io.File;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.Enumeration;
import java.util.EventListener;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.servlet.Filter;
import javax.servlet.FilterRegistration;
import javax.servlet.FilterRegistration.Dynamic;
import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;
import javax.servlet.SessionCookieConfig;
import javax.servlet.SessionTrackingMode;
import javax.servlet.descriptor.JspConfigDescriptor;

import org.apache.chemistry.opencmis.client.api.Folder;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.client.runtime.SessionFactoryImpl;
import org.apache.chemistry.opencmis.commons.PropertyIds;
import org.apache.chemistry.opencmis.commons.SessionParameter;
import org.apache.chemistry.opencmis.commons.enums.BindingType;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.modeshape.cmis.JcrServiceFactory;
import org.modeshape.jcr.api.RepositoryFactory;
import org.modeshape.web.jcr.ModeShapeJcrDeployer;

/**
 * @author <a href="mailto:ppalaga@redhat.com">Peter Palaga</a>
 *
 */
@RunWith(Arquillian.class)
public class CmisServerApiTest {

    /**
     * Necessary to make org.modeshape.web.jcr.RepositoryManager happy.
     *
     */
    public static class DummyServletContext implements ServletContext {
        private Map<String, String> initParameters = new HashMap<String, String>();

        /*
         * (non-Javadoc)
         *
         * @see javax.servlet.ServletContext#addFilter(java.lang.String, java.lang.Class)
         */
        @Override
        public Dynamic addFilter(String filterName, Class<? extends Filter> filterClass) {
            throw new UnsupportedOperationException();
        }

        /*
         * (non-Javadoc)
         *
         * @see javax.servlet.ServletContext#addFilter(java.lang.String, javax.servlet.Filter)
         */
        @Override
        public Dynamic addFilter(String filterName, Filter filter) {
            throw new UnsupportedOperationException();
        }

        /*
         * (non-Javadoc)
         *
         * @see javax.servlet.ServletContext#addFilter(java.lang.String, java.lang.String)
         */
        @Override
        public Dynamic addFilter(String filterName, String className) {
            throw new UnsupportedOperationException();
        }

        /*
         * (non-Javadoc)
         *
         * @see javax.servlet.ServletContext#addListener(java.lang.Class)
         */
        @Override
        public void addListener(Class<? extends EventListener> listenerClass) {
            throw new UnsupportedOperationException();
        }

        /*
         * (non-Javadoc)
         *
         * @see javax.servlet.ServletContext#addListener(java.lang.String)
         */
        @Override
        public void addListener(String className) {
            throw new UnsupportedOperationException();
        }

        /*
         * (non-Javadoc)
         *
         * @see javax.servlet.ServletContext#addListener(java.util.EventListener)
         */
        @Override
        public <T extends EventListener> void addListener(T t) {
            throw new UnsupportedOperationException();
        }

        /*
         * (non-Javadoc)
         *
         * @see javax.servlet.ServletContext#addServlet(java.lang.String, java.lang.Class)
         */
        @Override
        public javax.servlet.ServletRegistration.Dynamic addServlet(String servletName, Class<? extends Servlet> servletClass) {
            throw new UnsupportedOperationException();
        }

        /*
         * (non-Javadoc)
         *
         * @see javax.servlet.ServletContext#addServlet(java.lang.String, javax.servlet.Servlet)
         */
        @Override
        public javax.servlet.ServletRegistration.Dynamic addServlet(String servletName, Servlet servlet) {
            throw new UnsupportedOperationException();
        }

        /*
         * (non-Javadoc)
         *
         * @see javax.servlet.ServletContext#addServlet(java.lang.String, java.lang.String)
         */
        @Override
        public javax.servlet.ServletRegistration.Dynamic addServlet(String servletName, String className) {
            throw new UnsupportedOperationException();
        }

        /*
         * (non-Javadoc)
         *
         * @see javax.servlet.ServletContext#createFilter(java.lang.Class)
         */
        @Override
        public <T extends Filter> T createFilter(Class<T> clazz) throws ServletException {
            throw new UnsupportedOperationException();
        }

        /*
         * (non-Javadoc)
         *
         * @see javax.servlet.ServletContext#createListener(java.lang.Class)
         */
        @Override
        public <T extends EventListener> T createListener(Class<T> clazz) throws ServletException {
            throw new UnsupportedOperationException();
        }

        /*
         * (non-Javadoc)
         *
         * @see javax.servlet.ServletContext#createServlet(java.lang.Class)
         */
        @Override
        public <T extends Servlet> T createServlet(Class<T> clazz) throws ServletException {
            throw new UnsupportedOperationException();
        }

        /*
         * (non-Javadoc)
         *
         * @see javax.servlet.ServletContext#declareRoles(java.lang.String[])
         */
        @Override
        public void declareRoles(String... roleNames) {
            throw new UnsupportedOperationException();
        }

        /*
         * (non-Javadoc)
         *
         * @see javax.servlet.ServletContext#getAttribute(java.lang.String)
         */
        @Override
        public Object getAttribute(String name) {
            throw new UnsupportedOperationException();
        }

        /*
         * (non-Javadoc)
         *
         * @see javax.servlet.ServletContext#getAttributeNames()
         */
        @Override
        public Enumeration<String> getAttributeNames() {
            throw new UnsupportedOperationException();
        }

        /*
         * (non-Javadoc)
         *
         * @see javax.servlet.ServletContext#getClassLoader()
         */
        @Override
        public ClassLoader getClassLoader() {
            throw new UnsupportedOperationException();
        }

        /*
         * (non-Javadoc)
         *
         * @see javax.servlet.ServletContext#getContext(java.lang.String)
         */
        @Override
        public ServletContext getContext(String uripath) {
            throw new UnsupportedOperationException();
        }

        /*
         * (non-Javadoc)
         *
         * @see javax.servlet.ServletContext#getContextPath()
         */
        @Override
        public String getContextPath() {
            throw new UnsupportedOperationException();
        }

        /*
         * (non-Javadoc)
         *
         * @see javax.servlet.ServletContext#getDefaultSessionTrackingModes()
         */
        @Override
        public Set<SessionTrackingMode> getDefaultSessionTrackingModes() {
            throw new UnsupportedOperationException();
        }

        /*
         * (non-Javadoc)
         *
         * @see javax.servlet.ServletContext#getEffectiveMajorVersion()
         */
        @Override
        public int getEffectiveMajorVersion() {
            throw new UnsupportedOperationException();
        }

        /*
         * (non-Javadoc)
         *
         * @see javax.servlet.ServletContext#getEffectiveMinorVersion()
         */
        @Override
        public int getEffectiveMinorVersion() {
            throw new UnsupportedOperationException();
        }

        /*
         * (non-Javadoc)
         *
         * @see javax.servlet.ServletContext#getEffectiveSessionTrackingModes()
         */
        @Override
        public Set<SessionTrackingMode> getEffectiveSessionTrackingModes() {
            throw new UnsupportedOperationException();
        }

        /*
         * (non-Javadoc)
         *
         * @see javax.servlet.ServletContext#getFilterRegistration(java.lang.String)
         */
        @Override
        public FilterRegistration getFilterRegistration(String filterName) {
            throw new UnsupportedOperationException();
        }

        /*
         * (non-Javadoc)
         *
         * @see javax.servlet.ServletContext#getFilterRegistrations()
         */
        @Override
        public Map<String, ? extends FilterRegistration> getFilterRegistrations() {
            throw new UnsupportedOperationException();
        }

        /*
         * (non-Javadoc)
         *
         * @see javax.servlet.ServletContext#getInitParameter(java.lang.String)
         */
        @Override
        public String getInitParameter(String name) {
            return initParameters.get(name);
        }

        /*
         * (non-Javadoc)
         *
         * @see javax.servlet.ServletContext#getInitParameterNames()
         */
        @Override
        public Enumeration<String> getInitParameterNames() {
            return Collections.enumeration(initParameters.keySet());
        }

        /*
         * (non-Javadoc)
         *
         * @see javax.servlet.ServletContext#getJspConfigDescriptor()
         */
        @Override
        public JspConfigDescriptor getJspConfigDescriptor() {
            throw new UnsupportedOperationException();
        }

        /*
         * (non-Javadoc)
         *
         * @see javax.servlet.ServletContext#getMajorVersion()
         */
        @Override
        public int getMajorVersion() {
            throw new UnsupportedOperationException();
        }

        /*
         * (non-Javadoc)
         *
         * @see javax.servlet.ServletContext#getMimeType(java.lang.String)
         */
        @Override
        public String getMimeType(String file) {
            throw new UnsupportedOperationException();
        }

        /*
         * (non-Javadoc)
         *
         * @see javax.servlet.ServletContext#getMinorVersion()
         */
        @Override
        public int getMinorVersion() {
            throw new UnsupportedOperationException();
        }

        /*
         * (non-Javadoc)
         *
         * @see javax.servlet.ServletContext#getNamedDispatcher(java.lang.String)
         */
        @Override
        public RequestDispatcher getNamedDispatcher(String name) {
            throw new UnsupportedOperationException();
        }

        /*
         * (non-Javadoc)
         *
         * @see javax.servlet.ServletContext#getRealPath(java.lang.String)
         */
        @Override
        public String getRealPath(String path) {
            throw new UnsupportedOperationException();
        }

        /*
         * (non-Javadoc)
         *
         * @see javax.servlet.ServletContext#getRequestDispatcher(java.lang.String)
         */
        @Override
        public RequestDispatcher getRequestDispatcher(String path) {
            throw new UnsupportedOperationException();
        }

        /*
         * (non-Javadoc)
         *
         * @see javax.servlet.ServletContext#getResource(java.lang.String)
         */
        @Override
        public URL getResource(String path) throws MalformedURLException {
            throw new UnsupportedOperationException();
        }

        /*
         * (non-Javadoc)
         *
         * @see javax.servlet.ServletContext#getResourceAsStream(java.lang.String)
         */
        @Override
        public InputStream getResourceAsStream(String path) {
            throw new UnsupportedOperationException();
        }

        /*
         * (non-Javadoc)
         *
         * @see javax.servlet.ServletContext#getResourcePaths(java.lang.String)
         */
        @Override
        public Set<String> getResourcePaths(String path) {
            throw new UnsupportedOperationException();
        }

        /*
         * (non-Javadoc)
         *
         * @see javax.servlet.ServletContext#getServerInfo()
         */
        @Override
        public String getServerInfo() {
            throw new UnsupportedOperationException();
        }

        /*
         * (non-Javadoc)
         *
         * @see javax.servlet.ServletContext#getServlet(java.lang.String)
         */
        @Override
        public Servlet getServlet(String name) throws ServletException {
            throw new UnsupportedOperationException();
        }

        /*
         * (non-Javadoc)
         *
         * @see javax.servlet.ServletContext#getServletContextName()
         */
        @Override
        public String getServletContextName() {
            throw new UnsupportedOperationException();
        }

        /*
         * (non-Javadoc)
         *
         * @see javax.servlet.ServletContext#getServletNames()
         */
        @Override
        public Enumeration<String> getServletNames() {
            throw new UnsupportedOperationException();
        }

        /*
         * (non-Javadoc)
         *
         * @see javax.servlet.ServletContext#getServletRegistration(java.lang.String)
         */
        @Override
        public ServletRegistration getServletRegistration(String servletName) {
            throw new UnsupportedOperationException();
        }

        /*
         * (non-Javadoc)
         *
         * @see javax.servlet.ServletContext#getServletRegistrations()
         */
        @Override
        public Map<String, ? extends ServletRegistration> getServletRegistrations() {
            throw new UnsupportedOperationException();
        }

        /*
         * (non-Javadoc)
         *
         * @see javax.servlet.ServletContext#getServlets()
         */
        @Override
        public Enumeration<Servlet> getServlets() {
            throw new UnsupportedOperationException();
        }

        /*
         * (non-Javadoc)
         *
         * @see javax.servlet.ServletContext#getSessionCookieConfig()
         */
        @Override
        public SessionCookieConfig getSessionCookieConfig() {
            throw new UnsupportedOperationException();
        }

        /*
         * (non-Javadoc)
         *
         * @see javax.servlet.ServletContext#log(java.lang.Exception, java.lang.String)
         */
        @Override
        public void log(Exception exception, String msg) {
            throw new UnsupportedOperationException();
        }

        /*
         * (non-Javadoc)
         *
         * @see javax.servlet.ServletContext#log(java.lang.String)
         */
        @Override
        public void log(String msg) {
            throw new UnsupportedOperationException();
        }

        /*
         * (non-Javadoc)
         *
         * @see javax.servlet.ServletContext#log(java.lang.String, java.lang.Throwable)
         */
        @Override
        public void log(String message, Throwable throwable) {
            throw new UnsupportedOperationException();
        }

        /*
         * (non-Javadoc)
         *
         * @see javax.servlet.ServletContext#removeAttribute(java.lang.String)
         */
        @Override
        public void removeAttribute(String name) {
            throw new UnsupportedOperationException();
        }

        /*
         * (non-Javadoc)
         *
         * @see javax.servlet.ServletContext#setAttribute(java.lang.String, java.lang.Object)
         */
        @Override
        public void setAttribute(String name, Object object) {
            throw new UnsupportedOperationException();
        }

        /*
         * (non-Javadoc)
         *
         * @see javax.servlet.ServletContext#setInitParameter(java.lang.String, java.lang.String)
         */
        @Override
        public boolean setInitParameter(String name, String value) {
            if (initParameters.containsKey(name)) {
                return false;
            } else {
                initParameters.put(name, value);
                return true;
            }
        }

        /*
         * (non-Javadoc)
         *
         * @see javax.servlet.ServletContext#setSessionTrackingModes(java.util.Set)
         */
        @Override
        public void setSessionTrackingModes(Set<SessionTrackingMode> sessionTrackingModes) {
            throw new UnsupportedOperationException();
        }

    }

    //private static final Logger log = Logger.getLogger(CmisServerApiTest.class);

    @Deployment
    public static Archive<?> createDeployment() {

        return ShrinkWrap.create(WebArchive.class, CmisServerApiTest.class.getSimpleName() + ".war")
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
                .setManifest(new File("src/main/webapp/META-INF/MANIFEST.MF"));

    }

    private Session session;

    @Before
    public void setUp() {

        /*
         * A set of stupid workarounds to make org.modeshape.web.jcr.RepositoryManager happy.
         */
        ModeShapeJcrDeployer d = new ModeShapeJcrDeployer();
        ServletContext servletContext = new DummyServletContext();
        servletContext.setInitParameter(RepositoryFactory.URL, "jndi:jcr");
        ServletContextEvent servletContextEvent = new ServletContextEvent(servletContext);
        d.contextInitialized(servletContextEvent);

        SessionFactoryImpl factory = SessionFactoryImpl.newInstance();
        Map<String, String> parameter = new HashMap<String, String>();

        // user credentials
        parameter.put(SessionParameter.USER, "admin");
        parameter.put(SessionParameter.PASSWORD, "admin");

        // connection settings
        parameter.put(SessionParameter.BINDING_TYPE, BindingType.LOCAL.value());
        parameter.put(SessionParameter.LOCAL_FACTORY, JcrServiceFactory.class.getName());
        parameter.put(SessionParameter.REPOSITORY_ID, "artifacts:default");
        // create session
        session = factory.createSession(parameter);
    }

    @Test
    public void shouldAccessRootFolder() throws Exception {
        Folder root = session.getRootFolder();

        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(PropertyIds.OBJECT_TYPE_ID, "cmis:folder");
        properties.put(PropertyIds.NAME, "f55");
        //System.out.println("Root: " + root);
        root.createFolder(properties);
    }

}
