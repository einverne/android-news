package com.and.netease;

import java.util.Calendar;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class advanced_search extends Activity {
	private static final String TAG = "Demo";
	private String keyword;
	private int mYear;
	private int mMonth;
	private int mDay;
	private int tag;
	private String[] pos = new String[] { "所有位置", "标题", "摘要", "正文" };
	private String[] conjStrings = new String[] { "and", "or", "not", };
	static final int DATE_DIALOG_ID = 0;
	TextView startTextView;
	TextView endTextView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_advanced_search);

		Log.d(TAG, "advanced_search_start");

		final EditText searchkeywordEdit = (EditText) findViewById(R.id.searchkeyword);
		final EditText keywordEdit = (EditText) findViewById(R.id.keyword);
		final Spinner pos = (Spinner) findViewById(R.id.spinner_position);
		final Spinner conjSpinner = (Spinner) findViewById(R.id.spinner_conj);

		Button startdateButton = (Button) findViewById(R.id.button_startdate);
		Button enddateButton = (Button) findViewById(R.id.button1_enddate);
		startTextView = (TextView) findViewById(R.id.startdate);
		endTextView = (TextView) findViewById(R.id.enddate);

		Bundle bundle = this.getIntent().getExtras();
		keyword = bundle.getString("keyword");
		// use AlertDialog to show search keyword
		searchkeywordEdit.setText(keyword);

		ArrayAdapter<String> posAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, this.pos);
		// 设置弹出的下拉了表的样式
		posAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		pos.setAdapter(posAdapter);

		ArrayAdapter<String> conjAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, conjStrings);
		conjAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		conjSpinner.setAdapter(conjAdapter);

		Button advancedButton = (Button) findViewById(R.id.button_advanced_search);
		advancedButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				//Keyword 
				keyword = keywordEdit.getText().toString();
				
				if (keyword.length()<=0) {
					Toast.makeText(advanced_search.this, "请输入关键词", Toast.LENGTH_SHORT).show();
					return;
				}
				// Date 
				
				String startTime = startTextView.getText().toString();
				String endTime = endTextView.getText().toString();
				
				keyword = keyword + startTime + " " + endTime; //形成检索的字符串
				
				Intent intent = new Intent();
				intent.setClass(advanced_search.this, search.class);
				Bundle bundle = new Bundle();
				bundle.putString("keyword", keyword);
				intent.putExtras(bundle);
				startActivity(intent);
				advanced_search.this.finish();
			}
		});

		final Calendar c = Calendar.getInstance();
		mYear = c.get(Calendar.YEAR);
		mMonth = c.get(Calendar.MONTH);
		mDay = c.get(Calendar.DAY_OF_MONTH);
		// 设置当前时间

		Button addSearchButton = (Button) findViewById(R.id.addsearch);
		addSearchButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				CharSequence keyCharSequence = searchkeywordEdit.getText();
				if (keyCharSequence.length() <= 0) {
					Toast.makeText(advanced_search.this, "请输入关键词",
							Toast.LENGTH_LONG).show();
					return;
				}

				// 将关键字添加到搜索语句
				CharSequence posCharSequence = pos.getSelectedItem().toString();

				if (posCharSequence.toString() == "所有位置") {
					posCharSequence = "标题:\"" + keyCharSequence + "\" OR "
							+ "摘要:\"" + keyCharSequence + "\" OR " + "正文:\"";

				} else {
					posCharSequence = posCharSequence + ":\"";
				}

				keywordEdit.append(posCharSequence);
				keywordEdit.append(keyCharSequence);
				keywordEdit.append("\"");
				CharSequence conjCharSequence = conjSpinner.getSelectedItem()
						.toString();
				conjCharSequence = " " + conjCharSequence;
				keywordEdit.append(conjCharSequence);

				searchkeywordEdit.setText("");
				// 清空关键字

			}
		});

		startdateButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				tag = 0;
				showDialog(DATE_DIALOG_ID);
			}
		});

		enddateButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				tag = 1;
				showDialog(DATE_DIALOG_ID);
			}
		});

	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DATE_DIALOG_ID:
			return new DatePickerDialog(this, mDateSetListener, mYear, mMonth,
					mDay);
		}
		return null;
	}

	// updates the date we display in the TextView
	private void updateDisplay(int t) {
		if (t == 0) {
			startTextView.setText(new StringBuilder()
					// Month is 0 based so add 1
					.append(mMonth + 1).append("-").append(mDay).append("-")
					.append(mYear).append(" "));
		} else if (t == 1) {
			endTextView.setText(new StringBuilder().append(mMonth + 1)
					.append("-").append(mDay).append("-").append(mYear)
					.append(" "));

		}
	}

	private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			mYear = year;
			mMonth = monthOfYear;
			mDay = dayOfMonth;
			if (tag == 0) {
				updateDisplay(tag);
			} else if (tag == 1) {
				updateDisplay(1);
			}
		}
	};

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Intent intent = new Intent();
			intent.setClass(advanced_search.this, MainActivity.class);
			startActivityForResult(intent, 1);
			advanced_search.this.finish();
		}
		return true;
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		Log.d(TAG, "advance Destroy");
		super.onDestroy();
	}

	@Override
	protected void onStart() {
		Log.d(TAG, "advance Start");
		super.onStart();
	}

	@Override
	protected void onStop() {
		Log.d(TAG, "advance Stop");
		super.onStop();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.d(TAG, "request1111");
		if (requestCode == 1) {
			Log.d(TAG, "request");
		}
	}
}