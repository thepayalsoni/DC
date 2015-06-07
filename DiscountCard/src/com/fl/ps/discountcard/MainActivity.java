package com.fl.ps.discountcard;

import java.lang.reflect.Type;
import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.ProgressDialog;
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
import com.fl.ps.database.DatabaseHelper;
import com.fl.ps.dataholders.NavDrawerItem;
import com.fl.ps.parsing.Categories;
import com.fl.ps.requests.FetchCategoryRequest;
import com.fl.ps.requests.MyVolley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

@SuppressWarnings("deprecation")
public class MainActivity extends Activity {
	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;
	private ArrayList<Categories> arrCategories;

	// nav drawer title
	private CharSequence mDrawerTitle;

	// used to store app title
	private CharSequence mTitle;

	// slide menu items

	private ArrayList<NavDrawerItem> navDrawerItems;

	ProgressDialog mDialog;
	NavDrawerListAdapter adapter;

	private DatabaseHelper dbHelper;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mTitle = mDrawerTitle = getTitle();

		mDialog = new ProgressDialog(MainActivity.this);
		mDialog.setCancelable(false);
		mDialog.setMessage("Wait...");
		mDialog.show();
		getCategoriesFromServer();

		// load slide menu items

		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.list_slidermenu);

		mDrawerList.setOnItemClickListener(new SlideMenuClickListener());

		// setting the nav drawer list adapter

		navDrawerItems = new ArrayList<NavDrawerItem>();

		

		// enabling action bar app icon and behaving it as toggle button
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);

		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.drawable.ic_menu_black_24dp, // nav
																								// menu
																								// toggle
																								// icon
				R.string.app_name, // nav drawer open - description for
									// accessibility
				R.string.app_name // nav drawer close - description for
									// accessibility
		) {
			public void onDrawerClosed(View view) {
				getActionBar().setTitle(mTitle);
				// calling onPrepareOptionsMenu() to show action bar icons
				invalidateOptionsMenu();
			}

			public void onDrawerOpened(View drawerView) {
				getActionBar().setTitle(mDrawerTitle);
				// calling onPrepareOptionsMenu() to hide action bar icons
				invalidateOptionsMenu();
			}
		};
		mDrawerLayout.setDrawerListener(mDrawerToggle);

		if (savedInstanceState == null) {
			// on first time display view for first nav item
			displayView(0);
		}
		
		
	}

	/**
	 * Slide menu item click listener
	 * */
	private class SlideMenuClickListener implements ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			// display view for selected nav drawer item
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
		// toggle nav drawer on selecting action bar app icon/title
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		// Handle action bar actions click
		switch (item.getItemId()) {
		case R.id.action_settings:
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	/***
	 * Called when invalidateOptionsMenu() is triggered
	 */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// if nav drawer is opened, hide the action items
		boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
		menu.findItem(R.id.action_settings).setVisible(!drawerOpen);
		return super.onPrepareOptionsMenu(menu);
	}

	/**
	 * Diplaying fragment view for selected nav drawer list item
	 * */
	private void displayView(int position) {
		
		

		FragmentManager fragmentManager = getFragmentManager();
		
		// update selected item and title, then close the drawer
		mDrawerList.setItemChecked(position, true);
		mDrawerList.setSelection(position);
		if (arrCategories != null && arrCategories.size() != 0)
		{
			dbHelper = new DatabaseHelper(getApplicationContext());
			dbHelper.getReadableDatabase();
			String title = dbHelper.getAllCategoryFromDB().get(position);
			setTitle(title);
			
			fragmentManager.beginTransaction().replace(R.id.frame_container, CategoryFragment.newInstance(position, title))
			.commit();

		}
		mDrawerLayout.closeDrawer(mDrawerList);

	}

	@Override
	public void setTitle(CharSequence title) {
		mTitle = title;
		getActionBar().setTitle(mTitle);
	}

	/**
	 * When using the ActionBarDrawerToggle, you must call it during
	 * onPostCreate() and onConfigurationChanged()...
	 */

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// Pass any configuration change to the drawer toggls
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
				// downloadSlotImages(arrCategories);

				dbHelper = new DatabaseHelper(getApplicationContext(),arrCategories);
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

				Toast.makeText(getApplicationContext(), "Failed to parse " + new VolleyError(arg0).getMessage(), 1)
						.show();
			}
		};

		RequestQueue queue = MyVolley.getRequestQueue(getApplicationContext());
		final FetchCategoryRequest lRequest = new FetchCategoryRequest(getApplicationContext(),getString(R.string.API_CATEGORIES), onSuccess, onError);
		queue.add(lRequest);
	}

	public ArrayList<Categories> getCategries()
	{
		return arrCategories;
	}
	
	
}