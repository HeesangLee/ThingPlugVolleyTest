package wisol.example.volleytest.activity;

import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;

import wisol.example.volleytest.AppConfig;
import wisol.example.volleytest.JsonDataThingPlugLogin;
import wisol.example.volleytest.R;
import wisol.example.volleytest.R.id;
import wisol.example.volleytest.R.layout;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.LinearLayout;
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

public class MainActivity extends Activity {
	AppConfig appConfig;
	LinearLayout mLayoutLoginProgress;
	LinearLayout mLayoutLoginForm;
	TextView mTextView;
	AutoCompleteTextView mTextViewUserId;
	EditText mTextViewUserPassword;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		appConfig = AppConfig.getInstance();

		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_activity_main);

		initUIcomponents();

	}

	private void initUIcomponents() {
		mLayoutLoginProgress = (LinearLayout) findViewById(R.id.login_progress);
		mLayoutLoginForm = (LinearLayout) findViewById(R.id.login_form);
		mTextView = (TextView) findViewById(R.id.main_text);
		mTextViewUserId = (AutoCompleteTextView) findViewById(R.id.userid);
		mTextViewUserPassword = (EditText) findViewById(R.id.password);

		mTextViewUserId.setText(appConfig.getDefaultUserId());
		mTextViewUserPassword.setText(appConfig.getDefaultPassword());
	}

	public void onClickLoginButton(View v) {
		mLayoutLoginProgress.setVisibility(View.VISIBLE);
		mLayoutLoginForm.setVisibility(View.GONE);
		login();

	}

	private boolean makeGsonObject(JSONObject pJsonObject) {
		boolean result = false;

		Type type = new TypeToken<JsonDataThingPlugLogin>() {
		}.getType();

		JsonDataThingPlugLogin loginResponse = new GsonBuilder().create().fromJson(pJsonObject.toString(), type);

		if (loginResponse.getUKey().length() > 1) {
			appConfig.setLoginResponse(loginResponse);
			result = true;
		}

		return result;
	}

	private void retryLogin() {
		mLayoutLoginProgress.setVisibility(View.GONE);
		mLayoutLoginForm.setVisibility(View.VISIBLE);
	}

	private boolean login() {
		boolean result = false;
		String reqUrl = getUrl(appConfig.getPath() + appConfig.getPutLoginPath()).toString();
		final String pUserId = mTextViewUserId.getText().toString();
		final String pUserPassword = mTextViewUserPassword.getText().toString();

		Volley.newRequestQueue(this).add(
				new StringRequest(Request.Method.PUT, reqUrl, new Response.Listener<String>() {

					@Override
					public void onResponse(String response) {
						try {
							JSONObject jsonObject = XML.toJSONObject(response);
							mTextView.setText(jsonObject.toString(3));
							if (makeGsonObject(jsonObject) == true) {
								// -->next activity
							} else {
								retryLogin();
								Toast.makeText(getApplicationContext(), "uKey is wrong", Toast.LENGTH_SHORT)
										.show();
							}

						} catch (JSONException e) {
							e.printStackTrace();
							Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
							retryLogin();
						}
					}

				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						Toast.makeText(getApplicationContext(), "Error occured", Toast.LENGTH_SHORT).show();
						retryLogin();
					}
				}) {

					@Override
					public Map<String, String> getHeaders() throws AuthFailureError {
						Map<String, String> headers = new HashMap<String, String>();
						headers.put("Content-Type", "application/xml");
						headers.put("user_id", pUserId);
						headers.put("password", pUserPassword);
						headers.put("locale", "ko");

						return headers;
					}

				});

		return result;
	}

	private URL getUrl(String pPath) {
		URL hostURL = null;

		try {
			hostURL = new URL("http", appConfig.getHost(), appConfig.getHostPort(), pPath);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		return hostURL;
	}

}
