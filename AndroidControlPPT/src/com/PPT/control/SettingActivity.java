package com.PPT.control;

import android.os.Bundle;
import android.preference.PreferenceActivity;

/*
 * 
 */
public class SettingActivity extends PreferenceActivity {
		
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
			
		//从xml文件中初始该SharedPreferences  
		addPreferencesFromResource(R.xml.setting);
	}
}
