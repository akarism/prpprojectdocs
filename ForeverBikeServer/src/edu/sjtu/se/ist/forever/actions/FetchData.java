package edu.sjtu.se.ist.forever.actions;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;

import edu.sjtu.se.ist.forever.schedule.*;

import javax.servlet.http.HttpSession;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class FetchData  extends ForeverSupport{
	
	private static final long serialVersionUID = 1L;
	private String fetchData;
	private HttpSession session;

	public String execute() throws Exception {
		session = this.getRequest().getSession();
		if (session.getAttribute("Login") != null) {
			String userID = (String) this.session.getAttribute(ObjectConstants.USER_ID);
			String time = this.getRequest().getParameter(ParamConstants.Time);
			String posx = this.getRequest().getParameter(ParamConstants.POSX);
			String posy = this.getRequest().getParameter(ParamConstants.POSY);
			fetch(userID,time,posx,posy);
		} else {
			// authentication fail
			return ActionResultConstants.FAILED;
		}
		return ActionResultConstants.SUCCESS;
	}
	public void fetch(String userID,String time,String posx,String posy) throws SQLException, JSONException
	{
		JSONArray netPointArray = new JSONArray();
		
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
		CalWeightTask task = new CalWeightTask();
		double[] res = task.execute(userID, timeID);
		for (int i = 0; i<res.length; i++)
		{
			if (res[i]>=0.5)
				res[i] = 1;
			else res[i] = 0;
		}
		pstmt = conn.prepareStatement("select * from t_shop where latitude>=? and latitude<=? and longitude>=? and longitude<=? and ((class_1=1 and class_1=?) or (class_2=1 and class_2=?) or (class_3=1 and class_3=?) or (class_4=1 and class_4=?))");
		pstmt.setDouble(1, Double.parseDouble(posx)-1);
		pstmt.setDouble(2, Double.parseDouble(posx)+1);
		pstmt.setDouble(3, Double.parseDouble(posy)-1);
		pstmt.setDouble(4, Double.parseDouble(posy)+1);
		pstmt.setInt(5, (int)res[0]);
		pstmt.setInt(6, (int)res[1]);
		pstmt.setInt(7, (int)res[2]);
		pstmt.setInt(8, (int)res[3]);
		rs = pstmt.executeQuery();
		while (rs.next()) {
			//create a JSON Object
			JSONObject netPoint = new JSONObject();
			netPoint.put(ObjectConstants.STATION_NID, rs.getString("id_shop"));
			netPoint.put(ObjectConstants.STATION_NAME, rs.getString("shop_name"));
			netPoint.put(ObjectConstants.SHOP_LOCATION, rs.getString("shop_location"));
			netPoint.put(ObjectConstants.SHOP_DESCRIBE, rs.getString("describe"));
			netPoint.put(ObjectConstants.LATITUDE, rs.getString("latitude"));
			netPoint.put(ObjectConstants.LONGITUDE, rs.getString("longitude"));
			//add this object to json array
			netPointArray.put(netPoint);
		}
		fetchData = netPointArray.toString();
		System.out.println(fetchData);
	}

	public void setFetchData(String fetchData) {
		this.fetchData = fetchData;
	}

	public String getFetchData() {
		return fetchData;
	} 

	public static void main(String[] args) throws SQLException, JSONException
	{
		FetchData fd = new FetchData();
		fd.fetch("2", "06:00:00", "20", "128");
	}
}
