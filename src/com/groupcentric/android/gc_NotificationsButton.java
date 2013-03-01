package com.groupcentric.android;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;


public class gc_NotificationsButton  extends Fragment implements OnClickListener {
    
	@Override
	public void onResume() {
		super.onResume();
		getActivity().findViewById(R.id.groupcentric_btn_notification).setOnClickListener(this);
	}
	
	
	@Override  //this gets called first with fragments and is responsible for inflating and returning the main view of the fragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
    	
    	
        View myFragmentView = inflater.inflate(R.layout.gc_notificationsbutton, container, false);
        return myFragmentView;
    }
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v.getId() == R.id.groupcentric_btn_notification){
            startActivity(new Intent(this.getActivity(), gc_NotificationsList.class));
		}
	}

	public void updateButton(int updateCount)
	{
		if (updateCount > 0)
			getActivity().findViewById(R.id.groupcentric_btn_notification).setBackgroundResource(R.drawable.gc_notifications_active);
		else
			getActivity().findViewById(R.id.groupcentric_btn_notification).setBackgroundResource(R.drawable.gc_notifications_inactive);	
	}
	
}
