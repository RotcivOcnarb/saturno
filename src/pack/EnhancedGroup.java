/*
 * Decompiled with CFR 0.139.
 */
package pack;

import java.util.HashMap;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.tree.DefaultMutableTreeNode;
import layout.CustomTreeNode;
import pack.HitNameInfo;
import pack.MyPanel;

public class EnhancedGroup {
    HashMap<String, HitNameInfo> parameters;
    HashMap<String, EnhancedGroup> childs;
    static ImageIcon icon_coin;

    public EnhancedGroup() {
        if (icon_coin == null) {
            try {
                icon_coin = new ImageIcon(MyPanel.getScaledImage(ImageIO.read(this.getClass().getResourceAsStream("/moeda.png")), 16, 16));
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        this.parameters = new HashMap<String, HitNameInfo>();
        this.childs = new HashMap<String, EnhancedGroup>();
    }

    public void put(String key, HitNameInfo value) {
        this.parameters.put(key, value);
    }

    public void put(String key, EnhancedGroup value) {
        this.childs.put(key, value);
    }

    public HitNameInfo getParameter(String key) {
        return this.parameters.get(key);
    }

    public EnhancedGroup getChild(String key) {
        return this.childs.get(key);
    }

    public boolean containsChild(String child) {
        return this.childs.containsKey(child);
    }

    public boolean containsParameter(String param) {
        return this.parameters.containsKey(param);
    }

    public boolean isEmpty() {
        return this.parameters.size() + this.childs.size() <= 0;
    }

    public DefaultMutableTreeNode getNode(String name) {
        DefaultMutableTreeNode node = new DefaultMutableTreeNode();
        node.setUserObject(new CustomTreeNode(name, icon_coin, "enhanced ecommerce"));
        for (String s : this.parameters.keySet()) {
            DefaultMutableTreeNode param = new DefaultMutableTreeNode(new CustomTreeNode(this.parameters.get((Object)s).fullName, MyPanel.icon_parameter, this.parameters.get((Object)s).raw_name));
            param.add(new DefaultMutableTreeNode(new CustomTreeNode(this.parameters.get(s).getValue(), MyPanel.icon_value, this.parameters.get(s).getValue())));
            node.add(param);
        }
        for (String s : this.childs.keySet()) {
            node.add(this.childs.get(s).getNode(s));
        }
        return node;
    }

    public String toString() {
        String txt = "=== Grupo ===\n";
        txt = String.valueOf(txt) + "\t" + this.parameters.size() + " parametros:\n";
        for (String s : this.parameters.keySet()) {
            txt = String.valueOf(txt) + "\t" + this.parameters.get(s).toString() + "\n";
        }
        txt = String.valueOf(txt) + "\t" + this.childs.size() + " subgrupos:\n";
        for (String s : this.childs.keySet()) {
            txt = String.valueOf(txt) + "\t" + this.childs.get(s).toString() + "\n";
        }
        return txt;
    }
}

