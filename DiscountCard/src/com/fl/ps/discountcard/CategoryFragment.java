package com.fl.ps.discountcard;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.fl.ps.database.DatabaseHelper;
import com.fl.ps.parsing.CategoryItems;
import com.fl.ps.requests.FetchCategoryGetRequest;
import com.fl.ps.requests.GPSTracker;
import com.fl.ps.requests.MyVolley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class CategoryFragment extends Fragment {
	private static final String ARG_CATEGORY_NUMBER = "section_number";
	private static final String ARG_CATEGORY_TITLE = "title";

	DCListAdapter adapter;
	RecyclerView mRecyclerView;
	static ArrayList<CategoryItems> deals;
	ProgressDialog mDialog;
	private GPSTracker gpsTracker=null;
	private DatabaseHelper dbHelper;
	double latitude=0.0,longitude=0.0;

	public static CategoryFragment newInstance(int categoryNumber, String title) {
		CategoryFragment fragment = new CategoryFragment();
		Bundle args = new Bundle();
		args.putInt(ARG_CATEGORY_NUMBER, categoryNumber);
		args.putString(ARG_CATEGORY_TITLE, title);
		fragment.setArguments(args);

		return fragment;
	}

	public CategoryFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	
		View v = inflater.inflate(R.layout.fragment_main, container, false);
		mRecyclerView = (RecyclerView) v.findViewById(R.id.list);
		mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
		mRecyclerView.setItemAnimator(new DefaultItemAnimator());

		dbHelper = new DatabaseHelper(getActivity().getApplicationContext());
		dbHelper.getReadableDatabase();
		
		
		DatabaseHelper helper = new DatabaseHelper(getActivity());
		deals = new ArrayList<CategoryItems>();
		deals = helper.getAllDeals(getArguments().getString(ARG_CATEGORY_TITLE).replace(" ", "_").toLowerCase(Locale.getDefault()).trim());

		gpsTracker = new GPSTracker(getActivity());
		if(deals.size()>0)
		{
			if(gpsTracker.canGetLocation())
			{
				 latitude = gpsTracker.getLatitude();
                 longitude = gpsTracker.getLongitude();
                Toast.makeText(getActivity().getApplicationContext(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
			}
			else
			{
				gpsTracker.showSettingsAlert();
			}

			adapter = new DCListAdapter(getActivity().getApplicationContext());

			mRecyclerView.setAdapter(adapter);
		}
		else
		{
			mDialog = new ProgressDialog(getActivity());
			mDialog.setCancelable(false);
			mDialog.setMessage("Wait...");
			mDialog.show();

			getCategoriesDataFromServer(getArguments().getString(ARG_CATEGORY_TITLE));
		}
		
		

		return v;
	}

	

	class DCListAdapter extends RecyclerView.Adapter<ViewHolder> {

		LayoutInflater inflater;
	
		Context context;
		Bitmap bm;
		ImageLoader imloader;

		DCListAdapter(Context context) {
			this.context = context;
			inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			imloader = ImageLoader.getInstance();
			imloader.init(ImageLoaderConfiguration.createDefault(context));
			
			Location selected_location=new Location("current");
		    selected_location.setLatitude(latitude);
		    selected_location.setLongitude(longitude);
		   
			
			for(int i = 0 ;i< deals.size();i++)
			{
				 Location near_locations=new Location("loc");
				    near_locations.setLatitude((deals.get(i).getLatitude()));
				    near_locations.setLongitude((deals.get(i).getLongitude()));
				    
				    double distance=selected_location.distanceTo(near_locations);
				    
				    deals.get(i).setDistance(distance);
			}
			
			Collections.sort(deals, new CategoryItems());
			

		}

		@Override
		public long getItemId(int position) {
			
			return 0;
		}

		@Override
		public int getItemCount() {

			return deals != null ? deals.size() : 0;
		}

		@SuppressWarnings("deprecation")
		@Override
		public void onBindViewHolder(ViewHolder holder, int position) {
			
			holder.name.setText(deals.get(position).getName());
			
			  File file = imloader.getDiscCache().get(deals.get(position).getImageUrl());  
			  if (!file.exists()) {  
			       DisplayImageOptions options = new DisplayImageOptions.Builder()  
			       .cacheOnDisc()  
			       .build();  
			       imloader.displayImage(deals.get(position).getImageUrl(), holder.image, options);  
			  }  
			  else {  
				  holder.image.setImageURI(Uri.parse(file.getAbsolutePath()));  
			  }  

			holder.location.setText(deals.get(position).getAddress());
			holder.discount.setText(deals.get(position).getDistance()+"");

			holder.rating.setRating((Float.parseFloat(deals.get(position).getRating()) / 100) * 5);

		}

		@Override
		public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
			View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.category_item, viewGroup,
					false);
			return new ViewHolder(itemView);

		}

	}
	
	

	public static class ViewHolder extends RecyclerView.ViewHolder {

		ImageView image;
		TextView name, location,discount;
		RatingBar rating;
		

		public ViewHolder(View itemView) {
			super(itemView);
			name = (TextView) itemView.findViewById(R.id.item_name);
			location = (TextView) itemView.findViewById(R.id.item_location);
			discount = (TextView) itemView.findViewById(R.id.item_discount);
			image = (ImageView) itemView.findViewById(R.id.item_image);
			rating = (RatingBar) itemView.findViewById(R.id.rating);

			RelativeLayout.LayoutParams rlp = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT,
					LayoutParams.WRAP_CONTENT);

			image.setLayoutParams(rlp);
			image.setAdjustViewBounds(true);
			image.setScaleType(ImageView.ScaleType.FIT_CENTER);
		}

	}

	

	public void getCategoriesDataFromServer(String title) {

		Listener<String> onSuccess = new Listener<String>() {

			@Override
			public void onResponse(String imagesString) {
				mDialog.dismiss();

				Type typeCategoryItemDetails = new TypeToken<ArrayList<CategoryItems>>() {
				}.getType();

				deals = new Gson().fromJson(imagesString, typeCategoryItemDetails);

				dbHelper.writeToTable(deals);

				adapter = new DCListAdapter(getActivity().getApplicationContext());

				mRecyclerView.setAdapter(adapter);

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

				Toast.makeText(getActivity(), "Failed to parse " + new VolleyError(arg0).getMessage(), Toast.LENGTH_LONG).show();
			}
		};

		RequestQueue queue = MyVolley.getRequestQueue(getActivity().getApplicationContext());
		final FetchCategoryGetRequest lRequest = new FetchCategoryGetRequest(getActivity().getApplicationContext(),
				getString(R.string.API_CATEGORY_DETAILS) + title, onSuccess, onError);
		queue.add(lRequest);
	}
}
