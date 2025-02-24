<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="ISO-8859-1">
    <title></title>
    <link rel="stylesheet" href="styles.css">
    <style>
        .error-message {
            color: red;
            font-weight: bold;
        }
        .exception-details {
            color: darkred;
            font-size: 14px;
            background-color: #f8d7da;
            border: 1px solid red;
            padding: 10px;
            margin-top: 10px;
            white-space: pre-wrap;
        }
    </style>
</head>
<body>
    <h2>Error Page</h2>

    <!-- Display error message if present -->
    <c:if test="${not empty error}">
        <div class="error-message">
            <p>Error: ${error}</p>
        </div>

        <!-- Display exception details if present -->
        <c:if test="${not empty exception}">
            <div class="exception-details">
                <p><strong>Exception Details:</strong></p>
                <p>${exception}</p>
                
            </div>
        </c:if>
    </c:if>
    <hr>
<div style="text-align: center;">
    <h2>User already exists</h2>
</div>

 

</body>
</html>
