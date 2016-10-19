package project.warehouse.database;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLNonTransientConnectionException;
import java.sql.Statement;
import java.util.Properties;

//  @author jirawat, edited code by B
public class ConnectionBuilder {
    
    //<editor-fold defaultstate="collapsed" desc="เมธอดเมนสำหรับทำการทดสอบภายใน">
    //  Uses for testing internal only
    public static void main(String[] args) {
        new ConnectionBuilder();
        /*  
        Connection conn = getConnection();
        try {
            int a = -1;
            PreparedStatement ps = conn.prepareStatement("SELECT USER_ID FROM USERSYS");
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                a = rs.getInt("USER_ID");
            }
            System.out.println("The first User ID: " + a);
        } catch (SQLException ex) {
            System.err.println("Oops!, There's an internal error.\n" + ex.getMessage());
        }
        */
    }
    //</editor-fold>
    
    //  ใช้เรียกเมธอด getConnection() ผ่าน constructor (new ConnectionBuilder())
    public ConnectionBuilder() {
        getConnection();
    }
    
    //  เมธอดสำหรับใช้ในการเชื่อมต่อกับฐานข้อมูล
    public static Connection getConnection() {
        Connection conn = null;
        try {
            //  เมธอดสำหรับใช้เริ่มต้นการเชื่อมต่อกับฐานข้อมูลด้วยวิธีที่ 1
            Class.forName("org.apache.derby.jdbc.ClientDriver").newInstance();            
            conn = DriverManager.getConnection("jdbc:derby://localhost:1527/warehouse;create=true","root","123456");
            
            //  ในกรณีที่ยังไม่สามารถเชื่อมต่อกับฐานข้อมูลได้ภายใน 10 วินาที
            if (conn.isClosed() && !conn.isValid(10)) {
                
                //  เมธอดสำหรับใช้เริ่มต้นการเชื่อมต่อกับฐานข้อมูลด้วยวิธีที่ 2
                Properties props = new Properties();
                props.load(new FileInputStream("../Warehouse/lib/settings.properties"));
                org.apache.derby.jdbc.ClientDataSource ds = new org.apache.derby.jdbc.ClientDataSource();
                ds.setServerName(props.getProperty("DERBY_SERVER_NAME"));
                ds.setPortNumber(Integer.parseInt(props.getProperty("DERBY_SERVER_PORT")));
                ds.setDatabaseName(props.getProperty("DERBY_DB_NAME"));
                ds.setUser(props.getProperty("DERBY_DB_USERNAME"));
                ds.setPassword(props.getProperty("DERBY_DB_PASSWORD"));
                conn = ds.getConnection();
            }
            
            //  เช็คก่อนว่าตาราง PRODUCT และ USERSYS ถูกสร้างขึ้นมาแล้วหรือไม่ ถ้าไม่มีให้สร้างขึ้นมาเองโดยอัตโนมัติ
            DatabaseMetaData meta = conn.getMetaData();
            ResultSet table1 = meta.getTables(conn.getCatalog(), null, "PRODUCT", null);
            ResultSet table2 = meta.getTables(conn.getCatalog(), null, "USERSYS", null);
            Statement stmt = conn.createStatement();
            try {
                /**
                 * ดึงคำสั่ง SQL ที่เก็บไว้ในไฟล์ข้อความมาใช้ผ่าน statement โดยใช้
                 * read(String loc, int str, int end)
                 */
                String cmd1 = read("../Warehouse/lib/createTable.txt", 0, 237);
                String cmd2 = read("../Warehouse/lib/createTable.txt", 239, 351);
                String cmd3 = read("../Warehouse/lib/createTable.txt", 353, 534);
                String cmd4 = read("../Warehouse/lib/createTable.txt", 536, 612);
                String cmd5 = read("../Warehouse/lib/createTable.txt", 614, 698);
                String cmd6 = read("../Warehouse/lib/createTable.txt", 700, 772);
                
                if (!table1.next() || !table2.next()) {
                    stmt.execute(cmd1);     //  สร้างตาราง USERSYS
                    stmt.execute(cmd2);     //  สร้างตาราง PRODUCT
                    stmt.execute(cmd3);     //  สร้างตาราง STOCK
                    stmt.execute(cmd4);     //  สร้าง FOREIGN KEY ที่ชื่อ REF_USER_ID
                    stmt.execute(cmd5);     //  สร้าง FOREIGN KEY ที่ชื่อ STOCK_PRODUCT_ID
                    stmt.execute(cmd6);     //  สร้าง USER_ID ที่ตำแหน่ง 100001
                } else if (!table1.next() && table2.next()) {
                    stmt.execute(cmd1);
                } else if (table1.next() && !table2.next()) {
                    stmt.execute(cmd2);     //  (bug ทำงานไม่ได้ ต้องใช้ Recreate Table... ด้วยมืออย่างเดียว)
                }
            } finally {
                stmt.close();       //  ปิดการใช้ Execute Command... ด้วย SQL ผ่าน statement
            }
        }
        //  แสดงข้อความของข้อผิดพลาดเนื่องจากยังไม่ได้เปิดการเชื่อมต่อกับเซิร์ฟเวอร์ฐานข้อมูล (Java DB database server)
        catch (SQLNonTransientConnectionException ex) {
            if (ex.getMessage().equalsIgnoreCase("java.net.ConnectException : Error connecting to server localhost" +
                " on port 1,527 with message Connection refused: connect.")) {
                    System.err.println("Oops!, It seems that the database server is either not started or connected.");
            }
            //  แสดงข้อความของข้อผิดพลาดอิ่นๆนอกเหนือไปจากข้อความข้างต้น
            else {
                System.err.println("Oops!, There's an internal error.");
                ex.printStackTrace();
            }
        }
        //  แสดงข้อความของข้อผิดพลาดเนื่องจากการทำงานของ SQL มีปัญหา
        catch (SQLException ex) {
            //  แสดงข้อความของข้อผิดพลาดรหัส 1007 (มีฐานข้อมูลอยู่แล้ว)
            if (ex.getErrorCode() == 1007) {
                System.err.println("Oops!, There's an already existed database.\n" + ex.getMessage());
            }
            //  แสดงข้อความของข้อผิดพลาดรหัส 30000 (มีตารางอยู่แล้ว)
            else if (ex.getErrorCode() == 30000) {
                System.err.println("We have found an already existed table.\n" + ex.getMessage() +
                    "\n- For a missing PRODUCT table, we are able to create it again.\n" +
                    "      To complete creating a table, please try again once more\n" +
                    "      and reconnect the database to check the result." +
                    "\n- For a missing USERSYS table, we are unable to create it again.\n" +
                    "      Please backup any data in a PRODUCT table, delete a table,\n" +
                    "      try again once more and reconnect the database to check the result." +
                    "\nThis process can be done manually by recreating a missing table.");
            }
            //  แสดงข้อความของข้อผิดพลาดรหัสอิ่นๆนอกเหนือไปจากรหัสข้างต้น
            else {
                System.err.println("Oops!, There's an internal error.");
                ex.printStackTrace();
            }
        }
        //  แสดงข้อความของข้อผิดพลาดเนื่องจากยังไม่ได้เปิดการเชื่อมต่อกับเซิร์ฟเวอร์ฐานข้อมูล (Java DB database server)
        catch (NullPointerException ex) {
            System.err.println("Oops!, It seems that the database server is either not started or connected.");
        }
        //  แสดงข้อความของข้อผิดพลาดเนื่องจากไม่พบไฟล์หรือคลาสของโปรแกรม
        catch (FileNotFoundException | ClassNotFoundException ex) {
            System.err.println("Oops!, It seems that the program file or class is not found.\n" + ex.getMessage());
        } catch (InstantiationException | IllegalAccessException | IOException ex) {
            System.err.println("Oops!, There's an internal error.");
            ex.printStackTrace();
        } catch (Exception ex) {
            System.err.println("Oops!, There's an internal error.");
            ex.printStackTrace();
        }
        return conn;
    }
    
