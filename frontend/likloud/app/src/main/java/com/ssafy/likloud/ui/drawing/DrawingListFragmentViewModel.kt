package com.ssafy.likloud.ui.drawing

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.likloud.data.api.onSuccess
import com.ssafy.likloud.data.model.CommentDto
import com.ssafy.likloud.data.model.DrawingDetailDto
import com.ssafy.likloud.data.model.DrawingListDto
import com.ssafy.likloud.data.model.MemberProfileDto
import com.ssafy.likloud.data.repository.BaseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "차선호"
@HiltViewModel
class DrawingListFragmentViewModel @Inject constructor(
    private val baseRepository: BaseRepository
) : ViewModel() {


    private var _currentDrawingListDtoList = MutableLiveData<MutableList<DrawingListDto>>()
    val currentDrawingListDtoList: LiveData<MutableList<DrawingListDto>>
        get() = _currentDrawingListDtoList

    /**
     * 최신순(기본) 조회
     */
    fun getRecentOrderDrawingListDtoList(){
        // api 호출해서 _recentOrderDrawingDtoList에 넣어줘라
        viewModelScope.launch {
            baseRepository.getDrawingList("").onSuccess {
                Log.d(TAG, "getRecentOrderDrawingListDtoList 결과: $it")
                _currentDrawingListDtoList.value = it
            }
        }
    }
    /**
     * 인기순 조회
     */
    fun getRankingOrderDrawingListDtoList(){
        viewModelScope.launch {
            // api 호출해서 _rankingOrderDrawingDtoList에 넣어줘라
            baseRepository.getDrawingList("likesCount").onSuccess {
                Log.d(TAG, "getRankingOrderDrawingListDtoList 결과 : $it ")
                _currentDrawingListDtoList.value = it
            }
        }
    }

    /**
     * 조회순 조회
     */
    fun getViewOrderDrawingListDtoLit(){
        viewModelScope.launch {
            // api 호출해서 _rankingOrderDrawingDtoList에 넣어줘라
            baseRepository.getDrawingList("viewCount").onSuccess {
                _currentDrawingListDtoList.value = it
            }
        }
    }



    /////////////////////////////////////////////////////////   선택된 drawing  ////////////////////////////////////////////////////////////////////////

    private var _currentDrawingDetailDto = MutableLiveData<DrawingDetailDto>()
    val currentDrawingDetailDto: LiveData<DrawingDetailDto>
        get() = _currentDrawingDetailDto
    fun getSelectedDrawingDetailDto(dto: DrawingListDto){
        //여기서 api호출해서 받아라
        viewModelScope.launch {
            baseRepository.getDrawingDetail(dto.drawingId).onSuccess {
                _currentDrawingDetailDto.value = it
            }
        }
    }

    ///////////////////////////////////////////////////////// 선택된 그림의 member ////////////////////////////////////////////////////////////

    private var _currentDrawingMember = MutableLiveData<MemberProfileDto>()
    val currentDrawingMember: LiveData<MemberProfileDto>
        get() = _currentDrawingMember
    fun getCurrentDrawingMember(memberId: Int){
        //여기서 api호출해서 받아라
        viewModelScope.launch {
            baseRepository.getMemberProfile(memberId).onSuccess {
                _currentDrawingMember.value = it
            }
        }
    }

    ///////////////////////////////////////////////////// 좋아요 //////////////////////////////////
    private val _isLiked = MutableLiveData<Boolean>()
    val isLiked: LiveData<Boolean>
        get() = _isLiked
    fun setIsLiked(){
        _isLiked.value = _currentDrawingDetailDto.value!!.memberLiked
    }
    fun changeIsLiked(){
        // api 호출
        viewModelScope.launch {
            baseRepository.changeDrawingLike(_currentDrawingDetailDto.value!!.drawingId)
            _isLiked.value = !_isLiked.value!!
        }
    }

    private val _likeCount = MutableLiveData<Int>()
    val likeCount: LiveData<Int>
        get() = _likeCount
    fun setLikeCount(){
//        _likeCount.value = _currentDrawingDetailDto.value!!.likesCount
        if(_isLiked.value!!){
            _likeCount.value = _currentDrawingDetailDto.value!!.likesCount + 1
        }else{
            _likeCount.value = _currentDrawingDetailDto.value!!.likesCount
        }
    }
    fun changeLikeCount(){
        if(_isLiked.value!!){
            _likeCount.value = _currentDrawingDetailDto.value!!.likesCount + 1
        }else{
            _likeCount.value = _currentDrawingDetailDto.value!!.likesCount
        }
    }



    /////////////////////////////////////////////////////////   댓글  ////////////////////////////////////////////////////////////////////////

    private val _selectedDrawingCommentList = MutableLiveData<MutableList<CommentDto>>()
    val selectedDrawingCommentList: LiveData<MutableList<CommentDto>>
        get() = _selectedDrawingCommentList
    fun changeSelectedDrawingCommentList(list: MutableList<CommentDto>){
        _selectedDrawingCommentList.value = list
    }
//    fun addToCommentList(comment: CommentDto) {
//        viewModelScope.launch {
//            selectedDrawingCommentList.value?.add(comment)
//        }
//    }
//
//    fun removeComment(posi:Int){
//        viewModelScope.launch {
//            selectedDrawingCommentList.value!!.removeAt(posi)
//        }
//    }
}