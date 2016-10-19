package project.warehouse.function;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import project.warehouse.database.ConnectionBuilder;

//  @author B
public class CheckStock {
    
    /**
     * เมธอดภายในสำหรับดึงข้อมูลทั้งหมดมาจากฐานข้อมูล โดยจะเช็คว่าจะต้องการให้ดึงเฉพาะข้อมูลที่ตรงกับผู้ใช้นั้นๆหรือไม่
     * แล้วจากนั้นจึงค่อยเรียงลำดับข้อมูลที่ได้ตามประวัติการใส่เพิ่มเข้ามา
     */
    private static ArrayList<Object> checkStock(boolean bl) {
        ArrayList<Object> myArrList = new ArrayList<Object>();
        try {
            Connection conn = ConnectionBuilder.getConnection();
            PreparedStatement ps;
            String stmt = "SELECT * FROM ROOT.STOCK " +
                "JOIN ROOT.PRODUCT ON STOCK.STOCK_PRODUCT_ID=PRODUCT.PRODUCT_ID";
            if (bl) {
                stmt += " WHERE REF_USER_ID=?";
                ps = conn.prepareStatement(stmt);
                ps.setInt(1, User.getUserId());
            } else {
                ps = conn.prepareStatement(stmt);
            }
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                myArrList.add(rs.getString("STOCK_PRODUCT_ID"));
                myArrList.add(rs.getString("PRODUCT_NAME"));
                myArrList.add(rs.getInt("STOCK_PRODUCT_AMOUNT"));
                myArrList.add(rs.getInt("REF_USER_ID"));
            }
        } catch (SQLException ex) {
            System.err.println("Oops!, There's an internal error.");
            ex.printStackTrace();  
        }
        return myArrList;
    }
    
    //  เมธอดสำหรับเก็บข้อมูลที่ได้จาก checkStock(boolean bl) ไว้ในรูปแบบของ Object[][] (array สองมิติของ Object)
    public static Object[][] dataModel(boolean bl) {
        Object[] data = checkStock(bl).toArray();
        Object[][] model = new Object[checkStock(bl).size()/4][4];
        for (int c = 0; c < checkStock(bl).size(); c++)
            for (int a = 0; a < checkStock(bl).size()/4; a++) {
                for (int b = 0; b < 4; b++) {
                    model[a][b] = data[c++];
            }
        }
        return model;
    }
    
    /**
     * เมธอดสำหรับใส่ค่าที่ได้จาก dataModel(boolean bl) ลงในรูปแบบแถวๆหนึ่งของ [JTable] ใดๆ
     * (นำมาใช้กับ mainCheckStockActionPerformed(ActionEvent evt))
     */
    public static void stockTableRowInsert(JTable table, boolean bl) {
        Object[][] model = dataModel(bl);
        DefaultTableModel dtm = (DefaultTableModel) table.getModel();
        table.removeAll();
        while (dtm.getRowCount() > 0) {
            for (int i = 0; i < dtm.getRowCount(); i++) {
                dtm.removeRow(i);
            }
        }
        for (int a = 0; a < checkStock(bl).size()/4; a++) {
            dtm.addRow(new Object[]{model[a][0], model[a][1], model[a][2], model[a][3]});
        }
    }
}