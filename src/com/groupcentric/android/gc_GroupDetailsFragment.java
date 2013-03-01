package com.groupcentric.android;
import com.groupcentric.api.model.Group;
import com.groupcentric.util.ImageDownloader;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

public class gc_GroupDetailsFragment extends Fragment  implements OnClickListener{
	/**
	* @author support@groupcentric.com
	*	<br>
	* Purpose:User Group Details FRAGMENT
	* <br>
	* Notes: (Details Tab Of GroupDetails)
	* <br>
	* Revisions:
	* 
	*
	*/
    private ImageView groupImage1;
    private ImageView groupImage2;
    private ImageView groupImage3;
    private ImageView groupImage4;
    private ImageDownloader imgDownload;
    private RelativeLayout rlayout_group_pic;

    
        @Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
        rlayout_group_pic = (RelativeLayout) getActivity().findViewById(R.id.lyt_group_pic);
        groupImage1 = (ImageView) getActivity().findViewById(R.id.imgGroupPic);
        groupImage2 = (ImageView) getActivity().findViewById(R.id.imgGroupPic2);
        groupImage3 = (ImageView) getActivity().findViewById(R.id.imgGroupPic3);
        groupImage4 = (ImageView) getActivity().findViewById(R.id.imgGroupPic4);
        rlayout_group_pic.setOnClickListener(this);
        getActivity().findViewById(R.id.imgBtn_PushToggle).setOnClickListener(this);
        getActivity().findViewById(R.id.imgBtn_LeaveGroup).setOnClickListener(this);
        getActivity().findViewById(R.id.edt_group_title).setOnClickListener(this);    
        refreshGroupDetails();
	}

		@Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.gc_group_details_fragment, container, false);
            imgDownload = new ImageDownloader();
             return v;
        }
        
    	public void refreshGroupDetails() { 
    	//	try {
      			Group groupDetails = (Group) ((gc_GroupDetails) getActivity()).getGroup();
      			if (groupDetails==null)
      				return;
      		    

      			String strGroupImage1    = "";
      			boolean hasGroupImage = false;
       			 
      		if (groupDetails.getGroupimage()!=null)
      			if (groupDetails.getGroupimage().indexOf("http")!=-1) {
      				strGroupImage1 = groupDetails.getGroupimage();
    	   			imgDownload.download(strGroupImage1, groupImage1);
      				hasGroupImage = true;
      			}
      			else 
      				if (groupDetails.getFriends().size()>0) {
	      				strGroupImage1 = groupDetails.getFriends().get(0).getProfilePicURL();
	      				imgDownload.download(strGroupImage1, groupImage1);
      				}
      			
       	   		if (hasGroupImage)
      	   			setupGroupImage(1);
      	   		else
      	   		{
      	   	    // No group image so we need to use friends image
      	   			int iFriendCount = groupDetails.getFriends().size();
      	   			if (iFriendCount>=2) 
      	   				imgDownload.download( groupDetails.getFriends().get(1).getProfilePicURL(), groupImage2);
      	   			
      	   			if (iFriendCount>=3) 
      	   				imgDownload.download( groupDetails.getFriends().get(2).getProfilePicURL(), groupImage3);
      	   			
      	   			if (iFriendCount>=4) 
      	   				imgDownload.download( groupDetails.getFriends().get(3).getProfilePicURL(), groupImage4);
      	   			
      	   			setupGroupImage(iFriendCount);
      	   		}	
      	   		

      			((EditText) this.getActivity().findViewById(R.id.edt_group_title)).setText(groupDetails.getGroupname());
      			String strGroupStartedBy = getActivity().getString(R.string.gc_group_started_by);
      			((TextView) this.getActivity().findViewById(R.id.txt_started_by)).setText(strGroupStartedBy + " " + groupDetails.getGroupcreator());
      			if (groupDetails.getPushStatus().contentEquals("-1"))
      				getActivity().findViewById(R.id.imgBtn_PushToggle).setBackgroundResource(R.drawable.gc_pushnotifications_off);
      			else
      				getActivity().findViewById(R.id.imgBtn_PushToggle).setBackgroundResource(R.drawable.gc_pushnotifications_on);
    	}

		private void setupGroupImage(int imageCount) {

           //Hide other images and let setup display as desired
			groupImage2.setVisibility(View.GONE);
			groupImage3.setVisibility(View.GONE);
			groupImage4.setVisibility(View.GONE);
   			
  			// Get the size of the picture frame
  			Drawable dPicFrame = getActivity().getResources().getDrawable(R.drawable.gc_changegrouppic);
			int picFrameH = dPicFrame.getIntrinsicHeight();
			int picFrameW = dPicFrame.getIntrinsicWidth();

			if (imageCount==1)
			{
	  			LayoutParams image1params = new RelativeLayout.LayoutParams(picFrameW, 
	            		  picFrameH);
	  			image1params.addRule(RelativeLayout.ALIGN_RIGHT, rlayout_group_pic.getId());
	  			image1params.addRule(RelativeLayout.ALIGN_BOTTOM, rlayout_group_pic.getId());
	  			groupImage1.setLayoutParams(image1params);
	  			groupImage1.setVisibility(View.VISIBLE);
			}
  			

			if (imageCount==2)
			{
			  // Image 1
	  			LayoutParams image1params = new RelativeLayout.LayoutParams(picFrameW, 
	            		  picFrameH);
	  			image1params.addRule(RelativeLayout.ALIGN_RIGHT, rlayout_group_pic.getId());
	  			image1params.addRule(RelativeLayout.ALIGN_BOTTOM, rlayout_group_pic.getId());
	  			groupImage1.setLayoutParams(image1params);
				
	  		// Image 2
	  			LayoutParams image2params = new RelativeLayout.LayoutParams(picFrameW,   picFrameH/2);
	  			image2params.addRule(RelativeLayout.ALIGN_RIGHT, rlayout_group_pic.getId());
	  			image2params.addRule(RelativeLayout.ALIGN_BOTTOM, rlayout_group_pic.getId());
	  			groupImage2.setLayoutParams(image2params);
	  			groupImage2.setVisibility(View.VISIBLE);
			}
			
			if (imageCount==3)
			{
			  // Image 1
	  			LayoutParams image1params = new RelativeLayout.LayoutParams(picFrameW, 
	            		  picFrameH);
	  			image1params.addRule(RelativeLayout.ALIGN_RIGHT, rlayout_group_pic.getId());
	  			image1params.addRule(RelativeLayout.ALIGN_BOTTOM, rlayout_group_pic.getId());
	  			groupImage1.setLayoutParams(image1params);
				
	  		// Image 2
	  			LayoutParams image2params = new RelativeLayout.LayoutParams(picFrameW/2,   picFrameH/2);
	  			image2params.addRule(RelativeLayout.ALIGN_LEFT, rlayout_group_pic.getId());
	  			image2params.addRule(RelativeLayout.ALIGN_BOTTOM, rlayout_group_pic.getId());
	  			groupImage2.setLayoutParams(image2params);
	  			groupImage2.setVisibility(View.VISIBLE);
	  			
		  	// Image 3
	  			LayoutParams image3params = new RelativeLayout.LayoutParams(picFrameW/2,   picFrameH/2);
	  			image3params.addRule(RelativeLayout.ALIGN_RIGHT, rlayout_group_pic.getId());
	  			image3params.addRule(RelativeLayout.ALIGN_BOTTOM, rlayout_group_pic.getId());
	  			groupImage3.setLayoutParams(image3params);
	  			groupImage3.setVisibility(View.VISIBLE);
			}
			
			if (imageCount>=4)
			{
			  // Image 1
	  			LayoutParams image1params = new RelativeLayout.LayoutParams(picFrameW/2,picFrameH/2);
	  			image1params.addRule(RelativeLayout.ALIGN_LEFT, rlayout_group_pic.getId());
	  			image1params.addRule(RelativeLayout.ALIGN_TOP, rlayout_group_pic.getId());
	  			groupImage1.setLayoutParams(image1params);
				
	  		// Image 2
	  			LayoutParams image2params = new RelativeLayout.LayoutParams(picFrameW/2,picFrameH/2);
	  			image2params.addRule(RelativeLayout.ALIGN_RIGHT, rlayout_group_pic.getId());
	  			image2params.addRule(RelativeLayout.ALIGN_TOP, rlayout_group_pic.getId());
	  			groupImage2.setLayoutParams(image2params);
	  			groupImage2.setVisibility(View.VISIBLE);
	  			
		  	// Image 3
	  			LayoutParams image3params = new RelativeLayout.LayoutParams(picFrameW/2,picFrameH/2);
	  			image3params.addRule(RelativeLayout.ALIGN_LEFT, rlayout_group_pic.getId());
	  			image3params.addRule(RelativeLayout.ALIGN_BOTTOM, rlayout_group_pic.getId());
	  			groupImage3.setLayoutParams(image3params);
	  			groupImage3.setVisibility(View.VISIBLE);
	  			
			// Image 4
	  			LayoutParams image4params = new RelativeLayout.LayoutParams(picFrameW/2,picFrameH/2);
	  			image4params.addRule(RelativeLayout.ALIGN_RIGHT, rlayout_group_pic.getId());
	  			image4params.addRule(RelativeLayout.ALIGN_BOTTOM, rlayout_group_pic.getId());
	  			groupImage4.setLayoutParams(image4params);
	  			groupImage4.setVisibility(View.VISIBLE);
			}
		}
		
		public void onClick(View v) {
			if (v.getId() == R.id.imgBtn_PushToggle) {
				((gc_GroupDetails) getActivity()).toggleGroupPush();
			}
			
			if (v.getId() == R.id.imgBtn_LeaveGroup) {
				((gc_GroupDetails) getActivity()).removeGroup();
			}
			if (v.getId() == R.id.edt_group_title) {
				((gc_GroupDetails) getActivity()).editTitle();
			}
			if (v.getId() == R.id.lyt_group_pic) {
				((gc_GroupDetails) getActivity()).changeGroupPic();
			}
			
		}

    }

