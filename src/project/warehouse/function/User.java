package project.warehouse.function;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import project.warehouse.database.ConnectionBuilder;

//  @author jirawat
public class User {
    
    private String password;
    private String username;
    private static int userId = 0;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
    
    public static int getUserId() {
        return userId;
    }

    public static void setUserId(int userId) {
        User.userId = userId;
    }

    public static boolean verifyLogin(String username, String password) throws SQLException {
        Connection conn = ConnectionBuilder.getConnection();
        PreparedStatement ps = conn.prepareStatement("SELECT USER_NAME, USER_PASSWORD FROM ROOT.USERSYS WHERE USER_NAME=? AND USER_PASSWORD=?");
        ps.setString(1, username);
        ps.setString(2, password);
        ResultSet rs = ps.executeQuery();
        PreparedStatement ps1 = conn.prepareStatement("SELECT * FROM ROOT.USERSYS WHERE USER_NAME=?");
        ps1.setString(1, username);
        ResultSet rs1 = ps1.executeQuery();
        while (rs1.next()) {
            userId = rs1.getInt("USER_ID");
        }
        return rs.next();
    }

    public static int register(String username, String password) throws SQLException, AlreadyUsedUsername {
        Connection conn = ConnectionBuilder.getConnection();
        PreparedStatement ps = conn.prepareStatement("INSERT INTO ROOT.USERSYS (USER_NAME, USER_PASSWORD) VALUES (?, ?)");
        ps.setString(1, username);
        ps.setString(2, password);
        PreparedStatement ps1 = conn.prepareStatement("SELECT COUNT(*) AS rowcount FROM ROOT.USERSYS WHERE USER_NAME=?");
        ps1.setString(1, username);
        ResultSet rs = ps1.executeQuery();
        while (rs.next()) {
            if (rs.getInt("rowcount") == 1) {
                throw new AlreadyUsedUsername();
            }
        }
        return ps.executeUpdate();
    }
}