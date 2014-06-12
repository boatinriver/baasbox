package com.baasbox.service.storage;

import com.baasbox.dao.AppDao;
import com.baasbox.dao.CollectionDao;
import com.baasbox.dao.exception.InvalidAppException;
import com.baasbox.dao.exception.InvalidCollectionException;
import com.baasbox.dao.exception.InvalidModelException;
import com.baasbox.dao.exception.SqlInjectionException;
import com.baasbox.util.QueryParams;
import com.orientechnologies.orient.core.record.impl.ODocument;

import java.util.List;

/**
 * Created by boat on 6/12/14.
 */
public class Appservice {
    public static void drop(String appName) throws Throwable {
        AppDao dao = AppDao.getInstance();
        dao.delete(appName);
    }

    public static ODocument create(String appName) throws Throwable, InvalidAppException, InvalidModelException {
        AppDao dao = AppDao.getInstance();
        ODocument doc=dao.create(appName);
        return doc;
    }

    public static boolean exists(String appName) throws InvalidCollectionException, SqlInjectionException{
        AppDao dao = AppDao.getInstance();
        return dao.existsApp(appName);
    }

    public static ODocument get(String appName) throws SqlInjectionException {
        AppDao dao = AppDao.getInstance();
        return dao.getByName(appName);
    }

    public static List<ODocument> getApps(QueryParams criteria) throws SqlInjectionException, InvalidCollectionException{
        AppDao dao = AppDao.getInstance();
        return dao.get(criteria);
    }

    public static long getAppsCount(QueryParams criteria) throws SqlInjectionException, InvalidCollectionException{
        AppDao dao = AppDao.getInstance();
        return dao.getCount(criteria);
    }
}
