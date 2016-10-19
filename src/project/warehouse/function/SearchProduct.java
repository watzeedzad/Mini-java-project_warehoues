package project.warehouse.function;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.TreeSet;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import project.warehouse.database.ConnectionBuilder;
import project.warehouse.driver.UIMethods;

//  @author B
public class SearchProduct {
    
    /**
     * เมธอดภายในสำหรับเช็คข้อความใดๆว่าตรงกับข้อมูลที่มีอยู่ในฐานข้อมูลหรือไม่
     * โดยจะใช้ containsOnlyNumbers(String str) ใน project.warehouse.driver.UIMethods
     * และ Product.checkProduct...(String str) ในการตรวจสอบ ก่อนที่จะทำรายการใดๆก็ตามในแต่ละรูปแบบ
     * - ในส่วนของ ข้อความใดๆที่ตรงกับข้อมูลที่มีอยู่ในฐานข้อมูล
     *   ก็จะดึงข้อมูลมาจากฐานข้อมูลตรงตามข้อความนั้น
     * - ในส่วนของ ข้อความใดๆที่ไม่ตรงกับข้อมูลที่มีอยู่ในฐานข้อมูล
     *   ก็จะดึงตัวเลขทุกตัวมาจากฐานข้อมูลที่มีข้อความนั้นปรากฏอยู่
     *   จากนั้นจึงค่อยเรียงลำดับตัวเลขที่ดึงมาได้จากน้อยไปหามากโดยใช้ Collections.sort(id, new StringComparator());
     *   แล้วใช้ searchRelate(ArrayList<String> in, ArrayList<String> out) ในการดึงข้อมูลมาจากฐานข้อมูลตรงตามตัวเลขที่ดึงมาได้
     * (นำมาใช้กับ
     * - dataModel(String str)
     * - searchTableRowInsert(String str, JTable table)
     * - searchProduct(JTextField field, JTable table)
     * )
     */
    private static ArrayList<String> searchProduct(String str, boolean bl) {
        ArrayList<String> id, myArrList = new ArrayList<String>();
        TreeSet<String> temp = new TreeSet<String>();
        try {
            Connection conn = ConnectionBuilder.getConnection();
            PreparedStatement ps;
            String stmt, stmt0 = "SELECT * FROM ROOT.STOCK " +
                "JOIN ROOT.PRODUCT ON STOCK.STOCK_PRODUCT_ID=PRODUCT.PRODUCT_ID ";
            UIMethods ctrl = new UIMethods();
            if (ctrl.containsOnlyNumbers(str) && Product.checkProductId(str)) {
                stmt = stmt0 + "WHERE PRODUCT_ID=?";
                if (bl) {
                    stmt += " AND REF_USER_ID=?";
                    ps = conn.prepareStatement(stmt);
                    ps.setString(1, str);
                    ps.setInt(2, User.getUserId());
                } else {
                    ps = conn.prepareStatement(stmt);
                    ps.setString(1, str);
                }
            } else if (Product.checkProductName(str)) {
                stmt = stmt0 + "WHERE PRODUCT_NAME=?";
                if (bl) {
                    stmt += " AND REF_USER_ID=?";
                    ps = conn.prepareStatement(stmt);
                    ps.setString(1, str);
                    ps.setInt(2, User.getUserId());
                } else {
                    ps = conn.prepareStatement(stmt);
                    ps.setString(1, str);
                }
            } else {
                if (ctrl.containsOnlyNumbers(str)) {
                    if (bl) {
                        stmt = stmt0 + "WHERE REF_USER_ID=? AND (PRODUCT_NAME LIKE ? OR PRODUCT_ID LIKE ?)";
                        ps = conn.prepareStatement(stmt);
                        ps.setInt(1, User.getUserId());
                        ps.setString(2, "%" + str + "%");
                        ps.setString(3, "%" + str + "%");
                    } else {
                        stmt = stmt0 + "WHERE PRODUCT_NAME LIKE ? OR PRODUCT_ID LIKE ?";
                        ps = conn.prepareStatement(stmt);
                        ps.setString(1, "%" + str + "%");
                        ps.setString(2, "%" + str + "%");
                    }
                } else {
                    stmt = stmt0 + "WHERE PRODUCT_NAME LIKE ?";
                    if (bl) {
                        stmt += " AND REF_USER_ID=?";
                        ps = conn.prepareStatement(stmt);
                        ps.setString(1, "%" + str + "%");
                        ps.setInt(2, User.getUserId());
                    } else {
                        ps = conn.prepareStatement(stmt);
                        ps.setString(1, "%" + str + "%");
                    }
                }
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    temp.add(rs.getString("PRODUCT_ID"));
                }
                id = new ArrayList<String>(temp);
                searchRelate(id, myArrList, bl);
                return myArrList;
            }
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                myArrList.add(rs.getString("STOCK_PRODUCT_ID"));
                myArrList.add(rs.getString("PRODUCT_NAME"));
                myArrList.add(String.valueOf(rs.getInt("STOCK_PRODUCT_AMOUNT")));
                myArrList.add(String.valueOf(rs.getInt("REF_USER_ID")));
            }
        } catch (SQLException ex) {
            System.err.println("Oops!, There's an internal error.");
            ex.printStackTrace();  
        }
        return myArrList;
    }
    
    //  เมธอดภายในสำหรับดึงข้อมูลมาจากฐานข้อมูลตรงตามตัวเลขทุกตัวที่เก็บเอาไว้
    private static ArrayList<String> searchRelate(ArrayList<String> in, ArrayList<String> out, boolean bl) {
        try {
            Connection conn = ConnectionBuilder.getConnection();
            String stmt, stmt0 = "SELECT * FROM ROOT.STOCK " +
                "JOIN ROOT.PRODUCT ON STOCK.STOCK_PRODUCT_ID=PRODUCT.PRODUCT_ID " +
                "WHERE STOCK_PRODUCT_ID=?";
            if (bl) {
                stmt = stmt0 + " AND REF_USER_ID=?";
            } else {
                stmt = stmt0;
            }
            PreparedStatement ps = conn.prepareStatement(stmt, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            for (int i = 0; i < in.size(); i++) {
                if (bl) {
                    ps.setString(1, in.get(i));
                    ps.setInt(2, User.getUserId());
                } else {
                    ps.setString(1, in.get(i));
                }
                ResultSet rs = ps.executeQuery();
                rs.afterLast();
                while (rs.previous()) {
                    out.add(rs.getString("STOCK_PRODUCT_ID"));
                    out.add(rs.getString("PRODUCT_NAME"));
                    out.add(String.valueOf(rs.getInt("STOCK_PRODUCT_AMOUNT")));
                    out.add(String.valueOf(rs.getInt("REF_USER_ID")));
                }
            }
        } catch (SQLException ex) {
            System.err.println("Oops!, There's an internal error.");
            ex.printStackTrace();
        }
        return out;
    }
    
    /**
     * เมธอดภายในสำหรับเช็คข้อความใดๆว่าตรงกับตัวเลขที่มีอยู่ในฐานข้อมูลหรือไม่
     * (นำมาใช้กับ searchListInsert(String text))
     */
    private static ArrayList<String> searchProductId(String id) {
        ArrayList<String> myArrList = new ArrayList<String>();
        try {
            Connection conn = ConnectionBuilder.getConnection();
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM ROOT.PRODUCT " +
                "WHERE PRODUCT_ID LIKE ?");
            ps.setString(1, id + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                myArrList.add(rs.getString("PRODUCT_ID"));
            }
        } catch (SQLException ex) {
            System.err.println("Oops!, There's an internal error.");
            ex.printStackTrace();  
        }
        Collections.sort(myArrList);
        return myArrList;
    }
    
    /**
     * เมธอดภายในสำหรับเช็คข้อความใดๆว่าตรงกับชื่อที่มีอยู่ในฐานข้อมูลหรือไม่
     * (นำมาใช้กับ searchListInsert(String text))
     */
    private static ArrayList<String> searchProductName(String name) {
        ArrayList<String> myArrList = new ArrayList<String>();
        try {
            Connection conn = ConnectionBuilder.getConnection();
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM ROOT.PRODUCT " +
                "WHERE PRODUCT_NAME LIKE ?");
            ps.setString(1, name + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                myArrList.add(rs.getString("PRODUCT_NAME"));
            }
        } catch (SQLException ex) {
            System.err.println("Oops!, There's an internal error.");
            ex.printStackTrace();  
        }
        Collections.sort(myArrList);
        return myArrList;
    }
    
    /**
     * เมธอดภายในสำหรับเก็บข้อมูลที่ได้จาก searchProduct(String str) ไว้ในรูปแบบของ Object[][] (array สองมิติของ Object)
     * (นำมาใช้กับ searchTableRowInsert(String str, JTable table))
     */
    private static Object[][] dataModel(String str, boolean bl) {
        Object[] data = searchProduct(str, bl).toArray();
        Object[][] model = new Object[searchProduct(str, bl).size()/4][4];
        for (int c = 0; c < searchProduct(str, bl).size(); c++)
            for (int a = 0; a < searchProduct(str, bl).size()/4; a++) {
                for (int b = 0; b < 4; b++) {
                    model[a][b] = data[c++];
            }
        }
        return model;
    }
    
    /**
     * เมธอดภายในสำหรับใส่ค่าที่ได้จาก dataModel(String str) ลงในรูปแบบแถวๆหนึ่งของ [JTable] ใดๆ
     * (นำมาใช้กับ searchProduct(JTextField field, JTable table))
     */
    private static void searchTableRowInsert(String str, JTable table, boolean bl) {
        Object[][] model = dataModel(str, bl);
        DefaultTableModel dtm = (DefaultTableModel) table.getModel();
        while (dtm.getRowCount() > 0) {
            for (int i = 0; i < dtm.getRowCount(); i++) {
                dtm.removeRow(i);
            }
        }
        for (int a = 0; a < searchProduct(str, bl).size()/4; a++) {
            dtm.addRow(new Object[]{model[a][0], model[a][1], model[a][2], model[a][3]});
        }
    }

    /**
     * เมธอดสำหรับเก็บข้อมูลที่ได้จาก searchProduct...(String str) ไว้ในรูปแบบของ String[] (array ของ String)
     * โดยจะเช็คข้อความว่าจะผ่านเงื่อนไขของ containsOnlyNumbers(String str) ใน project.warehouse.driver.UIMethods
     * ได้หรือไม่ ก่อนที่จะทำรายการใดๆก็ตามในแต่ละรูปแบบ
     * (นำมาใช้กับ searchCaretUpdate(CaretEvent evt))
     */
    public static String[] searchListInsert(String text) {
        ArrayList<String> list = new ArrayList<String>();
        String[] data = new String[4];
        UIMethods ctrl = new UIMethods();
        
        if (text.length() == 1) {
            if (ctrl.containsOnlyNumbers(text)) {
                data = Arrays.copyOf(searchProductId(text).toArray(), 4, String[].class);
            } else {
                data = Arrays.copyOf(searchProductName(text).toArray(), 4, String[].class);
            }
        } else {
            for (int i = 0; i < 4; i++) {
                String[] name = Arrays.copyOf(searchProductName(text).toArray(), 2, String[].class);
                String[] id = Arrays.copyOf(searchProductId(text).toArray(), 2, String[].class);
                if (i < 2) {
                    data[i] = name[i];
                } else {
                    data[i] = id[i-2];
                }
            }
        }
        
        //  เอาข้อมูลที่เป็นข้อความว่าง ("") หรือ null ออก
        for (String str : data) {
            if (str != null && str.length() > 0) {
                list.add(str);
            }
        }
        data = list.toArray(new String[list.size()]);
        return data;
    }
    
    /**
     * เมธอดสำหรับเช็คข้อความใน [JTextField] ใดๆว่าเมื่อใช้ searchProduct(String str) แล้วจะมีข้อมูลหรือไม่
     * ถ้ามีก็ใช้ searchTableRowInsert(String str, JTable table) ใส่ค่าที่มีลงใน [JTable] ใดๆ
     * (นำมาใช้กับ searchActionPerformed(ActionEvent evt))
     */
    public static boolean searchProduct(JTextField field, JTable table, boolean bl) {
        String text = field.getText();
        ArrayList<String> data = searchProduct(text, bl);
        if (!data.isEmpty()) {
            searchTableRowInsert(text, table, bl);
            return true;
        }
        return false;
    }
}