package activity;


import java.util.ArrayList;
import java.util.List;

import util.HttpCallBackListener;
import util.HttpUtil;
import util.Utility;

import model.City;
import model.County;
import model.Province;

import com.bingxuan.weather.R;

import db.WeatherDB;

import android.R.anim;
import android.app.Activity;
import android.app.ProgressDialog;
import android.location.Address;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ChooseAreaActivity extends Activity {

	public static final int LEVEL_PROVINCE=0;
	public static final int LEVEL_CITY=1;
	public static final int LEVEL_COUNTY=2;
	private  ProgressDialog progressDialog;
	private ListView listview;
	private TextView titleText;
	private List<String> datalist =new ArrayList<String>();
	private ArrayAdapter<String> adapter;
	private WeatherDB weatherDB;
	private int currentlevel;
	//省列表
	private List<Province> provincesList;
	//市列表
	private List<City> cityList;
	//县列表
	private List<County> countyList;
	//选中的省份
	private Province selectedProvince;
	//选中的市
	private City selectedCity;
	//选中的县
	private County selectedCounty;
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.choosearea1);
		listview = (ListView) findViewById(R.id.listView1);
		titleText = (TextView) findViewById(R.id.title_text);
		
		adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,datalist);
		listview.setAdapter(adapter);
		
		weatherDB = WeatherDB.getInstance(this);
		listview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View view,
					int index, long arg3) {
				// TODO Auto-generated method stub
				if (currentlevel == LEVEL_PROVINCE) {
					selectedProvince=provincesList.get(index);
					queryCities();
				}else if (currentlevel==LEVEL_CITY) {
					selectedCity=cityList.get(index);
					queryCounties();
				}
			}
		});
		queryProvince();
	}

	
	private void queryProvince() {
		// TODO Auto-generated method stub
		provincesList = weatherDB.loadprovince();
		if (provincesList.size()>0) {
			datalist.clear();
			for (Province province :provincesList) {
				datalist.add(province.getProvinceName());
			}
			adapter.notifyDataSetChanged();
			listview.setSelection(0);
			titleText.setText("中国");
			currentlevel=LEVEL_PROVINCE;
		}else {
			queryFroServer(null,"province");
		}
	}
	private void queryCities() {
		// TODO Auto-generated method stub
		cityList = weatherDB.loadCities(selectedProvince.getId());
		if (cityList.size()>0) {
			datalist.clear();
			for (City city :cityList) {
				datalist.add(city.getCityName());
			}
			adapter.notifyDataSetChanged();
			listview.setSelection(0);
			titleText.setText(selectedProvince.getProvinceName());
			currentlevel=LEVEL_CITY;
		}else {
			queryFroServer(selectedProvince.getProvinceCode(),"city");
		}
	}

	private void queryCounties() {
		// TODO Auto-generated method stub
		countyList = weatherDB.loadCounties(selectedCity.getId());
		if (countyList.size()>0) {
			datalist.clear();
			for (County county :countyList) {
				datalist.add(county.getCountyName());
			}
			adapter.notifyDataSetChanged();
			listview.setSelection(0);
			titleText.setText(selectedCity.getCityName());
			currentlevel=LEVEL_COUNTY;
		}else {
			queryFroServer(selectedCity.getCityCode(),"county");
		}
	}

	
	
	private void queryFroServer(final String  code,final String type) {
		// TODO Auto-generated method stub
		String address;
		if (!TextUtils.isEmpty(code)) {
			address ="http://www.weather.com.cn/data/list3/city"+code+".xml";
		}else {
			address="http://www.weather.com.cn/data/list3/city.xml";
		}
		showProgressDialog();
		HttpUtil.sendHttpRequest(address, new HttpCallBackListener() {
			
			@Override
			public void onFinish(String response) {
				// TODO Auto-generated method stub
				boolean result=false;
				if ("province".equals(type)) {
					result=Utility.handleProvinceResponse(weatherDB, response);
				}else if ("city".equals(type)) {
					result=Utility.handleCitiesResponse(weatherDB, response, selectedProvince.getId());
				}else if("county".equals(type)) {
					result=Utility.handleCountiesResponse(weatherDB, response, selectedCity.getId());
				}
				if (result) {
					runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							// TODO Auto-generated method stub
						closeProgressDialog();
						if ("province".equals(type)) {
							queryProvince();
						}else if("city".equals(type)) {
							queryCities();
						}else if ("county".equals(type)) {
							queryCounties();
						}
						}

						
					});
				}
			}
			
			@Override
			public void onError(Exception e) {
				// TODO Auto-generated method stub
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
					closeProgressDialog();
					Toast.makeText(ChooseAreaActivity.this, "加载失败。。", Toast.LENGTH_SHORT).show();
					}
				});
			}
		});
	}

	private void showProgressDialog() {
		// TODO Auto-generated method stub
		if (progressDialog==null) {
			 progressDialog = new ProgressDialog(this);
			 progressDialog.setMessage("正自加载。。。");
			 progressDialog.setCanceledOnTouchOutside(false);
		}
		progressDialog.show();
	}
	private void closeProgressDialog() {
		// TODO Auto-generated method stub
		if (progressDialog!=null) {
			progressDialog.dismiss();
		}
	}
	@Override
	public void onBackPressed() {
		if (currentlevel==LEVEL_COUNTY) {
			queryCities();
		}else if (currentlevel==LEVEL_CITY) {
			queryProvince();
		}else {
			finish();
		}
	}
}
