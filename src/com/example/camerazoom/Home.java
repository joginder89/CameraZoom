package com.example.camerazoom;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class Home extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home);
	}
	
	public void takePhoto(View v) {
		//Intent intent = new Intent(this, PhotoWithoutPreview.class);
		//startActivity(intent);
		startService(new Intent(this, TakePhotoService.class));
	}
}
