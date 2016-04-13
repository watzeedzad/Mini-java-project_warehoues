package project.warehouse.driver;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.text.DateFormat;
import java.util.Date;
import java.util.Properties;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.Timer;
import project.warehouse.database.ConnectionBuilder;
import project.warehouse.function.Product;

//  @author B
public class UIMethods {
    
    //<editor-fold defaultstate="collapsed" desc="ตัวแปรที่เป็นไอคอนรูปภาพ">
    /**
     * ตัวแปรที่เป็นไอคอนรูปภาพสำหรับใช้กับหน้าต่างไดอะล็อกผ่านเมธอด showMessageDialog(Component parentComponent,
     *      Object message, String title, int messageType, Icon icon)
     * ใน javax.swing.JOptionPane
     * For details see https://docs.oracle.com/javase/7/docs/api/javax/swing/JOptionPane.html#showMessageDialog(java.awt.Component,%20java.lang.Object,%20java.lang.String,%20int,%20javax.swing.Icon)
     */
    //</editor-fold>
    public final ImageIcon addIcon = new ImageIcon(".\\img\\ic_add_shopping_cart_black_24dp.png");
    public final ImageIcon cptIcon = new ImageIcon(".\\img\\ic_assignment_turned_in_black_24dp.png");
    public final ImageIcon delIcon = new ImageIcon(".\\img\\ic_delete_sweep_black_24dp.png");
    public final ImageIcon errIcon = new ImageIcon(".\\img\\ic_error_black_24dp.png");
    public final ImageIcon infoIcon = new ImageIcon(".\\img\\ic_info_black_24dp.png");
    public final ImageIcon markIcon = new ImageIcon(".\\img\\ic_help_black_24dp.png");
    public final ImageIcon srchIcon = new ImageIcon(".\\img\\ic_search_black_24dp.png");
    public final ImageIcon strIcon= new ImageIcon(".\\img\\ic_store_black_24dp.png");
    public final ImageIcon subIcon = new ImageIcon(".\\img\\ic_delete_forever_black_24dp.png");
    public final ImageIcon vusrIcon = new ImageIcon(".\\img\\ic_verified_user_black_24dp.png");
    public final ImageIcon warnIcon = new ImageIcon(".\\img\\ic_warning_black_24dp.png");
    
    //  ตัวแปรที่เป็นข้อความช่วยเหลือ (Tooltip)
    public final String toolTip = "<html>Any letters, including special characters<br>" +
        "(e.g. @, #, $, %, &, !, ?, *, /, -, +, .),<br>are not allowed to be typed in this field.</html>";
    
    /**
     * เมธอดสำหรับเช็คข้อความใน [JTextField] ใดๆว่าไม่เป็นข้อความว่าง ("") และข้อความกับฟอนต์ที่เรากำหนดเอาไว้หรือไม่
     * นอกจากนี้ยังสามารถเลือกโหมดการทำงานแบบต่างๆได้อีกว่าจะเช็คให้มีเพียงแค่ตัวเลขหรือไม่
     * (นำมาใช้กับ
     * - checkSubmit(JTextField field1, JTextField field2, JTextField field3, String message1, String message2, int i),
     * - checkActionPerformed(JTextField field, String message, int i, JTextField nextField),
     * - checkFocusLost(JTextField field, String message), 
     * - checkInsert(JTextField field1, JTextField field2, JTextField field3)
     * - username/passwordFocusGained(FocusEvent evt)
     * - registerActionPerformed(ActionEvent evt)
     * - submitAddProductActionPerformed(ActionEvent evt)
     * - submitSubProductActionPerformed(ActionEvent evt)
     * - searchActionPerformed(ActionEvent evt)
     * - searchCaretUpdate(CaretEvent evt)
     * - searchFocusGained(FocusEvent evt)
     * - searchKeyPressed(KeyEvent evt)
     * )
     */
    public boolean checkCorrect(JTextField field, String message, int i) {
        String fieldText = field.getText();
        if (!fieldText.isEmpty()) {
            if (!field.getFont().equals(new Font("Segoe UI Semilight", Font.ITALIC, 18)) ||
                !fieldText.equalsIgnoreCase(message)) {
                    //<editor-fold defaultstate="collapsed" desc="// วิธีที่ 1">
                    /*
                    if (i == 0) {
                        return true;
                    } else if (i == 1) {
                        if (containsOnlyNumbers(fieldText)) {
                            return true;
                        }
                    } else if (i == 2) {
                        if (!containsOnlyNumbers(fieldText)) {
                            return true;
                        }
                    }
                    */
                    //</editor-fold>
                    
                    // วิธีที่ 2
                    switch (i) {
                        case 0 :    return true;
                        case 1 :    if (containsOnlyNumbers(fieldText)) {
                                        return true;
                                    }
                                    break;
                        case 2 :    if (!containsOnlyNumbers(fieldText)) {
                                        return true;
                                    }
                                    break;
                    }
            }
        }
        return false;
    }

