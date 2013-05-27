package com.PPT.control;
import java.awt.AWTException;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import java.awt.Dimension;

public class PPTServer {
	private final static int RIGHT = 1;
	private final static int LEFT = 2;
	private final static int SHIFTF5 = 0;
	private final static int ESC = 3;
	private final static int PEN = 4;
	private final static int K_BUTTON = 5;
	private final static int D_BUTTON = 6;
	private final static int WRITE = 7;
	
	private static int key;
	
	private static ObjectInputStream fromClient;
	private static ObjectOutputStream fromServer;
	
	private Robot robot;//自动化对象
	private Dimension dim; //存储屏幕尺寸
	
	public static void main(String[] args) throws IOException,
									ClassNotFoundException, AWTException, InterruptedException{
		
		// 这一段只能获取到192.168.141.59 就是本地连接ip地址
		String ip = null;
		ip = InetAddress.getLocalHost().getHostAddress();
		System.out.println(ip);
		
		ServerSocket sSocket = new ServerSocket(2013);
		System.out.println("waiting a connection from the client");
		
		Robot robot = new Robot();
		
		Socket sock = sSocket.accept();
		System.out.println("recv a connection");
		                                               
		fromClient = new ObjectInputStream(sock.getInputStream());
		fromServer = new ObjectOutputStream(sock.getOutputStream());

		PPTServer mmc = new PPTServer();
		do{
			try{
				Choices choice = (Choices)fromClient.readObject();
				try{
					int x = choice.getX();
					int y = choice.getY();
					boolean press = choice.getPress();
//					x = (x - 334)/100;
//					y = (y - 466)/100;
					x = x/40;
					y = y/40;
					System.out.println("x is " + choice.getX());
					System.out.println("y is " + choice.getY());
					System.out.println("press is " + choice.getPress());
					mmc.Move(x, y, press);
				}catch (Exception e){
					System.out.println("choice ERROR is " + e);
				}
				
				//choice = (Choices)fromClient.readObject();
				System.out.println("the flag is " + choice.getKey());
				key = choice.getKey();
				switch(key){
				
				case SHIFTF5:
					robot.keyPress(KeyEvent.VK_SHIFT);
					Thread.sleep(20);
					robot.keyPress(KeyEvent.VK_F5);
					Thread.sleep(10);
					robot.keyRelease(KeyEvent.VK_F5);
					robot.keyRelease(KeyEvent.VK_SHIFT);
					Thread.sleep(10);
					break;
					
				case LEFT:
					robot.keyPress(KeyEvent.VK_LEFT);
					Thread.sleep(10);
					robot.keyRelease(KeyEvent.VK_LEFT);
					Thread.sleep(10);
					break;
					
				case RIGHT:
					robot.keyPress(KeyEvent.VK_RIGHT);
					Thread.sleep(10);
					robot.keyRelease(KeyEvent.VK_RIGHT);
					Thread.sleep(10);
					break;
					
				case ESC:
					robot.keyPress(KeyEvent.VK_ESCAPE);
					Thread.sleep(10);
					robot.keyPress(KeyEvent.VK_ESCAPE);
					Thread.sleep(10);
					break;
					
				case PEN:
					robot.keyPress(KeyEvent.VK_CONTROL);
					Thread.sleep(20);
					robot.keyPress(KeyEvent.VK_P);
					Thread.sleep(10);
					robot.keyRelease(KeyEvent.VK_P);
					robot.keyRelease(KeyEvent.VK_CONTROL);
					Thread.sleep(10);
					break;
					
				case K_BUTTON:
					robot.keyPress(KeyEvent.VK_K);
					Thread.sleep(20);
					robot.keyRelease(KeyEvent.VK_K);
					Thread.sleep(10);
					break;
					
				case D_BUTTON:
					robot.keyPress(KeyEvent.VK_D);
					Thread.sleep(20);
					robot.keyRelease(KeyEvent.VK_D);
					Thread.sleep(10);
					break;
					
				case WRITE:
					robot.keyPress(KeyEvent.VK_D);
					Thread.sleep(20);
					robot.keyRelease(KeyEvent.VK_D);
					Thread.sleep(10);
					break;
					
					default:
						break;
				}
			}
			catch (Exception e){
				System.out.println(" all ERROR is " + e);
				break;
			}
		}while(key != -1);
		System.out.println("exit the app");
		fromClient.close();
		fromServer.close();
		sock.close();
		sSocket.close();
		
	}
	
	public PPTServer(){
        dim = Toolkit.getDefaultToolkit().getScreenSize(); // 获得屏幕大小
        System.out.println("屏幕大小为：" + dim.getWidth() + " " + dim.getHeight());
        try{
            robot = new Robot();
        }catch(AWTException e){
            e.printStackTrace();
        }
    }
	
	public void Move(int width,int heigh, boolean isPress){    //鼠标移动函数    
		System.out.println("enter Move()...");
		Point mousepoint = MouseInfo.getPointerInfo().getLocation();
		System.out.println("移动前坐标：" + mousepoint.x + " " + mousepoint.y);
		width += mousepoint.x;
		heigh += mousepoint.y;
		try{
			// robot.delay(300);
			// robot.delay(5);
			robot.mouseMove(width,heigh);
			//Thread.sleep(10);
			//robot.mousePress(InputEvent.BUTTON1_MASK);
		}catch(Exception e){
			e.printStackTrace();
		}
		if(isPress){
			robot.mousePress(InputEvent.BUTTON1_MASK);
			// robot.mouseRelease(InputEvent.BUTTON1_MASK);
		}
		else{
			robot.mouseRelease(InputEvent.BUTTON1_MASK);
		}
		System.out.println("移动后坐标：" + width + " " + heigh);
		//robot.mousePress(InputEvent.BUTTON1_MASK);//鼠标单击
		}
}
