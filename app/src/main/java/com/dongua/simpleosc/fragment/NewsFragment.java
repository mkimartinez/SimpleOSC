package com.dongua.simpleosc.fragment;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.dongua.simpleosc.R;
import com.dongua.simpleosc.base.fragment.BaseViewPagerFragment;
import com.dongua.simpleosc.bean.NewsTab;
import com.dongua.simpleosc.ui.news.PostFragment;
import com.dongua.simpleosc.ui.news.SubFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

import static com.dongua.simpleosc.bean.NewsTab.TYPE_BLOG;
import static com.dongua.simpleosc.bean.NewsTab.TYPE_NEWS;
import static com.dongua.simpleosc.bean.NewsTab.TYPE_POST;

/**
 * Created by Leiws on 17-11-18.
 */

public class NewsFragment extends BaseViewPagerFragment {

    private String[] tabs = new String[]{"开源资讯", "推荐博客", "技术问答", "每日一搏"};

    private List<NewsTab> tabList;

    @BindView(R.id.tl_news_tab)
    TabLayout mTabLayout;
    @BindView(R.id.vp_content)
    ViewPager mViewPager;

    private ContentPagerAdapter mAdapter;


    @Override
    protected int getLayoutId() {
        return R.layout.fragment_news_tab;
    }


    @Override
    protected void initBundle(Bundle bundle) {
        super.initBundle(bundle);

    }

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);

        mTabLayout.setTabMode(TabLayout.MODE_FIXED);
        NewsTab tab1 = new NewsTab("资讯", TYPE_NEWS, false, "");
        NewsTab tab2 = new NewsTab("博客", TYPE_BLOG, false, "");
        NewsTab tab3 = new NewsTab("问答", TYPE_POST, false, "");
//        NewsTab tab4 = new NewsTab("每日一搏", TYPE_DAILY, false, "");
        tabList = new ArrayList<>();
        tabList.add(tab1);
        tabList.add(tab2);
        tabList.add(tab3);
//        tabList.add(tab4);
        mAdapter = new ContentPagerAdapter(getChildFragmentManager(), tabList);
        mViewPager.setAdapter(mAdapter);

//        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
//            @Override
//            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//
//            }
//
//            @Override
//            public void onPageSelected(int position) {
//
//            }
//
//            @Override
//            public void onPageScrollStateChanged(int state) {
//
//            }
//        });

        mTabLayout.setupWithViewPager(mViewPager);


    }

    @Override
    public int getScroll() {
        return 200;
    }


    public class ContentPagerAdapter extends FragmentPagerAdapter {

        private List<NewsTab> tabList;

        ContentPagerAdapter(FragmentManager fm, List<NewsTab> data) {
            super(fm);
            tabList = data;
        }

        @Override
        public Fragment getItem(int position) {
//            switch (tabList.get(position).getType()) {
//                case TYPE_NEWS:
//                    return SubFragment.newInstance(getContext(), tabList.get(position));
//
//                case TYPE_BLOG:
//                    return TabFragment.newInstance(getContext(), tabList.get(position));
//
//                default:
//                    return null;
//            }
//            if (position   == 0)
//                return TabFragment.newInstance(getContext(), tabList.get(position));
//            else if (position   == 1)
//                return SubFragment.newInstance(getContext(), tabList.get(position));
//            else
            if(position == 2){
                return PostFragment.newInstance(getContext(), tabList.get(position));
            }
                return SubFragment.newInstance(getContext(), tabList.get(position));
        }

        @Override
        public int getCount() {
            return tabList.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabList.get(position).getName();
        }
    }
}
