package wisol.example.volleytest;

import android.util.Base64;
import android.util.Log;

public class ThingPlugDevice {
	private final String thingProtocol = "http";
	private String mHost = "61.250.21.211";
	private int mHostPort = 10005;

	private String mServiceName;
	private String mSclId;
	private String mDeviceId;
	private String mAuthId;
	private String mAuthKey;

	final private String defServiceName = "ThingPlug";
	final private String defSclId = "SC10009801";
	final private String defDeviceId = "AD10014854";
	final private String defAuthId = "AP10005666";
	final private String defAuthKey = "AK10000176";

	public ThingPlugDevice() {
		setServiceName(defServiceName);
		setSclId(defSclId);
		setDeviceId(defDeviceId);
		setAuthId(defAuthId);
		setAuthKey(defAuthKey);
	}

	public ThingPlugDevice(String pServiceName, String pSclId, String pDeviceId, String pAuthId, String pAuthKey) {
		setServiceName(pServiceName);
		setSclId(pSclId);
		setDeviceId(pDeviceId);
		setAuthId(pAuthId);
		setAuthKey(pAuthKey);

	}

	public String getAuthorization() {
		String str = Base64.encodeToString((this.mAuthId + ":" + this.mAuthKey).getBytes(),Base64.URL_SAFE);
		Log.v(getClass().getName(),str);
		
		return str;
	}

	public String getServiceName() {
		return mServiceName;
	}

	public void setServiceName(String mServiceName) {
		this.mServiceName = mServiceName;
	}

	public String getSclId() {
		return mSclId;
	}

	public void setSclId(String mSclId) {
		this.mSclId = mSclId;
	}

	public String getDeviceId() {
		return mDeviceId;
	}

	public void setDeviceId(String mDeviceId) {
		this.mDeviceId = mDeviceId;
	}

	public String getAuthId() {
		return mAuthId;
	}

	public void setAuthId(String mAuthId) {
		this.mAuthId = mAuthId;
	}

	public String getAuthKey() {
		return mAuthKey;
	}

	public void setAuthKey(String mAuthKey) {
		this.mAuthKey = mAuthKey;
	}

	public String getHost() {
		return mHost;
	}

	public void setHost(String mHost) {
		this.mHost = mHost;
	}

	public int getHostPort() {
		return mHostPort;
	}

	public void setHostPort(int mHostPort) {
		this.mHostPort = mHostPort;
	}
	public String getProtocol(){
		return this.thingProtocol;
	}
}
