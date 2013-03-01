package com.groupcentric.android;

import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.TextView;

/**
 * @author support@groupcentric.com <br>
 *         Purpose: Handles dynamic profile webview content <br>
 *         Notes: <br>
 *         Revisions:
 * 
 * 
 */
public class gc_ProfileWebView extends Activity implements OnClickListener {
	private gc_Variables var;
    WebView webview_profile;
    TextView txtHeader;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gc_activity_profile_webview);
		var = new gc_Variables(); //finish instantiating the GroupcentricVariables object which will give access to the Groupcentric UserID and APIKey
		webview_profile = (WebView) findViewById(R.id.webView_profile);
		txtHeader = (TextView) findViewById(R.id.txt_profile_header); 
		String strWebUrl = "";
		String strHeader = "";
		if (getIntent().getExtras() != null) {
			if (getIntent().getExtras().getString("web_url") != null) {
				strWebUrl = getIntent().getExtras().getString("web_url");
			}
			if (getIntent().getExtras().getString("header_text") != null) {
				strHeader = getIntent().getExtras().getString("header_text");
			}
		}
		txtHeader.setText(strHeader);
		webview_profile.getSettings().setJavaScriptEnabled(true);
		webview_profile.loadUrl(strWebUrl);
		findViewById(R.id.imgBack).setOnClickListener(this);
	}

	public void onClick(View v) {
		if (v.getId() == R.id.imgBack) {
			finish(); // Go back
		} 
	}
	
}
