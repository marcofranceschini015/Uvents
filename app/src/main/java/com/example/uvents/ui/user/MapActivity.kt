package com.example.uvents.ui.user

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.example.uvents.R
import com.example.uvents.controllers.MapController
import com.example.uvents.ui.user.fragments.MapFragment
import com.example.uvents.ui.user.fragments.SearchMapBarFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.mapbox.search.ApiType
import com.mapbox.search.ResponseInfo
import com.mapbox.search.SearchEngine
import com.mapbox.search.SearchEngineSettings
import com.mapbox.search.offline.OfflineResponseInfo
import com.mapbox.search.offline.OfflineSearchEngine
import com.mapbox.search.offline.OfflineSearchEngineSettings
import com.mapbox.search.offline.OfflineSearchResult
import com.mapbox.search.record.HistoryRecord
import com.mapbox.search.result.SearchResult
import com.mapbox.search.result.SearchSuggestion
import com.mapbox.search.ui.adapter.engines.SearchEngineUiAdapter
import com.mapbox.search.ui.view.CommonSearchViewConfiguration
import com.mapbox.search.ui.view.DistanceUnitType
import com.mapbox.search.ui.view.SearchResultsView

/**
 * Activity with the map and every events on it
 */
class MapActivity : AppCompatActivity() {

    private lateinit var mapController: MapController
    private lateinit var bottomNavigation: BottomNavigationView

    /**
     * On creation create a mapController and recover the user in it
     * then launch the fragment with the map, by filling it
     */
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_map)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        bottomNavigation = findViewById(R.id.bottomNav)
        mapController = MapController(this)
        mapController.setUser(intent.getStringExtra("uid"))

        replaceSearchBarFragment(SearchMapBarFragment(this))
        replaceFragment(MapFragment(mapController))
        bottomNavigation.visibility = View.VISIBLE



//        val accessToken = getString(R.string.mapbox_access_token)
//
//        val queryEditText = findViewById<EditText>(R.id.query_edit_text)
//        val searchResultsView = findViewById<SearchResultsView>(R.id.search_results_view)
//        searchResultsView.initialize(
//            SearchResultsView.Configuration(
//                commonConfiguration = CommonSearchViewConfiguration(DistanceUnitType.IMPERIAL)
//            )
//        )
//
//        val searchEngine = SearchEngine.createSearchEngineWithBuiltInDataProviders(
//            apiType = ApiType.GEOCODING,
//            settings = SearchEngineSettings(accessToken)
//        )
//
//        val offlineSearchEngine = OfflineSearchEngine.create(
//            OfflineSearchEngineSettings(accessToken)
//        )
//
//        val searchEngineUiAdapter = SearchEngineUiAdapter(
//            view = searchResultsView,
//            searchEngine = searchEngine,
//            offlineSearchEngine = offlineSearchEngine,
//        )
//
//        searchEngineUiAdapter.addSearchListener(object : SearchEngineUiAdapter.SearchListener {
//
//            private fun showToast(message: String) {
//                Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
//            }
//
//            override fun onSuggestionsShown(suggestions: List<SearchSuggestion>, responseInfo: ResponseInfo) {
//                // not implemented
//            }
//
//            override fun onSearchResultsShown(
//                suggestion: SearchSuggestion,
//                results: List<SearchResult>,
//                responseInfo: ResponseInfo
//            ) {
//                // not implemented
//            }
//
//            override fun onOfflineSearchResultsShown(results: List<OfflineSearchResult>, responseInfo: OfflineResponseInfo) {
//                // not implemented
//            }
//
//            override fun onSuggestionSelected(searchSuggestion: SearchSuggestion): Boolean {
//                return false
//            }
//
//            override fun onSearchResultSelected(searchResult: SearchResult, responseInfo: ResponseInfo) {
//                showToast("SearchResult clicked: ${searchResult.name}")
//            }
//
//            override fun onOfflineSearchResultSelected(searchResult: OfflineSearchResult, responseInfo: OfflineResponseInfo) {
//                showToast("OfflineSearchResult clicked: ${searchResult.name}")
//            }
//
//            override fun onError(e: Exception) {
//                showToast("Error happened: $e")
//            }
//
//            override fun onHistoryItemClick(historyRecord: HistoryRecord) {
//                showToast("HistoryRecord clicked: ${historyRecord.name}")
//            }
//
//            override fun onPopulateQueryClick(suggestion: SearchSuggestion, responseInfo: ResponseInfo) {
//                queryEditText.setText(suggestion.name)
//            }
//
//            override fun onFeedbackItemClick(responseInfo: ResponseInfo) {
//                // not implemented
//            }
//        })
//
//        queryEditText.addTextChangedListener(object : TextWatcher {
//
//            override fun onTextChanged(s: CharSequence, start: Int, before: Int, after: Int) {
//                searchResultsView.search(s.toString())
//            }
//
//            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
//                // not implemented
//            }
//
//            override fun afterTextChanged(e: Editable) { /* not implemented */ }
//        })

        if (!isPermissionGranted(Manifest.permission.ACCESS_FINE_LOCATION)) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
                PERMISSIONS_REQUEST_LOCATION
            )
        }

    }


    /**
     * Function that replace the fragment in the activity
     */
    fun replaceFragment(fragment: Fragment){
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frgMapContainer, fragment)
        fragmentTransaction.commit()
    }

    fun replaceSearchBarFragment(fragment: Fragment){
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frgSearchBarContainer, fragment)
        fragmentTransaction.commit()
    }

    private companion object {

        private const val PERMISSIONS_REQUEST_LOCATION = 0

        fun Context.isPermissionGranted(permission: String): Boolean {
            return ContextCompat.checkSelfPermission(
                this, permission
            ) == PackageManager.PERMISSION_GRANTED
        }
    }

}