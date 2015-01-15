package com.example.rottentomatoes;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;

public class MainActivity extends Activity {

	private ListView lvMovies;
	private BoxOfficeMoviesAdapter adapterMovies;
	RottenTomatoesClient client;
	public static final String MOVIE_DETAIL_KEY = "movie";


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		lvMovies = (ListView) findViewById(R.id.lvMovies);
		ArrayList<BoxOfficeMovie> aMovies = new ArrayList<BoxOfficeMovie>();
		adapterMovies = new BoxOfficeMoviesAdapter(this, aMovies);
		lvMovies.setAdapter(adapterMovies);
		setupMovieSelectedListener();
	}

	public void setupMovieSelectedListener() {
		lvMovies.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View item,
					int position, long rowId) {
				// Launch the detail view passing movie as an extra
				Intent i = new Intent(MainActivity.this,
						BoxOfficeDetailActivity.class);
				i.putExtra(MOVIE_DETAIL_KEY, adapterMovies.getItem(position));
				startActivity(i);
			}
		});
	}

	// Executes an API call to the box office endpoint, parses the results
	// Converts them into an array of movie objects and adds them to the adapter
	private void fetchBoxOfficeMovies() {
		client = new RottenTomatoesClient();
		client.getBoxOfficeMovies(new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode,
					org.apache.http.Header[] headers, JSONObject response) {
				JSONArray items = null;
				try {
					// Get the movies json array
					items = response.getJSONArray("movies");
					// Parse json array into array of model objects
					ArrayList<BoxOfficeMovie> movies = BoxOfficeMovie
							.fromJson(items);
					// Load model objects into the adapter
					for (BoxOfficeMovie movie : movies) {
						adapterMovies.add(movie);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});
	}

	private void checkNetwork() {
		Toast.makeText(this, isNetworkAvailable().toString(),
				Toast.LENGTH_SHORT).show();

	}

	private Boolean isNetworkAvailable() {
		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager
				.getActiveNetworkInfo();
		return activeNetworkInfo != null
				&& activeNetworkInfo.isConnectedOrConnecting();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
