package util;

import java.sql.Connection;
import java.sql.DriverManager;



public class DBConnection {
    // Veritabanı bilgilerini buraya yazıyoruz
    private static final String URL = "jdbc:mysql://localhost:3306/akilli_sehir_db";
    private static final String USER = "root"; // MySQL kullanıcı adın (genelde root)
    private static final String PASSWORD = "@Lifesk26"; // MySQL şifreni buraya yazmalısın

public static Connection getConnection() {
    try {
        Class.forName("com.mysql.cj.jdbc.Driver"); 
        
        return DriverManager.getConnection(URL, USER, PASSWORD);
    } catch (Exception e) {
        e.printStackTrace();
        return null;
    }
}
    

}