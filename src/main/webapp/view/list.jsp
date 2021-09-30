<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
    <title>User Management Application</title>
</head>
<body>
<h1>User Management</h1>
<h2>
    <a href="/users">Home</a>
</h2>
<h2>
    <a href="/users?action=create">Add New User</a>
</h2>
<h2>
    <a href="/users?action=sort">Sort by name</a>
</h2>
<form method="post" action="/users">
    <fieldset>
        <input name="country" width="200px" placeholder="Viet Nam">
        <input type="submit" name="action" value="search"/>
    </fieldset>
</form>
<div align="center">
    <table border="1" cellpadding="5">
        <caption><h2>List of Users</h2></caption>
        <tr>
            <th>STT</th>
            <th>ID</th>
            <th>Name</th>
            <th>Email</th>
            <th>Country</th>
            <th>Actions</th>
        </tr>
        <c:forEach var="user" items="${listUser}" varStatus="index">
            <tr>
                <td><c:out value="${index.index +1}"/></td>
                <td><c:out value="${user.id}"/></td>
                <td><c:out value="${user.name}"/></td>
                <td><c:out value="${user.email}"/></td>
                <td><c:out value="${user.country}"/></td>
                <td>
                    <a href="/users?action=edit&id=${user.id}">Edit</a>
                    <a href="/users?action=delete&id=${user.id}">Delete</a>
                </td>
            </tr>
        </c:forEach>
    </table>
</div>
</body>
</html>