package com.ssafy.likloud.config

import android.util.Log
import com.google.gson.Gson
import com.navercorp.nid.oauth.NidOAuthPreferencesManager.accessToken
import com.ssafy.likloud.ApplicationClass.Companion.X_REFRESH_TOKEN
import com.ssafy.likloud.ApplicationClass.Companion.sharedPreferences
import com.ssafy.likloud.data.api.ApiClient
import com.ssafy.likloud.data.api.ApiClient.BASE_URL
import com.ssafy.likloud.data.api.BaseService
import com.ssafy.likloud.data.model.response.ErrorResponse
import com.ssafy.likloud.data.model.response.MemberInfoResponse
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.ResponseBody
import okhttp3.internal.http2.ErrorCode
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException


/**
 * 서버에 요청할 때 accessToken유효한지 검사
 * 유효하지 않다면 재발급 api 호출
 * refreshToken이 유효하다면 정상적으로 accessToken재발급 후 기존 api 동작 완료
*/
private const val TAG = "ResponseInterceptor_싸피"
class ResponseInterceptor: Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val response = chain.proceed(request)

        var accessToken = ""
        var isRefreshable = false

        Log.d(TAG, "intercept BearerInterceptor: ${sharedPreferences.getString(X_REFRESH_TOKEN)}")
        Log.d(TAG, "intercept: 지금 메시지!!!!!!@!@!@!@!@!@! ${response.message}")
        Log.d(TAG, "intercept: 지금 코드!!!!!!@!@!@!@!@!@! ${response.code}")
        Log.d(TAG, "intercept: 지금 객체!!!!!!@!@!@!@!@!@! ${response}")
        Log.d(TAG, "intercept: 지금 바디!!!!!!@!@!@!@!@!@! ${response.body}")
        Log.d(TAG, "intercept: 지금 네트워크 리스폰스!!!!!!@!@!@!@!@!@! ${response.networkResponse}")
//        val testResponse = parseResponseTest(response.body)
//        Log.d(TAG, "intercept: 떠라떠라떠라@!!!!!!!!!!!!!!!!!!!@!@! ${testResponse}")

        when (response.code) {
            400 -> {

            }

            401 -> { // 여러 에러들 종합 (에러 메시지로 확인하자.)
                val errorResponse = parseErrorResponse(response.body)
                Log.d(TAG, "intercept: 에러 바디 파싱 !!!!!!!!!! ${errorResponse}")

                // 에러 코드로 분기
                when (errorResponse.errorCode) {
                    "Auth-001" -> { // 엑세스 토큰 만료 신호
                        Log.d(TAG, "intercept: 에러(Auth-001) : 만료된 토큰입니다.")
                        runBlocking {
                            //토큰 갱신 api 호출

                            Log.d(TAG, "intercept: ${sharedPreferences.getString(X_REFRESH_TOKEN)}")
                            sharedPreferences.getString(X_REFRESH_TOKEN)?.let {
                                Log.d(TAG, "intercept: ${sharedPreferences.getString(X_REFRESH_TOKEN)}")

                                val result = Retrofit.Builder()
                                    .baseUrl(BASE_URL)
                                    .addConverterFactory(GsonConverterFactory.create())
                                    .build()
                                    .create(BaseService::class.java).postRefreshToken("Bearer ${it}")

                                Log.d(
                                    TAG, "intercept 현재 찐 refresh: ${
                                        sharedPreferences.getString(
                                            X_REFRESH_TOKEN
                                        )
                                    }"
                                )
                                if (result.isSuccessful) {
                                    Log.d(TAG, "intercept: 다시 받아오는데 성공했어요!!!!!!")
                                    sharedPreferences.putString("access_token", result.body()!!.accessToken)
                                    Log.d(TAG, "intercept: 만료된 토큰 다시 받은거 ${result.body()!!.accessToken}")
                                    accessToken = result.body()!!.accessToken
                                    isRefreshable = true
                                }
                                if (result.body() == null) {
                                    Log.d(TAG, "intercept: 리프레시 받아오는 코드 실패입니다.")
                                    Log.d(TAG, "intercept success : ${result.isSuccessful}")
                                    Log.d(TAG, "intercept  : ${result.code()}")
                                    Log.d(TAG, "intercept: ${result.headers()}")
                                    Log.d(TAG, "intercept: ${result.message()}")
                                    Log.d(TAG, "intercept: ${result.errorBody()}")
                                }
                            }
                        }
                    }
                    "Auth-004" -> { // 엑세스 토큰 invalid 신호
                        Log.d(TAG, "intercept: 에러(Auth-004) : 해당 토큰은 엑세스 토큰이 아닙니다.")
                    }
                }
            }

            403 -> {

            }

            404 -> {

            }

            500 -> { // 서버에러

            }
        }

