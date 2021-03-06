package com.dongua.simpleosc.db;

import java.util.Map;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.AbstractDaoSession;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.identityscope.IdentityScopeType;
import org.greenrobot.greendao.internal.DaoConfig;

import com.dongua.simpleosc.bean.PostBean;
import com.dongua.simpleosc.bean.UserBean;
import com.dongua.simpleosc.bean.SubBean;
import com.dongua.simpleosc.bean.TweetBean;
import com.dongua.simpleosc.bean.DetailUserBean;

import com.dongua.simpleosc.db.PostBeanDao;
import com.dongua.simpleosc.db.UserBeanDao;
import com.dongua.simpleosc.db.SubBeanDao;
import com.dongua.simpleosc.db.TweetBeanDao;
import com.dongua.simpleosc.db.DetailUserBeanDao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * {@inheritDoc}
 * 
 * @see org.greenrobot.greendao.AbstractDaoSession
 */
public class DaoSession extends AbstractDaoSession {

    private final DaoConfig postBeanDaoConfig;
    private final DaoConfig userBeanDaoConfig;
    private final DaoConfig subBeanDaoConfig;
    private final DaoConfig tweetBeanDaoConfig;
    private final DaoConfig detailUserBeanDaoConfig;

    private final PostBeanDao postBeanDao;
    private final UserBeanDao userBeanDao;
    private final SubBeanDao subBeanDao;
    private final TweetBeanDao tweetBeanDao;
    private final DetailUserBeanDao detailUserBeanDao;

    public DaoSession(Database db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        postBeanDaoConfig = daoConfigMap.get(PostBeanDao.class).clone();
        postBeanDaoConfig.initIdentityScope(type);

        userBeanDaoConfig = daoConfigMap.get(UserBeanDao.class).clone();
        userBeanDaoConfig.initIdentityScope(type);

        subBeanDaoConfig = daoConfigMap.get(SubBeanDao.class).clone();
        subBeanDaoConfig.initIdentityScope(type);

        tweetBeanDaoConfig = daoConfigMap.get(TweetBeanDao.class).clone();
        tweetBeanDaoConfig.initIdentityScope(type);

        detailUserBeanDaoConfig = daoConfigMap.get(DetailUserBeanDao.class).clone();
        detailUserBeanDaoConfig.initIdentityScope(type);

        postBeanDao = new PostBeanDao(postBeanDaoConfig, this);
        userBeanDao = new UserBeanDao(userBeanDaoConfig, this);
        subBeanDao = new SubBeanDao(subBeanDaoConfig, this);
        tweetBeanDao = new TweetBeanDao(tweetBeanDaoConfig, this);
        detailUserBeanDao = new DetailUserBeanDao(detailUserBeanDaoConfig, this);

        registerDao(PostBean.class, postBeanDao);
        registerDao(UserBean.class, userBeanDao);
        registerDao(SubBean.class, subBeanDao);
        registerDao(TweetBean.class, tweetBeanDao);
        registerDao(DetailUserBean.class, detailUserBeanDao);
    }
    
    public void clear() {
        postBeanDaoConfig.clearIdentityScope();
        userBeanDaoConfig.clearIdentityScope();
        subBeanDaoConfig.clearIdentityScope();
        tweetBeanDaoConfig.clearIdentityScope();
        detailUserBeanDaoConfig.clearIdentityScope();
    }

    public PostBeanDao getPostBeanDao() {
        return postBeanDao;
    }

    public UserBeanDao getUserBeanDao() {
        return userBeanDao;
    }

    public SubBeanDao getSubBeanDao() {
        return subBeanDao;
    }

    public TweetBeanDao getTweetBeanDao() {
        return tweetBeanDao;
    }

    public DetailUserBeanDao getDetailUserBeanDao() {
        return detailUserBeanDao;
    }

}
