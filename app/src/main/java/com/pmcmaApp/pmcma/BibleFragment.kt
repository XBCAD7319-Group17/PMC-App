package com.pmcmaApp.pmcma

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.firebase.firestore.FirebaseFirestore
import org.json.JSONArray
import org.json.JSONObject
import java.util.TreeMap

class BibleFragment : Fragment() {

    private lateinit var bookSearch: AutoCompleteTextView
    private lateinit var chapterDropdown: Spinner
    private lateinit var verseDropdown: Spinner
    private lateinit var verseTextView: TextView
    private lateinit var progressBar: ProgressBar

    private val firestore = FirebaseFirestore.getInstance()
    private val booksMap = mutableMapOf<String, BookInfo>() // Store book names, chapter count, and document ID
    private lateinit var requestQueue: RequestQueue

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_bible, container, false)
        bookSearch = view.findViewById(R.id.bookSearch)
        chapterDropdown = view.findViewById(R.id.chapterDropdown)
        verseDropdown = view.findViewById(R.id.verseDropdown)
        verseTextView = view.findViewById(R.id.verseTextView)
        progressBar = view.findViewById(R.id.progressBar)

        requestQueue = Volley.newRequestQueue(requireContext())

        // Fetch data from Firestore and set up the search functionality
        fetchBooks()

        return view
    }

    private fun fetchBooks() {
        progressBar.visibility = View.VISIBLE
        bookSearch.visibility = View.GONE
        chapterDropdown.visibility = View.GONE
        verseDropdown.visibility = View.GONE
        verseTextView.visibility = View.GONE
        firestore.collection("bible").document("9879dbb7cfe39e4d-01")
            .collection("books")
            .get()
            .addOnSuccessListener { result ->
                val bookNames = mutableListOf<String>()
                for (document in result) {
                    val name = document.getString("name")
                    val chapters = document.get("chapters") // Use 'get' instead of 'getLong'
                    val docId = document.id // Store document ID

                    // Check if chapters is a string or a number and handle accordingly
                    val chaptersCount = when (chapters) {
                        is String -> chapters.toIntOrNull() // Convert string to Int
                        is Number -> chapters.toInt() // If it's a number, cast to Int
                        else -> null // Default to null if the field is missing or has an unexpected type
                    }

                    // Only add the book if chaptersCount is not null and greater than 0
                    if (name != null && chaptersCount != null && chaptersCount > 0) {
                        booksMap[name] = BookInfo(chaptersCount, docId) // Store non-null chaptersCount and docId
                        bookNames.add(name)
                    }
                }

                setupSearchBox(bookNames)
                progressBar.visibility = View.GONE
                bookSearch.visibility = View.VISIBLE
            }
            .addOnFailureListener {
                progressBar.visibility = View.GONE
                // Handle failure
            }
    }

    private fun setupSearchBox(bookNames: List<String>) {
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, bookNames)
        bookSearch.setAdapter(adapter)

        // Show all books when the search box is clicked with no input
        bookSearch.setOnClickListener {
            if (bookSearch.text.isEmpty()) {
                bookSearch.showDropDown()
            }
        }

        // Handle book selection
        bookSearch.setOnItemClickListener { _, _, position, _ ->
            val selectedBook = adapter.getItem(position) ?: return@setOnItemClickListener
            populateChaptersDropdown(selectedBook)
        }
    }

    private fun populateChaptersDropdown(bookName: String) {
        val chapterCount = booksMap[bookName]?.chapterCount ?: return

        // Create a list starting with "Intro" followed by the chapter numbers
        val chapters = mutableListOf("Intro") // Start with "Intro"
        chapters.addAll((1..chapterCount).map { it.toString() }) // Add chapter numbers

        val chapterAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, chapters)
        chapterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        chapterDropdown.visibility = View.VISIBLE
        chapterDropdown.adapter = chapterAdapter

        // Handle chapter selection
        chapterDropdown.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                val selectedChapter = chapterDropdown.selectedItem.toString()
                fetchVerses(bookName, selectedChapter)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        })
    }

    private fun fetchVerses(bookName: String, chapter: String) {
        val bookId = booksMap[bookName]?.documentId ?: return
        val chapterId = if (chapter == "Intro") "intro" else chapter

        val url = "https://api.scripture.api.bible/v1/bibles/9879dbb7cfe39e4d-01/chapters/$bookId.$chapterId/verses"
        val jsonObjectRequest = object : JsonObjectRequest(Request.Method.GET, url, null,
            { response ->
                handleVersesResponse(response, bookId, chapterId)
            },
            { error ->
                // Handle error
                Toast.makeText(requireContext(), "Failed to fetch verses", Toast.LENGTH_SHORT).show()
            }) {
            override fun getHeaders(): Map<String, String> {
                return mapOf("api-key" to "2b3c1b3e49a726a31eb8556caed9377b")
            }
        }

        requestQueue.add(jsonObjectRequest)
    }

    private fun handleVersesResponse(response: JSONObject, bookId: String, chapterId: String) {
        val verses = response.getJSONArray("data")
        val verseCount = verses.length()
        val verseNumbers = (1..verseCount).map { it.toString() }.toMutableList()

        verseDropdown.visibility = View.VISIBLE
        verseDropdown.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, verseNumbers)

        // Handle verse selection
        verseDropdown.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                val selectedVerse = verseDropdown.selectedItem.toString()
                displayAllVerses(bookId, chapterId, selectedVerse)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        })

    }

    private fun displayAllVerses(bookId: String, chapterId: String, selectedVerse: String) {
        // Show the loading spinner and hide the verseTextView initially
        progressBar.visibility = View.VISIBLE
        verseTextView.visibility = View.GONE
        verseTextView.text = ""

        val verseCount = verseDropdown.adapter.count
        val sortedVersesMap = TreeMap<Int, String>()

        for (i in 1..verseCount) {
            fetchVerseText(bookId, chapterId, i.toString()) { verseText, verseNumber ->
                // Add the verse text to the map using the verse number as the key
                val verseNumInt = verseNumber.toInt()
                sortedVersesMap[verseNumInt] = verseText

                // Check if all verses are fetched by comparing the size of the map
                if (sortedVersesMap.size == verseCount) {
                    val versesText = StringBuilder()
                    for ((verseNum, text) in sortedVersesMap) {
                        val formattedVerse = if (verseNum.toString() == selectedVerse) {
                            "<b>$verseNum: $text</b>"
                        } else {
                            "$verseNum: $text"
                        }
                        versesText.append(formattedVerse).append("\n")
                    }
                    // Once all verses are fetched, hide the loading spinner and show the verseTextView
                    progressBar.visibility = View.GONE
                    verseTextView.text = android.text.Html.fromHtml(versesText.toString())
                    verseTextView.visibility = View.VISIBLE
                }
            }
        }
    }


    private fun fetchVerseText(bookId: String, chapterId: String, verse: String, callback: (String, String) -> Unit) {
        val url = "https://api.scripture.api.bible/v1/bibles/9879dbb7cfe39e4d-01/verses/$bookId.$chapterId.$verse?content-type=json&include-notes=false&include-titles=false&include-chapter-numbers=false&include-verse-numbers=false&include-verse-spans=false&use-org-id=false"
        val jsonObjectRequest = object : JsonObjectRequest(Request.Method.GET, url, null,
            { response ->
                val verseData = response.getJSONObject("data")
                val contentArray = verseData.getJSONArray("content")
                val verseText = StringBuilder()

                for (i in 0 until contentArray.length()) {
                    val item = contentArray.getJSONObject(i)
                    if (item.has("items")) {
                        val itemsArray = item.getJSONArray("items")
                        for (j in 0 until itemsArray.length()) {
                            val verseItem = itemsArray.getJSONObject(j)
                            verseText.append(verseItem.getString("text"))
                        }
                    }
                }
                // Return the fetched verse text and its number
                callback(verseText.toString(), verse)
            },
            { error ->
                // Handle error
                Toast.makeText(requireContext(), "Failed to fetch verse text", Toast.LENGTH_SHORT).show()
            }) {
            override fun getHeaders(): Map<String, String> {
                return mapOf("api-key" to "2b3c1b3e49a726a31eb8556caed9377b")
            }
        }

        requestQueue.add(jsonObjectRequest)
    }


    private fun handleVerseTextResponse(response: JSONObject) {
        val verseData = response.getJSONObject("data")
        val contentArray = verseData.getJSONArray("content")
        val verseText = StringBuilder()

        for (i in 0 until contentArray.length()) {
            val item = contentArray.getJSONObject(i)
            if (item.has("items")) {
                val itemsArray = item.getJSONArray("items")
                for (j in 0 until itemsArray.length()) {
                    val verseItem = itemsArray.getJSONObject(j)
                    verseText.append(verseItem.getString("text"))
                }
            }
        }

        verseTextView.text = verseText.toString()
        verseTextView.visibility = View.VISIBLE
    }


    // Data class to store book information
    data class BookInfo(val chapterCount: Int, val documentId: String)
}
