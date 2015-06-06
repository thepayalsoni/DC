package com.fl.ps.discountcard;

import java.util.ArrayList;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.fl.ps.dataholders.CategoryData;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class CategoryFragment extends Fragment {
	private static final String ARG_CATEGORY_NUMBER = "section_number";

	DCListAdapter adapter;
	RecyclerView mRecyclerView;
	static ArrayList<CategoryData> deals;
	
	
	/**
	 * Returns a new instance of this fragment for the given category number.
	 */
	public static CategoryFragment newInstance(int categoryNumber, ArrayList<CategoryData> categoryData) {
		CategoryFragment fragment = new CategoryFragment();
		Bundle args = new Bundle();
		args.putInt(ARG_CATEGORY_NUMBER, categoryNumber);
		fragment.setArguments(args);
		deals = categoryData;
		return fragment;
	}

	public CategoryFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		Toast.makeText(getActivity(), "Fragmnet" + getArguments().getInt(ARG_CATEGORY_NUMBER), Toast.LENGTH_LONG)
				.show();

		View v = inflater.inflate(R.layout.fragment_main, container, false);
		mRecyclerView = (RecyclerView) v.findViewById(R.id.list);
		mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
		mRecyclerView.setItemAnimator(new DefaultItemAnimator());

		adapter = new DCListAdapter(getActivity().getApplicationContext());
		
		mRecyclerView.setAdapter(adapter);
		return v;
	}

	
	// //////////////////////////////////////////////////////////////

	class DCListAdapter extends RecyclerView.Adapter<ViewHolder> {

		LayoutInflater inflater;
		//ArrayList<Database> deals = new ArrayList<Database>();
		Context context;
		Bitmap bm;
		ImageLoader imloader;

		DCListAdapter(Context context) {
			this.context = context;
			inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			//DatabaseHelper helper = new DatabaseHelper(getActivity());
			//deals = new ArrayList<Database>();
			/*deals = helper.getAllDeals(helper.getAllCategory().size() > 0 ? helper.getAllCategory().get(
					getArguments().getInt(ARG_CATEGORY_NUMBER)) : "clothing");
			Log.v("deals", "size " + deals.size());*/

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
			//Log.v("deals", Uri.fromFile(new File(deals.get(position).getPhotoPath())).toString());
			holder.name.setText(deals.get(position).getName());

			imloader.displayImage(deals.get(position).getImageUrl(), holder.image);

			holder.location.setText(deals.get(position).getAddress());
			
			holder.rating.setRating((Float.parseFloat(deals.get(position).getRating())/100)*5);

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
			rating = (RatingBar)itemView.findViewById(R.id.rating);
			
			RelativeLayout.LayoutParams rlp = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT,
					LayoutParams.WRAP_CONTENT);

			image.setLayoutParams(rlp);
			image.setAdjustViewBounds(true);
			image.setScaleType(ImageView.ScaleType.FIT_CENTER);
		}

	}
	
/*	private void downloadImages(String path) {

		try {
			if (path == null) {
				return;
			}
			final DisplayImageOptions optionsCard = new DisplayImageOptions.Builder()
					.cacheInMemory(true).cacheOnDisc(true).build();

			String currentItem = path;

			if (currentItem != null) {
				imageLoader.loadImage(currentItem, optionsCard,
						new ImageLoadingListener() {
							@Override
							public void onLoadingStarted(String arg0, View arg1) {

							}

							@Override
							public void onLoadingFailed(String arg0, View arg1,
									FailReason arg2) {
								Log.e("Image loading error", arg2.toString());
							}

							@Override
							public void onLoadingComplete(String arg0,
									View arg1, Bitmap bmp) {

								HashMap<String, Object> slotItemMap = new HashMap<String, Object>();
								slotItemMap.put("image",
										new SoftReference<Bitmap>(bmp));
								slotItemMap.put("id", id);

								slotImages.add(slotItemMap);

							}

							@Override
							public void onLoadingCancelled(String arg0,
									View arg1) {

							}
						});
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}*/
}
