package com.groupcentric.android;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.groupcentric.api.model.Friend;
import com.groupcentric.util.ImageDownloader;
import com.groupcentric.util.Util;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Data;
import android.provider.ContactsContract.CommonDataKinds.Photo;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;
public class gc_ContactList  extends ListActivity implements OnClickListener{
	ListView lv;
    ContactsAdapter adapter;
    EditText searchText;
    String holdSearchText = "";
    long lContactID = 0;
    String strContactName = "";
    String strContactPhone = "";
    String strContactEmail = "";
    int iContactMultipleEmail = 0;
    int iContactMultiplePhone = 0;
    AlertDialog phoneAlert;

    ArrayList<Friend> tmpContactsToSave = new ArrayList<Friend>();

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putParcelableArrayList("tmpfriends", tmpContactsToSave);
		outState.putString("search_text", searchText.getText().toString());
		super.onSaveInstanceState(outState);
	}

	/**
	* @author support@groupcentric.com
	*	<br>
	* Purpose:List Users Contacts
	* <br>
	* Notes: 
	* <br>
	* Revisions:
	* 
	*
	*/
	@Override
	protected void onPause() {
		try{
		phoneAlert.dismiss();
		}
		catch (Exception ex){
		}
		super.onPause();
	}
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.gc_list_contacts);
        adapter = new ContactsAdapter(this);
        
        if (savedInstanceState != null) {
            try{
                //Get friends if we already have them
            	tmpContactsToSave = savedInstanceState.getParcelableArrayList("tmpfriends") ;
            	holdSearchText = savedInstanceState.getString("search_text");
            }
            catch (Exception ex){
            }
        }
        
        if (getIntent().getExtras()!=null){ // This could have been passed from Start Group Activity 
        	tmpContactsToSave = getIntent().getExtras().getParcelableArrayList("tmpfriends");
         	getIntent().removeExtra("tmpfriends");
        }        

        
        if (tmpContactsToSave == null)
        	tmpContactsToSave = new ArrayList<Friend>();
        if (tmpContactsToSave != null) {
            setCheckedContacts();
        }

        searchText = (EditText) findViewById(R.id.edt_contact_search); 
        findViewById(R.id.btn_done).setOnClickListener(this);
        getListView().setAdapter(adapter);
        getListView().setTextFilterEnabled(true);
        

        
        searchText.addTextChangedListener(new TextWatcher() {

			public void afterTextChanged(Editable arg0) {
				//Clear Selected Contacts when type
				if (!holdSearchText.contentEquals(searchText.getText().toString())){
					updateFooter();
				}
				
				adapter.getFilter().filter(arg0);
				
			}

			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
				// TODO Auto-generated method stub
				
			}

			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
			}
        });
    }

    @Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
    	 adapter.toggleChecked(id);
         if (adapter.isChecked(id)) {
             validateContact(Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_URI, String.valueOf(id)), id);
         } else {
             removeContact(id);
         }
		super.onListItemClick(l, v, position, id);
	}


    static class ContactsAdapter extends CursorAdapter implements Filterable {
        private boolean mEnableChkBox=true;
        protected ImageDownloader imgDownloader;
        Context mContext;
       
        public ContactsAdapter(Context context) {
            super(context, getContactsCursor(context, null), false);
            mContext = context;
            imgDownloader = new ImageDownloader();
        }
        
        private boolean selfChange;
        private HashMap<Long, Boolean> checkedItems = new HashMap<Long, Boolean>();
        private OnCheckedChangeListener chkListener;

        protected class ViewHolder {
        	RelativeLayout lyt_contactRow;
            ImageView img;
            TextView title;
            CheckBox chkBox;
        };

        protected int getIDColumnIdx(Cursor cursor) {
            return cursor.getColumnIndex(ContactsContract.Contacts._ID);
        }

        
        protected int getNameColumnIdx(Cursor cursor) {
            return cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
        }

        protected int getImageColumnIdx(Cursor cursor) {
            return cursor.getColumnIndex(ContactsContract.Contacts.PHOTO_ID);
        }

        private static Cursor getContactsCursor(Context ctx, CharSequence like) {
            String ef = "";
            if (!TextUtils.isEmpty(like)) {
                 ef += ContactsContract.Contacts.DISPLAY_NAME + " like " + "'%" + like + "%'";
            }
            
            if (!TextUtils.isEmpty(ef))
               ef = ef + " and " +  ContactsContract.Contacts.HAS_PHONE_NUMBER + "='1'"; 
            else
            	ef +=  ContactsContract.Contacts.HAS_PHONE_NUMBER + "='1'"; 
            
            return ctx.getContentResolver().query(ContactsContract.Contacts.CONTENT_URI,
                new String[] {
                    ContactsContract.Contacts._ID,
                    ContactsContract.Contacts.DISPLAY_NAME,
                    ContactsContract.Contacts.PHOTO_ID,
                    ContactsContract.Contacts.HAS_PHONE_NUMBER,
                }, ef, null, ContactsContract.Contacts.DISPLAY_NAME);
        }


        protected void initImage(Cursor cursor, ImageView img) {
            String s = cursor.getString(getImageColumnIdx(cursor));
            img.setImageBitmap(null);
            img.setBackgroundResource(R.drawable.gc_blankfriendimg);
            if (!TextUtils.isEmpty(s)) {
                Bitmap bmp = loadContactPhoto(mContext, Integer.parseInt(s));
                if (bmp != null) {
                    img.setImageBitmap(bmp);
                    return;
                }
            }
         }

        @Override
        public Cursor runQueryOnBackgroundThread(CharSequence constraint) {
            return getContactsCursor(mContext, constraint);
        }

        public static Bitmap loadContactPhoto(Context context, long photoId) {
            Cursor photoCursor = null;
            Bitmap photoBm = null;

            try {
                photoCursor = context.getContentResolver().query(
                        ContentUris.withAppendedId(Data.CONTENT_URI, photoId),
                        new String[] { Photo.PHOTO },
                        null, null, null);

                if (photoCursor.moveToFirst() && !photoCursor.isNull(0)) {
                    byte[] photoData = photoCursor.getBlob(0);
                    photoBm = BitmapFactory.decodeByteArray(photoData, 0,
                            photoData.length, null);
                }
            } finally {
                if (photoCursor != null) {
                    photoCursor.close();
                }
            }
            return photoBm;
        }

		@Override
		  public void bindView(View view, Context context, Cursor cursor) {
	        ViewHolder vh = (ViewHolder) view.getTag();
	        vh.title.setText(cursor.getString(getNameColumnIdx(cursor)));
	        initImage(cursor, vh.img);
            long id = 0;
            id = cursor.getLong(cursor.getColumnIndex("_id"));
            vh.chkBox.setChecked(isChecked(id));
            if (isChecked(id)){
            	vh.lyt_contactRow.setBackgroundResource(R.drawable.gc_contactrow_pressed);
            }
            else
            {
            	vh.lyt_contactRow.setBackgroundResource(R.drawable.gc_contactrow); 
            }

	    } 


		
		   @Override
		    public View newView(Context context, Cursor cursor, ViewGroup parent) {
			   ViewHolder vh = new ViewHolder();

		        View v = View.inflate(mContext, R.layout.gc_row_contact, null);
		        vh.img = (ImageView) v.findViewById(R.id.item__image);
		        vh.title = (TextView) v.findViewById(R.id.item__text);
		        vh.chkBox = (CheckBox) v.findViewById(R.id.item__checkbox);
		        vh.lyt_contactRow = (RelativeLayout) v.findViewById(R.id.lyt_contact_row);
		        
		        v.setTag(vh);
		        return v;
		    }
		   
		    public void toggleChecked(long id) {
		        checkedItems.put(id, !isChecked(id));       
		        notifyDataSetChanged();
	        
		    }
		    public boolean isChecked(long id) {
		        Boolean b = checkedItems.get(id);
		        if (b == null) b = false;
		        return b;
		    }

		    public long[] getCheckedItems() {
		        ArrayList<Long> checked = new ArrayList<Long>();
		        for (Long l : checkedItems.keySet())
		            if (isChecked(l))
		                checked.add(l);
		        return Util.convertLongArray(checked);
		    }

		    public void setCheckedData(long[] data) {
		        checkedItems.clear();
		        for (Long l : data)
		            checkedItems.put(l, true);
		        notifyDataSetChanged();
		    }

		    public void resetCheckedItems() {
		        checkedItems.clear();
		        notifyDataSetChanged();
		    }
		    
    }

    void validateContact(Uri myContact, Long lID) {
        lContactID = lID;
        strContactName = "";
        strContactPhone = "";
        strContactEmail = "";
        iContactMultipleEmail = 0;
        iContactMultiplePhone = 0;
        boolean gotMobile = false;
        boolean popAlert = false;

        int iEmailCount = 0;
        ArrayList<String> emailDetail = new ArrayList<String>(); // Email details - home:myemail@test.net
        final ArrayList<String> email = new ArrayList<String>(); // Just email -myemail@test.net
        int iPhoneCount = 0;
        ArrayList<String> phonesDetail = new ArrayList<String>(); // Phone detail - Home:203-555-5251
        final ArrayList<String> phones = new ArrayList<String>(); // Phone - 203-555-5251

        String col0 = ContactsContract.Contacts._ID;
        String col1 = ContactsContract.Contacts.DISPLAY_NAME;
        String col3 = ContactsContract.Contacts.HAS_PHONE_NUMBER;
        Cursor cursor = getContentResolver().query(myContact,
                new String[]{col0, col1, col3,},
                null, null, null);
        if (cursor.moveToFirst()) {
            Friend contactToSave = new Friend();
            contactToSave.setFullname(cursor.getString(cursor.getColumnIndex(col1)));
            contactToSave.setProfilePicURL("/images/noface.png");
            long id = cursor.getLong(cursor.getColumnIndex(col0));
            //Get the DisplayName
            String strName = cursor.getString(
                    cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
            strContactName = strName;
            //Check For Phone Number
            if (Integer.parseInt(cursor.getString(
                    cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                Cursor curPhones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + id, null, null);

                while (curPhones.moveToNext()) {

                    String iPhoneLabel = curPhones.getString(curPhones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
                    String strPhoneLabel = "Other";
                    try{		
                    	strPhoneLabel = ContactsContract.CommonDataKinds.Phone.getTypeLabel(
                            this.getResources(),
                            Integer.parseInt(iPhoneLabel),
                            getString(R.string.gc_undefinedTypeLabel)).toString();
                    }
                    	catch(Exception ex) {
                   	}
                    
                    
                    String strPhone = curPhones.getString(curPhones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    phonesDetail.add(strPhoneLabel + ":" + strPhone);
                    phones.add(strPhone);
                    iPhoneCount++;
                }
                curPhones.close();
                if (iPhoneCount > 1 & !gotMobile) {
                    iContactMultiplePhone = 1;

                } else {
                    strContactPhone = phones.get(0);
                }
            }

         }

        //Show Phone alerts
        //Let the user select the phone number
        if (iPhoneCount > 1 & !gotMobile) {
            popAlert = true;
            final CharSequence[] phonecs = phonesDetail.toArray(new CharSequence[phonesDetail.size()]);
            AlertDialog.Builder phonebuilder = new AlertDialog.Builder(this);
            phonebuilder.setTitle("Select Phone #");
            phonebuilder.setItems(phonecs, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {
                    strContactPhone = phones.get(item);
                    //Only save if use does not have to pick from emails & there is at least one email
                    if (email.isEmpty() == false &  iContactMultipleEmail==0) {
                        strContactEmail = email.get(0).toString();
                    }
                    Friend contactToSave = new Friend();
                    contactToSave.setFullname(strContactName);
                    contactToSave.setPhone(getOnlyNumerics(strContactPhone));
                    contactToSave.setFriendEmail(strContactEmail);
                    contactToSave.setId(lContactID);
                    tmpContactsToSave.add(contactToSave);
                    adapter.toggleChecked(lContactID);
                    dialog.dismiss();
                	updateFooter();
                }
            });

            phoneAlert = phonebuilder.create();
            phoneAlert.show();
        }
        //Show email alert
        //Let the user select the Email Address
        if (iEmailCount > 1) {
            popAlert = true;
            final CharSequence[] cs = emailDetail.toArray(new CharSequence[emailDetail.size()]);
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Select Email");
            builder.setItems(cs, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {
                    strContactEmail = email.get(item).toString();
                    if (iContactMultiplePhone == 0) {
                        Friend contactToSave = new Friend();
                        contactToSave.setFullname(strContactName);
                        contactToSave.setPhone(getOnlyNumerics(strContactPhone));
                        contactToSave.setFriendEmail(strContactEmail);
                        contactToSave.setId(lContactID);
                        tmpContactsToSave.add(contactToSave);
                        adapter.toggleChecked(lContactID);
                    }
                    dialog.dismiss();
                }
            });

            AlertDialog alert = builder.create();
            alert.show();
        }

        if (popAlert == false) {

            if (strContactEmail.length() > 0 | strContactPhone.length() > 0) {
                Friend contactToSave = new Friend();
                contactToSave.setFullname(strContactName);
                contactToSave.setPhone(getOnlyNumerics(strContactPhone));
                contactToSave.setFriendEmail(strContactEmail);
                contactToSave.setId(lContactID);
                tmpContactsToSave.add(contactToSave);
            } else {
				Toast.makeText(getApplicationContext(),	" has no phone number", Toast.LENGTH_SHORT).show();
                adapter.toggleChecked(lContactID);
            }

        } else {
            adapter.toggleChecked(lContactID);
        }

        updateFooter();
    }
    
	public static String getOnlyNumerics(String strNum) {
		/**
		* @author support@groupcentric.com
		*	<br>
		* Purpose:List Users Contacts
		* <br>
		* Notes: 
		* <br>
		* Revisions:
		* 
		*
		*/
	    if (strNum == null) {
	        return null;
	    }

	    StringBuilder strBuff = new StringBuilder();
	    char c;
	    
	    for (int i = 0; i < strNum.length() ; i++) {
	        c = strNum.charAt(i);
	        
	        if (Character.isDigit(c)) {
	            strBuff.append(c);
	        }
	    }
	    return strBuff.toString();
	}
	

    void removeContact(Long lID) {
        for (int i = 0; i < tmpContactsToSave.size(); i++) {
            if (tmpContactsToSave.get(i).getId() == lID) {
            	tmpContactsToSave.remove(i);
                break;
            }
        }
    	updateFooter();
    }

    void setCheckedContacts() {
        for (int i = 0; i < tmpContactsToSave.size(); i++) {
            adapter.toggleChecked(tmpContactsToSave.get(i).getId());
        }
        updateFooter();
    }

    public List<Friend> getSelectedContacts() {
        return tmpContactsToSave;
    }

	public void onClick(View v) {
		if (v.getId() == R.id.btn_done) {
            setResult(RESULT_OK, new Intent()
            .putParcelableArrayListExtra("EXTRA_CONTACTS", (ArrayList<Friend>) tmpContactsToSave)
         );
                   finish();
		}
	}
	
	public void updateFooter() {
		String strFriends ="";
		if (tmpContactsToSave.size()==1)
			strFriends = this.getResources().getString(R.string.gc_friend);
		else
			strFriends = this.getResources().getString(R.string.gc_friends);
			
		((TextView) findViewById(R.id.txt_contact_footer)).setText(Html.fromHtml(this.getResources().getString(R.string.gc_you_have_added) + " <b>" + tmpContactsToSave.size() + "</b> " + strFriends));
	}
	
}