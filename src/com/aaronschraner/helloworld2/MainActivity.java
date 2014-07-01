package com.aaronschraner.helloworld2;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends Activity
{
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Button b1=(Button)findViewById(R.id.button1);
		
		b1.setOnClickListener(new OnClickListener() { 
			public void onClick(View v)
			{
				((Button)v).setText("My father will hear about this.");
				launchCameraForPicture();
				
			}
		});
	}
	
	static final int REQUEST_IMAGE_CAPTURE = 9315;
	static final int REQUEST_TAKE_PHOTO = 9316;
	
	private void launchCameraForPicture()
	{
		
		Intent takePictureIntent = new Intent (MediaStore.ACTION_IMAGE_CAPTURE); //intent to get image
		if (takePictureIntent.resolveActivity(getPackageManager())!=null) //as long as the phone has a camera app
		{
			//startActivityForResult(takePictureIntent,REQUEST_IMAGE_CAPTURE);
			File photoFile=null;
			try {
				photoFile=createImageFile();
			} catch (IOException ex) {
				//TODO: error handling
			}
			if(photoFile != null) {
				takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, 
						Uri.fromFile(photoFile));
				startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
			}
			else
			{
				TextView t=(TextView)findViewById(R.id.textView1);
				t.setText("photoFile wuz null");
				startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
			}
			
		}
	}
	
	String mCurrentPhotoPath;
	private File createImageFile() throws IOException 
	{
		String timeStamp=(String)(new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()));
		String imageFileName=("HELLOWORLD_"+timeStamp);
		File storageDir = Environment.getExternalStoragePublicDirectory(
				Environment.DIRECTORY_PICTURES);
		File image = File.createTempFile(imageFileName, ".jpg", storageDir);
		mCurrentPhotoPath = "file:" + image.getAbsolutePath();
		return image;
	};
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		final ImageView mImageView = (ImageView)findViewById(R.id.imageView1);
		if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK)
		{
			setPic(mImageView);
			
			
		}
		else if (requestCode==REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK)
		{
			Bundle extras = data.getExtras();
	        Bitmap imageBitmap = (Bitmap) extras.get("data");
	        mImageView.setImageBitmap(imageBitmap);
		}
	}
	private void galleryAddPic() 
	{
	    Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
	    File f = new File(mCurrentPhotoPath);
	    Uri contentUri = Uri.fromFile(f);
	    mediaScanIntent.setData(contentUri);
	    this.sendBroadcast(mediaScanIntent);
	}
	
	private void setPic(ImageView mImageView)
	{
		// Get the dimensions of the View
	    int targetW = mImageView.getWidth();
	    int targetH = mImageView.getHeight();

	    // Get the dimensions of the bitmap
	    BitmapFactory.Options bmOptions = new BitmapFactory.Options();
	    bmOptions.inJustDecodeBounds = true;
	    BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
	    int photoW = bmOptions.outWidth;
	    int photoH = bmOptions.outHeight;

	    // Determine how much to scale down the image
	    int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

	    // Decode the image file into a Bitmap sized to fill the View
	    bmOptions.inJustDecodeBounds = false;
	    bmOptions.inSampleSize = scaleFactor;
	    bmOptions.inPurgeable = true;

	    Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
	    mImageView.setImageBitmap(bitmap);
	}
	
}
