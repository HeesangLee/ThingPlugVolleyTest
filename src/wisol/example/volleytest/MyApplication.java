package wisol.example.volleytest;

import android.support.multidex.MultiDexApplication;

import com.firebase.client.Firebase;

public class MyApplication extends MultiDexApplication {
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		new Runnable() {

			@Override
			public void run() {
				initFirebase();

			}
		};
		// Firebase.setAndroidContext(this);
	}

	private void initFirebase() {
		Firebase.setAndroidContext(this);
	}
}
