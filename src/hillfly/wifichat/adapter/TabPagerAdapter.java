package hillfly.wifichat.adapter;

import hillfly.wifichat.BaseFragment;

import java.util.List;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentPagerAdapter;

public class TabPagerAdapter extends FragmentPagerAdapter {

	List<BaseFragment> fragments;

	public void SetDate(List<BaseFragment> fragments) {
		this.fragments = fragments;
	}

	public TabPagerAdapter(FragmentManager fm,List<BaseFragment> fragments) {
		super(fm);
		this.fragments = fragments;
	}

	@Override
	public Fragment getItem(int position) {
		return fragments.get(position);
	}
	
	@Override
	public int getCount() {
		return fragments.size();
	}
}
