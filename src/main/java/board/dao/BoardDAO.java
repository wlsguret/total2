package board.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import board.model.BoardVO;
import board.model.PageBoard;

public class BoardDAO {

	public BoardDAO() {
	}

	public Connection connect() {
		Connection con = null;
		String url = "jdbc:oracle:thin:@localhost:1521:xe";
		String DBuser = "testuser";
		String DBpassword = "1111";
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			con = DriverManager.getConnection(url, DBuser, DBpassword);
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
		if (con == null) {
			System.out.println("BoardDAO connect() error!");
		}
		return con;
	}

	public void close(Connection con, PreparedStatement psmt, ResultSet rs) {
		if (rs != null) {
			try {
				rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		close(con, psmt);
	}

	public void close(Connection con, PreparedStatement psmt) {
		try {
			psmt.close();
			con.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public int insert(BoardVO board) {
		Connection con = connect();
		PreparedStatement psmt = null;
		int result = 0;
		String sql = null;
		try {
			con.setAutoCommit(false);
			sql = "insert into board values(";
			sql += "board_idx_seq.nextval,";
			sql += "?,?,0,";
			sql += "board_groupid_seq.nextval,0,0,";
			sql += "0,";
			sql += "?,?,sysdate";
			sql += ")";
			psmt = con.prepareStatement(sql);
			psmt.setString(1, board.getTitle());
			psmt.setString(2, board.getContent());
			psmt.setString(3, board.getWriteId());
			psmt.setString(4, board.getWriteName());

			result = psmt.executeUpdate();

			if (result > 0) {
				con.commit();
				System.out.println("sql 글 입력 성공");
			} else {
				con.rollback();
				System.out.println("sql 글 입력 실패");
			}
			close(con, psmt);
		} catch (Exception e) {
			try {
				con.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}
		return result;
	}

	public List<BoardVO> select() {
		Connection con = connect();
		List<BoardVO> list = new ArrayList<BoardVO>();
		String sql = "select * from board order by groupid desc";
		try {
			PreparedStatement psmt = con.prepareStatement(sql);
			ResultSet rs = psmt.executeQuery();
			while (rs.next()) {
				list.add(new BoardVO(rs.getInt("idx"), rs.getString("title"), rs.getString("content"),
						rs.getInt("readcount"), rs.getInt("groupid"), rs.getInt("depth"), rs.getInt("re_order"),
						rs.getInt("isdel"), rs.getString("write_id"), rs.getString("write_name"),
						rs.getDate("write_day")));
			}
			close(con, psmt, rs);
		} catch (SQLException e) {
			System.out.println("BoardDAO.select() error!");
			e.printStackTrace();
		}
		return list;
	}

	public BoardVO select(int idx) {
		Connection con = connect();
		BoardVO board = null;
		String sql = "select * from board where idx=?";
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			pstmt = con.prepareStatement(sql);
			pstmt.setInt(1, idx);
			rs = pstmt.executeQuery();
			if (rs.next()) {
				board = new BoardVO();
				board.setIdx(rs.getInt("idx"));
				board.setTitle(rs.getString("title"));
				board.setContent(rs.getString("content"));
				board.setReadcount(rs.getInt("readcount"));
				board.setGroupid(rs.getInt("groupid"));
				board.setDepth(rs.getInt("depth"));
				board.setReOrder(rs.getInt("re_order"));
				board.setIsdel(rs.getInt("isdel"));
				board.setWriteId(rs.getString("write_id"));
				board.setWriteName(rs.getString("write_name"));
				board.setWriteDay(rs.getDate("write_day"));
			}
			close(con, pstmt, rs);
		} catch (Exception e) {
		}
		return board;
	}

	public PageBoard list(int requestPage) {
		Connection con = connect();
		PreparedStatement psmt = null;
		ResultSet rs = null;
		String sql = null;
		PageBoard pageboard = null;
		// 페이지 관련 정보사항
		// 요청 페이지 인자전달(requestPage)
		int totalPage = 0; // 전체 페이지
		int beginPage = 0; // 시작 페이지
		int endPage = 0; // 마지막 페이지
		int firstRow = 0; // 시작 글번호
		int endRow = 0; // 끝 글번호
		int articleCount = 0;// 전체 글의수
		int countPerPage = 10;
		List<BoardVO> list = new ArrayList<BoardVO>();
		try {
			// 1.전체 게시물 수 구하기(articleCount)
			sql = "select count(*) from board";
			psmt = con.prepareStatement(sql);
			rs = psmt.executeQuery();
			if (rs.next()) {
				articleCount = rs.getInt(1);
			} else {
				articleCount = 0;
			}
			System.out.println("게시글수:" + articleCount);
			// 2.전체페이지 수(totalPage)
			totalPage = articleCount / countPerPage;
			if (articleCount % countPerPage > 0) {
				totalPage++;
			}
			System.out.println("전체페이지:" + totalPage);
			System.out.println("현재페이지:"+ requestPage);
			// 3.요청한 페이지에 대한 시작 글번호, 끝 글번호(firstRow, endRow)
			firstRow = (requestPage - 1) * countPerPage + 1;
			endRow = firstRow + countPerPage - 1;
			System.out.printf("firstRow:%d, endRow:%d\n", firstRow, endRow);
			// 4.시작페이지번호, 끝페이지 번호(beginPage, endPage)
			if (totalPage > 0) {
				beginPage = (requestPage - 1) / countPerPage * countPerPage + 1; // beginPage=requestPage-2;
				endPage = beginPage + 4;
				if (endPage > totalPage)
					endPage = totalPage;
			}
			System.out.printf("beginPage:%d, endPage:%d\n", beginPage, endPage);
			// 5.페이지에 해당하는 리스트(firstRow, endRow)
			sql = "select c.* from " + "(select rownum rnum, b.* from "
					+ "(select * from board a order by a.groupid desc, a.depth asc, a.idx asc) b where rownum <= ?) c "
					+ "where rnum >= ? ";
			psmt = con.prepareStatement(sql);
			psmt.setInt(1, endRow); // 10큰수
			psmt.setInt(2, firstRow);// 1작은수
			rs = psmt.executeQuery();
			// 6.DB의 리스트를 board객체에 담아 전송
			while (rs.next()) {
				// System.out.println("check");
				BoardVO board = new BoardVO();
				board.setIdx(rs.getInt("idx"));
				board.setTitle(rs.getString("title"));
				board.setContent(rs.getString("content"));
				board.setReadcount(rs.getInt("readcount"));
				board.setGroupid(rs.getInt("groupid"));
				board.setDepth(rs.getInt("depth"));
				board.setReOrder(rs.getInt("re_order"));
				board.setIsdel(rs.getInt("isdel"));
				board.setWriteId(rs.getString("write_id"));
				board.setWriteName(rs.getString("write_name"));
				board.setWriteDay(rs.getDate("write_day"));
				list.add(board);
			}
			pageboard = new PageBoard(list, requestPage, articleCount, totalPage, firstRow, endRow, beginPage, endPage);
			//System.out.println(pageboard.getList().toString());
			close(con, psmt, rs);
			// conn.commit();
		} catch (SQLException e) {
			System.out.println("BoardDAO.select(int requestPage) error!");
			e.printStackTrace();
		}
		return pageboard;
	}

	public int update(int idx, String title, String content) {
		Connection con = connect();
		PreparedStatement ps;
		try {
			String sql = "update board set title=?, content=? where idx=?";
			ps = con.prepareStatement(sql);
			ps.setString(1, title);
			ps.setString(2, content);
			ps.setInt(3, idx);
			return ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("BoardDAO.java update() error!");
			return -1;
		}
	}

	public int delete(int idx) {
		Connection con = connect();
		PreparedStatement ps;
		try {
			String sql = "delete from board where idx=?";
			ps = con.prepareStatement(sql);
			ps.setInt(1, idx);
			return ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("BoardDAO.java update() error!");
			return -1;
		}
	}

	/* replyInsert함수에서 이용 */
	public boolean checkParent(int idx) {
		BoardVO board = null;
		Connection con = null;
		PreparedStatement psmt = null;
		ResultSet rs = null;
		String sql = null;
		int result = 0;
		try {
			con = connect();
			sql = "select count(*) from board where idx=? and isdel=0";
			psmt = con.prepareStatement(sql);
			psmt.setInt(1, idx);
			rs = psmt.executeQuery();
			if (rs.next()) {
				result = rs.getInt(1);
			}
			if (result != 1) {
				return false;
			}
			close(con, psmt, rs);
		} catch (Exception e) {
		}
		return true;
	}

	/* replyInsert함수에서 이용 */
	public void reply_before_update(int depth, int groupid) {
		Connection con = null;
		PreparedStatement psmt = null;
		String sql = null;
		try {
			con = connect();
			sql = "update board set depth=depth+1 where groupid=? and depth>=?";
			psmt = con.prepareStatement(sql);
			psmt.setInt(1, groupid);
			psmt.setInt(2, depth);
			psmt.executeUpdate();
			close(con, psmt);
		} catch (Exception e) {
		}
	}

	/* 위의 두 함수 이용 */
	public int replyInsert(BoardVO board) {
		// 댓글은
		// 1.부모글이 존재하는 여부 확인 - checkParent(board.getIdx());
		// 2.댓글과 상관이 있는 글에 대해 groupid, depth,[re_order]값을 변경 -
		// reply_before_update(depth, groutid);
		// 3.댓글이 등록 //댓글 insert();
		Connection con = null;
		PreparedStatement psmt = null;
		String sql = null;
		int result = 0;
		try {
			con = connect();
			con.setAutoCommit(false);
			// 부모글 존재여부 확인
			if (!checkParent(board.getIdx())) {
				con.rollback();
				return 0;
			}
			System.out.println("부모글 확인");
			// 댓글과 관련된 글에 대해 업데이트
			reply_before_update(board.getDepth(), board.getGroupid());
			System.out.println("관련 댓글 입력 완료");
			// insert작업
			sql = "insert into board values(";
			sql += "board_idx_seq.nextval,";
			sql += "?,?,0,"; // 1.title, 2.content, readcount
			sql += "?,?,?,"; // 3.groupid, 4.depth, 5.re_order
			sql += "0,"; // 삭제여부
			sql += "?,?,sysdate"; // 6.write_id, 7.write_name, 8.날짜
			sql += ")";
			psmt = con.prepareStatement(sql);
			psmt.setString(1, board.getTitle());
			psmt.setString(2, board.getContent());
			psmt.setInt(3, board.getGroupid());
			psmt.setInt(4, board.getDepth());
			psmt.setInt(5, board.getReOrder());
			psmt.setString(6, board.getWriteId());
			psmt.setString(7, board.getWriteName());

			result = psmt.executeUpdate();
			System.out.println("result:" + result);
			if (result == 0) {
				con.rollback();
				System.out.println("댓글 실패");
				return 0;
			} else {
				con.commit();
				System.out.println("댓글 입력 완료");
			}

		} catch (Exception e) {
			try {
				con.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}
		return result;
	}

	public int readcountUpdate(int idx) {
		Connection con = connect();
		PreparedStatement ps;
		try {
			String sql = "update board set readcount=readcount+1 where idx=?";
			ps = con.prepareStatement(sql);
			ps.setInt(1, idx);
			return ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("BoardDAO.java readcountUpdate() error!");
			return -1;
		}
		
	}
}
