package com.fengyang.tallynote.greendao;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.ToOne;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.DaoException;

/**
 * Created by Administrator on 2019/5/13.
 */

@Entity
public class User {

    @Id(autoincrement = true)
    private Long userId;

    @NotNull
    private Long adminId;

    @NotNull
    private String name;

    private String age;

    @ToOne(joinProperty = "adminId") //user和admin多对一关系
    private Admin admin;

    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    @Generated(hash = 1507654846)
    private transient UserDao myDao;

    @Generated(hash = 1165807840)
    public User(Long userId, @NotNull Long adminId, @NotNull String name,
            String age) {
        this.userId = userId;
        this.adminId = adminId;
        this.name = name;
        this.age = age;
    }

    @Generated(hash = 586692638)
    public User() {
    }

    public Long getUserId() {
        return this.userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getAdminId() {
        return this.adminId;
    }

    public void setAdminId(Long adminId) {
        this.adminId = adminId;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAge() {
        return this.age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    @Generated(hash = 2048802654)
    private transient Long admin__resolvedKey;

    /** To-one relationship, resolved on first access. */
    @Generated(hash = 1070459104)
    public Admin getAdmin() {
        Long __key = this.adminId;
        if (admin__resolvedKey == null || !admin__resolvedKey.equals(__key)) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            AdminDao targetDao = daoSession.getAdminDao();
            Admin adminNew = targetDao.load(__key);
            synchronized (this) {
                admin = adminNew;
                admin__resolvedKey = __key;
            }
        }
        return admin;
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 1868681880)
    public void setAdmin(@NotNull Admin admin) {
        if (admin == null) {
            throw new DaoException(
                    "To-one property 'adminId' has not-null constraint; cannot set to-one to null");
        }
        synchronized (this) {
            this.admin = admin;
            adminId = admin.getAdminId();
            admin__resolvedKey = adminId;
        }
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#delete(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 128553479)
    public void delete() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.delete(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#refresh(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 1942392019)
    public void refresh() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.refresh(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#update(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 713229351)
    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.update(this);
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 2059241980)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getUserDao() : null;
    }




}
