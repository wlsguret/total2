package login.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginDAO {
	Connection con;
	public LoginDAO() {
		String url = "jdbc:oracle:thin:@localhost:1521:xe";
	    String DBuser = "testuser";
	    String DBpassword	="1111";
	    try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			con = DriverManager.getConnection(url,DBuser,DBpassword);
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
	    if(con == null) {
	    	System.out.println("loginDAO.java LoginDAO() error!");
	    }
	}
	
	public int loginCheck(String id, String password) {
		String sql = "select * from member where id=? and password=?";
	    PreparedStatement ps;
	    ResultSet rs;
	    int result;
		try {
			ps = con.prepareStatement(sql);
			ps.setString(1, id);
			ps.setString(2, password);
			rs = ps.executeQuery();
			if(rs.next()){
				result = 1;
			} else {
				result = 0;
			}
			rs.close();
			ps.close();
			con.close();
			return result;
		} catch (SQLException e) {
			e.printStackTrace();
			return 0;
		} 
	}
}
