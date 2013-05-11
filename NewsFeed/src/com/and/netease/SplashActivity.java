package com.and.netease;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ImageView.ScaleType;

import com.and.netease.utils.CheckNetwork;
import com.and.netease.utils.ConnectWeb;
import com.and.netease.utils.DBAdapter;


public class SplashActivity extends Activity {

	private final int SPLASH_DISPLAY_LENGHT = 4000;
	protected DBAdapter dbadapter;
		private ViewPager viewPager; // android-support-v4�еĻ������
		private List<ImageView> imageViews; // ������ͼƬ����
		private int[] imageResId; // ͼƬID
		private List<View> dots; // ͼƬ�������ĵ���Щ��
		private TextView tv_title;
		private int currentItem = 0; // ��ǰͼƬ�������

		// An ExecutorService that can schedule commands to run after a given delay,
		// or to execute periodically.
		private ScheduledExecutorService scheduledExecutorService;

		// �л���ǰ��ʾ��ͼƬ
		private Handler handler = new Handler() {
			public void handleMessage(android.os.Message msg) {
				viewPager.setCurrentItem(currentItem);// �л���ǰ��ʾ��ͼƬ
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
/*		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				CheckNetwork cn = new CheckNetwork(SplashActivity.this);
				if (cn.check()) {
					
				}
				 Create an Intent that will start the Menu-Activity. 
			
			}
		}, SPLASH_DISPLAY_LENGHT);*/

		new mytask().execute();
	
		imageResId = new int[] { R.drawable.splash1, R.drawable.splash,R.drawable.splash2};		
		imageViews = new ArrayList<ImageView>();

		// ��ʼ��ͼƬ��Դ
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
		viewPager.setAdapter(new MyAdapter());// �������ViewPagerҳ���������
		// ����һ������������ViewPager�е�ҳ��ı�ʱ����
		viewPager.setOnPageChangeListener(new MyPageChangeListener());
	}

	public class mytask extends AsyncTask<Void, Void, Void>{

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
			// ��Activity��ʾ������ÿ�������л�һ��ͼƬ��ʾ
			scheduledExecutorService.scheduleAtFixedRate(new ScrollTask(), 5, 10, TimeUnit.SECONDS);
			super.onStart();
		}

		@Override
		protected void onStop() {
			// ��Activity���ɼ��ʱ��ֹͣ�л�
			scheduledExecutorService.shutdown();
			super.onStop();
		}

		/**
		 * �����л�����
		 * 
		 * @author Administrator
		 * 
		 */
		private class ScrollTask implements Runnable {

			public void run() {
				synchronized (viewPager) {
					
					currentItem++;
					Log.d("currentItem: " ,""+currentItem);
					handler.obtainMessage().sendToTarget(); // ͨ��Handler�л�ͼƬ
				}
			}

		}

		/**
		 * ��ViewPager��ҳ���״̬����ı�ʱ����
		 * 
		 * @author Administrator
		 * 
		 */
		private class MyPageChangeListener implements OnPageChangeListener {
			private int oldPosition = 0;

			/**
			 * This method will be invoked when a new page becomes selected.
			 * position: Position index of the new selected page.
			 */
			public void onPageSelected(int position) {
			//	currentItem = position;
				dots.get(oldPosition).setBackgroundResource(R.drawable.dot_normal);
				dots.get(position).setBackgroundResource(R.drawable.dot_focused);
				oldPosition = position;
			}

			public void onPageScrollStateChanged(int arg0) {

			}

			public void onPageScrolled(int arg0, float arg1, int arg2) {

			}
		}

		/**
		 * ���ViewPagerҳ���������
		 * 
		 * @author Administrator
		 * 
		 */
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