    /**
     * เมธอดสำหรับอ่านไฟล์ข้อความ (ในรูปแบบไฟล์ที่รองรับ) ใดๆให้เป็นข้อความ
     * โดยกำหนดค่าเริ่มต้นและค่าสิ้นสุดของข้อความที่ต้องการจากไฟล์ข้อความนั้นๆ
     * (นำมาใช้กับ getConnection())
     */
    public static String read(String loc, int str, int end) {
        String text = "";
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(new File(loc))));
            text = in.readLine().substring(str, end);
        }
        //  แสดงข้อความของข้อผิดพลาดเนื่องจากค่าสิ้นสุดมากกว่าค่าเริ่มต้นหรือจำนวนตัวอักษรทั้งหมดของไฟล์ข้อความที่ใช้
        catch (StringIndexOutOfBoundsException ex) {
            System.err.println("Oops!, It seems that the end index you are looking for is not in your file.\n" + ex.getMessage());
        }
        //  แสดงข้อความของข้อผิดพลาดเนื่องจากไม่พบไฟล์ข้อความที่ใช้        
        catch (FileNotFoundException ex) {
            System.err.println("Oops!, We can not locate your file.\n" + ex.getMessage());
        } catch (Exception ex) {
            System.err.println("Oops!, There's an internal error.");
            ex.printStackTrace();            
	}
        return text;
    }
}