package com.belkanoid.composition.presentation

import android.content.res.ColorStateList
import android.media.MediaFormat.KEY_LEVEL
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.belkanoid.composition.R
import com.belkanoid.composition.databinding.FragmentGameBinding
import com.belkanoid.composition.domain.entety.GameResult
import com.belkanoid.composition.domain.entety.GameSettings
import com.belkanoid.composition.domain.entety.Level
import com.belkanoid.composition.domain.entety.Question
import com.belkanoid.composition.presentation.viewModel.GameViewModel


class GameFragment : Fragment() {

    private var _binding: FragmentGameBinding? = null
    private val binding: FragmentGameBinding
        get() = _binding ?: throw RuntimeException("FragmentGameBinding == null")

    private val gameViewModel: GameViewModel by lazy {
        ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application)
        )[GameViewModel::class.java]
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val level = requireArguments().getSerializable(KEY_LEVEL) as Level
        gameViewModel.startGame(level)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    )
            : View {
        _binding = FragmentGameBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        gameObservers()
    }

    private fun gameObservers() {
        gameViewModel.gameQuestion.observe(viewLifecycleOwner) {
            setQuestion(it)
        }
        gameViewModel.gameResult.observe(viewLifecycleOwner) {
            launchGameFinishedFragment(it)
        }
        gameViewModel.formatTime.observe(viewLifecycleOwner) {
            binding.tvTimer.text = it
        }
        gameViewModel.percentOfRightAnswers.observe(viewLifecycleOwner){
            binding.progressBar.setProgress(it, true)
        }
        gameViewModel.enoughPercent.observe(viewLifecycleOwner) {
            val color = getColorByState(it)
            binding.progressBar.progressTintList = ColorStateList.valueOf(color)
        }
        gameViewModel.enoughCount.observe(viewLifecycleOwner) {
            binding.tvAnswersProgress.setTextColor(getColorByState(it))
        }
        gameViewModel.minPercent.observe(viewLifecycleOwner) {
            binding.progressBar.secondaryProgress = it
        }
        gameViewModel.progressAnswers.observe(viewLifecycleOwner) {
            binding.tvAnswersProgress.text = it
        }

    }

    private fun getColorByState(goodState: Boolean): Int {
        val colorResId = if (goodState) {
            android.R.color.holo_green_light
        } else {
            android.R.color.holo_red_light
        }
        return ContextCompat.getColor(requireContext(), colorResId)
    }

    private fun launchGameFinishedFragment(gameResult: GameResult) {
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.main_container, GameFinishedFragment.newInstance(gameResult))
            .addToBackStack(null)
            .commit()
    }


    private fun setQuestion(question: Question) {
        with(binding) {
            tvSum.text = question.sum.toString()
            tvLeftNumber.text = question.visibleNumber.toString()

            tvOption1.apply {
                text = question.option[0].toString()
                setOnClickListener(answerCheckListener(question.option[0]))
            }
            tvOption2.apply {
                text = question.option[1].toString()
                setOnClickListener(answerCheckListener(question.option[1]))
            }
            tvOption3.apply {
                text = question.option[2].toString()
                setOnClickListener(answerCheckListener(question.option[2]))
            }
            tvOption4.apply {
                text = question.option[3].toString()
                setOnClickListener(answerCheckListener(question.option[3]))
            }
            tvOption5.apply {
                text = question.option[4].toString()
                setOnClickListener(answerCheckListener(question.option[4]))
            }
            tvOption6.apply {
                text = question.option[5].toString()
                setOnClickListener(answerCheckListener(question.option[5]))
            }
        }
    }


    private val answerCheckListener: ((Int) -> View.OnClickListener) = { option ->
        View.OnClickListener {
            gameViewModel.chooseAnswer(option)
        }
    }


    companion object {
        private const val KEY_LEVEL = "key level"
        const val NAME = "game"

        fun newInstance(level: Level) =
            GameFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(KEY_LEVEL, level)
                }
            }
    }
}