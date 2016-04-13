package project.warehouse.function;

import java.util.Comparator;

//  @author B
public class StringComparator implements Comparator<String> {
    @Override
    public int compare(String t, String t1) {
        return t.compareTo(t1);
    }
}