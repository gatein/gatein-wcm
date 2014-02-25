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

package org.example;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.gatein.wcm.api.WcmApi;
import org.gatein.wcm.api.domain.Category;
import org.gatein.wcm.api.services.WcmApiService;

@WebServlet(value="/test", loadOnStartup=1)
public class MyWcmServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/plain");
        PrintWriter out = response.getWriter();
        out.println(new Date().toString());
        try {
            WcmApiService wcm = WcmApi.getInstance();
            out.println(wcm.toString());
            long t0, t1;
            t0 = System.currentTimeMillis();
            List<Category> listCategories = wcm.findRootCategories("root");
            t1 = System.currentTimeMillis();
            out.println("findRootCategories(): " + (t1 - t0) + " ms");
            for (Category c : listCategories) {
                out.println(c);
            }
        } catch (Exception e) {
            out.write(e.toString());
        }

    }
}
