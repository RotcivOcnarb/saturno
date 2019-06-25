/*
 * Decompiled with CFR 0.139.
 */
package layout;

import java.awt.Component;
import java.util.ArrayList;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;

import de.javasoft.synthetica.dark.TreeCellRenderer;
import layout.CustomTreeNode;

public class CustomTreeCellRenderer extends TreeCellRenderer {
	private static final long serialVersionUID = 4818953314320253849L;
	static ArrayList<String> hit_types = new ArrayList<String>();

    static {
        hit_types.add("event");
        hit_types.add("pageview");
    }

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        CustomTreeNode nodeobj = (CustomTreeNode)((DefaultMutableTreeNode)value).getUserObject();
        this.setIcon(nodeobj.getImageIcon());
        this.setText(nodeobj.text);
        return this;
    }
}

