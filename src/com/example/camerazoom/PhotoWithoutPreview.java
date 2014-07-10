package com.example.camerazoom;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;  
import java.text.SimpleDateFormat;
import java.util.Date;
import android.os.Bundle;  
import android.os.Environment;  
import android.app.Activity;  
import android.content.Context;
import android.content.pm.PackageManager;   
import android.hardware.Camera;    
import android.hardware.Camera.PictureCallback;
import android.util.Log;  
import android.view.SurfaceView; 

public class PhotoWithoutPreview extends Activity {
     private Camera camera;  
     private boolean hasCamera;
     public boolean isCameraOpened = false;  
     String TAG = "MainActivity=>";
     public static final int MEDIA_TYPE_IMAGE = 1;
     String createdPhotoPath;
     
     @Override  
     public void onCreate(Bundle savedInstanceState) {  
           super.onCreate(savedInstanceState);  
	       hasCamera = checkCameraHardware(this);
	       
	       if(hasCamera) {
	    	   camera=getCameraInstance();
	    	   takePictureNoPreview(); 
	       } else {
	    	   Log.e("cameraInstance", "Device does not have Camera.");
	       }
       }  
     
     @Override  
     protected void onPause() {  
    	 releaseCamera();  
    	 super.onPause();  
     }  
     
     @Override
	protected void onStop() {
    	 releaseCamera();  
    	 super.onStop();
	}

	@Override
	protected void onDestroy() {
		releaseCamera();  
		super.onDestroy();
	}

	private void releaseCamera() {  
       if (camera != null) {  
            camera.stopPreview();  
            camera.release();  
            camera = null; 
            isCameraOpened = false;
            Log.e("releaseCamera", "Successfully");
       } else {
    	   Log.e("releaseCamera", "Already Closed");
       }
     } 
     
     /** Check if this device has a camera */
     private boolean checkCameraHardware(Context context) {
         if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
         	Log.e("HasCamera=>", "Yes");
             return true;
         } else {
         	Log.e("HasCamera=>", "No");
             return false;
         }
     }
     
     /** A safe way to get an instance of the Camera object. */
     public Camera getCameraInstance(){
         Camera c = null;
         try {
             c = Camera.open();
             isCameraOpened = true;
             Log.e("cameraInstance", "Camera is initiated Successfully.");
         }
         catch (Exception e){
         	Log.e("Error in opening Camera", "Error==>"+e.getMessage());
         }
         return c;
     }
     
   //Here is the example for dummy surface view.     

     public void takePictureNoPreview() {
    	 if(camera!=null) {
            try {
	              //set camera parameters if you want to
	              //...
	
	              // here, the unused surface view and holder
	              SurfaceView dummy=new SurfaceView(this);
	              try {
	            	  camera.setPreviewDisplay(dummy.getHolder());
	            	  Log.e("setPreviewDisplay", "Successfully");
	              } catch (IOException e) {
	            	  Log.e("setPreviewDisplay", "Error=>"+e.getMessage());
	            	  e.printStackTrace();
	              }    
	              camera.startPreview();
	              camera.takePicture(null, null, mPicture);
            } catch (Exception e) {
          	  Log.e("Error=>", "Error=>"+e.getMessage());
          	  e.printStackTrace();
            } 
          }else{
        	  Log.e("myCamera", "is null");
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
 	        } finally {
 	        	myFinalMethod();
 	        }
 	    }
 	};
 	
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
        } else {
        	Log.e("Image is not created=>", "Error");
        	
            return null;
        }

        return mediaFile;
    }
     
  /* //Selecting front facing camera.
     private Camera openFrontFacingCameraGingerbread() {
         int cameraCount = 0;
         Camera cam = null;
         Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
         cameraCount = Camera.getNumberOfCameras();
         for ( int camIdx = 0; camIdx < cameraCount; camIdx++ ) {
             Camera.getCameraInfo( camIdx, cameraInfo );
             if ( cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT  ) {
                 try {
                     cam = Camera.open( camIdx );
                 } catch (RuntimeException e) {
                     Log.e("==>", "Camera failed to open: " + e.getLocalizedMessage());
                 }
             }
         }

         return cam;
     }
     */
    
    public void myFinalMethod() {
    	if(camera != null) {  
    		camera.stopPreview();  
    		camera.release();  
    		camera = null; 
    		isCameraOpened = false;
    		Log.e("releaseCamera=>9", "Successfully");
    		Log.e("finally", "is Called");
    		finish();
    	} else {
    	   Log.e("releaseCamera=>9", "Already Closed");
    	   finish();
    	}
    }
}