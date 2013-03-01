package com.groupcentric.android;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.groupcentric.api.model.Message;
import com.groupcentric.util.ImageDownloader;
import com.groupcentric.util.ImageDownloader2;
public class gc_GroupChatAdapter extends ArrayAdapter<Message>{
    private static final int TYPE_CHAT_0 = 0;
    private static final int TYPE_CHAT_1 = 1;
    private static final int TYPE_CHAT_2 = 2;
    private static final int TYPE_CHAT_3 = 3;
    private static final int TYPE_CHAT_4 = 4;
    private static final int TYPE_CHAT_CUSTOM = 5;
    
    ImageDownloader imgDownloader;
    ImageDownloader2 imgDownloader2;
    protected final Context mContext;
    private final class ViewHolder {
         TextView txtMsgFrom;
         TextView txtMessage;
         TextView txtLastMessageDate;
         RelativeLayout rel_layout; 
         ImageView imgChatUser;
         //Attached picture
         ImageView imgAttachedPic;
         //Attached Map
         WebView map_webview;
         RelativeLayout rel_lyt_web;
         //Attached URL
         ImageView imgAttachedwebsite;
         TextView txtwebsitetitle;
         TextView txtwebsiteurl;
         RelativeLayout rel_attachedwebsite;
         
         //Attached Event
         ImageView imgAttachedevent;
         TextView txteventtitle;
         TextView txteventurl;
         RelativeLayout rel_attachedevent;
         
         RelativeLayout rel_attachedcustom;
         
        
    }

	@Override
	public Message getItem(int position) {
		return super.getItem(position);
	}
	
    public int getChatMessageType(int position) {
        return getItem(position).getMsgType();
    }
   

    
  //  private static Context mContext;
    public gc_GroupChatAdapter(Context context, int textViewResourceId, ArrayList<Message> items) {
            super(context, textViewResourceId, items);
            mContext = context;
            imgDownloader = new ImageDownloader();
            imgDownloader2 = new ImageDownloader2();
    }
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
    	
