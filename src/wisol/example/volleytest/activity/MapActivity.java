package wisol.example.volleytest.activity;

import java.lang.ref.WeakReference;
import java.lang.reflect.Type;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;

import wisol.example.volleytest.JsonContentInstanceDetail;
import wisol.example.volleytest.JsonResponseContentInstanceDetailedLastOne;
import wisol.example.volleytest.MyThingPlugDevices;
import wisol.example.volleytest.MyThingPlugDevices.MyDevices;
import wisol.example.volleytest.R;
import wisol.example.volleytest.ThingPlugDevice;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

public class MapActivity extends FragmentActivity
		implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
		GoogleApiClient.OnConnectionFailedListener {

	final String EXTRA_LOACTION = "locationData";
	final String EXTRA_LATITUDE = "LATITUDE";
	final String EXTRA_LONGITUDE = "LONGITUDE";

	TextView mTvMapDebug;
	Handler mHandler;
	GoogleMap mGoogleMap;
	GoogleApiClient mGoogleApiClient;
	LatLng mGatewayLatLng;
	Location mGatewayLocation;
	Marker mGatewayMarker;
	Marker mMarkerDeviceMax;
	Marker mMarkerDeviceNow;

	ThingPlugDevice mapDevice;

	String THING_AUTHORIZATION;
	String THING_REQ_URI;
	ArrayList<LoRaGpsDevice> mLoRaGpsDevices;
	LoRaGpsDevice mLoRaGpsMaxDistance;
	LoRaGpsDevice mLoRaGpsNow;

	Circle mCircleMaxDistanceRange;

	static boolean isActivated = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map);

		mTvMapDebug = (TextView) findViewById(R.id.tv_mapdebug);

		getExtra(savedInstanceState);
		initDevice();

		mGoogleApiClient = new GoogleApiClient.Builder(this)
				.addConnectionCallbacks(this)
				.addOnConnectionFailedListener(this)
				.addApi(LocationServices.API)
				.build();

		SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
				.findFragmentById(R.id.map);
		mapFragment.getMapAsync(this);

		mHandler = new WeakHandler(this) {
			@Override
			public void handleMessage(Message msg) {
				getThingPlugDeviceContent();
			}
		};
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		isActivated = true;
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		isActivated = false;
		super.onStop();
	}

	static public class WeakHandler extends Handler {
		private final WeakReference<FragmentActivity> mHandlerObj;

		public WeakHandler(FragmentActivity pHandlerObj) {
			mHandlerObj = new WeakReference<FragmentActivity>(pHandlerObj);
		}
	}

	private void initDevice() {
		MyThingPlugDevices myThingPlugDevices = MyThingPlugDevices.getInstance();

		mapDevice = new ThingPlugDevice(
				myThingPlugDevices.getServiceName(MyDevices.MAP),
				myThingPlugDevices.getSclId(MyDevices.MAP),
				myThingPlugDevices.getDeviceId(MyDevices.MAP),
				myThingPlugDevices.getAuthId(MyDevices.MAP),
				myThingPlugDevices.getAuthKey(MyDevices.MAP))
				.setTag("RoLa GPS")
				.registerDevice(true);

		THING_AUTHORIZATION = mapDevice.getAuthorization();
		THING_REQ_URI = mapDevice.getUrlContenInstancesDetailed(0, 1).toString();

		mLoRaGpsDevices = new ArrayList<MapActivity.LoRaGpsDevice>();
	}

	private JsonResponseContentInstanceDetailedLastOne toJsonResponse(JSONObject pJsonObject) {
		try {
			Log.d("json", pJsonObject.toString(3));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Type type = new TypeToken<JsonResponseContentInstanceDetailedLastOne>() {
		}.getType();

		JsonResponseContentInstanceDetailedLastOne response = new GsonBuilder().create().fromJson(
				pJsonObject.toString(), type);

		return response;
	}

	private void updateDeviceLocation(JSONObject pJsonObject) {
		JsonResponseContentInstanceDetailedLastOne response = toJsonResponse(pJsonObject);

		long delayTime = 5010;

		if (response.getCurrentNrOfInstances() == 0) {// No data
			delayTime = 15000;
		} else {
			if (checkDeviceLocation(response.getContentInstanceDetail()) == true) {
				updateDeviceLocationMarker();
			} else {
				delayTime = 8000;
			}
		}

		mHandler.sendEmptyMessageDelayed(0, delayTime);
	}

	private void updateDeviceLocationMarker() {
		if (mMarkerDeviceMax == null) {
			mMarkerDeviceMax = mGoogleMap.addMarker(new MarkerOptions()
					.position(mLoRaGpsMaxDistance.getLatLng())
					.draggable(false)
					.title(mLoRaGpsMaxDistance.getCreationDate().toString())
					.snippet("Distance:" + String.valueOf(getDistanceFromGateway(mLoRaGpsMaxDistance)) + "m")
					.icon(BitmapDescriptorFactory.fromResource(R.drawable.mk_red))
					.flat(true));
		} else {
			mMarkerDeviceMax.setPosition(mLoRaGpsMaxDistance.getLatLng());
			mMarkerDeviceMax.setTitle(mLoRaGpsMaxDistance.getCreationDate().toString());
			mMarkerDeviceMax.setSnippet("Distance:"
					+ String.valueOf(getDistanceFromGateway(mLoRaGpsMaxDistance)) + "m");
		}

		if (mMarkerDeviceNow == null) {
			mMarkerDeviceNow = mGoogleMap.addMarker(new MarkerOptions()
					.position(mLoRaGpsNow.getLatLng())
					.draggable(false)
					.title(mLoRaGpsNow.getCreationDate().toString())
					.snippet("Distance:" + String.valueOf(getDistanceFromGateway(mLoRaGpsNow)) + "m")
					.icon(BitmapDescriptorFactory.fromResource(R.drawable.mk_circle))
					.flat(true));
		} else {
			mMarkerDeviceNow.setPosition(mLoRaGpsNow.getLatLng());
			mMarkerDeviceNow.setTitle(mLoRaGpsNow.getCreationDate().toString());
			mMarkerDeviceNow
					.setSnippet("Distance:" + String.valueOf(getDistanceFromGateway(mLoRaGpsNow)) + "m");
		}
	}

	private boolean checkDeviceLocation(JsonContentInstanceDetail pJsonContentInstanceDetail) {
		boolean result = false;
		double latitude = 0, longitude = 0;

		String[] geoString = pJsonContentInstanceDetail.getContent().split(",");
		Date creationDate = pJsonContentInstanceDetail.getCreationTime();

		if ((geoString.length == 2) && (creationDate != null)) {
			result = true;
			String doublePtnString = "^[\\+\\-]{0,1}[0-9]+[\\.\\,]{1}[0-9]+$";

			try {
				latitude = Double
						.valueOf(geoString[0].replace(doublePtnString, ""));
				longitude = Double
						.valueOf(geoString[1].replace(doublePtnString, ""));

				mLoRaGpsNow = new LoRaGpsDevice()
						.setLatLng(new LatLng(latitude, longitude))
						.setCreationDate(creationDate);

				if (mLoRaGpsMaxDistance == null) {
					mLoRaGpsMaxDistance = mLoRaGpsNow.clone();
					updateMaxDistanceCircle();
				} else {
					if (getDistanceFromGateway(mLoRaGpsNow) > getDistanceFromGateway(mLoRaGpsMaxDistance)) {
						mLoRaGpsMaxDistance = mLoRaGpsNow.clone();
						updateMaxDistanceCircle();
					}
				}

				this.mTvMapDebug.setText("max:" + getDistanceFromGateway(mLoRaGpsMaxDistance) + "m,now:"
						+ getDistanceFromGateway(mLoRaGpsNow) + "m");

			} catch (Exception e) {
				result = false;
				e.printStackTrace();
			}
		}

		return result;
	}

	private void updateMaxDistanceCircle() {
		if (mCircleMaxDistanceRange == null) {
			mCircleMaxDistanceRange = mGoogleMap
					.addCircle(new CircleOptions()
							.center(mGatewayLatLng)
							.radius((double) getDistanceFromGateway(mLoRaGpsMaxDistance))
							.fillColor(0x2c5a7fb1)
							.strokeWidth(0));
		} else {
			mCircleMaxDistanceRange.setRadius((double) getDistanceFromGateway(mLoRaGpsMaxDistance));
		}

		updateCameraBounds();
	}

	private void updateCameraBounds() {
		double diffLat = mGatewayLatLng.latitude - mLoRaGpsMaxDistance.getLatLng().latitude;
		double diffLng = mGatewayLatLng.longitude - mLoRaGpsMaxDistance.getLatLng().longitude;
		double d = Math.sqrt(Math.pow(diffLat, 2) + Math.pow(diffLng, 2));

		double northEastLat = mGatewayLatLng.latitude + d;
		double northEastLng = mGatewayLatLng.longitude + d;
		double southWestLat = mGatewayLatLng.latitude - d;
		double southWestLng = mGatewayLatLng.longitude - d;

		LatLngBounds pBounds = new LatLngBounds(new LatLng(southWestLat, southWestLng), new LatLng(
				northEastLat, northEastLng));
		mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(pBounds, 100), 1000, null);

	}

	private float getDistanceFromGateway(LoRaGpsDevice pDevice) {
		float resultMeter = 0;

		Location pTargetLocation = new Location("Lora Device");
		pTargetLocation.setLatitude(pDevice.getLatLng().latitude);
		pTargetLocation.setLongitude(pDevice.getLatLng().longitude);

		resultMeter = mGatewayLocation.distanceTo(pTargetLocation);

		return resultMeter;
	}

	private boolean addDeviceLocation(JsonContentInstanceDetail pJsonContentInstanceDetail) {
		boolean result = false;
		double latitude = 0, longitude = 0;
		String[] geoString = pJsonContentInstanceDetail.getContent().split(",");
		Date creationDate = pJsonContentInstanceDetail.getCreationTime();

		if ((geoString.length == 2) && (creationDate != null)) {
			result = true;
			String doublePtnString = "^[\\+\\-]{0,1}[0-9]+[\\.\\,]{1}[0-9]+$";

			if (mLoRaGpsDevices.size() > 0) {
				if (creationDate.after(mLoRaGpsDevices.get(0).getCreationDate())) {
					try {
						latitude = Double
								.valueOf(geoString[0].replace(doublePtnString, ""));
						longitude = Double
								.valueOf(geoString[1].replace(doublePtnString, ""));

						this.mLoRaGpsDevices.add(new LoRaGpsDevice()
								.setLatLng(new LatLng(latitude, longitude))
								.setCreationDate(creationDate));
					} catch (Exception e) {
						result = false;
						e.printStackTrace();
					}
				}
			} else {
				try {
					latitude = Double
							.valueOf(geoString[0].replace(doublePtnString, ""));
					longitude = Double
							.valueOf(geoString[1].replace(doublePtnString, ""));
					this.mLoRaGpsDevices.add(new LoRaGpsDevice()
							.setLatLng(new LatLng(latitude, longitude))
							.setCreationDate(creationDate));
				} catch (Exception e) {
					result = false;
					e.printStackTrace();
				}
			}

		}

		this.mTvMapDebug.setText("RoLaDeviceLoc:" + pJsonContentInstanceDetail.getContent() + ":"
				+ ":" +
				String.valueOf(latitude) + "," + String.valueOf(longitude));

		return result;
	}

	private synchronized void getThingPlugDeviceContent() {
		if (mapDevice == null) {
			initDevice();
		}

		if (isActivated == false) {
			Log.d("ThingPlugReq", "Activity is paused~~ ");
			return;
		}

		Volley.newRequestQueue(this).add(
				new StringRequest(Request.Method.GET, THING_REQ_URI, new Response.Listener<String>() {

					@Override
					public void onResponse(String response) {
						try {
							JSONObject jsonObject = XML.toJSONObject(response);
							updateDeviceLocation(jsonObject);
						} catch (JSONException e) {
							e.printStackTrace();
							Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
						}
					}

				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						Toast.makeText(getApplicationContext(), ":Error occured",
								Toast.LENGTH_SHORT).show();
					}
				}) {

					@Override
					public Map<String, String> getHeaders() throws AuthFailureError {
						Map<String, String> headers = new HashMap<String, String>();
						headers.put("Content-Type", "application/xml");
						headers.put("Authorization", THING_AUTHORIZATION);
						headers.put("charset", "UTF-8");

						return headers;
					}
				});

	}

	private void getExtra(Bundle pBundle) {
		String latitude = getIntent().getStringExtra(EXTRA_LATITUDE);
		String longitude = getIntent().getStringExtra(EXTRA_LONGITUDE);
		double doubleLatitude = Double.valueOf(latitude);
		double doubleLongitude = Double.valueOf(longitude);

		mGatewayLatLng = new LatLng(doubleLatitude, doubleLongitude);
		mGatewayLocation = new Location("Gateway");
		mGatewayLocation.setLatitude(doubleLatitude);
		mGatewayLocation.setLongitude(doubleLongitude);

		mTvMapDebug.setText("Gateway Location:" + latitude + "," + longitude);
	}

	@Override
	public void onMapReady(GoogleMap pGoogleMap) {
		mGoogleMap = pGoogleMap;
		mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

		mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mGatewayLatLng, 17));
		mGatewayMarker = mGoogleMap.addMarker(new MarkerOptions()
				.position(mGatewayLatLng)
				.draggable(false)
				.title("Gateway")
				.snippet(mGatewayLatLng.toString())
				.icon(BitmapDescriptorFactory.fromResource(R.drawable.mk_blue))
				.flat(true));

		mHandler.sendEmptyMessageDelayed(0, 10);
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

	public class LoRaGpsDevice implements Cloneable {
		private LatLng mLatLng = null;
		private Date creationDate;

		public LoRaGpsDevice() {

		}

		public LoRaGpsDevice setLatLng(LatLng pLatLng) {
			mLatLng = pLatLng;
			return this;
		}

		public LatLng getLatLng() {
			return this.mLatLng;
		}

		public LoRaGpsDevice setCreationDate(Date pDate) {
			creationDate = pDate;
			return this;
		}

		public Date getCreationDate() {
			return this.creationDate;
		}

		@Override
		protected LoRaGpsDevice clone() throws CloneNotSupportedException {
			LoRaGpsDevice pLoraDevice = (LoRaGpsDevice) super.clone();
			pLoraDevice.mLatLng = this.mLatLng;
			pLoraDevice.creationDate = this.creationDate;

			return pLoraDevice;
		}
	}

}
