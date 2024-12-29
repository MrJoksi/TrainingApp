package com.example.TrainingApp

import android.os.Bundle
import android.widget.ExpandableListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONArray
import org.json.JSONObject
import java.io.InputStreamReader
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    lateinit var dateTV: TextView
    lateinit var expandableListView: ExpandableListView
    lateinit var expandableListAdapter: CustomExpandableListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize UI components
        dateTV = findViewById(R.id.idTVDate)
        expandableListView = findViewById(R.id.expandableListView)

        // Load data from JSON
        val jsonData = loadDataFromJson()

        // Get current date and display it
        val currentDate = getCurrentDate()
        dateTV.text = currentDate

        // Select random exercises for the day
        val selectedExercises = selectDailyExercises(jsonData, currentDate)

        // Set up the adapter with selected exercises
        expandableListAdapter = CustomExpandableListAdapter(this, selectedExercises)
        expandableListView.setAdapter(expandableListAdapter)
    }

    private fun loadDataFromJson(): JSONObject {
        val inputStream = resources.openRawResource(R.raw.tiedot)
        val reader = InputStreamReader(inputStream)
        val jsonString = reader.readText()
        reader.close()
        return JSONObject(jsonString)
    }

    private fun getCurrentDate(): String {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        return dateFormat.format(calendar.time)
    }

    private fun selectDailyExercises(jsonData: JSONObject, currentDate: String): JSONObject {
        val voimaList = jsonData.getJSONArray("Voima").toStringList()
        val lihasList = jsonData.getJSONArray("Lihas").toStringList()

        val seed = currentDate.hashCode().toLong()
        val random = Random(seed)

        val selectedVoima = voimaList.shuffled(random).take(7)
        val selectedLihas = lihasList.shuffled(random).take(7)

        // Muunna valitut liikkeet JSONArray-objekteiksi
        val selectedVoimaJsonArray = JSONArray(selectedVoima)
        val selectedLihasJsonArray = JSONArray(selectedLihas)

        // Luodaan uusi JSON-objekti valituista liikkeistä
        val selectedExercises = JSONObject()
        selectedExercises.put("Voima", selectedVoimaJsonArray)
        selectedExercises.put("Lihas", selectedLihasJsonArray)

        return selectedExercises
    }

    // Extension function to convert JSONArray to List<String>
    fun JSONArray.toStringList(): List<String> {
        val list = mutableListOf<String>()
        for (i in 0 until this.length()) {
            list.add(this.getString(i))  // Lisää jokaisen elementin listaan
        }
        return list
    }

    fun JSONArray.toList(): List<String> {
        val list = mutableListOf<String>()
        for (i in 0 until this.length()) {
            list.add(this.getString(i))
        }
        return list
    }
}
