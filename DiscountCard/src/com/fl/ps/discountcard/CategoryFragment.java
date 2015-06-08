package com.fl.ps.discountcard;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Locale;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
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
import com.fl.ps.requests.FetchCategoryRequest;
import com.fl.ps.requests.MyVolley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class CategoryFragment extends Fragment {
	private static final String ARG_CATEGORY_NUMBER = "section_number";
	private static final String ARG_CATEGORY_TITLE = "title";

	DCListAdapter adapter;
	RecyclerView mRecyclerView;
	static ArrayList<CategoryItems> deals;
	ProgressDialog mDialog;
	private DatabaseHelper dbHelper;

	/**
	 * Returns a new instance of this fragment for the given category number.
	 */
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
		// Inflate the layout for this fragment
		Toast.makeText(getActivity(),
				"Fragmnet" + getArguments().getInt(ARG_CATEGORY_NUMBER) + getArguments().getString(ARG_CATEGORY_TITLE),
				Toast.LENGTH_LONG).show();

		View v = inflater.inflate(R.layout.fragment_main, container, false);
		mRecyclerView = (RecyclerView) v.findViewById(R.id.list);
		mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
		mRecyclerView.setItemAnimator(new DefaultItemAnimator());

		dbHelper = new DatabaseHelper(getActivity().getApplicationContext());
		dbHelper.getReadableDatabase();
		
		
		DatabaseHelper helper = new DatabaseHelper(getActivity());
		deals = new ArrayList<CategoryItems>();
		deals = helper.getAllDeals(getArguments().getString(ARG_CATEGORY_TITLE).replace(" ", "_").toLowerCase(Locale.getDefault()).trim());

		
		if(deals.size()>0)
		{

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

	// //////////////////////////////////////////////////////////////

	class DCListAdapter extends RecyclerView.Adapter<ViewHolder> {

		LayoutInflater inflater;
		// ArrayList<Database> deals = new ArrayList<Database>();
		Context context;
		Bitmap bm;
		ImageLoader imloader;

		DCListAdapter(Context context) {
			this.context = context;
			inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			/*DatabaseHelper helper = new DatabaseHelper(getActivity());
			deals = new ArrayList<CategoryItems>();
			deals = helper.getAllDeals(helper.getAllCategoryFromDB().size() > 0 ? helper.getAllCategoryFromDB().get(
					getArguments().getInt(ARG_CATEGORY_NUMBER)) : "clothing");*/
			Log.v("deals", "size " + deals.size());

			imloader = ImageLoader.getInstance();
			imloader.init(ImageLoaderConfiguration.createDefault(context));

		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public int getItemCount() {

			return deals != null ? deals.size() : 0;
		}

		@Override
		public void onBindViewHolder(ViewHolder holder, int position) {
			
			holder.name.setText(deals.get(position).getName());

			imloader.displayImage(deals.get(position).getImageUrl(), holder.image);

			holder.location.setText(deals.get(position).getAddress());

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
		TextView name, location;
		RatingBar rating;

		public ViewHolder(View itemView) {
			super(itemView);
			name = (TextView) itemView.findViewById(R.id.item_name);
			location = (TextView) itemView.findViewById(R.id.item_location);
			image = (ImageView) itemView.findViewById(R.id.item_image);
			rating = (RatingBar) itemView.findViewById(R.id.rating);

			RelativeLayout.LayoutParams rlp = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT,
					LayoutParams.WRAP_CONTENT);

			image.setLayoutParams(rlp);
			image.setAdjustViewBounds(true);
			image.setScaleType(ImageView.ScaleType.FIT_CENTER);
		}

	}

	/*
	 * private void downloadImages(String path) {
	 * 
	 * try { if (path == null) { return; } final DisplayImageOptions optionsCard
	 * = new DisplayImageOptions.Builder()
	 * .cacheInMemory(true).cacheOnDisc(true).build();
	 * 
	 * String currentItem = path;
	 * 
	 * if (currentItem != null) { imageLoader.loadImage(currentItem,
	 * optionsCard, new ImageLoadingListener() {
	 * 
	 * @Override public void onLoadingStarted(String arg0, View arg1) {
	 * 
	 * }
	 * 
	 * @Override public void onLoadingFailed(String arg0, View arg1, FailReason
	 * arg2) { Log.e("Image loading error", arg2.toString()); }
	 * 
	 * @Override public void onLoadingComplete(String arg0, View arg1, Bitmap
	 * bmp) {
	 * 
	 * HashMap<String, Object> slotItemMap = new HashMap<String, Object>();
	 * slotItemMap.put("image", new SoftReference<Bitmap>(bmp));
	 * slotItemMap.put("id", id);
	 * 
	 * slotImages.add(slotItemMap);
	 * 
	 * }
	 * 
	 * @Override public void onLoadingCancelled(String arg0, View arg1) {
	 * 
	 * } }); }
	 * 
	 * } catch (Exception e) { e.printStackTrace(); }
	 * 
	 * }
	 */

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

				Toast.makeText(getActivity(), "Failed to parse " + new VolleyError(arg0).getMessage(), 1).show();
			}
		};

		RequestQueue queue = MyVolley.getRequestQueue(getActivity().getApplicationContext());
		final FetchCategoryRequest lRequest = new FetchCategoryRequest(getActivity().getApplicationContext(),
				getString(R.string.API_CATEGORY_DETAILS) + title, onSuccess, onError);
		queue.add(lRequest);
	}
}
