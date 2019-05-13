package com.fengyang.tallynote.model;

import java.util.Map;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.AbstractDaoSession;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.identityscope.IdentityScopeType;
import org.greenrobot.greendao.internal.DaoConfig;

import com.fengyang.tallynote.model.DayNote;
import com.fengyang.tallynote.model.IncomeNote;
import com.fengyang.tallynote.model.MemoNote;
import com.fengyang.tallynote.model.MonthNote;
import com.fengyang.tallynote.model.NotePad;
import com.fengyang.tallynote.model.User;

import com.fengyang.tallynote.model.DayNoteDao;
import com.fengyang.tallynote.model.IncomeNoteDao;
import com.fengyang.tallynote.model.MemoNoteDao;
import com.fengyang.tallynote.model.MonthNoteDao;
import com.fengyang.tallynote.model.NotePadDao;
import com.fengyang.tallynote.model.UserDao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * {@inheritDoc}
 * 
 * @see org.greenrobot.greendao.AbstractDaoSession
 */
public class DaoSession extends AbstractDaoSession {

    private final DaoConfig dayNoteDaoConfig;
    private final DaoConfig incomeNoteDaoConfig;
    private final DaoConfig memoNoteDaoConfig;
    private final DaoConfig monthNoteDaoConfig;
    private final DaoConfig notePadDaoConfig;
    private final DaoConfig userDaoConfig;

    private final DayNoteDao dayNoteDao;
    private final IncomeNoteDao incomeNoteDao;
    private final MemoNoteDao memoNoteDao;
    private final MonthNoteDao monthNoteDao;
    private final NotePadDao notePadDao;
    private final UserDao userDao;

    public DaoSession(Database db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        dayNoteDaoConfig = daoConfigMap.get(DayNoteDao.class).clone();
        dayNoteDaoConfig.initIdentityScope(type);

        incomeNoteDaoConfig = daoConfigMap.get(IncomeNoteDao.class).clone();
        incomeNoteDaoConfig.initIdentityScope(type);

        memoNoteDaoConfig = daoConfigMap.get(MemoNoteDao.class).clone();
        memoNoteDaoConfig.initIdentityScope(type);

        monthNoteDaoConfig = daoConfigMap.get(MonthNoteDao.class).clone();
        monthNoteDaoConfig.initIdentityScope(type);

        notePadDaoConfig = daoConfigMap.get(NotePadDao.class).clone();
        notePadDaoConfig.initIdentityScope(type);

        userDaoConfig = daoConfigMap.get(UserDao.class).clone();
        userDaoConfig.initIdentityScope(type);

        dayNoteDao = new DayNoteDao(dayNoteDaoConfig, this);
        incomeNoteDao = new IncomeNoteDao(incomeNoteDaoConfig, this);
        memoNoteDao = new MemoNoteDao(memoNoteDaoConfig, this);
        monthNoteDao = new MonthNoteDao(monthNoteDaoConfig, this);
        notePadDao = new NotePadDao(notePadDaoConfig, this);
        userDao = new UserDao(userDaoConfig, this);

        registerDao(DayNote.class, dayNoteDao);
        registerDao(IncomeNote.class, incomeNoteDao);
        registerDao(MemoNote.class, memoNoteDao);
        registerDao(MonthNote.class, monthNoteDao);
        registerDao(NotePad.class, notePadDao);
        registerDao(User.class, userDao);
    }
    
    public void clear() {
        dayNoteDaoConfig.clearIdentityScope();
        incomeNoteDaoConfig.clearIdentityScope();
        memoNoteDaoConfig.clearIdentityScope();
        monthNoteDaoConfig.clearIdentityScope();
        notePadDaoConfig.clearIdentityScope();
        userDaoConfig.clearIdentityScope();
    }

    public DayNoteDao getDayNoteDao() {
        return dayNoteDao;
    }

    public IncomeNoteDao getIncomeNoteDao() {
        return incomeNoteDao;
    }

    public MemoNoteDao getMemoNoteDao() {
        return memoNoteDao;
    }

    public MonthNoteDao getMonthNoteDao() {
        return monthNoteDao;
    }

    public NotePadDao getNotePadDao() {
        return notePadDao;
    }

    public UserDao getUserDao() {
        return userDao;
    }

}
