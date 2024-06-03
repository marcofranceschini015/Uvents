package com.example.uvents.ui.user.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import com.example.uvents.R
import com.example.uvents.ui.user.MapActivity
import com.mapbox.search.ui.view.SearchResultsView

/**
 * A simple [Fragment] subclass.
 * Use the [SearchMapBarFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SearchMapBarFragment(private val mapActivity: MapActivity) : Fragment() {

    private lateinit var queryEditText: EditText
    private lateinit var searchResultsView: SearchResultsView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val v: View? = inflater.inflate(R.layout.fragment_search_map_bar, container, false)

        /*
        val accessToken = getString(R.string.mapbox_access_token)
//        Plugin.Mapbox.getInstance(this, getString(R.string.mapbox_access_token))
        if (v != null) {
            queryEditText = v.findViewById<EditText>(R.id.query_edit_text)
            searchResultsView = v.findViewById<SearchResultsView>(R.id.search_results_view)
        }

        searchResultsView.initialize(
            SearchResultsView.Configuration(
                commonConfiguration = CommonSearchViewConfiguration(DistanceUnitType.IMPERIAL)
            )
        )

        val searchEngine = SearchEngine.createSearchEngineWithBuiltInDataProviders(
            apiType = ApiType.GEOCODING,
            settings = SearchEngineSettings(getString(R.string.mapbox_access_token))
        )

        val offlineSearchEngine = OfflineSearchEngine.create(
            OfflineSearchEngineSettings(accessToken)
        )

        val searchEngineUiAdapter = SearchEngineUiAdapter(
            view = searchResultsView,
            searchEngine = searchEngine,
            offlineSearchEngine = offlineSearchEngine,
        )

        searchEngineUiAdapter.addSearchListener(object : SearchEngineUiAdapter.SearchListener {

            private fun showToast(message: String) {
                Toast.makeText(mapActivity.applicationContext, message, Toast.LENGTH_SHORT).show()
            }

            override fun onSuggestionsShown(suggestions: List<SearchSuggestion>, responseInfo: ResponseInfo) {
                // not implemented
            }

            override fun onSearchResultsShown(
                suggestion: SearchSuggestion,
                results: List<SearchResult>,
                responseInfo: ResponseInfo
            ) {
                // not implemented
            }

            override fun onOfflineSearchResultsShown(results: List<OfflineSearchResult>, responseInfo: OfflineResponseInfo) {
                // not implemented
            }

            override fun onSuggestionSelected(searchSuggestion: SearchSuggestion): Boolean {
                return false
            }

            override fun onSearchResultSelected(searchResult: SearchResult, responseInfo: ResponseInfo) {
                showToast("SearchResult clicked: ${searchResult.name}")
            }

            override fun onOfflineSearchResultSelected(searchResult: OfflineSearchResult, responseInfo: OfflineResponseInfo) {
                showToast("OfflineSearchResult clicked: ${searchResult.name}")
            }

            override fun onError(e: Exception) {
                showToast("Error happened: $e")
            }

            override fun onHistoryItemClick(historyRecord: HistoryRecord) {
                showToast("HistoryRecord clicked: ${historyRecord.name}")
            }

            override fun onPopulateQueryClick(suggestion: SearchSuggestion, responseInfo: ResponseInfo) {
                queryEditText.setText(suggestion.name)
            }

            override fun onFeedbackItemClick(responseInfo: ResponseInfo) {
                // not implemented
            }
        })

        queryEditText.addTextChangedListener(object : TextWatcher {

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, after: Int) {
//                searchResultsView.search(s.toString())
                searchResultsView.focusSearch(after)
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // not implemented
            }

            override fun afterTextChanged(e: Editable) { /* not implemented */ }
        })

//        if (!isPermissionGranted(Manifest.permission.ACCESS_FINE_LOCATION)) {
//            ActivityCompat.requestPermissions(
//                mapActivity,
//                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
//                this.PERMISSIONS_REQUEST_LOCATION
//            )
//        }

*/
        return v
    }

//    private companion object {
//
//        private const val PERMISSIONS_REQUEST_LOCATION = 0
//
//        fun Context.isPermissionGranted(permission: String): Boolean {
//            return ContextCompat.checkSelfPermission(
//                this, permission
//            ) == PackageManager.PERMISSION_GRANTED
//        }
//    }

}