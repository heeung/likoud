package com.ssafy.likloud.ui.game

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.likloud.data.api.onSuccess
import com.ssafy.likloud.data.model.QuestionDto
import com.ssafy.likloud.data.model.response.MemberInfoResponse
import com.ssafy.likloud.data.repository.BaseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class GameFragmentViewModel @Inject constructor(
    private val baseRepository: BaseRepository
) : ViewModel() {

    /////////////////// 이미지 크기 ///////////////////////
    private var _initialFrameWidth = MutableLiveData<Int>()
    val initialFrameWidth: LiveData<Int>
        get() = _initialFrameWidth
    private var _initialFrameHeight = MutableLiveData<Int>()
    val initialFrameHeight: LiveData<Int>
        get() = _initialFrameHeight
    fun setInitialFrameSize(width: Int, height: Int){
        _initialFrameWidth.value = width
        _initialFrameHeight.value = height
    }

    private var _frameWidth = MutableLiveData<Int>()
    val frameWidth: LiveData<Int>
        get() = _frameWidth
    fun setFrameWidth(){
        _frameWidth.value = _initialFrameWidth.value
    }
    fun increaseFrameWidth(width: Int){
        _frameWidth.value = _frameWidth.value!! + width
    }
    fun decreaseFrameWidth(width: Int){
        _frameWidth.value = _frameWidth.value!! - width
    }

    private var _frameHeight = MutableLiveData<Int>()
    val frameHeight: LiveData<Int>
        get() = _frameHeight
    fun setFrameHeight(){
        _frameHeight.value = _initialFrameHeight.value
    }
    fun increaseFrameHeight(height: Int){
        _frameHeight.value = _frameHeight.value!! + height
    }
    fun decreaseFrameHeight(height: Int){
        _frameHeight.value = _frameHeight.value!! - height
    }

    ////////////////////////// 시간 PROGRESS BAR //////////////////////////////////
    private var _remainTime = MutableLiveData<Int>(30)
    val remainTime: LiveData<Int>
        get() = _remainTime
    fun initRemainTime(){
        _remainTime.value = 30
    }
    fun decreaseRemainTime(){
        _remainTime.value = _remainTime.value!! - 1
    }

    ///////////////////////////// 문제 랜덤 리스트 /////////////////////////////////
//    var randomQuestionIdxList = List(30) { Random.nextInt(30) }
    var randomQuestionIdxList = (0 until 30).shuffled().subList(0,30)
//    val randomQuestionIdxList: LiveData<List<Int>>
//        get() = _randomQuestionIdxList

    private var _currentQuestionIdx = MutableLiveData<Int>(0)
    val currentQuestionIdx: LiveData<Int>
        get() = _currentQuestionIdx
    fun increaseCurrentQuestionIdx(){
        _currentQuestionIdx.value = _currentQuestionIdx.value!! + 1
    }


    //////////////////////////// 정답 체크 ///////////////////////////////////
    fun checkAnswer(pick: String){
        if(QuestionLIist.questionList[randomQuestionIdxList[currentQuestionIdx.value!!]].answer == pick){
            increaseFrameWidth(20)
            increaseFrameHeight(20)
        }else{
            decreaseFrameWidth(20)
            decreaseFrameHeight(20)
        }
        increaseCurrentQuestionIdx()
    }


    ///////////////////////////// 홈으로 갈 지 다시 할 지 /////////////////////////////////////
    private var _nextFragment = MutableLiveData<String>()
    val nextFragment: LiveData<String>
        get() = _nextFragment
    fun changeNextFragment(value: String){
        _nextFragment.value = value
    }

    var successGameDialog = SuccessGameDialog()
    var failGameDialog = FailGameDialog()

}