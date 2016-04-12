package util;

import android.app.KeyguardManager;
import android.app.KeyguardManager.KeyguardLock;
import android.app.admin.DeviceAdminReceiver;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

/*
 * 	http://developer.android.com/guide/topics/admin/device-admin.html
 * 	http://rootfs.wordpress.com/2010/09/09/android-make-your-application-a-device-administrator/
 *  http://stackoverflow.com/questions/10893498/android-lock-screen-show-again-after-it-has-been-disabled-using-lock-disablekey
 * 
 * */
public class LockScreenUtil {
	
	// must define class in manifest file
    public static class MyAdmin extends DeviceAdminReceiver {}
	
	public static void run(Context context){
    	DevicePolicyManager mDPM;
    	ComponentName mAdminName;
       	// KeyguardManager keyguardManager;
       	// KeyguardLock lock;
        mDPM = (DevicePolicyManager)context.getSystemService(Context.DEVICE_POLICY_SERVICE);
        mAdminName = new ComponentName(context, MyAdmin.class);
       	// keyguardManager = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
       	// lock = keyguardManager.newKeyguardLock(Context.KEYGUARD_SERVICE);
        
        boolean isAdminActive = mDPM.isAdminActive(mAdminName);
        
        if(!isAdminActive){
        	try {
	        	Intent it = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
	        	it.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, mAdminName);
	        	it.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,
	        		"If you want to Uninstall this app.\n"
	        		+ "Go to Security -> Device administrators\n"
	        		+ "And check out [ Soft Lock Screen ] first.");
	        	context.startActivity(it);
        	}
        	catch(Exception e){
        		Toast.makeText(context, "Activate app from App Drawer first.", Toast.LENGTH_LONG).show();
        	}
        }
        else {
          	// lock.disableKeyguard();
           	// lock.reenableKeyguard();
        	mDPM.lockNow();
        }
	}

}