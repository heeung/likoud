package com.ssafy.likloud.ui.home

import android.Manifest
import android.animation.ObjectAnimator
import android.animation.TimeInterpolator
import android.content.Context
import android.graphics.Interpolator
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AccelerateInterpolator
import android.view.animation.AnimationUtils
import android.view.animation.DecelerateInterpolator
import android.view.animation.OvershootInterpolator
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.ssafy.likloud.ApplicationClass
import com.ssafy.likloud.MainActivity
import com.ssafy.likloud.R
import com.ssafy.likloud.base.BaseFragment
import com.ssafy.likloud.databinding.FragmentExampleBinding
import com.ssafy.likloud.databinding.FragmentHomeBinding
import com.ssafy.likloud.databinding.FragmentLoginBinding
import com.ssafy.likloud.example.ExampleFragmentViewModel
import com.ssafy.likloud.ui.login.LoginFragmentDirections
import com.ssafy.likloud.ui.login.LoginFragmentViewModel
import com.ssafy.likloud.ui.upload.UploadFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment :
    BaseFragment<FragmentHomeBinding>(FragmentHomeBinding::bind, R.layout.fragment_home) {

    private val homeFragmentViewModel: HomeFragmentViewModel by viewModels()
    private lateinit var navController: NavController
    private var isCameraOpened = false


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(binding.root)

        initView()
        initListener()
        initAnimation()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            initPermission()
        }
    }


    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun initPermission() {
        val permissionList = arrayOf(
            Manifest.permission.POST_NOTIFICATIONS,
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
        )
        requestMultiplePermission.launch(permissionList)
    }

    private val requestMultiplePermission =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { results ->
            results.forEach {
            }
        }

    /**
     * 클릭 리스너를 init합니다.
     */
    override fun initListener() {
        binding.buttonCamera.setOnClickListener {
            moveButtonsToLeft()
            startUploadFragment()
        }

        binding.buttonGame.setOnClickListener {
            navController.navigate(R.id.action_homeFragment_to_gameFragment)
        }

        binding.buttonPainting.setOnClickListener {
            navController.navigate(R.id.action_homeFragment_to_photoListFragment)
        }

        binding.buttonDrawingList.setOnClickListener {
            navController.navigate(R.id.action_homeFragment_to_drawingListFragment)
        }

        binding.layoutHomeBackground.setOnClickListener {
            when (isCameraOpened) {
                true -> {
                    endUploadFragment()
                }

                else -> {}
            }
        }

        // 안드로이드 뒤로가기 버튼 눌렀을 때
        mActivity.onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    when (isCameraOpened) {
                        true -> {
                            endUploadFragment()
                        }

                        false -> {
                            mActivity.finish()
                        }
                    }
                }
            })
    }

    /**
     * 뷰의 초기설정을 init 합니다.
     */
    private fun initView() {
        binding.frameFragmentUpload.translationX = 1600f
    }

    /**
     * 애니메이션을 init합니다.
     */
    private fun initAnimation() {
        binding.buttonCamera.animation =
            AnimationUtils.loadAnimation(mActivity, R.anim.shake_up_down)
        binding.buttonPainting.animation =
            AnimationUtils.loadAnimation(mActivity, R.anim.shake_up_down2)
        binding.buttonDrawingList.animation =
            AnimationUtils.loadAnimation(mActivity, R.anim.shake_up_down3)
        binding.buttonGame.animation =
            AnimationUtils.loadAnimation(mActivity, R.anim.shake_up_down4)
    }

    /**
     * 홈 화면에서 모든 버튼들이 왼쪽으로 움직입니다. 추가로 버튼의 크기가 0.7f로 작아집니다.
     */
    private fun moveButtonsToLeft() {
        makeButtonAnimationX(binding.buttonCamera, -300f)
        makeButtonAnimationX(binding.buttonPainting, -700f)
        makeButtonAnimationX(binding.buttonDrawingList, -700f)
        makeButtonAnimationX(binding.buttonGame, -1000f)

        makeButtonAnimationY(binding.buttonCamera, -150f)
        makeButtonAnimationY(binding.buttonPainting, 50f)
        makeButtonAnimationY(binding.buttonDrawingList, -100f)
        makeButtonAnimationY(binding.buttonGame, 50f)

        makeButtonAnimationScale(binding.buttonCamera, 0.7f)
        makeButtonAnimationScale(binding.buttonPainting, 0.7f)
        makeButtonAnimationScale(binding.buttonDrawingList, 0.7f)
        makeButtonAnimationScale(binding.buttonGame, 0.7f)
    }

    /**
     * 홈 화면에서 모든 버튼들이 오른쪽으로 움직입니다. 추가로 버튼의 크기가 원래대로 커집니다.
     */
    private fun moveButtonsToRight() {
        makeButtonAnimationX(binding.buttonCamera, 0f)
        makeButtonAnimationX(binding.buttonPainting, 0f)
        makeButtonAnimationX(binding.buttonDrawingList, 0f)
        makeButtonAnimationX(binding.buttonGame, 0f)

        makeButtonAnimationY(binding.buttonCamera, 0f)
        makeButtonAnimationY(binding.buttonPainting, 0f)
        makeButtonAnimationY(binding.buttonDrawingList, 0f)
        makeButtonAnimationY(binding.buttonGame, 0f)

        makeButtonAnimationScale(binding.buttonCamera, 1f)
        makeButtonAnimationScale(binding.buttonPainting, 1f)
        makeButtonAnimationScale(binding.buttonDrawingList, 1f)
        makeButtonAnimationScale(binding.buttonGame, 1f)
    }

    /**
     * 버튼에 X축으로 움직이는 애니메이션을 적용시킵니다.
     */
    private fun makeButtonAnimationX(button: View, values: Float) {
        ObjectAnimator.ofFloat(button, "translationX", values).apply {
//            interpolator = DecelerateInterpolator()
            interpolator = OvershootInterpolator()
            duration = 500
            start()
        }
    }

    /**
     * 버튼에 Y축으로 움직이는 애니메이션을 적용시킵니다.
     */
    private fun makeButtonAnimationY(button: View, values: Float) {
        ObjectAnimator.ofFloat(button, "translationY", values).apply {
//            interpolator = DecelerateInterpolator()
            interpolator = OvershootInterpolator()
//            duration = 500
            start()
        }
    }

    /**
     * 버튼에 크기를 조절하는 애니메이션을 적용시킵니다.
     */
    private fun makeButtonAnimationScale(button: View, values: Float) {
        ObjectAnimator.ofFloat(button, "scaleX", values).apply {
            duration = 500
            start()
        }
        ObjectAnimator.ofFloat(button, "scaleY", values).apply {
            duration = 500
            start()
        }
    }

    /**
     * 레이아웃에 사라지고 나타나는 애니메이션을 적용시킵니다.
     */
    private fun makeAnimationFade(layout: FrameLayout, values: Float) {
        ObjectAnimator.ofFloat(layout, "alpha", values).apply {
            duration = 600
            start()
        }
        ObjectAnimator.ofFloat(layout, "scaleX", values).apply {
            duration = 1000
            start()
        }
        ObjectAnimator.ofFloat(layout, "scaleY", values).apply {
            duration = 1000
            start()
        }
    }

    private fun startUploadFragment() {
//        makeAnimationFade(binding.frameFragmentUpload, 1f)
        isCameraOpened = true
        moveButtonsToLeft()
        makeButtonAnimationX(binding.frameFragmentUpload, 0f)

        childFragmentManager.beginTransaction()
            .replace(R.id.frame_fragment_upload, UploadFragment())
            .commit()
    }

    private fun endUploadFragment() {
//        makeAnimationFade(binding.frameFragmentUpload, 0f)
        isCameraOpened = false
        moveButtonsToRight()
        makeButtonAnimationX(binding.frameFragmentUpload, 1600f)
    }
}