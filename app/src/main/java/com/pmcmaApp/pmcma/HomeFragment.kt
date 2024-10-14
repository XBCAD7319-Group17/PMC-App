package com.pmcmaApp.pmcma

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.pmcmaApp.pmcma.databinding.FragmentHomeBinding
import okhttp3.OkHttpClient
import okhttp3.Request
import org.jsoup.Jsoup
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val sharedPrefFile = "VerseOfTheDayPref"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        loadVerseOfTheDay()
        return binding.root
    }

    private fun loadVerseOfTheDay() {
        val sharedPreferences = requireActivity().getSharedPreferences(sharedPrefFile, Context.MODE_PRIVATE)
        val lastFetchedDate = sharedPreferences.getString("lastFetchedDate", null)
        val savedVerse = sharedPreferences.getString("verseOfTheDay", null)
        val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

        if (lastFetchedDate == currentDate && savedVerse != null) {
            // Load the saved verse if it's the same day
            Glide.with(this)
                .load(savedVerse)
                .into(binding.verseImageBackground)
        } else {
            // Fetch a new verse if the date has changed
            fetchImageUrl(sharedPreferences, currentDate)
        }
    }

    private fun fetchImageUrl(sharedPreferences: SharedPreferences, currentDate: String) {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("https://dailyverses.net/random-bible-verse-picture")
            .build()

        client.newCall(request).enqueue(object : okhttp3.Callback {
            override fun onFailure(call: okhttp3.Call, e: IOException) {
                // Log error here for debugging
                e.printStackTrace()
                // Show a placeholder image
                activity?.runOnUiThread {
                    binding.verseImageBackground.setImageResource(R.drawable.ic_launcher_background)
                }
            }

            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                if (response.isSuccessful) {
                    val html = response.body?.string()
                    val doc = Jsoup.parse(html)
                    val imageUrl = doc.select("img").first()?.attr("src")

                    activity?.runOnUiThread {
                        if (imageUrl != null) {
                            val fullUrl = if (imageUrl.startsWith("http")) {
                                imageUrl
                            } else {
                                "https://dailyverses.net$imageUrl"
                            }

                            // Load the image using Glide
                            Glide.with(this@HomeFragment)
                                .load(fullUrl)
                                .into(binding.verseImageBackground)

                            // Save the new verse and current date in SharedPreferences
                            with(sharedPreferences.edit()) {
                                putString("verseOfTheDay", fullUrl)
                                putString("lastFetchedDate", currentDate)
                                apply()
                            }
                        } else {
                            binding.verseImageBackground.setImageResource(R.drawable.ic_launcher_background)
                        }
                    }
                } else {
                    // Log error response for debugging
                    activity?.runOnUiThread {
                        binding.verseImageBackground.setImageResource(R.drawable.ic_launcher_background)
                    }
                }
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
