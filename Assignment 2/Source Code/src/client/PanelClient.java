package client;

/* COMP90015 Distributed Systems (Semester 1, 2020)
 * Harfiyanto Santoso 
 * harfiyantos@student.unimelb.edu.au
 * (772503)
 * 
 * Assignment 2
 * 
 * [PanelClient.java]
 * 
 * Serves a login panel for the user to input their 
 * names and desired server location.  It also responsible 
 * for requesting connection to the server and processing 
 * bufferedData into arraylist for PaintCanvas.  
 */

import java.io.*;
import java.net.*;
import java.awt.*;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.UIManager;

import data.*;

public class PanelClient extends Thread {
    private static String userName;
    InetAddress address=null;
    int ID;
    private Socket socket;
    private final int PORT = 3005;
    private BufferedReader in; 
    public BufferedWriter out; 

    private PaintCanvas canvas = PaintCanvas.getInstance();

    public int id = 0;
    private Point pencilPoint = null; 
    private String dataBuffer;
    private String[] command; 
    private String[] data; 
    
    private Color pencilColor;
	private Color eraserColor;
	private Stroke pencilStroke;
	private Stroke eraserStroke;
	private String text;
	private String fonttype;
	private int bolder;
	private int fontSize;
	private int type;
	
	final static int PENCIL = 0;
	final static int ERASER = 1;
	final static int LINE = 2;
	final static int SQUARE = 3;
	final static int RECTANGLE = 4;
	final static int CIRCLE = 5;
	final static int OVAL = 6;
	final static int TEXT = 7;
	
    private static PanelClient panelClient = new PanelClient();

