package project.warehouse.function;

import java.io.File;
import java.io.FilenameFilter;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;
import project.warehouse.driver.UIMethods;

//  @author Better3
public class ExportFileFilter extends FileFilter {
    private String ext;
    private String des;
    private static UIMethods ctrl = new UIMethods();

    public ExportFileFilter(String extension, String description) {
        this.ext = extension;
        this.des = description;
    }
    
    @Override
    public boolean accept(File file) {
        if (file.isDirectory()) {
            return true;
        }
        return file.getName().endsWith(ext);
    }
 
    @Override
    public String getDescription() {
        return des + String.format(" (*%s)", ext);
    }
    
    /**
     * เมธอดสำหรับเช็คไฟล์ใดๆที่เรากำหนดเอาไว้ว่าตรงกับไฟล์ที่มีอยู่ในโฟลเดอร์เดียวกันกับไฟล์ที่เรากำหนดเอาไว้หรือไม่
     * เพื่อป้องกันการ overwrite ตัวไฟล์นั้นโดยไม่ได้ตั้งใจ โดยจะใส่ชนิด/ประเภท/นามสกุลของไฟล์เพื่อความแม่นยำในการตรวจสอบ
     * (นำมาใช้กับ exportCheckStockActionPerformed(ActionEvent evt))
     */
    public static boolean checkExist(File file, String ext) {
        boolean match = false;
        String found = "", name = file.getName(), path = file.getAbsolutePath().replace(name, "");
        File dir = new File(path);
        FilenameFilter filter = new FilenameFilter() {
            @Override
            public boolean accept(File dir, String fn) {
                return fn.startsWith(name + ext);
            }
        };
        String[] result = dir.list(filter);
        if (result != null) {
            for (String filename : result) {
                found += path + filename;
                match = true;
            }
        }
        
        if (match) {
            String msg = "Do you want to overwrite an already existed file?\n" + found;
            int response = JOptionPane.showConfirmDialog(null, msg, " Confirm ?", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, ctrl.markIcon);
            switch(response){
                case JOptionPane.YES_OPTION :   return true;
            }
        } else {
            return true;
        }
        return false;
    }
}