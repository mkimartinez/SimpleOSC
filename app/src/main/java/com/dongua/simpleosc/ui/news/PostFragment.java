package com.dongua.simpleosc.ui.news;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.dongua.simpleosc.App;
import com.dongua.simpleosc.R;
import com.dongua.simpleosc.base.fragment.BaseRecyclerFragment;
import com.dongua.simpleosc.bean.NewsTab;
import com.dongua.simpleosc.bean.PostBean;
import com.dongua.simpleosc.db.PostBeanDao;
import com.dongua.simpleosc.utils.SharedPreferenceUtil;
import com.orhanobut.logger.Logger;

import java.util.Date;
import java.util.List;

import static com.dongua.simpleosc.utils.Util.dateFormat;

/**
 * Created by duoyi on 17-11-27.
 */

public class PostFragment extends BaseRecyclerFragment<PostBean> implements NewsContract.View<PostBean> {

    private NewsContract.Presenter mPresenter;
    public static final String LAST_UPDATE_POSTBEAN = "update_post";

    @Override
    protected RecyclerView.Adapter getRecyclerAdapter() {
        return new PostRecyclerAdapter();
    }

    public static PostFragment newInstance(Context context, NewsTab tab) {

        PostFragment fragment = new PostFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(BUNDLE_TAB_BEAN, tab);
        fragment.setArguments(bundle);
        return fragment;

    }

    protected void initPresenter() {

        mPresenter = new NewsPresenter<PostBean>();
        mPresenter.attach(this);
    }

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);
        initPresenter();
    }

    @Override
    protected void initData() {
        super.initData();
        loadFromDB();

        //todo 把请求时间改为变量
        long lastUpdate = (long) SharedPreferenceUtil.get(LAST_UPDATE_POSTBEAN, 0L);
        long nowTime = new Date().getTime();
        if (!isRorate && (lastUpdate == 0 || nowTime - lastUpdate > 30 * 1000)) {
            SharedPreferenceUtil.put(LAST_UPDATE_POSTBEAN, nowTime);
            requestData();
        }
    }

    @Override
    protected void loadFromDB() {

        if (mDataList.isEmpty()) {
            List<PostBean> datas = App.getDaoSession().getPostBeanDao().queryBuilder()
                    .orderDesc(PostBeanDao.Properties.PubDateLong)
                    .limit(15)
                    .list();

            if (datas != null && !datas.isEmpty()) {
                mDataList.clear();
                mDataList.addAll(datas);
            }
        }
    }

    //todo 没有铺满首屏就调用loadMore
    @Override
    protected void loadMoreFromDB() {
        Logger.d("loadMoreFromDB");
        long minTime = mDataList.get(mDataList.size() - 1).getPubDateLong();
        //todo time相同的要做处理

        List<PostBean> data = App.getDaoSession().getPostBeanDao().queryBuilder()
                .where(PostBeanDao.Properties.PubDateLong.lt(minTime))
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
    protected void requestData() {
        mRefreshLayout.setRefreshing(true);

        if (mDataList.isEmpty()) {
            mPresenter.requestAll(mTab.getType());
        } else {
            mPresenter.requestBefore(mDataList.get(0).getPubDate(), mTab.getType());
        }
    }

    @Override
    public void requestFinished(List<PostBean> data) {
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
        mPresenter.detach();
        mPresenter.cancelRequest();
    }
//
//    private int id;
//    private String author;
//    private String pubDate;
//    private long pubDateLong;
//    private int authorid;
//    private String portrait;
//    private String title;
//    private int viewCount;
//
//    private int answerCount;
//    private String answerName;
//    private String answerTime;
//todo viewcount 添加
    class PostRecyclerAdapter extends RecyclerView.Adapter<PostRecyclerAdapter.PostHolder> {


        //todo 布局尚需调整  include复用
        @Override
        public PostHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View root = LayoutInflater.from(getActivity()).inflate(R.layout.layout_recycler_item_post, parent, false);
            return new PostHolder(root);
        }

        @Override
        public void onBindViewHolder(PostHolder holder, int position) {
            PostBean data = mDataList.get(position);
            holder.title.setText(data.getTitle());

            Glide.with(getContext())
                    .load(data.getPortrait())
                    .into(holder.portrait);
//            holder.description = data.get();
            holder.time.setText(String.format(getResources().getString(R.string.pub_info), data.getAuthor(), dateFormat(data.getPubDate())));
//            if (data.getCommentCount() > 0) {
//                holder.comment.setCompoundDrawables(getResources().getDrawable(R.mipmap.ic_comment), null, null, null);
//            }
            holder.answer.setText(String.valueOf(data.getAnswerCount()));

        }

        @Override
        public int getItemCount() {
            return mDataList.size();
        }


        class PostHolder extends RecyclerView.ViewHolder {

            ImageView portrait;
            TextView title;
            TextView description;
            TextView time;
            TextView answer;
            View line;

            PostHolder(View itemView) {
                super(itemView);
                portrait = itemView.findViewById(R.id.iv_author);
                title = itemView.findViewById(R.id.tv_question_title);
                description = itemView.findViewById(R.id.tv_question_content);
                time = itemView.findViewById(R.id.tv_author_time);
                answer = itemView.findViewById(R.id.tv_answer);
                line = itemView.findViewById(R.id.divider);
            }
        }
    }


}