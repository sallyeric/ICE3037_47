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
    Call<PostSignUp> signUp(@Field("userId") String userId,
                            @Field("password") String password,
                            @Field("creonAccount") String creonAccount);

    @FormUrlEncoded
    @POST("/login")
    Call<PostSignUp> login(@Field("userId") String userId,
                           @Field("password") String password);
}