            ViewHolder vh;
            Message item = getItem(position);
            // get the view type we wish to use
            int type = getItemViewType(position);
            if (convertView == null) {
                vh = new ViewHolder();

                switch (type) {
                    case TYPE_CHAT_0:
                        convertView = View.inflate(mContext, R.layout.gc_row_chat_type_0, null);
                        vh.txtMsgFrom = (TextView) convertView.findViewById(R.id.txtMsgFrom);
                        vh.txtMessage = (TextView) convertView.findViewById(R.id.txtMessage);
                        vh.txtLastMessageDate = (TextView) convertView.findViewById(R.id.txtGroupLastMsgDte);
                        vh.rel_layout = (RelativeLayout)  convertView.findViewById(R.id.rel_lyt_chat_row);
                        vh.imgChatUser = (ImageView) convertView.findViewById(R.id.imgChatPic); 
                        break;
                        
                    case TYPE_CHAT_1:
                        convertView = View.inflate(mContext, R.layout.gc_row_chat_type_1, null);
                        vh.txtMsgFrom = (TextView) convertView.findViewById(R.id.txtMsgFrom);
                        vh.txtMessage = (TextView) convertView.findViewById(R.id.txtMessage);
                        vh.txtLastMessageDate = (TextView) convertView.findViewById(R.id.txtGroupLastMsgDte);
                        vh.rel_layout = (RelativeLayout)  convertView.findViewById(R.id.rel_lyt_chat_row);
                        vh.imgChatUser = (ImageView) convertView.findViewById(R.id.imgChatPic);
                        vh.imgAttachedPic = (ImageView) convertView.findViewById(R.id.imgAttachedPic);
                        break;
                        
                    case TYPE_CHAT_2:
                        convertView = View.inflate(mContext, R.layout.gc_row_chat_type_2, null);
                        vh.imgChatUser = (ImageView) convertView.findViewById(R.id.imgChatPic);
                        vh.txtMsgFrom = (TextView) convertView.findViewById(R.id.txtMsgFrom);
                        vh.txtMessage = (TextView) convertView.findViewById(R.id.txtMessage);
                        vh.txtLastMessageDate = (TextView) convertView.findViewById(R.id.txtGroupLastMsgDte);
                        vh.rel_layout = (RelativeLayout)  convertView.findViewById(R.id.rel_lyt_chat_row);                       
                        vh.map_webview = (WebView) convertView.findViewById(R.id.map_webview);
                        vh.rel_lyt_web = (RelativeLayout) convertView.findViewById(R.id.lyt_map);
                        break;
                    
                    case TYPE_CHAT_3:
                        convertView = View.inflate(mContext, R.layout.gc_row_chat_type_3, null);
                        vh.imgChatUser = (ImageView) convertView.findViewById(R.id.imgChatPic);
                        vh.txtMsgFrom = (TextView) convertView.findViewById(R.id.txtMsgFrom);
                        vh.txtMessage = (TextView) convertView.findViewById(R.id.txtMessage);
                        vh.txtLastMessageDate = (TextView) convertView.findViewById(R.id.txtGroupLastMsgDte);
                        vh.rel_layout = (RelativeLayout)  convertView.findViewById(R.id.rel_lyt_chat_row);
                        vh.imgAttachedwebsite = (ImageView) convertView.findViewById(R.id.imgAttachedwebsite);
                        vh.txtwebsitetitle = (TextView) convertView.findViewById(R.id.txtwebsitetitle);
                        vh.txtwebsiteurl = (TextView) convertView.findViewById(R.id.txtwebsiteurl);
                        vh.rel_attachedwebsite = (RelativeLayout) convertView.findViewById(R.id.lyt_attachedwebsite);
                        break;
                        
                    case TYPE_CHAT_4:
                        convertView = View.inflate(mContext, R.layout.gc_row_chat_type_4, null);
                        vh.imgChatUser = (ImageView) convertView.findViewById(R.id.imgChatPic);
                        vh.txtMsgFrom = (TextView) convertView.findViewById(R.id.txtMsgFrom);
                        vh.txtMessage = (TextView) convertView.findViewById(R.id.txtMessage);
                        vh.txtLastMessageDate = (TextView) convertView.findViewById(R.id.txtGroupLastMsgDte);
                        vh.rel_layout = (RelativeLayout)  convertView.findViewById(R.id.rel_lyt_chat_row);
                        vh.imgAttachedevent = (ImageView) convertView.findViewById(R.id.imgAttachedevent);
                        vh.txteventtitle = (TextView) convertView.findViewById(R.id.txteventtitle);
                        vh.txteventurl = (TextView) convertView.findViewById(R.id.txteventurl);
                        vh.rel_attachedevent = (RelativeLayout) convertView.findViewById(R.id.lyt_attachedevent);
                        break;
                        
                    case TYPE_CHAT_CUSTOM: //custom type object 100+ .. displays like a type 3
                        convertView = View.inflate(mContext, R.layout.gc_row_chat_type_custom, null);
                        vh.imgChatUser = (ImageView) convertView.findViewById(R.id.imgChatPic);
                        vh.txtMsgFrom = (TextView) convertView.findViewById(R.id.txtMsgFrom);
                        vh.txtMessage = (TextView) convertView.findViewById(R.id.txtMessage);
                        vh.txtLastMessageDate = (TextView) convertView.findViewById(R.id.txtGroupLastMsgDte);
                        vh.rel_layout = (RelativeLayout)  convertView.findViewById(R.id.rel_lyt_chat_row);
                        vh.imgAttachedwebsite = (ImageView) convertView.findViewById(R.id.imgAttachedwebsite);
                        vh.txtwebsitetitle = (TextView) convertView.findViewById(R.id.txtwebsitetitle);
                        vh.txtwebsiteurl = (TextView) convertView.findViewById(R.id.txtwebsiteurl);
                        vh.rel_attachedcustom = (RelativeLayout) convertView.findViewById(R.id.lyt_attachedcustom);
                        break;    

                }
                convertView.setTag(vh);
  

            } else {
                vh = (ViewHolder) convertView.getTag(); 
            }

            vh.txtMsgFrom.setText(item.getUsername()); 
            vh.txtMessage.setText(item.getMessage());
            if (item.getMessage().trim().length()==0)
            	vh.txtMessage.setVisibility(View.GONE);
            else
            	vh.txtMessage.setVisibility(View.VISIBLE);
            
