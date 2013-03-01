package com.groupcentric.android;

/*
 * This is the main fragment in the sdk.
 * If a user isn't a Groupcentric user and this fragment is called, it will display the signup process.
 * This fragment also attaches another layout belowgroupslist.xml which contains a Start Group button and 
 * links to the users Groupcentric profile and TOS/Privacy.
 * 
 * This fragment is called from both a normal groups list activity and the Share Selector.
 * When it is called from the ShareSelector we set a boolean variable to denote that because
 * things have different behaviors like tapping on a group.
 * 
 * This fragment extends BaseTaskGCListFragment in the GroupcentricAPI jar which extends ListFragment.
 * The BaseTaskGCListFragment also handles web service tasks like get groups for example that
 * you see called below.
 * 
 */

import java.util.ArrayList;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.util.Log;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TextView.OnEditorActionListener;
import com.groupcentric.api.BaseTaskGCListFragment;
import com.groupcentric.api.model.Group;
import com.groupcentric.api.ws.BaseTask;
import com.groupcentric.api.ws.WS01RegisterUserStart;
import com.groupcentric.api.ws.WS02RegisterUserFinish;
import com.groupcentric.api.ws.WS04GetGroups;
import com.groupcentric.api.ws.WS08RemoveGroup;
import com.groupcentric.api.ws.WS10SendMessage;
import com.groupcentric.api.ws.WS11RequestPasscode;
import com.groupcentric.api.ws.WSGetMyGroupsResult;
import com.groupcentric.model.MessageContent;
import com.groupcentric.util.Util;
import com.urbanairship.push.PushManager;
import com.urbanairship.push.PushPreferences;

public class gc_GroupsListFragment extends BaseTaskGCListFragment implements View.OnClickListener{
 
	@Override
	public void onPause() {
        try {
            getActivity().unregisterReceiver(uaPushReceiver);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
		super.onPause();
	}

	private gc_GroupListAdapter m_adapter;
	private gc_Variables var;
	protected static final int CONTEXT_CLICK = Menu.FIRST; 
	private ImageButton imgSignup_GetStarted;
	private ImageButton imgSignup_Register;
	private ImageButton imgSignup_VerifyPhone;
	private EditText edtUsername, edtUserphone, edtPin;
	public boolean activityFromShareSelector = false;
	private boolean footerAdded = false;
	private boolean screenRestored = false;
	
	//handle objects being shared into an existing group or starting a group with existing object
	private int sharedObjectType = 0;
	private int sharedObjectToExistingGroupID = 0;
	private MessageContent msgSharedObject;
	
	//notifications button - toggle active/inactive pending updatecounts
	GroupcentricNotificationsButtonListener notificationsCallback;

    // Container Activity must implement this interface
    public interface GroupcentricNotificationsButtonListener {
        public void onGroupcentricNotificationsCheck(int updatecount);
    }
    
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        
        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            notificationsCallback = (GroupcentricNotificationsButtonListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement GroupcentricNotificationsButtonListener");
        }
    }
    
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		
		
