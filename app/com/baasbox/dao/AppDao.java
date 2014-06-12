package com.baasbox.dao;

import com.baasbox.dao.exception.AppAlreadyExistsException;
import com.baasbox.dao.exception.InvalidAppException;
import com.baasbox.dao.exception.SqlInjectionException;
import com.baasbox.db.DbHelper;
import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.index.OIndex;
import com.orientechnologies.orient.core.record.impl.ODocument;
import play.Logger;

/**
 * Created by boat on 6/11/14.
 */
public class AppDao extends NodeDao {

    private final static String MODEL_NAME="_BB_App";
    public final static String NAME="name";
    private static final String APP_NAME_INDEX = "_BB_App.name";

    public static AppDao getInstance(){
        return new AppDao();
    }

    protected AppDao() {
        super(MODEL_NAME);
    }

    @Override
    @Deprecated
    public ODocument create(){
        throw new IllegalAccessError("To create a new app call create(String appName)");
    }

    public ODocument create(String appName) throws Throwable {
        if (Logger.isTraceEnabled()) Logger.trace("Method Start");
        try {
            if (existsApp(appName)) throw new AppAlreadyExistsException("App  " + appName + " already exists");
        }catch (SqlInjectionException e){
            throw new InvalidAppException(e);
        }
        if (Character.isDigit(appName.charAt(0))){
            throw new InvalidAppException("App names cannot start by a digit");
        }
        ODocument doc = super.create();
        doc.field("name",appName);
        if(appName.toUpperCase().startsWith("_BB_")){
            throw new InvalidAppException("App name is not valid: it can't be prefixed with _BB_");
        }
        save(doc);
        if (Logger.isTraceEnabled()) Logger.trace("Method End");
        return doc;
    }

    public boolean existsApp(String appName) throws SqlInjectionException{
        if (Logger.isTraceEnabled()) Logger.trace("Method Start");
        OIndex idx = db.getMetadata().getIndexManager().getIndex(APP_NAME_INDEX);
        OIdentifiable record = (OIdentifiable) idx.get( appName );
        if (Logger.isTraceEnabled()) Logger.trace("Method End");
        return (record!=null) ;
    }

    public ODocument getByName(String appName) throws SqlInjectionException{
        if (Logger.isTraceEnabled()) Logger.trace("Method Start");
        OIndex idx = db.getMetadata().getIndexManager().getIndex(APP_NAME_INDEX);
        OIdentifiable record = (OIdentifiable) idx.get(appName);
        if (record==null) return null;
        return db.load(record.getIdentity());
    }
}
