package com.example.scoremaster

import android.graphics.Color
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.TextView

class MainActivity : AppCompatActivity() {

    // View variables using lazy initialization
    private val scoreTextView: TextView by lazy { findViewById(R.id.tv_score) }
    private val increaseScoreButton: Button by lazy { findViewById(R.id.btn_increase_score) }
    private val decreaseScoreButton: Button by lazy { findViewById(R.id.btn_decrease_score) }
    private val resetScoreButton: Button by lazy { findViewById(R.id.btn_reset_score) }

    // Media player for sounds
    private var winSoundPlayer: MediaPlayer? = null

    // Game variables
    private var currentScore = 0

    companion object {
        private const val TAG = "ScoreMaster"
        private const val MAX_SCORE = 15
        private const val CURRENT_SCORE_KEY = "CURRENT_SCORE"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Log.d(TAG, "Activity created")

        // Restore state if activity is recreated
        currentScore = savedInstanceState?.getInt(CURRENT_SCORE_KEY, 0) ?: 0
        updateScoreDisplay()

        // Set up button listeners
        setupButtonListeners()
    }

    /**
     * Sets up button listeners for handling score actions.
     */
    private fun setupButtonListeners() {
        increaseScoreButton.setOnClickListener {
            Log.d(TAG, "Increase button clicked")
            incrementScore()
        }

        decreaseScoreButton.setOnClickListener {
            Log.d(TAG, "Decrease button clicked")
            decrementScore()
        }

        resetScoreButton.setOnClickListener {
            Log.d(TAG, "Reset button clicked")
            resetScore()
        }
    }

    /**
     * Increments the score, updates the display, and plays a sound if the score reaches MAX_SCORE.
     */
    private fun incrementScore() {
        if (currentScore < MAX_SCORE) {
            currentScore++
            updateScoreDisplay()
            if (currentScore == MAX_SCORE) {
                Log.d(TAG, "Max score reached, playing win sound and showing confetti")
                playWinSound()
                scoreTextView.setTextColor(Color.GREEN) // Change color to green
                decreaseScoreButton.isEnabled = false // Disable the "steal" button
            }
        } else {
            Log.d(TAG, "Score is already at maximum")
        }
    }

    /**
     * Decrements the score and updates the display.
     */
    private fun decrementScore() {
        if (currentScore > 0) {
            currentScore--
            updateScoreDisplay()

            // Re-enable the steal button if it was disabled
            if (currentScore < MAX_SCORE && !decreaseScoreButton.isEnabled) {
                decreaseScoreButton.isEnabled = true
            }

            // Reset the score color to default if it's below the maximum
            if (currentScore < MAX_SCORE) {
                scoreTextView.setTextColor(Color.BLACK) // Reset to default color
            }
        } else {
            Log.d(TAG, "Score is already at minimum")
        }
    }

    /**
     * Resets the score to 0 and updates the display.
     */
    private fun resetScore() {
        currentScore = 0
        updateScoreDisplay()
        scoreTextView.setTextColor(Color.BLACK) // Reset to default color
        decreaseScoreButton.isEnabled = true // Re-enable the "steal" button
        Log.d(TAG, "Score reset to 0")
    }

    /**
     * Updates the score TextView to reflect the current score.
     */
    private fun updateScoreDisplay() {
        scoreTextView.text = currentScore.toString()
        Log.d(TAG, "Score updated to $currentScore")
    }

    /**
     * Plays the win sound when the score reaches MAX_SCORE.
     */
    private fun playWinSound() {
        winSoundPlayer?.release() // Release any previous MediaPlayer instance
        winSoundPlayer = MediaPlayer.create(this, R.raw.w).apply {
            setOnCompletionListener {
                it.release()
                Log.d(TAG, "Win sound playback completed")
            }
            start()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        // Save the current score
        outState.putInt(CURRENT_SCORE_KEY, currentScore)
        Log.d(TAG, "Score saved: $currentScore")
    }

    override fun onDestroy() {
        super.onDestroy()
        // Release MediaPlayer resources
        winSoundPlayer?.release()
        winSoundPlayer = null
        Log.d(TAG, "Activity destroyed, MediaPlayer resources released")
    }
}
