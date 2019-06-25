/*
 * Decompiled with CFR 0.139.
 */
package pack;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import pack.MyPanel;

public class DialogOut
extends PrintStream {
    MyPanel mp;

    public DialogOut(MyPanel mp) throws FileNotFoundException {
        super("log.txt");
        this.mp = mp;
    }

    @Override
    public void print(String s) {
        this.mp.console_area.append("\n" + s);
    }
}

