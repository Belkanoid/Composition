package com.belkanoid.composition.presentation

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.FragmentManager
import com.belkanoid.composition.R
import com.belkanoid.composition.databinding.FragmentGameFinishedBinding
import com.belkanoid.composition.domain.entety.GameResult


class GameFinishedFragment : Fragment() {

    private var _binding: FragmentGameFinishedBinding? = null
    private val binding: FragmentGameFinishedBinding
        get() = _binding ?: throw RuntimeException("FragmentGameFinishedBinding == null")

    private lateinit var gameResult: GameResult

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        gameResult = requireArguments().getSerializable(GAME_RESULT) as GameResult
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGameFinishedBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onBackPressedListener()

        with(gameResult) {
            val drawable = when(winner) {
                true -> R.drawable.ic_smile
                else -> R.drawable.ic_sad
            }
            with(binding) {
                emojiResult.setImageResource(drawable)
                tvScoreAnswers.text = getString(R.string.score_answers, countOfRightAnswers.toString())
                tvRequiredAnswers.text = getString(R.string.required_score, gameSettings.minCountOfRightAnswers.toString())
                tvRequiredPercentage.text = getString(R.string.required_percentage, gameSettings.minPercentOfRightAnswers.toString())
                tvScorePercentage.text = getString(R.string.score_percentage, (((countOfRightAnswers / countOfQuestions.toDouble()) * 100).toInt()).toString())
            }

        }
    }

    private fun onBackPressedListener() {

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    retryFinish()
                }
            })

        binding.buttonRetry.setOnClickListener {
            retryFinish()
        }
    }

    private fun retryFinish() {
        requireActivity().supportFragmentManager.popBackStack(
            GameFragment.NAME,
            FragmentManager.POP_BACK_STACK_INCLUSIVE
        )
    }

    companion object {

        private const val GAME_RESULT = "game_result"
        fun newInstance(gameResult: GameResult) =
            GameFinishedFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(GAME_RESULT, gameResult)
                }
            }
    }
}