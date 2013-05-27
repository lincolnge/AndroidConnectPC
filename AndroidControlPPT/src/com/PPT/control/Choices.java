 package com.PPT.control;

import java.io.Serializable;

 
 public class Choices implements Serializable{
	private int key;
	private int x;
	private int y;
	private boolean press;

	public Choices(int key) {
		super();
		this.key = key;
	}

	public Choices(int x, int y, boolean press) {
		super();
		this.x = x;
		this.y = y;
		this.press = press;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public boolean getPress() {
		return press;
	}
	
	public void setXY(int x, int y, boolean press) {
		this.x = x;
		this.y = y;
		this.press = press;
	}
	
	public int getKey() {
		return key;
	}

	public void setKey(int key) {
		this.key = key;
	}
	
}
