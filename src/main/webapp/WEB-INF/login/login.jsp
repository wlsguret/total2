<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<section>
<div class="container">
<form action="/login/login.do" method="post">
    <div class="form-group">
      <label for="id">ID:</label>
      <input type="text" class="form-control" id="id" name="id">
    </div>
    <div class="form-group">
      <label for="password">Password:</label>
      <input type="password" class="form-control" id="password" name="password">
    </div>
    <a>회원가입</a>/<a>아이디</a> <a>비밀번호찾기</a>
    <button type="submit" class="btn btn-primary">로그인</button>
  </form>
 </div>
</section>