    /**
     * เมธอดสำหรับเช็คข้อความใน [JTextField] ใดๆว่าตรงกับข้อมูลและจำนวนที่มีอยู่ในฐานข้อมูลหรือไม่
     * โดยจะใช้ Product.subProduct...(String str) ในการตรวจสอบ
     * (นำมาใช้กับ submitSubProductActionPerformed(ActionEvent evt))
     */
    public boolean checkExist(JTextField field1, JTextField field2, JTextField field3) throws SQLException {
        String id, msg, text;
        if (field1 != null) {
            id = field1.getText();
            text = Product.subProductName(id);  //  ดึงชื่อมาจากฐานข้อมูลโดยใช้ Product.subProductName(String id)
            msg = "Sorry, this product information does not exist in the database.\n\n" +
                  "<html><b>Product ID</b><html> : " + id;
        } else {
            String name = field2.getText();
            text = Product.subProductId(name);  //  ดึงตัวเลขมาจากฐานข้อมูลโดยใช้ Product.subProductId(String name)
            id = text;
            msg = "Sorry, this product information does not exist in the database.\n\n" +
                  "<html><b>Product name</b><html> : " + name;
        }
        
        /**
         * ดึงจำนวนมาจากฐานข้อมูลโดยใช้ Product.subProductAmount(String id)
         * โดยที่ต้องเรียกใช้เมธอดนี้โดยตรง ไม่สามารถเก็บไว้เป็นตัวแปรได้เพราะจะติด
         * java.sql.SQLDataException: Invalid character string format for type BIGINT.
         */
        if (text.isEmpty() || Product.subProductAmount(id) == -1) {
            JOptionPane.showMessageDialog(null, msg, " Error !!!", JOptionPane.ERROR_MESSAGE, errIcon);
            if (field1 != null) {
                field1.requestFocus();
            } else {
                field2.requestFocus();
            }
        } else if (Integer.parseInt(field3.getText()) > Product.subProductAmount(id)) {
            msg = "Sorry, the value exceeds the product amount in the database.\n\n" +
                  "<html><b>Product amount</b><html> : " + field3.getText() +
                  "\n<html><b>Product amount available</b><html> : " + Product.subProductAmount(id);
            JOptionPane.showMessageDialog(null, msg, " Error !!!", JOptionPane.ERROR_MESSAGE, errIcon);
            field3.requestFocus();
        } else {
            return true;
        }
        return false;
    }
    
