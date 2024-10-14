package com.pmcmaApp.pmcma

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

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        fetchImageUrl()
        return binding.root
    }

    private fun fetchImageUrl() {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("https://dailyverses.net/random-bible-verse-picture")
            .build()

        client.newCall(request).enqueue(object : okhttp3.Callback {
            override fun onFailure(call: okhttp3.Call, e: IOException) {
                // Handle the error (e.g., show a placeholder image)
                activity?.runOnUiThread {
                    binding.verseImageBackground.setImageResource(R.drawable.ic_launcher_background)
                }
            }

            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                if (response.isSuccessful) {
                    val html = response.body?.string()
                    val doc = Jsoup.parse(html)
                    // Assuming the image is in a specific tag; adjust the selector if necessary
                    val imageUrl = doc.select("img").first()?.attr("src")

                    activity?.runOnUiThread {
                        if (imageUrl != null) {
                            // Ensure the image URL is complete
                            val fullUrl = if (imageUrl.startsWith("http")) imageUrl else "https://dailyverses.net$imageUrl"

                            Glide.with(this@HomeFragment)
                                .load(fullUrl)
                                .into(binding.verseImageBackground)
                        } else {
                            binding.verseImageBackground.setImageResource(R.drawable.ic_launcher_background)
                        }
                    }
                } else {
                    // Handle the error response
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
