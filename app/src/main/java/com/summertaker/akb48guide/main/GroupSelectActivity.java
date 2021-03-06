package com.summertaker.akb48guide.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.github.paolorotolo.expandableheightlistview.ExpandableHeightGridView;
import com.summertaker.akb48guide.R;
import com.summertaker.akb48guide.birthday.BirthMonthActivity;
import com.summertaker.akb48guide.common.BaseActivity;
import com.summertaker.akb48guide.common.Config;
import com.summertaker.akb48guide.data.DataManager;
import com.summertaker.akb48guide.data.GroupData;
import com.summertaker.akb48guide.janken.JankenStageActivity;
import com.summertaker.akb48guide.member.TeamListActivity;
import com.summertaker.akb48guide.puzzle.PuzzleLevelActivity;
import com.summertaker.akb48guide.quiz.MemoryActivity;
import com.summertaker.akb48guide.quiz.QuizActivity;
import com.summertaker.akb48guide.quiz.SlideActivity;
import com.summertaker.akb48guide.rawphoto.RawPhotoSelectActivity;

import java.util.ArrayList;

public class GroupSelectActivity extends BaseActivity {

    String mAction;
    String mTitle;

    ArrayList<GroupData> mGroupDataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_select_activity);

        mContext = GroupSelectActivity.this;

        Intent intent = getIntent();
        mAction = intent.getStringExtra("action");
        mTitle = intent.getStringExtra("title");

        switch (mAction) {
            case Config.MAIN_ACTION_SLIDE:
                mTitle = getString(R.string.slide);
                break;
            case Config.MAIN_ACTION_MEMORY:
                mTitle = getString(R.string.memory);
                break;
            case Config.MAIN_ACTION_QUIZ:
                mTitle = getString(R.string.quiz);
                break;
        }
        if (mTitle == null) {
            mTitle = getString(R.string.app_name);
        }

        initBaseToolbar(Config.TOOLBAR_ICON_BACK, mTitle);

        DataManager dataManager = new DataManager(mContext);
        mGroupDataList = dataManager.getGroupList(mAction);

        //initGridText();
        initGridView();
    }

    private void initGridText() {
        LinearLayout loListView = (LinearLayout) findViewById(R.id.loListView);
        if (loListView != null) {
            //loListView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.card_background));
            loListView.setVisibility(View.VISIBLE);
            GridView gridText = (GridView) findViewById(R.id.gridText);
            if (gridText != null) {
                gridText.setAdapter(new GroupSelectTextAdapter(mContext, mGroupDataList));
                gridText.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        GroupData groupData = (GroupData) parent.getItemAtPosition(position);
                        goActivity(groupData);
                    }
                });
            }
        }
    }

    private void initGridView() {
        ScrollView loGridView = (ScrollView) findViewById(R.id.loGridView);
        if (loGridView != null) {
            loGridView.setVisibility(View.VISIBLE);
            //loGridView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.white));
            ExpandableHeightGridView gridView = (ExpandableHeightGridView) findViewById(R.id.gridView);
            if (gridView != null) {
                gridView.setExpanded(true);
                gridView.setAdapter(new GroupSelectGridAdapter(mContext, mGroupDataList));
                gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        GroupData groupData = (GroupData) parent.getItemAtPosition(position);
                        goActivity(groupData);
                    }
                });
            }
        }
    }

    public void goActivity(GroupData groupData) {

        Intent intent = null;

        //Setting setting = new Setting(mContext);
        //String displayProfilePhoto = setting.get(Config.SETTING_DISPLAY_OFFICIAL_PHOTO);

        switch (mAction) {
            case Config.MAIN_ACTION_MEMBER:
                intent = new Intent(this, TeamListActivity.class);
                break;
            case Config.MAIN_ACTION_BIRTHDAY:
                intent = new Intent(this, BirthMonthActivity.class);
                break;
            case Config.MAIN_ACTION_RAW_PHOTO:
                intent = new Intent(this, RawPhotoSelectActivity.class);
                break;
            case Config.MAIN_ACTION_JANKEN:
                intent = new Intent(this, JankenStageActivity.class);
                break;
            case Config.MAIN_ACTION_SLIDE:
                //if (displayProfilePhoto.equals(Config.SETTING_DISPLAY_OFFICIAL_PHOTO_YES)) {
                    intent = new Intent(this, SlideActivity.class);
                //} else {
                //    intent = new Intent(this, SlideTextActivity.class);
                //}
                break;
            case Config.MAIN_ACTION_MEMORY:
                intent = new Intent(this, MemoryActivity.class);
                break;
            case Config.MAIN_ACTION_QUIZ:
                intent = new Intent(this, QuizActivity.class);
                break;
            case Config.MAIN_ACTION_PUZZLE:
            case Config.MAIN_ACTION_ENIGMA:
                intent = new Intent(this, PuzzleLevelActivity.class);
                break;
        }
        //intent = new Intent(this, QuizResultActivity.class);

        if (intent != null) {
            intent.putExtra("action", mAction);
            intent.putExtra("title", mTitle);
            intent.putExtra("groupData", groupData);
            //showToolbarProgressBar();
            startActivityForResult(intent, 0);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Log.e(mTag, "onActivityResult().resultCode: " + resultCode);

        if (resultCode == Config.RESULT_CODE_FINISH) {
            finish();
        }

        //hideToolbarProgressBar();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
