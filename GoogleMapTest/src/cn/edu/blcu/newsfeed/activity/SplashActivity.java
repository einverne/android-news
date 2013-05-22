package cn.edu.blcu.newsfeed.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import cn.edu.blcu.newsfeed.R;
import cn.edu.blcu.newsfeed.tabactivity.MainActivity;

import com.and.netease.utils.CheckNetwork;
import com.and.netease.utils.ConnectWeb;
import com.and.netease.utils.DBAdapter;

@SuppressLint("HandlerLeak")
public class SplashActivity extends Activity {

	protected DBAdapter dbadapter;
	private ViewPager viewPager;
	private List<ImageView> imageViews;
	private int[] imageResId;
	private List<View> dots;
	private int currentItem = 0;

	// An ExecutorService that can schedule commands to run after a given delay,
	// or to execute periodically.
	private ScheduledExecutorService scheduledExecutorService;

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			viewPager.setCurrentItem(currentItem);
		};
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash2);

		/*
		 * New Handler to start the Menu-Activity and close this Splash-Screen
		 * after some seconds.
		 */
		dbadapter = new DBAdapter(this);
		/*
		 * new Handler().postDelayed(new Runnable() {
		 * 
		 * @Override public void run() { CheckNetwork cn = new
		 * CheckNetwork(SplashActivity.this); if (cn.check()) {
		 * 
		 * } Create an Intent that will start the Menu-Activity.
		 * 
		 * } }, SPLASH_DISPLAY_LENGHT);
		 */

		new mytask().execute();

		imageResId = new int[] { R.drawable.splash1, R.drawable.splash,
				R.drawable.splash2 };
		imageViews = new ArrayList<ImageView>();

		for (int i = 0; i < imageResId.length; i++) {
			ImageView imageView = new ImageView(this);
			imageView.setImageResource(imageResId[i]);
			imageView.setScaleType(ScaleType.CENTER_CROP);
			imageViews.add(imageView);
		}

		dots = new ArrayList<View>();
		dots.add(findViewById(R.id.v_dot0));
		dots.add(findViewById(R.id.v_dot1));
		dots.add(findViewById(R.id.v_dot2));

		viewPager = (ViewPager) findViewById(R.id.vp);
		viewPager.setAdapter(new MyAdapter());
		viewPager.setOnPageChangeListener(new MyPageChangeListener());
	}

	public class mytask extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			CheckNetwork cn = new CheckNetwork(SplashActivity.this);
			if (cn.check()) {
				ConnectWeb.getzuijinxinwen(dbadapter);
				ConnectWeb.getpeoples(dbadapter);
				ConnectWeb.getplaces(dbadapter);
				ConnectWeb.getdivisions(dbadapter);
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			Intent mainIntent = new Intent(SplashActivity.this,
					MainActivity.class);
			SplashActivity.this.startActivity(mainIntent);
			SplashActivity.this.finish();
			super.onPostExecute(result);
		}

	}

	@Override
	protected void onStart() {
		scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
		scheduledExecutorService.scheduleAtFixedRate(new ScrollTask(), 5, 10,
				TimeUnit.SECONDS);
		super.onStart();
	}

	@Override
	protected void onStop() {
		scheduledExecutorService.shutdown();
		super.onStop();
	}

	private class ScrollTask implements Runnable {

		@Override
		public void run() {
			synchronized (viewPager) {

				currentItem++;
				Log.d("currentItem: ", "" + currentItem);
				handler.obtainMessage().sendToTarget();
			}
		}

	}

	private class MyPageChangeListener implements OnPageChangeListener {
		private int oldPosition = 0;

		/**
		 * This method will be invoked when a new page becomes selected.
		 * position: Position index of the new selected page.
		 */
		@Override
		public void onPageSelected(int position) {
			dots.get(oldPosition).setBackgroundResource(R.drawable.dot_normal);
			dots.get(position).setBackgroundResource(R.drawable.dot_focused);
			oldPosition = position;
		}

		@Override
		public void onPageScrollStateChanged(int arg0) {

		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {

		}
	}

	private class MyAdapter extends PagerAdapter {

		@Override
		public int getCount() {
			return imageResId.length;

		}

		@Override
		public Object instantiateItem(View arg0, int arg1) {
			((ViewPager) arg0).addView(imageViews.get(arg1));
			return imageViews.get(arg1);
		}

		@Override
		public void destroyItem(View arg0, int arg1, Object arg2) {
			((ViewPager) arg0).removeView((View) arg2);
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public void restoreState(Parcelable arg0, ClassLoader arg1) {

		}

		@Override
		public Parcelable saveState() {
			return null;
		}

		@Override
		public void startUpdate(View arg0) {

		}

		@Override
		public void finishUpdate(View arg0) {

		}
	}

}
