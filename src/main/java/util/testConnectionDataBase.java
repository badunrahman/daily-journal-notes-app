package util;

import java.sql.Connection;
import java.sql.SQLException;

public class testConnectionDataBase
{
    public static void main(String[] args) {
        try{
            Connection conn = DBConnection.getInstance().getConnection();
            if(conn != null && !conn.isClosed()){
                System.out.println("DataBase is running");
            }else{
                System.out.println("Connection is closed");
            }
        }catch (SQLException e){
            System.out.println("Failed to connect to the database");
            e.printStackTrace();
        }
    }
}
