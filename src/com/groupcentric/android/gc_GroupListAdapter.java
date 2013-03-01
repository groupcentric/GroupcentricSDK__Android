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
import com.groupcentric.api.model.Group;
import com.groupcentric.util.ImageDownloader;


public class gc_GroupListAdapter extends ArrayAdapter<Group>{
    private static final int TYPE_GROUP_FRIEND1 = 0;
    private static final int TYPE_GROUP_FRIEND2 = 1;
    private static final int TYPE_GROUP_FRIEND3 = 2;
    private static final int TYPE_GROUP_FRIEND4 = 3;
    ImageDownloader imgDownloader;
    
    private final class ViewHolder {
         TextView txtGroupTitle;
         TextView txtLastMsg;
         TextView txtlastMsgDte;
         RelativeLayout rel_layout; 
         ImageView imgGroupPic1;
         ImageView imgGroupPic2;
         ImageView imgGroupPic3;
         ImageView imgGroupPic4;
         RelativeLayout rel_lt_grp_updated; 
    }

	@Override
	public Group getItem(int position) {
		return super.getItem(position);
	}
	
    public int getFriendCount(int position) {
        return getItem(position).getFriends().size();
    }
    
    public String getGroupImage(int position) {
        return getItem(position).getGroupimage();
    }
    
    private static Context mContext;
    public gc_GroupListAdapter(Context context, int textViewResourceId, ArrayList<Group> items) {
            super(context, textViewResourceId, items);
            mContext = context;
            imgDownloader = new ImageDownloader();
    }
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder vh;
            Group item = getItem(position);
            // get the view type we wish to use
            int type = getItemViewType(position);
            if (convertView == null) {
                vh = new ViewHolder();

                switch (type) {
                    case TYPE_GROUP_FRIEND1:
                        convertView = View.inflate(mContext, R.layout.gc_row_group1, null);
                        vh.txtGroupTitle = (TextView) convertView.findViewById(R.id.txtGroupTitle);
                        vh.txtLastMsg = (TextView) convertView.findViewById(R.id.txtGroupLastMsg);
                        vh.txtlastMsgDte = (TextView) convertView.findViewById(R.id.txtGroupLastMsgDte);
                        vh.rel_layout = (RelativeLayout)  convertView.findViewById(R.id.rel_lyt_group_row);
                        vh.imgGroupPic1 = (ImageView) convertView.findViewById(R.id.imgGroupPic1);
                        vh.rel_lt_grp_updated = (RelativeLayout)  convertView.findViewById(R.id.lytGrpUpdateIndicator);
                        break;

                    case TYPE_GROUP_FRIEND2:
                        convertView = View.inflate(mContext, R.layout.gc_row_group2, null);
                        vh.txtGroupTitle = (TextView) convertView.findViewById(R.id.txtGroupTitle);
                        vh.txtLastMsg = (TextView) convertView.findViewById(R.id.txtGroupLastMsg);
                        vh.txtlastMsgDte = (TextView) convertView.findViewById(R.id.txtGroupLastMsgDte);
                        vh.rel_layout = (RelativeLayout)  convertView.findViewById(R.id.rel_lyt_group_row);
                        vh.imgGroupPic1 = (ImageView) convertView.findViewById(R.id.imgGroupPic1);
                        vh.imgGroupPic2 = (ImageView) convertView.findViewById(R.id.imgGroupPic2);
                        vh.rel_lt_grp_updated = (RelativeLayout)  convertView.findViewById(R.id.lytGrpUpdateIndicator);
                        break;
                    
                    case TYPE_GROUP_FRIEND3:
                        convertView = View.inflate(mContext, R.layout.gc_row_group3, null);
                        vh.txtGroupTitle = (TextView) convertView.findViewById(R.id.txtGroupTitle);
                        vh.txtLastMsg = (TextView) convertView.findViewById(R.id.txtGroupLastMsg);
                        vh.txtlastMsgDte = (TextView) convertView.findViewById(R.id.txtGroupLastMsgDte);
                        vh.rel_layout = (RelativeLayout)  convertView.findViewById(R.id.rel_lyt_group_row);
                        vh.imgGroupPic1 = (ImageView) convertView.findViewById(R.id.imgGroupPic1);
                        vh.imgGroupPic2 = (ImageView) convertView.findViewById(R.id.imgGroupPic2);
                        vh.imgGroupPic3 = (ImageView) convertView.findViewById(R.id.imgGroupPic3);
                        vh.rel_lt_grp_updated = (RelativeLayout)  convertView.findViewById(R.id.lytGrpUpdateIndicator);
                        break;
                    case TYPE_GROUP_FRIEND4:
                        convertView = View.inflate(mContext, R.layout.gc_row_group4, null);
                         vh.txtGroupTitle = (TextView) convertView.findViewById(R.id.txtGroupTitle);
                        vh.txtLastMsg = (TextView) convertView.findViewById(R.id.txtGroupLastMsg);
                        vh.txtlastMsgDte = (TextView) convertView.findViewById(R.id.txtGroupLastMsgDte);
                        vh.rel_layout = (RelativeLayout)  convertView.findViewById(R.id.rel_lyt_group_row);
                        vh.imgGroupPic1 = (ImageView) convertView.findViewById(R.id.imgGroupPic1);
                        vh.imgGroupPic2 = (ImageView) convertView.findViewById(R.id.imgGroupPic2);
                        vh.imgGroupPic3 = (ImageView) convertView.findViewById(R.id.imgGroupPic3);
                        vh.imgGroupPic4 = (ImageView) convertView.findViewById(R.id.imgGroupPic4);
                        vh.rel_lt_grp_updated = (RelativeLayout)  convertView.findViewById(R.id.lytGrpUpdateIndicator);
                        break;
                }
                convertView.setTag(vh);
  

            } else {
                vh = (ViewHolder) convertView.getTag(); 
            }

