package db;

import java.util.ArrayList;
import java.util.List;

import model.City;
import model.County;
import model.Province;
import android.R.integer;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.nfc.tech.IsoDep;

public class WeatherDB {

	//数据库名
	public static final String DB_NAME="weather";
	//数据库版本
	public static final int VERSION= 1;
	
	private static WeatherDB weatherDB;
	private SQLiteDatabase db;
	
	
	private WeatherDB(Context context){
		WeatherOpenHelper dbHelper = new WeatherOpenHelper(context, DB_NAME, null, VERSION);
		db = dbHelper.getWritableDatabase();
	}
	//获取WeatherDB的实例
	public synchronized static WeatherDB getInstance(Context context){
		if (weatherDB==null) {
			weatherDB = new WeatherDB(context);
		}
		return weatherDB;
	}
	//将province实例存储到数据库中。
	public void savaprovince(Province province){
		if (province!=null) {
			ContentValues values = new ContentValues();
			values.put("province_name", province.getProvinceName());
			values.put("province_code", province.getProvinceCode());
			db.insert("Province", null, values);
		}
	}
	
	//从数据库都去全国所有省份信息。
	public List <Province> loadprovince(){
		List<Province> list=new ArrayList<Province>();
		Cursor cursor= db.query("Province", null, null, null, null, null, null);
		if (cursor.moveToFirst()) {
			do{
				Province province = new Province();
				province.setId(cursor.getInt(cursor.getColumnIndex("id")));
				province.setProvinceName(cursor.getString(cursor.getColumnIndex("province_name")));
				province.setProvinceCode(cursor.getString(cursor.getColumnIndex("province_code")));
				list.add(province);
			}while(cursor.moveToNext());
		}
		if (cursor!=null) {
			cursor.close();
		}
		return list;
	}
	//将city实例存储到数据库
	public void saveCity(City city){
		if (city!=null) {
			ContentValues values = new ContentValues();
			values.put("city_name", city.getCityName());
			values.put("city_code", city.getCityCode());
			values.put("province_id", city.getProvinceId());
			db.insert("City", null, values);
		}
	}
	//从数据库读取谋生下所有的城市信息。
	public List <City> loadCities(int provinceId){
		List<City> list=new ArrayList<City>();
		Cursor cursor= db.query("City", null, "province_id=?", new String[]{String.valueOf(provinceId)}, null, null, null);
		if (cursor.moveToFirst()) {
			do{
				City city = new City();
				city.setId(cursor.getInt(cursor.getColumnIndex("id")));
				city.setCityName(cursor.getString(cursor.getColumnIndex("city_name")));
				city.setCityCode(cursor.getString(cursor.getColumnIndex("city_code")));
				list.add(city);
			}while(cursor.moveToNext());
		}
		if (cursor!=null) {
			cursor.close();
		}
		return list;
	}
	//将county实例存储到数据库。
	public void saveCounty(County county){
		if (county!=null) {
			ContentValues values = new ContentValues();
			values.put("county_name", county.getCountyName());
			values.put("county_code", county.getCountyCode());
			values.put("city_id", county.getCityId());
			db.insert("county", null, values);
		}
	}
	//从数据库读取某城市下所有县信息。
	public List <County> loadCounties(int cityid){
		List<County> list=new ArrayList<County>();
		Cursor cursor= db.query("County", null, "city_id=?", new String[]{String.valueOf(cityid)}, null, null, null);
		if (cursor.moveToFirst()) {
			do{
				County county = new County();
				county.setId(cursor.getInt(cursor.getColumnIndex("id")));
				county.setCountyName(cursor.getString(cursor.getColumnIndex("county_name")));
				county.setCountyCode(cursor.getString(cursor.getColumnIndex("county_code")));
				list.add(county);
			}while(cursor.moveToNext());
		}
		if (cursor!=null) {
			cursor.close();
		}
		return list;
	}
	
}
