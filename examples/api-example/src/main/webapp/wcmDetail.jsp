<%
    /*
     * JBoss, a division of Red Hat
     * Copyright 2012, Red Hat Middleware, LLC, and individual
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
%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="org.gatein.wcm.api.WcmApi" %>
<%@ page import="org.gatein.wcm.api.services.WcmApiService" %>
<%@ page import="org.gatein.wcm.api.domain.Category" %>
<%@ page import="org.gatein.wcm.api.domain.Post" %>
<%@ page import="java.util.List" %>
<%@ page import="org.gatein.wcm.api.util.WcmUtils" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="description" content="">
    <meta name="author" content="">

    <title>GateIn WCM API example using Bootstrap</title>

    <!-- Bootstrap core CSS -->
    <link href="css/bootstrap.min.css" rel="stylesheet">

    <!-- Custom styles for this template -->
    <link href="css/jumbotron.css" rel="stylesheet">

</head>

<body>

<div class="navbar navbar-inverse navbar-fixed-top">
    <div class="container">
        <div class="navbar-header">
            <button type="button" class="navbar-toggle" data-toggle="collapse" data-target=".navbar-collapse">
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
            </button>
            <a class="navbar-brand" href="/api-example/wcmPage.jsp">GateIn WCM API example</a>
        </div>
        <div class="navbar-collapse collapse">
            <ul class="nav navbar-nav">
                <li class="active"><a href="/api-example/wcmPage.jsp">Home</a></li>
                <li><a href="/portal/classic/magazine">Portal</a></li>
                <li class="dropdown">
                    <a href="/portal/classic/magazine" class="dropdown-toggle" data-toggle="dropdown">TicketMonster's Magazine <b class="caret"></b></a>
                    <ul class="dropdown-menu">
                        <li><a href="/portal/classic/magazine/concert">Concert</a></li>
                        <li><a href="/portal/classic/magazine/theatre">Theatre</a></li>
                        <li><a href="/portal/classic/magazine/sporting">Sporting</a></li>
                    </ul>
                </li>
            </ul>
        </div><!--/.navbar-collapse -->
    </div>
</div>

<%
    /*
        Access to GateIn WCM API
     */
    String id = request.getParameter("id");
    String user = "anonymous";
    WcmApiService wcm = WcmApi.getInstance();
    if (id != null) {
        Post detail = wcm.findPost(new Long(id), user);
%>
<!-- Main jumbotron for a primary marketing message or call to action -->
<div class="jumbotron">
    <div class="container">
        <h1><%= detail.getTitle() %></h1>
        <p><img src="<%= WcmUtils.extractSrcImg(detail.getContent(), 0) %>" class="top-img"><%= detail.getExcerpt() %></p>
    </div>
</div>

<div class="container">
    <%
        String skipImg = WcmUtils.extractImg(detail.getContent(), 0);
        String content = detail.getContent();
        if (!"".equals(skipImg)) {
            content = content.replace(skipImg, "");
        }
    %>
    <p><%= content %></p>

    <hr>

    <footer>
        <p>&copy; TicketMonster's Magazine 2013</p>
    </footer>
</div>
<%
    }
%>
<!-- Bootstrap core JavaScript
================================================== -->
<!-- Placed at the end of the document so the pages load faster -->
<script src="js/jquery.js"></script>
<script src="js/bootstrap.min.js"></script>
</body>
</html>