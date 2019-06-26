/*
 * Decompiled with CFR 0.139.
 */
package pack;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import javax.imageio.ImageIO;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.Timer;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;
import layout.CustomTreeCellRenderer;
import layout.CustomTreeNode;
import pack.filters.Filter;

public class MyPanel extends JPanel implements Runnable {
	private static final long serialVersionUID = 8112169075851362512L;
	
	DefaultMutableTreeNode root;
    Thread hit_listener_thread; //A thread que fica escutando os logs
    
    //Mapas de id de parametro pra o significado deles
    HashMap<String, String> param_names; 
    HashMap<String, String> compose_names;
    HashMap<String, String> enhanced_names;
    
    HashMap<String, Integer> paramPriority;
    
    ArrayList<HitObject> allHits;
    
    //Ícones
    ImageIcon icon_app;
    ImageIcon icon_hit;
    public static ImageIcon icon_parameter;
    public static ImageIcon icon_value;
    ImageIcon icon_broken;
    
    ArrayList<Process> allrunning;//Lista de processos rodando pra quando o programa fechar, matar todos eles
    boolean isRefreshing = false;
    Thread updateThread; //Thread que atualiza a interface pra fazer as animaçõeszinhas maneiras
    
    //Coisas da animação da interface
    volatile Dimension panelCurrent;
    volatile float targetWidth = 240.0f;
    volatile float currentWidth = 240.0f;
    volatile Dimension consoleCurrent;
    volatile float targetConsoleHeight = 30.0f;
    volatile float currentConsoleHeight = 30.0f;
    		
    JButton button_clear;
    JButton export_btn;
    JSplitPane splitPanel;
    JPanel panel_center;
    JScrollPane playload_area_scroll;
    JTextArea playload_area;
    DefaultTableModel params_table_model;
    JTable params_table;
    JPanel center_bottom_panel;
    JTextArea console_area;
    DefaultTreeModel hits_model;
    JTree hits_tree;
    JScrollPane hits_tree_scroll;
    volatile JPanel panel_east;
    JPanel both_filters_panel;
    JPanel hit_filter_panel;
    JLabel hit_filter_title;
    JButton hit_filter_add;
    JButton hit_filter_remove;
    JList<Filter> hit_filter_list;
    DefaultListModel<Filter> hit_filter_model;
    JPanel filter_panel;
    JLabel filter_title;
    JTextField filter_txt;
    JButton filter_add;
    JButton filter_remove;
    JList<String> filter_list;
    JScrollPane list_scroll;
    DefaultListModel<String> filter_model;
    JScrollPane hit_list_scroll;
    JButton retract_right;
    Process log;
    BufferedReader br;
    boolean requestInterruption = false;

