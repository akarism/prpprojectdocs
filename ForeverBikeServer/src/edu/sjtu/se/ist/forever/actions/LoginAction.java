package edu.sjtu.se.ist.forever.actions;

import javax.servlet.http.HttpSession;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class LoginAction extends ForeverSupport {

	private static final long serialVersionUID = 1L;
	private String loginfeedback;
	private HttpSession session;

	public String execute() throws Exception {
		System.out.println("Enter into login");
		session = this.getRequest().getSession();
		String userName = this.getRequest().getParameter(
				ParamConstants.USER_NAME);
		String password = this.getRequest().getParameter(
				ParamConstants.PASSWORD);
		if (session.getAttribute("Login") != null) {
			// already exists a session
			if (!session.getAttribute(ObjectConstants.USER_NAME).equals(
					userName)
					|| !session.getAttribute(ObjectConstants.PASSWORD).equals(
							password)) {
				// authentication fail
				return ActionResultConstants.FAILED;
			}
			// authentication success
			loginfeedback = session.getId();
		} else if (login(userName, password)) {
			// no session but authentication success
			loginfeedback = session.getId();
		} else {
			// authentication fail
			return ActionResultConstants.FAILED;
		}
		System.out.println(loginfeedback);
		return ActionResultConstants.SUCCESS;
	}

	public boolean login(String userName, String password) {
		Connection conn = null;
		Statement stmt = null;
		String sql = "select id_user,passwd from t_user where username='"
				+ userName + "';";
		ResultSet rs = null;
		boolean result = false;
		try {
			// Class.forName("oracle.jdbc.driver.OracleDriver");
			// String url = "jdbc:oracle:thin:@localhost:1521:BIKE";
			// conn = DriverManager.getConnection(url, "HR", "HR");
			Class.forName("com.mysql.jdbc.Driver");
			String url = "jdbc:mysql://localhost:3306/bike?useUnicode=true&amp;characterEncoding=utf8";
			conn = DriverManager.getConnection(url, "bike", "bike");
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);
			rs.next();
			if (rs.getString("passwd").equals(password)) {
				if (session != null) {
					session.setAttribute("Login", "true");
					session.setAttribute(ObjectConstants.USER_ID, rs.getString("id_user"));
					session.setAttribute(ObjectConstants.USER_NAME, userName);
					session.setAttribute(ObjectConstants.PASSWORD, password);
				}
				result = true;
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return false;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		} finally {
			try {
				if (rs != null) {
					rs.close();
					rs = null;
				}
				if (stmt != null) {
					stmt.close();
					stmt = null;
				}
				if (conn != null) {
					conn.close();
					conn = null;
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	public String getLoginfeedback() {
		return loginfeedback;
	}

	public void setLoginfeedback(String loginfeedback) {
		this.loginfeedback = loginfeedback;
	}

	public static void main(String[] args) {
		LoginAction lg = new LoginAction();
		System.out.println(lg.login("user", "user"));
	}
}
