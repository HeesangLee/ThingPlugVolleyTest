package wisol.example.volleytest.activity;

import wisol.example.volleytest.R;
import android.app.Activity;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

public class SelGatewayActivity extends Activity
		implements ConnectionCallbacks, OnConnectionFailedListener, LocationListener {
	ImageView mImageGeoMarkTop;
	TextView mTextViewLocation;
	Button mBtnGatewayHere;
	Button mBtnEditLocation;
	LinearLayout mLayoutManualInput;
	EditText mTextLatitude;
	EditText mTextLongitude;
	Button mBtnSetGateway;

	private static final String TAG = MainActivity.class.getSimpleName();
	private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;
	private Location mLastLocation;
	// Google client to interact with Google API
	private GoogleApiClient mGoogleApiClient;
	// boolean flag to toggle periodic location updates
	private boolean mRequestingLocationUpdates = false;
	private LocationRequest mLocationRequest;

	// Location updates intervals in sec
	private static int UPDATE_INTERVAL = 20000;
	private static int FATEST_INTERVAL = 10000;
	private static int DISPLACEMENT = 5;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sel_gateway);

		initUiComponents();

		if (checkPlayServices()) {
			buildGoogleApiClient();
			createLocationRequest();
		}
	}

	/**
	 * Creating location request object
	 * */
	protected void createLocationRequest() {
		mLocationRequest = new LocationRequest();
		mLocationRequest.setInterval(UPDATE_INTERVAL);
		mLocationRequest.setFastestInterval(FATEST_INTERVAL);
		mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
		mLocationRequest.setSmallestDisplacement(DISPLACEMENT); // 10 meters
	}

	private void displayLocation() {

		mLastLocation = LocationServices.FusedLocationApi
				.getLastLocation(mGoogleApiClient);

		if (mLastLocation != null) {
			double latitude = mLastLocation.getLatitude();
			double longitude = mLastLocation.getLongitude();

			mTextViewLocation.setText(latitude + ", " + longitude);
			mBtnGatewayHere.setVisibility(View.VISIBLE);

		} else {

			mTextViewLocation
					.setText("Couldn't get the location. Make sure location is enabled on the device");
		}
	}

	private void initUiComponents() {
		mImageGeoMarkTop = (ImageView) findViewById(R.id.iv_selgw_mark);
		mTextViewLocation = (TextView) findViewById(R.id.tv_selgw_location);
		mBtnGatewayHere = (Button) findViewById(R.id.btn_gatewayhere);
		mBtnEditLocation = (Button) findViewById(R.id.btn_editlocation);
		mLayoutManualInput = (LinearLayout) findViewById(R.id.selgateway_manual_location);
		mTextLatitude = (EditText) findViewById(R.id.manual_latitude);
		mTextLongitude = (EditText) findViewById(R.id.manual_longitude);
		mBtnSetGateway = (Button) findViewById(R.id.btn_setgateway);

		mLayoutManualInput.setVisibility(View.GONE);
		mTextViewLocation.setText("HERE");
		mBtnGatewayHere.setVisibility(View.GONE);
	}

	public void onClickYouAreAt(View v) {
		if (mImageGeoMarkTop.getVisibility() == View.GONE) {
			mLayoutManualInput.setVisibility(View.GONE);
			mImageGeoMarkTop.setVisibility(View.VISIBLE);
			mBtnEditLocation.setVisibility(View.VISIBLE);
		}
	}

	public void onClickUpdateGeo(View v) {
		if (mRequestingLocationUpdates) {
			mRequestingLocationUpdates = false;
			stopLocationUpdates();
			mImageGeoMarkTop.setImageResource(R.drawable.mark_uarat);
		} else {
			mRequestingLocationUpdates = true;
			startLocationUpdates();
			mImageGeoMarkTop.setImageResource(R.drawable.mark_uaratupdate);
		}
	}

	public void onClickGatewayHere(View v) {

	}

	public void onClickEditLocation(View v) {
		mLayoutManualInput.setVisibility(View.VISIBLE);
		mImageGeoMarkTop.setVisibility(View.GONE);
		mBtnEditLocation.setVisibility(View.GONE);
	}

	public void onClickSetGateway(View v) {

	}

	protected synchronized void buildGoogleApiClient() {
		mGoogleApiClient = new GoogleApiClient.Builder(this)
				.addConnectionCallbacks(this)
				.addOnConnectionFailedListener(this)
				.addApi(LocationServices.API).build();
	}

	/**
	 * Starting the location updates
	 * */
	protected void startLocationUpdates() {

		LocationServices.FusedLocationApi.requestLocationUpdates(
				mGoogleApiClient, mLocationRequest, this);

	}

	/**
	 * Stopping location updates
	 */
	protected void stopLocationUpdates() {
		LocationServices.FusedLocationApi.removeLocationUpdates(
				mGoogleApiClient, this);
	}

	/**
	 * Method to verify google play services on the device
	 * */
	private boolean checkPlayServices() {
		int resultCode = GooglePlayServicesUtil
				.isGooglePlayServicesAvailable(this);
		if (resultCode != ConnectionResult.SUCCESS) {
			if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
				GooglePlayServicesUtil.getErrorDialog(resultCode, this,
						PLAY_SERVICES_RESOLUTION_REQUEST).show();
			} else {
				Toast.makeText(getApplicationContext(),
						"This device is not supported.", Toast.LENGTH_LONG)
						.show();
				finish();
			}
			return false;
		}
		return true;
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();

		if (mGoogleApiClient != null) {
			mGoogleApiClient.connect();
		}
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

		checkPlayServices();
		if (mGoogleApiClient.isConnected() && mRequestingLocationUpdates) {
			startLocationUpdates();
		}
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		if (mGoogleApiClient.isConnected()) {
			mGoogleApiClient.disconnect();
		}
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		stopLocationUpdates();
	}

	@Override
	public void onConnectionFailed(ConnectionResult result) {
		Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = "
				+ result.getErrorCode());

	}

	@Override
	public void onConnected(Bundle connectionHint) {
		displayLocation();
		if (mRequestingLocationUpdates) {
			startLocationUpdates();
		}
	}

	@Override
	public void onConnectionSuspended(int cause) {
		mGoogleApiClient.connect();
	}

	@Override
	public void onLocationChanged(Location location) {
		// Assign the new location
		mLastLocation = location;
		Toast.makeText(getApplicationContext(), "Location changed!",
				Toast.LENGTH_SHORT).show();
		displayLocation();

	}
}
