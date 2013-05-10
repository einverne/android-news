package com.and.netease;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.RadioGroup;

import com.and.netease.utils.ConnectWeb;

public class Dingzhi_detail extends Activity {
	String username;
	String query;
	String description;
	String aboutChina;

	Bundle bundle;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.dingzhi_detail);

		bundle = this.getIntent().getExtras();
		String keyword = (String) bundle.getString("keyword");

		String date_start = (String) bundle.getString("date_start");
		String date_end = (String) bundle.getString("date_end");

		query = "title:\"" + keyword + "\" OR description:\"" + keyword
				+ "\" OR text:\"" + keyword + "\" AND date:[" + date_start
				+ " TO " + date_end + "]";
		EditText des = (EditText) findViewById(R.id.editText_description);
		description = des.getText().toString();

		aboutChina = "T";
		RadioGroup ra = (RadioGroup) findViewById(R.id.radioGroup1);
		int ra_click = (int) ra.getCheckedRadioButtonId();
		if (ra_click == 1) {
			aboutChina = "F";
		}

		Log.d("dingzhi", aboutChina);
		Log.d("dingzhi", query);
		// description = "acv";
		Log.d("dingzhi", description);

		findViewById(R.id.button_sure).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub

						ConnectWeb.submitJob(username, query, aboutChina,
								description);
						Intent intent = new Intent();
						intent.setClass(Dingzhi_detail.this, dingzhi.class);
						startActivity(intent);
						finish();

					}
				});

	}

}