//        if (response.code == 401) {
//            Log.d(TAG, "intercept code: ${response.code}")
//            Log.d(TAG, "intercept code: ${response.body}")
//            Log.d(TAG, "intercept: 토큰이 만료되어서 다시 보내용")
//
//                runBlocking {
//                //토큰 갱신 api 호출
//
//                Log.d(TAG, "intercept: ${sharedPreferences.getString(X_REFRESH_TOKEN)}")
//                sharedPreferences.getString(X_REFRESH_TOKEN)?.let {
//                    Log.d(TAG, "intercept: ${sharedPreferences.getString(X_REFRESH_TOKEN)}")
////                    sharedPreferences.putString(X_ACCESS_TOKEN, it)
////                    val newRequest = chain.request().newBuilder().addHeader("Authorization", "Bearer $it").build()
////                    chain.proceed(newRequest)
//
//
////                    Log.d(TAG, "intercept: new request ${newRequest.body}")
//
//                    val result = Retrofit.Builder()
//                        .baseUrl(BASE_URL)
//                        .addConverterFactory(GsonConverterFactory.create())
//                        .build()
//                        .create(BaseService::class.java).postRefreshToken("Bearer ${it}")
//
////                    val result = Retrofit.Builder()
////                        .baseUrl("https://jsonplaceholder.typicode.com/")
////                        .addConverterFactory(GsonConverterFactory.create())
////                        .build()
////                        .create(BaseService::class.java).getPosts(1)
////                    Log.d(TAG, "intercept: 저장되어 있는 리프레시 토큰 ${it}")
////                    Log.d(TAG, "intercept: ${result.body()}")
//                    Log.d(TAG, "intercept 현재 찐 refresh: ${sharedPreferences.getString(
//                        X_REFRESH_TOKEN)}")
//                    if(result.isSuccessful) {
//
//                        Log.d(TAG, "intercept: dddd")
//                        sharedPreferences.putString("access_token", result.body()!!.accessToken)
//                        Log.d(TAG, "intercept: 만료된 토큰 다시 받은거 ${result.body()!!.accessToken}")
////                        sharedPreferences.putString("refresh_token", result.data.refreshToken)
//                        accessToken = result.body()!!.accessToken
//                        isRefreshable = true
//                    }
//                    if (result.body() == null){
////                        Log.d(TAG, "intercept: 만료된 토큰 다시 받은거 에러!!! ${result.body()!!.accessToken}")
//                        Log.d(TAG, "intercept success : ${result.isSuccessful}")
//                        Log.d(TAG, "intercept  : ${result.code()}")
//                        Log.d(TAG, "intercept: ${result.headers()}")
//                        Log.d(TAG, "intercept: ${result.message()}")
//                        Log.d(TAG, "intercept: ${result.errorBody()}")
//                    }
//                }
//            }
//        }

        // 다시 내가 호출했었던 거 호출하는 로직 필요할듯?
        if(isRefreshable) {
            Log.d(TAG, "intercept: 리프레시가 알맞게 통신했고, 새 엑세스토큰으로 가능하다는 소리입니다~")
            val newRequest = chain.request().newBuilder().addHeader("Authorization", "Bearer $accessToken").build()
            return chain.proceed(newRequest)
        }

        return response
    }

    private fun parseErrorResponse(responseBody: ResponseBody?): ErrorResponse {
        val gson = Gson()
        return gson.fromJson(responseBody?.charStream(), ErrorResponse::class.java)
    }
}