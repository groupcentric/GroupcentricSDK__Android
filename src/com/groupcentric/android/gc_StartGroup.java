package com.groupcentric.android;
import java.util.ArrayList;

import com.groupcentric.api.BaseTaskGCActivity;
import com.groupcentric.api.model.Friend;
import com.groupcentric.api.ws.BaseTask;
import com.groupcentric.api.ws.WS06StartGroup;
import com.groupcentric.api.ws.WS12StartGroupFromObject;
import com.groupcentric.api.ws.WSGetMyGroupDetailsResult;
import com.groupcentric.util.SquareImageDownloader;

import android.content.Intent;
import android.graphics.Color;
import android.opengl.Visibility;
import android.os.Bundle;
import android.text.Html;
import android.text.Spannable;
import android.text.style.ForegroundColorSpan;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class gc_StartGroup extends BaseTaskGCActivity implements OnClickListener{
	/**
	 * @author support@groupcentric.com
	 *	<br>
	 * Purpose:Start New Group
	 * <br>
	 * Notes: 
	 * <br>
	 * Revisions:
	 * 
	 *
	 */
	EditText edtGroupName,edtGroupMessage;
	private gc_Variables var;
    private static final int RC_PICK_CONTACT = 10;
    
    private boolean startGroupWithObject = false;
    private int sharedObject_type = 0;
    private String sharedObject_title = "";
    private String sharedObject_subtitle = "";
    private String sharedObject_url = "";
    private String sharedObject_dateof = "";
    private String sharedObject_imageurl = "";
    private String sharedObject_details = "";
    private String sharedObject_markup = "";
    
    private SquareImageDownloader imageDownloader;
    private ImageView imgShareItemImage;
    private TextView txtItemTitle;

    ArrayList<Friend> tmpFriends = new ArrayList<Friend>();
    boolean startingNewGroup = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gc_activity_start_group);
        
        //CHECK FOR EXTRA, SET MARKER THAT SHARED OBJ, CALL WS12
        try
        {
	        if (getIntent().getExtras().getString("ObjectType") != null) {
	        	sharedObject_type = 0;
	            sharedObject_title = "";
	            sharedObject_subtitle = "";
	            sharedObject_url = "";
	            sharedObject_dateof = "";
	            sharedObject_imageurl = "";
	            sharedObject_details = "";
	            sharedObject_markup = "";
	        	startGroupWithObject = true;
	        	
	        	sharedObject_type = Integer.parseInt(getIntent().getExtras().getString("ObjectType"));
	        	if(sharedObject_type == 1)
	        	{
	        		sharedObject_imageurl = getIntent().getExtras().getString("ObjectImage");
	        	}
	        	else if(sharedObject_type == 3)
	        	{
	        		sharedObject_title = getIntent().getExtras().getString("ObjectTitle");
	        		sharedObject_url = getIntent().getExtras().getString("ObjectURL");
	        		sharedObject_imageurl = getIntent().getExtras().getString("ObjectImage");
	        	}
	        	else if(sharedObject_type == 4)
	        	{
	        		sharedObject_title = getIntent().getExtras().getString("ObjectTitle");
	        		sharedObject_subtitle = getIntent().getExtras().getString("ObjectSubtitle");
	        		sharedObject_url = getIntent().getExtras().getString("ObjectURL");
	        		sharedObject_dateof = getIntent().getExtras().getString("ObjectDate");
	        		sharedObject_imageurl = getIntent().getExtras().getString("ObjectImage");
	        		sharedObject_details = getIntent().getExtras().getString("ObjectDetails");
	        	}
	        	else if(sharedObject_type >= 100)
	        	{
	        		sharedObject_title = getIntent().getExtras().getString("ObjectTitle");
	        		sharedObject_subtitle = getIntent().getExtras().getString("ObjectSubtitle");
	        		sharedObject_url = getIntent().getExtras().getString("ObjectURL");
	        		sharedObject_dateof = getIntent().getExtras().getString("ObjectDate");
	        		sharedObject_imageurl = getIntent().getExtras().getString("ObjectImage");
	        		sharedObject_details = getIntent().getExtras().getString("ObjectDetails");
	        		sharedObject_markup = getIntent().getExtras().getString("ObjectType");
	        	}
	        	
	        	imageDownloader = new SquareImageDownloader();
	            imgShareItemImage = (ImageView) findViewById(R.id.item__image);
	            txtItemTitle = (TextView) findViewById(R.id.item__title);
	            try {
	        		if(getIntent().getExtras().getString("ObjectImage").length() > 0)
	        			imageDownloader.download(getIntent().getExtras().getString("ObjectImage"), imgShareItemImage); 
	        	}
	        	catch(Exception ex){}
	        	//display the content title being shared
	        	txtItemTitle.setText(Html.fromHtml(getIntent().getStringExtra("ObjectTitle")));
	        }
        }
        catch(Exception ex)
        {
        	//intent was null
        }
        
        if(startGroupWithObject)
        {
        	findViewById(R.id.layout_group_name).setVisibility(View.GONE);
        	findViewById(R.id.layout_shared_info).setVisibility(View.VISIBLE);
        }
        
        edtGroupName = (EditText)  findViewById(R.id.edt_group_name);
        edtGroupMessage = (EditText)  findViewById(R.id.edt_message);
        var = new gc_Variables(); //finish instantiating the GroupcentricVariables object which will give access to the Groupcentric UserID and APIKey
        if (savedInstanceState != null)
            	tmpFriends = savedInstanceState.getParcelableArrayList("tmpfriends") ;
        if (tmpFriends == null)
        	tmpFriends = new ArrayList<Friend>();
        setFriendsInfo();
        findViewById(R.id.btn_start).setOnClickListener(this);
        findViewById(R.id.btn_back).setOnClickListener(this);
        findViewById(R.id.layout_friends).setOnClickListener(this);
        findViewById(R.id.btn_add_friends).setOnClickListener(this);
        
        edtGroupName.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button move to the message field
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                        edtGroupMessage.requestFocus();
                        return true;
                    }
                else                  
				return false;
            }
        });
        
        edtGroupMessage.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button move to the message field
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                	onClick(findViewById(R.id.btn_start));
                        return true;
                    }
                else                  
				return false;
            }
        });
    }
	
    @Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putParcelableArrayList("tmpfriends", tmpFriends);
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			if (requestCode == RC_PICK_CONTACT) {
				tmpFriends = data.getParcelableArrayListExtra("EXTRA_CONTACTS" );
			        setFriendsInfo();
				}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	public void onClick(View v) {
		if (v.getId() == R.id.btn_start) {
			startGroup();
		} else if (v.getId() == R.id.btn_back) {
			finish(); //Go back 
		} else if (v.getId() ==R.id.layout_friends | v.getId()==R.id.btn_add_friends) {
			startActivityForResult(new Intent(this.getBaseContext(),gc_ContactList.class)
			.putParcelableArrayListExtra("tmpfriends", tmpFriends)
			, RC_PICK_CONTACT);
		}
	}

	public void onSuccess(BaseTask<?, ?, ?> task) {
		if (task instanceof WS06StartGroup) {
			 WSGetMyGroupDetailsResult result_my_groups = ((WS06StartGroup) task).getActualResult();

			if (result_my_groups.group != null) {
			       //app.setNotificationCount(result_my_groups.update_count);
			       startActivity(new Intent(this, gc_GroupDetails.class)
			        .putExtra("group", result_my_groups.group)
			    );

			       finish();
			}else
			{
				Toast.makeText(this, "Problem creating group", Toast.LENGTH_SHORT).show();
				startingNewGroup = false;
			}
			
		}
		else if (task instanceof WS12StartGroupFromObject) {
			 WSGetMyGroupDetailsResult result_my_groups = ((WS12StartGroupFromObject) task).getActualResult();

				if (result_my_groups.group != null) {
				       //app.setNotificationCount(result_my_groups.update_count);
				       startActivity(new Intent(this, gc_GroupDetails.class)
				        .putExtra("group", result_my_groups.group)
				    );

				       finish();
				}else
				{
					Toast.makeText(this, "Problem creating group", Toast.LENGTH_SHORT).show();
					startingNewGroup = false;
				}
				
			}
		
	}
	
	private void startGroup() {
		//Check that there is a title
		if(startGroupWithObject) {
			edtGroupName.setText("");
		}
		else {
			if (edtGroupName.getText().toString().trim().length() == 0) {
				Toast.makeText(this, getResources().getString(R.string.gc_enter_group_name), Toast.LENGTH_SHORT).show();
				return;
			}
		}
		String strFriends =  formatFriendsForGroup();
		String strGroupName = edtGroupName.getText().toString().trim();
		String strGroupMessage = edtGroupMessage.getText().toString().trim();
		
		if(!startGroupWithObject)
		{
			if (!startingNewGroup){
			startTask(new WS06StartGroup( this,var.getGroupcentricUserID(this), var.getGroupcentricAPIKey(this),
										   strGroupName,
										   strGroupMessage,
										   "",
										   strFriends));
			startingNewGroup = true;
			}
		}
		else //shared object
		{
			if (!startingNewGroup){
			startTask(new WS12StartGroupFromObject( this,var.getGroupcentricUserID(this), var.getGroupcentricAPIKey(this),
					   strGroupName,
					   strGroupMessage,
					   "",
					   strFriends,
					   sharedObject_type,
					   sharedObject_imageurl,
					   sharedObject_url,
					   sharedObject_title,
					   sharedObject_subtitle,
					   sharedObject_dateof,
					   sharedObject_details,
					   sharedObject_markup));
			startingNewGroup = true;
			}
			
		}
	}
    private void setFriendsInfo() {
        String strTitle = "Friends (0): ";
        String strFriends = "";
        TextView txt_group_friends = (TextView)  findViewById(R.id.txt_friends);

        for (int i = 0; i < tmpFriends.size(); i++) {
            strFriends = strFriends + (tmpFriends.get(i).getFullname() + ",");
        }

        if (strFriends.length() > 0) {
            strFriends = strFriends.substring(0, strFriends.length() - 1);
            strTitle = "Friends (" + (tmpFriends.size()) + "): ";
        }
        txt_group_friends.setText(strTitle + " " + strFriends, TextView.BufferType.SPANNABLE);


        if (tmpFriends.size() > 0) {
            Spannable WordtoSpan = (Spannable) txt_group_friends.getText();
            WordtoSpan.setSpan(new ForegroundColorSpan(Color.parseColor("#2c93e3")),9, 9+Integer.toString(tmpFriends.size()).length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            WordtoSpan.setSpan(new ForegroundColorSpan(Color.parseColor("#2c93e3")), strTitle.length(), WordtoSpan.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            txt_group_friends.setText(WordtoSpan);
        }

    }
    
    private String formatFriendsForGroup() {
		 String strNameNumber = "";
	        for (int i = 0; i < tmpFriends.size(); i++) {	
	            strNameNumber = strNameNumber + tmpFriends.get(i).getFullname() + ";" + tmpFriends.get(i).getPhone() + ",";
	        }
	        return strNameNumber;
    }
}
