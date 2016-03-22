package wisol.example.volleytest.activity;

import wisol.example.volleytest.R;
import wisol.example.volleytest.TestService;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.TextView;

public class MenuActivity extends Activity {
	final String EXTRA_THIS_DEVICE = "THIS_DEVICE";
	TextView mTvDebug;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_activity_menu);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		stopService(new Intent(this, TestService.class));
		super.onResume();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		launchTestService();
		super.onPause();
	}
	
	public void launchTestService() {
		Intent i = new Intent(this, TestService.class);

		startService(i);
	}

	public void onClickMenuMsg(View v) {
		Intent intent = new Intent(this, MessageViewActivity.class);
		intent.putExtra(EXTRA_THIS_DEVICE, "MESSAGE");
		startActivity(intent);

		// startActivity(new Intent(this,MessageViewActivity.class));
	}

	public void onClickMenuDoor(View v) {
		startActivity(new Intent(this, DoorViewActivity.class));
	}

	public void onClickMenuMap(View v) {
		if(isLocationServiceEnabled()){
			startActivity(new Intent(this, SelGatewayActivity.class));	
		}else{
			popUpLocationSetting();
		}
		
	}

	public void onButtonClickFireBaseTest(View v) {
		startActivity(new Intent(this, FirebaseTestActivity.class));
	}
	
	private void popUpLocationSetting() {
		AlertDialog.Builder dlgLocation = new AlertDialog.Builder(this);

		dlgLocation.setMessage("Go to Location setting")
				.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
					}
				})
				.setNegativeButton("Exit", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						finishAffinity();
					}
				})
				.setTitle("Location Service is disabled")
				.setIcon(R.drawable.ic_launcher)
				.show();
	}

	private boolean isLocationServiceEnabled() {
		boolean result = false;
		LocationManager lm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
		boolean gps_enabled = false;
		boolean network_enabled = false;

		try {
			gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		try {
			network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		result = gps_enabled | network_enabled;

		return result;
	}
	
	@Override
	public void onBackPressed() {
		finishAffinity();
	}

}
