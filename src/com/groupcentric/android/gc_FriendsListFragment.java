package com.groupcentric.android;
import java.util.ArrayList;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import com.groupcentric.api.model.Friend;
import com.groupcentric.api.model.Group;
import com.groupcentric.api.model.Message;

import android.support.v4.app.ListFragment;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.webkit.WebView;


public class gc_FriendsListFragment extends ListFragment implements OnClickListener {
	/**
	 * @author support@groupcentric.com
	 *	<br>
	 * Purpose:User friends list Fragment
	 * <br>
	 * Notes: (Friends List Tab Of Group Details)
	 * <br>
	 * Revisions:
	 * 
	 *
	 */
	private gc_GroupFriendsAdapter m_adapter;
	Friend Selectedfriend;
	TextView txtFreindsLoc;
//	App app;
	protected static final int CONTEXT_CLICK = Menu.FIRST;
    
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	};
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.gc_list_friends_fragment, container, false); 
		
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		txtFreindsLoc = (TextView) getActivity().findViewById(R.id.txtFriendLocatoin);
		getActivity().findViewById(R.id.btn_invite_friends).setOnClickListener(this);
		getActivity().findViewById(R.id.map_conent).setOnClickListener(this);
		if (((gc_GroupDetails) getActivity()).getGroup() != null)
		refreshFriendsDetails();
	}

	@Override
	public void onResume() {
		super.onResume();
	}

   
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {

		if (m_adapter.getItem(position).getlat().length()>0 && m_adapter.getItem(position).getLon().length()>0){
			updateMap(m_adapter.getItem(position).getlat(), 
					  m_adapter.getItem(position).getLon(),
					  m_adapter.getItem(position).getFullname());
			Selectedfriend = m_adapter.getItem(position);
		}
		super.onListItemClick(l, v, position, id);

 	}
	
	void updateMap(String lat, String Lon,String friendsName) {
		String friendsMap = "http://maps.googleapis.com/maps/api/staticmap?center="
				+ lat
				+ ","
				+ Lon
				+ "&sensor=false"
				+ "&size=400x130&sensor=false&markers=color:blue%7C%7C"
				+ lat
				+ ","
				+ Lon
				+ "&maptype=normal"
				+ "&zoom=15";
		WebView webFriendsURL = (WebView) getActivity().findViewById(R.id.map_friends_location);
		webFriendsURL.loadUrl(friendsMap);
		txtFreindsLoc.setText(friendsName + "'s " + "Location  (tap map to open)");
	}
	
	public void refreshFriendsDetails() {

		try {
			setListAdapter(null);
			Group groupDetails = (Group) ((gc_GroupDetails) getActivity()).getGroup();
			 WebView webFriendsURL = (WebView) getActivity().findViewById(R.id.map_friends_location);
			 //groupDetails.setFriendsLocationurl("");
			if (groupDetails.getFriendsLocationurl().length()>0) {
				String mSize = "size=400x400";
				String newMSize ="size=400x130";
				String friendsMap= groupDetails.getFriendsLocationurl();
				friendsMap = friendsMap.replace(mSize, newMSize);
				webFriendsURL.loadUrl(friendsMap);
				getActivity().findViewById(R.id.img_blankmap).setVisibility(View.GONE);
				txtFreindsLoc.setText(getActivity().getResources().getString(R.string.gc_message_friends_location));
			}
			else
			{
				getActivity().findViewById(R.id.img_blankmap).setVisibility(View.VISIBLE);
				txtFreindsLoc.setText(getActivity().getResources().getString(R.string.gc_message_friends_no_location));
			}

			this.m_adapter = new gc_GroupFriendsAdapter(getActivity(),R.layout.gc_row_friend_type_0,(ArrayList<Friend>) groupDetails.getFriends());
			setListAdapter(this.m_adapter);

		} catch (Exception ex) {
			Toast.makeText(getActivity(), ex.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
 		}
	}

	public void onClick(View v) {
		if (v.getId() == R.id.btn_invite_friends) {
			((gc_GroupDetails) getActivity()).addFriendToGroup();
		}
		
		if (v.getId() == R.id.map_conent) {
			mapTapped();
		}
		
	}
	
	public void mapTapped() {
		if (Selectedfriend !=null)
	        try {
	            String strURL = "geo:0,0?q=" + Selectedfriend.getlat() + ",+" + Selectedfriend.getLon() + "(" + Selectedfriend.getFullname()+ ")";
	            Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(strURL));
	            startActivity(intent);
			} catch (Exception ex) {
				Toast.makeText(getActivity(), "Invalid map points",Toast.LENGTH_SHORT).show();
		}
	}
	
}