            String strLastMessage = formatDateTime(item.getMsgDate());
            strLastMessage = getLastMessageTime(strLastMessage);
            vh.txtLastMessageDate.setText(strLastMessage);
            imgDownloader.download(item.getUserpic(), vh.imgChatUser);
            
            
            switch (type) {
                case TYPE_CHAT_0:  // Normal Message
                 			
                    break;
                case TYPE_CHAT_1: //Attached Pic
                	imgDownloader2.download(item.getImageurl(), vh.imgAttachedPic);
                       
                		vh.imgAttachedPic.setTag(position);
                        vh.imgAttachedPic.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View v) {
                              //  Message ms = (Message) v.getTag();
                            	Integer pos = (Integer) v.getTag();
                            	Message ms = getItem(pos);
                                ((gc_GroupDetails) mContext).itemImageTapped(ms);
                            }
                        });
                    break;
				case TYPE_CHAT_2: // Location Message
		
					try {
						String[] strLatLong = item.getVar1().split(",");
						String strMapURL = "http://maps.googleapis.com/maps/api/staticmap?center="
								+ strLatLong[0]
								+ ","
								+ strLatLong[1]
								+ "&sensor=false"
								+ "&size=300x150&sensor=false&markers=color:blue%7C%7C"
								+ strLatLong[0]
								+ ","
								+ strLatLong[1]
								+ "&maptype=normal"
								+ "&zoom=15";
						vh.map_webview.loadUrl(strMapURL);
		
					} catch (Exception ex) {
						// System.out.println(ex.getMessage());
					}
					vh.rel_lyt_web.setTag(position);
					vh.rel_lyt_web.setOnClickListener(new View.OnClickListener() {
						public void onClick(View v) {
							Integer pos = (Integer) v.getTag();
							Message ms = getItem(pos);
							((gc_GroupDetails) mContext).mapImageTapped(ms);
						}
					});
					break;
				case TYPE_CHAT_3: // Website Attached
					 if (item.getImageurl().length()>0) {
					      vh.imgAttachedwebsite.setVisibility(View.VISIBLE);
					      imgDownloader2.download(item.getImageurl(), vh.imgAttachedwebsite); 
					     }
					 else {
					      vh.imgAttachedwebsite.setVisibility(View.GONE);
					      vh.txtwebsiteurl.setVisibility(View.GONE);
					 }
					 
					
					
					if (item.getVar_title().length()>0)
						vh.txtwebsitetitle.setText(item.getVar_title());
					else
						vh.txtwebsitetitle.setText("Tap to view:");
					
					if (item.getVar_subtitle().length()>0)
					{
						vh.txtwebsiteurl.setText(item.getVar_subtitle());
						vh.txtwebsiteurl.setVisibility(View.VISIBLE);
					}
					else
						vh.txtwebsiteurl.setText(item.getVar1());
					
					vh.rel_attachedwebsite.setTag(position);
					vh.rel_attachedwebsite.setOnClickListener(new View.OnClickListener() {
						public void onClick(View v) {
							Integer pos = (Integer) v.getTag();
							Message ms = getItem(pos);
							((gc_GroupDetails) mContext).websiteTapped(ms);
						}
					});
					break;
					
				case TYPE_CHAT_4: // Event Attached
					imgDownloader2.download(item.getImageurl(), vh.imgAttachedevent);
					vh.txteventtitle.setText(item.getVar_title());
					
					if (item.getVar_subtitle().length()>0)
						vh.txteventurl.setText(item.getVar_subtitle());
					else
						vh.txteventurl.setVisibility(View.GONE);
					
					vh.rel_attachedevent.setTag(position);
					vh.rel_attachedevent.setOnClickListener(new View.OnClickListener() {
						public void onClick(View v) {
							Integer pos = (Integer) v.getTag();
							Message ms = getItem(pos);
							((gc_GroupDetails) mContext).eventTapped(ms);
						}
					});
					break;
				case TYPE_CHAT_CUSTOM: //custom type 100+ 
						
						  //YOUR CUSTOM OBJECTS
						  //if you're using custom objects in the chat then 
						  //you need to implement the following for each one of your types
	
						  //set the image in the chat message ui
						  imgDownloader2.download(item.getImageurl(), vh.imgAttachedwebsite); 
						  //set the title in the chat message ui
						  vh.txtwebsitetitle.setText(item.getVar_title());
						  vh.rel_attachedcustom.setTag(position);
		
						  vh.rel_attachedcustom.setOnClickListener(new View.OnClickListener() {
							  public void onClick(View v) {
								  Integer pos = (Integer) v.getTag();
								  Message ms = getItem(pos);
								  //set the onClick to your own function here
								  // if(ms.getMsgType() == <YOURTYPE>) { //<-- your type number here
								  //{
							          //Change the function name only
							          //((gc_GroupDetails) mContext).<YOUR FUNCTION NAME>(ms);
								  //}
							  }
						  });
				break;	
            }
            convertView.setTag(vh);
            return convertView;
        }
    //How many layouts will we be using
    @Override
    public int getViewTypeCount() {
        return 6;
    }
    
    // return the layout type we wish to use
    @Override
    public int getItemViewType(int position) {
    	
    	int iViewType = 0;
    	iViewType =  getChatMessageType(position);


        if (iViewType == 0) {  //Standand Message
        	iViewType = TYPE_CHAT_0;
        }
        
        else if (iViewType == 1) {  //Picture Message
        	iViewType = TYPE_CHAT_1;
        }
        
        else if (iViewType == 2) {  //Location Message
        	iViewType = TYPE_CHAT_2;
        }
        else if (iViewType == 3) {  //Website Message
        	iViewType = TYPE_CHAT_3;
        }
        
        else if (iViewType == 4) {  //Website Message
        	iViewType = TYPE_CHAT_4;
        }
        else iViewType = TYPE_CHAT_CUSTOM;
//if (iViewType != 0 & iViewType != 1 & iViewType !=2 & iViewType !=3 & iViewType !=4) // For development
		//iViewType = 0;
        return iViewType; 
    }
    
    private String getLastMessageTime(String strMessageLastSent){
        SimpleDateFormat formatter = new SimpleDateFormat("MMM dd yyyy hh:mmaa"); 
        
       
        Date lstMsgDate = null;
            try {
				lstMsgDate = formatter.parse(strMessageLastSent);
			} catch (java.text.ParseException e) {
				return "";
			}   
            
            Date CurrDte = new Date();
            StringBuilder CurrentDate = new StringBuilder(formatter.format(CurrDte));
            try {
            	CurrDte = formatter.parse(CurrentDate.toString());
			} catch (java.text.ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return "";
			}   

            Calendar calendar1 = Calendar.getInstance(); 
            calendar1.setTimeInMillis(lstMsgDate.getTime()); 
			Calendar calCurrent = Calendar.getInstance();
			calCurrent.setTimeInMillis(CurrDte.getTime());
            
			long milliseconds1 = calendar1.getTimeInMillis(); 
            long milliseconds2 = calCurrent.getTimeInMillis(); 
            long diff = milliseconds2 - milliseconds1; 
            long diffMinutes = diff / (60 * 1000); 
            long diffHours = diff / (60 * 60 * 1000); 
            long diffDays = diff / (24 * 60 * 60 * 1000);
            
            String strWhen = "";
            if (diffDays>=1){  // Show Days
            	if (diffDays==1)
            		strWhen= diffDays +" Day Ago";
            	else
            		strWhen= diffDays +" Days Ago";
            }
            else // Show hrs minutes
            {
            	if (diffMinutes==1)
            		strWhen=diffMinutes + " Minute ago";
            	
            	if (diffMinutes>1)
            		strWhen=diffMinutes +" Minutes ago";
            	
            	if (diffHours==1)
            		strWhen= diffHours + " Hour ago";
            	if (diffHours>1)
            		strWhen=diffHours +" Hours ago";
            }
            if (strWhen.length()==0)
            	strWhen = "Just now";
            return strWhen;
    }
    

    static String formatDateTime(Date date) {
    	SimpleDateFormat formatter = new SimpleDateFormat("MMM dd yyyy hh:mmaa"); 
        return formatter.format(date);
    }

}