package com.sungbin.gitkakaobot.module

import com.sungbin.androidutils.util.DataUtil
import com.sungbin.androidutils.util.Logger
import com.sungbin.gitkakaobot.GitMessengerBot
import com.sungbin.gitkakaobot.util.manager.PathManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton


/**
 * Created by SungBin on 2020-12-26.
 */

@Module
@InstallIn(ApplicationComponent::class)
class GithubClient {

    private class AuthInterceptor : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            val token = DataUtil.readData(
                GitMessengerBot.context,
                PathManager.TOKEN,
                ""
            )
            Logger.w("github token", token)
            val builder = chain.request().newBuilder()
                .addHeader("Authorization", "token 1f08ab309166e6d6df8d785fb095eeed6c8a59e5")
            return chain.proceed(builder.build())
        }
    }

    private val provideLoggingInterceptor: HttpLoggingInterceptor
        get() {
            val interceptor = HttpLoggingInterceptor()
            interceptor.level = HttpLoggingInterceptor.Level.BODY
            return interceptor
        }

    private fun getInterceptor(vararg interceptors: Interceptor): OkHttpClient {
        val builder = OkHttpClient.Builder()
        for (interceptor in interceptors) builder.addInterceptor(interceptor)
        return builder.build()
    }

    @Provides
    @Singleton
    @Named("Api")
    fun apiInstance() = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
        .client(getInterceptor(provideLoggingInterceptor, AuthInterceptor()))
        .baseUrl("https://api.github.com/")
        .build()

    @Provides
    @Singleton
    @Named("Auth")
    fun authInstance() = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
        .client(getInterceptor(provideLoggingInterceptor))
        .baseUrl("https://github.com/")
        .build()
}