    /**
     * เมธอดสำหรับเช็คข้อความใน [JTextField] ใดๆว่ามีตัวเลขมากกว่า Integer.MAX_VALUE หรือเป็นศูนย์หรือไม่
     * โดยจะใช้ containsOnlyNumbers(String str) เช็คว่ามีเพียงแค่ตัวเลขหรือไม่ก่อน ก่อนที่จะทำในเงื่อนไขถัดไป
     * ถ้ามากกว่าหรือเป็นศูนย์ก็ใช้ reenterText(JTextField field, String message)
     * แต่ถ้าไม่มากกว่าหรือไม่เป็นศูนย์ก็ไม่ต้องทำอะไรในเงื่อนไขข้างต้น
     * (นำมาใช้กับ
     * - prodAmount...FocusLost(FocusEvent evt)
     * - submit...ProductActionPerformed(ActionEvent evt)
     * )
     */
    public boolean checkInt(JTextField field) {
        String fieldText = field.getText();
        if (containsOnlyNumbers(fieldText)) {
            if (Double.parseDouble(fieldText) > Integer.MAX_VALUE) {
                reenterText(field, "Oops! Exceed value.");
            } else if (Double.parseDouble(fieldText) == 0) {
                reenterText(field, "Oops! Value cannot be zero.");
            } else {
                return true;
            }
        }
        return false;
    }
    
    /**
     * เมธอดสำหรับเช็คข้อความใน [JTextField] ใดๆว่าจะผ่านเงื่อนไขของ Product.checkProduct...(String str) ในแต่ละรูปแบบได้หรือไม่
     * ถ้าผ่านหรือไม่ผ่านทั้งสองเงื่อนไขก็จะสามารถทำเมธอดใดๆก็ตามในรายการต่อไปได้
     * แต่ถ้าผ่านเงื่อนไขเพียงแค่อันใดอันหนึ่งก็จะขึ้นหน้าต่างเตือนก่อน ก่อนที่จะใช้ Product.subProduct...(String str) ใส่ค่าจากที่มีอยู่ในฐานข้อมูลลงใน [JTextField] ที่กำหนดไว้
     * (นำมาใช้กับ submitAddProductActionPerformed(ActionEvent evt))
     */
    public boolean checkProduct(JTextField field1, JTextField field2) {
        try {
            String msg, id = field1.getText(), name = field2.getText();
            String msg0 = "Sorry, This product already exists in our Warehouse database.\n\n";
            if ((Product.checkProductId(id) && Product.checkProductName(name)) ||
               (!Product.checkProductId(id) && !Product.checkProductName(name))) {
                    return true;
            } else if (Product.checkProductId(id)) {
                String name1 = Product.subProductName(id);
                msg = msg0 + "<html><b>Product ID</b><html> : " + id + "\n\nWe'll set your product name from\n\"" +
                      name + "\"    to    \"" + name1 + "\"";
                JOptionPane.showMessageDialog(null, msg, " Error !!!", JOptionPane.ERROR_MESSAGE, errIcon);
                setText(field2, "Enter a product name.");
                field2.setText(name1);
            } else if (Product.checkProductName(name)) {
                String id1 = Product.subProductId(name);
                msg = msg0 + "<html><b>Product name</b><html> : " + name + "\n\nWe'll set your product ID from\n\"" +
                      id + "\"    to    \"" + id1 + "\"";
                JOptionPane.showMessageDialog(null, msg, " Error !!!", JOptionPane.ERROR_MESSAGE, errIcon);
                setText(field1, "Type only number!");
                field1.setText(id1);
            }
        } catch (SQLException ex) {
            System.err.println("Oops!, There's an internal error.");
            ex.printStackTrace();
        }
        return false;
    }
    
