package com.example.camerazoom;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import android.view.SurfaceView;

public class TakePhotoService extends Service {

	private Camera cameraObj;  
    private boolean hasCamera;
    public boolean isCameraOpened = false;  
    String TAG = "MainActivity=>";
    public static final int MEDIA_TYPE_IMAGE = 1;
    String createdPhotoPath;
	Context context;
	
	@Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        Log.e("onCreate","Called");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e("onDestroy","Called");
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        Log.e("onLowMemory","Called");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
    	hasCamera = checkCameraHardware(this);
    	
    	if(hasCamera) {
    		cameraObj=getCameraInstance();
    		takePictureNoPreview();
    		//new TakePhoto().execute("jk1");
    	} else {
    		Log.e("cameraInstance", "Device does not have Camera.");
    	}
    	
    	//Log.e("startId",String.valueOf(startId));
    	
    	
        return super.onStartCommand(intent, flags, startId);
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
    
    public void takePictureNoPreview() {
    	Log.e("takePictureNoPreview","Called");
    	if(cameraObj != null) {
            try {
	              
    	    		Camera.Parameters params = cameraObj.getParameters();
    	    		if(params.isZoomSupported()) {
    	    			Log.e("isZoomSupported", "yes");
    	    			Log.e("current Zoom Value", String.valueOf(params.getZoom()));
    	    			Log.e("getMaxZoom Value", String.valueOf(params.getMaxZoom()));
    	    			Log.e("getZoomRatios Value", String.valueOf(params.getZoomRatios()));
    	    			
    	    			params.setZoom(20);
    	    			cameraObj.setParameters(params);
    	    		} else {
    	    			Log.e("isZoomSupported", "no");
    	    		}
	              SurfaceView dummy=new SurfaceView(context);
	              try {
	            	  cameraObj.setPreviewDisplay(dummy.getHolder());
	            	  Log.e("setPreviewDisplay", "Called Successfully");
	              } catch (IOException e) {
	            	  Log.e("setPreviewDisplay", "Error=>"+e.getMessage());
	            	  e.printStackTrace();
	              }   
	              cameraObj.startPreview();
	              Log.e("startPreview", "Called Successfully");
	              cameraObj.takePicture(null, null, mPicture);
	              Log.e("takePicture", "Called Successfully");
            } catch (Exception e) {
          	  Log.e("Error in doInBackground", "=>"+e.getMessage());
          	  e.printStackTrace();
            } 
          }else{
        	  Log.e("myCamera", "is null");
          }
    }
    
    
    class UploadPhoto extends AsyncTask<String, String, String> {
       
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... args) {
        	Log.e("doInBackground","Called");
        	
            return "success";
        }
        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.e("onPostExecute","Called");
            myFinalMethod();
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
 	        	releaseCamera();
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
    
    public void releaseCamera() {
    	if(cameraObj != null) {  
    		cameraObj.stopPreview();  
    		cameraObj.release();  
    		cameraObj = null; 
    		isCameraOpened = false;
    		Log.e("Camera released =>9", "Successfully");
    	} else {
    	   Log.e("Camera released =>9", "Already Closed");
    	}
    }
    
    public void myFinalMethod() {
    	if(cameraObj != null) {  
    		cameraObj.stopPreview();  
    		cameraObj.release();  
    		cameraObj = null; 
    		isCameraOpened = false;
    		Log.e("releaseCamera=>9", "Successfully");
    		Log.e("finally", "is Called");
    		stopSelf();
    	} else {
    	   Log.e("releaseCamera=>9", "Already Closed");
    	   stopSelf();
    	}
    }

}
