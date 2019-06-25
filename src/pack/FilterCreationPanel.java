/*
 * Decompiled with CFR 0.139.
 */
package pack;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import pack.filters.ContainsKey;
import pack.filters.Filter;
import pack.filters.KeyEqual;
import pack.filters.KeyNotEqual;
import pack.filters.NotContainsKey;

public class FilterCreationPanel extends JPanel {
	private static final long serialVersionUID = 2508936196570617269L;
	JComboBox<String> filterTypes;
    JTextField txt_1;
    JLabel lbl;
    JTextField txt_2;

    public FilterCreationPanel() {
        this.setLayout(new FlowLayout());
        DefaultComboBoxModel<String> comboModel = new DefaultComboBoxModel<String>();
        comboModel.addElement("Key equals");
        comboModel.addElement("Key NOT equals");
        comboModel.addElement("Hit contains key");
        comboModel.addElement("Hit NOT contains key");
        this.filterTypes = new JComboBox<String>(comboModel);
        this.filterTypes.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                switch (FilterCreationPanel.this.filterTypes.getSelectedIndex()) {
                    case 0: {
                        System.out.println("zero!");
                        FilterCreationPanel.this.remove(FilterCreationPanel.this.lbl);
                        FilterCreationPanel.this.remove(FilterCreationPanel.this.txt_2);
                        FilterCreationPanel.this.lbl.setText("=");
                        FilterCreationPanel.this.add(FilterCreationPanel.this.lbl);
                        FilterCreationPanel.this.add(FilterCreationPanel.this.txt_2);
                        break;
                    }
                    case 1: {
                        System.out.println("um!");
                        FilterCreationPanel.this.remove(FilterCreationPanel.this.lbl);
                        FilterCreationPanel.this.remove(FilterCreationPanel.this.txt_2);
                        FilterCreationPanel.this.lbl.setText("!=");
                        FilterCreationPanel.this.add(FilterCreationPanel.this.lbl);
                        FilterCreationPanel.this.add(FilterCreationPanel.this.txt_2);
                        break;
                    }
                    case 2: {
                        System.out.println("dois!");
                        FilterCreationPanel.this.remove(FilterCreationPanel.this.lbl);
                        FilterCreationPanel.this.remove(FilterCreationPanel.this.txt_2);
                        break;
                    }
                    case 3: {
                        System.out.println("tres!");
                        FilterCreationPanel.this.remove(FilterCreationPanel.this.lbl);
                        FilterCreationPanel.this.remove(FilterCreationPanel.this.txt_2);
                    }
                }
                FilterCreationPanel.this.repaint();
            }
        });
        this.add(this.filterTypes);
        this.txt_1 = new JTextField();
        this.txt_1.setPreferredSize(new Dimension(100, 30));
        this.add(this.txt_1);
        this.lbl = new JLabel("=", 0);
        this.lbl.setPreferredSize(new Dimension(20, 30));
        this.add(this.lbl);
        this.txt_2 = new JTextField();
        this.txt_2.setPreferredSize(new Dimension(100, 30));
        this.add(this.txt_2);
    }

    public Filter createFilter() {
        switch (this.filterTypes.getSelectedIndex()) {
            case 0: {
                return new KeyEqual(this.txt_1.getText(), this.txt_2.getText());
            }
            case 1: {
                return new KeyNotEqual(this.txt_1.getText(), this.txt_2.getText());
            }
            case 2: {
                return new ContainsKey(this.txt_1.getText());
            }
            case 3: {
                return new NotContainsKey(this.txt_1.getText());
            }
        }
        return null;
    }

}

