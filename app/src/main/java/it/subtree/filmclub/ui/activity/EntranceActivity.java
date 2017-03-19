package it.subtree.filmclub.ui.activity;

import android.app.ProgressDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import it.subtree.filmclub.R;
import it.subtree.filmclub.data.api.MovieDbApiClient;
import it.subtree.filmclub.data.api.MovieDbEndpointInterface;
import it.subtree.filmclub.data.db.MoviesContract;
import it.subtree.filmclub.data.model.Movie;
import it.subtree.filmclub.data.model.MoviesResponse;
import it.subtree.filmclub.ui.adapter.MoviesAdapter;
import it.subtree.filmclub.ui.layout.GridAutofitLayoutManager;
import it.subtree.filmclub.util.EndlessRecyclerOnScrollListener;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EntranceActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor>{

    public static String TAG = EntranceActivity.class.getSimpleName();
    private static final int FAVORITE_MOVIES_LOADER_ID = 0;

    private static final String SORT_MODE = "sort_mode";

    @BindView(R.id.rv_movies)
    protected RecyclerView mRecyclerView;

    @BindView(R.id.tv_empty_list)
    protected TextView mEmptyView;

    private ProgressDialog mLoading;

    private enum SortBy {
        POPULARITY(1),
        RATING(2),
        FAVORITES(3);

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
        ButterKnife.bind(this);

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new GridAutofitLayoutManager(EntranceActivity.this, (int) getResources().getDimension(R.dimen.cv_poster_width)));

        mSortBy = (savedInstanceState != null) ? SortBy.valueOf(savedInstanceState.getInt(SORT_MODE, SortBy.POPULARITY.getValue())) : SortBy.POPULARITY;

        if (mSortBy == SortBy.FAVORITES) {
            getSupportLoaderManager().initLoader(FAVORITE_MOVIES_LOADER_ID, null, this);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mSortBy == SortBy.FAVORITES) {
            getSupportLoaderManager().initLoader(FAVORITE_MOVIES_LOADER_ID, null, this);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(SORT_MODE, mSortBy.value);

        if (mSortBy == SortBy.FAVORITES) {
            getSupportLoaderManager().destroyLoader(FAVORITE_MOVIES_LOADER_ID);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dismissLoading();
        if (mSortBy == SortBy.FAVORITES) {
            getSupportLoaderManager().destroyLoader(FAVORITE_MOVIES_LOADER_ID);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_entrance, menu);

        MenuItem action_sort_by_popularity = menu.findItem(R.id.action_sort_by_popularity);
        MenuItem action_sort_by_rating = menu.findItem(R.id.action_sort_by_rating);
        MenuItem action_sort_by_favorites = menu.findItem(R.id.action_sort_by_favorites);

        switch (mSortBy) {
            case RATING:
                action_sort_by_rating.setChecked(true);
                break;
            case POPULARITY:
                action_sort_by_popularity.setChecked(true);
                break;
            default:
                action_sort_by_favorites.setChecked(true);
                break;
        }

        loadMovies(mSortBy);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        item.setChecked(true);
        int id = item.getItemId();
        SortBy oldSortBy = mSortBy;
        switch (id) {
            case R.id.action_sort_by_popularity:
                mSortBy = SortBy.POPULARITY;
                break;
            case R.id.action_sort_by_rating:
                mSortBy = SortBy.RATING;
                break;
            case R.id.action_sort_by_favorites:
                mSortBy = SortBy.FAVORITES;
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        if (oldSortBy != mSortBy) {
            if (oldSortBy == SortBy.FAVORITES) {
                getSupportLoaderManager().destroyLoader(FAVORITE_MOVIES_LOADER_ID);
            }
            loadMovies(mSortBy);
        }
        return true;
    }

    private void loadMovies(SortBy sortBy) {
        switch (sortBy) {
            case FAVORITES:
                getSupportLoaderManager().initLoader(FAVORITE_MOVIES_LOADER_ID, null, this);
                break;
            default:
                updateMovies(sortBy, 1);
        }
    }

    private void updateMovies(final SortBy sortBy, final int page) {
        if (MovieDbApiClient.API_KEY.isEmpty()) {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.error_api_key_missing), Toast.LENGTH_LONG).show();
            return;
        }

        mLoading = ProgressDialog.show(EntranceActivity.this, getResources().getString(R.string.loading_title), getResources().getString(R.string.loading_message));

        MovieDbEndpointInterface apiService =
                MovieDbApiClient.getClient().create(MovieDbEndpointInterface.class);

        Call<MoviesResponse> call = null;
        switch (sortBy) {
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
                    mRecyclerView.getAdapter().registerAdapterDataObserver(new MovieDataObserver());
                    mRecyclerView.getAdapter().notifyDataSetChanged();
                    mRecyclerView.clearOnScrollListeners();
                    mRecyclerView.addOnScrollListener(new EndlessRecyclerOnScrollListener((GridLayoutManager) mRecyclerView.getLayoutManager()) {
                        @Override
                        public void onLoadMore(int current_page) {
                            Log.i(TAG,"Load page " + current_page);
                            updateMovies(sortBy, current_page);
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

                View.OnClickListener reloadOnClickListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        updateMovies(sortBy, page);
                    }
                };

                Snackbar.make(mRecyclerView, getResources().getString(R.string.error_connection), Snackbar.LENGTH_INDEFINITE)
                        .setAction(getResources().getString(R.string.retry), reloadOnClickListener)
                        .show();
            }
        });

    }

    private void dismissLoading() {
        if (mLoading != null && mLoading.isShowing()) {
            mLoading.dismiss();
        }
    }

    // LoaderCallbacks

    @Override
    public Loader<Cursor> onCreateLoader(int id, final Bundle loaderArgs) {

        return new AsyncTaskLoader<Cursor>(this) {

            // Initialize a Cursor, this will hold all the task data
            Cursor mFavorites = null;

            // onStartLoading() is called when a loader first starts loading data
            @Override
            protected void onStartLoading() {
                if (mFavorites != null) {
                    // Delivers any previously loaded data immediately
                    deliverResult(mFavorites);
                } else {
                    // Force a new load
                    forceLoad();
                }
            }

            // loadInBackground() performs asynchronous loading of data
            @Override
            public Cursor loadInBackground() {
                try {
                    return getContentResolver().query(MoviesContract.MovieEntry.CONTENT_URI,
                            null,
                            null,
                            null,
                            MoviesContract.MovieEntry._ID);

                } catch (Exception e) {
                    Log.e(TAG, "Failed to asynchronously load data.");
                    e.printStackTrace();
                    return null;
                }
            }

            // deliverResult sends the result of the load, a Cursor, to the registered listener
            public void deliverResult(Cursor data) {
                mFavorites = data;
                super.deliverResult(data);
            }
        };

    }


    /**
     * Called when a previously created loader has finished its load.
     *
     * @param loader The Loader that has finished.
     * @param data The data generated by the Loader.
     */
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // Update the data that the adapter uses to create ViewHolders
        if (mRecyclerView.getAdapter() == null) {
            mRecyclerView.setAdapter(new MoviesAdapter(data));
            mRecyclerView.getAdapter().registerAdapterDataObserver(new MovieDataObserver());
        } else {
            ((MoviesAdapter)mRecyclerView.getAdapter()).swapCursor(data);
            mRecyclerView.requestLayout();
        }
    }


    /**
     * Called when a previously created loader is being reset, and thus
     * making its data unavailable.
     * onLoaderReset removes any references this activity had to the loader's data.
     *
     * @param loader The Loader that is being reset.
     */
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if (mRecyclerView.getAdapter() != null) {
            ((MoviesAdapter)mRecyclerView.getAdapter()).swapCursor(null);
        }
    }

    private class MovieDataObserver extends RecyclerView.AdapterDataObserver {
        @Override
        public void onChanged() {
            super.onChanged();
            if (mRecyclerView.getAdapter().getItemCount() == 0) {
                mRecyclerView.setVisibility(View.GONE);
                mEmptyView.setVisibility(View.VISIBLE);
            } else {
                mRecyclerView.setVisibility(View.VISIBLE);
                mEmptyView.setVisibility(View.GONE);
            }
        }
    }
}