    public MyPanel() {
    	allHits = new ArrayList<>();
        new Timer(10, new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                if (MyPanel.this.panel_east != null && MyPanel.this.center_bottom_panel != null) {
                    MyPanel.this.currentWidth += (MyPanel.this.targetWidth - MyPanel.this.currentWidth) / 5.0f;
                    MyPanel.this.panelCurrent.setSize(MyPanel.this.currentWidth, MyPanel.this.panelCurrent.getHeight());
                    MyPanel.this.panel_east.setPreferredSize(MyPanel.this.panelCurrent);
                    MyPanel.this.panel_east.setSize(MyPanel.this.panelCurrent);
                    MyPanel.this.currentConsoleHeight += (MyPanel.this.targetConsoleHeight - MyPanel.this.currentConsoleHeight) / 5.0f;
                    MyPanel.this.consoleCurrent.setSize(MyPanel.this.consoleCurrent.getWidth(), MyPanel.this.currentConsoleHeight);
                    MyPanel.this.center_bottom_panel.setPreferredSize(MyPanel.this.consoleCurrent);
                    MyPanel.this.center_bottom_panel.setSize(MyPanel.this.consoleCurrent);
                    MyPanel.this.revalidate();
                    MyPanel.this.repaint();
                }
            }
        }).start();
        splitPanel = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        
        this.panelCurrent = new Dimension(200, 1000);
        this.consoleCurrent = new Dimension(200, 200);
        this.addComponentListener(new ComponentListener(){
            public void componentShown(ComponentEvent e) {}
            public void componentResized(ComponentEvent e) {
                MyPanel.this.list_scroll.setPreferredSize(new Dimension(200, (int)MyPanel.this.filter_panel.getSize().getHeight() - 150));
                MyPanel.this.hit_list_scroll.setPreferredSize(new Dimension(200, (int)MyPanel.this.hit_filter_panel.getSize().getHeight() - 120));
                MyPanel.this.repaint();
            }
            public void componentMoved(ComponentEvent e) {}
            public void componentHidden(ComponentEvent e) {}
        });
        this.allrunning = new ArrayList<Process>();
        this.filter_model = new DefaultListModel<String>();
        this.hit_filter_model = new DefaultListModel<Filter>();
        this.loadParamNames();
        this.hit_listener_thread = new Thread(this);
        this.hit_listener_thread.start();
        this.setLayout(new BorderLayout());
        this.both_filters_panel = new JPanel();
        this.both_filters_panel.setLayout(new GridLayout(2, 1));
        this.panel_east = new JPanel();
        this.panel_east.setLayout(new BorderLayout());
        this.filter_panel = new JPanel();
        this.filter_panel.setPreferredSize(new Dimension(200, 1000));
        this.filter_panel.setLayout(new FlowLayout());
        this.filter_title = new JLabel("Hide parameters", 0);
        this.filter_title.setFont(new Font("Arial", 1, 14));
        this.filter_title.setPreferredSize(new Dimension(200, 30));
        this.filter_panel.add(this.filter_title);
        this.filter_txt = new JTextField();
        this.filter_txt.setPreferredSize(new Dimension(200, 30));
        this.filter_panel.add(this.filter_txt);
        this.filter_add = new JButton("Add Filter");
        this.filter_add.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                if (!MyPanel.this.filter_txt.getText().equals("")) {
                    MyPanel.this.filter_model.addElement(MyPanel.this.filter_txt.getText());
                    MyPanel.this.filter_txt.setText("");
                }
            }
        });
        this.filter_add.setPreferredSize(new Dimension(200, 30));
        this.filter_panel.add(this.filter_add);
        this.filter_remove = new JButton("Remove Filter");
        this.filter_remove.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                if (MyPanel.this.filter_list.getSelectedIndex() != -1) {
                    MyPanel.this.filter_model.remove(MyPanel.this.filter_list.getSelectedIndex());
                }
            }
        });
        this.filter_remove.setPreferredSize(new Dimension(200, 30));
        this.filter_panel.add(this.filter_remove);
        this.filter_list = new JList<String>(this.filter_model);
        this.list_scroll = new JScrollPane(this.filter_list);
        this.filter_panel.add(this.list_scroll);
        this.both_filters_panel.add(this.filter_panel);
        this.hit_filter_panel = new JPanel();
        this.hit_filter_panel.setPreferredSize(new Dimension(200, 1000));
        this.hit_filter_panel.setLayout(new FlowLayout());
        this.hit_filter_title = new JLabel("Exclude hits", 0);
        this.hit_filter_title.setFont(new Font("Arial", 1, 14));
        this.hit_filter_title.setPreferredSize(new Dimension(200, 30));
        this.hit_filter_panel.add(this.hit_filter_title);
        this.hit_filter_add = new JButton("Add Filter");
        this.hit_filter_add.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                FilterCreationPanel fcp = new FilterCreationPanel();
                int resp = JOptionPane.showConfirmDialog(null, fcp, "Filter add", 2);
                if (resp == 0) {
                    MyPanel.this.hit_filter_model.addElement(fcp.createFilter());
                }
            }
        });
        this.hit_filter_add.setPreferredSize(new Dimension(200, 30));
        this.hit_filter_panel.add(this.hit_filter_add);
        this.hit_filter_remove = new JButton("Remove Filter");
        this.hit_filter_remove.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                if (MyPanel.this.hit_filter_list.getSelectedIndex() != -1) {
                    MyPanel.this.hit_filter_model.remove(MyPanel.this.hit_filter_list.getSelectedIndex());
                }
            }
        });
        this.hit_filter_remove.setPreferredSize(new Dimension(200, 30));
        this.hit_filter_panel.add(this.hit_filter_remove);
        this.hit_filter_list = new JList<Filter>(this.hit_filter_model);
        this.hit_list_scroll = new JScrollPane(this.hit_filter_list);
        this.hit_list_scroll.setPreferredSize(new Dimension(200, 500));
        this.hit_filter_panel.add(this.hit_list_scroll);
        this.both_filters_panel.add(this.hit_filter_panel);
        this.panel_east.add((Component)this.both_filters_panel, "Center");
        this.retract_right = new JButton(">");
        this.retract_right.setPreferredSize(new Dimension(40, 20));
        this.retract_right.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent arg0) {
                if (MyPanel.this.retract_right.getText().equals(">")) {
                    MyPanel.this.retract_right.setText("<");
                    MyPanel.this.targetWidth = 40.0f;
                } else if (MyPanel.this.retract_right.getText().equals("<")) {
                    MyPanel.this.retract_right.setText(">");
                    MyPanel.this.targetWidth = 240.0f;
                }
            }
        });
        this.panel_east.add((Component)this.retract_right, "West");
        this.add((Component)this.panel_east, "East");
        try {
            this.icon_app = new ImageIcon(MyPanel.getScaledImage(ImageIO.read(this.getClass().getResourceAsStream("/smartphone.png")), 16, 16));
            this.icon_hit = new ImageIcon(MyPanel.getScaledImage(ImageIO.read(this.getClass().getResourceAsStream("/rocket-hitting-target.png")), 16, 16));
            icon_parameter = new ImageIcon(MyPanel.getScaledImage(ImageIO.read(this.getClass().getResourceAsStream("/tools.png")), 16, 16));
            icon_value = new ImageIcon(MyPanel.getScaledImage(ImageIO.read(this.getClass().getResourceAsStream("/value.png")), 16, 16));
            this.icon_broken = new ImageIcon(MyPanel.getScaledImage(ImageIO.read(this.getClass().getResourceAsStream("/broken-link.png")), 16, 16));
        }
        catch (Exception e) {
            Main.showException(e);
        }
        this.root = new DefaultMutableTreeNode(new CustomTreeNode("Aplicação", this.icon_app, ""));
        this.hits_model = new DefaultTreeModel(this.root);
        this.hits_tree = new JTree(this.hits_model);
        this.hits_tree.addTreeSelectionListener(new TreeSelectionListener(){
            public void valueChanged(TreeSelectionEvent e) {
                int i;
                DefaultMutableTreeNode node = (DefaultMutableTreeNode)MyPanel.this.hits_tree.getLastSelectedPathComponent();
                if (node == null) 
                    return;
                
                try {
                    MyPanel.this.playload_area.setText(((CustomTreeNode)node.getUserObject()).getPayload());
                }
                catch (Exception e1) {
                    e1.printStackTrace();
                }
                for (i = MyPanel.this.params_table_model.getRowCount() - 1; i >= 0; --i) 
                    MyPanel.this.params_table_model.removeRow(i);
                
                if (node != null && node.getChildCount() > 0) {
                    for (i = 0; i < node.getChildCount(); ++i) {
                        DefaultMutableTreeNode ch = (DefaultMutableTreeNode)node.getChildAt(i);
                        if (ch.getChildCount() > 0) {
                            DefaultMutableTreeNode val = (DefaultMutableTreeNode)ch.getChildAt(0);
                            MyPanel.this.params_table_model.addRow(new Object[]{ch.getUserObject(), val.getUserObject()});
                            continue;
                        }
                        MyPanel.this.params_table_model.addRow(new Object[]{node.getUserObject(), ch.getUserObject()});
                    }
                }
            }
        });
        try {
            this.hits_tree.setCellRenderer(new CustomTreeCellRenderer());
        }
        catch (Exception e) {
            Main.showException(e);
        }
        this.hits_tree_scroll = new JScrollPane(this.hits_tree);
        this.splitPanel.add((Component)this.hits_tree_scroll);

        this.hits_tree_scroll.setPreferredSize(new Dimension(400, 1000));
        JPanel upper_panel = new JPanel();
        upper_panel.setLayout(new BorderLayout());
        this.button_clear = new JButton("Clear");
        this.button_clear.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent arg0) {
                MyPanel.this.root.removeAllChildren();
                MyPanel.this.hits_model.setRoot(MyPanel.this.root);
            }
        });
        upper_panel.add((Component)this.button_clear, "Center");
        export_btn = new JButton("Export");
        export_btn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					export();
				} catch (WriteException | IOException e1) {
					e1.printStackTrace();
				}
			}
        });
        upper_panel.add(export_btn, "East");
        ImageIcon refresh_icon = null;
        try {
            refresh_icon = new ImageIcon(MyPanel.getScaledImage(ImageIO.read(this.getClass().getResourceAsStream("/refresh-button.png")), 16, 16));
        }
        catch (IOException e1) {
            e1.printStackTrace();
        }
        JButton refresh = new JButton();
        refresh.setIcon(refresh_icon);
        refresh.setPreferredSize(new Dimension(40, 30));
        refresh.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent arg0) {
                if (!MyPanel.this.isRefreshing) {
                    MyPanel.this.resetADB();
                }
            }
        });
        upper_panel.add((Component)refresh, "West");
        this.add((Component)upper_panel, "North");
        this.panel_center = new JPanel();
        this.panel_center.setLayout(new BorderLayout());
        this.playload_area = new JTextArea();
        this.playload_area.setLineWrap(true);
        this.playload_area.setPreferredSize(new Dimension(100, 100));
        this.playload_area.setEditable(false);
        JScrollPane payload_scroll = new JScrollPane(this.playload_area);
        payload_scroll.setPreferredSize(new Dimension(100, 50));
        this.panel_center.add(payload_scroll, "North");
        this.params_table_model = new DefaultTableModel();
        this.params_table_model.addColumn("Name");
        this.params_table_model.addColumn("Value");
        this.params_table = new JTable(this.params_table_model);
        this.playload_area_scroll = new JScrollPane(this.params_table);
        this.panel_center.add(this.playload_area_scroll, BorderLayout.CENTER);
        this.center_bottom_panel = new JPanel();
        this.center_bottom_panel.setLayout(new BorderLayout());
        this.console_area = new JTextArea();
        this.console_area.setLineWrap(true);
        this.console_area.setEditable(false);
        JScrollPane console_scroll = new JScrollPane(this.console_area);
        console_scroll.setPreferredSize(new Dimension(100, 100));
        this.center_bottom_panel.add((Component)console_scroll, "Center");
        final JButton retract_console = new JButton("^");
        retract_console.setPreferredSize(new Dimension(40, 30));
        retract_console.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent arg0) {
                if (retract_console.getText().equals("v")) {
                    retract_console.setText("^");
                    MyPanel.this.targetConsoleHeight = 30.0f;
                } else if (retract_console.getText().equals("^")) {
                    retract_console.setText("v");
                    MyPanel.this.targetConsoleHeight = 200.0f;
                }
            }
        });
        this.center_bottom_panel.add(retract_console, "North");
        this.panel_center.add(this.center_bottom_panel, "South");
        splitPanel.add(this.panel_center);
        this.add(splitPanel, "Center");
    }

    public String[] removeEmpties(String[] arr) {
        int cont = 0;
        for (String s : arr) {
            if (s.equals("")) continue;
            ++cont;
        }
        String[] result = new String[cont];
        int i = 0;
        for (String s : arr) {
            if (s.equals("")) continue;
            result[i] = s;
            ++i;
        }
        return result;
    }

    public String getName(String code, boolean debug) {
        if (this.param_names.get(code) == null) {
            if (!code.split("\\d+")[0].equals(code)) {
                String result = "(" + code + ") ";
                String[] ltr = code.split("\\d+");
                String[] nms = this.removeEmpties(code.split("[a-z]+"));
                int nums = 0;
                for (String s : ltr) {
                    result = nums < nms.length ? String.valueOf(result) + this.compose_names.get(s) + " " + nms[nums] + " " : String.valueOf(result) + this.compose_names.get(s);
                    ++nums;
                }
                return result;
            }
            return code;
        }
        return "(" + code + ") " + this.param_names.get(code);
    }

    private void loadParamNames() {
    	paramPriority  = new HashMap<>();
    	paramPriority.put("t", 0);
    	paramPriority.put("dl", 1);
    	paramPriority.put("dp", 2);
    	paramPriority.put("dh", 3);
    	paramPriority.put("cd", 4);
    	paramPriority.put("ec", 5);
    	paramPriority.put("ea", 6);
    	paramPriority.put("el", 7);
    	    	
        this.param_names = new HashMap<String, String>();
        this.param_names.put("tid", "Tracking ID");
        this.param_names.put("aip", "Anonymous IP");
        this.param_names.put("ds", "Data Source");
        this.param_names.put("qt", "Queue Time");
        this.param_names.put("z", "Cache");
        this.param_names.put("cid", "Client ID");
        this.param_names.put("uid", "User ID");
        this.param_names.put("sc", "Session Control");
        this.param_names.put("uip", "User IP");
        this.param_names.put("ua", "User Agent");
        this.param_names.put("geoid", "Geographic ID");
        this.param_names.put("dr", "Document Referer");
        this.param_names.put("cn", "Campaign Name");
        this.param_names.put("cs", "Campaign Source");
        this.param_names.put("cm", "Campaign Medium");
        this.param_names.put("ck", "Campaign Keyword");
        this.param_names.put("cc", "Campaign Content");
        this.param_names.put("ci", "Campaign ID");
        this.param_names.put("gclid", "Adwords ID");
        this.param_names.put("dclid", "Announce ID");
        this.param_names.put("sr", "Screen Resolution");
        this.param_names.put("vp", "Viewport");
        this.param_names.put("de", "Document Encoding");
        this.param_names.put("sd", "Screen Depth");
        this.param_names.put("ul", "User Language");
        this.param_names.put("je", "Java Enabled");
        this.param_names.put("fl", "Flash Version");
        this.param_names.put("t", "Hit type");
        this.param_names.put("ni", "Non interactive Hit");
        this.param_names.put("dl", "Document Location");
        this.param_names.put("dh", "Document Host");
        this.param_names.put("dp", "Document Path");
        this.param_names.put("dt", "Document Title");
        this.param_names.put("cd", "Screen Name");
        this.param_names.put("linkid", "Link ID");
        this.param_names.put("an", "App Name");
        this.param_names.put("aid", "App ID");
        this.param_names.put("aiid", "Installer ID");
        this.param_names.put("av", "App Version");
        this.param_names.put("ec", "Event Category");
        this.param_names.put("ea", "Event Action");
        this.param_names.put("el", "Event Label");
        this.param_names.put("ev", "Event Value");
        this.param_names.put("ti", "Transaction ID");
        this.param_names.put("ta", "Transaction Affiliate");
        this.param_names.put("tr", "Transaction Recipe");
        this.param_names.put("ts", "Transaction Sent");
        this.param_names.put("tt", "Transaction Tributes");
        this.param_names.put("in", "Item Name");
        this.param_names.put("il", "Item List");
        this.param_names.put("ip", "Item Price");
        this.param_names.put("iq", "Item Quantity");
        this.param_names.put("ic", "Item Code");
        this.param_names.put("iv", "Item Category");
        this.param_names.put("pa", "Product Action");
        this.param_names.put("tcc", "Coupon Code");
        this.param_names.put("pal", "Product Action List");
        this.param_names.put("cos", "Checkout Step");
        this.param_names.put("col", "Checkout Step Option");
        this.param_names.put("promoa", "Promotion Action");
        this.param_names.put("cu", "Currency Code");
        this.param_names.put("sn", "Social Network");
        this.param_names.put("sa", "Social Action");
        this.param_names.put("st", "Social Action Target");
        this.param_names.put("utc", "User Timing Category");
        this.param_names.put("utv", "User Timing Variable");
        this.param_names.put("utt", "User Timing Time");
        this.param_names.put("utl", "User Timing Label");
        this.param_names.put("plt", "Page Load Time");
        this.param_names.put("dns", "Time to DNS Lookup");
        this.param_names.put("pdt", "Page Download Time");
        this.param_names.put("rrt", "Redirect Response Time");
        this.param_names.put("tcp", "TCP Connect Time");
        this.param_names.put("srt", "Server Response Time");
        this.param_names.put("dit", "DOM Interactive Time");
        this.param_names.put("clt", "Content Load Time");
        this.param_names.put("exd", "Exception Description");
        this.param_names.put("exf", "Exception Fail");
        this.param_names.put("xid", "Experiment ID");
        this.param_names.put("xvar", "Experiment Variant");
        this.compose_names = new HashMap<String, String>();
        this.compose_names.put("pr", "Price");
        this.compose_names.put("cd", "Custom Dimension");
        this.compose_names.put("cm", "Custom Metric");
        this.compose_names.put("id", "ID");
        this.compose_names.put("nm", "Name");
        this.compose_names.put("br", "Brand");
        this.compose_names.put("ca", "Category");
        this.compose_names.put("va", "Variant");
        this.compose_names.put("ps", "Position");
        this.compose_names.put("gc", "Content Group");
        this.compose_names.put("pi", "Product ID");
        this.enhanced_names = new HashMap<String, String>();
        this.enhanced_names.put("pr", "Product");
        this.enhanced_names.put("il", "Item List");
        this.enhanced_names.put("promo", "Promotion");
        HitNameInfo.compose_names = this.compose_names;
        HitNameInfo.param_names = this.param_names;
        HitNameInfo.enhanced_names = this.enhanced_names;
        try {
            String ln;
            File f = new File("filters.conf");
            if (!f.exists()) {
                f.createNewFile();
            }
            FileReader fr = new FileReader(f);
            BufferedReader br = new BufferedReader(fr);
            while ((ln = br.readLine()) != null) {
                this.filter_model.addElement(ln);
            }
            br.close();
        }
        catch (Exception e) {
            Main.showException(e);
        }
    }

    @Override
    public void paint(Graphics g) {
        if (this.list_scroll != null) {
            this.list_scroll.setPreferredSize(new Dimension(200, (int)this.filter_panel.getSize().getHeight() - 150));
            this.hit_list_scroll.setPreferredSize(new Dimension(200, (int)this.hit_filter_panel.getSize().getHeight() - 120));
        }
        super.paint(g);
    }

    public void resetADB() {
        this.isRefreshing = true;
        this.end();
        this.hit_listener_thread.interrupt();
        System.out.println("Interrompendo");
        while (this.hit_listener_thread.isAlive()) {
        }
        this.requestInterruption = false;
        this.hit_listener_thread = new Thread(this);
        System.out.println("Reiniciando");
        this.hit_listener_thread.start();
    }
    
    public String findValue(String[] array, String key) {
    	
    	for(String p : array) {
    		if(p.startsWith(key)) {
    			return p.split("=")[1];
    		}
    	}
    	return null;
    	
    }
    
    //metodo que salva em xlsx
    public void export() throws IOException, RowsExceededException, WriteException {
    	
    	File file;
    	JFileChooser chooser = new JFileChooser();
    	
    	if (chooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
    		file = new File(chooser.getSelectedFile().getAbsolutePath() + ".xlsx");
    		
    		WritableWorkbook workbook = Workbook.createWorkbook(file);
    		WritableSheet sheet = workbook.createSheet("Hits", 0);
    		
    		//Pega todos os parametros e mapeia
    		ArrayList<String> allParams = new ArrayList<>();
    		for(int i = 0; i < allHits.size(); i++) {
    			HitObject hit = allHits.get(i);
    			for(String key : hit.getParameters().keySet()) {
    				if(!allParams.contains(key)) {
    					allParams.add(key);
    				}
    			}
    			
    		}
    		
    		Collections.sort(allParams, new Comparator<String>() {
				public int compare(String h1, String h2) {
					if(paramPriority.containsKey(h1) && paramPriority.containsKey(h2)) {
						return paramPriority.get(h1) - paramPriority.get(h2);
					}
					else if(paramPriority.containsKey(h1)) {
						return -1;
					}
					else if(paramPriority.containsKey(h2)) {
						return 1;
					}
					else {
						return h2.compareTo(h1);
					}
				}
    		});
    		
    		//Agora cria o header do excel
    		WritableFont arial10font = new WritableFont(WritableFont.ARIAL, 14, WritableFont.BOLD); 
    		WritableCellFormat arial10format = new WritableCellFormat (arial10font); 
    		
    		for(int i = 0; i < allParams.size(); i ++) {
    			Label label = new Label(i, 0, getName(allParams.get(i), false), arial10format);
				sheet.addCell(label);
    		}
    		//E coloca todos os hits
    		for(int i = 0; i < allHits.size(); i++) {
    			HitObject hit = allHits.get(i);
    			
    			for(int k = 0; k < allParams.size(); k ++) {
    				String val = hit.getParameter(allParams.get(k));
    				if(val != null) {
	        			Label label = new Label(k, i+1, val);
	    				sheet.addCell(label);
    				}
        		}
    			
    		}
    		
    		workbook.write();
    		workbook.close();
    		Label label = new Label(0, 0, "Cerula");
    		sheet.addCell(label);
    		
    		JOptionPane.showMessageDialog(null, "Saved!");
    	}

    	
    	
    	
    }

    //A thread que escuta os logs
    public void run() {
        try {
            
        	//Inicia o servidor
            Runtime.getRuntime().exec("adb start-server").waitFor();
            //Configura o listener do logcat
            Process p = Runtime.getRuntime().exec("adb shell setprop log.tag.GAv4 DEBUG");
            this.allrunning.add(p);
            p.waitFor();
            //Limpa os disparos de antes
            p = Runtime.getRuntime().exec("adb logcat -c");
            this.allrunning.add(p);
            p.waitFor();
            //Printa os dispositivos (serve pra nada, só estética)
            ArrayList<String> devices = Main.getDevicesConnected();
            for (String s2 : devices) {
                System.out.println("\t" + s2);
            }
            this.isRefreshing = false;
            //Começa a escutar os disparos
            this.log = Runtime.getRuntime().exec("adb logcat -s GAv4");
            this.allrunning.add(this.log);
            this.br = new BufferedReader(new InputStreamReader(this.log.getInputStream()));
            String s2;
            //Esse while roda escutando os disparos
            while ((s2 = this.br.readLine()) != null && !this.requestInterruption) {
            	///A string é um payloadzao gigante, com um "header" que eu to arrancando fora, separado  por ":"
                String[] arr = s2.split(":");
                if (arr.length < 5) continue; //Checa se tem o numero correto de coisas
                String title = "ERR";
                //Monta a string de volta pegando só o payload (do 4º pedaço pra frente)
                String hit_s = "";
                for (int i = 4; i < arr.length; ++i) {
                    hit_s += arr[i] + ":";
                }
                hit_s = hit_s.substring(0, hit_s.length() - 1); //Tira o ultimo caracter ??
                
                try {
                	//Procura o titulo baseado no parametro "t" do payload
                    title = hit_s.replaceAll("\\s", "").contains(",t=") ? hit_s.replaceAll("\\s", "").split(",t=")[1].split(",")[0] : "undetected";
                }
                catch (Exception e) {
                    Main.showException(e);
                    System.out.println("ERRO: " + hit_s.replaceAll("\\s", ""));
                    continue;
                }
                if (!hit_s.contains("=")) continue;
                
                //Separa todos os parametros em strings no formato chave=valor
                String[] params = hit_s.replaceAll("\\s", "").split(",");
                
                allHits.add(new HitObject(params));
                
                if(title.equals("event")) {
                	//Se for evento já coloca o título como category > action > label (copiei do roger watcher kkk)
                	String cat = findValue(params, "ec");
                	String act = findValue(params, "ea");
                	String lbl = findValue(params, "el");
                	title = cat + " > " + act + " > " + lbl;
                }
                
                if(title.equals("pageview")) {
                	//Caso seja pageview, já mostra a url
                	title = "PV > " + findValue(params, "dl");
                }
                
                if(title.equals("screenview")) {
                	//Screen view a mesma coisa pro nome da tela
                	title = "SV > " + findValue(params, "cd");
                }
                
                //Começa a criar o nodo que vai aparecer na árvore da Esquerda
                DefaultMutableTreeNode hit = new DefaultMutableTreeNode(new CustomTreeNode(title, this.icon_hit, s2));
                boolean out = true;
                //Aplica todos os filtros usando a funçãozinha basica "evaluate()"
                for (int i = 0; i < this.hit_filter_model.size(); ++i) {
                    Filter f = this.hit_filter_model.get(i);
                    if (!f.evaluate(params)) continue;
                    out = false;
                    break;
                }
                if (!out) continue;
                //Cria o agrupamento de Enhanced Ecommerce
                EnhancedGroup group = new EnhancedGroup();
                for (String k : params) {
                	
                	//Aqui começa o monstro, tente entender por sua própria conta e risco
                	//não lembro como eu fiz isso, não me pergunta pf
                    DefaultMutableTreeNode param;
                    if (this.filter_model.contains(k.split("=")[0]) || !k.contains("=")) continue;
                    HitNameInfo hitName = new HitNameInfo(k);
                    if (hitName.fullName.equals("erro")) continue;
                    if (hitName.getTypeOfEnhance() != "") {
                        if (!group.containsChild(hitName.getTypeOfEnhance())) {
                            group.put(hitName.getTypeOfEnhance(), new EnhancedGroup());
                        }
                        EnhancedGroup iteration_group = group.getChild(hitName.getTypeOfEnhance());
                        for (int id : hitName.getIds()) {
                            if (!iteration_group.containsChild(String.valueOf(id))) 
                                iteration_group.put(String.valueOf(id), new EnhancedGroup());
                            iteration_group = iteration_group.getChild(String.valueOf(id));
                        }
                        iteration_group.put(hitName.getFullName(), hitName);
                    }
                    if (hitName.isEnhancedEcommerce()) continue;
                    if (k.split("=").length == 1) {
                        if (!k.endsWith("=")) continue;
                        param = new DefaultMutableTreeNode(new CustomTreeNode(hitName.getFullName(), icon_parameter, k));
                        param.add(new DefaultMutableTreeNode(new CustomTreeNode("", icon_value, k)));
                        hit.add(param);
                        continue;
                    }
                    param = new DefaultMutableTreeNode(new CustomTreeNode(hitName.getFullName(), icon_parameter, k));
                    param.add(new DefaultMutableTreeNode(new CustomTreeNode(k.split("=")[1], icon_value, k)));
                    hit.add(param);
                    //Aqui termina o monstro
                }
                if (!group.isEmpty()) 
                    hit.add(group.getNode("enhanced"));
                this.root.add(hit);
                this.hits_model.setRoot(this.root);
            }
            this.br.close();
        }
        catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        catch (InterruptedException e1) {
            Main.showException(e1);
        }
    }

    public static Image getScaledImage(Image srcImg, int w, int h) {
        BufferedImage resizedImg = new BufferedImage(w, h, 2);
        Graphics2D g2 = resizedImg.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.drawImage(srcImg, 0, 0, w, h, null);
        g2.dispose();
        return resizedImg;
    }

    public void end() {
        this.requestInterruption = true;
        try {
            Runtime.getRuntime().exec("adb kill-server").waitFor();
        }
        catch (InterruptedException e1) {
            Main.showException(e1);
        }
        catch (IOException e1) {
            Main.showException(e1);
        }
        for (Process p : this.allrunning) {
            try {
                p.destroy();
            }
            catch (Exception e) {
                System.out.println("N\u00e3o consegui matar o processo");
                Main.showException(e);
            }
        }
        this.allrunning.clear();
    }

}

