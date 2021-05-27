/* COMP90015 Distributed Systems (Semester 1, 2020)
 * Harfiyanto Santoso 
 * harfiyantos@student.unimelb.edu.au
 * (772503)
 * 
 * Assignment 2
 * 
 * [.java]
 * 
 * This class sets up the GUI and relay any user interaction (Toolbar, Chat) to other classes. 
 */

package client;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTextArea;
import javax.swing.JToggleButton;
import javax.swing.border.EmptyBorder;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;

import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.awt.event.MouseAdapter;
import javax.swing.JTextField;
import javax.swing.JList;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import java.awt.Panel;
import javax.swing.JSpinner;

public class ClientWindow extends JFrame implements Runnable {

	private JPanel contentPane;
	
	final static int PENCIL = 0;
	final static int ERASER = 1;
	final static int LINE = 2;
	final static int SQUARE = 3;
	final static int RECTANGLE = 4;
	final static int CIRCLE = 5;
	final static int OVAL = 6;
	final static int TEXT = 7;
	
//    private JFrame frame;
    private PaintCanvas drawPanel;
    JLabel thickValLbl = new JLabel();
    JTextArea chatField = new JTextArea();
    JList userList = new JList(listUser);
    private static DefaultListModel listUser = new DefaultListModel();
    
    public static ClientWindow client = new ClientWindow();
    private JTextField msgField;
    private JTextField txtText;
    private JSpinner spinner;
    
    public static ClientWindow getInstance() {
        return client;
    }
    public void run() {
        this.setVisible(true);
    }

	/**
	 * Create the frame.
	 */
	public ClientWindow() {
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
//				JDialog.setDefaultLookAndFeelDecorated(true);
                int response = JOptionPane.showConfirmDialog(null,
                        "Are you sure you want to quit?", "Exiting...",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE);
                if (response == JOptionPane.YES_OPTION) {
                	try {
                		PanelClient.getInstance().out.write("quit");
                		PanelClient.getInstance().out.newLine();
                		PanelClient.getInstance().out.flush();
                    } catch (IOException f) {
                        // Unknown Error
                    }
                    System.exit(1);
                }
			}
		});
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 1041, 478);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));
		
		JPanel toolPanel = new JPanel();
		contentPane.add(toolPanel, BorderLayout.NORTH);
		
		JButton  pencilBtn = new JButton ("Pencil");
		
		pencilBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				drawPanel.setType(PENCIL);
			}
		});
		toolPanel.add(pencilBtn);
		
		JButton eraserBtn = new JButton("Eraser");
		eraserBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				drawPanel.setType(ERASER);
			}
		});
		toolPanel.add(eraserBtn);
		
		JButton lineBtn = new JButton("Line");
		lineBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				drawPanel.setType(LINE);
			}
		});
		toolPanel.add(lineBtn);
		
		JButton squareBtn = new JButton("Square");
		squareBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				drawPanel.setType(RECTANGLE);
			}
		});
		toolPanel.add(squareBtn);
		
		JButton circleBtn = new JButton("Circle");
		circleBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				drawPanel.setType(OVAL);
			}
		});
		
		JButton rectBtn = new JButton("Rectangle");
		rectBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				drawPanel.setType(RECTANGLE);
			}
		});
		toolPanel.add(rectBtn);
		toolPanel.add(circleBtn);
		
		JButton btnNewButton_5 = new JButton("Oval");
		btnNewButton_5.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				drawPanel.setType(OVAL);
			}
		});
		toolPanel.add(btnNewButton_5);
		
		JPanel thicknessPanel = new JPanel();
		thicknessPanel.setPreferredSize(new Dimension(200,30));
		toolPanel.add(thicknessPanel);
		thicknessPanel.setLayout(new BoxLayout(thicknessPanel, BoxLayout.X_AXIS));
		
		JLabel thickLbl = new JLabel("Thickness: ");
		thicknessPanel.add(thickLbl);
		thickLbl.setFont(new Font("Tahoma", Font.PLAIN, 12));
		
		JSlider slider = new JSlider();
		slider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				thickValLbl.setText(String.valueOf(slider.getValue()));
				drawPanel.setPencilStroke(new BasicStroke((float) slider.getValue()));
				drawPanel.setPencilSize(slider.getValue());
				drawPanel.setEraserStroke(new BasicStroke((float) slider.getValue()));
				drawPanel.setEraserSize(slider.getValue());
			}
		});
		thicknessPanel.add(slider);
		thicknessPanel.add(thickValLbl);
		thickValLbl.setText("50");
		
		JButton textBtn = new JButton("Text");
		textBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				drawPanel.setType(TEXT);
				drawPanel.setText(txtText.getText());
				drawPanel.setFontSize((Integer) spinner.getValue());
			}
		});
		toolPanel.add(textBtn);
		
		txtText = new JTextField();
		txtText.setText("Text");
		toolPanel.add(txtText);
		txtText.setColumns(10);
		
		spinner = new JSpinner();
		toolPanel.add(spinner);
		
		
		
		JPanel chatPanel = new JPanel();
		contentPane.add(chatPanel, BorderLayout.WEST);
		chatPanel.setLayout(new BorderLayout(0, 0));
		
		chatField.setText("");
		chatField.setColumns(20);
		chatPanel.add(chatField);
		chatField.setLineWrap(true);
		chatField.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(chatField);
        chatPanel.add(scrollPane, BorderLayout.CENTER);
        
        
        scrollPane.setRowHeaderView(userList);
        
		JPanel panel = new JPanel();
		chatPanel.add(panel, BorderLayout.SOUTH);
		
		msgField = new JTextField();
		panel.add(msgField);
		msgField.setColumns(10);
		
		JButton sendButton = new JButton("Send");
		sendButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String msg = PanelClient.getUserName() + "." +
						msgField.getText();
				try {
					PanelClient.getInstance().out.write(msg);
					PanelClient.getInstance().out.newLine();
					PanelClient.getInstance().out.flush();
                } catch (IOException f) {
                    // Unknown Error
                }
			}
		});
		panel.add(sendButton);
		
		Panel panel_1 = new Panel();
		panel.add(panel_1);
		
		JLabel lblNewLabel = new JLabel("Chat Box");
		chatPanel.add(lblNewLabel, BorderLayout.NORTH);
		
		JPanel panel_2 = new JPanel();
		contentPane.add(panel_2, BorderLayout.CENTER);
		drawPanel = PaintCanvas.getInstance();
		panel_2.add(drawPanel);
		
		panel_2.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(
                172, 168, 153)));
		panel_2.setLayout(new BorderLayout(0, 0));
        drawPanel.setBounds(new Rectangle(2, 2, 600, 600));
	}
	
	public void addUser(String user) {
		this.listUser.addElement(user);
	}
	
	public void removeUser(String user) {
		this.listUser.removeElement(user);
	}
	
	public void updateChatBox(String user, String msg) {
		this.chatField.append(user + ": " + msg + "\n");
	}
	
}
