package wisol.example.volleytest.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;

import wisol.example.volleytest.JsonContentInstanceDetail;
import wisol.example.volleytest.MyThingPlugDevices;
import wisol.example.volleytest.MyThingPlugDevices.MyDevices;
import wisol.example.volleytest.R;
import wisol.example.volleytest.ThingPlugDevice;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

public class DoorViewActivity extends Activity {
	TextView mTvDoorInfo;
	TextView mTvDoorInfoDate;
	ListView mLvDoorList;
	ArrayList<ThingPlugDevice> mDoorDevices;
	ArrayList<JsonContentInstanceDetail> mDoorContentList;
	private DoorListViewAdapter mDoorListViewAdapter;

	Handler mHandler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_door_view);

		initUiComponents();
		initDoorListArray();

		mHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				getThingPlugDeviceContent(msg.what);
			}
		};

	}

	private synchronized void getThingPlugDeviceContent(int pDeviceNum) {
		final String authorization = mDoorDevices.get(pDeviceNum).getAuthorization();
		final String reqUrl = mDoorDevices.get(pDeviceNum).getUrlContenInstancesDetailed(0, 1).toString();
		final int deviceNum = pDeviceNum;

		Volley.newRequestQueue(this).add(
				new StringRequest(Request.Method.GET, reqUrl, new Response.Listener<String>() {

					@Override
					public void onResponse(String response) {
						try {
							JSONObject jsonObject = XML.toJSONObject(response);
							// updateMessageList(jsonObject);

						} catch (JSONException e) {
							e.printStackTrace();
							Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
						}
					}

				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						Toast.makeText(getApplicationContext(), String.valueOf(deviceNum) + ":Error occured",
								Toast.LENGTH_SHORT).show();
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

	private void initDoorListArray() {
		final int UNREG_DEVICE_NUM = 10;
		final String unRegDeviceName = "Unregistered ";

		mDoorDevices = new ArrayList<ThingPlugDevice>();
		mDoorContentList = new ArrayList<JsonContentInstanceDetail>();

		MyThingPlugDevices myThingPlugDevices = MyThingPlugDevices.getInstance();

		mDoorDevices.add(new ThingPlugDevice(
				myThingPlugDevices.getServiceName(MyDevices.DOOR1),
				myThingPlugDevices.getSclId(MyDevices.DOOR1),
				myThingPlugDevices.getDeviceId(MyDevices.DOOR1),
				myThingPlugDevices.getAuthId(MyDevices.DOOR1),
				myThingPlugDevices.getAuthKey(MyDevices.DOOR1)).setTag("Hana Bank").registerDevice(true));

		mDoorDevices.add(new ThingPlugDevice(
				myThingPlugDevices.getServiceName(MyDevices.DOOR2),
				myThingPlugDevices.getSclId(MyDevices.DOOR2),
				myThingPlugDevices.getDeviceId(MyDevices.DOOR2),
				myThingPlugDevices.getAuthId(MyDevices.DOOR2),
				myThingPlugDevices.getAuthKey(MyDevices.DOOR2)).setTag("Home Plus").registerDevice(true));

		mDoorDevices.add(new ThingPlugDevice(
				myThingPlugDevices.getServiceName(MyDevices.DOOR3),
				myThingPlugDevices.getSclId(MyDevices.DOOR3),
				myThingPlugDevices.getDeviceId(MyDevices.DOOR3),
				myThingPlugDevices.getAuthId(MyDevices.DOOR3),
				myThingPlugDevices.getAuthKey(MyDevices.DOOR3)).setTag("WISOL").registerDevice(true));

		for (int i = 0; i < UNREG_DEVICE_NUM; i++) {
			mDoorDevices.add(new ThingPlugDevice()
					.setTag(unRegDeviceName + String.valueOf(i))
					.registerDevice(false));
		}


		for (ThingPlugDevice pDevice : mDoorDevices) {
			mDoorContentList.add(new JsonContentInstanceDetail()
					.setName(pDevice.getTag())
					.register(false));
//					.register(pDevice.isRegistered()));
		}
		
		updateDataList();
	}

	private void initUiComponents() {
		mTvDoorInfo = (TextView) findViewById(R.id.door_info);
		mTvDoorInfoDate = (TextView) findViewById(R.id.door_info_date);
		mLvDoorList = (ListView) findViewById(R.id.door_list);
	}

	private void updateDataList() {
		if (mDoorListViewAdapter == null) {
			mDoorListViewAdapter = new DoorListViewAdapter(this, R.layout.door_listview_item,
					this.mDoorContentList);
			mLvDoorList.setAdapter(mDoorListViewAdapter);
		} else {
			mDoorListViewAdapter.notifyDataSetChanged();
		}
	}

	private class DoorListViewAdapter extends ArrayAdapter<JsonContentInstanceDetail> {

		public DoorListViewAdapter(Context context, int resource, ArrayList<JsonContentInstanceDetail> objects) {
			super(context, resource, objects);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View v = convertView;
			JsonContentInstanceDetail item = getItem(position);
			ViewHolder viewHolder;

			if (v == null) {
				viewHolder = new ViewHolder();
				LayoutInflater vi = LayoutInflater.from(getContext());
				v = vi.inflate(R.layout.door_listview_item, parent, false);

				viewHolder.img = (ImageView) v.findViewById(R.id.doorItemStateImg);
				viewHolder.title = (TextView) v.findViewById(R.id.doorItemTitle);
				viewHolder.msg = (TextView) v.findViewById(R.id.doorItemStateText);
				viewHolder.date = (TextView) v.findViewById(R.id.doorItemDateText);

				v.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) v.getTag();

			}

			if (item.isRegistered()) {
				if (item != null) {
					// viewHolder.img
					switch (isDoorOpenClose(item.getContent())) {
					case OPEN:
						viewHolder.img.setImageResource(R.drawable.door_open);
						break;
					case CLOSE:
						viewHolder.img.setImageResource(R.drawable.door_close);
						break;
					default:
						viewHolder.img.setImageResource(R.drawable.door_nodata);
						break;

					}
					viewHolder.title.setText(item.getName());
					viewHolder.msg.setText(item.getContent());
					viewHolder.date.setText(item.getCreationTime().toString());
				}
			} else {
				viewHolder.img.setImageResource(R.drawable.door_unregistered);
				viewHolder.title.setText(item.getName());
				viewHolder.msg.setText("-----");
				viewHolder.date.setText("-----");
			}

			return v;
		}
	}

	public DoorState isDoorOpenClose(String pInput) {// open:false, close:true;
		DoorState result = DoorState.UNKOWN;
		final String DOOR_OPEN = "open";
		final String DOOR_CLOSE = "close";

		final String pInputLowerCase = pInput.toLowerCase();

		final String ptnStrinOpen = "(.*)" + DOOR_OPEN + "(.*)";
		final String ptnStrinClose = "(.*)" + DOOR_CLOSE + "(.*)";

		Pattern ptnOpen = Pattern.compile(ptnStrinOpen);
		Pattern ptnClose = Pattern.compile(ptnStrinClose);

		Matcher mOpen = ptnOpen.matcher(pInputLowerCase);
		Matcher mClose = ptnClose.matcher(pInputLowerCase);

		if (mOpen.find()) {
			result = DoorState.OPEN;
		} else if (mClose.find()) {
			result = DoorState.CLOSE;
		} else {
			result = DoorState.UNKOWN;
		}

		return result;
	}

	private enum DoorState {
		OPEN, CLOSE, UNKOWN
	}

	public static class ViewHolder {
		ImageView img;
		TextView title;
		TextView msg;
		TextView date;
	}
}