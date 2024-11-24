package paypocket;

import java.sql.*;

public class DBLoader {

  public static Connection getConnection() throws Exception {
        Class.forName("oracle.jdbc.OracleDriver");
        System.out.println("Driver Loading Done");
        return DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:orcl", "sys as sysdba", "Jatin$123");
    }

    public static ResultSet executeQuery(String sql) throws Exception {
        Connection conn = getConnection();
           System.out.println("connection done");
        Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
         System.out.println("Statement Done");
        return stmt.executeQuery(sql);
        
    }

    public static int executeUpdate(String sql, Object... params) throws Exception {
        Connection conn = getConnection();
         System.out.println("connection done");
        PreparedStatement pstmt = conn.prepareStatement(sql);
          System.out.println("Statement Done");
        for (int i = 0; i < params.length; i++) {
            pstmt.setObject(i + 1, params[i]);
        }
        return pstmt.executeUpdate();
    }

}
