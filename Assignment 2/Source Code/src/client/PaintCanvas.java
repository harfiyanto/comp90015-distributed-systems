package client;

/* COMP90015 Distributed Systems (Semester 1, 2020)
 * Harfiyanto Santoso 
 * harfiyantos@student.unimelb.edu.au
 * (772503)
 * 
 * Assignment 2
 * 
 * [PaintCanvas.java]
 * 
 *  This class renders the image on the canvas locally as 
 *  well as detecting user interaction on the canvas 
 *  (drawing) and inform other clients through the socket.
 */

import java.awt.BasicStroke;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.Point;
import java.awt.Stroke;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.IOException;
import java.util.Iterator;

import javax.swing.JLabel;

import data.*;

public class PaintCanvas extends Canvas implements MouseListener,
		MouseMotionListener {

	private Image screenBuffer;
	private Graphics2D screenGc;

	private int id; 
	private int type; 
	private Point startPoint; 
	private Point endPoint; 

	private int startX;
	private int startY;
	private int oldX;
	private int oldY;
	private int currX;
	private int currY;
	private int endX;
	private int endY;

	private DataArray arr = new DataArray();
	private DrawData temp; 
	private String dataBuffer;
	private static PaintCanvas paintCanvas = new PaintCanvas();

	private int erasersize;
	private int pencilsize;
	private Color pencilColor = Color.BLACK;
	private Color eraserColor = Color.WHITE;
	private Stroke pencilStroke = new BasicStroke(1.0f);
	private Stroke eraserStroke =  new BasicStroke(5.0f);
	private int fontSize;
	private String text;
	
	
	final static int PENCIL = 0;
	final static int ERASER = 1;
	final static int LINE = 2;
	final static int SQUARE = 3;
	final static int RECTANGLE = 4;
	final static int CIRCLE = 5;
	final static int OVAL = 6;
	final static int TEXT = 7;
	

	public static PaintCanvas getInstance() {
		return paintCanvas;
	}

	public PaintCanvas() {
		initialise();
	}


	public void initialise() {
		setBackground(Color.WHITE);
		setForeground(Color.BLACK);
		startPoint = new Point(0, 0);
		endPoint = new Point(0, 0);
		this.setBackground(Color.white);
		this.addMouseListener(this);
		this.addMouseMotionListener(this);
		this.setVisible(true);
	}

	public void setPencilColor(Color color) {
		this.pencilColor = color;
	}

	public void setPencilSize(int size) {
		this.pencilsize = size;
	}

	public void setEraserColor(Color color) {
		this.eraserColor = color;
	}
	
	public void setEraserSize(int size) {
		this.pencilsize = size;
	}
	
	public void setFontSize(int size) {
		this.fontSize = size;
	}
	public void setPencilStroke(Stroke stroke) {
		pencilStroke = stroke;
	}

	public void setEraserStroke(Stroke stroke) {
		eraserStroke = stroke;
	}
	
	public void setText(String text) {
		this.text = text;
	}

	public void mousePressed(MouseEvent e) {
		startX = e.getX();
		startY = e.getY();
		endX = e.getX();
		endY = e.getY();

		id = PanelClient.getInstance().id;
		
		arr.addData(new DrawData(id, type,new Point(startX, startY), new Point(endX,endY), 
				pencilColor, pencilStroke, text, fontSize));
		
		if (type==ERASER) {
			dataBuffer = new String(id + "." + type + "." + pencilsize + "."
					+ getStringFromColor(eraserColor) + "." + startX + "." + 
					startY + "." + text + "." + fontSize);
		} else {
			dataBuffer = new String(id + "." + type + "." + pencilsize + "."
					+ getStringFromColor(pencilColor) + "." + startX + "." + 
					startY + "." + text + "." + fontSize);
		}
		
		sendGraphicMessage();

	}
	
	public void mouseDragged(MouseEvent e) {
		if (type == PENCIL || type == ERASER) {
			currX = e.getX();
			currY = e.getY();
			arr.addData(new DrawData(id, type,new Point(startX, startY), new Point(currX,
					currY), pencilColor, pencilStroke, text,
					fontSize));
			startX = e.getX();
			startY = e.getY();
		} else {
			int i;
			for (i = arr.array.size() - 1; this.id != arr.array.get(i).getId(); i--) {
				arr.array.get(i).setEndPoint(new Point(e.getX(), e.getY()));
			}
		}

		repaint();
		dataBuffer = new String(this.id + "." + e.getX() + "." + e.getY());
		sendGraphicMessage();

	}
	
	public void mouseReleased(MouseEvent e) {

		if (type != PENCIL && type != ERASER) {
			int i;
			for (i = arr.array.size() - 1; this.id != arr.array.get(i).getId(); i--)
				;
			arr.array.get(i).setEndPoint(e.getPoint());
		}
		
		dataBuffer = new String("n.n.n");
		sendGraphicMessage();
	}

	
	public void sendGraphicMessage() {
		try {
			PanelClient.getInstance().out.write(dataBuffer);
			PanelClient.getInstance().out.newLine();
			PanelClient.getInstance().out.flush();
		} catch (IOException exp) {
			exp.printStackTrace();
		}
	}

	public void update(Graphics g) {
		screenBuffer = (BufferedImage) createImage(getWidth(), getHeight());
		screenGc = (Graphics2D) screenBuffer.getGraphics();
		paint(screenGc);
		screenGc.dispose();
		g.drawImage(screenBuffer, 0, 0, null);
		screenBuffer.flush();
	}
	
	public void clear() {
	    screenGc.setPaint(Color.white);
	    screenGc.fillRect(0, 0, getSize().width, getSize().height);
	    screenGc.setPaint(Color.black);
	    repaint();
	  }

	public void paint(Graphics g) {
		synchronized (this.arr) {
			Iterator<DrawData> i = arr.iterator();
			while (i.hasNext()) {
				temp = i.next();

				startPoint = temp.getStartPoint();
				endPoint = temp.getEndPoint();

				switch (temp.getType()) {
					case PENCIL:
						pencil(startPoint, endPoint, temp.getPencilColor(), temp.getStroke(), (Graphics2D) g);
						break;
					case ERASER:
						eraser(startPoint, endPoint, Color.WHITE, temp.getStroke(), (Graphics2D) g);
						break;
					case LINE:
						line(startPoint, endPoint, temp.getPencilColor(), temp.getStroke(), (Graphics2D) g);
						break;
					case SQUARE:
						square(startPoint, endPoint, temp.getPencilColor(), temp.getStroke(), (Graphics2D) g);
						break;
					case RECTANGLE:
						rectangle(startPoint, endPoint, temp.getPencilColor(), temp.getStroke(), (Graphics2D) g);
						break;
					case CIRCLE:
						circle(startPoint, endPoint, temp.getPencilColor(), temp.getStroke(), (Graphics2D) g);
						break;
					case OVAL:
						oval(startPoint, endPoint, temp.getPencilColor(), temp.getStroke(), (Graphics2D) g);
						break;
					case TEXT:
						typeText(startPoint, temp.getText(), this.fontSize, temp.getPencilColor(), temp.getStroke(), (Graphics2D) g);
						break;
				}
			}
		}
	}

	public DataArray getArr() {
		return arr;
	}
	public void setType(int type) {
		this.type = type;
	}
	

	public void mouseClicked(MouseEvent e) {
		
	}

	public void mouseEntered(MouseEvent e) {
		
	}

	public void mouseExited(MouseEvent e) {
		
	}

	public void mouseMoved(MouseEvent e) {

	}
	
	private void pencil(Point start, Point end,
			Color pencilColor, Stroke stroke, Graphics2D g) {
		g.setColor(pencilColor);
		g.setStroke(stroke);
		g.drawLine(start.x, start.y, end.x, end.y);
	}
	
	private void eraser(Point start, Point end,
			Color eraserColor, Stroke stroke, Graphics2D g) {
		g.setColor(Color.WHITE);
		g.setStroke(stroke);
		g.drawLine(start.x, start.y, end.x, end.y);
	}
	
	private void square (Point start, Point end,
			Color pencilColor, Stroke stroke, Graphics2D g) {
		g.setColor(pencilColor);
		g.setStroke(stroke);
		int width, height, r;

		width = Math.abs(end.x - start.x);
		height = Math.abs(end.y - start.y);
		
		if (width>height) {
			if (start.x > end.x) {
				r = end.x;
			} else {
				r = start.x;
			}
			g.drawRect(r, r, height, height);
		} else {
			if (start.y > end.y) {
				r = end.y;
			} else {
				r = start.y;
			}
			g.drawRect(r, r, height, height);
		}
	}
	
	private void rectangle (Point startPoint, Point endPoint,
			Color pencilColor, Stroke stroke, Graphics2D g) {
		g.setColor(pencilColor);
		g.setStroke(stroke);
		int w, h, width, height;
		w = endPoint.x - startPoint.x;
		h = endPoint.y - startPoint.y;
		width = Math.abs(w);
		height = Math.abs(h);
		int x, y;
		if (startPoint.x > endPoint.x) {
			x = endPoint.x;
		} else {
			x = startPoint.x;
		}
		if (startPoint.y > endPoint.y) {
			y = endPoint.y;
		} else {
			y = startPoint.y;
		}
		g.drawRect(x, y, width, height);
	}
	
	private void circle(Point start, Point end,
			Color pencilColor, Stroke stroke, Graphics2D g) {
		g.setColor(pencilColor);
		g.setStroke(stroke);
		int width, height, r;

		width = Math.abs(end.x - start.x);
		height = Math.abs(end.y - start.y);
		
		if (width>height) {
			if (start.x > end.x) {
				r = end.x;
			} else {
				r = start.x;
			}
			g.drawOval(r, r, width, width);
		} else {
			if (start.y > end.y) {
				r = end.y;
			} else {
				r = start.y;
			}
			g.drawOval(r, r, height, height);
		}
	}
	
	private void oval(Point start, Point end,
			Color pencilColor, Stroke stroke, Graphics2D g) {
		g.setColor(pencilColor);
		g.setStroke(stroke);
		int x, y, width, height;

		width = Math.abs(end.x - start.x);
		height = Math.abs(end.y - start.y);
		if (start.x > end.x) {
			x = end.x;
		} else {
			x = start.x;
		}
		if (start.y > end.y) {
			y = end.y;
		} else {
			y = start.y;
		}
		g.drawOval(x, y, width, height);
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
	
	private void line (Point startPoint, Point endPoint,
			Color pencilColor, Stroke stroke, Graphics2D g) {
		g.setColor(pencilColor);
		g.setStroke(stroke);
		g.drawLine(startPoint.x, startPoint.y, endPoint.x, endPoint.y);
	}

	private void typeText (Point start, String string, int size, 
			Color pencilColor, Stroke stroke, Graphics2D g) {
		g.setFont(new Font("TimesRoman", Font.PLAIN, size)); 
		g.setColor(pencilColor);
		g.setStroke(stroke);
		g.drawString(string, start.x, start.y);
	}
}
