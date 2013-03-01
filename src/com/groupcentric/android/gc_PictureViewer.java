package com.groupcentric.android;

import android.app.Activity;
import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import com.groupcentric.api.model.GCImageItem;
import com.groupcentric.util.ImageDownloader2;

public class gc_PictureViewer extends Activity {
	/**
	 * @author support@groupcentric.com
	 *	<br>
	 * Purpose:Picture View (when user taps on attached picture)
	 * <br>
	 * Notes: 
	 * <br>
	 * Revisions:
	 * 
	 *
	 */
    private static final int SWIPE_MIN_DISTANCE = 120;
    private static final int SWIPE_MAX_OFF_PATH = 250;
    private static final int SWIPE_THRESHOLD_VELOCITY = 200;
    private GestureDetector gestureDetector;
    private ImageView imgChatPictureView;
    private ImageDownloader2 imgDownloader;
    private static int iSelectedPic = 0;
    ArrayList<GCImageItem> gcImageItems = new ArrayList<GCImageItem>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gc_activity_picture_viewer);
        
        String strPicURL = "";
        if (getIntent().getExtras() != null) {
            if (getIntent().getExtras().getString("SelectedPic") != null) {
            	strPicURL = getIntent().getExtras().getString("SelectedPic");
            }
            
            if (getIntent().getExtras().getParcelable("imageUri") != null) {
               	Uri myImageUri = getIntent().getParcelableExtra("imageUri");
               	
/////
        		BitmapFactory.Options o = new BitmapFactory.Options();
        		o.inJustDecodeBounds = false;
        		o.inSampleSize = 4;
        		Bitmap bitmapPreview = null;
        		ContentResolver cr = this.getContentResolver();
        		boolean bLoadedPreview = true;
        		try {
        			bitmapPreview = BitmapFactory.decodeStream(cr.openInputStream(myImageUri),null,o);
        		} catch (FileNotFoundException e) {
        			// TODO Auto-generated catch block
        			e.printStackTrace();
        			Toast.makeText(this, "Cant load image", Toast.LENGTH_SHORT)
        					.show();
        			bLoadedPreview = false;

        		} // load
        		cr = null;
               	////
        		BitmapDrawable d = new BitmapDrawable(getResources(), bitmapPreview);
               	((ImageView) findViewById(R.id.img_picture_viewer)).setImageDrawable(d);
               	return;
               	
            }
            
            if (getIntent().getParcelableArrayListExtra("group_pics") != null) {
            	gcImageItems = getIntent().getParcelableArrayListExtra("group_pics");
                gcImageItems.get(0).getImgURL();
                
		        for (int i = 0; i < gcImageItems.size(); i++) {
		        	if (gcImageItems.get(i).getImgURL().equalsIgnoreCase(strPicURL)) {
		        		iSelectedPic = i;
		        		break;
		        	}
		        }
		        
		        if (gcImageItems.size()>1)
	                  Toast.makeText(gc_PictureViewer.this,"Photo " + (iSelectedPic + 1) + " Of " + gcImageItems.size(), Toast.LENGTH_SHORT).show();
		        
            }
            
              
            
         }

        final GestureDetector myGesture = new GestureDetector(this, new MyGestureDetector());


        imgChatPictureView = (ImageView) findViewById(R.id.img_picture_viewer);
        imgDownloader = new ImageDownloader2();
        imgDownloader.download(gcImageItems.get(iSelectedPic).getImgURL(), imgChatPictureView);
        
        View picView = (View) findViewById(R.id.picture_view);
        picView.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if (myGesture.onTouchEvent(event)) {
                    return true;
                }
                return false;
            }
        });
        picView.setClickable(true);


    }



    class MyGestureDetector implements OnGestureListener {
    	/**
    	 * @author support@groupcentric.com
    	 *	<br>
    	 * Purpose:Picture View (when user taps on attached picture)
    	 * <br>
    	 * Notes: 
    	 * <br>
    	 * Revisions:
    	 * 
    	 *
    	 */
       
		public boolean onDown(MotionEvent e) {
			return false;
		}

		public void onLongPress(MotionEvent e) {
		}

		public boolean onScroll(MotionEvent e1, MotionEvent e2,
				float distanceX, float distanceY) {
			
			return false;
		}

		public void onShowPress(MotionEvent e) {
			// TODO Auto-generated method stub
			
		}

		public boolean onSingleTapUp(MotionEvent e) {
			// TODO Auto-generated method stub
			return false;
		}

		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			

			if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH) {
                return false;

            }

            // right to left swipe
            if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE
                    && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                if (iSelectedPic < gcImageItems.size() - 1) {

                    iSelectedPic = iSelectedPic + 1;
                    Toast.makeText(gc_PictureViewer.this,
                            "Photo " + (iSelectedPic + 1) + " Of " + gcImageItems.size(),
                            Toast.LENGTH_SHORT).show();
                    imgDownloader.download(gcImageItems.get(iSelectedPic).getImgURL(), imgChatPictureView);
                }

                // Left to right swipe
            } else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE
                    && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                if (iSelectedPic > 0) {

                    iSelectedPic = iSelectedPic - 1;
                    Toast.makeText(gc_PictureViewer.this,
                            "Photo " + (iSelectedPic + 1) + " Of " + gcImageItems.size(),
                            Toast.LENGTH_SHORT).show();
                    imgDownloader.download(gcImageItems.get(iSelectedPic).getImgURL(), imgChatPictureView);
                }


            }
			return false;
		}


    }

}