            vh.txtGroupTitle.setText(item.getGroupname()); 
            vh.txtLastMsg.setText(item.getLastmsgName() + ": " + item.getLastmsg()) ;

            vh.txtLastMsg.setTextColor(mContext.getResources().getColor(R.color.gc_group_list_last_message_color));
            vh.txtLastMsg.setTextSize(13);
            
            if (item.getLastmsgName()== null) 
            	vh.txtLastMsg.setVisibility(View.GONE);
            else
            	vh.txtLastMsg.setVisibility(View.VISIBLE);
            	
            
            String strLastMessageTime = getLastMessageTime(formatDateTime(item.getLastupdated()));
            	
            vh.txtlastMsgDte.setText(strLastMessageTime);
            vh.txtlastMsgDte.setTextColor(mContext.getResources().getColor(R.color.gc_group_list_last_message_time_color));
            vh.txtlastMsgDte.setTextSize(13);

             if (item.getNewcontent()==1) {
            	convertView.setBackgroundResource(R.drawable.gc_list_item_group_new_bg);
            	vh.rel_lt_grp_updated.setVisibility(View.VISIBLE);
            }
            else{
            	convertView.setBackgroundResource(R.drawable.gc_list_item_group_old_bg); 
            	vh.rel_lt_grp_updated.setVisibility(View.GONE);
            }
            //// -- specific view logic

            switch (type) {
                case TYPE_GROUP_FRIEND1:
                		if (item.getGroupimage().length()>0)  // Use group image if we have one
                			imgDownloader.download(item.getGroupimage(), vh.imgGroupPic1);
                		else  // Use Profile Image
                			if (item.getFriends().size()>0)
                				imgDownloader.download(item.getFriends().get(0).getProfilePicURL(), vh.imgGroupPic1);
                			
                    break;

                case TYPE_GROUP_FRIEND2:
                	imgDownloader.download(item.getFriends().get(0).getProfilePicURL(), vh.imgGroupPic1);
                	imgDownloader.download(item.getFriends().get(1).getProfilePicURL(), vh.imgGroupPic2);
                    break;
                
                case TYPE_GROUP_FRIEND3:
                	imgDownloader.download(item.getFriends().get(0).getProfilePicURL(), vh.imgGroupPic1);
                	imgDownloader.download(item.getFriends().get(1).getProfilePicURL(), vh.imgGroupPic2);
                	imgDownloader.download(item.getFriends().get(2).getProfilePicURL(), vh.imgGroupPic3);
                    break;

                case TYPE_GROUP_FRIEND4:
                	imgDownloader.download(item.getFriends().get(0).getProfilePicURL(), vh.imgGroupPic1);
                	imgDownloader.download(item.getFriends().get(1).getProfilePicURL(), vh.imgGroupPic2);
                	imgDownloader.download(item.getFriends().get(2).getProfilePicURL(), vh.imgGroupPic3);
                	imgDownloader.download(item.getFriends().get(3).getProfilePicURL(), vh.imgGroupPic4);
                    break;
                    
            }
            convertView.setTag(vh);
            return convertView;
        }
    //How many layouts will we be using
    @Override
    public int getViewTypeCount() {
        return 4;
    }
    
    // return the layout type we wish to use
    @Override
    public int getItemViewType(int position) {
    	
    	int iViewType = 0;

        if (getFriendCount(position) == 1) {  //Read Group
        	iViewType = TYPE_GROUP_FRIEND1;
        }
        else if (getFriendCount(position) ==2) {
        	iViewType = TYPE_GROUP_FRIEND2;
        }
        else if (getFriendCount(position) ==3) {
        	iViewType =  TYPE_GROUP_FRIEND3;
        }
        else if (getFriendCount(position) >=4) {
        	iViewType =  TYPE_GROUP_FRIEND4;
        }
        //We would like to use the group image if the user added one
        if (getGroupImage(position).length()>0)
        	iViewType = TYPE_GROUP_FRIEND1;
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
            		strWhen= diffDays +" Day ago";
            	else
            		strWhen= diffDays +" Days ago";
            }
            else // Show hrs minutes
            {
            	if (diffMinutes==1)
            		strWhen=diffMinutes + " Min ago";
            	
            	if (diffMinutes>1)
            		strWhen=diffMinutes +" Min ago";
            	
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