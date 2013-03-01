package com.groupcentric.android;
import java.util.ArrayList;
import java.util.List;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.groupcentric.api.BaseTaskGCListActivity;
import com.groupcentric.api.model.GCNotification;
import com.groupcentric.api.ws.BaseTask;
import com.groupcentric.api.ws.WS18GetNotificationsForApp;

public class gc_NotificationsList extends BaseTaskGCListActivity implements OnClickListener{
	private gc_Variables var;
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		//Notification Action 0 = Open Group
		//                    1 = Open Web link
		if (m_adapter.getItem(position).getAction() == 0) {
			int iGroupID =  Integer.parseInt(m_adapter.getItem(position).getActionvariable());
			startActivity(new Intent(this, gc_GroupDetails.class)
			.putExtra("groupid",iGroupID)
			.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
		}
		
		if (m_adapter.getItem(position).getAction() == 1) {
			String strWebURL = m_adapter.getItem(position).getActionvariable();
	        try {
	            Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(strWebURL));
	            startActivity(intent);
			} catch (Exception ex) {
				Toast.makeText(this, "Error loading website",Toast.LENGTH_SHORT).show();
			}
		}
        
	}

	/**
	 * @author support@groupcentric.com
	 *	<br>
	 * Purpose:Notifications list
	 * <br>
	 * Notes: 
	 * <br>
	 * Revisions:
	 * 
	 *
	 */
	private gc_NotificationsAdapter m_adapter;
	
	protected static final int CONTEXT_CLICK = Menu.FIRST;
    
	protected void onCreate(android.os.Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gc_activity_notification_list);
		findViewById(R.id.btn_x).setOnClickListener(this);
		registerForContextMenu(getListView());
		
		var = new gc_Variables(); //finish instantiating the GroupcentricVariables object which will give access to the Groupcentric UserID and APIKey
	};
	
	@Override
	protected void onResume() {
		super.onResume();
		registerReceiver(uaPushReceiver, new IntentFilter(var.IncomingGCPushMSG));
		// get User Notification list
		getMyNotifications();
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
    
	
    private BroadcastReceiver uaPushReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            getMyNotifications(); //Need to refresh notifications because an alert came in
        }
    };    

    public void onSuccess(BaseTask<?, ?, ?> task) {	
		if (task instanceof WS18GetNotificationsForApp) {
			 List<GCNotification> my_notifications =   ((WS18GetNotificationsForApp) task).getActualResult();
				this.m_adapter = new gc_NotificationsAdapter(this, R.layout.gc_row_notification,	(ArrayList<GCNotification>) my_notifications);
				setListAdapter(this.m_adapter);
		}
		}

	
	private void getMyNotifications() {
		startTask(new WS18GetNotificationsForApp(this, var.getGroupcentricUserID(this),var.getGroupcentricAPIKey(this)));
	}
	
	public void onClick(View v) {
		if (v.getId() == R.id.btn_x){
            finish();
		}
		
	}

}
