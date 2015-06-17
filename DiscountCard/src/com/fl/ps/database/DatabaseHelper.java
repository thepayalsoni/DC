package com.fl.ps.database;

import java.util.ArrayList;
import java.util.Locale;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.PointF;
import android.util.Log;

import com.fl.ps.discountcard.R;
import com.fl.ps.parsing.Categories;
import com.fl.ps.parsing.CategoryItems;

public class DatabaseHelper extends SQLiteOpenHelper {
	String TAG = "DATABASEHELPER";

	private String CATEGORY = "col_category";
	private static int DATABASE_VERSION = 1;

	private ArrayList<Database> db_data;
	private Context mContext;

	SharedPreferences sharedpreferences;

	public DatabaseHelper(Context context, ArrayList<Categories> catageor) {
		super(context, context.getString(R.string.DATABASE_NAME), null, DATABASE_VERSION);
		db_data = getCategoriesTableName(catageor);
		mContext = context;
		sharedpreferences = context.getSharedPreferences(context.getString(R.string.PREFS_DC), Context.MODE_PRIVATE);

	}

	public DatabaseHelper(Context context) {
		super(context, context.getString(R.string.DATABASE_NAME), null, DATABASE_VERSION);
		mContext = context;
		sharedpreferences = context.getSharedPreferences(context.getString(R.string.PREFS_DC), Context.MODE_PRIVATE);

	}

	public ArrayList<Database> getCategoriesTableName(ArrayList<Categories> arrayList) {
		ArrayList<Database> allCategories = new ArrayList<Database>();

		for (int i = 0; i < arrayList.size(); i++) {
			Database db = new Database();
			db.setTableName(arrayList.get(i).getCategoryName().replace(" ", "_").toLowerCase(Locale.getDefault()));

			allCategories.add(db);
		}

		return allCategories;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {

		Log.v(TAG, "onCreate");

		Editor editor = sharedpreferences.edit();
		editor.putString(mContext.getString(R.string.PREFS_DC_KEY_TABLE_EXISTS), "true");
		editor.commit();

		db.execSQL(" create table if not exists " + mContext.getString(R.string.ALL_CATEGORY_TABLE)
				+ "(category_id integer primary key autoincrement not null, " + CATEGORY + " text unique "
				+ " ,category_key text unique);");

		for (int i = 0; i < db_data.size(); i++) {
			db.execSQL(createTable(db_data.get(i)));
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

		db.execSQL("DROP TABLE IF EXISTS " + mContext.getString(R.string.ALL_CATEGORY_TABLE));

		for (int i = 0; i < db_data.size(); i++) {
			db.execSQL("DROP TABLE IF EXISTS " + db_data.get(i).getTableName());
		}
		onCreate(db);

	}

	private String createTable(Database db) {

		String create = " create table if not exists " + db.getTableName()
				+ "(_id integer primary key autoincrement not null, " + " col_name text not null, "
				+ " col_address text not null, " + " col_about text not null, " + " col_discount text not null, "
				+ " col_description text not null, " + " col_rating text not null, "
				+ " col_item_id text not null unique, " + " col_longitude text not null, "
				+ " col_latitude text not null, " + " col_photopath text not null " + " );";

		return create;
	}

	public void writeToTable(ArrayList<CategoryItems> databaseValues) {

		SQLiteDatabase db = this.getWritableDatabase();

		for (int i = 0; i < databaseValues.size(); i++) {
			ContentValues values = new ContentValues();
			values.put("col_name", databaseValues.get(i).getName());
			values.put("col_address", databaseValues.get(i).getAddress());
			values.put("col_about", databaseValues.get(i).getAbout());
			values.put("col_discount", databaseValues.get(i).getDiscount());
			values.put("col_description", databaseValues.get(i).getDescription());
			values.put("col_rating", databaseValues.get(i).getRating());
			values.put("col_latitude", databaseValues.get(i).getLatitude());
			values.put("col_longitude", databaseValues.get(i).getLongitude());
			values.put("col_item_id", databaseValues.get(i).getId());
			values.put("col_photopath", databaseValues.get(i).getImageUrl());

			db.insert(
					databaseValues.get(i).getMainCategory().replace(" ", "_").toLowerCase(Locale.getDefault()).trim(),
					null, values);
		}

		db.close();

	}

	public void allCategories(ArrayList<Categories> categories) {
		SQLiteDatabase db = this.getWritableDatabase();
		for (int i = 0; i < categories.size(); i++) {

			ContentValues values = new ContentValues();
			values.put("col_category", categories.get(i).getCategoryName());
			values.put("category_key", categories.get(i).getId());

			db.insert(mContext.getString(R.string.ALL_CATEGORY_TABLE), null, values);

		}
		db.close();
	}

	public ArrayList<String> getAllCategoryFromDB() {

		ArrayList<String> category;
		try {
			String query = "Select * from " + mContext.getString(R.string.ALL_CATEGORY_TABLE);
			SQLiteDatabase db = this.getWritableDatabase();
			Cursor curos = db.rawQuery(query, null);
			category = new ArrayList<String>();
			if (curos.moveToFirst()) {
				do {
					category.add(curos.getString(1));
				} while (curos.moveToNext());
			}

			return category;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}

	public ArrayList<CategoryItems> getAllDeals(String tablename) {

		ArrayList<CategoryItems> deals;
		try {
			String query = "Select * from " + tablename.replace(" ", "_").toLowerCase(Locale.getDefault());
			SQLiteDatabase db = this.getWritableDatabase();
			Cursor curos = db.rawQuery(query, null);
			deals = new ArrayList<CategoryItems>();

			if (curos.moveToFirst()) {
				do {
					CategoryItems deal_data = new CategoryItems();

					deal_data.setName(curos.getString(1));
					deal_data.setAddress(curos.getString(2));
					deal_data.setAbout(curos.getString(3));
					deal_data.setDiscount(curos.getString(4));
					deal_data.setDescription(curos.getString(5));
					deal_data.setRating(curos.getString(6));
					deal_data.setId(curos.getString(7));
					deal_data.setLongitude(curos.getDouble(8));
					deal_data.setLatitude(curos.getDouble(9));
					deal_data.setImageUrl(curos.getString(10));

					deals.add(deal_data);
				} while (curos.moveToNext());
			}

			for (int i = 0; i < deals.size(); i++)
				Log.v("deals", deals.get(i).getName());
			return deals;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}
	
	
}
