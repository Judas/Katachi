package com.judas.katachi.core.api;

import com.google.gson.Gson;
import com.judas.katachi.BuildConfig;
import com.judas.katachi.core.api.data.GameListResponse;
import com.judas.katachi.core.api.data.GameResponse;
import com.judas.katachi.core.api.data.Go4GoGame;
import com.judas.katachi.core.api.interceptors.UserAgentInterceptor;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;

import static com.judas.katachi.BuildConfig.APPLICATION_ID;
import static com.judas.katachi.BuildConfig.BUILD_TYPE;
import static com.judas.katachi.BuildConfig.GO4GO_ENDPOINT;
import static com.judas.katachi.BuildConfig.VERSION_NAME;
import static com.judas.katachi.utils.log.Logger.Level.DEBUG;
import static com.judas.katachi.utils.log.Logger.log;

public class Go4Go {
    private final static String TAG = Go4Go.class.getSimpleName();

    public interface Api {
        @GET(GO4GO_ENDPOINT)
        Single<GameListResponse> getNewGames();

        @GET("200/0/{id}")
        Single<GameResponse> getGame(@Path("id") String id);
    }

    private static Api api;

    public static Api go4GoApi() {
        if (api == null) {
            final HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.level(BuildConfig.DEBUG ? HttpLoggingInterceptor.Level.BODY : HttpLoggingInterceptor.Level.NONE);

            final OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();
            clientBuilder
                    .addInterceptor(new UserAgentInterceptor(buildUserAgent()))
                    .addInterceptor(loggingInterceptor);

            api = new Retrofit.Builder()
                    .baseUrl(GO4GO_ENDPOINT)
                    .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create(new Gson()))
                    .client(clientBuilder.build())
                    .build()
                    .create(Api.class);
        }
        return api;
    }

    private static String buildUserAgent() {
        return "android-" + APPLICATION_ID + "-" + VERSION_NAME + "-" + BUILD_TYPE;
    }

    // =============================================================================================

    public static Observable<Go4GoGame> recentProGames() {
        log(DEBUG, TAG, "recentProGames");
        return go4GoApi()
                .getNewGames()
                .map(gameListResponse -> gameListResponse.games)
                .flattenAsObservable(games -> games);
    }

    public static Single<String> sgfFor(final Go4GoGame game) {
        log(DEBUG, TAG, "sgfFor");
        return go4GoApi()
                .getGame(game.id)
                .map(gameListResponse -> gameListResponse.sgf)
                .map(sgf -> {
                    while (!sgf.startsWith(";B[") && !sgf.startsWith(";W[")) {
                        sgf = sgf.substring(1);
                    }
                    return sgf;
                });
    }
}
