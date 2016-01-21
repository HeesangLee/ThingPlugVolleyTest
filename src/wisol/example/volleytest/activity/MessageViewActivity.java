package wisol.example.volleytest.activity;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;

import wisol.example.volleytest.JsonContentInstanceDetail;
import wisol.example.volleytest.JsonResponseContentInstancesDetailed;
import wisol.example.volleytest.MyThingPlugDevices;
import wisol.example.volleytest.MyThingPlugDevices.MyDevices;
import wisol.example.volleytest.R;
import wisol.example.volleytest.ThingPlugDevice;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

public class MessageViewActivity extends Activity {

	TextView mTvLastMsg;
	TextView mTvLastDate;
	ListView mLvMsgList;

	private ThingPlugDevice mThingPlugDevice;
	MyThingPlugDevices myThingPlugDevices;

	Handler mMainHandler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_message_view);
		initUiComponents();

		myThingPlugDevices = MyThingPlugDevices.getInstance();

		mThingPlugDevice = new ThingPlugDevice(myThingPlugDevices.getServiceName(MyDevices.MESSAGE),
				myThingPlugDevices.getSclId(MyDevices.MESSAGE),
				myThingPlugDevices.getDeviceId(MyDevices.MESSAGE),
				myThingPlugDevices.getAuthId(MyDevices.MESSAGE),
				myThingPlugDevices.getAuthKey(MyDevices.MESSAGE));

		initTimerHandlerForCheckData(1);
	}

	@SuppressLint("HandlerLeak") private void initTimerHandlerForCheckData(long postDelayed) {
		mMainHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				thingPlugRequest(mThingPlugDevice, mThingPlugDevice.getUrlContenInstancesDetailed(0, 10).toString(),
						Request.Method.GET);
			}
		};
		mMainHandler.sendEmptyMessageDelayed(1, postDelayed);
	}

	private void initUiComponents() {
		mTvLastMsg = (TextView) findViewById(R.id.msg_last_msg);
		mTvLastDate = (TextView) findViewById(R.id.msg_last_date);
		mLvMsgList = (ListView) findViewById(R.id.msg_list);
	}

	private void testGsonObject(JSONObject pJsonObject) {
		Type type = new TypeToken<JsonResponseContentInstancesDetailed>() {
		}.getType();

		JsonResponseContentInstancesDetailed response = new GsonBuilder().create().fromJson(
				pJsonObject.toString(), type);

		mTvLastMsg.clearComposingText();
		for (JsonContentInstanceDetail detail : response.getContentInstanceDetails()) {
			mTvLastMsg.append(
					"\n" +
							detail.getId() + "," +
							detail.getCreationTime().toString() + "," +
							detail.getLastModifiedTime().toString() + "," +
							detail.getContent() + "," +
							detail.getCountIndex() + "," +
							detail.getTatalCount() + "," +
							detail.getCurrentCount() + "\n");
		}
	}

	private void updateLastMessage(JSONObject pJsonObject) {
		Type type = new TypeToken<JsonResponseContentInstancesDetailed>() {
		}.getType();
		JsonResponseContentInstancesDetailed response = new GsonBuilder().create().fromJson(
				pJsonObject.toString(), type);
		if (response.getContentInstanceDetails().size() == 0) {
			mTvLastMsg.setText("No data today");
			mTvLastDate.setText("-");
		} else {
			mTvLastMsg.setText(response.getContentInstanceDetails().get(0).getContent());
			mTvLastDate.setText(response.getContentInstanceDetails().get(0).getCreationTime().toString()
					+ "[" +
					+response.getContentInstanceDetails().size() +
					"]");
		}
		
		mMainHandler.sendEmptyMessageDelayed(0, 500);
		
	}

	synchronized private void thingPlugRequest(ThingPlugDevice pThingPlugDevice, String reqUrl,
			int pRequestMethod) {
		Log.v(getClass().getName(), reqUrl);
		final String authorization = pThingPlugDevice.getAuthorization();
		final int reqMethod = pRequestMethod;

		Volley.newRequestQueue(this).add(new StringRequest(reqMethod, reqUrl, new Response.Listener<String>() {

			@Override
			public void onResponse(String response) {
				try {
					JSONObject jsonObject = XML.toJSONObject(response);
					updateLastMessage(jsonObject);
					
				} catch (JSONException e) {
					e.printStackTrace();
					Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
				}
			}

		}, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				Toast.makeText(getApplicationContext(), "Error occured", Toast.LENGTH_SHORT).show();
			}
		}) {

			@Override
			public Map<String, String> getHeaders() throws AuthFailureError {
				Map<String, String> headers = new HashMap<String, String>();
				headers.put("Content-Type", "application/xml");
				headers.put("Authorization", authorization);
				headers.put("charset", "UTF-8");

				return headers;
			}

			@Override
			public byte[] getBody() throws AuthFailureError {
				// TODO Auto-generated method stub
				return super.getBody();
			}

		});

	}
}
