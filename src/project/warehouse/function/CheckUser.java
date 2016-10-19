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
public class CheckUser {
    
    //  เมธอดภายในสำหรับดึงข้อมูลทั้งหมดมาจากฐานข้อมูล โดยเรียงลำดับตามประวัติการใส่เพิ่มเข้ามา
    private static ArrayList<Object> checkUser() {
        ArrayList<Object> myArrList = new ArrayList<Object>();
        try {
            Connection conn = ConnectionBuilder.getConnection();
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM ROOT.USERSYS");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                myArrList.add(rs.getInt("USER_ID"));
                myArrList.add(rs.getString("USER_NAME"));
            }
        } catch (SQLException ex) {
            System.err.println("Oops!, There's an internal error.");
            ex.printStackTrace();  
        }
        return myArrList;
    }
    
    //  เมธอดสำหรับเก็บข้อมูลที่ได้จาก checkStock() ไว้ในรูปแบบของ Object[][] (array สองมิติของ Object)
    public static Object[][] dataModel() {
        Object[] data = checkUser().toArray();
        Object[][] model = new Object[checkUser().size()/2][2];
        for (int c = 0; c < checkUser().size(); c++)
            for (int a = 0; a < checkUser().size()/2; a++) {
                for (int b = 0; b < 2; b++) {
                    model[a][b] = data[c++];
            }
        }
        return model;
    }
    
    /**
     * เมธอดสำหรับใส่ค่าที่ได้จาก dataModel() ลงในรูปแบบแถวๆหนึ่งของ [JTable] ใดๆ
     * (นำมาใช้กับ mainCheckStockActionPerformed(ActionEvent evt))
     */
    public static void userTableRowInsert(JTable table) {
        Object[][] model = dataModel();
        DefaultTableModel dtm = (DefaultTableModel) table.getModel();
        while (dtm.getRowCount() > 0) {
            for (int i = 0; i < dtm.getRowCount(); i++) {
                dtm.removeRow(i);
            }
        }
        for (int a = 0; a < checkUser().size()/2; a++) {
            dtm.addRow(new Object[]{model[a][0], model[a][1]});
        }
    }
}