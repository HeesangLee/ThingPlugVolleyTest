package wisol.example.volleytest;

import java.io.UnsupportedEncodingException;
import java.util.Date;

import android.util.Base64;

import com.google.gson.annotations.SerializedName;

public class JsonContentInstanceDetail {
	@SerializedName("id")
	private String id;
	@SerializedName("creationTime")
	private String creationTime;
	@SerializedName("lastModifiedTime")
	private String lastModifiedTime;
	@SerializedName("content")
	private String content;
	@SerializedName("countIndex")
	private String countIndex;
	@SerializedName("totalCount")
	private String totalCount;
	@SerializedName("currentCount")
	private String currentCount;

	public String getId() {
		return this.id;
	}

	public String getCreationTimeString() {
		return this.creationTime;
	}

	public Date getCreationTime() {
		Date result = null;
		if (this.getCreationTimeString().split("T").length > 1) {
			result = stringToDate(this.getCreationTimeString());
		}
		return result;
	}

	public String getLastModifiedTimeString() {
		return this.lastModifiedTime;
	}

	public Date getLastModifiedTime() {
		Date result = null;
		if (this.getLastModifiedTimeString().split("T").length > 1) {
			result = stringToDate(this.getLastModifiedTimeString());
		}
		return result;
	}

	public String getBase64Content() {
		return this.content;
	}

	public String getContent() {
		String result = "";
		result = new String(Base64.decode(this.content, android.util.Base64.DEFAULT));
		return result;
	}

	public int getCountIndex() {
		return Integer.valueOf(this.countIndex);
	}

	public int getTatalCount() {
		return Integer.valueOf(this.totalCount);
	}

	public int getCurrentCount() {
		return Integer.valueOf(this.currentCount);
	}

	private Date stringToDate(String strDate) {
		String[] ymdt = strDate.split("T");
		String[] ymd = ymdt[0].split("-");
		String[] time = ymdt[1].split(":");
		Date result = new Date(
				Integer.valueOf(ymd[0]).intValue(),
				Integer.valueOf(ymd[1]).intValue(),
				Integer.valueOf(ymd[2]).intValue(),
				Integer.valueOf(time[0]).intValue(),
				Integer.valueOf(time[1]).intValue(),
				Integer.valueOf(time[2]).intValue());

		return result;
	}
}
