package com.ssafy.likloud.data.api

import android.provider.ContactsContract.CommonDataKinds.Nickname
import com.google.firebase.database.core.Repo
import com.ssafy.likloud.base.BaseResponse
import com.ssafy.likloud.data.model.CommentDto
import com.ssafy.likloud.data.model.DrawingDetailDto
import com.ssafy.likloud.data.model.DrawingListDto
import com.ssafy.likloud.data.model.PhotoListDto
import com.ssafy.likloud.data.model.MemberInfoDto
import com.ssafy.likloud.data.model.MemberProfileDto
import com.ssafy.likloud.data.model.NftGiftDto
import com.ssafy.likloud.data.model.NftListDto
import com.ssafy.likloud.data.model.NftRegistDto
import com.ssafy.likloud.data.model.NftWalletDto
import com.ssafy.likloud.data.model.ReportDto
import com.ssafy.likloud.data.model.photo.PhotoUploadResponseDto
import com.ssafy.likloud.data.model.request.LoginRequest
import com.ssafy.likloud.data.model.response.LoginResponse
import com.ssafy.likloud.data.model.response.ReLoginResponse
import com.ssafy.likloud.data.model.SampleDto
import com.ssafy.likloud.data.model.UserDto
import com.ssafy.likloud.data.model.WalletInfo
import com.ssafy.likloud.data.model.drawing.DrawingUploadResponse
import com.ssafy.likloud.data.model.request.LoginAdditionalRequest
import com.ssafy.likloud.data.model.request.MemberInfoRequest
import com.ssafy.likloud.data.model.request.ProfileEditRequest
import com.ssafy.likloud.data.model.response.AccessoryResponse
import com.ssafy.likloud.data.model.response.MemberInfoResponse
import com.ssafy.likloud.data.model.response.StoreItemBuyResponse
import com.ssafy.likloud.data.model.response.StoreResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
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
    suspend fun postRefreshToken(@Header("Authorization") Authorization: String): Response<ReLoginResponse>

    // 회원가입 / (로그인)
    @POST("api/oauth/login")
    suspend fun postLogin(@Body body: LoginRequest): Response<LoginResponse>

    // 로그아웃
    @POST("api/oauth/logout")
    suspend fun postLogout(): Response<String>

    // 유저 정보를 받아온다.
    @GET("api/mypage/home")
    suspend fun getMemberInfo(@Query("body") body: MemberInfoRequest): Response<MemberInfoResponse>

    // MEMBER로 올리고, newToken을 헤더에 담아 받아온다.
    @PATCH("api/member/additional")
    suspend fun patchAdditionalInfo(@Body body: LoginAdditionalRequest): Response<ReLoginResponse>

    /**
     * 그림 게시물 조회
     * orderBy에 "?orderBy=likesCount"(좋아요 순), "?orderBy=viewCount"(조회수 순)
     */
    @GET("api/drawings/")
    suspend fun getDrawingList(@Query("orderBy") orderBy: String): Response<MutableList<DrawingListDto>>

    /**
     * 그림 상세 조회 함수(인자로 DrawingListDto의 _id값 넣으면 됨)
     */
    @GET("api/drawings/{drawingId}")
    suspend fun getDrawingDetail(@Path("drawingId") drawingId: Int): Response<DrawingDetailDto>


    /**
     * 사진을 업로드 합니다.
      */
    @Multipart
    @POST("api/photo/upload")
    suspend fun postPhotoMultipart(
        @Part multipartFiles: List<MultipartBody.Part>,
        @Part ("memberInfoDto") memberInfoDto: MemberInfoDto
    ): Response<List<PhotoUploadResponseDto>>

    /**
     * 단일 사진을 업로드 합니다.
     */
    @Multipart
    @POST("api/photo/upload")
    suspend fun postSinglePhotoMultipart(
        @Part multipartFiles: MultipartBody.Part,
        @Part("memberInfoDto") memberInfoDto: MemberInfoDto
    ): Response<List<PhotoUploadResponseDto>>

    @Multipart
    @POST("/api/drawings/upload/from/{photoId}")
    suspend fun postDrawingMultipart(
        @Path("photoId") photoId: Int,
        @Part file : MultipartBody.Part,
        @Part("title") title : RequestBody,
        @Part("content") content : RequestBody,
        @Part("memberInfoDto") memberInfoDto: MemberInfoDto
    ): Response<DrawingUploadResponse>

    /**
     * 사진 게시물 조회
     * parameter로 new(최신순)-기본 / pick(그린 수 순) / bookmarkdesc(즐찾순)
     */
    @GET("api/photo/{orderBy}")
    suspend fun getPhotoList(@Path("orderBy") orderBy: String): Response<MutableList<PhotoListDto>>

    /**
     * 특정 멤버 프로필 조회
     */
    @GET("api/member/search/{memberId}")
    suspend fun getMemberProfile(@Path("memberId") memberId: Int): Response<MemberProfileDto>

    /**
     * 해당 사진에 대한 그림 목록 조회
     */
    @GET("api/photo/{photoId}/alldrawings")
    suspend fun getPhotoDrawingList(@Path("photoId") photoId: Int): Response<MutableList<DrawingListDto>>

    /**
     * 내가 보유하고 있는 악세서리 목록 조회
     */
    @GET("api/mypage/accessory")
    suspend fun getMyAccessoryList(@Query("body") body: MemberInfoRequest): Response<MutableList<AccessoryResponse>>

    /**
     * 프로필 수정
     */
    @PUT("api/mypage/profile")
    suspend fun editMyProfile(@Body body: ProfileEditRequest): Response<MemberInfoResponse>

    /**
     * 게임 성공 시 은코인 상승
     */
    @POST("api/member/plusSilver")
    suspend fun plusSliver(): Response<String>
    /**
     * 내가 그린 그림 조회(마이페이지)
     */
    @GET("api/mypage/drawings")
    suspend fun getMyDrawingListDtoList(): Response<MutableList<DrawingListDto>>
    /**
     * 내가 좋아요 한 그림 조회(마이페이지)
     */
    @GET("api/mypage/likes")
    suspend fun getLikeDrawingListDtoList(): Response<MutableList<DrawingListDto>>
    /**
     * 내가 올린 사진 조회(마이페이지)
     */
    @GET("api/mypage/photos")
    suspend fun getMyPhotoListDtoList(): Response<MutableList<PhotoListDto>>

    /**
     * 상점 페이지 들어갈 때 조회
     */
    @GET("api/store/home")
    suspend fun getStoreInfo(@Query("memberInfo") memberInfo: MemberInfoRequest): Response<StoreResponse>

    /**
     * 악세서리 구매
     */
    @POST("api/store/buy/{storeId}")
    suspend fun postBuyAccessory(@Query("storeId") storeId: Int, @Body body: MemberInfoRequest): Response<StoreItemBuyResponse>

    /**
     * 내가 즐찾한 사진 조회(마이페이지)
     */
    @GET("api/mypage/bookmarks")
    suspend fun getBookmarkPhotoListDtoList(): Response<MutableList<PhotoListDto>>
    /**
     * 사진 상세 조회
     */
    @GET("api/photo/{photoId}")
    suspend fun getCurrentPhotoListDto(@Path("photoId") photoId: Int): Response<PhotoListDto>
    /**
     * 그림 좋아요
     */
    @POST("api/drawings/{drawingId}/likes")
    suspend fun changeDrawingLike(@Path("drawingId") drawingId: Int): Response<String>
    /**
     * 사진 즐찾
     */
    @POST("api/photo/{photoId}/bookmark")
    suspend fun changePhotoBookmark(@Path("photoId") photoId: Int): Response<String>
    /**
     * 댓글 등록
     */
    @POST("api/comment/to/{drawingId}")
    suspend fun registDrawingComment(@Path("drawingId") drawingId: Int, @Query("content") content: String): Response<CommentDto>
    /**
     * 댓글 삭제
     */
    @DELETE("api/comment/delete/{commentId}")
    suspend fun deleteDrawingComment(@Path("commentId") commentId: Int): Response<String>
    /**
     * 내 NFT 조회
     */
    @GET("api/mypage/nft")
    suspend fun getMyNftList(): Response<MutableList<NftListDto>>
    /**
     * NFT 발급
     */
    @POST("api/nft/token/{drawingId}")
    suspend fun registNft(@Path("drawingId") drawingId: Int): Response<NftRegistDto>
    /**
     * NFT 선물함 조회
     */
    @GET("api/mypage/gift")
    suspend fun getNftGiftList(): Response<MutableList<NftGiftDto>>
    /**
     * 닉네임으로 유저 검색
     */
    @POST("api/member/search/{nickname}")
    suspend fun getCurrentSearchUserList(@Path("nickname") nickname: String): Response<MutableList<MemberInfoResponse>>
    /**
     * nft 선물하기
     */
    @POST("api/nft/token/{nftId}/to/{toMemberId}")
    suspend fun sendGift(@Path("nftId") nftId: Int, @Path("toMemberId") toMemberId: Int, @Query("message") message: String): Response<NftGiftDto>
    /**
     * 회원 정보 조회
     */
    @GET("api/member/info")
    suspend fun getMemgerInfo2(): Response<MemberInfoResponse>
    /**
     * NFT 지갑 발급
     */
    @POST("api/nft/wallet")
    suspend fun getNftWallet(): Response<NftWalletDto>
    /**
     * 선물 수락
     */
    @POST("api/mypage/gift/{transferId}/accept/{nftId}")
    suspend fun acceptGift(@Path("transferId") transferId: Int, @Path("nftId") nftId: Int): Response<NftListDto>
    /**
     * 선물 거부
     */
    @POST("api/mypage/gift/{transferId}/reject/{nftId}")
    suspend fun rejectGift(@Path("transferId") transferId: Int, @Path("nftId") nftId: Int): Response<String>
    /**
     * 게시글 신고
     */
    @POST("api/report/{drawingId}")
    suspend fun sendReport(@Path("drawingId") drawingId: Int, @Query("content") content: String): Response<ReportDto>
    /**
     * 닉네임 변경
     */
    @PUT("api/mypage/nickname")
    suspend fun editNickname(@Query("nickname") nickname: String) : Response<MemberInfoResponse>
    /**
     * 특정 멤버 지갑 조회
     */
    @GET("api/nft/wallet/{memberId}")
    suspend fun getMemberNftWallet(@Path("memberId") memberId: Int): Response<WalletInfo>
    /**
     * 특정 그림 삭제
     */
    @DELETE("api/drawings/delete/{drawingId}")
    suspend fun deleteDrawing(@Path("drawingId") drawingId: Int): Response<String>
}

//api 만드는 과정
//1. api service 작성 (BaseService 참고)S
//2. api service를 사용하는 Repository Interface 생성 (BaseRepository 참고)
//3. repository Interface 구현체인 Impl 파일 생성 (BaseRepositoryImpl 참고)
//4. respository module에 내가 만든 repository 등록 (RepositoryModule 참고)

//5. 뷰모델에서 불러와 사용 (HomeFragmentViewModel 참고)