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
    
    //  เมธอดภายในสำหรับดึงข้อมูลทั้งหมดมาจากฐานข้อมูล โดยเรียงลำดับตามประวัติการใส่เพิ่มเข้ามา
    private static ArrayList<Object> checkStock() {
        ArrayList<Object> myArrList = new ArrayList<Object>();
        try {
            Connection conn = ConnectionBuilder.getConnection();
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM ROOT.STOCK " +
                "JOIN ROOT.PRODUCT ON STOCK.STOCK_PRODUCT_ID=PRODUCT.PRODUCT_ID");
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
    
    //  เมธอดภายในสำหรับเก็บข้อมูลที่ได้จาก checkStock() ไว้ในรูปแบบของ Object[][] (array สองมิติของ Object)
    private static Object[][] dataModel() {
        Object[] data = checkStock().toArray();
        Object[][] model = new Object[checkStock().size()/4][4];
        for (int c = 0; c < checkStock().size(); c++)
            for (int a = 0; a < checkStock().size()/4; a++) {
                for (int b = 0; b < 4; b++) {
                    model[a][b] = data[c++];
            }
        }
        return model;
    }
    
    /**
     * เมธอดสำหรับใส่ค่าที่ได้จาก dataModel() ลงในรูปแบบแถวๆหนึ่งของ [JTable] ใดๆ
     * (นำมาใช้กับ mainCheckStockActionPerformed(ActionEvent evt))
     */
    public static void stockTableRowInsert(JTable table) {
        Object[][] model = dataModel();
        DefaultTableModel dtm = (DefaultTableModel) table.getModel();
        for (int a = dtm.getRowCount(); a < checkStock().size()/4; a++) {
            dtm.addRow(new Object[]{model[a][0], model[a][1], model[a][2], model[a][3]});
        }
    }
}