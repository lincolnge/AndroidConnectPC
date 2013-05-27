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
import android.view.KeyEvent;
import android.view.Menu;  
import android.view.MenuItem;  
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

public class PPTClient extends Activity implements OnTouchListener, OnGestureListener {
	private Button start;
	private Button escape;
	private Button pen;
	private Button K_Button;
	private Button D_Button;
	private Button write;
	
	private Socket sock;
	private ObjectOutputStream fromClient;
	private ObjectInputStream fromServer;
	
	private final static int RIGHT = 1;
	private final static int LEFT = 2;
	private final static int SHIFTF5 = 0;
	private final static int ESC = 3;
	private final static int PEN = 4;
	private final static int K_BUTTON = 5;
	private final static int D_BUTTON = 6;
	private final static int WRITE = 7;
	private boolean press;
	private boolean first;
	private boolean isPen = false;
	
	GestureDetector mGestureDetector; 
	private static final int FLING_MIN_DISTANCE = 50;  
    private static final int FLING_MIN_VELOCITY = 0;
    
    private String serverUrl = "192.168.51.1";
    private int serverPort = 2013;
    
    EditTextPreference serverURLIP;
    /** Called when the activity is first created. */
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        /*        
        try {
        	sock = new Socket(InetAddress.getByName(serverUrl),serverPort);
        	fromClient = new ObjectOutputStream(sock.getOutputStream());
			fromServer = new ObjectInputStream(sock.getInputStream());
			
		} catch (Exception e1) {
			
		}
        */
        Thread connectPC = new MySendCommondThread();
        connectPC.start(); 
  	  	
        start = (Button)this.findViewById(R.id.start);
        escape = (Button)this.findViewById(R.id.escape);
        pen = (Button)this.findViewById(R.id.pen);
        K_Button = (Button)this.findViewById(R.id.K_Button);
        D_Button = (Button)this.findViewById(R.id.D_Button);
        write = (Button)this.findViewById(R.id.write);
        
        start.setOnClickListener(new Button.OnClickListener(){

			@Override
			public void onClick(View v) {
				Choices choice = new Choices(SHIFTF5);
				try {
					fromClient.writeObject(choice);
					System.out.println("send the start shift + f5");
				} catch (Exception e) {
//					e.printStackTrace();
//					Toast.makeText(this, "error", Toast.LENGTH_SHORT).show();
				}
			}
        	
        });
        
        escape.setOnClickListener(new Button.OnClickListener(){

			@Override
			public void onClick(View arg0) {
				Choices choice = new Choices(ESC);
				try {
					fromClient.writeObject(choice);
					System.out.println("send the escape");
				} catch (Exception e) {
//					e.printStackTrace();
				}
			}
        });
        
        pen.setOnClickListener(new Button.OnClickListener(){

			@Override
			public void onClick(View arg0) {
				Choices  choice = new Choices(PEN);
				try {
					fromClient.writeObject(choice);
					System.out.println("choice the pen");
				} catch (Exception e) {
//					e.printStackTrace();
				}
				isPen = !isPen;
			}
        });
        
        write.setOnClickListener(new Button.OnClickListener(){
			@Override
			public void onClick(View arg0) {
				if(first){
					press = true;
					first = !first;
				}
				else{
					press = false;
					first = !first;
				}
			}
        });
        
        K_Button.setOnClickListener(new Button.OnClickListener(){

			@Override
			public void onClick(View arg0) {
				Choices  choice = new Choices(K_BUTTON);
				try {
					fromClient.writeObject(choice);
					System.out.println("choice (保留)K");
				} catch (Exception e) {
//					e.printStackTrace();
				} 
			}
        });
        