    public static PanelClient getInstance() {
        return panelClient;
    }
    public static void main(String[] args) {
//        PanelClient client=PanelClient.getInstance();   
    }
    private void initialization() {
        
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        String serverIP;
        String portString;
        JFrame frame = new JFrame();
        int inputPort = 0;
        boolean valid = false;
        
        do {
            try {
            	JTextField nameText= new JTextField();
                JTextField IPText= new JTextField();
                JTextField portText= new JTextField();
                nameText.setText("Guest");
                IPText.setText("localhost");
                portText.setText("3005");
                Object complexMsg[] = {new JLabel("Username"),nameText,new JLabel("Server Address"),IPText
                        ,new JLabel("Port:"),portText};
                    JOptionPane optionPane = new JOptionPane();
                    optionPane.setMessage(complexMsg);
                    optionPane.setMessageType(JOptionPane.INFORMATION_MESSAGE);
                    optionPane.setOptionType(optionPane.OK_CANCEL_OPTION);
                    JDialog dialog = optionPane.createDialog(null, "Login");
                    dialog.setVisible(true);
                    int value = ((Integer)optionPane.getValue()).intValue();
                    if (value == JOptionPane.OK_OPTION) {
                        setUserName((String)(nameText.getText()));

                        serverIP=(String)IPText.getText();
                        if (serverIP.length()==0){
                            serverIP=InetAddress.getLocalHost().getHostAddress();
                        }
                        address = InetAddress.getByName(serverIP);
                        
                        if (((String)(portText.getText())).length()==0){
                            portString="3005";
                        }else{
                            portString=(String)portText.getText();
                        }
                        inputPort = Integer.parseInt(portString);
                        socket = new Socket(address,inputPort);
                        valid = true;
                    } else {
                        System.exit(0);
                    }
            } catch (UnknownHostException e1) {
                JOptionPane.showMessageDialog(null,
                        "UnknownHostException", "Error",
                        JOptionPane.ERROR_MESSAGE);
            } catch (ConnectException e) {
            	e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Connection cannot be established. Server might not be online or at different address/port.",
                        "Error", JOptionPane.ERROR_MESSAGE);
                System.exit(0);
            } catch (Exception e) {
            	e.printStackTrace();
                JOptionPane.showMessageDialog(null, "UnkownException",
                        "Error", JOptionPane.ERROR_MESSAGE);
                System.exit(0);
            }      
        } while (!valid);
        
//        System.out.println(inputPort);
    }

    public PanelClient() {
            this.initialization();
      
            try {
                
                in = new BufferedReader(new InputStreamReader(
                        socket.getInputStream()));
                out = new BufferedWriter(new OutputStreamWriter(
                        socket.getOutputStream()));
                out.write(getUserName());
                out.newLine();
                out.flush();
               
                id = Integer.parseInt(in.readLine());
               
                ClientWindow window = ClientWindow.getInstance();
                window.setVisible(true);

                this.start(); 
            } catch (SocketException e){
                JOptionPane.showMessageDialog(null,
                        "You fail to join in this whiteboard", "warning",
                        JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, "IO Error has been detected.");
                System.exit(1);
            } catch(Exception e){
            	JOptionPane.showMessageDialog(null, "Unidentified Error has been detected.");
                System.exit(1);
            }
    }

    public void run() {
        try {
            readMsg();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Server has stopped!");
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
            	JOptionPane.showMessageDialog(null, "IO Error has been detected.");
                System.exit(1);
            } catch(Exception e){
            	JOptionPane.showMessageDialog(null, "Unidentified Error has been detected.");
                System.exit(1);
            }
        }
    }


    public void readMsg() throws IOException {

        while (true) {

            dataBuffer = in.readLine(); 
//            System.out.println(dataBuffer);
            if (dataBuffer == null){
                JOptionPane.showMessageDialog(null, "Something went wrong.", "Warning",
                        JOptionPane.ERROR_MESSAGE);
                out.close();
                System.exit(0); 
            } else if (dataBuffer.equals("kick")) {
            	JOptionPane.showMessageDialog(null,
                        "You have been kicked out by the manager.", "Exiting...",
                        JOptionPane.ERROR_MESSAGE);
                out.close();
                System.exit(0); 
            } else if (dataBuffer.equals("quit")) {
            	JOptionPane.showMessageDialog(null,
                        "You have quit the server.", "Exiting...", JOptionPane.ERROR_MESSAGE);
                out.close();
                System.exit(0); 
            }
            command = dataBuffer.split("\\ ");
            data = dataBuffer.split("\\.");
//            System.out.println("buffer received: " + dataBuffer);
//            System.out.println("command received: " + command[0]);
//            System.out.println("msg received: " + command[1]);
//            System.out.println("data received: " + data);
            if (data.length > 4) { 
            	setupMessage(data);
            } else if (data.length == 2){
            	ClientWindow window = ClientWindow.getInstance();
            	if (data[0].equals("newuser")) {
            		System.out.println("New user joined!" + data[1]);
            		window.addUser(data[1]);
            	} else if (data[0].equals("removeuser")) {
            		window.removeUser(data[0]);
            	} else {
            		System.out.println("Chat received from " + data[0] + " message: " + data[1]);
            		window.updateChatBox(data[0],data[1]);
            	}
            } else {
            	startDrawing(); 
            }
        }
    }
 
    public void setupMessage(String[] d) {
		int id, type, x, y;
		Point startPoint = null;

		id = Integer.parseInt(d[0]); 
		if (id == this.id)
			return; 
		type = Integer.parseInt(d[1]); 
		pencilStroke = new BasicStroke(Float.valueOf(d[2]));
		eraserStroke = pencilStroke;
		pencilColor = getColor(d[3]);
		eraserColor = Color.WHITE;
		x = Integer.parseInt(d[4]); 
		y = Integer.parseInt(d[5]);
		startPoint = new Point(x, y); 
		pencilPoint = startPoint; 
		text = d[6];
		fontSize = Integer.parseInt(d[7]);
		canvas.getArr().addData(
				new DrawData(id, type,new Point(x, y), new Point(x,
						y), pencilColor, pencilStroke, text, fontSize));
	}
    
	
	public Color getColor(String c) {
		switch (c) {
		case "BLACK":
			return Color.black;
		case "WHITE":
			return Color.white;
		}
		return Color.black;
	}
	
	public String getStringFromColor (Color c) {
		if (c == Color.BLACK) {
			return "BLACK";
		} else if (c == Color.WHITE) {
			return "WHITE";
		} else {
			return "BLACK";
		}
	}

	public void startDrawing() {
		int id = 0, scaleX = 0, scaleY = 0;
		Point endPoint = null;
		// End of motion
		if (dataBuffer.equals("n.n.n")) {
			return;
		}
			
		id = Integer.parseInt(data[0]);
		// Avoid double drawing
		if (id == this.id) {
			return;
		}
			
		scaleX = Integer.parseInt(data[1]);
		scaleY = Integer.parseInt(data[2]);
		endPoint = new Point(scaleX, scaleY);

		int i;
		for (i = canvas.getArr().array.size() - 1; id != 
				canvas.getArr().array.get(i).getId(); i--)
			;
		if (canvas.getArr().array.get(i).getType() == PENCIL) 
		{
			canvas.getArr().addData(
					new DrawData(id, type,new Point(pencilPoint),
							new Point(endPoint), pencilColor,pencilStroke,text,
							fontSize)); 
			pencilPoint = endPoint;

		} else if(canvas.getArr().array.get(i).getType() == ERASER){
			canvas.getArr().addData(
					new DrawData(id, type,new Point(pencilPoint),
							new Point(endPoint), eraserColor ,eraserStroke,text, fontSize));  
			pencilPoint = endPoint;
		} else {
			canvas.getArr().array.get(i).setEndPoint(new Point(endPoint));
		}
		canvas.repaint(); 
	}


    public Socket getSocket() {
        return this.socket;
    }
	public static String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}

}
class NameNullException extends Exception {

}