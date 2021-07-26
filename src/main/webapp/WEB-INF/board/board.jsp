<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<script>
	function dataCheck() {
		if (document.getElementById("search").value == "") {
			alert("검색할 단어를 입력하세요");
			document.getElementById("search").focus();
			return;
		}
		document.form.submit();
	}
</script>
<!-- 로그인이 되지 않을 경우 페이지 이동 -->
<c:if test="${empty sessionScope.id}">
<!-- 왜 독작안하는지 모르겠음  -->
<section>
로그인 후 이용가능한 페이지 입니다.
</section>
</c:if>
<!-- ----------------- -->
<c:if test="${not empty sessionScope.id}">
	<section>
		<div class="container">
			<h2>
				<button class="btn" onclick="location.href='/index'">
					<i class="fa fa-home"></i>
				</button>
				메시지 전체 리스트
			</h2>
			<!-- 검색리스트 -->
			<form name=form action="/board/searchList" method=post>
				<div class="form-group">
	
					<select name=field>
						<option value=title>제목</option>
						<option value=content>내용</option>
						<option value=write_name>작성자</option>
					</select> <input onmouseover="this.focus()" type="text" id="search"
						name="search" placeholder="찾을 내용을 입력하세요">
					<button type="button" onclick=dataCheck() class="btn btn-default">검색</button>
				</div>
			</form>
			<!-- 검색리스트  -->
	
			<table class="table">
				<!-- 페이지번호 표시 -->
				<tr align="right">
					<td colspan="5">
						${pageboard.articleCount-pageboard.firstRow+1}-${pageboard.articleCount-pageboard.endRow+1}
						[${pageboard.requestPage}/${pageboard.totalPage }]</td>
				</tr>
	
				<tr class="success">
					<th>번호</th>
					<th>제목</th>
					<th>작성자</th>
					<th>작성일</th>
					<th>조회수</th>
				</tr>
				<c:forEach var="board" items="${pageboard.list}">
					<tr onmouseover="this.style.backgroundColor='lightgray'"
						onmouseout="this.style.backgroundColor='white'"
						onclick="location.href='/board/read?idx=${board.idx}&requestPage=${pageboard.requestPage}'">
						<td>${board.idx}</td>
						<td><c:if test="${board.reOrder>0}">
								<%-- <c:forEach begin="1" end="${board.depth}">-</c:forEach>&gt; --%>
								<c:forEach begin="1" end="${board.reOrder}">&nbsp;&nbsp;&nbsp;</c:forEach>
								<img style="width: 42px; height: 15px" src="/img/reply_icon.gif" />
							</c:if> ${board.title}</td>
						<td>${board.writeName}</td>
						<td>${board.writeDay}</td>
						<td>${board.readcount}</td>
					</tr>
				</c:forEach>
				<!-- page list -->
				<tr>
					<td colspan=4 align=center valign="center">
						<ul class="pagination">
	
							<c:if test="${pageboard.requestPage!=1}">
								<li class="page-item"><a class="page-link"
									href="/board?requestPage=${pageboard.requestPage-1}">이전페이지</a></li>
							</c:if>
	
							<c:forEach var="i" begin="${pageboard.beginPage}"
								end="${pageboard.endPage}">
								<li class="page-item"><a class="page-link"
									href="/board?requestPage=${i}">${i}</a></li>
							</c:forEach>
	
							<c:if test="${pageboard.totalPage!=pageboard.requestPage}">
								<li class="page-item"><a class="page-link"
									href="/board?requestPage=${pageboard.requestPage+1}">다음페이지</a></li>
							</c:if>
	
						</ul>
					</td>
					<td>
						<button onclick="location.href='/board/insert'">글쓰기</button>
					</td>
				</tr>
				<!-- end page list -->
			</table>
	
		</div>
	</section>
</c:if>