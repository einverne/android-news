package cn.edu.blcu.newsfeed.search;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.app.ActionBar;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import cn.edu.blcu.newsfeed.R;

public class advanced_search extends Activity {
	private static final String TAG = "Demo";
	private String keyword;
	private int mYear;
	private int mMonth;
	private int mDay;
	String datef;
	String datet;
	private int tag;
	static final int DATE_DIALOG_ID = 0;
	TextView startTextView;
	TextView endTextView;

	String syear;
	CheckBox cb_aboutChina;
	boolean aboutChina;
	int mon;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_advanced_search);

		ActionBar actionBar = this.getActionBar();
		actionBar.setTitle(R.string.title_search_advanced);
		final EditText searchkeywordEdit = (EditText) findViewById(R.id.searchkeyword);
		Button startdateButton = (Button) findViewById(R.id.button_startdate);
		Button enddateButton = (Button) findViewById(R.id.button1_enddate);
		startTextView = (TextView) findViewById(R.id.startdate);
		endTextView = (TextView) findViewById(R.id.enddate);
		cb_aboutChina = (CheckBox) findViewById(R.id.checkBox_aboutChina);

		final Calendar c = Calendar.getInstance();
		mYear = c.get(Calendar.YEAR);
		mMonth = c.get(Calendar.MONTH);
		mDay = c.get(Calendar.DAY_OF_MONTH);
		int curMonth;
		int curYear;
		if (mMonth >= 4) {
			curMonth = mMonth - 2;
		} else {
			curYear = mYear - 1;
			curMonth = mMonth + 10;
		}
		mon = mMonth + 1;
		String date = mYear + "-" + mon + "-" + mDay;
		String fordate = mYear + "-" + curMonth + "-" + mDay;

		startTextView.setText(fordate);
		endTextView.setText(date);

		Bundle bundle = this.getIntent().getExtras();
		keyword = bundle.getString("keyword");
		// use AlertDialog to show search keyword
		searchkeywordEdit.setText(keyword);

		Button advancedButton = (Button) findViewById(R.id.button_advanced_search);
		advancedButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// Keyword
				keyword = searchkeywordEdit.getText().toString();

				if (keyword.length() <= 0) {
					Toast.makeText(advanced_search.this, "请输入关键词",
							Toast.LENGTH_SHORT).show();
					return;
				}

				// Date

				String startTime = startTextView.getText().toString();
				String endTime = endTextView.getText().toString();

				String currentTime = mYear + "-" + mon + "-" + mDay;

				Log.d("wwwcurTime", "" + currentTime);
				DateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
				DateFormat format2 = new SimpleDateFormat("yyyy-MM-dd");
				DateFormat format3 = new SimpleDateFormat("yyyy-MM-dd");
				Date date1 = null;
				Date date2 = null;
				Date date3 = null;

				try {
					date1 = format1.parse(startTime);
					date2 = format2.parse(endTime);
					date3 = format3.parse(currentTime);
				} catch (ParseException e) {
					e.printStackTrace();
				}

				aboutChina = cb_aboutChina.isChecked();
				Log.d(TAG, "aboutChina:" + aboutChina);

				if (date1 == null || date2 == null
						|| date1.compareTo(date2) > 0
						|| date1.compareTo(date3) > 0
						|| date2.compareTo(date3) > 0) {
					Toast.makeText(advanced_search.this, "请选择日期",
							Toast.LENGTH_SHORT).show();
					return;
				} else {
					Intent intent = new Intent();
					intent.setClass(advanced_search.this, search.class);
					Bundle bundle = new Bundle();
					bundle.putString("dateF", datef);
					bundle.putString("dateT", datet);
					bundle.putString("keyword", keyword);
					bundle.putBoolean("aboutChina", aboutChina);
					intent.putExtras(bundle);
					startActivity(intent);
					advanced_search.this.finish();
				}
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
					.append(mYear).append("-").append(mMonth + 1).append("-")
					.append(mDay).append(" "));
			String mon, day;
			if ((mMonth + 1) <= 9) {
				mMonth++;
				mon = "0" + mMonth;
			} else {
				mMonth++;
				mon = mMonth + "";
			}
			if (mDay <= 9) {
				day = "0" + mDay;
			} else {
				day = "" + mDay;
			}
			datef = mYear + mon + day;
		} else if (t == 1) {
			endTextView.setText(new StringBuilder().append(mYear).append("-")
					.append(mMonth + 1).append("-").append(mDay).append(" "));
			String mon, day;
			if ((mMonth + 1) <= 9) {
				mMonth++;
				mon = "0" + mMonth;
			} else {
				mMonth++;
				mon = mMonth + "";
			}
			if (mDay <= 9) {
				day = "0" + mDay;
			} else {
				day = "" + mDay;
			}
			datet = mYear + mon + day;

		}
	}

	private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {
		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			mYear = year;
			mMonth = monthOfYear;
			mDay = dayOfMonth;
			// if (tag == 0) {
			updateDisplay(tag);
			// } else if (tag == 1) {
			// updateDisplay(1);
			// }
		}
	};

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			this.finish();
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

}