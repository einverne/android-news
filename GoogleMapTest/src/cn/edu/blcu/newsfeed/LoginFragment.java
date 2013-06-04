package cn.edu.blcu.newsfeed;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import cn.edu.blcu.newsfeed.dingzhi.dingzhi;
import cn.edu.blcu.newsfeed.utils.ConnectWeb;

public class LoginFragment extends Fragment {
	private static final String TAG = "Demo";
	private EditText username;
	private EditText psw;
	private SharedPreferences sharedPreferences;
	private SharedPreferences.Editor editor;
	Boolean boo = false;
	String str_name = null;
	private ProgressDialog progressDialog;
	protected static final int MESSAGE_OK = 0;
	String str_psw = null;

	ArrayList<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();

	public LoginFragment() {
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		Log.d(TAG, "Login start");
		username = (EditText) getActivity()
				.findViewById(R.id.editText_username);
		psw = (EditText) getActivity().findViewById(R.id.editText_psw);

		this.sharedPreferences = getActivity().getSharedPreferences("userinfo",
				Context.MODE_PRIVATE);
		editor = sharedPreferences.edit();

		String store_name = sharedPreferences.getString("name", "");
		String store_psw = sharedPreferences.getString("psw", "");
		if (store_name != "") {
			username.setText(store_name);
			psw.setText(store_psw);
		}

		// Intent intent = new Intent();
		// intent.setClass(TabLoginActivity.this, dingzhi.class);
		// Bundle data = new Bundle();
		// data.putString("name",store_name);
		// intent.putExtras(data);
		// String str = (String)intent.getStringExtra("name");
		// startActivity(intent);
		// }

		Button loginButton = (Button) getActivity().findViewById(
				R.id.button_denglu);
		loginButton.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				str_name = username.getText().toString();
				str_psw = psw.getText().toString();
				if (str_name != "" && str_psw != "") {

					progressDialog = ProgressDialog.show(getActivity(), "等待",
							"正在验证");
					new Thread() {
						@Override
						public void run() {
							try {
								boo = ConnectWeb.getlogin(str_name, str_psw);
							} catch (Exception e) {

							}
							Message msg_listData = new Message();
							msg_listData.what = MESSAGE_OK;
							handler.sendMessage(msg_listData);
						}
					}.start();

				} else {
					Toast.makeText(getActivity(), "请输入用户名密码",
							Toast.LENGTH_SHORT).show();
				}
			}
		});

		Button quxiaoButton = (Button) getActivity().findViewById(
				R.id.button_quxiao);
		quxiaoButton.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				setNull();
			}
		});
		super.onActivityCreated(savedInstanceState);
	}

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message message) {
			switch (message.what) {
			case MESSAGE_OK:
				if (boo) {
					editor.putString("name", str_name);
					editor.putString("psw", str_psw);
					editor.commit();
					Intent intent = new Intent(getActivity(), dingzhi.class);
					Bundle data = new Bundle();
					data.putString("name", str_name);
					intent.putExtras(data);
					startActivity(intent);
				} else {
					Toast.makeText(getActivity(), "用户名密码错误", Toast.LENGTH_SHORT)
							.show();
					setNull();
				}
				progressDialog.dismiss();
				break;
			}
		}
	};

	public void setNull() {
		username.setText("");
		psw.setText("");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.layout_login, null);
	}

}