    /**
     * เมธอดสำหรับเช็คข้อความใน [JTextField] ใดๆตามโหมดการทำงานที่เรากำหนดเอาไว้ ว่าจะผ่านเงื่อนไขของ
     * checkCorrect(JTextField field, String message, int i) ในแต่ละรูปแบบได้หรือไม่
     * (นำมาใช้กับ submit...ProductActionPerformed(ActionEvent evt))
     */
    public boolean checkSubmit(JTextField field1, JTextField field2, JTextField field3, String message1, String message2, String message3, int i) {
        switch (i) {
            case 0 :    if (!checkCorrect(field1, message1, 0) ||
                            !checkCorrect(field2, message2, 0) ||
                            !checkCorrect(field3, message1, 0)) {
                            return true;
                        }
                        break;
            case 1 :    if (!checkCorrect(field1, message1, 0)) {
                            if (checkCorrect(field2, message2, 0)) {
                                if (checkCorrect(field3, message1, 1)) {
                                    return true;
                                }
                            }
                        }
                        break;
            case 2 :    if (checkCorrect(field1, message1, 1)) {
                            if (!checkCorrect(field2, message2, 0)) {
                                if (checkCorrect(field3, message1, 1)) {
                                    return true;
                                }
                            }
                        }
                        break;
            case 3 :    if (checkCorrect(field1, message1, 1)) {
                            if (checkCorrect(field2, message2, 0)) {
                                if (!checkCorrect(field3, message1, 1)) {
                                    return true;
                                }
                            }
                        }
                        break;
            case 4 :    if (checkCorrect(field1, message1, 1)) {
                            if (checkCorrect(field2, message2, 0)) {
                                if (checkCorrect(field3, message1, 1)) {
                                    return true;
                                }
                            }
                        }
                        break;
            case 5 :    if (field1.isEnabled()) {
                            if (!checkCorrect(field1, message1, 0) ||
                                !checkCorrect(field3, message3, 0)) {
                                    return true;
                            }
                        } else {
                            if (!checkCorrect(field2, message2, 0) ||
                                !checkCorrect(field3, message3, 0)) {
                                    return true;
                            }
                        }
                        break;
            case 6 :    if (!checkCorrect(field1, message1, 0)) { 
                            if (checkCorrect(field3, message3, 1)) {
                                return true;
                            }
                        }
                        break;
            case 7 :   if (!checkCorrect(field2, message2, 0)) {
                            if (checkCorrect(field3, message3, 1)) {
                                return true;
                            }
                        }
                        break;
            case 8 :    if (checkCorrect(field1, message1, 1) ||
                            checkCorrect(field2, message2, 0)) {
                                if (!checkCorrect(field3, message3, 1)) {
                                    return true;
                                }
                        }
                        break;
            case 9 :    if (checkCorrect(field1, message1, 1)) {
                            if (checkCorrect(field3, message2, 1)) {
                                return true;
                            }
                        }
                        break;
            case 10 :   if (checkCorrect(field2, message1, 0)) {
                            if (checkCorrect(field3, message2, 1)) {
                                return true;
                            }
                        }
                        break;
        }
        return false;
    }
    
