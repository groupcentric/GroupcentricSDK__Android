package com.groupcentric.android;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

/**
 * @author support@groupcentric.com
 *	<br>
 * Purpose: Allow the user to edit the group name
 * <br>
 * Notes: 
 * <br>
 * Revisions:
 * 
 *
 */
public class gc_EditGroupName extends Activity implements OnClickListener {
    String passedGroupName = "";
    EditText edtGroupname;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gc_activity_edit_group_name);
		findViewById(R.id.btn_cancel).setOnClickListener(this);
		findViewById(R.id.btn_done).setOnClickListener(this);
		
		edtGroupname = (EditText) findViewById(R.id.edt_group_title);
		
		edtGroupname.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(edtGroupname.getWindowToken(), 0);
                        validateGroupname();
                        return true;
                    }
                else                  
				return false;
            }
        });
        
        if (getIntent().getExtras() != null) {
            if (getIntent().getExtras().getString("group_name") != null) {
            	passedGroupName = getIntent().getExtras().getString("group_name");
            	edtGroupname.setText(passedGroupName);          
            }
        }

        
         WindowManager.LayoutParams lp = getWindow().getAttributes(); 
         lp.dimAmount = 0.5f; 
         getWindow().setAttributes(lp); 
		 getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);


	}
	
	public void onClick(View v) {
		if (v.getId() == R.id.btn_done) {
			validateGroupname();
		} else if (v.getId() == R.id.btn_cancel) {
			finish();
		}
	}
	
	void validateGroupname() {
		edtGroupname.setText(edtGroupname.getText().toString().trim());
		if (passedGroupName.contentEquals(edtGroupname.getText())) {
			finish(); // No changes made
			return;
		}
		
		if (edtGroupname.getText().length()==0) {
			Toast.makeText(this,"Group name required",Toast.LENGTH_SHORT).show();
			edtGroupname.setText("");
			return;
		}
		
		//User edited group name
		setResult(Activity.RESULT_OK, new Intent().putExtra("group_name", edtGroupname.getText().toString()));
		finish();
		
	}
	

}
