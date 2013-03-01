package com.groupcentric.android;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.groupcentric.api.model.Friend;
import com.groupcentric.util.ImageDownloader;
public class gc_GroupFriendsAdapter extends ArrayAdapter<Friend>{
    private static final int TYPE_FRIEND_0 = 0;
    ImageDownloader imgDownloader;
    protected final Context mContext;
    private final class ViewHolder {
         TextView txtFriendName;
         TextView txtUser;
         TextView txtLocation;
         TextView txtTapToSee;
         RelativeLayout rel_layout; 
         ImageView imgFriend;

    }

	@Override
	public Friend getItem(int position) {
		return super.getItem(position);
	}
	
    public int getFriendMessageType(int position) {
       // return getItem(position).getFriendType();
    	return 0;
    }
   

    
  //  private static Context mContext;
    public gc_GroupFriendsAdapter(Context context, int textViewResourceId, ArrayList<Friend> items) {
            super(context, textViewResourceId, items);
            mContext = context;
            imgDownloader = new ImageDownloader();
    }
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder vh;
            Friend item = getItem(position);
            // get the view type we wish to use
            int type = getItemViewType(position);
            if (convertView == null) {
                vh = new ViewHolder();

                switch (type) {
                    case TYPE_FRIEND_0:
                        convertView = View.inflate(mContext, R.layout.gc_row_friend_type_0, null);
                        vh.rel_layout = (RelativeLayout)  convertView.findViewById(R.id.rel_lyt_friend_row);
                        vh.imgFriend= (ImageView) convertView.findViewById(R.id.imgFriendPic);
                        vh.txtFriendName = (TextView) convertView.findViewById(R.id.txtFriendName);
                        vh.txtLocation = (TextView) convertView.findViewById(R.id.txtFriendLocation);
                        vh.txtTapToSee = (TextView) convertView.findViewById(R.id.txtTapToViewLocation);
                        vh.txtUser = (TextView) convertView.findViewById(R.id.txtUserIndicator);

                        
                        break;
                        
                }
                convertView.setTag(vh);
  

            } else {
                vh = (ViewHolder) convertView.getTag(); 
            }

            vh.txtFriendName.setText(item.getFullname() ); 
            imgDownloader.download(item.getProfilePicURL(), vh.imgFriend);
            vh.txtLocation.setText(item.getLocationOfFriend());
            if (item.getlat().length()>0 && item.getLon().length()>0) {
            	vh.txtTapToSee.setVisibility(View.VISIBLE);
            }
            else {
            	vh.txtTapToSee.setVisibility(View.GONE);
            }
            
            if (item.getLocationOfFriend().length()>0) {
            	vh.txtLocation.setVisibility(View.VISIBLE);
            }
            else {
            	vh.txtLocation.setVisibility(View.GONE);
            }
            	
            	
            if (item.getUser_or_friend().equalsIgnoreCase("U"))
            		vh.txtUser.setVisibility(View.VISIBLE);
            else
            		vh.txtUser.setVisibility(View.GONE);
            
            switch (type) {
                case TYPE_FRIEND_0:  // Normal Message
                 			
                    break;
                
                  
            }
            convertView.setTag(vh);
            return convertView;
        }
    //How many layouts will we be using
    @Override
    public int getViewTypeCount() {
        return 1;
    }
    
    // return the layout type we wish to use
    @Override
    public int getItemViewType(int position) {
    	
    	int iViewType = 0;
    	iViewType =  getFriendMessageType(position);
    		

        if (iViewType == 0) {  //Standand Message
        	iViewType = TYPE_FRIEND_0;
        }
        

if (iViewType != 0) // For development
		iViewType = 0;
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
           // 	CurrDte = formatter.parse(pdate);
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
            
            return strWhen;
    }
    

    static String formatDateTime(Date date) {
    	SimpleDateFormat formatter = new SimpleDateFormat("MMM dd yyyy hh:mmaa"); 
        return formatter.format(date);
    }

}