    //  เมธอดสำหรับเช็คข้อความใดๆว่ามีเพียงแค่ตัวเลขหรือไม่
    public boolean containsOnlyNumbers(String str) {
        for (int i = 0; i < str.length(); i++) {
            if (!Character.isDigit(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }
    
    //  เมธอดสำหรับออกจากระบบ
    public boolean logout() {
        String msg1 = "Are you sure you want to logout?\n\n" +
                      "<html><i>(Any left over data in this session will be cleared out.)</i></html>";
        String msg2 = "Your account has been successfully logged out.";
        int response = JOptionPane.showConfirmDialog(null, msg1, " Confirm ?", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, markIcon);
        if (response == JOptionPane.YES_OPTION) {
            logout(true);
            JOptionPane.showMessageDialog(null, msg2, " Success", JOptionPane.INFORMATION_MESSAGE, infoIcon);
            return true;
        }
        return false;
    }
    
    //  เมธอดสำหรับอ่านไฟล์รูปภาพใดๆให้เป็น BufferedImage
    public BufferedImage imgRead(String str){
        try {
            BufferedImage bfi = ImageIO.read(new File(str));
            return bfi;
        } catch (IOException ex) {
            System.err.println("Oops!, We can not locate your file.\n" + ex.getMessage());
        }
        return null;
    }
    
    //  เมธอดสำหรับแสดงเวลา (ตามนาฬิกาของระบบ) บน [JLabel] ใดๆ (โดยคลาดเคลื่อน +/- 1 วินาที) 
    public String clock(final JLabel label) {
        Timer timer = new Timer(0, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                label.setText(DateFormat.getDateTimeInstance().format(new Date()));
            }
        });
        timer.start();        
        return "";
    }
    
    //  เมธอดสำหรับปิดการเชื่อมต่อกับฐานข้อมูล
    private void logout(boolean bl) {
        if (bl) {
            try {
                if (!ConnectionBuilder.getConnection().isClosed()) {
                    ConnectionBuilder.getConnection().close();
                }
            } catch (Exception ex) {
                System.err.println("Oops!, There's an internal error.");
                ex.printStackTrace();            
            }
        }
    }
    
    /**
     * เมธอดสำหรับเช็คข้อความใน [JTextField] ใดๆตามโหมดการทำงานที่เรากำหนดเอาไว้ เมื่อมีการกด Enter ไปแล้ว
     * จะผ่านเงื่อนไขของ checkCorrect(JTextField field, String message, int i) ในแต่ละรูปแบบได้หรือไม่
     * - ในโหมด i == 0
     *   ถ้าไม่ผ่านก็ใช้ reenterText(JTextField field, String message)
     *   แต่ถ้าผ่านก็จะขอ Focus ไปให้กับ Component อันถัดไปหรืออันใดๆก็ได้
     * - ในโหมด i == 1
     *   จะขึ้นหน้าต่างกับข้อความแสดงข้อผิดพลาดขึ้นมา เนื่องจากโหมด i = 1 ใช้เป็นการภายในกับ
     *   checkSubmit(JTextField field1, JTextField field2, JTextField field3, String message1, String message2, int i)
     *   เพียงอันเดียวเท่านั้นในตอนนี้ จึงยังไม่มีการเขียนฟังก์ชันการทำงานใดๆ
     * - ในโหมด i == 2
     *   ถ้าผ่านก็จะทำงานแบบเดียวกันกับ checkFocusLost(JTextField field, String message)
     *   แต่ถ้าไม่ผ่านก็จะทำงานในส่วนที่เหลือแบบเดียวกันกับโหมด i == 0
     * (นำมาใช้กับ username/prodId.../prodName...ActionPerformed(ActionEvent evt))
     */
    public void checkActionPerformed(JTextField field, String message, int i, JTextField nextField) {
        //<editor-fold defaultstate="collapsed" desc="// วิธีที่ 1">
        /*
        if (i == 0) {
            if (!checkCorrect(field, message, i)) {
                reenterText(field, "Oops! Blank data.");
            } else {
                nextField.requestFocus();
            }
        } else if (i == 1) {
            JOptionPane.showMessageDialog(null, "Sorry, this function hasn't been written yet.", " Error !!!", JOptionPane.ERROR_MESSAGE, errIcon);
            System.err.println("Oops!, It seems that some lines of the program code in \"UI.java\" may not be written properly.");
        } else if (i == 2) {
            if (checkCorrect(field, message, i)) {
                reenterText(field, "Oops! Incorrect data.");
            } else if (!checkCorrect(field, message, 0)) {
                reenterText(field, "Oops! Blank data.");
            } else {
                nextField.requestFocus();
            }
        }
        */
        //</editor-fold>

        // วิธีที่ 2        
        switch (i) {
            case 0 :    if (!checkCorrect(field, message, i)) {
                            reenterText(field, "Oops! Blank data.");
                        } else {
                            nextField.requestFocus();
                        }
                        break;
            case 1 :    JOptionPane.showMessageDialog(null, "Sorry, this function hasn't been written yet.", " Error !!!", JOptionPane.ERROR_MESSAGE, errIcon);
                        System.err.println("Oops!, It seems that some lines of the program code in \"UI.java\" may not be written properly.");
                        break;
            case 2 :    if (checkCorrect(field, message, i)) {
                            reenterText(field, "Oops! Incorrect data.");
                        } else if (!checkCorrect(field, message, 0)) {
                            reenterText(field, "Oops! Blank data.");
                        } else {
                            nextField.requestFocus();
                        }
                        break;
        }
    }
    
    /**
     * เมธอดสำหรับเช็คข้อความใน [JTextField] ใดๆที่เคยมี Focus ว่าจะผ่านเงื่อนไขของ
     * checkCorrect(JTextField field, String message, int i) ได้หรือไม่
     * ถ้าผ่านก็ใช้ reenterText(JTextField field, String message)
     * แต่ถ้าไม่ผ่านก็ไม่ต้องทำอะไรในเงื่อนไขข้างต้น
     * (นำมาใช้กับ prodId.../prodAmount...FocusLost(FocusEvent evt))
     */
    public void checkFocusLost(JTextField field, String message) {
        if (checkCorrect(field, message, 2)) {
            reenterText(field, "Oops! Incorrect data.");
        }
    }
    
    /**
     * เมธอดสำหรับเช็คข้อความใน [JTextField] ใดๆว่าตรงกับข้อมูลที่มีอยู่ในฐานข้อมูลหรือไม่
     * โดยจะใช้ checkCorrect(JTextField field, String message, int i) และ Product.checkProduct...(String str)
     * เช็คว่าจะผ่านเงื่อนไขในแต่ละรูปแบบได้หรือไม่ ก่อนที่จะทำในเงื่อนไขถัดไป
     * นอกจากนี้ก็จะใช้ Product.checkProduct...(String str) ในการตรวจสอบแต่ละรูปแบบซ้ำอีกครั้งหนึ่งด้วย
     * ถ้าผ่านเงื่อนไขก็จะใช้ Product.subProduct...(String str) ใส่ค่าจากที่มีอยู่ในฐานข้อมูลลงใน [JTextField] ที่กำหนดไว้
     * (นำมาใช้กับ prodId/prodNameFocusLost(FocusEvent evt))
     */
    public void checkInsert(JTextField field1, JTextField field2, JTextField field3) {
        try {
            String id = field1.getText(), name = field2.getText();
            if (checkCorrect(field1, "Type only number!", 1) && (!checkCorrect(field2, "Enter a product name.", 0) || !Product.checkProductName(name))) {
                if (Product.checkProductId(id)) {
                    name = Product.subProductName(id);
                    setText(field2, "Enter a product name.");
                    field2.setText(name);
                    field3.requestFocus();
                }
            } else if ((!checkCorrect(field1, "Type only number!", 1) || !Product.checkProductId(id)) && checkCorrect(field2, "Enter a product name.", 0)) {
                if (Product.checkProductName(name)) {
                    id = Product.subProductId(name);
                    setText(field1, "Type only number!");
                    field1.setText(id);
                    field3.requestFocus();
                }
            }
        } catch (SQLException ex) {
            System.err.println("Oops!, There's an internal error.");
            ex.printStackTrace();
        }
    }
    
    /**
     * เมธอดสำหรับเช็คข้อความใน [JTextField] ใดๆว่ามีตัวเลขมากกว่า Long.MAX_VALUE หรือเป็นศูนย์หรือไม่
     * โดยจะใช้ containsOnlyNumbers(String str) เช็คว่ามีเพียงแค่ตัวเลขหรือไม่ก่อน ก่อนที่จะทำในเงื่อนไขถัดไป
     * ถ้ามากกว่าหรือเป็นศูนย์ก็ใช้ reenterText(JTextField field, String message)
     * แต่ถ้าไม่มากกว่าหรือไม่เป็นศูนย์ก็ไม่ต้องทำอะไรในเงื่อนไขข้างต้น
     * (นำมาใช้กับ prodId...FocusLost(FocusEvent evt))
     */
    public void checkLong(JTextField field) {
        String fieldText = field.getText();
        if (containsOnlyNumbers(fieldText)) {
            if (Double.parseDouble(fieldText) > Long.MAX_VALUE) {
                reenterText(field, "Oops! Exceed value.");
            } else if (Double.parseDouble(fieldText) == 0) {
                reenterText(field, "Oops! Value cannot be zero.");
            }
        }
    }
    
    /**
     * เมธอดสำหรับเช็คข้อความใน [JTextField] ใดๆว่ามีตัวเลขมากกว่า Long.MAX_VALUE หรือเป็นศูนย์หรือไม่
     * โดยจะใช้ containsOnlyNumbers(String str) เช็คว่ามีเพียงแค่ตัวเลขหรือไม่ก่อน ก่อนที่จะทำในเงื่อนไขถัดไป
     * ถ้ามากกว่าหรือเป็นศูนย์ก็ใช้ reenterText(JTextField field, String message)
     * แต่ถ้าไม่มากกว่าหรือไม่เป็นศูนย์ก็จะขอ Focus ไปให้กับ Component อันถัดไปหรืออันใดๆก็ได้
     * (นำมาใช้กับ prodId...ActionPerformed(ActionEvent evt))
     */
    public void checkLong(JTextField field, JTextField nextField) {
        String fieldText = field.getText();
        if (containsOnlyNumbers(fieldText)) {
            if (Double.parseDouble(fieldText) > Long.MAX_VALUE) {
                reenterText(field, "Oops! Exceed value.");
            } else if (Double.parseDouble(fieldText) == 0) {
                reenterText(field, "Oops! Value cannot be zero.");
            } else {
                nextField.requestFocus();
            }
        }
    }
    
    //  เมธอดสำหรับนำทางไปที่หน้า [JPanel] ต่างๆ
    public void clickMainBack (JPanel panelSide, JPanel panelFunction) {
        panelSide.removeAll();
        panelSide.repaint();
        panelSide.revalidate();
        panelSide.add(panelFunction);
        panelSide.repaint();
        panelSide.revalidate();
    }
    
    /**
     * เมธอดสำหรับกำหนดการใช้งาน [JTextField] ใดๆสองอันโดยใช้ [JRadioButton] ใดๆ
     * เมื่อมีการกดไปแล้ว btn อันนั้นก็จะถูกเลือกขึ้นมา
     * ถ้าถูกเลือกก็จะตั้งให้ field ทำงาน (กดป้อนข้อความได้) และ oppositeField ไม่ทำงาน (กดป้อนข้อความไม่ได้)
     * นอกจากนี้ก็จะตั้งให้เป็นข้อความ default สำหรับ oppositeField ด้วย
     * แต่ถ้าไม่ถูกเลือกก็จะทำงานตรงกันข้าม
     * (นำมาใช้กับ
     * - mainSubProductActionPerformed(ActionEvent evt)
     * - subProductBy...ActionPerformed(ActionEvent evt)
     * )
     */
    public void clickSelect(JRadioButton btn, JTextField field, JTextField oppositeField, String message){
        btn.setSelected(true);
        if (btn.isSelected()) {
            field.setEnabled(true);
            oppositeField.setEnabled(false);
            defaultText(oppositeField, message);
        } else {
            field.setEnabled(false);
            oppositeField.setEditable(true);
        }
    }
    
    //  เมธอดสำหรับออกจากโปรแกรม
    public void close(boolean bl) {
        try {
            JCheckBox checkBox = new JCheckBox("Do not show this message again.");
            String msg4 = "Goodbye . . .";
            String msg1 = "Are you sure you want to exit the program?\n\n";
            String msg2 = "You have been successfully exit the program.\n" + msg4;
            String msg3 = "Your account has been successfully logged out.\nNow you will exit the program. " + msg4;
            Object[] params = {msg1, checkBox,};
            Properties settings = new Properties();
            settings.load(new FileInputStream("../Warehouse/settings.properties"));
            
            /**
             * เช็คว่าใน settings.properties มีค่า Close.prompt หรือไม่
             * ถ้ายังไม่มีก็ให้ขึ้นหน้าต่างยืนยันขึ้นมาเพื่อออกจากโปรแกรม
             * แต่ถ้ามีแล้วก็ไม่ต้องขึ้นหน้าต่างยืนยันขึ้นมาอีก สามารถออกจากโปรแกรมได้เลย
             */
            if (!settings.containsKey("Close.prompt")) {
                int response = JOptionPane.showConfirmDialog(null, params, " Confirm ?", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, markIcon);
                
                //  ต้องกด Yes เท่านั้นถึงจะออกจากโปรแกรมได้
                if (response == JOptionPane.YES_OPTION) {
                    try {
                        //  แสดงหน้าต่างข้อความตามสถานะการเข้าสู่ระบบ โดยอาศัยค่าที่ใช้เช็คว่าออกจากระบบแล้วหรือไม่มาตรวจสอบ
                        if (!bl) {
                            JOptionPane.showMessageDialog(null, msg2, " Success", JOptionPane.INFORMATION_MESSAGE, infoIcon);
                        } else {
                            JOptionPane.showMessageDialog(null, msg3, " Success", JOptionPane.INFORMATION_MESSAGE, infoIcon);
                        }
                        
                        //  เช็คว่าใน checkBox มีการกดเลือกหรือไม่ ถ้ามีให้เก็บค่า Close.prompt ใน settings.properties
                        if (checkBox.isSelected()) {
                            settings.put("Close.prompt", "false");
                            settings.store(new FileOutputStream("../Warehouse/settings.properties"), "Settings Properties File");
                        }
                        logout(true);
                    } finally {
                        System.exit(0);
                    }
                }
            } else {
                
                //  กดเพื่อออกจากโปรแกรมได้เลย
                try {
                    logout(true);
                } finally {
                    System.exit(0);
                }
            }
        }
        //  แสดงข้อความของข้อผิดพลาดเนื่องจากไม่พบไฟล์ข้อความที่ใช้
        catch (FileNotFoundException ex) {
            System.err.println("Oops!, We can not locate your file.\n" + ex.getMessage());
        } catch (Exception ex) {
            System.err.println("Oops!, There's an internal error.");
            ex.printStackTrace();            
	}
    }
    
    //  เมธอดสำหรับตั้งค่าการแสดงข้อความใน [JTextField] ใดๆให้เป็นแบบ prompt text (default, เป็นข้อความที่เห็นก่อนที่จะกดป้อนค่าข้อความใดๆลงไป)
    public void defaultText(JTextField field, String message) {
        field.setText(message);
        field.setFont(new Font("Segoe UI Semilight", Font.ITALIC, 18));
        field.setForeground(new Color(204, 204, 204));
    }
    
    /**
     * เมธอดสำหรับตั้งค่าการป้อนข้อความใหม่ใน [JTextField] ใดๆ
     * โดยจะขอ Focus ใหม่อีกรอบหนึ่ง ตั้งให้เป็นข้อความว่าง ("") และขึ้นหน้าต่างเตือนขึ้นมาตามข้อความที่เรากำหนดเอาไว้
     */
    public void reenterText(JTextField field, String message) {
        field.requestFocus();
        field.setText("");
        JOptionPane.showMessageDialog(null, message, " Warning !", JOptionPane.WARNING_MESSAGE, warnIcon);
    }
    
    /**
     * เมธอดสำหรับตั้งค่าการป้อนข้อความใน [JTextField] ใดๆโดยเช็คว่าเป็นข้อความว่าง ("") หรือข้อความที่เรากำหนดเอาไว้หรือไม่
     * ถ้าเป็นก็จะตั้งให้เป็นข้อความว่าง ("")
     * แต่ถ้าไม่เป็นก็ไม่ต้องทำอะไรในเงื่อนไขข้างต้น
     * จากนั้นจึงค่อยตั้งฟอนต์และสีตามลำดับ
     */
    public void setText(JTextField field, String message) {
        if (field.getText().isEmpty() || field.getText().equalsIgnoreCase(message)) {
            field.setText("");
        }
        field.setFont(new Font("Segoe UI", 0, 18));
        field.setForeground(Color.BLACK);
    }
}