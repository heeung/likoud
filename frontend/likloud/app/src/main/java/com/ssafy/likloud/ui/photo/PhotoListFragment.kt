package com.ssafy.likloud.ui.photo

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.OvershootInterpolator
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.jackandphantom.carouselrecyclerview.CarouselLayoutManager
import com.ssafy.likloud.MainActivity
import com.ssafy.likloud.MainActivityViewModel
import com.ssafy.likloud.R
import com.ssafy.likloud.base.BaseFragment
import com.ssafy.likloud.data.model.DrawingListDto
import com.ssafy.likloud.data.model.MemberProfileDto
import com.ssafy.likloud.data.model.PhotoListDto
import com.ssafy.likloud.databinding.FragmentPhotoListBinding
import com.ssafy.likloud.ui.drawing.DrawingListAdapter
import com.ssafy.likloud.ui.drawing.DrawingListFragmentDirections
import com.ssafy.likloud.ui.drawingpad.BitmapCanvasObject
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val TAG = "차선호"

@AndroidEntryPoint
class PhotoListFragment : BaseFragment<FragmentPhotoListBinding>(
    FragmentPhotoListBinding::bind,
    R.layout.fragment_photo_list
) {

    private val photoListFragmentViewModel: PhotoListFragmentViewModel by viewModels()
    private val activityViewModel: MainActivityViewModel by activityViewModels()
    private lateinit var photoListAdapter: PhotoListAdapter
    private lateinit var photoDrawingListAdapter: PhotoDrawingListAdapter
    private var isScrolling = false
    private var isCurrentOrder = 0


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return super.onCreateView(inflater, container, savedInstanceState)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()
        initListener()
    }

    override fun onResume() {
        super.onResume()
        initObserver()
    }


    private fun init() {
        when(isCurrentOrder){
            0 ->{
                photoListFragmentViewModel.getRecentOrderPhotoListDtoList()
                toggleButton(binding.buttonRecentOrder)
            }
            1->{
                photoListFragmentViewModel.getRankingOrderPhotoListDtoList()
                toggleButton(binding.buttonRankingOrder)
            }
            else ->{
                photoListFragmentViewModel.getBookmarkOrderPhotoListDtoList()
                toggleButton(binding.buttonBookmarkOrder)
            }
        }
        initRecyclerView()
        initPhotoDrawingListRecyclerView()
        loadingAnimation()
    }

    override fun initListener() {
        binding.apply {
            //최신순 눌렀을 때
            buttonRecentOrder.setOnClickListener {
                if (!isScrolling) {
                    photoListFragmentViewModel.getRecentOrderPhotoListDtoList()
                    initRecyclerView()
                    toggleButton(buttonRecentOrder)
                    loadingAnimation()
                    isCurrentOrder = 0
                }
            }
            //랭킹순 눌렀을 때
            buttonRankingOrder.setOnClickListener {
                if (!isScrolling) {
                    photoListFragmentViewModel.getRankingOrderPhotoListDtoList()
                    initRecyclerView()
                    toggleButton(buttonRankingOrder)
                    loadingAnimation()
                    isCurrentOrder = 1
                }
            }
            //즐찾순 눌렀을 때
            buttonBookmarkOrder.setOnClickListener {
                if (!isScrolling) {
                    photoListFragmentViewModel.getBookmarkOrderPhotoListDtoList()
                    initRecyclerView()
                    toggleButton(buttonBookmarkOrder)
                    loadingAnimation()
                    isCurrentOrder = 2
                }
            }
            //즐겨찾기(스타)를 눌렀을 때
            imageStar.setOnClickListener {
                photoListFragmentViewModel.changeBookmarkCount()
                photoListFragmentViewModel.changeIsBookmarked()
            }
            //뒤로가기 눌렀을 때
            buttonBack.setOnClickListener {
                findNavController().navigate(R.id.action_photoListFragment_to_homeFragment)
            }

            buttonPaint.setOnClickListener {
                findNavController().navigate(R.id.action_photoListFragment_to_drawingPadFragment)
                BitmapCanvasObject.clearAllDrawingPoints()
            }

            constraintPhotoOriginal.setOnClickListener {
                val animationX = ObjectAnimator.ofFloat(binding.imagePhotoOrigin, "scaleX", 1.0f, 0.0f)
                val animationY = ObjectAnimator.ofFloat(binding.imagePhotoOrigin, "scaleY", 1.0f, 0.0f)
                animationX.duration = 400
                animationY.duration = 400
                val animation = AnimatorSet()
                animation.playTogether(animationX, animationY)
                animation.start()
                animation.addListener(object : Animator.AnimatorListener {
                    override fun onAnimationStart(p0: Animator) {}
                    override fun onAnimationEnd(p0: Animator) {
                        it.visibility = View.GONE
                        layoutAppbar.setBackgroundResource(R.color.transparent)
                    }
                    override fun onAnimationCancel(p0: Animator) {}
                    override fun onAnimationRepeat(p0: Animator) {}
                })
            }
        }
        // 안드로이드 뒤로가기 버튼 눌렀을 때
        mActivity.onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    findNavController().navigate(R.id.action_photoListFragment_to_homeFragment)
                }
            }
        )
    }

    private fun initObserver() {

        photoListFragmentViewModel.currentPhotoListDtoList.observe(viewLifecycleOwner) {
            if (it.size != 0) {
                photoListAdapter.submitList(it)
                photoListFragmentViewModel.getCurrentPhotoListDto(it[0].photoId)
            }
        }

        photoListFragmentViewModel.currentPhotoListDto.observe(viewLifecycleOwner) {
            Log.d(TAG, "initObserver: observe")
            //여기서 현재 그림에 대한 사진 리스트, 사진 올린 멤버 조회 & 초기 bookmark 정보 세팅
            photoListFragmentViewModel.getCurrentPhotoMember(it.memberId)
            photoListFragmentViewModel.getCurrentPhotoDrawingList(it.photoId)
            photoListFragmentViewModel.setIsBookmarked()
            photoListFragmentViewModel.setBookmarkCount()
            activityViewModel.setUploadingPhotoUrl(it.photoUrl)
            activityViewModel.setUploadingPhotoId(it.photoId)
        }

        photoListFragmentViewModel.currentPhotoMember.observe(viewLifecycleOwner) {
            //사진 정보, 유저 정보 뷰 세팅
            initInfoView(photoListFragmentViewModel.currentPhotoListDto.value!!, it)
            isScrolling = false
        }

        photoListFragmentViewModel.currentPhotoDrawingList.observe(viewLifecycleOwner) {
            //현재 사진에 대한 그림들 리사이클러뷰 세팅
            photoDrawingListAdapter.submitList(it)
            if (it.size == 0) {
                binding.apply {
                    imageNoDrawing.visibility = View.VISIBLE
                    textNoDrawing.visibility = View.VISIBLE
                }
            } else {
                binding.apply {
                    imageNoDrawing.visibility = View.INVISIBLE
                    textNoDrawing.visibility = View.INVISIBLE
                }
            }
        }

        photoListFragmentViewModel.isBookmarked.observe(viewLifecycleOwner) {
            Log.d(TAG, "initObserverStar : $it ")
            if (it) {
                binding.imageStar.setImageResource(R.drawable.icon_selected_star)
            } else {
                binding.imageStar.setImageResource(R.drawable.icon_unselected_star)
            }
        }

        photoListFragmentViewModel.bookmarkCount.observe(viewLifecycleOwner) {
            binding.textBookmarkCount.text =
                photoListFragmentViewModel.bookmarkCount.value.toString()
        }
    }

    private fun initRecyclerView() {
        Log.d(
            TAG,
            "initRecyclerView list : ${photoListFragmentViewModel.currentPhotoListDtoList.value} "
        )
        photoListAdapter = PhotoListAdapter()
        binding.apply {
            recyclerviewDrawaing.apply {
                this.adapter = photoListAdapter.apply {
                    itemClickListner = object :
                        PhotoListAdapter.ItemClickListener {

                        override fun onClick(view: View, imageUrl: String) {
//                            val action =
//                                PhotoListFragmentDirections.actionPhotoListFragmentToDrawingOriginalFragment(imageUrl)
//                            findNavController().navigate(action)
                            Glide.with(binding.imagePhotoOrigin)
                                .load(imageUrl)
                                .apply(RequestOptions.bitmapTransform(RoundedCorners(50)))
                                .into(binding.imagePhotoOrigin)
                            binding.constraintPhotoOriginal.visibility = View.VISIBLE
                            binding.imagePhotoOrigin.visibility = View.VISIBLE
                            binding.imagePhotoOrigin.scaleX = 0.0f
                            binding.imagePhotoOrigin.scaleY = 0.0f
                            val animationX = ObjectAnimator.ofFloat(binding.imagePhotoOrigin, "scaleX", 0.0f, 1.0f)
                            val animationY = ObjectAnimator.ofFloat(binding.imagePhotoOrigin, "scaleY", 0.0f, 1.0f)
                            animationX.duration = 400
                            animationY.duration = 400
                            val animation = AnimatorSet()
                            animation.playTogether(animationX, animationY)
                            animation.start()
                            binding.layoutAppbar.setBackgroundResource(R.color.background_half_transparent)
                        }
                    }
                }
                set3DItem(true)
                setAlpha(true)
                setOrientation(RecyclerView.VERTICAL)
                setItemSelectListener(object : CarouselLayoutManager.OnSelected {
                    //본인한테서 멈췄을 때 이벤트
                    override fun onItemSelected(position: Int) {
                        photoListFragmentViewModel.getCurrentPhotoListDto(
                            photoListFragmentViewModel.currentPhotoListDtoList.value!![position].photoId
                        )
                    }
                })
                addOnScrollListener(object : RecyclerView.OnScrollListener() {
                    override fun onScrollStateChanged(
                        recyclerView: RecyclerView,
                        newState: Int
                    ) {
                        super.onScrollStateChanged(recyclerView, newState)
                        if (newState != RecyclerView.SCROLL_STATE_IDLE) {
                            isScrolling = true
                        }
                    }
                })
            }

        }
    }

    private fun initInfoView(photo: PhotoListDto, member: MemberProfileDto) {
        binding.apply {
            Glide.with(binding.imageDrawingProfileColor)
                .load(activityViewModel.waterDropColorList[member.profileColor].resourceId)
                .into(binding.imageDrawingProfileColor)
            Glide.with(binding.imageDrawingProfileFace)
                .load(activityViewModel.waterDropFaceList[member.profileFace].resourceId)
                .into(binding.imageDrawingProfileFace)
            Glide.with(binding.imageDrawingProfileAccessory)
                .load(activityViewModel.waterDropAccessoryList[member.profileAccessory].resourceId)
                .into(binding.imageDrawingProfileAccessory)
            textDrawingNickname.text = member.nickname
            textBookmarkCount.text = photo.bookmarkCount.toString()
            textDrawCount.text = photo.pickCount.toString()
        }
    }

    private fun toggleButton(view: View) {
        binding.apply {
            buttonRecentOrder.background =
                ContextCompat.getDrawable(mActivity, R.drawable.frame_button_grey_mild)
            buttonRankingOrder.background =
                ContextCompat.getDrawable(mActivity, R.drawable.frame_button_grey_mild)
            buttonBookmarkOrder.background =
                ContextCompat.getDrawable(mActivity, R.drawable.frame_button_grey_mild)
        }
        view.background =
            ContextCompat.getDrawable(mActivity, R.drawable.frame_button_yellow_mild_200)
    }

    private fun initPhotoDrawingListRecyclerView() {
        photoDrawingListAdapter = PhotoDrawingListAdapter()
        binding.apply {
            recyclerviewPhotoDrawingList.apply {
                layoutManager =
                    LinearLayoutManager(mActivity, LinearLayoutManager.HORIZONTAL, false)
                this.adapter = photoDrawingListAdapter.apply {
                    this.itemClickListener = object : PhotoDrawingListAdapter.ItemClickListener {
                        override fun onClick(view: View, drawing: DrawingListDto) {
                            //여기서 drawing객체를 가진 상태로 PhotoDrawingDetailFragment로 전달
                            val action =
                                PhotoListFragmentDirections.actionPhotoListFragmentToPhotoDrawingDetailFragment(
                                    drawing.drawingId
                                )
                            findNavController().navigate(action)
                        }
                    }
                }
            }
        }
    }

    private fun loadingAnimation() {
        binding.layoutInfo.translationX = 1300f
        binding.layoutPhotoDrawingList.translationX = 1300f
        binding.recyclerviewDrawaing.translationX = -1300f
        binding.textviewDrawings.translationX = 1300f
        initAnimation()
    }

    private fun initAnimation() {
        lifecycleScope.launch {
            makeAnimationX(binding.layoutInfo, 0f, 450)
            delay(100)
            makeAnimationX(binding.layoutPhotoDrawingList, 0f, 500)
            makeAnimationX(binding.textviewDrawings, 0f, 500)
            delay(50)
            makeAnimationX(binding.recyclerviewDrawaing, 0f, 600)
        }
    }

    /**
     * 뷰에 X축으로 움직이는 애니메이션을 적용시킵니다.
     */
    private fun makeAnimationX(view: View, values: Float, speed: Long) {
        ObjectAnimator.ofFloat(view, "translationX", values).apply {
//            interpolator = DecelerateInterpolator()
            interpolator = OvershootInterpolator()
//            interpolator = AccelerateInterpolator()
//            interpolator = AccelerateDecelerateInterpolator()
            duration = speed
            start()
        }
    }
}