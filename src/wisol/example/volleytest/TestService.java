package wisol.example.volleytest;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;

import wisol.example.volleytest.MyThingPlugDevices.MyDevices;
import wisol.example.volleytest.activity.DoorViewActivity;
import wisol.example.volleytest.activity.MainActivity;
import wisol.example.volleytest.activity.MenuActivity;
import wisol.example.volleytest.activity.MessageViewActivity;
import wisol.example.volleytest.activity.SelGatewayActivity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

public class TestService extends Service {
	private volatile HandlerThread mHandlerThread;
	private ServiceHandler mServiceHandler;

	ThingPlugDevice mapDevice;

	// Date mCreationTimePreMap;
	ArrayList<ThingPlugDevice> mThingPlugDevices;
	// ArrayList<Date> mPreCreationTimeArray;
	Map<String, Date> mPreCreationDateMap;
	int mNewDataCount = 0;
	String preUpdatedDeviceName = "";
	int checkDeviceIndex = 0;

	private final class ServiceHandler extends Handler {
		public ServiceHandler(Looper looper) {
			super(looper);
		}

		@Override
		public void handleMessage(Message msg) {
			this.sendEmptyMessageDelayed(0, 1500);
			if (isNetworkConnected()) {
				checkNewData(mThingPlugDevices.get(checkDeviceIndex));
				// for (ThingPlugDevice pDevice : mThingPlugDevices) {
				// checkNewData(pDevice);
				// }
				checkDeviceIndex = checkDeviceIndex < mThingPlugDevices.size() - 1 ? checkDeviceIndex + 1 : 0;
			}
		}
	}

