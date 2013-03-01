package com.groupcentric.android;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.groupcentric.api.model.GCNotification;
import com.groupcentric.util.ImageDownloader;

public class gc_NotificationsAdapter extends ArrayAdapter<GCNotification>{
    private static final int TYPE_NOTIFICATION_0 = 0;
    ImageDownloader imgDownloader;
    protected final Context mContext;
    private final class ViewHolder {
         TextView txtTitle;
         TextView txtMessage;
         TextView txtDate;
         RelativeLayout rel_layout; 
         RelativeLayout rel_new_notification;
         ImageView imgNotification;

    }

	@Override
	public GCNotification getItem(int position) {
		return super.getItem(position);
	}
	
    public int getNotificationType(int position) {
       // Default to 0  We can use this for different layout types
    	return 0;
    }
   
  //  private static Context mContext;
    public gc_NotificationsAdapter(Context context, int textViewResourceId, ArrayList<GCNotification> items) {
            super(context, textViewResourceId, items);
            mContext = context;
            imgDownloader = new ImageDownloader();
    }
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder vh;
            GCNotification item = getItem(position);
            // get the view type we wish to use
            int type = getItemViewType(position);
            if (convertView == null) {
                vh = new ViewHolder();

                switch (type) {
                    case TYPE_NOTIFICATION_0:
                        convertView = View.inflate(mContext, R.layout.gc_row_notification, null);
                        vh.rel_layout = (RelativeLayout)  convertView.findViewById(R.id.rel_lyt_notification);
                        vh.rel_new_notification = (RelativeLayout)  convertView.findViewById(R.id.lytNotificationIndicator);
                        vh.imgNotification= (ImageView) convertView.findViewById(R.id.imgNotificationPic);
                        vh.txtTitle= (TextView) convertView.findViewById(R.id.txtNotificationTitle);
                        vh.txtMessage= (TextView) convertView.findViewById(R.id.txtNotificationMessage);
                        vh.txtDate= (TextView) convertView.findViewById(R.id.txtNotificationDate);
                        break;
                        
                }
                convertView.setTag(vh);
  

            } else {
                vh = (ViewHolder) convertView.getTag(); 
            }
            //Alternate background
            if (position % 2 == 0)
            	convertView.setBackgroundResource(R.drawable.gc_list_item_notification_1_bg);
            else
            	convertView.setBackgroundResource(R.drawable.gc_list_item_notification_2_bg);
            //New notification indicator
            if (item.getNewcontent()==1)
            	vh.rel_new_notification.setVisibility(View.VISIBLE);
            else
            	vh.rel_new_notification.setVisibility(View.GONE);
            
            vh.txtTitle.setText(item.getTitle() ); 
            vh.txtMessage.setText(item.getSubtitle());
            SimpleDateFormat formatter = new SimpleDateFormat("MMMM dd");
            StringBuilder CurrentDate = new StringBuilder(formatter.format(item.getDateof()));
            vh.txtDate.setText(CurrentDate) ;
            imgDownloader.download(item.getImageurl(), vh.imgNotification);

            
            switch (type) {
                case TYPE_NOTIFICATION_0:  // Normal Notification
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
    	iViewType =  getNotificationType(position);
    		
        if (iViewType == 0) {  //Standand Notification
        	iViewType = TYPE_NOTIFICATION_0;
        }
        
		iViewType = 0;
        return iViewType; 
    }
 
}