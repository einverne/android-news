package cn.edu.blcu.newsfeed;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import cn.edu.blcu.newsfeed.tabactivity.TabSearchActivity;

public class FragmentMenu extends ListFragment {

	private static final int Zuijinxinwen_id = 0;
	private static final int Hot_id = 1;
	private static final int Search_id = 2;
	private static final int Login_id = 3;

	public FragmentMenu() {

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		return inflater.inflate(R.layout.menulist, null);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {

		String[] menu = getResources().getStringArray(R.array.sliding_menu);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
				android.R.layout.simple_list_item_1, android.R.id.text1, menu);
		setListAdapter(adapter);
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		Fragment newContent = null;
		switch (position) {
		case Zuijinxinwen_id:
			newContent = new ZuijinxinwenFragment();
			break;
		case Hot_id:
			newContent = new HotFragment();
			// Intent intent = new Intent(getActivity(), hot_main.class);
			// startActivity(intent);
			break;
		case Search_id:
			Intent intent_search = new Intent(getActivity(),
					TabSearchActivity.class);
			startActivity(intent_search);
			break;
		case Login_id:
			newContent = new LoginFragment();
			// Intent intent_login = new Intent(getActivity(),
			// TabLoginActivity.class);
			// startActivity(intent_login);
			break;
		default:
			break;
		}
		if (newContent != null)
			switchFragment(newContent);
		super.onListItemClick(l, v, position, id);
	}

	// the meat of switching the above fragment
	private void switchFragment(Fragment fragment) {
		if (getActivity() == null)
			return;

		if (getActivity() instanceof MainFragment) {
			MainFragment fca = (MainFragment) getActivity();
			fca.switchContent(fragment);
		}
		// else if (getActivity() instanceof ResponsiveUIActivity) {
		// ResponsiveUIActivity ra = (ResponsiveUIActivity) getActivity();
		// ra.switchContent(fragment);
		// }
	}
}
