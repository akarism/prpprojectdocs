package edu.sjtu.se.ist.forever.actions;

import java.sql.Connection;
import java.util.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;

import javax.servlet.http.HttpSession;

import org.json.JSONException;

public class SelectShop extends ForeverSupport{
	
	private static final long serialVersionUID = 1L;
	private String shopData;
	private HttpSession session;

	public String execute() throws Exception {
		session = this.getRequest().getSession();
		if (session.getAttribute("Login") != null) {
			String userID = (String) this.session.getAttribute(ObjectConstants.USER_ID);
			String time = this.getRequest().getParameter(ParamConstants.Time);
			String shopID = this.getRequest().getParameter(ParamConstants.SHOP);
			select(userID,time,shopID);
		} else {
			// authentication fail
			return ActionResultConstants.FAILED;
		}
		return ActionResultConstants.SUCCESS;
	}
	
	public void select(String userID,String time,String shopID) throws SQLException
	{
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			}
		Connection conn = java.sql.DriverManager.getConnection(
			    "jdbc:mysql://59.78.58.190:3306/bike", "remote", "root");
		PreparedStatement pstmt = conn.prepareStatement("select * from t_time where start<=? and end>?");
		pstmt.setString(1, time);
		pstmt.setString(2, time);
		ResultSet rs = pstmt.executeQuery();
		String timeID="";
		if (rs.next())
		{
			timeID = rs.getString("id_time");
		}
		
		String[] classes = new String[4];
		pstmt = conn.prepareStatement("select * from t_shop where id_shop=?");
		pstmt.setString(1, shopID);
		rs = pstmt.executeQuery();
		if (rs.next())
		{
			classes[0] = rs.getString("class_1");
			classes[1] = rs.getString("class_2");
			classes[2] = rs.getString("class_3");
			classes[3] = rs.getString("class_4");
		}
		
		SimpleDateFormat bartDateFormat =new SimpleDateFormat("yyyy-MM-dd");        
		Date date = new Date();
		pstmt = conn.prepareStatement("select * from t_record where date=? and id_user=? and id_time=?");
		pstmt.setString(1, bartDateFormat.format(date));
		pstmt.setString(2, userID);
		pstmt.setString(3, timeID);
		rs = pstmt.executeQuery();
		if (rs.next())
		{
			if (rs.getString("class_1").equals("1"))
				classes[0] = "1";
			if (rs.getString("class_2").equals("1"))
				classes[1] = "1";
			if (rs.getString("class_3").equals("1"))
				classes[2] = "1";
			if (rs.getString("class_4").equals("1"))
				classes[3] = "1";
			pstmt = conn.prepareStatement("update t_record set class_1=?,class_2=?,class_3=?,class_4=? where date=? and id_user=? and id_time=?");
			pstmt.setString(1, classes[0]);
			pstmt.setString(2, classes[1]);
			pstmt.setString(3, classes[2]);
			pstmt.setString(4, classes[3]);
			pstmt.setString(5, bartDateFormat.format(date));
			pstmt.setString(6, userID);
			pstmt.setString(7, timeID);
			pstmt.executeUpdate();
		}
		else
		{
			pstmt = conn.prepareStatement("insert into t_record(date,id_user,id_time,id_temperature,class_1,class_2,class_3,class_4) values(?,?,?,NULL,?,?,?,?)");
			pstmt.setString(1, bartDateFormat.format(date));
			pstmt.setString(2, userID);
			pstmt.setString(3, timeID);
			pstmt.setString(4, classes[0]);
			pstmt.setString(5, classes[1]);
			pstmt.setString(6, classes[2]);
			pstmt.setString(7, classes[3]);
			pstmt.executeUpdate();
		}
	}
	public static void main(String[] args) throws SQLException, JSONException
	{
		SelectShop ss = new SelectShop();
		ss.select("1", "06:00:00", "3");
	}
}