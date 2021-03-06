package com.summertaker.akb48guide.janken;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.ScrollView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.github.paolorotolo.expandableheightlistview.ExpandableHeightGridView;
import com.summertaker.akb48guide.R;
import com.summertaker.akb48guide.common.BaseActivity;
import com.summertaker.akb48guide.common.BaseApplication;
import com.summertaker.akb48guide.common.Config;
import com.summertaker.akb48guide.common.Setting;
import com.summertaker.akb48guide.data.GroupData;
import com.summertaker.akb48guide.data.MemberData;
import com.summertaker.akb48guide.data.TeamData;
import com.summertaker.akb48guide.data.WebData;
import com.summertaker.akb48guide.parser.BaseParser;
import com.summertaker.akb48guide.util.Util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class JankenTeamActivity extends BaseActivity {

    String mTitle;
    String mAction;
    GroupData mGroupData;
    ArrayList<MemberData> mGroupMemberList = new ArrayList<>();
    ArrayList<TeamData> mTeamMemberList = new ArrayList<>();
    JankenTeamAdapter mAdapter;

    ProgressBar mPbLoading;

    boolean mIsMobile = false;

    Setting mSetting;

    //CacheManager mCacheManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = JankenTeamActivity.this;
        mResources = mContext.getResources();

        setContentView(R.layout.janken_team_activity);

        Intent intent = getIntent();
        mAction = intent.getStringExtra("action");
        mGroupData = (GroupData) intent.getSerializableExtra("groupData");

        mTitle = getString(R.string.rock_paper_scissors) + " / " + mGroupData.getName();
        initBaseToolbar(Config.TOOLBAR_ICON_BACK, mTitle);
        mBaseToolbar.setBackgroundColor(ContextCompat.getColor(mContext, R.color.janken_primary));

        mPbLoading = (ProgressBar) findViewById(R.id.pbLoading);
        Util.setProgressBarColor(mPbLoading, Config.PROGRESS_BAR_COLOR_TRANSPARENT, null);

        mAdapter = new JankenTeamAdapter(this, mGroupData, mTeamMemberList);

        //mCacheManager = new CacheManager(mSharedPreferences);

        mSetting = new Setting(mContext);

        String url = mGroupData.getUrl();
        String userAgent = Config.USER_AGENT_WEB;

        requestData(url, userAgent);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mTeamMemberList.size() > 0) {
            updateData();
            mAdapter.notifyDataSetChanged();

            int count = 0;
            for (TeamData teamData : mTeamMemberList) {
                //Log.e(mTag, teamData.getName());
                String key = Config.JANKEN_KEY_RESULT + mGroupData.getName() + teamData.getName();
                String val = mSetting.get(key);
                if (!val.isEmpty()) {
                    count++;
                }
            }

            if (count == mTeamMemberList.size()) {
                mSetting.set(Config.JANKEN_KEY_RESULT + mGroupData.getName(), "1");
            }
        }

        //mSetting.set(Config.JANKEN_KEY_RESULT + mGroupData.getName(), "");
    }

    private void requestData(final String url, final String userAgent) {
        //Log.e(mTag, "url: " + url);
        //Log.e(mTag, "userAgent: " + userAgent);

        //final String cacheId = Util.urlToId(url);
        //String cacheData = mCacheManager.load(cacheId);

        //if (cacheData == null) {
        StringRequest strReq = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Log.e(mTag, response);
                //mCacheManager.save(cacheId, response);
                parseData(url, response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(mTag, "NETWORK ERROR: " + url);
                mErrorMessage = Util.getErrorMessage(error);
                parseData(url, "");
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                //headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put("User-agent", userAgent);
                return headers;
            }
        };

        BaseApplication.getInstance().addToRequestQueue(strReq, "strReq");
        //} else {
        //    parseData(url, cacheData);
        //}
    }

    private void parseData(String url, String response) {
        BaseParser baseParser = new BaseParser();
        baseParser.parseMemberList(mContext, response, mGroupData, mGroupMemberList, mTeamMemberList, mIsMobile);

        updateData();
    }

    private void updateData() {
        for (TeamData teamData : mTeamMemberList) {
            //Log.e(mTag, teamData.getName());
            String key = Config.JANKEN_KEY_RESULT + mGroupData.getName() + teamData.getName();
            String val = mSetting.get(key);
            teamData.setLocked(val.isEmpty());
        }

        renderData();
    }

    private void renderData() {
        mPbLoading.setVisibility(View.GONE);

        //String team = getString(R.string.team);
        //String trainee = getString(R.string.trainee);

        if (mGroupMemberList.size() == 0) {
            alertNetworkErrorAndFinish(mErrorMessage);
        } else {
            //String title = getResources().getString(R.string.s_people, mGroupMemberList.size());
            //title = " (" + title + ")";
            //mBaseToolbar.setTitle(mTitle + title);

            ScrollView scrollView = (ScrollView) findViewById(R.id.scrollView);
            scrollView.setVisibility(View.VISIBLE);

            ExpandableHeightGridView gridView = (ExpandableHeightGridView) findViewById(R.id.gridView);
            if (gridView != null) {
                gridView.setExpanded(true);
                gridView.setAdapter(mAdapter);
                gridView.setOnItemClickListener(itemClickListener);
            }
        }
    }

    private AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            TeamData teamData = (TeamData) parent.getItemAtPosition(position);
            //Log.e(mTag, teamData.getName());

            ArrayList<MemberData> memberList = new ArrayList<>();
            for (MemberData memberData : mGroupMemberList) {
                if (memberData.getTeamName().equals(teamData.getName())) {
                    //Log.e(mTag, ">> " + md.getTeamName());
                    memberData.setGroupId(mGroupData.getId());
                    memberList.add(memberData);
                }
            }
            goActivity(teamData, memberList);
        }
    };

    public void goActivity(TeamData teamData, ArrayList<MemberData> memberList) {
        Intent intent = new Intent(this, JankenStageActivity.class);
        intent.putExtra("groupData", mGroupData);
        intent.putExtra("teamData", teamData);
        intent.putExtra("memberList", memberList);

        startActivityForResult(intent, 0);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
