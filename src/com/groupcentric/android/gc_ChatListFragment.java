package com.groupcentric.android;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import com.groupcentric.api.model.Message;
import com.groupcentric.api.model.Group;
import com.groupcentric.model.MessageContent;
import com.groupcentric.util.ImageDownloader2;
import android.support.v4.app.ListFragment;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.webkit.URLUtil;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.EditText;
import android.widget.ImageView;


public class gc_ChatListFragment extends ListFragment implements OnClickListener {
	/**
	 * @author support@groupcentric.com
	 *	<br>
	 * Purpose:User Chat list Fragment
	 * <br>
	 * Notes: (List Fragment Tab of GroupDetails)
	 * <br>
	 * Revisions:
	 * 
	 *
	 */
	private gc_GroupChatAdapter m_adapter;
	protected static final int CONTEXT_CLICK = Menu.FIRST;
	protected static final int UPLOADED_IMAGE = 101;
    private static final int RC_SHARE_WEB_LINK_RESULT   = 150;
	private static final String GroupDetails = null;
	EditText edtMessage;
	TextView txtURL;
	WebView wvInsertedMap;
	RelativeLayout dummyLayout;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	};
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.gc_list_chat_fragment, container, false); 
	}

	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		getActivity().findViewById(R.id.btn_send).setOnClickListener(this);
		getActivity().findViewById(R.id.btn_plus).setOnClickListener(this);
		getActivity().findViewById(R.id.btn_remove).setOnClickListener(this);
		getActivity().findViewById(R.id.lyt_inserted_item).setOnClickListener(this);
		getActivity().findViewById(R.id.btn_replace).setOnClickListener(this);
		getActivity().findViewById(R.id.btn_view).setOnClickListener(this);
		edtMessage =  (EditText) getActivity().findViewById(R.id.edt_message);		
		txtURL = (TextView) getActivity().findViewById(R.id.txtAttachText);
		wvInsertedMap = (WebView) getActivity().findViewById(R.id.inserted_webview_map);
		dummyLayout = (RelativeLayout)  getActivity().findViewById(R.id.dummy_layout);
		
		edtMessage.addTextChangedListener(new TextWatcher() {
	            public void afterTextChanged(Editable s) {

	                if (edtMessage.getText().length() > 0) {
	                } else {
	                }
	            }

	            public void beforeTextChanged(CharSequence s, int start, int count,
	                                          int after) {
	                // TODO Auto-generated method stub
	            }

	            public void onTextChanged(CharSequence s, int start, int before,
	                                      int count) {
	                // TODO Auto-generated method stub
	            }

	        });

		edtMessage.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(edtMessage.getWindowToken(), 0);
                        sendChatMessage();
                    return true;
                }
                return false;
            }
        });
		registerForContextMenu(getListView());
		
		if (((gc_GroupDetails) getActivity()).getGroup() != null)
			refreshChatDetails();
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == getActivity().RESULT_OK) {
			if (requestCode == UPLOADED_IMAGE) {
				int iUploadStatus = data.getExtras().getInt("upload_complete",0);
				if (iUploadStatus ==1){
					clearAttachment(); // Cleanup screen
					((gc_GroupDetails) getActivity()).refreshGroup(); // Refresh the group to see new message
				}
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	
    private void clearAttachment() {
    	edtMessage.setText("");
		((gc_GroupDetails) getActivity()).removeAttachment();
		View layoutAttachment = getActivity().findViewById(R.id.lyt_inserted_item);
		if (layoutAttachment.getVisibility()==View.VISIBLE) {
			layoutAttachment.startAnimation(AnimationUtils.loadAnimation(getActivity(),	R.anim.fade_out));
			layoutAttachment.setVisibility(View.GONE);
		}
		ImageView imageView = (ImageView) getActivity().findViewById(R.id.imgChatInsertedPic);		
		if (imageView.getVisibility()==View.VISIBLE){
			imageView.startAnimation(AnimationUtils.loadAnimation(getActivity(),R.anim.fade_out));
			imageView.setVisibility(View.GONE);
		}
		if (wvInsertedMap.getVisibility()==View.VISIBLE) {
			wvInsertedMap.startAnimation(AnimationUtils.loadAnimation(getActivity(),R.anim.fade_out));
			wvInsertedMap.setVisibility(View.GONE);
		}

		if (wvInsertedMap.getVisibility()==View.VISIBLE) {
			wvInsertedMap.startAnimation(AnimationUtils.loadAnimation(getActivity(),R.anim.fade_out));
			wvInsertedMap.setVisibility(View.GONE);
		}
		ImageView imageViewSharedObject = (ImageView) getActivity().findViewById(R.id.imgSharedObjectPic);		
		if (imageViewSharedObject.getVisibility()==View.VISIBLE){
			imageViewSharedObject.startAnimation(AnimationUtils.loadAnimation(getActivity(),R.anim.fade_out));
			imageViewSharedObject.setVisibility(View.GONE);
			getActivity().findViewById(R.id.txtSharedObjectTitle).setVisibility(View.GONE);
		}
		
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo menuInfo) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        int position = (info.position-1);
      //Dont let user try to copy header 
        if (position<0)
       	 return;
        if (TextUtils.isEmpty(m_adapter.getItem(position).getMessage()))
        		return;
        String strUserName = m_adapter.getItem(position).getUsername();
        menu.setHeaderTitle("Message from " + strUserName);
            menu.add(0, CONTEXT_CLICK, 0, "Copy message text");
        super.onCreateContextMenu(menu, view, menuInfo);
    }
	
    @Override
	public boolean onContextItemSelected(MenuItem item) {

		switch (item.getItemId()) {

		case CONTEXT_CLICK:
			AdapterView.AdapterContextMenuInfo menuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
			int iChatMsg= 0;
			if (item.toString().contentEquals("Message Function")) {

			}
			break;

			default:
				
			return super.onContextItemSelected(item);
		}
		return true;
	}
	
     
    public void fillChatList (ArrayList<Message> aMsges, String header) {

    }

	public void atachedImageTapped(Integer pos) {
		Toast.makeText(getActivity(), m_adapter.getItem(pos).getMessage(), Toast.LENGTH_SHORT).show();

	}
	
	public void refreshChatDetails() {
//		try {
			setListAdapter(null);
			ListView lv = this.getListView(); 
			LayoutInflater inflater = getActivity().getLayoutInflater(); 
			ViewGroup header = (ViewGroup)inflater.inflate(R.layout.gc_chat_listview_header, lv, false); 
			if (getListView().getHeaderViewsCount()==0)
				this.getListView().addHeaderView(header);
			Group groupDetails = (Group) ((gc_GroupDetails) getActivity()).getGroup();
			//Create Chat Header
			String strHeader = "";
			if (groupDetails.getFriends().size() > 1) {
				//Make sure to grab a friends name
				String strFriendsName = "";
		        for (int i = 0; i < groupDetails.getFriends().size(); i++) {
		        	strFriendsName = (groupDetails.getFriends().get(i).getFullname() );
		        	if (groupDetails.getFriends().get(i).getUser_or_friend().equalsIgnoreCase("F")) {
		        				        		break;
		        	}
		        }
				if (groupDetails.getFriends().size() == 2)
					strHeader = "---- Chat with <b>" + strFriendsName + "</b> ----";
				else
					strHeader = "---- Chat with <b>" + strFriendsName + "</b> and <b>" + (groupDetails.getFriends().size()-2)
							+ " Others</b> ----";
			}
			((TextView) this.getActivity().findViewById(R.id.txt_chat_header)).setText(Html.fromHtml(strHeader));
			this.m_adapter = new gc_GroupChatAdapter(getActivity(),R.layout.gc_row_chat_type_0,(ArrayList<Message>) groupDetails.getMessages());
			setListAdapter(this.m_adapter);
			String strAttachedURL = ((gc_GroupDetails) getActivity()).getAttachedURL();
			//Checked for attached URL -
			
			if (strAttachedURL.length()>0){
				if (URLUtil.isValidUrl(strAttachedURL)){
    			Intent iShareWebLink = new Intent(getActivity(), gc_ShareWebLink.class); 
					iShareWebLink.putExtra("web_share_url", strAttachedURL);
					iShareWebLink.putExtra("group_id", ((gc_GroupDetails) getActivity()).getGroup().getId());
			       startActivityForResult(iShareWebLink, RC_SHARE_WEB_LINK_RESULT);
					clearAttachment();
				}
				else
				((EditText) getActivity().findViewById(R.id.edt_message)).setText(strAttachedURL);
				
			}
			
			//Check for attached Image
			Uri attachedImage = ((gc_GroupDetails) getActivity()).getAttachedImaged();
			if (attachedImage !=null){
				picutreInserted(attachedImage);
			}
			
			//check for attached location
			String strAttachedLatLon = ((gc_GroupDetails) getActivity()).getAttachedLatLon();
			if (strAttachedLatLon.length()>0)
				locationInserted(strAttachedLatLon);
			
			//check for shared object 
			int msgSharedObjType = ((gc_GroupDetails) getActivity()).getSharedObjectType();
			if(msgSharedObjType > 0)
			{
				MessageContent msgSharedObj = ((gc_GroupDetails) getActivity()).getSharedObject();
				sharedObjectInserted(msgSharedObj);
			}
			
			
			
			//Get on the last item -
			getListView().setSelection(this.m_adapter.getCount()+1);
            
	}

	public void onClick(View v) {
		if (v.getId() == R.id.btn_send) {
			sendChatMessage();
		}
		if (v.getId() == R.id.btn_plus || v.getId()==R.id.btn_replace) {
			getActivity().findViewById(R.id.dummy_layout).requestFocus();
			
			clearAttachment();
			((gc_GroupDetails) getActivity()).insertAttachment();
		}
		
		if (v.getId() == R.id.btn_remove){
			clearAttachment();
		}
		if (v.getId() == R.id.btn_view){
			Uri attachedImage = ((gc_GroupDetails) getActivity()).getAttachedImaged();
			Intent i = new Intent(getActivity(), gc_PictureViewer.class);
		 	i.putExtra("imageUri", attachedImage);
			startActivity(i);
		}
		
	}
	
	private void sendChatMessage() {
		//Wait to send message if chat is updating 
		if (((gc_GroupDetails) getActivity()).isChatUpdateing())
			return;
		String strMessage = ((EditText) getActivity().findViewById(R.id.edt_message)).getText().toString();
		//Attempt to hide soft keyboard if up 
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(edtMessage.getWindowToken(), 0);
	    
		// Check to see if this is attachment message
		Uri attachedImage = ((gc_GroupDetails) getActivity()).getAttachedImaged();
		if (attachedImage!=null)
		{
		Intent intent = new Intent(getActivity(), gc_UploadImage.class); 
	    	   intent.putExtra("imageUri", attachedImage);
	    	   intent.putExtra("msg", strMessage);
	    	   intent.putExtra("imagetype", "C");
	    	   intent.putExtra("groupid",((gc_GroupDetails) getActivity()).getGroup().getId());
	       startActivityForResult(intent, UPLOADED_IMAGE);
	       return;
		}
	
		boolean hasAttachment = false;
		try {
		hasAttachment =  getActivity().findViewById(R.id.lyt_inserted_item).getVisibility()==View.VISIBLE;
		}
		catch (Exception ex) {}
		if (strMessage.trim().length() > 0 | hasAttachment) {
			((gc_GroupDetails) getActivity()).sendMessage(strMessage.trim());
			((EditText) getActivity().findViewById(R.id.edt_message)).setText("");
			//Ensure attachment are gone
			getActivity().findViewById(R.id.lyt_inserted_item).setVisibility(View.GONE);

		}
	}

	public void sharedObjectInserted(MessageContent sharedObj)
	{
		getActivity().findViewById(R.id.btn_view).setVisibility(View.GONE);
		getActivity().findViewById(R.id.btn_replace).setVisibility(View.GONE);
		getActivity().findViewById(R.id.inserted_webview_map).setVisibility(View.GONE);
		getActivity().findViewById(R.id.imgChatInsertedPic).setVisibility(View.GONE);
		getActivity().findViewById(R.id.txtAttachText).setVisibility(View.GONE);
		getActivity().findViewById(R.id.lyt_inserted_item).setVisibility(View.VISIBLE);
		
		getActivity().findViewById(R.id.imgSharedObjectPic).setVisibility(View.VISIBLE);
		getActivity().findViewById(R.id.txtSharedObjectTitle).setVisibility(View.VISIBLE);
		//set title to txtAttachText, pic to imgChatInsertedPic
		if(sharedObj.getType().equals("1"))
		{
			 if (sharedObj.getTitle()!=null)
				 ((EditText) getActivity().findViewById(R.id.edt_message)).setText("'"+sharedObj.getTitle()+"'");
		}
		else
			((TextView) getActivity().findViewById(R.id.txtSharedObjectTitle)).setText(sharedObj.getTitle());
		ImageView imgShareItemImage = (ImageView) getActivity().findViewById(R.id.imgSharedObjectPic);
		ImageDownloader2 imageDownloader = new ImageDownloader2();
		imageDownloader.download(sharedObj.getImageURL(), imgShareItemImage); 
		edtMessage.requestFocus();
	}
	
	public void locationInserted(String LatLon) {
		if (LatLon.length()>0){ 
			String[] strLatLong = LatLon.split(",");
			String strMapURL = "http://maps.googleapis.com/maps/api/staticmap?center="
					+ strLatLong[0]
					+ ","
					+ strLatLong[1]
					+ "&sensor=false"
					+ "&size=300x100&sensor=false&markers=color:blue%7C%7C"
					+ strLatLong[0]
					+ ","
					+ strLatLong[1]
					+ "&maptype=normal"
					+ "&zoom=15";
			wvInsertedMap.loadUrl(strMapURL);
			getActivity().findViewById(R.id.btn_view).setVisibility(View.GONE);
			getActivity().findViewById(R.id.imgChatInsertedPic).setVisibility(View.GONE);
			getActivity().findViewById(R.id.btn_replace).setVisibility(View.GONE);
			getActivity().findViewById(R.id.lyt_inserted_item).setVisibility(View.VISIBLE);
			getActivity().findViewById(R.id.inserted_webview_map).setVisibility(View.VISIBLE);
		}
		edtMessage.requestFocus();
	}
	
	public void picutreInserted(Uri uri) {

		ImageView imageView = (ImageView) getActivity().findViewById(
				R.id.imgChatInsertedPic);

		if (imageView.getDrawable() != null)
			if (((BitmapDrawable) imageView.getDrawable()).getBitmap() != null)
				((BitmapDrawable) imageView.getDrawable()).getBitmap()
						.recycle();
		imageView.invalidate();
		imageView.setImageBitmap(null);
		// imageView.setImageURI(uri);
		// preview image

		BitmapFactory.Options o = new BitmapFactory.Options();
		o.inJustDecodeBounds = false;
		o.inSampleSize = 4;
		Bitmap bitmapPreview = null;
		ContentResolver cr = getActivity().getContentResolver();
		boolean bLoadedPreview = true;
		try {
			bitmapPreview = BitmapFactory.decodeStream(cr.openInputStream(uri),null,o);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Toast.makeText(getActivity(), "Cant load image", Toast.LENGTH_SHORT)
					.show();
			bLoadedPreview = false;

		} // load
		cr = null;
		// preview
		// image

		BitmapDrawable d = new BitmapDrawable(getResources(), bitmapPreview);
		imageView.setImageDrawable(d);

		File imgFileName = new File(uri.getPath());
		String fileName = "";
		fileName = uri.getLastPathSegment();
		if (bLoadedPreview) {
			getActivity().findViewById(R.id.lyt_inserted_item).setVisibility(
					View.VISIBLE);
			getActivity().findViewById(R.id.imgChatInsertedPic).setVisibility(
					View.VISIBLE);
		} else
			clearAttachment();
		edtMessage.requestFocus();
	}
	
}

