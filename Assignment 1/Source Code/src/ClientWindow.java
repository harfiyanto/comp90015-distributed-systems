/* COMP90015 Distributed Systems (Semester 1, 2020)
 * Harfiyanto Santoso 
 * harfiyantos@student.unimelb.edu.au
 * (772503)
 * 
 * [ClientWindow.java]
 * 
 * Class to handle the GUI for the client and responsible for processing  
 * and sending commands (query, add, remove) to the corresponding thread/
 * worker (Dictionary.java)
 * 
 */

import java.awt.EventQueue;

import javax.swing.JFrame;
import java.awt.GridBagLayout;
import javax.swing.JTextField;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.SwingConstants;
import javax.swing.JOptionPane;

import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.StringTokenizer;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
//import java.awt.event.ActionEvent;

import javax.swing.JTextArea;
import java.awt.Font;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import org.apache.commons.cli.*;
import javax.swing.JLabel;

public class ClientWindow {

	private JFrame frame;
	private JTextField inputField;
	private JTextArea resultField;
	private static int PORT = 3012;
	private static String ADDRESS;
	private JTextArea defField;
	private boolean inputHint = true;
	private boolean defHint = true;
	private JLabel clientLabel;
	private JLabel lblNewLabel;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		// Parsing the command line parameters
		Options options = new Options();

        Option input = new Option("p", "port", true, "server port");
        input.setRequired(true);
        options.addOption(input);

        Option output = new Option("a", "address", true, "server address");
        output.setRequired(true);
        options.addOption(output);

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd = null;

        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("utility-name", options);

