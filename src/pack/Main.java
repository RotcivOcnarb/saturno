/*
 * Decompiled with CFR 0.139.
 */
package pack;

import java.awt.Color;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import com.jtattoo.plaf.hifi.HiFiLookAndFeel;

public class Main {
    //static String ADB_PATH;

    public static void showException(Exception e) {
        e.printStackTrace();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable(){public void run() {
        	
        	
                final JFrame frame = new JFrame("Saturno");
                new Thread(new Runnable(){

                    @Override
                    public void run() {
                        try {
                            UIManager.setLookAndFeel(new HiFiLookAndFeel());
                        }
                        catch (Exception e) {
                            Main.showException(e);
                        }
                        Main.showDevices();
                        Main.createWindow();
                        ((SplashPanel)frame.getContentPane()).exit();
                    }
                }).start();
                Image icon = Toolkit.getDefaultToolkit().getImage(this.getClass().getResource("/saturn_icon.png"));
                frame.setSize(200, 200);
                frame.setUndecorated(true);
                frame.setIconImage(icon);
                frame.setDefaultCloseOperation(0);
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
                frame.setBackground(new Color(0, 0, 0, 0));
                frame.setContentPane(new SplashPanel());
                frame.getContentPane().setBackground(new Color(1.0f, 1.0f, 1.0f, 0.0f));
            }

        });
    }

    public static void createWindow() {
        JFrame frame = new JFrame("Saturno");
        frame.setSize(800, 600);
        frame.setIconImage(Toolkit.getDefaultToolkit().getImage(new Main().getClass().getResource("/saturn_icon.png")));
        frame.setDefaultCloseOperation(3);
        frame.setLocationRelativeTo(null);
        final MyPanel mp = new MyPanel();
        try {
            System.setErr(new DialogOut(mp));
            System.setOut(new DialogOut(mp));
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        frame.setContentPane(mp);
        frame.addWindowListener(new WindowListener(){
            public void windowOpened(WindowEvent e) {}
            public void windowIconified(WindowEvent e) {}
            public void windowDeiconified(WindowEvent e) {}
            public void windowDeactivated(WindowEvent e) {}
            public void windowClosing(WindowEvent e) {
                mp.end();
            }
            public void windowClosed(WindowEvent e) {}
            public void windowActivated(WindowEvent e) {}
        });
        frame.setVisible(true);
    }

    public static ArrayList<String> getDevicesConnected() throws InterruptedException, IOException {
        String s;
        ArrayList<String> retorno = new ArrayList<String>();
        Process devices = Runtime.getRuntime().exec("adb devices -l");
        BufferedReader dr = new BufferedReader(new InputStreamReader(devices.getInputStream()));
        boolean header = true;
        while ((s = dr.readLine()) != null) {
            if (header) {
                header = false;
                continue;
            }
            String[] aa = s.split(":");
            if (aa.length <= 2) continue;
            retorno.add(s.split(":")[2].split("\\s+")[0]);
        }
        devices.waitFor();
        return retorno;
    }

    private static void showDevices() {
        try {
                    	
            System.out.println("starting server");
            Runtime.getRuntime().exec(new String[] {"adb start-server"}, new String[] {}, new File("C://"));
                        
            ArrayList<String> devices = Main.getDevicesConnected();
            for(String d : devices)
            	System.out.println("\t" + d);
            if (devices.size() == 0) {
                JOptionPane.showMessageDialog(null, "Nenhum dispositivo encontrado, conecte seu dispositivo Android antes de executar o aplicativo");
                //Runtime.getRuntime().exec("adb kill-server").waitFor();
                //System.exit(1);
            } else {
                String dispo = "";
                for (String s : devices) {
                    dispo = String.valueOf(dispo) + s + "\n";
                }
                JOptionPane.showMessageDialog(null, String.valueOf(devices.size()) + " dispositivos encontrados:\n" + dispo, "Dispositivos", 1);
            }
        }
        catch (Exception e) {
            Main.showException(e);
        }
    }

}

