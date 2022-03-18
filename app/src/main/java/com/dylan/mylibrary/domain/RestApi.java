package com.dylan.mylibrary.domain;


import com.dylan.mylibrary.MyApplication;
import com.dylan.mylibrary.domain.conventer.GsonConverterFactory;
import com.dylan.mylibrary.domain.conventer.ScalarsConverterFactory;

import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;


/**
 * Created by IvanCai on 2016/2/21.
 */
public interface RestApi {

    String BASE_URL = "http://test.dankal.cn/";
    String BASE_URL2="http://120.24.162.102/";
    //String BASE_URL="http://120.25.66.55/";
    String BASE_QINIU = "";

    //获取七牛云的域名
    @GET("/video_web/public/index.php/api/User/BucketDomain")
    Call<String> qiniuDomain();

    //获取七牛云的token
    @GET("/video_web/public/index.php/api/User/qiniu")
    Call<String> qiniUpLoadToken();

    //首页
    @GET("/video_web/public/index.php/api/Dmax/todayHit")
    Call<String> home3DTodayhot();

    @GET("/video_web/public/index.php/api/Dmax/original")
    Call<String> home3DOriginal();

    @GET("/video_web/public/index.php/api/Dmax/hitVideo")
    Call<String> home3DHotMovice();

    @GET("/video_web/public/index.php/api/fictitious/todayhit")
    Call<String> homeVitureTodayHot();

    @GET("/video_web/public/index.php/api/Fictitious/original")
    Call<String> homeVitureOriginal();

    @GET("/video_web/public/index.php/api/Fictitious/hitVideo")
    Call<String> homeVitureHotMovice();

    @GET("/video_web/public/index.php/api/panorama/todayHit")
    Call<String> homePanoramaTodayHot();

    @GET("/video_web/public/index.php/api/Panorama/original")
    Call<String> homePanoramOriginal();

    @GET("/video_web/public/index.php/api/panorama/hitVideo")
    Call<String> homePanoramaHotMovice();

    //首页轮播图
    @GET("/video_web/public/index.php/api/Carousel/fictitious")
    Call<String> loadHomeCarsousel();


    //获取VR原创导航栏数据
    @GET("/video_web/public/index.php/api/Navigation/vrNavigation")
    Call<String> loadVRNavigateBarData();

    @FormUrlEncoded
    @POST("/video_web/public/index.php/api/Original/classify")
    Call<String> loadVRVideo(@Field("tag_id") int tag_id, @Field("page") int page);

    @GET("/video_web/public/index.php/api/Carousel/vroriginal")
    Call<String> loadVRCarousel();

    //推荐
    @FormUrlEncoded
    @POST("/video_web/public/index.php/api/Original/recommend")
    Call<String> loadVRrecommend(@Field("page") int page);

    //搜索
    @FormUrlEncoded
    @POST("/video_web/public/index.php/api/Search/search")
    Call<String> search(@Field("name") String name);

    //我的页面顶部个人信息
    @FormUrlEncoded
    @POST("video_web/public/index.php/api/User/userInfo")
    Call<String> loaduserinfo(@Field("user_id") int userid, @Field("token") String token);


    class Factory {
        public static final int STRING_CONVERTER = 0;
        public static final int GSON_CONVERTER = 1;
        public static final int DEF_CONVERTER = 3;


        public static RestApi getInstance(int converterType) {
            return getRetrofit(converterType).create(RestApi.class);
        }

        private static Retrofit getRetrofit(int converterType) {
            OkHttpClient.Builder builder = new OkHttpClient().newBuilder()
                    .connectTimeout(30, TimeUnit.SECONDS)//设置超时时间
                    .readTimeout(10, TimeUnit.SECONDS)//设置读取超时时间
                    .writeTimeout(10, TimeUnit.SECONDS);//设置写入超时时间
            int cacheSize = 10 * 1024 * 1024; // 10 MiB
            Cache cache = new Cache(MyApplication.getApplication().getCacheDir(), cacheSize);

            builder.cache(cache);
//            builder.addInterceptor(interceptor);
            OkHttpClient mOkHttpClient = builder.build();

            if (converterType == STRING_CONVERTER)
                return new Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(ScalarsConverterFactory.create()).client(mOkHttpClient).build();
            else if (converterType == GSON_CONVERTER)
                return new Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create()).client(mOkHttpClient).build();
            return new Retrofit.Builder().baseUrl(BASE_URL).build();
        }
    }
}
