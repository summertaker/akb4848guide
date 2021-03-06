package com.summertaker.akb48guide.quiz;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.summertaker.akb48guide.data.MemberData;

import java.util.ArrayList;

public class SlideAdapter  extends FragmentPagerAdapter {

    ArrayList<MemberData> mMemberList = new ArrayList<>();

    public SlideAdapter(FragmentManager fm, ArrayList<MemberData> memberList) {
        super(fm);
        this.mMemberList = memberList;
    }

    @Override
    public Fragment getItem(int position) {
        return SlideFragment.newInstance(position, mMemberList.get(position));
    }

    @Override
    public int getCount() {
        return mMemberList.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mMemberList.get(position).getName();
    }
}
