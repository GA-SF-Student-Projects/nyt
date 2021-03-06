package adi.sf1.targaryen.newyorktimes.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import adi.sf1.targaryen.newyorktimes.ArticleActivity;
import adi.sf1.targaryen.newyorktimes.CheckInternetConnection;
import adi.sf1.targaryen.newyorktimes.R;
import adi.sf1.targaryen.newyorktimes.api.Call;
import adi.sf1.targaryen.newyorktimes.api.Callback;
import adi.sf1.targaryen.newyorktimes.api.NewYorkTimes;
import adi.sf1.targaryen.newyorktimes.api.result.StoryInterface;
import adi.sf1.targaryen.newyorktimes.api.result.TopStories;
import adi.sf1.targaryen.newyorktimes.recyclerAdapter.ArticleFeedAdapter;
import retrofit2.Response;

/**
 * Fragment showing the list of articles for a certain new york times section/category.
 * User can swipe to different categories or select them on the tab.
 * User can click on articles to read the article and search articles by keyword from this fragment
 */
public class ArticleFeedFragment extends Fragment implements ArticleFeedAdapter.OnItemClickListener {

  private static final String TAG = "ArticleFeedFragment";
  private RecyclerView recyclerView;
  private TopStories.Section section = TopStories.Section.HOME;

  protected Context context;
  protected SwipeRefreshLayout swipeContainer;
  protected ArticleFeedAdapter articleFeedAdapter;

  public static final String EXTRA_SECTION = "section";
  public static final String URL_EXTRA_KEY = "urlExtraKey";

  /**
   * Grabs the section the user is currently browsing to populate articles that are within that section
   * @param savedInstanceState
   */
  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Bundle bundle = getArguments();
    if (bundle != null) {
      String section = bundle.getString(EXTRA_SECTION);
      if (section != null) {
        this.section = TopStories.Section.valueOf(section);
      }
    }
  }

  /**
   * Sets the views for the fragment and implements the various methods
   *
   * @param inflater
   * @param container
   * @param savedInstanceState
   * @return
   */
  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_feed, container, false);
    recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
    swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);
    context = getContext();
    setArticleFeedAdapter();
    setFeedList();
    swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
      @Override
      public void onRefresh() {
        setFeedList(false);
      }
    });
    // Configure the refreshing colors
    swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
      android.R.color.holo_green_light,
      android.R.color.holo_orange_light,
      android.R.color.holo_red_light);

    return view;
  }

  /**
   * Sets the recycler view and adapter for recycler view into the fragment
   */
  protected void setArticleFeedAdapter() {
    articleFeedAdapter = new ArticleFeedAdapter(this);
    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context);
    recyclerView.setLayoutManager(layoutManager);
    recyclerView.setAdapter(articleFeedAdapter);
  }

  protected void setFeedList() {
    setFeedList(true);
  }

  /**
   * Calls the NY Times API and grabs the needed data depending on the query made.
   * This data is placed in the recycler view
   */
  protected void setFeedList(boolean cache) {
    if (CheckInternetConnection.isOnline(this.context)) {
      NewYorkTimes.getInstance().getTopStories(section).enqueue(new Callback<TopStories>() {
        @Override
        public void onResponse(Call<TopStories> call, Response<TopStories> response) {
          articleFeedAdapter.changeDataSet(response.body().getResults());
          swipeContainer.setRefreshing(false);
        }

        @Override
        public void onFailure(Call<TopStories> call, Throwable t) {
          Toast.makeText(context, "Could not retrieve Top Stories", Toast.LENGTH_SHORT).show();
          Log.w(TAG, "onFailure: ", t);
        }
      }, cache);
    } else {
      Toast.makeText(context, "No Internet Connection", Toast.LENGTH_SHORT).show();
    }
  }

  /**
   * Overrides onItemClick method for the recycler view
   * Sends the user to the article activity
   * Sends an intent with the article url to the article activity
   * The url is used to grab all of the article's details from the story object
   *
   * @param story
   */
  @Override
  public void onItemClick(StoryInterface story) {
    Intent articleActivityIntent = new Intent(context, ArticleActivity.class);
    articleActivityIntent.putExtra(URL_EXTRA_KEY, story.getUrl());
    startActivity(articleActivityIntent);
  }
}
