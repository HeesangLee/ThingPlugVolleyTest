package wisol.example.volleytest.activity;

import wisol.example.volleytest.R;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

public class MapActivity extends FragmentActivity
		implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
		GoogleApiClient.OnConnectionFailedListener {

	TextView mTvMapDebug;
	Handler mHandler;
	GoogleMap mGoogleMap;
	GoogleApiClient mGoogleApiClient;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map);

		mTvMapDebug = (TextView) findViewById(R.id.tv_mapdebug);

		mGoogleApiClient = new GoogleApiClient.Builder(this)
				.addConnectionCallbacks(this)
				.addOnConnectionFailedListener(this)
				.addApi(LocationServices.API)
				.build();

		SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
				.findFragmentById(R.id.map);
		mapFragment.getMapAsync(this);

	}

	@Override
	public void onMapReady(GoogleMap pGoogleMap) {
		mGoogleMap = pGoogleMap;
		mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
	}

	@Override
	public void onConnected(Bundle pBundle) {
		Toast.makeText(this, "onConnected", Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onConnectionSuspended(int arg0) {
		Toast.makeText(this, "onConnectionSuspended", Toast.LENGTH_SHORT).show();

	}

	@Override
	public void onConnectionFailed(ConnectionResult pConnectionResult) {
		Toast.makeText(this, "onConnectionFailed", Toast.LENGTH_SHORT).show();

	}
}
