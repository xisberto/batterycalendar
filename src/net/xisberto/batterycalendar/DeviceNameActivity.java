package net.xisberto.batterycalendar;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

public class DeviceNameActivity extends Activity implements OnClickListener {
	public static final String EXTRA_DEVICE_NAME = "device_name";
	private EditText edit;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_device_name);
		
		edit = (EditText) findViewById(R.id.edit_devicename);
		edit.setText(android.os.Build.MODEL);
		
		findViewById(R.id.btn_cancel).setOnClickListener(this);
		findViewById(R.id.btn_ok).setOnClickListener(this);
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.btn_ok:
			Intent data = new Intent();
			data.putExtra(EXTRA_DEVICE_NAME, edit.getText().toString());
			setResult(RESULT_OK, data);
		case R.id.btn_cancel:
			finish();
		default:
			break;
		}
	}

}