        D_Button.setOnClickListener(new Button.OnClickListener(){

			@Override
			public void onClick(View arg0) {
				Choices  choice = new Choices(D_BUTTON);
				try {
					fromClient.writeObject(choice);
					System.out.println("choice (放弃)D");
				} catch (Exception e) {
//					e.printStackTrace();
				} 
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

					} finally  {
						// finally中的代码，无论异常不异常都会执行。
						// finally子句的作用主要是当抛出的异常没有被捕获时用来释放内存,和恢复调用的资源.
						// 就是执行了finally子句以后,程序要回到抛出异常的地方,而不是执行finally后面的语句.
			        }
					System.exit(0);		
				}

			});
			builder.setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					
				}
			});
			builder.show();
		}
		return super.onKeyDown(keyCode, event);
	}
    
    @Override  
    public boolean onTouch(View v, MotionEvent event) {  
        // TODO Auto-generated method stub  
    	Log.i("touch","touch");
    	/*
    	// Toast.makeText( this, "Touch Touch", Toast.LENGTH_SHORT).show();
    	int x = (int) event.getX();
    	int y = (int) event.getY();
    	Choices choice = new Choices(x, y);
    	// Toast.makeText( this, "x:" + x + "y: " + y, Toast.LENGTH_SHORT).show();
    	try {
			fromClient.writeObject(choice);
			Thread.sleep(40);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
			Toast.makeText( this, "error is " + e, Toast.LENGTH_SHORT).show();
		}
		*/
    	return mGestureDetector.onTouchEvent(event);
    	// return false;
    }
    /*
    public boolean onTouchEvent(MotionEvent event) {	
    	int x = (int) event.getX();
    	int y = (int) event.getY();
    	Choices choice = new Choices(x, y);
    	Toast.makeText( this, "x:" + x + "y: " + y, Toast.LENGTH_SHORT).show();
    	try {
			fromClient.writeObject(choice);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
			Toast.makeText( this, "error is " + e, Toast.LENGTH_SHORT).show();
		}
    	return false;
    }
    */
    @Override  
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {  
    	// TODO Auto-generated method stub
    	/*
    	int x = (int) (e1.getX() - e2.getX());
    	int y = (int) (e1.getY() - e2.getY());
    	System.out.println("e1: " + e1.getX() + ", " + e1.getY());
    	System.out.println("e2: " + e2.getX() + ", " + e2.getY());
    	System.out.println("x: " + x + " y: " + y);
    	Choices choice = new Choices(x, y);
    	Toast.makeText( this, "x:" + x + "y: " + y, Toast.LENGTH_SHORT).show();
    	try {
			fromClient.writeObject(choice);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
			Toast.makeText( this, "error is " + e, Toast.LENGTH_SHORT).show();
		}
    	*/
    	if(!isPen){
	    	if (e1.getX()-e2.getX() > FLING_MIN_DISTANCE && Math.abs(velocityX) > FLING_MIN_VELOCITY) {   
	    		// Fling left   
	    		// Toast.makeText(this, "向左手势", Toast.LENGTH_SHORT).show();
	    		
	            Choices choice = new Choices(RIGHT);
				try {
					fromClient.writeObject(choice);
					System.out.println("send the right (the next)");
				} catch (Exception e) {
					// e.printStackTrace();
					Toast.makeText(this, "error", Toast.LENGTH_SHORT).show();
				} finally  {  
				
				}
				
				} else if (e2.getX()-e1.getX() > FLING_MIN_DISTANCE && Math.abs(velocityX) > FLING_MIN_VELOCITY) {   
				// Fling right   
				//  Toast.makeText(this, "向右手势", Toast.LENGTH_SHORT).show();
				 
				Choices choice = new Choices(LEFT);
				try {
					fromClient.writeObject(choice);
					System.out.println("send the left (the last)");
				} catch (Exception e) {
					// e.printStackTrace();
					Toast.makeText(this, "error", Toast.LENGTH_SHORT).show();
				} finally  {  
				
				}
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
			} catch (Exception e) {
				// TODO Auto-generated catch block
				// e.printStackTrace();
				Toast.makeText( this, "Scroll error is " + e, Toast.LENGTH_SHORT).show();
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
//        Toast.makeText(this, "onResume", Toast.LENGTH_SHORT).show();
//        Thread connectPC = new MySendCommondThread();
//        connectPC.start();
    }

    @Override  
    protected void onStop() {  
        super.onStop();
        Choices choice = new Choices(-1);
		try {
			fromClient.writeObject(choice);
		} catch (Exception e) {
//			e.printStackTrace();
			Toast.makeText(this, "stop" + e.toString(), Toast.LENGTH_SHORT).show();
		}
//        Thread connectPC = new MySendCommondThread();
//        connectPC.interrupt();
//        connectPC = null;
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
        switch(item.getItemId()){  
	        case 1:
	        {
        		Intent intent = new Intent(this, SettingActivity.class);
	        	try{
					startActivity(intent);
	        	} catch(Exception e){
	        		Toast.makeText(this, "option error1" + e.toString(), Toast.LENGTH_SHORT).show();
	        		Log.e("MYAPP", "exception: " + e.toString());
	        	} catch(Error e2){
	        		Toast.makeText(this, e2.toString(), Toast.LENGTH_SHORT).show();
	        		Log.e("MYAPP", "exception: " + e2.toString());
	        	}
	            break;
	        }
	        case 2:{
	        	try{
	        		//杀掉线程强制退出
					android.os.Process.killProcess(android.os.Process.myPid());
	        	} catch(Exception e){
	        		
	        	}
	            break;  
        	}  
        }
        return true;  
    }  
}