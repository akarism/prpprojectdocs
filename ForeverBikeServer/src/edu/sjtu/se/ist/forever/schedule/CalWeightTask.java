package edu.sjtu.se.ist.forever.schedule;

import java.sql.*;

public class CalWeightTask {

	private int count = 1;
	public double[] execute(String userID,String time) throws SQLException
	{
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			}
		Connection conn = java.sql.DriverManager.getConnection(
			    "jdbc:mysql://59.78.58.190:3306/bike", "remote", "root");
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery("select * from t_record");
		rs.last();
		Object[] array = new Object[rs.getRow()];
		rs.beforeFirst();
		
		int t = 0;
		double[] result = new double[4];
		while(rs.next()) {
		    String[] res = new String[10];
		    res[0] = rs.getString("id_time");
		    String id = rs.getString("id_user");
		    PreparedStatement pstmt = conn.prepareStatement("select * from t_user where id_user = ?");
		    pstmt.setString(1, id);
		    ResultSet rs2 = pstmt.executeQuery();
		    rs2.next();
		    res[1] = rs2.getString("id_age");
		    res[2] = rs2.getString("id_work");
		    res[3] = rs2.getString("id_study");
		    res[4] = rs2.getString("id_marry");
		    res[5] = rs2.getString("id_sex");
		    res[6] = rs.getString("class_1");
		    res[7] = rs.getString("class_2");
		    res[8] = rs.getString("class_3");
		    res[9] = rs.getString("class_4");
		    array[t] = res;
		    t++;
		}
		
		String[] calcData = new String[6];
		calcData[0] = time;
		PreparedStatement pstmt = conn.prepareStatement("select * from t_user where id_user = ?");
	    pstmt.setString(1, userID);
	    rs = pstmt.executeQuery();
	    if (rs.next())
	    {
	    	calcData[1] = rs.getString("id_age");
	    	calcData[2] = rs.getString("id_work");
	    	calcData[3] = rs.getString("id_study");
	    	calcData[4] = rs.getString("id_marry");
	    	calcData[5] = rs.getString("id_sex");
	    }
		Object[] trainData = new Object[array.length];
		for (int i = 0;i<4; i++)
		{
			DTree tree = new DTree();
			for (int j = 0; j<array.length; j++)
			{
				String[] trainRow = new String[7];
				String[] temp = (String[]) array[j];
				for (int k = 0; k<6; k++)
				{
					trainRow[k] = temp[k];
				}
				trainRow[6] = temp[6+i];
				trainData[j] = trainRow;
			}
			tree.create(trainData, 6);
			result[i] = tree.compare(calcData, tree.root);
		}
		return result;
		
		//System.out.println("===============END PRINT TREE===============");
		//String[] printData = new String[] { "2", "3", "2", "2", "2", "1"};
		//System.out.println("===============DECISION RESULT===============");
		//System.out.println("Execute " + count++ +" times.");
	}
	
	public void doIt(){
		System.out.println("We did it.");
	}
}
