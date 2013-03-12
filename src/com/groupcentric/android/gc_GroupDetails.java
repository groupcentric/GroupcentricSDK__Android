package com.groupcentric.android;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;
import com.groupcentric.api.BaseTaskGCFragmentActivity;
import com.groupcentric.api.model.Friend;
import com.groupcentric.api.model.GCImageItem;
import com.groupcentric.api.model.Group;
import com.groupcentric.api.model.Message;
import com.groupcentric.api.ws.BaseTask;
import com.groupcentric.api.ws.WS05GetGroupDetails;
import com.groupcentric.api.ws.WS07InviteMoreFriendsToGroup;
import com.groupcentric.api.ws.WS08RemoveGroup;
import com.groupcentric.api.ws.WS09UpdateGroup;
import com.groupcentric.api.ws.WS10SendMessage;
import com.groupcentric.api.ws.WS19TogglePushNotificationsForGroup;
import com.groupcentric.api.ws.WSGetMyGroupDetailsResult;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TabHost;
import android.widget.Toast;
import android.widget.TextView;
import android.widget.ImageView;

import com.groupcentric.model.MessageContent;
import com.groupcentric.util.GroupcentricReadWritePrefs;
import com.urbanairship.UAirship;

public class gc_GroupDetails extends BaseTaskGCFragmentActivity  implements OnClickListener{
	/**
	* @author support@groupcentric.com
	*	<br>
	* Purpose:User Group Details 
	* <br>
	* Notes: (adds fragments to tabhost)
	* <br>
	* Revisions:
	* 
	*
	*/
	TabHost mTabHost;
    TabManager mTabManager;
    Group tmpGroup;
    private gc_Variables var;
    private static final int RC_PICK_CONTACT   	        = 10;
    private static final int RC_UPDATE_TITLE            = 20;
    private static final int RC_INSERT_CHAT_ATTACHMENT  = 30;
    private static final int RC_SELECT_PICTURE          = 40;
    private static final int RC_TAKE_PICTURE		    = 50;
    private static final int RC_SET_LOCATION            = 60;
    private static final int RC_CHANGE_GROUP_PICTURE    = 70;
    private static final int RC_TAKE_CHAT_PICTURE       = 80;
    private static final int RC_SELECT_GROUP_PICTURE    = 90;
    private static final int UPLOADED_IMAGE 		    = 100;
    private static final int RC_GET_GPS				    = 110;
    private static final int RC_SELECT_CHAT_PICTURE     = 120;
    private static final int RC_TAKE_GROUP_PICTURE	    = 130;
    private static final int RC_SHARE_LINK              = 140;
    private static final int RC_SHARE_WEB_LINK_RESULT   = 150;
    
    
    private String updatedGroupName = "";
    private String sharedURL = "";
    private Uri imageUri;
    private String gpsLat = "";
    private String gpsLon = "";
    private MessageContent msgSharedObj;
    private int msgSharedObjType = 0;
    private int holdMessageCount = 0;
    private boolean bRefreshOnResume = true;
    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gc_group_details);
        var = new gc_Variables(); //finish instantiating the GroupcentricVariables object which will give access to the Groupcentric UserID and APIKey
        
        findViewById(R.id.btn_chat).setOnClickListener(this);
        findViewById(R.id.btn_friends).setOnClickListener(this);
        findViewById(R.id.btn_details).setOnClickListener(this);
        findViewById(R.id.btn_back).setOnClickListener(this);
        findViewById(R.id.btn_notification).setOnClickListener(this);
        //refreshNotificationIndicator(app.getNotificationCount(), findViewById(R.id.btn_notification));
        
        mTabHost = (TabHost) findViewById(android.R.id.tabhost);
        mTabHost.setup();


        mTabManager = new TabManager(this, mTabHost, R.id.realtabcontent);
        mTabManager.addTab(mTabHost.newTabSpec("Chat").setIndicator("Chat"),gc_ChatListFragment.class, null);
        mTabManager.addTab(mTabHost.newTabSpec("Friends").setIndicator("Friends"),gc_FriendsListFragment.class, null);
        mTabManager.addTab(mTabHost.newTabSpec("Details").setIndicator("Details"),gc_GroupDetailsFragment.class, null);
         if (savedInstanceState != null) {
            if (savedInstanceState.getString("gpsLat")!=null)
              	gpsLat = savedInstanceState.getString("gpsLat");
             
            if (savedInstanceState.getString("gpsLon")!=null)
              	gpsLon = savedInstanceState.getString("gpsLon");
              
            if (savedInstanceState.getParcelable("imageUri")!=null) ;
          		imageUri = savedInstanceState.getParcelable("imageUri");
            
          	try{
                mTabHost.setCurrentTabByTag(savedInstanceState.getString("tab")); 
                //Get group details if we already have them
                tmpGroup = savedInstanceState.getParcelable("tmpgroup") ;
                sharedURL = savedInstanceState.getString("sharedurl");
                updatedGroupName = savedInstanceState.getString("updated_group_name");
                if (tmpGroup!=null)
                	setGrouptitle();
                // Handle indicator
                handleTabIndicator(savedInstanceState.getString("tab"));
            }
            catch (Exception ex){
            	Toast.makeText(this,ex.getMessage() ,Toast.LENGTH_SHORT).show(); 
            }
            
        }
        else
        {
        	boolean hasGroupID = false;
        	if (getIntent().getExtras()!=null)
        	{

        	if (getIntent().getExtras().getString("sharedObjectType") != null) {
        		msgSharedObjType = Integer.parseInt(getIntent().getExtras().getString("sharedObjectType"));
        		msgSharedObj = getIntent().getParcelableExtra("sharedObject");
            	//Clear out the shared Object intent -
            	getIntent().getExtras().remove("sharedObject");
            	getIntent().getExtras().remove("sharedObjectType");
        	}
        	
        	if (getIntent().getExtras().getString("sharedurl") != null) {
        		sharedURL = getIntent().getExtras().getString("sharedurl");
            	//Clear out the shared URL intent -
            	getIntent().getExtras().remove("sharedurl");
        	}
        	
        	if (getIntent().getExtras().getParcelable("imageUri") != null) {
               	imageUri =  getIntent().getParcelableExtra("imageUri");
            }
        	
        	// If called from group list we will only have the group id and will need to request the details
       			int iGroupID = getIntent().getExtras().getInt("groupid",-1);       			
       			if (iGroupID>0) { // we were passed a userid so lets request the details
       				startTask(new WS05GetGroupDetails(this, var.getGroupcentricUserID(this), var.getGroupcentricAPIKey(this), iGroupID));  
       				hasGroupID=true;
       			}
       			
       			if (getIntent().getExtras().getParcelable("group")!=null) {
       				tmpGroup = getIntent().getExtras().getParcelable("group");
     				//refreshNotificationIndicator(app.getNotificationCount(), findViewById(R.id.btn_notification));
       				hasGroupID=true;
    				setGrouptitle();
    				///refreshDetailTabs();
       			}
        	}

           		// Check to see if called from Notification Push 
       			if (var.getGroupIDToOpen(this)>0) {
       				startTask(new WS05GetGroupDetails(this, var.getGroupcentricUserID(this), var.getGroupcentricAPIKey(this), var.getGroupIDToOpen(this)));  
       				var.setGroupIDToOpen(this,0);
       				hasGroupID=true;
       			}
            //If we make it here and no ID we need to drop the user into the main app -
       		//This condition can happen if the user previously opened a group from a push 
       		//then exited the app and THEN opened app via Recent apps -
       	    if (!hasGroupID){
       			Intent i = this.getApplicationContext().getPackageManager().getLaunchIntentForPackage(this.getPackageName() );
    			i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    			startActivity(i);
    			finish();
       	    }
          }

    }
    
	
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState); 
        outState.putString("tab", mTabHost.getCurrentTabTag());
        //If we have group details save them
        outState.putParcelable("tmpgroup", tmpGroup);
        outState.putString("updated_group_name", updatedGroupName);
        outState.putString("sharedurl", sharedURL);
        outState.putString("gpsLat",gpsLat);
        outState.putString("gpsLon",gpsLon);
        outState.putParcelable("imageUri", imageUri);
    }

    /**
     * This is a helper class that implements a generic mechanism for
     * associating fragments with the tabs in a tab host.  It relies on a
     * trick.  Normally a tab host has a simple API for supplying a View or
     * Intent that each tab will show.  This is not sufficient for switching
     * between fragments.  So instead we make the content part of the tab host
     * 0dp high (it is not shown) and the TabManager supplies its own dummy
     * view to show as the tab content.  It listens to changes in tabs, and takes
     * care of switch to the correct fragment shown in a separate content area
     * whenever the selected tab changes.
     */
    public static class TabManager implements TabHost.OnTabChangeListener {
        private final FragmentActivity mActivity;
        private final TabHost mTabHost;
        private final int mContainerId;
        private final HashMap<String, TabInfo> mTabs = new HashMap<String, TabInfo>();
        TabInfo mLastTab;

        static final class TabInfo {
            private final String tag;
            private final Class<?> clss;
            private final Bundle args;
            private Fragment fragment;

            TabInfo(String _tag, Class<?> _class, Bundle _args) {
                tag = _tag;
                clss = _class;
                args = _args;
            }
        }

        static class DummyTabFactory implements TabHost.TabContentFactory {
            private final Context mContext;

            public DummyTabFactory(Context context) {
                mContext = context;
            }

           // @Override
            public View createTabContent(String tag) {
                View v = new View(mContext);
                v.setMinimumWidth(0);
                v.setMinimumHeight(0);
                return v;
            }
        }

        public TabManager(FragmentActivity activity, TabHost tabHost, int containerId) {
            mActivity = activity;
            mTabHost = tabHost;
            mContainerId = containerId;
            mTabHost.setOnTabChangedListener(this);
        }

        public void addTab(TabHost.TabSpec tabSpec, Class<?> clss, Bundle args) {
            tabSpec.setContent(new DummyTabFactory(mActivity));
            String tag = tabSpec.getTag();

            TabInfo info = new TabInfo(tag, clss, args);

            // Check to see if we already have a fragment for this tab, probably
            // from a previously saved state.  If so, deactivate it, because our
            // initial state is that a tab isn't shown.
            info.fragment = mActivity.getSupportFragmentManager().findFragmentByTag(tag);
            if (info.fragment != null && !info.fragment.isDetached()) {
                FragmentTransaction ft = mActivity.getSupportFragmentManager().beginTransaction();
                ft.detach(info.fragment);
                ft.commit();
            }

            mTabs.put(tag, info);
            mTabHost.addTab(tabSpec);
        }

       // @Override
        public void onTabChanged(String tabId) {
            TabInfo newTab = mTabs.get(tabId);
            if (mLastTab != newTab) {
                FragmentTransaction ft = mActivity.getSupportFragmentManager().beginTransaction();
                if (mLastTab != null) {
                    if (mLastTab.fragment != null) {
                        ft.detach(mLastTab.fragment);
                    }
                }
                if (newTab != null) {
                    if (newTab.fragment == null) {
                        newTab.fragment = Fragment.instantiate(mActivity,
                                newTab.clss.getName(), newTab.args);
                        ft.add(mContainerId, newTab.fragment, newTab.tag);
                    } else {
                        ft.attach(newTab.fragment);
                    }
                }

                mLastTab = newTab;
                ft.commit();
                mActivity.getSupportFragmentManager().executePendingTransactions();
            }
        }
    }
    
    
    public Group getGroup() {
    	return tmpGroup;
    }

	public void onSuccess(BaseTask<?, ?, ?> task) {
		if (task instanceof WS05GetGroupDetails) {
			 WSGetMyGroupDetailsResult result_my_groups = ((WS05GetGroupDetails) task).getActualResult();
			if (result_my_groups.group != null) {
				tmpGroup = result_my_groups.group;
				//app.setNotificationCount(result_my_groups.update_count);
				refreshNotificationIndicator(result_my_groups.update_count, findViewById(R.id.btn_notification));
				setGrouptitle();
				refreshDetailTabs();
			}
		}
		if (task instanceof WS10SendMessage) {
			int iMsgSent = ((WS10SendMessage) task).getActualResult();
			//refresh the Group
			if (iMsgSent == 1)
				startTask(new WS05GetGroupDetails(this, var.getGroupcentricUserID(this), var.getGroupcentricAPIKey(this), tmpGroup.getId())); 
			else
				Toast.makeText(getApplicationContext(),	"Problem sending message..", Toast.LENGTH_SHORT).show();
		}
		
		if ( task instanceof WS19TogglePushNotificationsForGroup ) {
			int iTogglePushNotificationsonToggle = ((WS19TogglePushNotificationsForGroup) task).getActualResult();
			if (iTogglePushNotificationsonToggle==1) { // No need to call get detals since we know the status and it was succsessfull
				if (tmpGroup.getPushStatus().contentEquals("-1"))  //so update locally and refresh the tabs
					tmpGroup.setPushStatus("0");
				else
					tmpGroup.setPushStatus("-1");
				refreshDetailTabs();
			}
				
			else		
				Toast.makeText(getApplicationContext(), "Please try again..", Toast.LENGTH_SHORT).show();
		}
		
		if (task instanceof WS08RemoveGroup) {
			int iMyDeleteGroupResult = ((WS08RemoveGroup) task).getActualResult();
			if (iMyDeleteGroupResult==1) {
				finish();
			}
			else
				Toast.makeText(getApplicationContext(),	"Group not removed. Please try again", Toast.LENGTH_SHORT).show();
		}
		
		if (task instanceof WS07InviteMoreFriendsToGroup) {
			int iFriendInviteResult = ((WS07InviteMoreFriendsToGroup) task).getActualResult();
			
			if (iFriendInviteResult==1)  // Need to refresh group if added
				startTask(new WS05GetGroupDetails(this, var.getGroupcentricUserID(this), var.getGroupcentricAPIKey(this), tmpGroup.getId())); 
			else
				Toast.makeText(getApplicationContext(),"Could not add friend. Please try again", Toast.LENGTH_SHORT).show();
		}
		
		if (task instanceof WS09UpdateGroup) {
			int iMyUpdatedgroup = ((WS09UpdateGroup) task).getActualResult();
			if (iMyUpdatedgroup==1) //No need to call get deails again -  use locally stored var to update details
			{
				tmpGroup.setGroupname(updatedGroupName);
				setGrouptitle();
				startTask(new WS05GetGroupDetails(this, var.getGroupcentricUserID(this), var.getGroupcentricAPIKey(this), tmpGroup.getId())); //refreshDetailTabs();
			}
			else
				Toast.makeText(getApplicationContext(),"Could not update group. Please try again", Toast.LENGTH_SHORT).show();
		}
		
	}
	
	private void setGrouptitle() {
        ((TextView) findViewById(R.id.txt_group_title)).setText(tmpGroup.getGroupname()); 
	}
	
	private void refreshDetailTabs() {
		
		
		try {
			if (mTabHost.getCurrentTabTag().equalsIgnoreCase("Chat")) {
				gc_ChatListFragment chatFragment = (gc_ChatListFragment) this.getSupportFragmentManager().findFragmentByTag("Chat");
				if (chatFragment != null)
					//if (holdMessageCount==0 | holdMessageCount != tmpGroup.getMessages().size())
						chatFragment.refreshChatDetails();
						holdMessageCount = tmpGroup.getMessages().size();
			}
			
			if (mTabHost.getCurrentTabTag().equalsIgnoreCase("Friends")) {
				gc_FriendsListFragment groupfriendsFragment = (gc_FriendsListFragment) this.getSupportFragmentManager().findFragmentByTag(
								"Friends");
				if (groupfriendsFragment != null)
					groupfriendsFragment.refreshFriendsDetails();
			}
			
			if (mTabHost.getCurrentTabTag().equalsIgnoreCase("Details")) {
				gc_GroupDetailsFragment groupDetailsFragment = (gc_GroupDetailsFragment) this.getSupportFragmentManager().findFragmentByTag(
								"Details");
				if (groupDetailsFragment != null)
					groupDetailsFragment.refreshGroupDetails();
			}
		} catch (Exception ex) {
			Toast.makeText(this, ex.getMessage() + "..",Toast.LENGTH_SHORT).show();
		}
	}


	public void itemImageTapped(Message ms) {

        Bundle b = new Bundle();
        ArrayList<GCImageItem> gcImageItems = new ArrayList<GCImageItem>();
        gcImageItems.addAll(tmpGroup.getGCImageItems());
        b.putParcelableArrayList("group_pics", gcImageItems); 
        Intent i = new Intent(this, gc_PictureViewer.class);
        i.putExtras(b); 
        i.putExtra("SelectedPic",  ms.getImageurl());
        startActivity(i);
 		
	}


	public void mapImageTapped(Message ms) {
        try {
            String[] strLatLong = ms.getVar1().split(",");
            String strURL = "geo:0,0?q=" + strLatLong[0]+ ",+" + strLatLong[1] + "(" + ms.getUsername()+ ")";
            Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(strURL));
            startActivity(intent);
		} catch (Exception ex) {
			Toast.makeText(this, "Invalid map points",Toast.LENGTH_SHORT).show();
		}
		
	}

	public void websiteTapped(Message ms) {
        try {
        	String strURL = ms.getVar1();
            Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(strURL));
            startActivity(intent);
		} catch (Exception ex) {
			Toast.makeText(this, "Error loading website",Toast.LENGTH_SHORT).show();
		}
	}

	public void onClick(View v) {
		if (v.getId() == R.id.btn_chat) {
			handleTabPush(v);
		} else if (v.getId() == R.id.btn_friends) {
			handleTabPush(v);
		} else if (v.getId() == R.id.btn_details) {
			handleTabPush(v);
		}  else if (v.getId() == R.id.btn_back) {
			finish();
		} else if (v.getId() == R.id.btn_notification) {
            startActivity(new Intent(this, gc_NotificationsList.class));
		}
	}
	
	public void handleTabPush(View v) {
		if (tmpGroup==null)
			return;
		if (tmpGroup!=null & tmpGroup.getId()==0)
			return;
		if (v.getId() == R.id.btn_chat) {
			mTabHost.setCurrentTabByTag("Chat");
			handleTabIndicator("Chat");
		} else if (v.getId() == R.id.btn_friends) {
			mTabHost.setCurrentTabByTag("Friends"); 
			handleTabIndicator("Friends");
		} else if (v.getId() == R.id.btn_details) {
			mTabHost.setCurrentTabByTag("Details"); 
			handleTabIndicator("Details");
		}
	}
	
	public void handleTabIndicator(String Tab) {

		((ImageView) findViewById(R.id.btn_chat)).setImageDrawable(null);
		((ImageView) findViewById(R.id.btn_friends)).setImageDrawable(null);
		((ImageView) findViewById(R.id.btn_details)).setImageDrawable(null);
		 
		findViewById(R.id.btn_chat).setBackgroundResource(R.drawable.gc_btn_chat_not_selected);
		findViewById(R.id.btn_friends).setBackgroundResource(R.drawable.gc_btn_friends_not_selected);
		findViewById(R.id.btn_details).setBackgroundResource(R.drawable.gc_btn_details_not_selected);
		
		if (Tab.contentEquals("Chat")) {
			((ImageView) findViewById(R.id.btn_chat)).setBackgroundResource(R.drawable.gc_btn_chat);
		} else if (Tab.contentEquals("Friends")) {
			((ImageView) findViewById(R.id.btn_friends)).setBackgroundResource(R.drawable.gc_btn_friends);
		} else if (Tab.contentEquals("Details")) {
			((ImageView) findViewById(R.id.btn_details)).setBackgroundResource(R.drawable.gc_btn_details);
		}
	
	}

	public void sendMessage(String strMessage) {
		if (tmpGroup==null)
			return;
        
		int iMessageType = 0;
		String imageURL = "";
		String var1 = "";
		String varTitle="";
		String varSubtitle = "";
		String varDate = "";
		String varDetails = "";
		String varMarkup = "";
		//Check for shared url
		if (sharedURL.length()>0){
			iMessageType = 3;
			var1 = sharedURL;
		}
		if(msgSharedObjType > 0)
		{
			iMessageType = Integer.parseInt(msgSharedObj.getType());
			varTitle = msgSharedObj.getTitle();
			varSubtitle = msgSharedObj.getSubtitle();
			var1 = msgSharedObj.getURL();
			imageURL = msgSharedObj.getImageURL();
			varDate = msgSharedObj.getDateof();
			varDetails = msgSharedObj.getDetails();
			varMarkup = msgSharedObj.getMarkup();
		}
		
		if (gpsLat.length()>0 && gpsLon.length()>0){
			iMessageType = 2;
			var1 = gpsLat + "," + gpsLon;
		}
		
		startTask(new WS10SendMessage(this, var.getGroupcentricUserID(this), var.getGroupcentricAPIKey(this), tmpGroup.getId(), 
						strMessage, 
						iMessageType, imageURL, var1, varTitle, varSubtitle, varDate, varDetails, varMarkup));
		//Reset our shared URL - 
		sharedURL = "";
		//Reset our Lat\lon
		gpsLat = "";
		gpsLon = "";
		//Reset our shared object
		msgSharedObjType = 0;
	}

	public void toggleGroupPush() {
		if (tmpGroup==null)
			return;
		if (tmpGroup.getPushStatus().contentEquals("-1"))
			startTask(new WS19TogglePushNotificationsForGroup(this,var.getGroupcentricUserID(this), var.getGroupcentricAPIKey(this),tmpGroup.getId(),1));
		else
			startTask(new WS19TogglePushNotificationsForGroup(this,var.getGroupcentricUserID(this), var.getGroupcentricAPIKey(this),tmpGroup.getId(),0));
	}
	
	public void addFriendToGroup() {
		if (tmpGroup==null)
			return;
		bRefreshOnResume = false;
		startActivityForResult(new Intent(this.getBaseContext(),gc_ContactList.class), RC_PICK_CONTACT);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			if (requestCode == RC_PICK_CONTACT) {
				 ArrayList<Friend> selectedContacts = data.getParcelableArrayListExtra("EXTRA_CONTACTS" );
				 String strNameNumber = "";
			        for (int i = 0; i < selectedContacts.size(); i++) {	
			            strNameNumber = strNameNumber + selectedContacts.get(i).getFullname() + ";" + selectedContacts.get(i).getPhone() + ",";
			        }
			        if (strNameNumber.length()>0)
			        	startTask(new WS07InviteMoreFriendsToGroup(this, var.getGroupcentricUserID(this), var.getGroupcentricAPIKey(this),tmpGroup.getId(),strNameNumber));
			}
			
			if (requestCode == RC_UPDATE_TITLE) {

				try{
				updatedGroupName = data.getStringExtra("group_name");
				startTask(new WS09UpdateGroup(this, var.getGroupcentricUserID(this), var.getGroupcentricAPIKey(this), tmpGroup.getId(), updatedGroupName, tmpGroup.getGroupimage()));
				}
				catch (Exception ex){
					
				}
			
				}
			
			if (requestCode==RC_INSERT_CHAT_ATTACHMENT){
				//Handle Picture Selection
					if (data.getIntExtra("attchment_type", 0)==RC_SELECT_PICTURE)
						selectChatPicture();
					else if (data.getIntExtra("attchment_type", 0)== RC_SET_LOCATION)
						selectGetGPS(); 
					else if (data.getIntExtra("attchment_type", 0)== RC_TAKE_PICTURE)
						takeCameraPhoto(RC_TAKE_CHAT_PICTURE); 
					else if (data.getIntExtra("attchment_type", 0)==RC_SHARE_LINK)
						shareWebLink();
				}
			
			if (requestCode==RC_GET_GPS){
				gpsLat = data.getStringExtra("gps_lat");
				gpsLon = data.getStringExtra("gps_lon");
				
				gc_ChatListFragment chatFragment = (gc_ChatListFragment) this.getSupportFragmentManager().findFragmentByTag("Chat");
				if (chatFragment != null)
				{
					try {
						chatFragment.locationInserted(gpsLat +","+ gpsLon);
					}
					catch (Exception ex){
						Toast.makeText(this,"Problem with gps" + ex.getMessage(), Toast.LENGTH_SHORT).show();
					}
				}
			}
			
            if (requestCode==RC_CHANGE_GROUP_PICTURE) {
				//Handle Picture Selection
				if (data.getIntExtra("attchment_type", 0)==RC_SELECT_PICTURE)
					selectGroupPictureFromGallery();
				else if (data.getIntExtra("attchment_type", 0)== RC_TAKE_PICTURE)
					takeCameraPhoto(RC_TAKE_GROUP_PICTURE); 	
            }
			
			
			if (requestCode ==RC_SELECT_CHAT_PICTURE) { 
				//  Chat Picture Selected
				gc_ChatListFragment chatFragment = (gc_ChatListFragment) this.getSupportFragmentManager().findFragmentByTag("Chat");
				if (chatFragment != null)
				{
					try {
						Uri tmp = data.getData();
						imageUri = data.getData();
						chatFragment.picutreInserted(imageUri);
					}
					catch (Exception ex){
					}
				}
			}
			
			if (requestCode ==RC_TAKE_CHAT_PICTURE) {
				gc_ChatListFragment chatFragment = (gc_ChatListFragment) this.getSupportFragmentManager().findFragmentByTag("Chat");
				if (chatFragment != null)
				{
					try {
						//
					
                    File dir = Environment.getExternalStorageDirectory();
                    GroupcentricReadWritePrefs rwPrefs = new GroupcentricReadWritePrefs();
                    String strPhotoFileName = rwPrefs.readWriteUserSetting(this, "Read", "photo_filename", "");
                    File yourFile = new File(dir + File.separator + gc_Variables.gcImageDir, strPhotoFileName);
                    if (!yourFile.exists()) {
                    	Toast.makeText(this,"Error retrieving photo", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    imageUri = Uri.fromFile(yourFile);

						chatFragment.picutreInserted(imageUri);
					}
					catch (Exception ex){
						Toast.makeText(this,"Error "+ ex.getMessage(), Toast.LENGTH_SHORT).show();
					}
				}
				
				
			}
			
			if (requestCode==RC_SELECT_GROUP_PICTURE) {
				try {
					uploadGroupPic(data.getData());
				}
				catch (Exception ex){
					Toast.makeText(this,"Cant Load Image" + ex.getMessage(), Toast.LENGTH_SHORT).show();
				}
			}
			
			if (requestCode==RC_TAKE_GROUP_PICTURE) {
				try {
					File dir = Environment.getExternalStorageDirectory();
					GroupcentricReadWritePrefs rwPrefs = new GroupcentricReadWritePrefs();
					String strPhotoFileName = rwPrefs.readWriteUserSetting(
							this, "Read", "photo_filename", "");
					File yourFile = new File(dir + File.separator + gc_Variables.gcImageDir, strPhotoFileName);
					if (!yourFile.exists()) {
						Toast.makeText(this,"Error retrieving photo", Toast.LENGTH_SHORT).show();
						return;
					}
					imageUri = Uri.fromFile(yourFile);
					uploadGroupPic(imageUri);
				 	imageUri=null;
				} catch (Exception ex) {
					Toast.makeText(this,"Error "+ ex.getMessage(), Toast.LENGTH_SHORT).show();
				}

			}

			if (requestCode == UPLOADED_IMAGE) {
				int iUploadStatus = data.getExtras().getInt("upload_complete",0);
				if (iUploadStatus ==1)
					refreshGroup();
			}
		}
		else // Result was not OK - We still want to refresh chat if coming back from adding friends or updating title
		{
			if (requestCode == RC_PICK_CONTACT)  
				refreshGroup();
			
			if (requestCode == RC_UPDATE_TITLE) 
				refreshGroup();	
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	

	@Override
	public boolean shouldShowProgress() {
		boolean showProgress = false;  // NO FULL progress if in chat details 
		if (tmpGroup == null)   //  We want to progress showing when geting initial group details.
		{
			showProgress = true;
			return showProgress;
		}
		
		if (mTabHost.getCurrentTabTag().equalsIgnoreCase("Chat")) { 
			if (taskHelper.getTask().isInProgress())
			{
				((ProgressBar) findViewById(R.id.gc_prog_chat_details)).getIndeterminateDrawable().setColorFilter(Color.parseColor(getResources().getString(R.color.gc_font_white)), android.graphics.PorterDuff.Mode.MULTIPLY); 
				findViewById(R.id.gc_prog_chat_details).setVisibility(View.VISIBLE);
			}
			//else
				//findViewById(R.id.gc_prog_chat_details).setVisibility(View.GONE);
				
		}
		if (!taskHelper.getTask().isInProgress()) findViewById(R.id.gc_prog_chat_details).setVisibility(View.GONE);
		if (mTabHost.getCurrentTabTag().equalsIgnoreCase("Friends"))  //full progress when updating from friends 
			showProgress = true;
		if (mTabHost.getCurrentTabTag().equalsIgnoreCase("Details"))  // Full progress when updating from details
			showProgress = true;
		
		return showProgress;
	}

    public void removeGroup() {
		if (tmpGroup==null)
			return;
        final int myGroupID = tmpGroup.getId();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Leave " + tmpGroup.getGroupname() + "?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        //Add Remove Group Functionality
                    	startTask(new WS08RemoveGroup(gc_GroupDetails.this, var.getGroupcentricUserID(gc_GroupDetails.this), var.getGroupcentricAPIKey(gc_GroupDetails.this), myGroupID));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }


	public void editTitle() {
		if (tmpGroup==null)
			return;
		bRefreshOnResume = false;
		startActivityForResult(new Intent(this, gc_EditGroupName.class)
        .putExtra("group_name",tmpGroup.getGroupname()), 
         RC_UPDATE_TITLE
         );
		
	}

	public void insertAttachment() {
		if (tmpGroup==null)
			return;
		startActivityForResult(new Intent(this, gc_AttachItem.class)
		.putExtra("select_for_type", "chat")
		,RC_INSERT_CHAT_ATTACHMENT
         );
		
	}
	
	public void selectChatPicture() {
		Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
		photoPickerIntent.setType("image/*");
		startActivityForResult(photoPickerIntent, RC_SELECT_CHAT_PICTURE);
	}
    
	public void selectGetGPS() {
		startActivityForResult(new Intent(this, gc_GPS.class)
		,RC_GET_GPS
         );
		
	}

	public String getAttachedURL() {
		return sharedURL;
	}
	
	public MessageContent getSharedObject() {
		return msgSharedObj;
	}
	public int getSharedObjectType() {
		return msgSharedObjType;
	}
	
	public Uri getAttachedImaged() {
		return imageUri;
	}


	public void removeAttachment() { 
		imageUri = null;
		sharedURL = "";
		gpsLat = "";
		gpsLon = "";
		msgSharedObjType = 0;
	}


	public void changeGroupPic() {
		if (tmpGroup==null)
			return;
		startActivityForResult(new Intent(this, gc_AttachItem.class)
		.putExtra("select_for_type", "group")
		,RC_CHANGE_GROUP_PICTURE
         );
	}
	
	public void selectGroupPictureFromGallery() {
		Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
		photoPickerIntent.setType("image/*");
		startActivityForResult(photoPickerIntent, RC_SELECT_GROUP_PICTURE);
	}
	private void uploadGroupPic(Uri imageUri) {
	   Intent intent = new Intent(this, gc_UploadImage.class); 
 	   intent.putExtra("imageUri", imageUri);
 	   intent.putExtra("msg", "");
 	   intent.putExtra("imagetype", "G");
 	   intent.putExtra("groupid",tmpGroup.getId());
 	   startActivityForResult(intent, UPLOADED_IMAGE);
 	  imageUri=null;
	}
	
	public void refreshGroup() {
		try
		{
			if((tmpGroup != null) && (tmpGroup.getId() > 0))
					startTask(new WS05GetGroupDetails(this, var.getGroupcentricUserID(this), var.getGroupcentricAPIKey(this), tmpGroup.getId())); 
		}
		catch(Exception ex){}
	}


	public void eventTapped(Message ms) {
        startActivity(new Intent(this, gc_Type4Details.class)
        .putExtra("event_message", ms)
        		);
	}


	public String getAttachedLatLon() {
		String latLon = "";
		if (gpsLat.length()>0 && gpsLon.length()>0)
			latLon = gpsLat+"," + gpsLon;
		return latLon;
	}


	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event){
    	final EditText myInputField = (EditText) findViewById(R.id.edt_message);
        // this will happen on first key pressed on hard-keyboard only. Once myInputField 
        // gets the focus again, it will automatically receive further key presses.
    	if (myInputField!=null) //
    	{
	        if (!myInputField.hasFocus()){ 
	            myInputField.requestFocus();
	            myInputField.onKeyDown(keyCode, event);
	        }
    	}
	    return super.onKeyDown(keyCode, event);
	}
	
	public void refreshNotificationIndicator(int count,View vNotification) {
		if (count>0)
			vNotification.setBackgroundResource(R.drawable.gc_notifications_active);
		else
			vNotification.setBackgroundResource(R.drawable.gc_notifications_inactive);	
	}
	
	private void takeCameraPhoto(int iTakePicureForChatGroup) {
		// iTakePicureForChatGroup  = RC_TAKE_CHAT_PICTURE - Taking a chat picture
		// 							 RC_TAKE_GROUP_PICTURE - Taking a group picture 
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)) {

		} else {
			Toast.makeText(gc_GroupDetails.this,"Can not currently access your SD card ", Toast.LENGTH_LONG)
					.show();
			return;
		}
		
		if (checkGCDirectory()==false) {
			return;
		}
		File imageDirectory = new File(Environment.getExternalStorageDirectory() + File.separator + gc_Variables.gcImageDir);//File imageDirectory = new File("/sdcard");
		String path = imageDirectory.toString().toLowerCase();
		String name = imageDirectory.getName().toLowerCase();
		ContentValues values = new ContentValues();
		values.put(MediaStore.Images.Media.TITLE, "Image");
		values.put(MediaStore.Images.Media.BUCKET_ID, path.hashCode());
		values.put(MediaStore.Images.Media.BUCKET_DISPLAY_NAME, name);
		values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
		values.put(MediaStore.Images.Media.DESCRIPTION,"Image capture by camera");
		String strFileName = "GROUPcentric_" + getDateTime() + "_tmp.jpg";
		GroupcentricReadWritePrefs rwPrefs = new GroupcentricReadWritePrefs();
		rwPrefs.readWriteUserSetting(gc_GroupDetails.this, "Write","photo_filename", strFileName);

		values.put("_data",Environment.getExternalStorageDirectory() + File.separator + gc_Variables.gcImageDir + File.separator  + strFileName);
		Uri imageCameraUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
		Intent i = new Intent("android.media.action.IMAGE_CAPTURE");
		i.putExtra(MediaStore.EXTRA_OUTPUT, imageCameraUri);
		startActivityForResult(i, iTakePicureForChatGroup);
		imageCameraUri = null;
    }

	private void shareWebLink() {
		Intent iShareWebLink = new Intent(this, gc_ShareWebLink.class); 
			iShareWebLink.putExtra("group_id", tmpGroup.getId());
	        startActivityForResult(iShareWebLink, RC_SHARE_WEB_LINK_RESULT);		
	}
	
    private  final static String getDateTime()
    {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd_hh_mm_ss");
        df.setTimeZone(TimeZone.getTimeZone("EST"));
        return df.format(new Date());
    }
    
    private BroadcastReceiver uaPushReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            String strMsg = (String) bundle.get("msg");  //What was passed
            String strExtra = (String) bundle.get("extra");
            int iNotificationID = bundle.getInt("Notification_ID");

            //Something came in so refresh the group -
            refreshGroup();
            String ns = gc_GroupDetails.NOTIFICATION_SERVICE;
            NotificationManager mNotificationManager = (NotificationManager) getSystemService(ns);
            mNotificationManager.cancel(iNotificationID);
        }
    };
    
    @Override
    protected void onResume() {
    	super.onResume();
        if (bRefreshOnResume)
        	refreshGroup();
        else
        {
        	bRefreshOnResume = true;
        }
        registerReceiver(uaPushReceiver, new IntentFilter(var.IncomingGCPushMSG));
    } 
    
    @Override
    protected void onPause() {
        super.onPause();
        try {
            unregisterReceiver(uaPushReceiver);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    private boolean checkGCDirectory() {
		File folder = new File(Environment.getExternalStorageDirectory() + File.separator + gc_Variables.gcImageDir);
		boolean bHasGCDirectory = false;
		folder.delete();  //This will delete the directory if empty
		if (!folder.exists()) {
		    folder.mkdir();
		}
	
		if (folder.exists()) {
			bHasGCDirectory = true;
		} else {
			Toast.makeText(gc_GroupDetails.this,"Error creating directory ", Toast.LENGTH_SHORT).show();
			bHasGCDirectory = false;
		}
		return bHasGCDirectory;
    }
    
    public boolean isChatUpdateing() {
    	boolean chatUpdating = false;
    	if (findViewById(R.id.gc_prog_chat_details).getVisibility()==View.VISIBLE)
    		chatUpdating = true;
    		return chatUpdating;
    }
    
    public void DEVELOPER_FUNCTION_FROM_CUSTOM_OBJECT_CLICK_IN_gc_GroupChatAdapter(Message ms) {
		try {
			String YOUR_CLASS_PACKAGE_NAME = "your.package.class";
			Intent intent = new Intent(this, this.getApplication().getClass().getClassLoader()
					.loadClass(YOUR_CLASS_PACKAGE_NAME));
			//use the Message object and pass any extras you need from it to your class
			 //for example this shows how to pass the title, var1, and object date
			  		
			intent.putExtra("title", ms.getVar_title());
			intent.putExtra("Var1", ms.getVar1());
			intent.putExtra("ObjectDate", ms.getVar_date());
            if (ms.getVar1()!=null & ms.getVar1().length()>0)
			startActivity(intent);
			
		} catch (ClassNotFoundException e) {
			Toast.makeText(this, "Class not found..", Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		}
	}
    
}