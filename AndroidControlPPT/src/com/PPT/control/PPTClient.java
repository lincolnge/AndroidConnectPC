package com.PPT.control;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;  
import android.view.MenuItem;  
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Toast;
import android.widget.ToggleButton;

public class PPTClient extends Activity implements OnTouchListener, OnGestureListener {
	private Button specification;
	private Button start;
	private Button escape;
	private ToggleButton pen;
	private ToggleButton write;
	private Button K_Button;
	private Button D_Button;
	
	private Socket sock;
	private ObjectOutputStream fromClient;
	private ObjectInputStream fromServer;
	
	private final static int RIGHT = 1;
	private final static int LEFT = 2;
	private final static int SHIFTF5 = 8;
	private final static int ESC = 3;
	private final static int PEN = 4;
	private final static int K_BUTTON = 5;
	private final static int D_BUTTON = 6;
	private final static int WRITE = 7;
	private final static int ARROW = 9;
	private boolean press;
	private boolean first;
	private boolean isPen = false;
	
	GestureDetector mGestureDetector; 
	private static final int FLING_MIN_DISTANCE = 50;  
	private static final int FLING_MIN_VELOCITY = 0;
	private static final String view = null;
		
	private String serverUrl = "192.168.51.1";
	private int serverPort = 2013;

	EditTextPreference serverURLIP;
	/** Called when the activity is first created. */
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		Thread connectPC = new MySendCommondThread();
		connectPC.start(); 
		
		specification = (Button)this.findViewById(R.id.specification);
		start = (Button)this.findViewById(R.id.start);
		escape = (Button)this.findViewById(R.id.escape);
		pen = (ToggleButton)this.findViewById(R.id.pen);
		write = (ToggleButton)this.findViewById(R.id.write);
		K_Button = (Button)this.findViewById(R.id.K_Button);
		D_Button = (Button)this.findViewById(R.id.D_Button);
		
		specification.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View view) {
				/*创建新Toast对象*/
				Toast showImageToast=new Toast(PPTClient.this);
				/*创建新ImageView对象*/
				ImageView imageView=new ImageView(PPTClient.this);
				/*从资源中获取图片*/
				imageView.setImageResource(R.drawable.specification);
				/*设置Toast上的View--(ImageView)*/
				showImageToast.setView(imageView);
				/*设置Toast位置*/
				showImageToast.setGravity(Gravity.CENTER, 0, 0);
				/*设置Toast显示时间*/
				showImageToast.setDuration(Toast.LENGTH_LONG);
				/*显示Toast*/
				showImageToast.show();
			}
		});
			
		start.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View view) {
				Choices choice = new Choices(SHIFTF5);
				try {
					fromClient.writeObject(choice);
					System.out.println("send the start shift + f5");
				} catch (Exception e) {}
			}
		});
		
		escape.setOnClickListener(new Button.OnClickListener(){
			@Override
			public void onClick(View arg0) {
				Choices choice = new Choices(ESC);
				try {
					fromClient.writeObject(choice);
					System.out.println("send the escape");
				} catch (Exception e) {}
			}
		});
			
			pen.setOnClickListener(new ToggleButton.OnClickListener() {
				@Override
				public void onClick(View arg0) {
					Choices  choice = new Choices(PEN);
					try {
						fromClient.writeObject(choice);
						System.out.println("choice the pen");
					} catch (Exception e) {}
					// 当按钮第一次被点击时候响应的事件        
					if (pen.isChecked()) {
						isPen = true;
					}   
					// 当按钮再次被点击时候响应的事件  
					else {
						isPen = false;
						choice = new Choices(ARROW);
						try {
							fromClient.writeObject(choice);
							System.out.println("send the escape");
						} catch (Exception e) {}
					}      
				}  
			});
	
			write.setOnClickListener(new ToggleButton.OnClickListener() {
				@Override
				public void onClick(View arg0) {
					// 当按钮第一次被点击时候响应的事件        
					if (write.isChecked()) {
						press = true;
					}   
					// 当按钮再次被点击时候响应的事件  
					else {
						press = false;
					}      
				}
			});
			
			K_Button.setOnClickListener(new Button.OnClickListener() {
				@Override
				public void onClick(View arg0) {
					Choices  choice = new Choices(K_BUTTON);
					try {
						fromClient.writeObject(choice);
						System.out.println("choice (保留)K");
					} catch (Exception e) {} 
				}
			});
			
			D_Button.setOnClickListener(new Button.OnClickListener() {
				@Override
				public void onClick(View arg0) {
					Choices  choice = new Choices(D_BUTTON);
					try {
						fromClient.writeObject(choice);
						System.out.println("choice (放弃)D");
					} catch (Exception e) {}
				}
			});
			
			mGestureDetector = new GestureDetector(this);  
			LinearLayout imageview=(LinearLayout)findViewById(R.id.imageView);  
			imageview.setOnTouchListener(this);  
			imageview.setLongClickable(true); 
	}
	
	/**
	 * 监听BACK键
	 * @param keyCode
	 * @param event
	 * @return
	 */
	public boolean onKeyDown(int keyCode, KeyEvent event) 
	{	
	if ( event.getKeyCode() == KeyEvent.KEYCODE_BACK){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("exit app");
		builder.setMessage("You will exit the app...");
		//builder.setIcon(R.drawable.stat_sys_warning);
		builder.setPositiveButton("OK",new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Intent startMain = new Intent(Intent.ACTION_MAIN);
				startMain.addCategory(Intent.CATEGORY_HOME); 
				startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
				startActivity(startMain);					
				try {
							Choices choice = new Choices(-1);
					fromClient.writeObject(choice);
				} catch (Exception e) {

				} finally {
					// finally中的代码，无论异常不异常都会执行。
					// finally子句的作用主要是当抛出的异常没有被捕获时用来释放内存,和恢复调用的资源.
					// 就是执行了finally子句以后,程序要回到抛出异常的地方,而不是执行finally后面的语句.
				}
				System.exit(0);		
			}

		});
		builder.setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {}
		});
		builder.show();
	}
	return super.onKeyDown(keyCode, event);
}
	
