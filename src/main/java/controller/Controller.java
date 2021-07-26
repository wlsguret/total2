package controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import board.dao.BoardDAO;
import board.model.BoardVO;
import board.model.PageBoard;
import login.dao.LoginDAO;

@WebServlet("/")
public class Controller extends HttpServlet {
	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		String url = request.getRequestURI().substring(1);
		String[] urls = url.split("/");
		String page = "";

		System.out.println("url:" + url);
		if (urls[0].equals("index")) {
			page = "/home/" + "section.jsp";
		} else if (urls[0].equals("home")) {
			page = "/home/" + "section.jsp";
		} else if (urls[0].equals("intro")) {
			page = "/intro/" + "intro.jsp";
		} else if (urls[0].equals("realchat")) {
			page = "/realchat/" + "realchat.jsp";
		} else if (urls[0].equals("board")) {
			page = "/board/";
			if (urls.length == 1) {
				BoardDAO dao = new BoardDAO();
				int requestPage = 1;
				String _requestPage = request.getParameter("requestPage");
				if (_requestPage != null && !_requestPage.equals("")) {
					requestPage = Integer.parseInt(_requestPage);
				}
				PageBoard pageboard = dao.list(requestPage);
				System.out.println(pageboard.getList());
				request.setAttribute("pageboard", pageboard);
				page += "board.jsp";
			} else if (urls[1].equals("insert")) {
				page += "writeForm.jsp";
			} else if (urls[1].equals("insert.do")) {
				String title = request.getParameter("title");
				String content = request.getParameter("content");
				String id = request.getParameter("writeName");
				BoardVO board = new BoardVO(title, content, id, id);
				BoardDAO dao = new BoardDAO();
				dao.insert(board);
				PageBoard pageboard = dao.list(1);
				request.setAttribute("pageboard", pageboard);
				page += "board.jsp";
			} else if (urls[1].equals("read")) {
				String _idx = request.getParameter("idx");
				String requestPage = request.getParameter("requestPage");
				int idx = 0;
				if (_idx != null && !_idx.equals("")) {
					idx = Integer.parseInt(_idx);
					BoardDAO dao = new BoardDAO();
					dao.readcountUpdate(idx);
					BoardVO board = dao.select(idx);
					request.setAttribute("board", board);
					request.setAttribute("requestPage", requestPage);
					page = "/board/";
					page += "read.jsp";
				}
			} else if (urls[1].equals("update")) {
				String _idx = request.getParameter("idx");
				String requestPage = request.getParameter("requestPage");
				int idx = 0;
				if (_idx != null && !_idx.equals("")) {
					idx = Integer.parseInt(_idx);
					BoardDAO dao = new BoardDAO();
					BoardVO board = dao.select(idx);
					request.setAttribute("board", board);
					request.setAttribute("requestPage", requestPage);
					page = "/board/";
					page += "update.jsp";
				}
			} else if (urls[1].equals("update.do")) {
				int idx = Integer.parseInt(request.getParameter("idx"));
				String requestPage = request.getParameter("requestPage");
				String title = request.getParameter("title");
				String content = request.getParameter("content");
				BoardDAO dao = new BoardDAO();
				int result = dao.update(idx, title, content);
				if (result > 0) {
					System.out.println("Controller@update.do success");
					response.sendRedirect("/board?requestPage=" + requestPage);
					return;
				}
			} else if (urls[1].equals("delete")) {
				int idx = Integer.parseInt(request.getParameter("idx"));
				String requestPage = request.getParameter("requestPage");
				BoardDAO dao = new BoardDAO();
				int result = dao.delete(idx);
				if (result > 0) {
					System.out.println("Controller@delete success");
					response.sendRedirect("/board?requestPage=" + requestPage);
					return;
				}
			} else if (urls[1].equals("replyForm")) {
				int idx = Integer.parseInt(request.getParameter("idx"));
				int groupid = Integer.parseInt(request.getParameter("groupid"));
				int depth = Integer.parseInt(request.getParameter("depth"));
				depth++;
				int reOrder = Integer.parseInt(request.getParameter("re_order"));
				reOrder++;
				String title = request.getParameter("title");
				String requestPage = request.getParameter("requestPage");
				BoardVO board = new BoardVO();
				board.setIdx(idx);
				board.setGroupid(groupid);
				board.setDepth(depth);
				board.setReOrder(reOrder);
				board.setTitle(title);
				//System.out.println(board);
				request.setAttribute("board", board);
				request.setAttribute("requestPage", requestPage);
				page += "replyForm.jsp";
			} else if (urls[1].equals("reply.do")) {
				int idx = Integer.parseInt(request.getParameter("parent_idx"));
				int groupid = Integer.parseInt(request.getParameter("groupid"));
				int depth = Integer.parseInt(request.getParameter("depth"));
				int reOrder = Integer.parseInt(request.getParameter("re_order"));
				String title = request.getParameter("title");
				String content = request.getParameter("content");
				String writeId = request.getParameter("write_id");
				String writeName = request.getParameter("write_name");
				String requestPage = request.getParameter("requestPage");
				BoardVO board = new BoardVO();
				board.setIdx(idx);
				board.setGroupid(groupid);
				board.setDepth(depth);
				board.setReOrder(reOrder);
				board.setTitle(title);
				board.setContent(content);
				board.setWriteId(writeId);
				board.setWriteName(writeName);
				BoardDAO dao = new BoardDAO();
				int result = dao.replyInsert(board);
				if (result > 0) {
					response.sendRedirect("/board?requestPage=" + requestPage);
					return;
				}
			}

		} else if (urls[0].equals("login")) {
			if (urls.length == 1) {
				page = "/login/";
				page += "login.jsp";
			} else if (urls[1].equals("login.do")) {
				LoginDAO logindao = new LoginDAO();
				int result = logindao.loginCheck(request.getParameter("id"), request.getParameter("password"));
				if (result == 1) {
					request.getSession().setAttribute("id", request.getParameter("id"));
				}
				page = "/home/" + "section.jsp";
			} else if (urls[1].equals("logout")) {
				request.getSession().invalidate();
				page = "/home/" + "section.jsp";
			} else {
				page = "/login/";
				page += "login.jsp";
			}
		} else {
			page = "/home/" + "section.jsp";
		}
		request.setAttribute("section", page);
		request.getRequestDispatcher("/WEB-INF/index.jsp").forward(request, response);
	}

}
