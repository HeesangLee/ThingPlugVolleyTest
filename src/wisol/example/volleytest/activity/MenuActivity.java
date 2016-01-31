package wisol.example.volleytest.activity;

import wisol.example.volleytest.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class MenuActivity extends Activity {

	TextView mTvDebug;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_activity_menu);
		this.initUIcomponents();
	}

	private void initUIcomponents() {
		mTvDebug = (TextView) findViewById(R.id.menu_debug_text);
	}

	public void onClickMenuMsg(View v) {
		mTvDebug.setText("msg");
		startActivity(new Intent(this,MessageViewActivity.class));
	}

	public void onClickMenuDoor(View v) {
		mTvDebug.setText("door");
		startActivity(new Intent(this,DoorViewActivity.class));
	}

	public void onClickMenuMap(View v) {
		mTvDebug.setText("map");
	}
	
}
