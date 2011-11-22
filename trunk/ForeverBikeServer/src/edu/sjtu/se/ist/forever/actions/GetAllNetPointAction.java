package edu.sjtu.se.ist.forever.actions;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import org.json.*;

public class GetAllNetPointAction extends ForeverSupport {

	/**
	 * 
	 */
	private String allNetPoint;
	private static final long serialVersionUID = 1L;

	public String execute() throws Exception {
		System.out.println("Enter into getpoint");
		//db connection
		Connection conn = null;
		Statement stmt = null;
		String sql = "select * from t_station";
		ResultSet rs = null;
		//set pattern for city county town code
		DecimalFormat df = (DecimalFormat) DecimalFormat.getInstance();
		df.applyPattern("000");
		//json array
		JSONArray netPointArray = new JSONArray();

		//get parameters in request
		String city = this.getRequest().getParameter(ParamConstants.CITY);
		String county = this.getRequest().getParameter(ParamConstants.COUNTY);
		String town = this.getRequest().getParameter(ParamConstants.TOWN);
		
		//add conditions in sql statement
		if (city != null) {
			sql += " where CITY_CODE='" + city + "'";
			if (county != null) {
				sql += " and COUNTY_CODE='" + county + "'";
				if (town != null)
					sql += " and TOWN_CODE='" + town + "'";
			}
		}
		//get data from db and generate return file
		try {
//			Class.forName("oracle.jdbc.driver.OracleDriver");
//			String url = "jdbc:oracle:thin:@localhost:1521:BIKE";
//			conn = DriverManager.getConnection(url, "HR", "HR");
			Class.forName("com.mysql.jdbc.Driver");
			String url = "jdbc:mysql://localhost:3306/bike?useUnicode=true&amp;characterEncoding=utf8";
			conn = DriverManager.getConnection(url,"bike","bike");
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);
			while (rs.next()) {
				//combine STATION_NID
				String temp = "";
				int nid = rs.getInt("CITY_CODE");
				temp += df.format(nid);
				nid = rs.getInt("COUNTY_CODE");
				temp += df.format(nid);
				nid = rs.getInt("TOWN_CODE");
				temp += df.format(nid);
				nid = rs.getInt("STATION_CODE");
				temp += df.format(nid);
				//create a JSON Object
				JSONObject netPoint = new JSONObject();
				netPoint.put(ObjectConstants.STATION_NID, temp);
				netPoint.put(ObjectConstants.STATION_NAME, rs.getString("STATION_NAME"));
				netPoint.put(ObjectConstants.LATITUDE, rs.getString("LATITUDE"));
				netPoint.put(ObjectConstants.LONGITUDE, rs.getString("LONGITUDE"));
				//add this object to json array
				netPointArray.put(netPoint);
			}
			allNetPoint = netPointArray.toString();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return ActionResultConstants.FAILED;
		} catch (SQLException e) {
			e.printStackTrace();
			return ActionResultConstants.FAILED;
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
		return ActionResultConstants.SUCCESS;
		
	}

	public String getAllNetPoint() {
		return allNetPoint;
	}

	public void setAllNetPoint(String allNetPoint) {
		this.allNetPoint = allNetPoint;
	}
}
