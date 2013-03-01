package com.groupcentric.android;

import com.groupcentric.api.ws.BaseTask;
import com.groupcentric.api.ws.ErrorResponse;
import com.groupcentric.api.ws.TaskContext;
import com.groupcentric.api.ws.WS16UpdateUAToken;
import com.urbanairship.push.PushManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
public class gc_UpdateUATokenService extends Service implements TaskContext {


    private PowerManager pm;
    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
        };
    };

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private gc_Variables var;
    private WS16UpdateUAToken task;



    private void cancelUpdate() {
        mHandler.removeMessages(0);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        var = new gc_Variables(); //finish instantiating the GroupcentricVariables object which will give access to the Groupcentric UserID and APIKey
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
    	updateUAToken();
		return START_STICKY;
    }

    public void updateUAToken() {
        if (task != null && task.isInProgress())
            task.cancel(true);
        
        String strUAToken = "";
        if (PushManager.shared().getAPID()!=null)
        	strUAToken = PushManager.shared().getAPID();

        if (var.getGroupcentricUserID(this) >0) {
        	task = new WS16UpdateUAToken(gc_UpdateUATokenService.this, var.getGroupcentricUserID(this), var.getGroupcentricAPIKey(this), 2, strUAToken);
        	task.execute();
        }
        else {
        	stopSelf();
        }
        	
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        cancelUpdate();
    }
    
	public void onError(ErrorResponse err) {
		stopSelf();
		
	}

	public void onSuccess(BaseTask<?, ?, ?> task) {
		if (task instanceof WS16UpdateUAToken) {
			//Add any post processes here -
		}
		stopSelf();
		
	}

	public void setInProgress(boolean inProgress) {
		// TODO Auto-generated method stub
		
	}

	public void onTaskFinish() {
		// TODO Auto-generated method stub
		
	}

	public Context getContext() {
		// TODO Auto-generated method stub
		return this;
	}

	public void onCancel() {
		// TODO Auto-generated method stub
		
	}

	public boolean shouldShowProgress() {
		return false;
	}
}