	private boolean isNetworkConnected() {
		boolean result = false;
		ConnectivityManager manager = (ConnectivityManager) getApplicationContext()
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		final boolean isMobileConnected = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnected();
		final boolean isWifiConnected = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnected();

		result = isMobileConnected | isWifiConnected;

		return result;
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

	synchronized private void checkNewDataUpdate(JSONObject pJsonObject, String kName) {
		JsonResponseContentInstanceDetailedLastOne response = toJsonResponse(pJsonObject);

		if (response.getCurrentNrOfInstances() != 0) {
			Date pCreationTime = response.getContentInstanceDetail().getCreationTime();
			if (mPreCreationDateMap.containsKey(kName)) {
				if (pCreationTime.after(mPreCreationDateMap.get(kName))) {
					initNotification(response.getContentInstanceDetail(), kName);
					mPreCreationDateMap.put(kName, pCreationTime);
				}
			} else {
				mPreCreationDateMap.put(kName, pCreationTime);
			}
		}
	}

	synchronized private void checkNewData(ThingPlugDevice pThingPlugDevice) {
		final ThingPlugDevice thingPlugDevice = pThingPlugDevice;

		Volley.newRequestQueue(getApplicationContext()).add(
				new StringRequest(Request.Method.GET, thingPlugDevice.getUrlContenInstancesDetailed(0, 1)
						.toString(), new Response.Listener<String>() {

					@Override
					public void onResponse(String response) {
						try {
							JSONObject jsonObject = XML.toJSONObject(response);
							// checkMapDataUpdate(jsonObject);
							checkNewDataUpdate(jsonObject, thingPlugDevice.getTag());
						} catch (JSONException e) {
							e.printStackTrace();
							Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT)
									.show();
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
						headers.put("Authorization", thingPlugDevice.getAuthorization());
						headers.put("charset", "UTF-8");

						return headers;
					}
				});
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		mHandlerThread = new HandlerThread("TestService.HandlerThread");
		mHandlerThread.start();

		mServiceHandler = new ServiceHandler(mHandlerThread.getLooper());

		initDevice();
	}

	private void initDevice() {
		MyThingPlugDevices myThingPlugDevices = MyThingPlugDevices.getInstance();

		mThingPlugDevices = new ArrayList<ThingPlugDevice>();
		// mPreCreationTimeArray = new ArrayList<Date>();
		mPreCreationDateMap = new HashMap<String, Date>();

		final ArrayList<MyDevices> registeredMyDevices = myThingPlugDevices.getRegisteredMyDeviceList();

		for (MyDevices myDevice : registeredMyDevices) {
			// mPreCreationTimeArray.add(null);
			// Log.v("mydevice", myDevice.toString());

			mThingPlugDevices.add(new ThingPlugDevice(
					myThingPlugDevices.getServiceName(myDevice),
					myThingPlugDevices.getSclId(myDevice),
					myThingPlugDevices.getDeviceId(myDevice),
					myThingPlugDevices.getAuthId(myDevice),
					myThingPlugDevices.getAuthKey(myDevice))
					.setTag(myDevice.name())
					.registerDevice(true));
		}
	}

	private void initNotification(JsonContentInstanceDetail pJsonContentInstanceDetail, String pMyDeviceName) {
		String pDeviceNameStr = pMyDeviceName.replaceAll("\\d", "");
		if (!preUpdatedDeviceName.equals(pDeviceNameStr)) {
			mNewDataCount++;
		}
		preUpdatedDeviceName = pDeviceNameStr;

		String pTitle = "LoRa ThingPlug";
		String pContentText = "";
		Intent resultIntent = new Intent(getApplicationContext(), MenuActivity.class);

		if (mNewDataCount > 1) {
			pContentText = "New data updated on server";
		} else {
			switch (MyDevices.valueOf(pMyDeviceName)) {
			case MESSAGE:
				if (pJsonContentInstanceDetail.getContent().length() > 5) {
					pContentText = "New message:" + pJsonContentInstanceDetail.getContent().subSequence(0, 5);
				} else {
					pContentText = "New message:" + pJsonContentInstanceDetail.getContent();
				}
				resultIntent = new Intent(getApplicationContext(), MessageViewActivity.class);
				break;
			case DOOR1:
			case DOOR2:
			case DOOR3:
				pContentText = pMyDeviceName + " : " + pJsonContentInstanceDetail.getContent();
				resultIntent = new Intent(getApplicationContext(), DoorViewActivity.class);
				break;
			case MAP:
				pContentText = "Location is updated";
				resultIntent = new Intent(getApplicationContext(), SelGatewayActivity.class);
				break;
			case GATEWAY:
				pContentText = "Gateway location is updated";
				resultIntent = new Intent(getApplicationContext(), SelGatewayActivity.class);
				break;
			}
		}

		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext())
				.setSmallIcon(R.drawable.ic_launcher)
				.setContentTitle(pTitle)
				.setContentText(pContentText);

		TaskStackBuilder stackBuilder = TaskStackBuilder.create(getApplicationContext());
		stackBuilder.addParentStack(MainActivity.class);
		stackBuilder.addNextIntent(resultIntent);

		PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

		mBuilder.setContentIntent(resultPendingIntent);
		mBuilder.setAutoCancel(true);

		NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationManager.notify(0, mBuilder.build());

	}

	private void appAdTest() {
		final String checkAppPkg = "dalcoms.pub.flashlight2";
		if (isAppInstalled(checkAppPkg)) {
			Toast.makeText(getApplicationContext(), checkAppPkg + " is installed", Toast.LENGTH_SHORT).show();
		} else {
			NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext())
					.setSmallIcon(R.drawable.ic_launcher)
					.setContentTitle("wow")
					.setContentText("please download my app");

			TaskStackBuilder stackBuilder = TaskStackBuilder.create(getApplicationContext());
			stackBuilder.addParentStack(MainActivity.class);
			stackBuilder.addNextIntent(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.naver.com")));

			PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0,
					PendingIntent.FLAG_UPDATE_CURRENT);

			mBuilder.setContentIntent(resultPendingIntent);
			mBuilder.setAutoCancel(true);

			NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
			mNotificationManager.notify(100, mBuilder.build());
		}
	}

	private boolean isAppInstalled(String pkgStr) {
		boolean result = false;
		PackageManager pm = getPackageManager();
		try {
			pm.getPackageInfo(pkgStr, PackageManager.GET_ACTIVITIES);
			result = true;
		} catch (NameNotFoundException e) {

		}
		return result;

	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		mServiceHandler.sendEmptyMessageDelayed(0, 10000);
//		appAdTest();

		return START_STICKY;
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onDestroy() {
		mHandlerThread.quit();
	}

}
