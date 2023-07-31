package com.ssafy.likloud.data.api

import com.ssafy.likloud.base.BaseResponse
import com.ssafy.likloud.data.model.MemberInfoDto
import com.ssafy.likloud.data.model.PhotoUploadResponseDto
import com.ssafy.likloud.data.model.request.LoginRequest
import com.ssafy.likloud.data.model.response.LoginResponse
import com.ssafy.likloud.data.model.response.ReLoginResponse
import com.ssafy.likloud.data.model.SampleDto
import com.ssafy.likloud.data.model.UserDto
import com.ssafy.likloud.data.model.request.LoginAdditionalRequest
import com.ssafy.likloud.data.model.request.MemberInfoRequest
import com.ssafy.likloud.data.model.response.MemberInfoResponse
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*


interface BaseService {

    //BaseResponse 객체로 return 하면 오류남.. 왜???
    @GET("posts/{id}")
    suspend fun getPosts(@Path("id") id: Int): Response<UserDto>

    @GET("api/example/{id}")
    suspend fun getComment(@Path("id") id: Int): BaseResponse<SampleDto>

    // JWT 토큰 재발급
//    @Headers()
    @POST("api/accounts/access-token/re")
    suspend fun postRefreshToken(@Header("Authorization") Authorization : String): Response<ReLoginResponse>

    // 회원가입 / (로그인)
    @POST("api/oauth/login")
    suspend fun postLogin(@Body body: LoginRequest): Response<LoginResponse>

    // 유저 정보를 받아온다.
    @GET("api/member/info")
    suspend fun getUserInfo(@Query("body") body: MemberInfoRequest): Response<MemberInfoResponse>

    // MEMBER로 올리고, newToken을 헤더에 담아 받아온다.
    @PATCH("api/member/additional")
    suspend fun patchAdditionalInfo(@Body body: LoginAdditionalRequest): Response<ReLoginResponse>

    @Multipart
    @POST("api/photo/upload")
    suspend fun postPhotoMultipart(
        @Part files: List<MultipartBody.Part>,
        @Part("memberInfoDto") memberInfoDto: MemberInfoDto
    ): Response<List<PhotoUploadResponseDto>>

}

//api 만드는 과정
//1. api service 작성 (BaseService 참고)
//2. api service를 사용하는 Repository Interface 생성 (BaseRepository 참고)
//3. repository Interface 구현체인 Impl 파일 생성 (BaseRepositoryImpl 참고)
//4. respository module에 내가 만든 repository 등록 (RepositoryModule 참고)

//5. 뷰모델에서 불러와 사용 (HomeFragmentViewModel 참고)