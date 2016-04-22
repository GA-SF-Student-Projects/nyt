package adi.sf1.targaryen.newyorktimes.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import adi.sf1.targaryen.newyorktimes.MainActivity;
import adi.sf1.targaryen.newyorktimes.api.Call;
import adi.sf1.targaryen.newyorktimes.api.Callback;
import adi.sf1.targaryen.newyorktimes.api.NewYorkTimes;
import adi.sf1.targaryen.newyorktimes.api.result.ArticleSearch;
import retrofit2.Response;

/**
 * Created by emiliaaxen on 16-04-21.
 * Fragment that shows the users search results and presents them in the recycler view
 */


public class SearchFragment extends ArticleFeedFragment {
  private static final String TAG = "SearchFragment";

  String searchArticleQuery;

  /**
   * Grabs the search query from the article feed fragment
   * @param savedInstanceState
   */
  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Bundle bundle = getArguments();
    if (bundle != null) {
      searchArticleQuery = bundle.getString(MainActivity.SEARCH_KEY);
    }
  }

  /**
   * Overrides the method to set the feed with the article search api
   * @param cache
   */
  @Override
  protected void setFeedList(boolean cache) {
    NewYorkTimes.getInstance().articleSearch(searchArticleQuery).enqueue(new Callback<ArticleSearch>() {
      @Override
      public void onResponse(Call<ArticleSearch> call, Response<ArticleSearch> response) {
        articleFeedAdapter.changeDataSet(response.body().getResults());
        swipeContainer.setRefreshing(false);
      }

      @Override
      public void onFailure(Call<ArticleSearch> call, Throwable t) {
        Toast.makeText(context, "Could not retrive Search Result", Toast.LENGTH_LONG).show();
        Log.w(TAG, "onFailure: ", t);
      }
    }, cache);
  }

}
