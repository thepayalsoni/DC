package com.fl.ps.discountcard;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.FragmentManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.fl.ps.adapters.NavDrawerListAdapter;
import com.fl.ps.broadcastreceivers.DeleteDataReceiver;
import com.fl.ps.database.DatabaseHelper;
import com.fl.ps.dataholders.NavDrawerItem;
import com.fl.ps.parsing.Categories;
import com.fl.ps.requests.FetchCategoryGetRequest;
import com.fl.ps.requests.MyVolley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

@SuppressWarnings("deprecation")
public class MainActivity extends Activity {
	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;
	private ArrayList<Categories> arrCategories;
	private CharSequence mDrawerTitle;
	private CharSequence mTitle;
	private ArrayList<NavDrawerItem> navDrawerItems;
	private ProgressDialog mDialog;
	private NavDrawerListAdapter adapter;

	private DatabaseHelper dbHelper;

	SharedPreferences sharedpreferences;

	private PendingIntent pendingIntent;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mTitle = mDrawerTitle = getTitle();
		navDrawerItems = new ArrayList<NavDrawerItem>();

		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.list_slidermenu);

		mDrawerList.setOnItemClickListener(new SlideMenuClickListener());

		sharedpreferences = getSharedPreferences(getString(R.string.PREFS_DC), Context.MODE_PRIVATE);

		if (sharedpreferences.getString(getString(R.string.PREFS_DC_KEY_TABLE_EXISTS), "false").equals("true")) {

			dbHelper = new DatabaseHelper(getApplicationContext());
			dbHelper.getReadableDatabase();
			for (int i = 0; i < dbHelper.getAllCategoryFromDB().size(); i++) {

				navDrawerItems.add(new NavDrawerItem(dbHelper.getAllCategoryFromDB().get(i)));

			}

			adapter = new NavDrawerListAdapter(getApplicationContext(), navDrawerItems);
			mDrawerList.setAdapter(adapter);
		}

		else {
			mDialog = new ProgressDialog(MainActivity.this);
			mDialog.setCancelable(false);
			mDialog.setMessage("Wait...");
			mDialog.show();
			getCategoriesFromServer();
		}

		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);

		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.drawable.ic_menu_black_24dp, // nav

				R.string.app_name, R.string.app_name) {
			public void onDrawerClosed(View view) {
				getActionBar().setTitle(mTitle);

				invalidateOptionsMenu();
			}

			public void onDrawerOpened(View drawerView) {
				getActionBar().setTitle(mDrawerTitle);

				invalidateOptionsMenu();
			}
		};
		mDrawerLayout.setDrawerListener(mDrawerToggle);
		Intent alarmIntent = new Intent(MainActivity.this, DeleteDataReceiver.class);
		pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0, alarmIntent, 0);

		deleteAt10();

	}

	public void deleteAt10() {
		AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		int interval = 600000;
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(System.currentTimeMillis());
		calendar.set(Calendar.HOUR_OF_DAY, 13);
		calendar.set(Calendar.MINUTE, 57);

		manager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), interval, pendingIntent);
	}

	private class SlideMenuClickListener implements ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

			displayView(position);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}

		switch (item.getItemId()) {
		case R.id.action_settings:
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {

		boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
		menu.findItem(R.id.action_settings).setVisible(!drawerOpen);
		return super.onPrepareOptionsMenu(menu);
	}

	private void displayView(int position) {

		FragmentManager fragmentManager = getFragmentManager();

		mDrawerList.setItemChecked(position, true);
		mDrawerList.setSelection(position);
		if (dbHelper.getAllCategoryFromDB() != null && dbHelper.getAllCategoryFromDB().size() > 0) {
			dbHelper = new DatabaseHelper(getApplicationContext());
			dbHelper.getReadableDatabase();
			String title = dbHelper.getAllCategoryFromDB().get(position);
			setTitle(title);

			fragmentManager.beginTransaction()
					.replace(R.id.frame_container, CategoryFragment.newInstance(position, title)).commit();

		}
		mDrawerLayout.closeDrawer(mDrawerList);

	}

	@Override
	public void setTitle(CharSequence title) {
		mTitle = title;
		getActionBar().setTitle(mTitle);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);

		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);

		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	public void getCategoriesFromServer() {

		Listener<String> onSuccess = new Listener<String>() {

			@Override
			public void onResponse(String imagesString) {
				mDialog.dismiss();

				Type typeCategoryItemDetails = new TypeToken<ArrayList<Categories>>() {
				}.getType();

				arrCategories = new Gson().fromJson(imagesString, typeCategoryItemDetails);

				dbHelper = new DatabaseHelper(getApplicationContext(), arrCategories);
				dbHelper.getReadableDatabase();

				if (dbHelper.getAllCategoryFromDB().size() > 0) {

				} else {

					dbHelper.allCategories(arrCategories);
				}

				for (int i = 0; i < dbHelper.getAllCategoryFromDB().size(); i++) {

					navDrawerItems.add(new NavDrawerItem(dbHelper.getAllCategoryFromDB().get(i)));

				}

				adapter = new NavDrawerListAdapter(getApplicationContext(), navDrawerItems);
				mDrawerList.setAdapter(adapter);

			}
		};

		ErrorListener onError = new ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError arg0) {
				mDialog.dismiss();
				/*
				 * dismissProgressDialog();
				 * showAlertDialog(FillerUpHomeActivity.this, null, new
				 * CustomVolleyError(arg0).getErrorMessage(), new
				 * DialogInterface.OnClickListener() {
				 * 
				 * @Override public void onClick(DialogInterface dialog, int
				 * which) { dialog.dismiss(); finish(); }
				 * 
				 * });
				 */

				Log.v("error", new VolleyError(arg0).getMessage());

				Toast.makeText(getApplicationContext(), "Failed to parse " + new VolleyError(arg0).getMessage(),
						Toast.LENGTH_LONG).show();
			}
		};

		RequestQueue queue = MyVolley.getRequestQueue(getApplicationContext());
		final FetchCategoryGetRequest lRequest = new FetchCategoryGetRequest(getApplicationContext(),
				getString(R.string.API_CATEGORIES), onSuccess, onError);
		queue.add(lRequest);
	}

	public ArrayList<Categories> getCategries() {
		return arrCategories;
	}

	public void closeActivity() {
		System.exit(0);
		finish();
	}

}