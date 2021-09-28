package com.example.wisieleckotlin

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.wisieleckotlin.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val maxSteps = 6
    private var wordToFind: String? = null
    private lateinit var wordFound: CharArray
    private var nbErrors = 0

    // letters already entered by user
    private val letters: ArrayList<String> = ArrayList()
    private var img: ImageView? = null
    private var wordTv: TextView? = null
    private var wordToFindTv: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        img = binding.img
        wordTv = binding.wordTv
        wordToFindTv = binding.wordToFindTv
        newGame()
    }

    private fun newGame() {
        //binding.restart.visibility = Button.INVISIBLE
        binding.restart.isEnabled = false //disable start
        nbErrors = 0
        letters.clear()
        wordToFind = setWord()

        // word found initialization
        wordFound = CharArray(wordToFind!!.length)
        for (i in wordFound.indices) {
            wordFound[i] = '#'
        }
        updateImage(nbErrors)
        wordTv!!.text = wordFoundContent()
        wordToFindTv!!.text = ""
    }

    private fun setWord(): String? {
        val words = resources.getStringArray(R.array.words)
        val random = (words.indices).random()
        return words[random]
    }

    private fun updateImage(play: Int) {
        val resImg = resources.getIdentifier("wisielec_$play", "drawable", packageName)
        img?.setImageResource(resImg)
    }


    // Method returning trust if word is found by user
    private fun wordFound(): Boolean {
        return wordToFind!!.contentEquals(String(wordFound))
    }

    // Method updating the word found after user entered a character
    private fun enter(c: String) {
        if (!letters.contains(c)) {
            // we check if word to find contains c
            if (wordToFind!!.contains(c)) {
                // if so, we replace _ by the character c
                var index = wordToFind!!.indexOf(c)
                while (index >= 0) {
                    wordFound[index] = c[0]
                    index = wordToFind!!.indexOf(c, index + 1)
                }
            } else {
                // c not in the word => error
                nbErrors++
               // Toast.makeText(this, R.string.try_an_other, Toast.LENGTH_SHORT).show()
            }

            // c is now a letter entered
            letters.add(c)
        }
    }

    // Method returning the state of the word found by the user until by now
    private fun wordFoundContent(): String {
        val builder = StringBuilder()
        for (i in wordFound.indices) {
            builder.append(wordFound[i])
            if (i < wordFound.size - 1) {
                builder.append(" ")
            }
        }
        return builder.toString()
    }

    fun touchLetter(view: View) {
        if (nbErrors < maxSteps
                && getString(R.string.you_win) != wordToFindTv!!.text) {
            val letter = (view as Button).text.toString()
            view.isEnabled = false
            //Toast.makeText(this, "TOAST $letter", Toast.LENGTH_SHORT).show()
            enter(letter)
            wordTv!!.text = wordFoundContent()
            updateImage(nbErrors)

            // check if word is found
            if (wordFound()) {
                Toast.makeText(this, R.string.you_win, Toast.LENGTH_SHORT).show()
                wordToFindTv!!.setText(R.string.you_win)
                binding.restart.isEnabled = true
            } else {
                if (nbErrors >= maxSteps) {
                    Toast.makeText(this, R.string.you_lose, Toast.LENGTH_SHORT).show()
                    binding.restart.isEnabled = true
                }
            }
        } else {
            binding.restart.isEnabled = true
            Toast.makeText(this, R.string.game_is_ended, Toast.LENGTH_SHORT).show()
        }
    }

    fun restartButton(view: View) {
        refreshKeyboard()
        newGame()
    }

    private fun refreshKeyboard() {
        val keyboardView = binding.keyboardLayout
        for (i in 0 until keyboardView.childCount) {
            val childAt :LinearLayout = keyboardView.getChildAt(i) as LinearLayout
            for(j in 0 until childAt.childCount) {
                childAt.getChildAt(j).isEnabled = true
            }
        }
    }
}