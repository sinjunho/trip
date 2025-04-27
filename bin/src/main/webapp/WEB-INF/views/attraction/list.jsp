<%@page import="com.ssafy.trip.model.dto.Attraction"%>
<%@page import="java.util.List"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
</head>
<body>
<%@ include file="/WEB-INF/views/fragments/header.jsp"%>
	<h2>조회된 컨텐츠 목록</h2>
    <%
       List<Attraction> attrList = (List<Attraction>) session.getAttribute("attrList");
       if (attrList.size() != 0) {
    	   
    	   out.println("<h3>유형 : %s</h3>".formatted(attrList.get(0).getContentTypeName()));
    	   out.println("<h3>시 : %s</h3>".formatted(attrList.get(0).getSido()));
    	   out.println("<h3>구군 : %s</h3>".formatted(attrList.get(0).getGugun()));
    	   
           for (Attraction attr : attrList) {
    %>
                <p><%= attr %></p>  <%-- attr.toString()이 자동 호출됨 --%>
    <%
            }
        } else {
    %>
        <p>조회된 데이터가 없습니다.</p>
    <%
        }
    %>
  
</body>
</html>