            System.exit(1);
        }

        PORT = Integer.parseInt(cmd.getOptionValue("port"));
        ADDRESS = cmd.getOptionValue("address");
		
		EventQueue.invokeLater(new Runnable() {
			// Run the window and open a socket to request connection.
			public void run() {
				try {
					Socket s = new Socket(ADDRESS, PORT);
					ClientWindow window = new ClientWindow(s);
					window.frame.setVisible(true);
				} catch (SocketException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public ClientWindow(Socket s) {
		initialize(s);
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize(Socket s) {
		
		frame = new JFrame("Client");
		frame.setBounds(100, 100, 390, 384);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{144, 123, 125, 0};
		gridBagLayout.rowHeights = new int[]{30, 59, 97, 21, 0, 147, 0};
		gridBagLayout.columnWeights = new double[]{1.0, 0.0, 0.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 1.0, 1.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		frame.getContentPane().setLayout(gridBagLayout);
		
		JButton addButton = new JButton("Add");
		addButton.setHorizontalAlignment(SwingConstants.RIGHT);
		addButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (defHint) {
					defField.setText("");
					defHint = false;
				}
				addWord(s);
			};
		});
		
		inputField = new JTextField();
		inputField.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (inputHint) {
					inputField.setText("");
					inputHint = false;
				}
			}
		});
		
		clientLabel = new JLabel("Client connected to Server at Port:" + PORT);
		clientLabel.setFont(new Font("Tahoma", Font.BOLD, 12));
		GridBagConstraints gbc_clientLabel = new GridBagConstraints();
		gbc_clientLabel.anchor = GridBagConstraints.WEST;
		gbc_clientLabel.gridwidth = 3;
		gbc_clientLabel.insets = new Insets(0, 0, 5, 0);
		gbc_clientLabel.gridx = 0;
		gbc_clientLabel.gridy = 0;
		frame.getContentPane().add(clientLabel, gbc_clientLabel);
		inputField.setToolTipText("Word (type a word to query, add or remove)");
		inputField.setText("Word (type a word to query, add or remove)");
		GridBagConstraints gbc_inputField = new GridBagConstraints();
		gbc_inputField.fill = GridBagConstraints.HORIZONTAL;
		gbc_inputField.insets = new Insets(0, 0, 5, 0);
		gbc_inputField.gridwidth = 3;
		gbc_inputField.gridx = 0;
		gbc_inputField.gridy = 1;
		frame.getContentPane().add(inputField, gbc_inputField);
		inputField.setColumns(10);
		
		defField = new JTextArea();
		defField.setToolTipText("Definition (use: separate definitions by ';')");
		defField.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				if (defHint) {
					defField.setText("");
					defHint = false;
				}
			}
		});
		defField.setFont(new Font("Tahoma", Font.PLAIN, 10));
		defField.setText("Definition (use: separate definitions by ';')");
		GridBagConstraints gbc_defField = new GridBagConstraints();
		gbc_defField.gridwidth = 3;
		gbc_defField.insets = new Insets(0, 0, 5, 0);
		gbc_defField.fill = GridBagConstraints.BOTH;
		gbc_defField.gridx = 0;
		gbc_defField.gridy = 2;
		frame.getContentPane().add(defField, gbc_defField);
		GridBagConstraints gbc_addButton = new GridBagConstraints();
		gbc_addButton.insets = new Insets(0, 0, 5, 5);
		gbc_addButton.gridx = 0;
		gbc_addButton.gridy = 3;
		frame.getContentPane().add(addButton, gbc_addButton);
		
		JButton queryButton = new JButton("Query");
		queryButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.print("searching...");
				queryWord(s);
			}
		});
		GridBagConstraints gbc_queryButton = new GridBagConstraints();
		gbc_queryButton.insets = new Insets(0, 0, 5, 5);
		gbc_queryButton.gridx = 1;
		gbc_queryButton.gridy = 3;
		frame.getContentPane().add(queryButton, gbc_queryButton);
		
		JButton removeButton = new JButton("Remove");
		removeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.print("removing...");
				removeWord(s);
			}
		});
		GridBagConstraints gbc_removeButton = new GridBagConstraints();
		gbc_removeButton.insets = new Insets(0, 0, 5, 0);
		gbc_removeButton.gridx = 2;
		gbc_removeButton.gridy = 3;
		frame.getContentPane().add(removeButton, gbc_removeButton);
		
		lblNewLabel = new JLabel("Response");
		lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 10));
		GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
		gbc_lblNewLabel.anchor = GridBagConstraints.WEST;
		gbc_lblNewLabel.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel.gridx = 0;
		gbc_lblNewLabel.gridy = 4;
		frame.getContentPane().add(lblNewLabel, gbc_lblNewLabel);
		
		resultField = new JTextArea();
		resultField.setEditable(false);
		GridBagConstraints gbc_resultField = new GridBagConstraints();
		gbc_resultField.fill = GridBagConstraints.BOTH;
		gbc_resultField.gridwidth = 3;
		gbc_resultField.gridx = 0;
		gbc_resultField.gridy = 5;
		frame.getContentPane().add(resultField, gbc_resultField);
	}
	
	// Method to request the server to query the meaning of a word
	private void queryWord(Socket s) {
		if (inputField.getText().equals("")) {
			// Warning, text field is empty. Please input a word to search
		} else {
			String msg = "Query " + inputField.getText() + "\n";
			resultField.setEditable(true);
			resultField.setText(""); // Reset the result field
			if (sendMessage(msg, s)) {
                BufferedReader input = null;
                try {
                    input = new BufferedReader(new InputStreamReader(s.getInputStream(),
                            "UTF-8"));
                    String reply = input.readLine();
                    if (reply.equals("FailWordNotFound")) {
                    	resultField.setText("The word does not exist");
                    	JOptionPane.showMessageDialog(null, "The word does not exist in the dictionary. "
                    			+ "Please try again.", "Error", JOptionPane.WARNING_MESSAGE);
                    } else {
                    	StringTokenizer answer = new StringTokenizer(reply, ";");
                        int i = 1;
                        while (answer.hasMoreTokens()) {
                        	resultField.setText("\n" + resultField.getText() + "Definition " 
                        			+ i + ": " + answer.nextToken() + "\n");
                        	i++;
                        }
                    }
                }
                catch (IOException e) {
                	JOptionPane.showMessageDialog(null, "Error communicating with the Server. "
                			+ "Please try again.", "Error", JOptionPane.PLAIN_MESSAGE);
                }
            }
			resultField.setEditable(false);
		}
	}
	
	// Method to request the server to add a word
	private void addWord(Socket s) {
		if (inputField.getText().equals("") || defField.getText().equals("")) {
			JOptionPane.showMessageDialog(null, "Both word and definition fields cannot be empty! "
        			+ "Please try again.", "Error", JOptionPane.WARNING_MESSAGE);
		} else {
			String msg = "Add " + inputField.getText() +  " " + defField.getText() + "\n";
			resultField.setEditable(true);
			resultField.setText(""); // Reset the result field
			if (sendMessage(msg, s)) {
                BufferedReader input = null;
                try {
                    input = new BufferedReader(new InputStreamReader(s.getInputStream(),
                            "UTF-8"));
                    String reply = input.readLine();
                    if (reply.equals("DuplicateFound")) {
                    	resultField.setText("The word already exists");
                    	JOptionPane.showMessageDialog(null, "The word already exists in the dictionary."
                    			+ "Please try again.", "Error", JOptionPane.WARNING_MESSAGE);
                    } else if (reply.equals("WordAdded")){
                    	resultField.setText("Word has been added.");
                    	JOptionPane.showMessageDialog(null, "The word has been added into the dictionary!"
                    			, "Success", JOptionPane.PLAIN_MESSAGE);
                    } else {
                    	resultField.setText("Reply not recognized.");
                    	JOptionPane.showMessageDialog(null, "Something wrong. Reply not recognized!"
                    			, "Error", JOptionPane.WARNING_MESSAGE);
                    }
                } catch (IOException e) {
                	resultField.setText("Connection error.");
                	JOptionPane.showMessageDialog(null, "Error communicating with the Server. "
                			+ "Please try again.", "Error", JOptionPane.WARNING_MESSAGE);
                }
            }
			resultField.setEditable(false);
		}
	}
	
	// Method to request the server to remove a word from the dictionary
	private void removeWord(Socket s) {
		if (inputField.getText().equals("")) {
			JOptionPane.showMessageDialog(null, "Word field cannot be empty! "
        			+ "Please try again.", "Error", JOptionPane.WARNING_MESSAGE);
		} else {
			String msg = "Remove " + inputField.getText() +  "\n";
			resultField.setEditable(true);
			resultField.setText(""); // Reset the result field
			if (sendMessage(msg, s)) {
                BufferedReader input = null;
                try {
                    input = new BufferedReader(new InputStreamReader(s.getInputStream(),"UTF-8"));
                    String reply = input.readLine();
                    if (reply.equals("FailWordNotFound")) {
                    	resultField.setText("The word does not exist.");
                    	JOptionPane.showMessageDialog(null, "The word does not exist in the dictionary. "
                    			+ "Please try again.", "Error", JOptionPane.WARNING_MESSAGE);
                    } else if (reply.equals("WordRemoved")){
                    	resultField.setText("Word has been removed.");
                    	JOptionPane.showMessageDialog(null, "The word has been removed from dictionary. "
                    			, "Success", JOptionPane.PLAIN_MESSAGE);
                    } else {
                    	resultField.setText("Reply not recognized.");
                    	JOptionPane.showMessageDialog(null, "Something wrong. Reply not recognized!"
                    			, "Error", JOptionPane.WARNING_MESSAGE);
                    }
                } catch (IOException e) {
                	JOptionPane.showMessageDialog(null, "Error communicating with the Server. "
                			+ "Please try again.", "Error", JOptionPane.WARNING_MESSAGE);
                }
            }
			resultField.setEditable(false);
		}
	}
	
	// Method to send a request through the socket
	private boolean sendMessage(String msg, Socket s) {
		try {
            BufferedWriter output = new BufferedWriter(new OutputStreamWriter(s.getOutputStream(),
                    "UTF-8"));
            output.write(msg);
            output.flush();
            return true;
        } catch (UnknownHostException e) {
        	e.printStackTrace();
            return false;
        } catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
}
