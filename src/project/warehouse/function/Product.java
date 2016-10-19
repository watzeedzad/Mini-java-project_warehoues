package project.warehouse.function;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import project.warehouse.database.ConnectionBuilder;

//  @author B, edited code by jirawat
public class Product {

    private int prodAmount;
    private String prodId;
    private String prodName;

    public int getProdAmount() {
        return prodAmount;
    }

    public void setProdAmount(int prodAmount) {
        this.prodAmount = prodAmount;
    }

    public String getProdId() {
        return prodId;
    }

    public void setProdId(String prodId) {
        this.prodId = prodId;
    }

    public String getProdName() {
        return prodName;
    }

    public void setProdName(String prodName) {
        this.prodName = prodName;
    }

    public static void addProduct(String prodId, String prodName, int prodAmount) throws SQLException {
        int userId = User.getUserId();
        Connection conn = ConnectionBuilder.getConnection();

        PreparedStatement pss = conn.prepareStatement("SELECT COUNT(*) AS rowcount FROM ROOT.PRODUCT WHERE PRODUCT_ID=? AND PRODUCT_NAME=?");
        pss.setString(1, prodId);
        pss.setString(2, prodName);
        ResultSet rs = pss.executeQuery();
        while (rs.next()) {
            if (rs.getInt("rowcount") >= 1) {
                PreparedStatement ps1 = conn.prepareStatement("INSERT INTO ROOT.STOCK (REF_USER_ID, STOCK_PRODUCT_ID, STOCK_PRODUCT_AMOUNT) VALUES (?, ?, ?)");
                ps1.setInt(1, userId);
                ps1.setString(2, prodId);
                ps1.setInt(3, prodAmount);
                ps1.executeUpdate();
                return;
            }
        }
        
        PreparedStatement ps = conn.prepareStatement("INSERT INTO ROOT.PRODUCT (PRODUCT_ID, PRODUCT_NAME) VALUES(?, ?)");
        ps.setString(1, prodId);
        ps.setString(2, prodName);
        ps.executeUpdate();
        PreparedStatement ps1 = conn.prepareStatement("INSERT INTO ROOT.STOCK (REF_USER_ID, STOCK_PRODUCT_ID, STOCK_PRODUCT_AMOUNT) VALUES (?, ?, ?)");
        ps1.setInt(1, userId);
        ps1.setString(2, prodId);
        ps1.setInt(3, prodAmount);
        ps1.executeUpdate();
    }

    public static void subProduct(String prodId, String prodName, int prodAmount) throws SQLException {
        int userId = User.getUserId();
        Connection conn = ConnectionBuilder.getConnection();
        PreparedStatement ps = conn.prepareStatement("UPDATE ROOT.STOCK SET STOCK_PRODUCT_AMOUNT=? WHERE REF_USER_ID=? AND STOCK_PRODUCT_ID=?");
        ps.setInt(1, subProductAmount(prodId) - prodAmount);
        ps.setInt(2, userId);
        ps.setString(3, prodId);
        ps.executeUpdate();
    }

    public static void updateProduct(String prodId, int prodAmount, boolean append) throws SQLException {
        int userId = User.getUserId();
        Connection conn = ConnectionBuilder.getConnection();
        PreparedStatement ps = conn.prepareStatement("UPDATE ROOT.STOCK SET STOCK_PRODUCT_AMOUNT=? WHERE REF_USER_ID=? AND STOCK_PRODUCT_ID=?");
        if (append) {
            PreparedStatement ps1 = conn.prepareStatement("SELECT STOCK_PRODUCT_AMOUNT FROM ROOT.STOCK WHERE STOCK_PRODUCT_ID=?");
            ps1.setString(1, prodId);
            ResultSet rs = ps1.executeQuery();
            int temp = 0;
            while (rs.next()) {
                temp = rs.getInt("STOCK_PRODUCT_AMOUNT");
            }
            ps.setInt(1, prodAmount + temp);
        } else {
            ps.setInt(1, prodAmount);
        }
        ps.setInt(2, userId);
        ps.setString(3, prodId);
        ps.executeUpdate();
    }

