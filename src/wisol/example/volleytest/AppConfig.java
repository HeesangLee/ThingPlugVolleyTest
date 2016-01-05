package wisol.example.volleytest;

public class AppConfig {
	private static AppConfig mInstance;

	private final String mHost = "thingplug.sktiot.com";
	private final int mHostPort = 9000;
	private final String PUT_LOGIN_PATH = "?division=user&function=login";
	private String mPath = "ThingPlug";

	private final String DEF_USERID = "wisolHsLee";
	private final String DEF_PASSWORD = "wisol1234!";

	private String userId = DEF_USERID;
	private String userPassword = DEF_PASSWORD;

	private JsonDataThingPlugLogin mJsonDataThingPlugLogin;

	public static synchronized AppConfig getInstance() {
		if (mInstance == null) {
			mInstance = new AppConfig();
		}
		return mInstance;
	}

	public JsonDataThingPlugLogin getLoginResponse() {
		return mJsonDataThingPlugLogin;
	}

	public void setLoginResponse(JsonDataThingPlugLogin pDataThingPlugLogin) {
		this.mJsonDataThingPlugLogin = pDataThingPlugLogin;
	}

	public String getHost() {
		return this.mHost;
	}

	public int getHostPort() {
		return this.mHostPort;
	}


	public String getPath() {
		return mPath;
	}

	public String getPutLoginPath() {
		return this.PUT_LOGIN_PATH;
	}

	public String getDefaultUserId() {
		return this.DEF_USERID;
	}

	public String getDefaultPassword() {
		return this.DEF_PASSWORD;
	}

	public void setUserId(String pUserId) {
		this.userId = pUserId;
	}

	public void setUserPassword(String pUserPassword) {
		this.userPassword = pUserPassword;
	}

}
