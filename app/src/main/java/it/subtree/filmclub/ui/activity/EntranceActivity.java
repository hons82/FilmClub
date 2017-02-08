package it.subtree.filmclub.ui.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.subtree.filmclub.R;
import it.subtree.filmclub.data.api.MovieDbApiClient;
import it.subtree.filmclub.data.api.MovieDbEndpointInterface;
import it.subtree.filmclub.data.model.Movie;
import it.subtree.filmclub.data.model.MoviesResponse;
import it.subtree.filmclub.ui.adapter.MoviesAdapter;
import it.subtree.filmclub.ui.layout.GridAutofitLayoutManager;
import it.subtree.filmclub.util.EndlessRecyclerOnScrollListener;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EntranceActivity extends AppCompatActivity {
    public static String TAG = EntranceActivity.class.getSimpleName();
    private static final String SORT_MODE = "sort_mode";

    private RecyclerView mRecyclerView;
    private ProgressDialog mLoading;

    private enum SortBy {
        POPULARITY(1),
        RATING(2);

        private int value;
        private static Map map = new HashMap<>();

        private SortBy(int value) {
            this.value = value;
        }

        static {
            for (SortBy pageType : SortBy.values()) {
                map.put(pageType.value, pageType);
            }
        }

        public static SortBy valueOf(int pageType) {
            return (SortBy) map.get(pageType);
        }

        public int getValue() {
            return value;
        }
    }

    private SortBy mSortBy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_entrance);

        mRecyclerView = (RecyclerView) findViewById(R.id.rv_movies);
        mRecyclerView.setHasFixedSize(true);

        mRecyclerView.setLayoutManager(new GridAutofitLayoutManager(EntranceActivity.this, (int) getResources().getDimension(R.dimen.cv_poster_width)));

        mSortBy = (savedInstanceState != null) ? SortBy.valueOf(savedInstanceState.getInt(SORT_MODE, SortBy.POPULARITY.getValue())) : SortBy.POPULARITY;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(SORT_MODE, mSortBy.value);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dismissLoading();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_entrance, menu);

        MenuItem action_sort_by_popularity = menu.findItem(R.id.action_sort_by_popularity);
        MenuItem action_sort_by_rating = menu.findItem(R.id.action_sort_by_rating);

        switch (mSortBy) {
            case RATING:
                action_sort_by_rating.setChecked(true);
                break;
            default:
                action_sort_by_popularity.setChecked(true);
        }

        loadMovies(mSortBy);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        item.setChecked(true);
        int id = item.getItemId();
        switch (id) {
            case R.id.action_sort_by_popularity:
                mSortBy = SortBy.POPULARITY;
                break;
            case R.id.action_sort_by_rating:
                mSortBy = SortBy.RATING;
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        loadMovies(mSortBy);
        return true;
    }

    private void loadMovies(SortBy sortOrder) {
        updateMovies(sortOrder, 1);
    }

    private void updateMovies(SortBy sortOrder, final int page) {
        if (MovieDbApiClient.API_KEY.isEmpty()) {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.error_api_key_missing), Toast.LENGTH_LONG).show();
            return;
        }

        mLoading = ProgressDialog.show(EntranceActivity.this, getResources().getString(R.string.loading_title), getResources().getString(R.string.loading_message));

        MovieDbEndpointInterface apiService =
                MovieDbApiClient.getClient().create(MovieDbEndpointInterface.class);

        Call<MoviesResponse> call = null;
        switch (sortOrder) {
            case RATING:
                call = apiService.getTopRatedMovies(MovieDbApiClient.API_KEY, page);
                break;
            default:
                call = apiService.getPopularMovies(MovieDbApiClient.API_KEY, page);
        }
        call.enqueue(new Callback<MoviesResponse>() {
            @Override
            public void onResponse(Call<MoviesResponse> call, Response<MoviesResponse> response) {
                int statusCode = response.code();
                List<Movie> movies = null;
                if (statusCode == 200) {
                    movies = response.body().getResults();
                } else {
                    Log.i(TAG, "StatusCode " + statusCode);
                    movies = new ArrayList<Movie>();
                }
                if (page == 1 || mRecyclerView.getAdapter() == null) {
                    mRecyclerView.setAdapter(new MoviesAdapter(movies));
                    mRecyclerView.clearOnScrollListeners();
                    mRecyclerView.addOnScrollListener(new EndlessRecyclerOnScrollListener((GridLayoutManager) mRecyclerView.getLayoutManager()) {
                        @Override
                        public void onLoadMore(int current_page) {
                            Log.i(TAG,"Load page " + current_page);
                            updateMovies(mSortBy, current_page);
                        }
                    });
                } else {
                    ((MoviesAdapter)mRecyclerView.getAdapter()).addMovies(movies);
                    mRecyclerView.requestLayout();
                }
                dismissLoading();
            }

            @Override
            public void onFailure(Call<MoviesResponse> call, Throwable t) {
                Log.e(TAG, t.toString());
                dismissLoading();
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.error_connection), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void dismissLoading() {
        if (mLoading != null && mLoading.isShowing()) {
            mLoading.dismiss();
        }
    }

}
