package com.dongua.simpleosc.base.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.dongua.simpleosc.R;
import com.dongua.simpleosc.bean.NewsTab;
import com.dongua.simpleosc.ui.myview.LoadMoreView;
import com.dongua.simpleosc.utils.UIUtil;
import com.orhanobut.logger.Logger;
import com.youth.banner.Banner;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;


/**
 * Created by Leiws on 17-11-27.
 */

public abstract class BaseRecyclerFragment<T extends Parcelable> extends BaseFragment {

    public static final String BUNDLE_TAB_BEAN = "tabbean";
    public static final String BUNDLE_SUB_BEAN = "subbean";
    public static final String BUNDLE_RV_POS = "rvpos";
    public static final int MSG_REQUEST_SUCCESS = 1;
    private static final int MSG_REQUEST_FAIL = 2;
    private static final int MSG_LOADMORE_SUCCESS = 3;
    private static final int MSG_LOADMORE_FAIL = 4;
    private static final int MSG_REQUEST_NO_UPDATE = 5;

    protected boolean isRorate = false;

    //    protected BaseRecyclerAdapter<T> mAdapter;
    protected RecyclerView.Adapter mAdapter;
    protected List<T> mDataList;

    @BindView(R.id.rv_banner)
    protected Banner mBanner;
    @BindView(R.id.rv_content)
    protected RecyclerView mRecyclerView;
    @BindView(R.id.swipe_refresh)
    protected SwipeRefreshLayout mRefreshLayout;
    @BindView(R.id.cv_loadmore)
    protected LoadMoreView mLoadMore;

//    //todo 考虑放到父类
//    protected BasePresenter mPresenter;
//
//    protected abstract void initPresenter();


    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_REQUEST_SUCCESS:
                    hideProgress();
                    mAdapter.notifyDataSetChanged();
                    break;
                case MSG_REQUEST_NO_UPDATE:
                    hideProgress();
                    break;
                case MSG_REQUEST_FAIL:
                    hideProgress();
                    UIUtil.showShortToast(getContext(), getString(R.string.request_fail));
                    break;
                case MSG_LOADMORE_SUCCESS:
                    hideProgress();
                    mAdapter.notifyDataSetChanged();
                    break;
                case MSG_LOADMORE_FAIL:
                    hideProgress();
                    break;
                default:
                    ;

            }
        }
    };

    protected void sendMsgRequestNoUpdate() {
        Message message = Message.obtain();
        message.what = MSG_REQUEST_NO_UPDATE;
        mHandler.sendMessage(message);
    }

    protected void sendMsgRequestSuccess() {
        Message message = Message.obtain();
        message.what = MSG_REQUEST_SUCCESS;
        mHandler.sendMessage(message);
    }

    protected void sendMsgRequestFail() {
        Message message = Message.obtain();
        message.what = MSG_REQUEST_FAIL;
        mHandler.sendMessage(message);
    }

    protected void sendMsgLoadMoreSuccess() {
        Message message = Message.obtain();
        message.what = MSG_LOADMORE_SUCCESS;
        mHandler.sendMessageDelayed(message, 1000);
    }

    protected void sendMsgLoadMoreFail() {
        Message message = Message.obtain();
        message.what = MSG_LOADMORE_FAIL;
        mHandler.sendMessageDelayed(message, 1000);

    }

    private void hideProgress() {
        if (mRefreshLayout.isRefreshing())
            mRefreshLayout.setRefreshing(false);
        if (mLoadMore.getVisibility() != View.GONE)
            mLoadMore.setVisibility(View.GONE);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_sub_base;
    }

    @Override
    protected void initArguments(Bundle bundle) {
        super.initArguments(bundle);

    }

    @Override
    protected void initBundle(Bundle savedInstanceState) {
        super.initBundle(savedInstanceState);
        if (savedInstanceState != null) {

            mDataList = savedInstanceState.getParcelableArrayList(BUNDLE_SUB_BEAN);
            if (mDataList == null) {
                mDataList = new ArrayList<>();
            } else {
                isRorate = true;
            }
        } else {
            mDataList = new ArrayList<>();
        }
    }

    @Override
    protected void saveBundle(Bundle outState) {
        super.saveBundle(outState);
        outState.putParcelableArrayList(BUNDLE_SUB_BEAN, (ArrayList<T>) mDataList);
    }

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);

        mLoadMore.setDrawSpeed(LoadMoreView.FAST);

//        if (mTab != null && mTab.getShowBanner()) {
//            //show banner
//        }

        mAdapter = getRecyclerAdapter();
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(mAdapter);

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
//                Logger.d("qq"+layoutManager.findLastVisibleItemPosition());
//                Logger.d("qq"+layoutManager.getChildCount());
//                Logger.d("qq"+layoutManager.getItemCount());
                if (!recyclerView.canScrollVertically(1)
                        ) {
                    //&& layoutManager.getChildCount()!=layoutManager.getItemCount()

                    //todo item很少的情况下会触发

                    mLoadMore.setVisibility(View.VISIBLE);
                    loadMoreFromDB();
//                    mPresenter.loadMore();

                }
            }
        });

        mRefreshLayout.setDistanceToTriggerSync(300);
//        mRefreshLayout.setSize(SwipeRefreshLayout.LARGE);
        mRefreshLayout.setColorSchemeResources(R.color.red200, R.color.yellow200,
                R.color.blue200, R.color.green200);
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestData();
            }
        });
    }


//    @Override
//    protected void initData() {
//        super.initData();
//
//        loadFromDB();
//        if (!isRorate) {
//            requestData();
//        }
//    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacksAndMessages(null);
    }

    //    protected abstract BaseRecyclerAdapter<T> getRecyclerAdapter();
    protected abstract RecyclerView.Adapter getRecyclerAdapter();

    protected abstract void requestData();

    protected abstract void loadFromDB();

    protected abstract void loadMoreFromDB();

}
