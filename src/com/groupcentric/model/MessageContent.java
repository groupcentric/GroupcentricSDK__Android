package com.groupcentric.model;

import java.text.SimpleDateFormat;
import android.os.Parcel;
import android.os.Parcelable;

public class MessageContent implements Parcelable{
    private String title;
    private String subtitle;
    private String dateof;
    private String details;
    private String imageurl;
    private String url;
    private String markup;
    private String type;
    private int id;
    
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");  //should try to have time too
    
    public static final Parcelable.Creator<MessageContent> CREATOR = new Parcelable.Creator<MessageContent>() {
        public MessageContent createFromParcel(Parcel in) {
            return new MessageContent(in);
        }

		@Override
		public MessageContent[] newArray(int size) {
			// TODO Auto-generated method stub
			return null;
		}

    };

    private MessageContent(Parcel in) {
        readFromParcel(in);
    }

    public void readFromParcel(Parcel in) {
    	title = in.readString();
        subtitle = in.readString();
        dateof = in.readString();
        details = in.readString();
        imageurl = in.readString();
        url = in.readString();
        markup = in.readString();
        type = in.readString();
        id = in.readInt();
    }

    public MessageContent() {
    	title = "";
        subtitle = "";
        dateof = "";
        details = "";
        imageurl = "";
        url = "";
        markup = "";
        type = "";
    }

    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }

    public void setSubtitle(String subt) {
        this.subtitle = subt;
    }

    public String getSubtitle() {
        return subtitle;
    }
    public void setDateof(String dateof) {
        this.dateof = dateof;
    }

    public String getDateof() {
    	return dateof;
    }
    public void setDetails(String subt) {
        this.details = subt;
    }

    public String getDetails() {
        return details;
    }
    public void setMarkup(String subt) {
        this.markup = subt;
    }

    public String getMarkup() {
        return markup;
    }
    public void setImageURL(String subt) {
        this.imageurl = subt;
    }

    public String getImageURL() {
        return imageurl;
    }
    
    public void setURL(String subt) {
        this.url = subt;
    }

    public String getURL() {
        return url;
    }

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		dest.writeString(title);
		dest.writeString(subtitle);
	    dest.writeString(dateof);
	    dest.writeString(details);
	    dest.writeString(imageurl);
	    dest.writeString(url);
	    dest.writeString(markup);
	    dest.writeString(type);
	    dest.writeInt(id);
		
	}
}
