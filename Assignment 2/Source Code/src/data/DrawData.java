package data;

/* COMP90015 Distributed Systems (Semester 1, 2020)
 * Harfiyanto Santoso 
 * harfiyantos@student.unimelb.edu.au
 * (772503)
 * 
 * Assignment 2
 * 
 * [DrawData.java]
 * 
 * Contains information that facilitates drawing 
 * (e.g. pencil color, coordinates, text, etc.)
 */

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Point;
import java.awt.Stroke;

public class DrawData {
	private int id;
	private int type;
	private Color pencilColor;
	private Color eraserColor;
	private Stroke stroke;
	private String text;
	private int fontsize;
	private Point startPoint;
	private Point endPoint;
	
	public DrawData(){
		type=0;
		pencilColor=Color.BLACK;
		eraserColor=Color.WHITE;
		stroke = new BasicStroke(1.0f);
	    fontsize=12;
		startPoint=new Point(0,0);
		endPoint=new Point(0,0);
	
	}
	public DrawData(int id,int type,Point startPoint,Point endPoint,Color pencilColor,Stroke stroke,String text,int fontsize){
		this.id=id;
		this.type=type;
		this.pencilColor=pencilColor;
		this.eraserColor=Color.WHITE;
		this.text=text;
		this.stroke=stroke;
		this.fontsize=fontsize;
		this.startPoint=startPoint;
		this.endPoint=endPoint;
	
	}
	
	public int getId() {
		return id;
	}
	
	public int getType() {
		return type;
	}
	
	public Color getPencilColor() {
		return pencilColor;
	}
	
	public Color getEraserColor() {
		return eraserColor;
	}
	
	public Stroke getStroke() {
		return stroke;
	}
	
	public String getText() {
		return text;
	}
	
	public int getFontsize() {
		return fontsize;
	}
	
	public Point getStartPoint() {
		return startPoint;
	}
	
	public Point getEndPoint() {
		return endPoint;
	}
	
	public void setEndPoint(Point endPoint) {
		this.endPoint = endPoint;
	}
	
	public void setStartPoint(Point startPoint) {
		this.startPoint = startPoint;
	}

}