    public static String subProductId(String name) throws SQLException {
        String id = "";
        Connection conn = ConnectionBuilder.getConnection();
        PreparedStatement ps = conn.prepareStatement("SELECT * FROM ROOT.PRODUCT WHERE PRODUCT_NAME=?");
        ps.setString(1, name);
        ResultSet rs = ps.executeQuery();
        String temp = "0";
        while (rs.next()) {
            temp = rs.getString("PRODUCT_ID");
        }
        PreparedStatement ps1 = conn.prepareStatement("SELECT * FROM ROOT.STOCK WHERE STOCK_PRODUCT_ID=?");
        ps1.setString(1, temp);
        ResultSet rs1 = ps1.executeQuery();
        while (rs1.next()) {
            id = rs1.getString("STOCK_PRODUCT_ID");
        }
        return id;
    }

    public static String subProductName(String id) throws SQLException {
        String name = "";
        Connection conn = ConnectionBuilder.getConnection();
        PreparedStatement ps = conn.prepareStatement("SELECT * FROM ROOT.STOCK "
                + "JOIN ROOT.PRODUCT ON STOCK.STOCK_PRODUCT_ID=PRODUCT.PRODUCT_ID WHERE PRODUCT_ID=?");
        ps.setString(1, id);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            name = rs.getString("PRODUCT_NAME");
        }
        return name;
    }

    public static int subProductAmount(String id) throws SQLException {
        int amount = -1;
        Connection conn = ConnectionBuilder.getConnection();
        PreparedStatement ps = conn.prepareStatement("SELECT * FROM ROOT.STOCK WHERE STOCK_PRODUCT_ID=? AND REF_USER_ID=?");
        ps.setString(1, id);
        ps.setInt(2, User.getUserId());
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            amount = rs.getInt("STOCK_PRODUCT_AMOUNT");
        }
        return amount;
    }

    public static boolean checkProductId(String id) throws SQLException {
        Connection conn = ConnectionBuilder.getConnection();
        PreparedStatement ps = conn.prepareStatement("SELECT * FROM ROOT.PRODUCT WHERE PRODUCT_ID=?");
        ps.setString(1, id);
        ResultSet rs = ps.executeQuery();
        boolean temp;
        if (rs.next()) {
            if (id.equals(rs.getString("PRODUCT_ID"))) {
                temp = true;
            } else {
                temp = false;
            }
        } else {
            temp = false;
        }
        return temp;
    }

    public static boolean checkProductName(String name) throws SQLException {
        Connection conn = ConnectionBuilder.getConnection();
        PreparedStatement ps = conn.prepareStatement("SELECT * FROM ROOT.PRODUCT WHERE PRODUCT_NAME=?");
        ps.setString(1, name);
        ResultSet rs = ps.executeQuery();
        boolean temp;
        if (rs.next()) {
            if (name.equals(rs.getString("PRODUCT_NAME"))) {
                temp = true;
            } else {
                temp = false;
            }
        } else {
            temp = false;
        }
        return temp;
    }

    public static boolean checkProductIdAndName(String prodId, String prodName) throws SQLException {
        Connection conn = ConnectionBuilder.getConnection();
        PreparedStatement ps = conn.prepareStatement("SELECT * FROM ROOT.STOCK "
                + "JOIN ROOT.PRODUCT ON STOCK.STOCK_PRODUCT_ID=PRODUCT.PRODUCT_ID WHERE PRODUCT_ID=? AND PRODUCT_NAME=? AND REF_USER_ID=?");
        ps.setString(1, prodId);
        ps.setString(2, prodName);
        ps.setInt(3, User.getUserId());
        ResultSet rs = ps.executeQuery();
        PreparedStatement ps1 = conn.prepareStatement("SELECT * FROM ROOT.STOCK WHERE STOCK_PRODUCT_ID=?");
        ps1.setString(1, prodId);
        ResultSet rs1 = ps1.executeQuery();
        boolean temp;
        if (rs.next() && rs1.next()) {
            temp = true;
        } else {
            temp = false;
        }
        return temp;
    }
}
