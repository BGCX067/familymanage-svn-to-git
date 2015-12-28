package com.exuan.eweather;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import java.io.UnsupportedEncodingException;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import android.widget.TextView;

public class EWeatherActivity extends Activity {
	private DayWeather[] mWeatherData;
	private TextView mDateaTextView;
	private TextView mDatebTextView;
	private TextView mDatecTextView;
	private TextView mTemperatureaTextView;
	private TextView mTemperaturebTextView;
	private TextView mTemperaturecTextView;
	private TextView mWindaTextView;
	private TextView mWindbTextView;
	private TextView mWindcTextView;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		//init the view
		mDateaTextView = (TextView)findViewById(R.id.textview_datea);
		mDatebTextView = (TextView)findViewById(R.id.textview_dateb);;
		mDatecTextView = (TextView)findViewById(R.id.textview_datec);
		mTemperatureaTextView = (TextView)findViewById(R.id.textview_temperaturea);
		mTemperaturebTextView = (TextView)findViewById(R.id.textview_temperatureb);
		mTemperaturecTextView = (TextView)findViewById(R.id.textview_temperaturec);
		mWindaTextView = (TextView)findViewById(R.id.textview_winda);
		mWindbTextView = (TextView)findViewById(R.id.textview_windb);
		mWindcTextView = (TextView)findViewById(R.id.textview_windc);
		updateData();
	}
	
	private void updateData()
	{
		new Thread(){
			public void run()
			{
				String city = "北京";
				getWeather(city); 
				mHandler.sendEmptyMessage(0);
			}
		}.start();
	}
	
	private Handler mHandler = new Handler(){
		public void handleMessage(Message msg)
		{
			updateWeather();
		}
	};
	
	private void updateWeather()
	{
		if(null != mWeatherData)
		{
			mDateaTextView.setText(mWeatherData[0].mDate);
			mDatebTextView.setText(mWeatherData[1].mDate);
			mDatecTextView.setText(mWeatherData[2].mDate);
			mTemperatureaTextView.setText(mWeatherData[0].mTemperature);
			mTemperaturebTextView.setText(mWeatherData[1].mTemperature);
			mTemperaturecTextView.setText(mWeatherData[2].mTemperature);
			mWindaTextView.setText(mWeatherData[0].mWind);
			mWindbTextView.setText(mWeatherData[1].mWind);
			mWindcTextView.setText(mWeatherData[2].mWind);
		}
	}
	
	private static final String NAMESPACE = "http://WebXml.com.cn/";
	// WebService address
	private static final String URL = "http://www.webxml.com.cn/webservices/weatherwebservice.asmx";
	// WebService method name
	private static final String METHOD_NAME = "getWeatherbyCityName";
	// WebService action
	private static final String SOAP_ACTION = "http://WebXml.com.cn/getWeatherbyCityName";
	private SoapObject mWeather;

	public void getWeather(String cityName) {
		try {
			//init soap object
			SoapObject rpc = new SoapObject(NAMESPACE, METHOD_NAME);
			rpc.addProperty("theCityName", cityName);
			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
			envelope.bodyOut = rpc;
			envelope.dotNet = true;
			envelope.setOutputSoapObject(rpc);
			HttpTransportSE ht = new HttpTransportSE(URL);
			ht.debug = true;
			//call the action
			ht.call(SOAP_ACTION, envelope);
			//get the response soap object
			mWeather =(SoapObject) envelope.getResponse();
			getWeather(mWeather, cityName);
			return;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void getWeather(SoapObject detail,String local)throws UnsupportedEncodingException
	{
		mWeatherData = new DayWeather[3];
		mWeatherData[0] = new DayWeather();
		mWeatherData[0].mDate = detail.getProperty(6).toString();
		mWeatherData[0].mTemperature = detail.getProperty(5).toString();
		mWeatherData[0].mWind = detail.getProperty(7).toString();
		mWeatherData[1] = new DayWeather();
		mWeatherData[1].mDate = detail.getProperty(13).toString();
		mWeatherData[1].mTemperature = detail.getProperty(12).toString();
		mWeatherData[1].mWind = detail.getProperty(14).toString();
		mWeatherData[2] = new DayWeather();
		mWeatherData[2].mDate = detail.getProperty(18).toString();
		mWeatherData[2].mTemperature = detail.getProperty(17).toString();
		mWeatherData[2].mWind = detail.getProperty(19).toString();
	}
	
	//store a day's weather infomation
	class DayWeather
	{
		String mDate;
		String mTemperature;
		String mWind;
	}
	/*以下为LOG各property内容
		E/property1: 北京
		E/property4(: 2012-3-27 16:03:45
		E/property5(: 5℃/20℃
		E/property6: 3月27日 晴
		E/property7: 无持续风向微风
		E/property10: 今日天气实况：气温：23℃；风向/风力：西风 2级；湿度：8%；空气质量：较差；紫外线强度：中等。
		E/property12: 9℃/18℃
		E/property13: 3月28日 多云转阴
		E/property14: 无持续风向微风
		E/property17: 6℃/15℃
		E/property18(: 3月29日 阵雨转多云
		E/property19: 无持续风向微风转北风4-5级
	 */
}