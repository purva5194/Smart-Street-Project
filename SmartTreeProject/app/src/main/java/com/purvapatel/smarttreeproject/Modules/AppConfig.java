package com.purvapatel.smarttreeproject.Modules;

/**
 * Created by purvapatel on 10/22/17.
 */

import java.util.HashMap;

import retrofit.Callback;
import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;

public class AppConfig {

    // insert comment info into database
    public interface addcomment {
        @POST("/userlist")
        void add_comment(
                @Body HashMap<String, Object> body,
                Callback<Response> callback);

    }

    // get comment info from database by username
    public interface getcomment {
        @GET("/userlist/{name}")
        void get_comment(
                @Path("name") String name,
                Callback<Response> callback);


    }

    // add user registration info into database
    public interface adduserprofile {
        @POST("/userprofile")
        void add_user_profile(
                @Body HashMap<String, Object> body,
                Callback<Response> callback);


    }


}
