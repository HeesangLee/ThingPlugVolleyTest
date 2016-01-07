package wisol.example.volleytest.activity;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import wisol.example.volleytest.R;
import wisol.example.volleytest.ThingPlugDevice;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

public class DebugActivity extends Activity {

	TextView mTextView;
	private ThingPlugDevice mThingPlugDevice;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_debug);
		initUIcomponents();
		mThingPlugDevice = new ThingPlugDevice();

		contenInstancesRetrieve(mThingPlugDevice);
	}

	private void initUIcomponents() {
		mTextView = (TextView) findViewById(R.id.debug_text);
	}

	private void contenInstancesRetrieve(ThingPlugDevice pThingPlugDevice) {
		String reqUrl = getUrl(pThingPlugDevice).toString();
		Log.v(getClass().getName(), reqUrl);
		final String authorization = pThingPlugDevice.getAuthorization();
		
		Volley.newRequestQueue(this).add(
				new StringRequest(Request.Method.GET, reqUrl, new Response.Listener<String>() {

					@Override
					public void onResponse(String response) {
						try {
							JSONObject jsonObject = XML.toJSONObject(response);
							mTextView.setText(jsonObject.toString(3));

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

				});
		
	}

	private URL getUrl(ThingPlugDevice pThingPlugDevice) {
		URL result = null;

		try {
			result = new URL(pThingPlugDevice.getProtocol(), pThingPlugDevice.getHost(),
					pThingPlugDevice.getHostPort(), "/ThingPlug/scls/SC10009801/attachedDevices/");

		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		return result;
	}
}
