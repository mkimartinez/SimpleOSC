package com.dongua.simpleosc.ui.tweet;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.dongua.simpleosc.App;
import com.dongua.simpleosc.R;
import com.dongua.simpleosc.activity.GalleryActivity;
import com.dongua.simpleosc.activity.TweetDetailActivity;
import com.dongua.simpleosc.base.adapter.BaseRecyclerAdapter;
import com.dongua.simpleosc.base.fragment.BaseRecyclerFragment;
import com.dongua.simpleosc.bean.TweetBean;
import com.dongua.simpleosc.db.TweetBeanDao;
import com.dongua.simpleosc.listener.RecyclerItemListener;
import com.dongua.simpleosc.ui.myview.NameImageView;
import com.dongua.simpleosc.ui.myview.OnSudokuItemClickListener;
import com.dongua.simpleosc.ui.myview.SudokuLayout;
import com.dongua.simpleosc.ui.news.PostFragment;
import com.dongua.simpleosc.utils.ActivitySwitcher;
import com.dongua.simpleosc.utils.SharedPreferenceUtil;
import com.dongua.simpleosc.utils.UIUtil;
import com.dongua.simpleosc.utils.Util;
import com.orhanobut.logger.Logger;

import java.util.Date;
import java.util.List;

import static com.dongua.simpleosc.fragment.MeFragment.DEFAULT_AVATAR;
import static com.dongua.simpleosc.utils.Util.dateFormat;

/**
 * author: Lewis
 * date: On 18-1-22.
 */

public class VpTweetFragment extends BaseRecyclerFragment<TweetBean> implements TweetContract.View {

    public static final String SUDOKU_POSTION = "pos";
    public static final String SUDOKU_NUMS = "nums";
    public static final String SUDOKU_URLS = "urls";

    public static final String TWEET_ACT_ID = "tweetid";
    public static final String TWEET_USR_ID = "authorid";
    public static final String BUNDLE_TWEET_FLAG = "tweet_flag";
    public static final String LAST_UPDATE_TWEET = "update_tweet";
    public static final int TYPE_LATEST = 0;
    public static final int TYPE_HOT = -1;
    public static final int TYPE_MINE = 3;
    private int mTweetType;

    private TweetPresenter mPresenter;

    @Override
    protected RecyclerView.Adapter getRecyclerAdapter() {
        TweetRecyclerAdapter adapter = new TweetRecyclerAdapter(getContext());
        adapter.setItemListener(new RecyclerItemListener() {
            @Override
            public void onClick(View view, int pos) {
//                UIUtil.showShortToast(mDataList.get(pos).getId()+"");
                Bundle bundle = new Bundle();
                bundle.putInt(TWEET_ACT_ID, mDataList.get(pos).getId());
                bundle.putInt(TWEET_USR_ID, mDataList.get(pos).getAuthorid());
                ActivitySwitcher.switchTo(getActivity(), TweetDetailActivity.class, bundle);
            }

            @Override
            public boolean onLongClick(View view, int pos) {
                return false;
            }
        });
        return adapter;
    }


    public static VpTweetFragment newInstance(Context context, int type) {

        VpTweetFragment fragment = new VpTweetFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(BUNDLE_TWEET_FLAG, type);
        fragment.setArguments(bundle);
        return fragment;

    }


    @Override
    protected void initArguments(Bundle bundle) {
        super.initArguments(bundle);
        mTweetType = bundle.getInt(BUNDLE_TWEET_FLAG, TYPE_LATEST);
    }

    protected void initPresenter() {
        mPresenter = new TweetPresenter();
        mPresenter.attach(this);
    }


    @Override
    protected void initWidget(View root) {
        super.initWidget(root);
        initPresenter();
    }

    @Override
    protected void initData() {
//        super.initData();
        loadFromDB();

        //todo 把请求时间改为变量
        long lastUpdate = (long) SharedPreferenceUtil.get(LAST_UPDATE_TWEET + mTweetType, 0L);
        long nowTime = new Date().getTime();
        if (!isRorate && (lastUpdate == 0 || nowTime - lastUpdate > 30 * 1000)) {
            SharedPreferenceUtil.put(LAST_UPDATE_TWEET + mTweetType, nowTime);
            requestData();
        }
    }

    @Override
    protected void requestData() {
        mRefreshLayout.setRefreshing(true);

        if (mDataList.isEmpty()) {
            mPresenter.requestAll(mTweetType);

        } else {
            mPresenter.requestBefore(mDataList.get(0).getPubDate(), mTweetType);
        }
    }

