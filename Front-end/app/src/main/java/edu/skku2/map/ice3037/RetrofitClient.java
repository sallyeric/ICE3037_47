//package edu.skku2.map.ice3037;
//
//import com.google.gson.Gson;
//import com.google.gson.GsonBuilder;
//
//import retrofit2.Retrofit;
//import retrofit2.converter.gson.GsonConverterFactory;
//
//public class RetrofitClient {
//
//    private static final String BASE_URL = "http://2f77adfb7db7.ngrok.io/"; //이 url은 서버컴을 재부팅하면 초기화해야 합니다...
//
//    public static RetrofitAPI getApiService(){return getInstance().create(RetrofitAPI.class);}
//
//    private static Retrofit getInstance(){
//        Gson gson = new GsonBuilder().setLenient().create();
//        return new Retrofit.Builder()
//                .baseUrl(BASE_URL)
//                .addConverterFactory(GsonConverterFactory.create(gson))
//                .build();
//    }
//}