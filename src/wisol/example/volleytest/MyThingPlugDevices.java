package wisol.example.volleytest;

import java.util.HashMap;
import java.util.Map;

public class MyThingPlugDevices {
	private static MyThingPlugDevices instance;
	private final String serviceNameKey = "serviceName";
	private final String sclIdKey = "sclId";
	private final String deviceIdKey = "deviceId";
	private final String authIdKey = "authId";
	private final String authKeyKey = "authKey";

	Map<String, String> deviceInfoMsg = new HashMap<String, String>() {
		{
			put(serviceNameKey, "ThingPlug");
			put(sclIdKey, "SC10009801");
			put(deviceIdKey, "AD10014854");
			put(authIdKey, "AP10005666");
			put(authKeyKey, "AK10000176");
		}
	};

	Map<String, String> deviceInfoDoor = new HashMap<String, String>() {// 종속디바이스
																		// 확인해야함.
		{
			put(serviceNameKey, "ThingPlug");
			put(sclIdKey, "SC10010146");
			put(deviceIdKey, "AD10014854");
			put(authIdKey, "AP10005666");
			put(authKeyKey, "AK10000176");
		}
	};

	Map<String, String> deviceInfoMap = new HashMap<String, String>() {
		{
			put(serviceNameKey, "ThingPlug");
			put(sclIdKey, "SC10010147");
			put(deviceIdKey, "AD10014854");
			put(authIdKey, "AP10005666");
			put(authKeyKey, "AK10000176");
		}
	};

	public static synchronized MyThingPlugDevices getInstance() {
		if (instance == null) {
			instance = new MyThingPlugDevices();
		}
		return instance;
	}

	public String getServiceName(MyDevices pDevice) {
		return getDeviceInfo(pDevice).get(this.serviceNameKey);
	}
	
	public String getSclId(MyDevices pDevice){
		return getDeviceInfo(pDevice).get(this.sclIdKey);
	}
	
	public String getDeviceId(MyDevices pDevice){
		return getDeviceInfo(pDevice).get(this.deviceIdKey);
	}

	public String getAuthId(MyDevices pDevice){
		return getDeviceInfo(pDevice).get(this.authIdKey);
	}
	
	public String getAuthKey(MyDevices pDevice){
		return getDeviceInfo(pDevice).get(this.authKeyKey);
	}
	
	public Map<String, String> getDeviceInfo(MyDevices pDevice) {
		Map<String, String> result = new HashMap<String, String>();
		switch (pDevice) {
		case DEFAULT:
			result.putAll(this.deviceInfoMsg);
			break;
		case MESSAGE:
			result.putAll(this.deviceInfoMsg);
			break;
		case DOOR:
			result.putAll(this.deviceInfoDoor);
			break;
		case MAP:
			result.putAll(this.deviceInfoMap);
			break;
		default:
			result.putAll(this.deviceInfoMsg);
			break;
		}
		return result;
	}

	public enum MyDevices {
		DEFAULT, MESSAGE, DOOR, MAP;
	}

}
