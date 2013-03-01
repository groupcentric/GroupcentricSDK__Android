package com.groupcentric.android;

/*
 * This activity is fired off when a user is in some content activity and taps a 'Share' button.
 * This activity loads up the list of the users Groupcentric groups (if they have any) through the GroupsListFragment.
 * The content being shared is displayed at the top of the activity.
 * If the user is not a Groupcentric user yet, the GroupsListFragment will take care of that.
 */

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.support.v4.app.FragmentActivity;
import com.groupcentric.model.MessageContent;
import com.groupcentric.util.SquareImageDownloader;
import com.groupcentric.android.gc_GroupsListFragment.GroupcentricNotificationsButtonListener;


public class gc_ShareSelector extends FragmentActivity implements GroupcentricNotificationsButtonListener  {
    private SquareImageDownloader imageDownloader;
    private ImageView imgShareItemImage;
    private TextView txtItemTitle;
    private TextView txtItemAddress;
    private RelativeLayout lyt_cancel;
    private int contentType = 0;
    
    private MessageContent msgObj;
    gc_Variables var;

   
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
		setContentView(R.layout.gc_gc_shareselector);
		
		 msgObj = new MessageContent();
		 
        //doing this for testing
		var = new gc_Variables();
        //var.setGroupcentricUserID(this,3);
        
        //setup the ui elements to display the shared item -------------------------
        imageDownloader = new SquareImageDownloader();
        imgShareItemImage = (ImageView) findViewById(R.id.item__image);
        txtItemTitle = (TextView) findViewById(R.id.item__title);
        txtItemAddress = (TextView) findViewById(R.id.item__subtitle);
        
        //get the content object being shared---------------------------
        Intent intent = getIntent();
        String msgtype = intent.getStringExtra("MessageType"); 
        var.setGroupcentricAPIKey(this, intent.getStringExtra("APIKey"));
        msgObj.setType(intent.getStringExtra("MessageType"));
        
        int contentType =  Integer.parseInt(msgtype); 
        if(contentType == 1)
        {
        	//display the content image being shared
        	imageDownloader.download(intent.getExtras().getString("ObjectImage"), imgShareItemImage); 
        	msgObj.setImageURL(intent.getStringExtra("ObjectImage"));
        	msgObj.setTitle(intent.getStringExtra("ObjectTitle"));
        }
        else if(contentType == 3)
        {
        	//display the content image being shared if there is one
        	try {
        		if(intent.getExtras().getString("ObjectImage").length() > 0)
        			imageDownloader.download(intent.getExtras().getString("ObjectImage"), imgShareItemImage); 
        	}
        	catch(Exception ex){}
        	//display the content title being shared
        	txtItemTitle.setText(Html.fromHtml(intent.getStringExtra("ObjectTitle")));
        	//now take content and put into a Type 3 object
        	msgObj.setTitle(intent.getStringExtra("ObjectTitle"));
        	msgObj.setURL(intent.getStringExtra("ObjectURL"));
        	msgObj.setImageURL(intent.getStringExtra("ObjectImage"));
        }
        else if(contentType == 4)
        {
        	//display the content image being shared
        	imageDownloader.download(intent.getExtras().getString("ObjectImage"), imgShareItemImage); 
        	//display the content title being shared
        	txtItemTitle.setText(Html.fromHtml(intent.getStringExtra("ObjectTitle")));
        	//now take content and put into a Type 4 object
        	msgObj.setTitle(intent.getStringExtra("ObjectTitle"));
        	msgObj.setSubtitle(intent.getStringExtra("ObjectSubtitle"));
        	msgObj.setDateof(intent.getStringExtra("ObjectDate"));
        	msgObj.setDetails(intent.getStringExtra("ObjectDetails"));
        	msgObj.setImageURL(intent.getStringExtra("ObjectImage"));
        	msgObj.setURL(intent.getStringExtra("ObjectURL"));
        }
        else if(contentType >= 100)
        {
        	//display the content image being shared
        	imageDownloader.download(intent.getExtras().getString("ObjectImage"), imgShareItemImage); 
        	//display the content title being shared
        	txtItemTitle.setText(Html.fromHtml(intent.getStringExtra("ObjectTitle")));
        	//now take content and put into a Type 4 object
        	msgObj.setType(intent.getStringExtra("MessageType"));
        	msgObj.setTitle(intent.getStringExtra("ObjectTitle"));
        	msgObj.setSubtitle(intent.getStringExtra("ObjectSubtitle"));
        	msgObj.setDateof(intent.getStringExtra("ObjectDate"));
        	msgObj.setDetails(intent.getStringExtra("ObjectDetails"));
        	msgObj.setImageURL(intent.getStringExtra("ObjectImage"));
        	msgObj.setURL(intent.getStringExtra("ObjectVar1"));
        	msgObj.setMarkup(intent.getStringExtra("ObjectMarkup"));
        }
        
    }
    
    @Override
	public void onResume() {
		super.onResume();
		//tell the GroupsListFragment that it is being fired off from the Share Selector
		gc_GroupsListFragment fragment = (gc_GroupsListFragment)getSupportFragmentManager().findFragmentById(R.id.list);
		fragment.setFromShareSelector();
		fragment.setMessageSharedObject(msgObj);
		//will have to pass it the shared content - not implemented just yet
    }

	@Override
	public void onGroupcentricNotificationsCheck(int updatecount) {
		// TODO Auto-generated method stub
		
	}
    

}
