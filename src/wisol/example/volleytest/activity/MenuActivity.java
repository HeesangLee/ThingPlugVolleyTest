package wisol.example.volleytest.activity;

import wisol.example.volleytest.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class MenuActivity extends Activity {
	final String EXTRA_THIS_DEVICE = "THIS_DEVICE";
	TextView mTvDebug;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_activity_menu);
	}

	public void onClickMenuMsg(View v) {
		Intent intent = new Intent(this, MessageViewActivity.class);
		intent.putExtra(EXTRA_THIS_DEVICE, "MAP");
		startActivity(intent);
		
//		startActivity(new Intent(this,MessageViewActivity.class));
	}

	public void onClickMenuDoor(View v) {
		startActivity(new Intent(this,DoorViewActivity.class));
	}

	public void onClickMenuMap(View v) {
		startActivity(new Intent(this,SelGatewayActivity.class));
	}
	
}
