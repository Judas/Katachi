package com.judas.katachi.feature.api


import com.judas.katachi.BuildConfig
import com.judas.katachi.BuildConfig.GO4GO_ENDPOINT
import com.judas.katachi.feature.api.interceptor.UserAgentInterceptor
import com.judas.katachi.feature.api.model.ProGame
import com.judas.katachi.feature.api.model.ProGameDetail
import com.judas.katachi.feature.api.model.RecentProGames
import com.judas.katachi.utility.Logger.Level.DEBUG
import com.judas.katachi.utility.Logger.Level.ERROR
import com.judas.katachi.utility.log
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

class ProGamesApi {
    interface ProGamesRestApi {
        @GET(GO4GO_ENDPOINT)
        suspend fun recentProGames(): Response<RecentProGames>

        @GET("200/0/{id}")
        suspend fun proGame(@Path("id") id: String): Response<ProGameDetail>
    }

    private val apiClient = Retrofit.Builder()
        .baseUrl(GO4GO_ENDPOINT)
        .client(
            OkHttpClient.Builder()
                .addInterceptor(HttpLoggingInterceptor().apply {
                    level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY
                    else HttpLoggingInterceptor.Level.NONE
                })
                .addInterceptor(UserAgentInterceptor())
                .addInterceptor(HttpLoggingInterceptor().apply { setLevel(HttpLoggingInterceptor.Level.BODY) })
                .build()
        )
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(ProGamesRestApi::class.java)

    suspend fun recentProGames(): List<ProGame> = try {
        log(DEBUG, "recentProGames")

        val response = apiClient.recentProGames()
        if (response.isSuccessful) {
            log(DEBUG, "Fetched recent pro games")
            response.body()?.games ?: run {
                log(ERROR, "Error parsing pro games")
                listOf()
            }
        } else {
            log(ERROR, "Error fetching pro games")
            listOf()
        }
    } catch (e: Exception) {
        log(ERROR, "Error during pro games API call")
        listOf()
    }

    suspend fun gameSgf(game: ProGame): String = try {
        log(DEBUG, "gameSgf ${game.gameId}")

        val response = apiClient.proGame(game.gameId)
        if (response.isSuccessful) {
            log(DEBUG, "Fetched game ${game.gameId}")
            response.body()?.sgf
                ?.replace("(;EV[]", game.enhancedHeader)
                ?: run {
                    log(ERROR, "Error parsing game ${game.gameId}")
                    ""
                }
        } else {
            log(ERROR, "Error fetching game ${game.gameId}")
            ""
        }
    } catch (e: Exception) {
        log(ERROR, "Error during game API call")
        ""
    }
}
