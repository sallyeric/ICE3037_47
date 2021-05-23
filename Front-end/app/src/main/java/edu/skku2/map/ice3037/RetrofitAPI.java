package edu.skku2.map.ice3037;

import org.json.JSONArray;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface RetrofitAPI {

    @FormUrlEncoded
    @POST("/signup")
    Call<Post> signUp(@Field("userId") String userId,
                      @Field("password") String password,
                      @Field("creonAccount") String creonAccount);

    @FormUrlEncoded
    @POST("/login")
    Call<Post> login(@Field("userId") String userId,
                     @Field("password") String password);

    @FormUrlEncoded
    @POST("/home")
    Call<Post> home(@Field("userId") String userId);

    @FormUrlEncoded
    @POST("/info")
    Call<Post> info(@Field("companyName") String companyName);

    @FormUrlEncoded
    @POST("/onAutoTrade")
    Call<Post> OnAutoTrade(@Field("userId") String userId,
                           @Field("companyName") String companyName,
                           @Field("budgets") int budgets,
                           @Field("check1") Boolean check1,
                           @Field("check2") Boolean check2,
                           @Field("check3") Boolean check3);

    @FormUrlEncoded
    @POST("/offAutoTrade")
    Call<Post> OffAutoTrade(@Field("userId") String userId,
                           @Field("companyName") String companyName);

    @FormUrlEncoded
    @POST("/myInfo")
    Call<Post> myInfo(@Field("userId") String userId);

}