@Override  
public boolean onTouch(View v, MotionEvent event) {  
	Log.i("touch","touch");
	return mGestureDetector.onTouchEvent(event);
	// return false;
}

public boolean onTouchEvent(MotionEvent event) {
	return false;
}

@Override  
public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {  
	if(!isPen){
		if (e1.getX()-e2.getX() > FLING_MIN_DISTANCE && Math.abs(velocityX) > FLING_MIN_VELOCITY) {   
			// Fling left   
			// Toast.makeText(this, "向左手势", Toast.LENGTH_SHORT).show();
			Choices choice = new Choices(RIGHT);
			try {
				fromClient.writeObject(choice);
				System.out.println("send the right (the next)");
			} catch (Exception e) {} 
			finally {}
		} else if (e2.getX()-e1.getX() > FLING_MIN_DISTANCE && Math.abs(velocityX) > FLING_MIN_VELOCITY) {   
			// Fling right   
			//  Toast.makeText(this, "向右手势", Toast.LENGTH_SHORT).show();
			Choices choice = new Choices(LEFT);
			try {
				fromClient.writeObject(choice);
				System.out.println("send the left (the last)");
			} catch (Exception e) {} 
			finally  {}
		}
	}
	return false;
}  

@Override
public boolean onDown(MotionEvent e) {
	// TODO Auto-generated method stub
	return false;
}

@Override
public void onLongPress(MotionEvent e) {
	// TODO Auto-generated method stub
}

@Override
public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
		float distanceY) {
	// TODO Auto-generated method stub
	if(isPen){
		int x = (int) (e2.getX() - e1.getX());
		int y = (int) (e2.getY() - e1.getY());
		// press = false;
		Choices choice = new Choices(x, y, press);
		// Toast.makeText( this, "x:" + x + "y: " + y, Toast.LENGTH_SHORT).show();
		try {
			fromClient.writeObject(choice);
			//Thread.sleep(10);
			//Thread.sleep(300);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
			// Toast.makeText( this, "Scroll error is " + e, Toast.LENGTH_SHORT).show();
		}
	}
	return false;
}

@Override
public void onShowPress(MotionEvent e) {
	// TODO Auto-generated method stub
}

@Override
public boolean onSingleTapUp(MotionEvent e) {
	// TODO Auto-generated method stub
	return false;
}

//当我们按HOME键时，我在onPause方法里，将输入的值赋给mString  
	@Override  
	protected void onPause() { // 原来这里影响了 我的setting, 找了半天
		super.onPause();
	}
	
	@Override
	public void onStart()//重新启动的时候
	{
		super.onStart();
		Thread connectPC = new MySendCommondThread();
		connectPC.start(); 
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		// 在这里调用线程会影响app的操作
		// Toast.makeText(this, "onResume", Toast.LENGTH_SHORT).show();
		// Thread connectPC = new MySendCommondThread();
		// connectPC.start();
	}

	@Override  
	protected void onStop() {  
		super.onStop();
		Choices choice = new Choices(-1);
		try {
			fromClient.writeObject(choice);
		} catch (Exception e) {}
			// Thread connectPC = new MySendCommondThread();
			// connectPC.interrupt();
			// connectPC = null;
	}
	
	/**发送命令线程*/
	class MySendCommondThread extends Thread{
		public void run(){
			
			//读取配置文件
			SharedPreferences preParas = PreferenceManager.getDefaultSharedPreferences(PPTClient.this);
			serverUrl = preParas.getString("ServerUrl", "192.168.51.1");
			String tempStr = preParas.getString("ServerPort", "2013");
			serverPort = Integer.parseInt(tempStr);
				
			//实例化Socket
			try {
				sock = new Socket(InetAddress.getByName(serverUrl),serverPort);
				fromClient = new ObjectOutputStream(sock.getOutputStream());
				fromServer = new ObjectInputStream(sock.getInputStream());
			} catch (Exception e1) {
				
			} 
		}
	}
	
	public static final int SET = Menu.FIRST;  
	public static final int EXIT = Menu.FIRST+1;  
			
	//创建Menu菜单  
	@Override  
	public boolean onCreateOptionsMenu(Menu menu) {  
		menu.add(0,SET,0,"设置");  
		menu.add(0,EXIT,0,"退出");  
		return super.onCreateOptionsMenu(menu);  
	}  

	//点击Menu菜单选项响应事件   
	@Override  
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);//获取菜单
		switch(item.getItemId()) {  
			case 1:
			{
				Intent intent = new Intent(this, SettingActivity.class);
				try{
					startActivity(intent);
				} catch(Exception e) {
					// Toast.makeText(this, "option error1" + e.toString(), Toast.LENGTH_SHORT).show();
					Log.e("MYAPP", "exception: " + e.toString());
				} catch(Error e2) {
					Toast.makeText(this, e2.toString(), Toast.LENGTH_SHORT).show();
					Log.e("MYAPP", "exception: " + e2.toString());
				}
				break;
			}
			case 2:{
				try{
					//杀掉线程强制退出
					android.os.Process.killProcess(android.os.Process.myPid());
				} catch(Exception e) {
					
				}
				break;
			}
		}
		return true;
	}  
}