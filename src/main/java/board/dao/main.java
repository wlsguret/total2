package board.dao;

import board.model.BoardVO;

public class main {

	public static void main(String[] args) {
		BoardDAO dao = new BoardDAO();
//		BoardVO board = new BoardVO();
//		board.setTitle("����20");
//		board.setContent("�۳���");
//		board.setWriteId("user2");
//		board.setWriteName("user2");
//		System.out.println(dao.insert(board));
		for(BoardVO vo : dao.select()) {
			System.out.println(vo);
		}
		System.out.println(dao.checkParent(22));
//		dao.select(2); 

	}

}
