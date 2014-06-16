/*
     Copyright 2012-2013 
     Claudio Tesoriero - c.tesoriero-at-baasbox.com

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */
package com.baasbox.controllers;

import com.baasbox.controllers.actions.filters.AnonymousCredentialWrapFilter;
import com.baasbox.controllers.actions.filters.ConnectToDBFilter;
import com.baasbox.dao.UserDao;
import com.baasbox.dao.exception.InvalidCollectionException;
import com.baasbox.dao.exception.SqlInjectionException;
import com.baasbox.service.user.UserService;
import com.baasbox.util.JSONFormats;
import com.baasbox.util.Util;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import com.orientechnologies.orient.core.exception.OSerializationException;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.serialization.serializer.OJSONWriter;
import org.apache.commons.lang.exception.ExceptionUtils;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

import play.Logger;
import play.mvc.Http;
import play.mvc.Http.Context;

import com.baasbox.util.QueryParams;
import com.baasbox.util.IQueryParametersKeys;
import com.baasbox.BBConfiguration;
import com.baasbox.IBBConfigurationKeys;
import play.mvc.With;

import java.security.InvalidParameterException;
import java.util.List;


public class Application extends Controller {
  
	  /***
	   * Admin panel web page
	   * @return
	   */
	  public static Result login(){
		  String version = BBConfiguration.configuration.getString(IBBConfigurationKeys.API_VERSION);
		  String edition = BBConfiguration.configuration.getString(IBBConfigurationKeys.EDITION);
		  return ok(views.html.admin.index.render(version,edition));
	  } 
	  
	//renders the spashscreen
  public static Result index() {
	  String version = BBConfiguration.configuration.getString(IBBConfigurationKeys.API_VERSION);
	  String edition = BBConfiguration.configuration.getString(IBBConfigurationKeys.EDITION);
	  return ok(views.html.index.render(version,edition));
  }
  
  public static Result apiVersion() {
	  ObjectNode result = Json.newObject();
	  result.put("api_version", BBConfiguration.configuration.getString(IBBConfigurationKeys.API_VERSION));
	  result.put("edition", BBConfiguration.configuration.getString(IBBConfigurationKeys.API_VERSION));
	  return ok(result);
  }

  public static Result appList(){

      if (Logger.isTraceEnabled()) Logger.trace("Method Start");
      Context ctx=Http.Context.current.get();
      QueryParams criteria = (QueryParams) ctx.args.get(IQueryParametersKeys.QUERY_PARAMETERS);
      List<ODocument> apps=null;
      String ret="{[]}";
      try{
          apps = com.baasbox.service.storage.AppService.getApps(criteria);
      }catch (SqlInjectionException e ){
          return badRequest("The request is malformed: check your query criteria");
      }catch(InvalidCollectionException e){
          return badRequest("The InvalidCollectionException exception: what's wrong? not know yet!");
      }
      try{
          ret= OJSONWriter.listToJSON(apps, JSONFormats.Formats.USER.toString());
      }catch (Throwable e){
          return internalServerError(ExceptionUtils.getFullStackTrace(e));
      }
      if (Logger.isTraceEnabled()) Logger.trace("Method End");
      response().setContentType("application/json");
      return ok(ret);

  }

    /* create app */
    @With (ConnectToDBFilter.class)
  public static Result addApp(){

          if (Logger.isTraceEnabled()) Logger.trace("Method Start");
          Http.RequestBody body = request().body();

          JsonNode bodyJson = body.asJson();
          if (Logger.isDebugEnabled()) Logger.debug("signUp bodyJson: " + bodyJson);

          //check and validate input
          if (!bodyJson.has("appname"))
              return badRequest("The 'appname' field is missing");

          //extract fields
          String appname=(String) bodyJson.findValuesAsText("appname").get(0);

          //try to creat new app
          try {
              com.baasbox.service.storage.AppService.create(appname);
          }catch(InvalidParameterException e){
              return badRequest(e.getMessage());
          }catch (OSerializationException e){
              return badRequest("Body is not a valid JSON: ");
          }catch (Exception e) {
              Logger.error(ExceptionUtils.getFullStackTrace(e));
              throw new RuntimeException(e) ;
          }catch(Throwable e){

          }
          if (Logger.isTraceEnabled()) Logger.trace("Method End");
          return created();

      }//createApp

}