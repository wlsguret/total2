<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>	
<nav>
	<div class="navbar_log" style="align-items: center;">
		<a href="#"> <i class="fab fa-apple"></i>apple
		</a>
	</div>
	<ul class="navbar_menu">
		<a href="/home"><li>HOME</li></a>
		<a href="/intro"><li>INTRO</li></a>
		<a href="/realchat"><li>REALCHAT</li></a>
		<a href="/board"><li>BOARD</li></a>
		<c:if test="${empty id}">
			<a href="/login"><li>LOGIN</li></a>
		</c:if>
		<c:if test="${not empty id}">
			<a href="/login/logout"><li>LOGOUT[${id }]</li></a>
		</c:if>
	</ul>
	<ul class="navbar_icon">
		<li><a href=""><i class="fas fa-birthday-cake"></i></a></li>
		<li><a href=""><i class="far fa-image"></i></a></li>
		<li><a href=""><i class="fas fa-camera"></i></a></li>
	</ul>
	<i class="fas fa-bars" id="icon_bar"></i>
</nav>