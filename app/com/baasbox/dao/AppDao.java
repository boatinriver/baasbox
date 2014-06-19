package com.baasbox.dao;

import com.baasbox.dao.exception.AppAlreadyExistsException;
import com.baasbox.dao.exception.InvalidAppException;
import com.baasbox.dao.exception.InvalidCollectionException;
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

    @Override
    public void delete(String name) throws Exception{
        if (!existsApp(name)) throw new InvalidAppException("App " + name + " does not exists");

        //get the helper class
        GenericDao gdao = GenericDao.getInstance();

        //begin transaction
        DbHelper.requestTransaction();

        try {
//            //delete all vertices linked to objects in this class
//            String deleteVertices =
//                    "delete vertex _bb_nodevertex where _node.@class=?";
            Object[] params={name};
//            gdao.executeCommand(deleteVertices, params);

//            //delete vertices linked to the collection entry in the _bb_collection class
//            //note: the params are equals to the previous one (just the collection name)
//            String deleteVertices2="delete vertex _bb_nodevertex where _node.@class='_bb_collection' and _node.name=?";
//            gdao.executeCommand(deleteVertices2, params);


            //delete this collection from the list of declared collections
            //note: the params are equals to the previous one (just the collection name)
            String deleteFromCollections= "delete from _bb_app where name =?";
            gdao.executeCommand(deleteFromCollections, params);

//            //delete all records belonging to the dropping collection....
//            //it could be done dropping the class, but in this case we not should be able to perform a rollback
//            String deleteAllRecords= "delete from " + name;
//            gdao.executeCommand(deleteAllRecords, new Object[] {});

            //commit
            DbHelper.commitTransaction();

//            //drop the collection class
//            String dropCollection= "drop class " + name;
//            gdao.executeCommand(dropCollection, new Object[] {});

        } catch (Exception e) {
            //rollback in case of error
            DbHelper.rollbackTransaction();
            if (Logger.isDebugEnabled()) Logger.debug ("An error occured deleting the app " + name, e);
            throw e;
        }
    }//delete
}
