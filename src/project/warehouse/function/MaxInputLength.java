package project.warehouse.function;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

//  @author B
public final class MaxInputLength extends PlainDocument {
    
    private final int limit;

    public MaxInputLength(int limit) {
        this.limit = limit;
    }

    @Override
    public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
        if ((getLength() + str.length()) <= limit) {
            super.insertString(offs, str, a);
        }
    }
}