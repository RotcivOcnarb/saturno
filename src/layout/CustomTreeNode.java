/*
 * Decompiled with CFR 0.139.
 */
package layout;

import javax.swing.ImageIcon;

public class CustomTreeNode {
    ImageIcon icon;
    String text;
    String payload;

    public CustomTreeNode(String text, ImageIcon icon, String payload) {
        this.text = text;
        this.icon = icon;
        this.payload = payload;
    }

    public String getPayload() {
        return this.payload;
    }

    public ImageIcon getImageIcon() {
        return this.icon;
    }

    public void setImageIcon(ImageIcon icon) {
        this.icon = icon;
    }

    public String toString() {
        return this.text;
    }
}

