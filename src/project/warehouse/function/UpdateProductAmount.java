package project.warehouse.function;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import project.warehouse.database.ConnectionBuilder;

//  @author jirawat
public class UpdateProductAmount extends Exception {
    public UpdateProductAmount(String prodId, int prodAmount) throws SQLException {
        int userId = User.getUserId();
        Connection conn = ConnectionBuilder.getConnection();
        PreparedStatement ps = conn.prepareStatement("SELECT STOCK_PRODUCT_AMOUNT FROM ROOT.STOCK WHERE REF_USER_ID=? AND STOCK_PRODUCT_ID=?");
        ps.setInt(1, userId);
        ps.setString(2, prodId);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            int temp = rs.getInt("STOCK_PRODUCT_AMOUNT");
            prodAmount += temp;
            PreparedStatement ps1 = conn.prepareStatement("UPDATE ROOT.STOCK SET STOCK_PRODUCT_AMOUNT=? WHERE REF_USER_ID=? AND STOCK_PRODUCT_ID=?");
            ps1.setInt(1, prodAmount);
            ps1.setInt(2, userId);
            ps1.setString(3, prodId);
            ps1.executeUpdate();
        }
    }
}