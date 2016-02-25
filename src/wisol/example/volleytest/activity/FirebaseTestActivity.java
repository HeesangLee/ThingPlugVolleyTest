package wisol.example.volleytest.activity;

import wisol.example.volleytest.R;
import android.app.Activity;
import android.os.Bundle;

import com.firebase.client.Firebase;

public class FirebaseTestActivity extends Activity {

	final String mFirebaseUri = "https://shining-fire-4201.firebaseio.com";
	private Firebase mFirebaseRef;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_firebase_test);
//		Firebase.setAndroidContext(this);

//		initFirebase();
	}

	private void initFirebase() {
		mFirebaseRef = new Firebase(mFirebaseUri);
		
		Firebase newUser = mFirebaseRef.child("user").child("new man");
		User newMan = new User("HeesangLee", 19771012);
		newUser.setValue(newMan);
	}

	public class User {
		private int birthYear;
		private String fullName;

		public User() {

		}

		public User(String pFullName, int pBirthYear) {
			this.fullName = pFullName;
			this.birthYear = pBirthYear;
		}
		
		public long getBirthYear(){
			return birthYear;
		}
		public String getFullName(){
			return this.fullName;
		}

	}

}
