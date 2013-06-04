package cn.edu.blcu.contentprovider;


import android.content.SearchRecentSuggestionsProvider;

public class SearchSuggestion extends SearchRecentSuggestionsProvider {
	public final static String AUTHORITY = "cn.edu.blcu.contentprovider.SearchSuggestion";
	public final static int MODE = DATABASE_MODE_QUERIES;

	public SearchSuggestion() {
		super();
		setupSuggestions(AUTHORITY, MODE);
	}

}
