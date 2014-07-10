package com.example.camerazoom;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.example.camerazoom.R;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;


public class MainActivity extends Activity {
	
	private boolean hasCamera;
	private Camera mCamera;
    private CameraPreview mPreview;
    String TAG = "MainActivity=>";
    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;
    Camera.Parameters params;
    String createdPhotoPath;

    @Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.preview);
	    hasCamera = checkCameraHardware(this);
	    
	    
	    if(hasCamera) {
	    	
	    	// Create an instance of Camera
	    	try {
	    		mCamera = getCameraInstance();
	    		Log.e("cameraInstance=>0", mCamera.toString());
	    	} catch(Exception e) {
	    		Log.e("Error in creating Camera Intance=>", "Error=>"+e.getMessage());
	    	}
	    	
	        
	    	// get Camera parameters
	    	Camera.Parameters params = mCamera.getParameters();
	    	
	    	try {
	    		if(params.isZoomSupported()) {
	    			Log.e("isZoomSupported", "yes");
	    			Log.e("current Zoom Value", String.valueOf(params.getZoom()));
	    			Log.e("getMaxZoom Value", String.valueOf(params.getMaxZoom()));
	    			Log.e("getZoomRatios Value", String.valueOf(params.getZoomRatios()));
	    			
	    			params.setZoom(20);
	    			mCamera.setParameters(params);
	    		} else {
	    			Log.e("isZoomSupported", "no");
	    		}
	    	} catch ( Exception e) {
	    		Log.e("Error in creating Camera Intance=>", "Error=>"+e.getMessage());
	    	}
	    	
	        // Create our Preview view and set it as the content of our activity.
	        mPreview = new CameraPreview(this, mCamera);
	        FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
	        Log.e("mPreview", mPreview.toString());
	        preview.addView(mPreview);
	        
	    	Log.e("cameraInstance=>2", mCamera.toString());
	    	
	    	
	    	
	    	// Add a listener to the Capture button
		    Button captureButton = (Button) findViewById(R.id.button_capture);
		    captureButton.setOnClickListener(
		        new View.OnClickListener() {
		            @Override
		            public void onClick(View v) {
		                // get an image from the camera
		            	Log.e("button_capture=>", "Click");
		                mCamera.takePicture(null, null, mPicture);
		            }
		        }
		    );
	    	
	    } else {
	    	Log.e("cameraInstance", "Device does not have Camera.");
	    }
	    
	}
	
	private PictureCallback mPicture = new PictureCallback() {
		
	    @Override
	    public void onPictureTaken(byte[] data, Camera camera) {
	    	Log.e("onPictureTaken=>", "Call");
	        File pictureFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);
	        if (pictureFile == null){
	            Log.d(TAG, "Error creating media file, check storage permissions: pictureFile is null");
	            return;
	        }

	        try {
	            FileOutputStream fos = new FileOutputStream(pictureFile);
	            fos.write(data);
	            fos.close();
	            
	            Log.e("Photo is save ", "at position =>"+createdPhotoPath);
	            
	        } catch (FileNotFoundException e) {
	            Log.d(TAG, "File not found: " + e.getMessage());
	        } catch (IOException e) {
	            Log.d(TAG, "Error accessing file: " + e.getMessage());
	        }
	    }
	};
	
	
	/** Check if this device has a camera */
    private boolean checkCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
            // this device has a camera
        	Log.e("HasCamera=>", "Yes");
            return true;
        } else {
            // no camera on this device
        	Log.e("HasCamera=>", "No");
            return false;
        }
    }
    
    /** A safe way to get an instance of the Camera object. */
    public static Camera getCameraInstance(){
        Camera c = null;
        try {
            c = Camera.open(); // attempt to get a Camera instance
        }
        catch (Exception e){
        	Log.e("Error in opening Camera", "Error==>"+e.getMessage());
            // Camera is not available (in use or does not exist)
        }
        return c; // returns null if camera is unavailable
    }
    
    @Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		Log.e("onPause=>1", "mCamera.released");
		if (mCamera != null){
            mCamera.release();        // release the camera for other applications
            mCamera = null;
        } else {
        	Log.e("onPause=>2", "mCamera is null");
        }
		Log.e("onPause=>3", "mCamera.released");
	}
    
    
    /** Create a file Uri for saving an image or video */
    private Uri getOutputMediaFileUri(int type){
          return Uri.fromFile(getOutputMediaFile(type));
    }

    /** Create a File for saving an image or video */
    private File getOutputMediaFile(int type){
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                  Environment.DIRECTORY_PICTURES), "MyCameraApp");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                Log.d("MyCameraApp", "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) { 
        	
        	createdPhotoPath = mediaStorageDir.getPath() + File.separator + "IMG_"+ timeStamp + ".jpg";
        	
            mediaFile = new File(createdPhotoPath);
            
            Log.e("Image is created=>", createdPhotoPath);
        } else if(type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
            "VID_"+ timeStamp + ".mp4");
        } else {
        	Log.e("Image is not created=>", "Error");
        	
            return null;
        }

        return mediaFile;
    }
}
