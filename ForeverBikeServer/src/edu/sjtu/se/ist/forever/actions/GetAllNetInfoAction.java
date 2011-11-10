package edu.sjtu.se.ist.forever.actions;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;

import org.json.*;

public class GetAllNetInfoAction extends ForeverSupport {

	/**
	 * 
	 */
	private String allNetInfo;
	private static final long serialVersionUID = 1L;

	public String execute() throws Exception {
		System.out.println("Enter into getinfo");
		// db connection
		Connection conn = null;
		Statement stmt = null;
		String sql = "select * from T_STATION_STATUS";
		ResultSet rs = null;
		// set pattern for station nid
		DecimalFormat df = (DecimalFormat) DecimalFormat.getInstance();
		df.applyPattern("000000000000");
		// json array for return
		JSONArray netInfoArray = new JSONArray();
		// get a json array from request which contains the nid of required
		// stations and set conditions in sql clause
		String stations = this.getRequest()
				.getParameter(ParamConstants.STATIONS);
		if (stations != null) {
			JSONArray paramArray = new JSONArray(this.getRequest()
					.getParameter(ParamConstants.STATIONS));
			if (paramArray.length() > 0) {
				sql += " where STATION_NID='" + df.format(paramArray.getLong(0))
						+ "'";
				for (int iter = 1; iter < paramArray.length(); ++iter) {
					sql += " or STATION_NID='"
							+ df.format(paramArray.getLong(iter)) + "'";
				}
			}
		}
		// get data from db and generate return file
		try {
//			Class.forName("oracle.jdbc.driver.OracleDriver");
//			String url = "jdbc:oracle:thin:@localhost:1521:BIKE";
//			conn = DriverManager.getConnection(url, "HR", "HR");
			Class.forName("com.mysql.jdbc.Driver");
			String url = "jdbc:mysql://localhost:3306/BIKE?useUnicode=true&amp;characterEncoding=utf8";
			conn = DriverManager.getConnection(url,"rest","rest");
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);
			while (rs.next()) {
				JSONObject netInfo = new JSONObject();
				netInfo.put(ObjectConstants.STATION_NID,
						rs.getString("STATION_NID"));
				netInfo.put(ObjectConstants.LOCK_COUNT,
						rs.getString("LOCK_COUNT"));
				netInfo.put(ObjectConstants.BIKE_COUNT,
						rs.getString("BIKE_COUNT"));
				netInfoArray.put(netInfo);
			}
			allNetInfo = netInfoArray.toString();
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
		System.out.println(allNetInfo);
		return SUCCESS;
	}

	public void setAllNetInfo(String allNetInfo) {
		this.allNetInfo = allNetInfo;
	}

	public String getAllNetInfo() {
		return allNetInfo;
	}
}