		if(var.getGroupcentricUserID(getActivity()) == 0)
		{
			//else user is not yet a Groupcentric user so setup and display the signup process
			//Need to see if screen has been restored from previous state
				((LinearLayout) getActivity().findViewById(R.id.layout_signup_view1)).setVisibility(View.VISIBLE);
				((LinearLayout) getActivity().findViewById(R.id.layout_signup_view2)).setVisibility(View.GONE);
				((LinearLayout) getActivity().findViewById(R.id.layout_signup_view2)).setVisibility(View.GONE);


			getActivity().findViewById(R.id.txt_learnmore).setOnClickListener(this);
			imgSignup_GetStarted = (ImageButton) getActivity().findViewById(R.id.imgSignup_GetStarted);
			imgSignup_GetStarted.setOnClickListener(this);
			getActivity().findViewById(R.id.txt_learnmore).setOnClickListener(this);
			
	        edtUsername = (EditText) getActivity().findViewById(R.id.edt_signup_name);
	        edtUserphone = (EditText) getActivity().findViewById(R.id.edt_signup_phone);
	        edtUserphone.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
	        
	        edtUserphone.setOnEditorActionListener(new OnEditorActionListener(){  
				public boolean onEditorAction(TextView v, int actionId,
						KeyEvent event) {
			
					if (EditorInfo.IME_ACTION_DONE==actionId)
					{
						onClick(getActivity().findViewById(R.id.imgSignup_Register));
					}
					return false;
				} 

	         }); 
	        
	        imgSignup_Register = (ImageButton) getActivity().findViewById(R.id.imgSignup_Register);
	        imgSignup_Register.setOnClickListener(this);
	        
	        getActivity().findViewById(R.id.txtnotnumber).setOnClickListener(this);
	        getActivity().findViewById(R.id.txtresendpin).setOnClickListener(this);
	        
	        edtPin = (EditText) getActivity().findViewById(R.id.edt_pin);
	        edtPin.setOnEditorActionListener(new OnEditorActionListener(){  
				public boolean onEditorAction(TextView v, int actionId,
						KeyEvent event) {
			
					if (EditorInfo.IME_ACTION_DONE==actionId)
					{
						onClick(getActivity().findViewById(R.id.imgSignup_VerifyPhone));
					}
					return false;
				} 

	         }); 
	        imgSignup_VerifyPhone = (ImageButton) getActivity().findViewById(R.id.imgSignup_VerifyPhone);
			imgSignup_VerifyPhone.setOnClickListener(this);
			getListView().setVisibility(View.GONE);
			if(activityFromShareSelector)
			   ((LinearLayout) getActivity().findViewById(R.id.layout_sdkfragment_notificationsbutton)).setVisibility(View.GONE);
		
		}
		if (savedInstanceState != null) {
		//Restore visibility states
			getActivity().findViewById(R.id.layout_signup_view1).setVisibility(savedInstanceState.getInt("lyt_signup1", View.VISIBLE));	
			getActivity().findViewById(R.id.layout_signup_view2).setVisibility(savedInstanceState.getInt("lyt_signup2", View.VISIBLE));	
			getActivity().findViewById(R.id.layout_signup_view3).setVisibility(savedInstanceState.getInt("lyt_signup3", View.VISIBLE));	
			screenRestored=true;
		}

		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		//save the visibility states of layouts
		outState.putInt("lyt_signup1", getActivity().findViewById(R.id.layout_signup_view1).getVisibility());
		outState.putInt("lyt_signup2", getActivity().findViewById(R.id.layout_signup_view2).getVisibility());
		outState.putInt("lyt_signup3", getActivity().findViewById(R.id.layout_signup_view3).getVisibility());
		super.onSaveInstanceState(outState);
	}
    
	@Override
	public void onResume() {
		super.onResume();
		Log.i("here","here1");
		getActivity().registerReceiver(uaPushReceiver, new IntentFilter(var.IncomingGCPushMSG));
		
		if(var.getGroupcentricUserID(getActivity()) == 0 & getListView().getVisibility()==View.VISIBLE)
		{
			Log.i("here","here2");
			//We make it here if user logged out of GC -  So restart signup and hide Gridview
				((LinearLayout) getActivity().findViewById(R.id.layout_signup_view1)).setVisibility(View.VISIBLE);
				((LinearLayout) getActivity().findViewById(R.id.layout_signup_view2)).setVisibility(View.GONE);
				((LinearLayout) getActivity().findViewById(R.id.layout_signup_view2)).setVisibility(View.GONE);
				getActivity().findViewById(R.id.txt_learnmore).setOnClickListener(this);
				imgSignup_GetStarted = (ImageButton) getActivity().findViewById(R.id.imgSignup_GetStarted);
				imgSignup_GetStarted.setOnClickListener(this);
				getActivity().findViewById(R.id.txt_learnmore).setOnClickListener(this);
				
		        edtUsername = (EditText) getActivity().findViewById(R.id.edt_signup_name);
		        edtUserphone = (EditText) getActivity().findViewById(R.id.edt_signup_phone);
		        edtUserphone.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
		        
		        edtUserphone.setOnEditorActionListener(new OnEditorActionListener(){  
					public boolean onEditorAction(TextView v, int actionId,
							KeyEvent event) {
				
						if (EditorInfo.IME_ACTION_DONE==actionId)
						{
							onClick(getActivity().findViewById(R.id.imgSignup_Register));
						}
						return false;
					} 

		         }); 
		        
		        imgSignup_Register = (ImageButton) getActivity().findViewById(R.id.imgSignup_Register);
		        imgSignup_Register.setOnClickListener(this);
		        
		        getActivity().findViewById(R.id.txtnotnumber).setOnClickListener(this);
		        getActivity().findViewById(R.id.txtresendpin).setOnClickListener(this);
		        
		        edtPin = (EditText) getActivity().findViewById(R.id.edt_pin);
		        edtPin.setOnEditorActionListener(new OnEditorActionListener(){  
					public boolean onEditorAction(TextView v, int actionId,
							KeyEvent event) {
				
						if (EditorInfo.IME_ACTION_DONE==actionId)
						{
							onClick(getActivity().findViewById(R.id.imgSignup_VerifyPhone));
						}
						return false;
					} 

		         }); 
		        imgSignup_VerifyPhone = (ImageButton) getActivity().findViewById(R.id.imgSignup_VerifyPhone);
				imgSignup_VerifyPhone.setOnClickListener(this);
				getListView().setVisibility(View.GONE);
				if(activityFromShareSelector)
					((LinearLayout) getActivity().findViewById(R.id.layout_sdkfragment_notificationsbutton)).setVisibility(View.GONE);
		}
		
		if (edtUserphone!=null & getActivity().findViewById(R.id.txtpindesc) !=null)
			((TextView) getActivity().findViewById(R.id.txtpindesc)).setText(getResources().getString(R.string.gc_enter_pin_desc) + " " + edtUserphone.getText().toString()); 

		// if the user has a Groupcentric account (id > 0) then get their groups to display
		if(var.getGroupcentricUserID(getActivity()) > 0)
		{
			Log.i("here","here3");
			((LinearLayout) getActivity().findViewById(R.id.layout_signup_view1)).setVisibility(View.GONE);
			((LinearLayout) getActivity().findViewById(R.id.layout_signup_view2)).setVisibility(View.GONE);
			((LinearLayout) getActivity().findViewById(R.id.layout_signup_view2)).setVisibility(View.GONE);
			getListView().setVisibility(0);
			getMyGroups();
			registerForContextMenu(getListView());
		}
	}
 
    private BroadcastReceiver uaPushReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            getMyGroups(); //Need to refresh groups because an alert came in
        }
    };    
	
    @Override  //this gets called first with fragments and is responsible for inflating and returning the main view of the fragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
    	var = new gc_Variables(); //finish instantiating the GroupcentricVariables object which will give access to the Groupcentric UserID and APIKey
        View myFragmentView = inflater.inflate(R.layout.gc_groupslistfragment, container, false);
        return myFragmentView;
    }

    //web service results
    public void onSuccess(BaseTask<?, ?, ?> task) {
    	if (task instanceof WS01RegisterUserStart) {
			int iRegiserStart = ((WS01RegisterUserStart) task).getActualResult();
			if (iRegiserStart==1) // Valid start of registration 
			{
				((TextView) getActivity().findViewById(R.id.txtpindesc)).setText(getResources().getString(R.string.gc_enter_pin_desc) + " " + edtUserphone.getText().toString()); 
				((LinearLayout) getActivity().findViewById(R.id.layout_signup_view2)).setVisibility(View.GONE);
				((LinearLayout) getActivity().findViewById(R.id.layout_signup_view3)).setVisibility(View.VISIBLE);
			}
			else  //Problem starting registration
        		Toast.makeText(getActivity(), getResources().getString(R.string.gc_registration_problem), Toast.LENGTH_SHORT).show();
		} else if (task instanceof WS08RemoveGroup) { //User Removed Group
			int iRemoveMyResult = ((WS08RemoveGroup) task).getActualResult();
			if (iRemoveMyResult==1) {
				Toast.makeText(getActivity(),	getResources().getString(R.string.gc_remove_pass), Toast.LENGTH_SHORT).show();
				getMyGroups();
			}
			else
				Toast.makeText(getActivity(),	getResources().getString(R.string.gc_remove_fail), Toast.LENGTH_SHORT).show();
		} 	else if (task instanceof WS04GetGroups & getActivity() !=null) {
    		//groups returned, bind them to the list using the GroupListAdapter
			WSGetMyGroupsResult resultMygroups = ((WS04GetGroups) task).getActualResult();
			 //add footer to bottom of groups list, footer contains start group button and profile link
			if(!footerAdded)
			{
				View footer = ((LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.gc_belowgroupslist, null, false);
				getListView().addFooterView(footer);
				ImageButton btnStartGroup = (ImageButton)footer.findViewById(R.id.imgStartGroup);
				btnStartGroup.setOnClickListener(new OnClickListener() 
				{
					@Override
					public void onClick(View v) {
						if(activityFromShareSelector)
						{
							if(sharedObjectType == 1)
							{
								startActivity(new Intent(getActivity(), gc_StartGroup.class)
								.putExtra("ObjectType",Integer.toString(sharedObjectType))
								.putExtra("ObjectImage", msgSharedObject.getImageURL())
								.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
							}
							else if(sharedObjectType == 3)
							{
								startActivity(new Intent(getActivity(), gc_StartGroup.class)
								.putExtra("ObjectType",Integer.toString(sharedObjectType))
								.putExtra("ObjectTitle", msgSharedObject.getTitle())
								.putExtra("ObjectImage", msgSharedObject.getImageURL())
								.putExtra("ObjectURL", msgSharedObject.getURL())
								.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
							}
							else if(sharedObjectType == 4)
							{
								startActivity(new Intent(getActivity(), gc_StartGroup.class)
								.putExtra("ObjectType",Integer.toString(sharedObjectType))
								.putExtra("ObjectTitle", msgSharedObject.getTitle())
								.putExtra("ObjectSubtitle", msgSharedObject.getSubtitle())
								.putExtra("ObjectImage", msgSharedObject.getImageURL())
								.putExtra("ObjectDate", msgSharedObject.getDateof())
								.putExtra("ObjectDetails", msgSharedObject.getDetails())
								.putExtra("ObjectURL", msgSharedObject.getURL())
								.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
							}
							else if(sharedObjectType >= 100)
							{
								startActivity(new Intent(getActivity(), gc_StartGroup.class)
								.putExtra("ObjectType",Integer.toString(sharedObjectType))
								.putExtra("ObjectTitle", msgSharedObject.getTitle())
								.putExtra("ObjectSubtitle", msgSharedObject.getSubtitle())
								.putExtra("ObjectImage", msgSharedObject.getImageURL())
								.putExtra("ObjectDate", msgSharedObject.getDateof())
								.putExtra("ObjectDetails", msgSharedObject.getDetails())
								.putExtra("ObjectURL", msgSharedObject.getURL())
								.putExtra("ObjectMarkup", msgSharedObject.getMarkup())
								.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
							}
						}
						else
						{
							startActivity(new Intent(getActivity(), gc_StartGroup.class));
						}
					}
				});
				TextView profileLink = (TextView) footer.findViewById(R.id.txtGCProfile);
				profileLink.setOnClickListener(new OnClickListener() 
				{
					@Override
					public void onClick(View v) {
						startActivity(new Intent(getActivity(), gc_Profile.class));
					}
				});
				footerAdded = true;
			} 
		     
			 this.m_adapter = new gc_GroupListAdapter(getActivity(), R.layout.gc_row_group1,	(ArrayList<Group>) resultMygroups.my_groups);
			 setListAdapter(this.m_adapter);
			 
			 //if user doesnt have any groups
			 if (resultMygroups.my_groups.size() == 0) {
				 getActivity().findViewById(R.id.txtNoGroupsMessage).setVisibility(View.VISIBLE);
			 }
			 else
			 {
				 getActivity().findViewById(R.id.txtNoGroupsMessage).setVisibility(View.GONE);
			 }
			 //some small ui tweaks if displaying this fragment in the Share Selector
			 if(activityFromShareSelector)
			 {
					getActivity().findViewById(R.id.txtGCProfile).setVisibility(View.GONE);
			 }
			 
			 //update in-app notifications button to active if updates > 0
			 notificationsCallback.onGroupcentricNotificationsCheck(resultMygroups.update_count);
			 
		
		}
    	//if user requests the PIN to be sent again in the signup process
    	else if (task instanceof WS11RequestPasscode) {
			int iRequestPasscode = ((WS11RequestPasscode) task).getActualResult();
			if (iRequestPasscode==1) // passcode succsefully sent according to server 
				Toast.makeText(getActivity(), getResources().getString(R.string.gc_resend_pin_pass), Toast.LENGTH_SHORT).show();
			else // passcode was not sent
        		Toast.makeText(getActivity(), getResources().getString(R.string.gc_resend_pin_fail), Toast.LENGTH_SHORT).show();
		}
		
		//Registration finish result
    	else if (task instanceof WS02RegisterUserFinish) {
			int iRegUserGCID = ((WS02RegisterUserFinish) task).getActualResult();
			if (iRegUserGCID > 0) { // > 0 is a valid GCuserid
				//set the users new Groupcentric User ID in the preferences
				var.setGroupcentricUserID(getActivity(),iRegUserGCID);
				String strUAToken = "";
				
				try {
					PushManager.enablePush();
					PushPreferences prefs = PushManager.shared().getPreferences();
					if (prefs.getPushId() != null)
						strUAToken = prefs.getPushId();
					}
				catch(ExceptionInInitializerError ex){}
        		
        		if (strUAToken.length()>0) {
        			Intent iUpdateTokenService = new Intent(getActivity(), gc_UpdateUATokenService.class);
        			getActivity().startService(iUpdateTokenService);
        		}

    			
                ((LinearLayout) getActivity().findViewById(R.id.layout_signup_view3)).setVisibility(View.GONE);
    			getMyGroups(); //user may have groups so load them up
    			getListView().setVisibility(0);
    			if(!activityFromShareSelector)
    			((LinearLayout) getActivity().findViewById(R.id.layout_sdkfragment_notificationsbutton)).setVisibility(View.VISIBLE);
			}
            else
            	Toast.makeText(getActivity(),getResources().getString(R.string.gc_join_failed),Toast.LENGTH_SHORT).show();
		}
    	
    	//shared an object into an existing group from outside groups
    	else if (task instanceof WS10SendMessage) {
    		startActivity(new Intent(getActivity(), gc_GroupDetails.class)
			.putExtra("groupid",sharedObjectToExistingGroupID)
			.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
    	}
	}
	
	private void getMyGroups() {
		startTask(new WS04GetGroups(this, var.getGroupcentricUserID(getActivity()), var.getGroupcentricAPIKey(getActivity())));
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.imgSignup_GetStarted) {
			((LinearLayout) getActivity().findViewById(R.id.layout_signup_view1)).setVisibility(View.GONE);
			((LinearLayout) getActivity().findViewById(R.id.layout_signup_view2)).setVisibility(View.VISIBLE);
		}
		else if (v.getId() == R.id.imgSignup_Register) {
			if (hasValidPhoneAndName())
				startTask(new WS01RegisterUserStart( this,  Util.getOnlyNumerics(edtUserphone.getText().toString()),var.getGroupcentricAPIKey(getActivity())));
		}
		else if (v.getId() == R.id.imgSignup_VerifyPhone) {
			if (hasEnteredPin())
				finishRegistration();
		}else if (v.getId() == R.id.txtnotnumber) {
			//Not users phone number go back to previous activity (finish) to allow user to reenter
			((LinearLayout) getActivity().findViewById(R.id.layout_signup_view3)).setVisibility(View.GONE);
			((LinearLayout) getActivity().findViewById(R.id.layout_signup_view2)).setVisibility(View.VISIBLE);
		} else if (v.getId() == R.id.txtresendpin) {
			startTask(new WS11RequestPasscode( this, Util.getOnlyNumerics(edtUserphone.getText().toString()),var.getGroupcentricAPIKey(getActivity())));
		}else if (v.getId() == R.id.imgStartGroup) {
			startActivity(new Intent(getActivity(), gc_StartGroup.class));
		}
		else if (v.getId() == R.id.txt_learnmore)
		{
			openWebiew(this.getResources().getString(R.string.gc_profile_tos),"Groupcentric TOS");
		}
	}
	
	private void openWebiew(String webURL, String header) {
        startActivity(new Intent(getActivity(), gc_ProfileWebView.class)
        .putExtra("web_url", webURL)
        .putExtra("header_text", header)
    );
	}
	
	
	@Override //grouplist on group tap
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Group sGrp = (Group) getListAdapter().getItem(position);
        if(activityFromShareSelector)
        {
        	
        	startActivity(new Intent(getActivity(), gc_GroupDetails.class)
        	.putExtra("groupid",sGrp.getId())
        	.putExtra("sharedObject",msgSharedObject)
			.putExtra("sharedObjectType",msgSharedObject.getType())
			.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        	
        }
        else
        {
        	startActivity(new Intent(getActivity(), gc_GroupDetails.class)
			.putExtra("groupid",sGrp.getId())
			.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        }
        	
        
    }
	 
	private boolean hasValidPhoneAndName() {
		boolean valid = false;
        if (edtUsername.length() > 0 &  Util.getOnlyNumerics(edtUserphone.getText().toString()).length() > 8) {
        	valid = true;
        } else {
        	if (edtUsername.length()==0)
        		Toast.makeText(getActivity(),getResources().getString(R.string.gc_username_required), Toast.LENGTH_SHORT).show();
        	if ( Util.getOnlyNumerics(edtUserphone.getText().toString()).length()< 9)
        		Toast.makeText(getActivity(), getResources().getString(R.string.gc_phone_required), Toast.LENGTH_SHORT).show();
        	valid = false;
        }
		
		return valid;
	}
	
	private boolean hasEnteredPin() {
		boolean valid = false;
        if (edtPin.length() > 0) {
        	valid = true;
        } else {
       		Toast.makeText(getActivity(), getResources().getString(R.string.gc_pin_required), Toast.LENGTH_SHORT).show();
        	valid = false;
        }		
		return valid;
	}
	
	private void openTOS(String strURL){
		   Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
				    Uri.parse(strURL));
				            try{
				              startActivity(intent);
				            }
				            catch (Exception ex){}

	}
	
	private void finishRegistration() {
		String strUAToken = "";
		startTask(new WS02RegisterUserFinish( this,  Util.getOnlyNumerics(edtUserphone.getText().toString()), var.getGroupcentricAPIKey(getActivity()),
				edtUsername.getText().toString(), Integer.parseInt(edtPin.getText().toString()), strUAToken));		
	}
	
	//this is called from the ShareSelector to let this fragment know
	public void setFromShareSelector()
	{
		activityFromShareSelector = true;
	}
	public void setMessageSharedObject(MessageContent msgobj)
	{
		msgSharedObject = msgobj;
		sharedObjectType = Integer.parseInt(msgobj.getType());
	}
	
	public void setGroupcentricAPIKey(String apikey)
	{
		var.setGroupcentricAPIKey(getContext(), apikey);
	}
	
	
	@Override
    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo menuInfo) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        int position = info.position;
        String strGroupName = m_adapter.getItem(position).getGroupname();

        menu.setHeaderTitle(strGroupName);
            menu.add(0, CONTEXT_CLICK, 0, "View group details");
            menu.add(0, CONTEXT_CLICK, 1, "Leave this group");
 
        super.onCreateContextMenu(menu, view, menuInfo);
    }

	@Override
	public boolean onContextItemSelected(MenuItem item) {

		switch (item.getItemId()) {

		case CONTEXT_CLICK:
			AdapterView.AdapterContextMenuInfo menuInfo = (AdapterView.AdapterContextMenuInfo) item
					.getMenuInfo();

			int iGroupID = m_adapter.getItem(menuInfo.position).getId();
			if (item.toString().contentEquals("View group details")) {
			       Intent intent = new Intent(getActivity(), gc_GroupDetails.class); 
			       intent.putExtra("groupid", iGroupID); 
			       
		          	//((TextView) findViewById(R.id.txtGroupListHelp)).setText(this.getResources().getString(R.string.gc_grouplist_help)); 
			       startActivity(intent);
			}
			if (item.toString().contentEquals("Leave this group")) {
				ConfirmDelete("Group", iGroupID);
			}

			break;

		default:

			return super.onContextItemSelected(item);

		}
		return true;
	}
	
    public void ConfirmDelete(String strType, int iGroupD) {
        final int myGroupID = iGroupD;
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Leave this " + strType + "?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        //Add Remove Group Functionality
                    	startTask(new WS08RemoveGroup(gc_GroupsListFragment.this, var.getGroupcentricUserID(getActivity()), var.getGroupcentricAPIKey(getActivity()), myGroupID));
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
}