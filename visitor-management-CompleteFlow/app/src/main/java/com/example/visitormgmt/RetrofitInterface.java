package com.example.visitormgmt;

import com.google.gson.JsonObject;

import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface RetrofitInterface {


        String BASEURL = "http://192.168.100.187:3001/";
        String pnr = "asdsdf";

        @Headers({ "Content-Type: application/json;charset=UTF-8"})
        @GET("visitors")
        Call<String> getString(@Header("Authorization") String auth);


       /* @Headers({ "Content-Type: application/json;charset=UTF-8"})
        @FormUrlEncoded
        @POST("visitors")
        Call<String>  getPostData(
                @Field("FirstName") String FirstName,
                @Field("LastName") String LastName,
                @Field("email") String email,
                @Field("Address") String Address,
                @Field("organization") String organization
        );*/
       @Multipart
       @POST("upload")
       Call<Object> uploadImagePost(
               @Header("Authorization") String Authorization,
               @Part MultipartBody.Part file,
               @Part("ref") RequestBody ref,
               @Part("refId") RequestBody refId,
               @Part("field") RequestBody Photo
       );

    @Headers({"Content-Type: application/json;charset=Utf-8"})
    @POST("insertvisitordetails")
    Call<Object> createPost(@Body JsonObject fields);

    @Headers({"Content-Type: application/json;charset=Utf-8"})
    @POST("visitorlogs")
    Call<Post> createPostVisitInfo( @Header("Authorization") String Authorization, @Body JsonObject fields);


    @GET("pnrstatus/{pnr}")
    Call<String> getPNRDetails(@Path("pnr") String pnr);

    @PUT("checkout/{pnr}")
    Call<checkOutStatus> createCheckOut(@Path("pnr") String pnr);

    @PUT("checkoutmobile/{contactno}")
    Call<checkOutStatus> createCheckOut_mobileNumber(@Path("contactno") String contactno);

   /* @Headers({"Content-Type: application/json;charset=Utf-8"})
    @FormUrlEncoded
    @POST("updatevisitorsdetails")
    Call<Object> updateExisting(@Field("status") String status,
                                @Field("name") String fname,
                                @Field("contactno") String contact,
                                @Field("organization") String company,
                                @Field("purpose") String Purpose,
                                @Field("contactPerson") String ContactPerson,
                                @Field("emailId") String EmailId,
                                @Field("visitorpassPic") String UserImage,
                                @Field("visitorpassIdProof") String UserIDImage);

*/
    @GET("contactstatus/{contactno}")
    Call<String> getVisitorDetails(@Path("contactno") String contactno);

    @Headers({"Content-Type: application/json;charset=Utf-8"})
    @POST("updatevisitorsdetails")
    Call<Object> updateExisting(@Body JsonObject fields);

}