    @Override
    protected void loadFromDB() {
        if (mDataList.isEmpty()) {
            List data = App.getDaoSession().getTweetBeanDao().queryBuilder()
                    .where(TweetBeanDao.Properties.Type.eq(mTweetType))
                    .orderDesc(TweetBeanDao.Properties.PubDateLong)
                    .limit(15)
                    .list();

            if (data != null && !data.isEmpty()) {
                mDataList.clear();
                mDataList.addAll(data);
            }
        }
    }

    @Override
    protected void loadMoreFromDB() {
        long minTime = mDataList.get(mDataList.size() - 1).getPubDateLong();


        List<TweetBean> data = App.getDaoSession().getTweetBeanDao().queryBuilder()
                .orderDesc(TweetBeanDao.Properties.PubDateLong)
                .where(TweetBeanDao.Properties.PubDateLong.lt(minTime))
                .where(TweetBeanDao.Properties.Type.eq(mTweetType))
                .limit(15)
                .list();

        if (data != null && !data.isEmpty()) {
            mDataList.addAll(data);
            sendMsgLoadMoreSuccess();
        } else {
            sendMsgLoadMoreFail();
        }
    }

    @Override
    public void requestFinished(List<TweetBean> data) {
        if (data != null && !data.isEmpty()) {
            mDataList.addAll(0, data);
            sendMsgRequestSuccess();

        } else {
            sendMsgRequestNoUpdate();
        }
    }

    @Override
    public void requestFailed() {
        sendMsgRequestFail();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mPresenter != null)
            mPresenter.detach();
    }


    class TweetRecyclerAdapter extends BaseRecyclerAdapter<TweetRecyclerAdapter.TweetHolder> {


        public TweetRecyclerAdapter(Context mContext) {
            super(mContext);
        }


        @Override
        protected int getItemLayoutID() {
            return R.layout.layout_recycler_item_tweet;
        }

        @Override
        protected TweetRecyclerAdapter.TweetHolder getViewHolder(View root) {
            return new TweetHolder(root);
        }

        @Override
        public void onBindViewHolder(TweetRecyclerAdapter.TweetHolder holder, int position) {
            final int curPos = position;
            holder.itemView.setTag(position);

            TweetBean tb = mDataList.get(position);
            holder.author.setText(tb.getAuthor());
            holder.body.setText(tb.getBody());
            holder.time.setText(dateFormat(tb.getPubDate()));
            holder.comment.setText(String.valueOf(tb.getCommentCount()));
//            Glide.with(getContext())
//                    .load(tb.getPortrait())
//                    .into(holder.portrait);
            String potraitUrl = tb.getPortrait();
            if (potraitUrl.equals(DEFAULT_AVATAR)) {
                holder.portrait.setName(tb.getAuthor().substring(0, 1));

            } else {
                Glide.with(getContext())
                        .load(tb.getPortrait())
                        .into(holder.portrait);
            }

            holder.sudokuLayout.setItemClickListener(new OnSudokuItemClickListener() {
                @Override
                public void onClick(int pos) {
                    Bundle bundle = new Bundle();
                    bundle.putInt(SUDOKU_POSTION, pos);
                    bundle.putString(SUDOKU_URLS, mDataList.get(curPos).getImgBig());
                    ActivitySwitcher.switchTo(getActivity(), GalleryActivity.class, bundle, false);
                }
            });

            if (tb.getImgSmall() != null && !tb.getImgSmall().isEmpty()) {
                holder.sudokuLayout.setVisibility(View.VISIBLE);

                if (tb.getImgSmall().contains(",")) {

                    String[] urls = Util.splitImgUrls(tb.getImgSmall());
                    holder.sudokuLayout.setUrls(urls);
                } else {

                    holder.sudokuLayout.setUrl(tb.getImgSmall());
                }

            } else {
                holder.sudokuLayout.setVisibility(View.GONE);

            }


        }

        @Override
        public void onViewRecycled(TweetHolder holder) {
            if (holder != null) {
                Glide.with(getContext()).clear(holder.portrait);
            }
            super.onViewRecycled(holder);
        }


        @Override
        public int getItemCount() {
            return mDataList.size();
        }


        class TweetHolder extends RecyclerView.ViewHolder {

            TextView author;
            TextView body;
            TextView time;
            TextView comment;
            View line;
            NameImageView portrait;
            SudokuLayout sudokuLayout;

            TweetHolder(View itemView) {
                super(itemView);
                portrait = itemView.findViewById(R.id.iv_tweet_face);
                author = itemView.findViewById(R.id.tv_author_name);
                body = itemView.findViewById(R.id.tv_tweet_body);
                time = itemView.findViewById(R.id.tv_tweet_time);
                comment = itemView.findViewById(R.id.tv_tweet_comment_count);
                line = itemView.findViewById(R.id.h_line);
                sudokuLayout = itemView.findViewById(R.id.iv_img_small);
            }
        }
    }
}
