package com.aplixcorp.intelliprofile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.WifiLock;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class IntelliprofileActivity extends Activity {
	private Context mContext;
	private ProgressDialog mProgress;
	private Button mScanBtButton;
	private CheckBox mStartCheckBox;
	private Button mRestBtConfigButton;
	
	private Button mScanWlanButton;
	private Button mRestWlanConfigButton;
	private TextView mMessageControlTextView;
	
	private ListView mBtDeviceListConfigView;
	private ListView mBtDeviceListScanningView;
	
	private ListView mWlanDeviceListConfigView;
	private ListView mWlanDeviceListScanningView;
	
	private SeekBar mIntervalSeekBar;
	private TextView mSeekBarTextView;
	private int mInterval;
	
    public static final int TOAST_MSG_SUCCESS = 0;
    public static final int TOAST_MSG_ERROR = 1;
    public static final int TOAST_MSG_ENABLE_BT_ERROR = 2;
    public static final int TOAST_MSG_ENABLE_BT = 3;
    
    private static final int MENU_SETTING = 0;
    private static final int MENU_ABOUT = MENU_SETTING + 1;
    
    private static final int SCAN_INTERVAL_DIALOG_ID = 0;
    public static final int TURN_WLAN_DIALOG_ID = 1;
    
    private static final int SCAN_INTERVAL_MAX = 10;
    private static final int SCAN_INTERVAL_MIN = 1;
    
    private static final int SCAN_WIFI_TIME = 5000;
    
    public final static int REQUEST_ENABLE_BT = 10000;
    private static boolean has_registerBtReceiver = false;
    private static boolean has_registerWlanReceiver = false;
    
    private ArrayList<HashMap<String, Object>> ConfigListArray = new ArrayList<HashMap<String, Object>>();
    private ArrayList<HashMap<String, Object>> NewScanningListArray = new ArrayList<HashMap<String, Object>>();
    
    private ArrayList<HashMap<String, Object>> WlanConfigListArray = new ArrayList<HashMap<String, Object>>();
    private ArrayList<HashMap<String, Object>> WlanNewScanningListArray = new ArrayList<HashMap<String, Object>>();
    
    BtAdapter Configadapter;
    BtAdapter Scanningadapter;
    
    WlanAdapter WlanConfigadapter;
    WlanAdapter WlanScanningadapter;
    
    private WifiManager mWifiManager;

    private static final String TAG = "jayce";

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        mContext = this;
        mStartCheckBox = (CheckBox)findViewById(R.id.start_checkbox);
        
        mScanBtButton = (Button)findViewById(R.id.scan_button);
        mScanWlanButton = (Button)findViewById(R.id.scan_wlan_button);
        mRestBtConfigButton = (Button)findViewById(R.id.RestConfig_button);
        mRestWlanConfigButton = (Button)findViewById(R.id.RestConfig_wlan_button);
        
        mMessageControlTextView = (TextView)findViewById(R.id.message_control_textview);
        
        mBtDeviceListConfigView = (ListView)findViewById(R.id.device_listview);
        mWlanDeviceListConfigView = (ListView)findViewById(R.id.wlan_listview);
        
        Configadapter = new BtAdapter(this,ConfigListArray);
        WlanConfigadapter = new WlanAdapter(this,WlanConfigListArray);
        
        mBtDeviceListConfigView.setAdapter(Configadapter);
        mWlanDeviceListConfigView.setAdapter(WlanConfigadapter);
        
        mBtDeviceListScanningView = (ListView)findViewById(R.id.deviceScanning_listview);
        mWlanDeviceListScanningView = (ListView)findViewById(R.id.wlanScanning_listview);
        
        Scanningadapter = new BtAdapter(this,NewScanningListArray);
        WlanScanningadapter = new WlanAdapter(this,WlanNewScanningListArray);
        
        mBtDeviceListScanningView.setAdapter(Scanningadapter);
        mBtDeviceListScanningView.setFastScrollEnabled(true);
        mWlanDeviceListScanningView.setAdapter(WlanScanningadapter);
        mWlanDeviceListScanningView.setFastScrollEnabled(true);
        
        initConfigDataFromDatebase();
        initWlanConfigDataFromDatebase();
        
        SharedPreferences prefs = mContext.getSharedPreferences("ContextAware", Context.MODE_PRIVATE);
    	String service = prefs.getString("StartContextAware", "false");
        mStartCheckBox.setChecked(service.equals("true"));
        
        mStartCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener(){

			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				// TODO Auto-generated method stub
				if(arg1)
				{
					Toast.makeText(mContext, getString(R.string.has_started), Toast.LENGTH_SHORT).show();
					Intent b_intent = new Intent();
		    		b_intent.setAction("jp.aplix.contextaware.StartContextAware");
					sendBroadcast(b_intent);
					SharedPreferences prefs = getSharedPreferences("ContextAware", MODE_PRIVATE);
			    	prefs.edit().putString("StartContextAware", "true").commit();
				}
				else
				{
					Toast.makeText(mContext, getString(R.string.has_stopped), Toast.LENGTH_SHORT).show();
					Intent b_intent = new Intent();
		    		b_intent.setAction("jp.aplix.contextaware.StopContextAware");
					sendBroadcast(b_intent);
					SharedPreferences prefs = getSharedPreferences("ContextAware", MODE_PRIVATE);
			    	prefs.edit().putString("StartContextAware", "false").commit();
				}
			}});
        
        mMessageControlTextView.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(mContext, MessageControlActivity.class);
				startActivity(intent);
			}
        	
        });
        
        
        mScanBtButton.setOnClickListener(new OnClickListener(){

		@Override
		public void onClick(View v) {
			WlanNewScanningListArray.clear();
			WlanScanningadapter.notifyDataSetChanged();		        
			NewScanningListArray.clear();
			Scanningadapter.notifyDataSetChanged();

			mProgress = new ProgressDialog(mContext);
			mProgress.setMessage(getString(R.string.scanning));
			mProgress.show();
			// TODO Auto-generated method stub
			BluetoothAdapter  _myBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
	    	if(_myBluetoothAdapter == null){
	    		Log.d("ContextAware","cannot get any device for bluetooth");
	    	}else{
	    		if (!_myBluetoothAdapter.isEnabled()) {
	    			Intent TurnOnBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
	    			startActivityForResult(TurnOnBtIntent, REQUEST_ENABLE_BT);
	    		}else{
	    			Log.d("ContextAware","Start bt found and reload listeitem");
	    			if(has_registerBtReceiver){
	    				unregisterReceiver(BlueToothDeviceFoundReceiver);
	    			}
	    			if(has_registerBtReceiver == false){
	    				_myBluetoothAdapter.startDiscovery();
	    				IntentFilter intentFilter = new IntentFilter();
	    				intentFilter.addAction(BluetoothDevice.ACTION_FOUND);   
	    				intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);  
	    				registerReceiver(BlueToothDeviceFoundReceiver, intentFilter);
	    				has_registerBtReceiver = true;
	    			}
	    		}
	    	}
		}});
        
        mScanWlanButton.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
      			        NewScanningListArray.clear();
			        Scanningadapter.notifyDataSetChanged();				
				mProgress = new ProgressDialog(mContext);
				mProgress.setMessage(getString(R.string.scanning));
				mProgress.show();
				WlanNewScanningListArray.clear();
				WlanScanningadapter.notifyDataSetChanged();

				mWifiManager = (WifiManager) mContext.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
			    if (null == mWifiManager){
			        Toast.makeText(mContext, "fail to gain WifiManager service", Toast.LENGTH_LONG).show();
			        return;
			    }
			    //mWifiLock = mWifiManager.createWifiLock(TAG);
		       // mWifiLock.acquire();
		        if (!mWifiManager.isWifiEnabled()){
		        	showDialog(TURN_WLAN_DIALOG_ID); 		
		        }
		        else
    			{
    				boolean result = mWifiManager.startScan();
    				if(result)
    				{
	    				new Thread(){
	    					public void run()
	    					{/*
	    						try
	    						{
	    						Thread.sleep(SCAN_WIFI_TIME);
	    						}
	    						catch(Exception e)
	    						{
	    							
	    						}*/
					    		if(!has_registerWlanReceiver)
					    		{
					    			IntentFilter intentFilter = new IntentFilter();
					    			intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);  
					    			registerReceiver(WlanDeviceFoundReceiver, intentFilter);
					    			has_registerWlanReceiver = true;
					    		}
	    					}
	    				}.start();
    				}
    				else
    				{
    					//mWifiLock.release();
    					Toast.makeText(mContext, getString(R.string.scan_wlan_failed), Toast.LENGTH_SHORT).show();
    				}
    			}
				
			}});
        
        mRestBtConfigButton.setOnClickListener(new OnClickListener(){

    		@Override
    		public void onClick(View v) {
    	    	Configadapter.notifyDataSetChanged();
    			Intent b_intent = new Intent();
    			b_intent.setAction("jp.aplix.contextaware.ConfigReload");
    			clearBlueTooth();
    			ConfigListArray.clear();
    			Configadapter.notifyDataSetChanged();
    		}});
        
        mRestWlanConfigButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				WlanConfigadapter.notifyDataSetChanged();
    			clearWlan();
    			WlanConfigListArray.clear();
    			WlanConfigadapter.notifyDataSetChanged();
			}});
        
        mBtDeviceListConfigView.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(mContext, SetBtProfileActivity.class);
				HashMap<String, Object> Item = ConfigListArray.get(position);
				intent.putExtra(ProfileDatabaseHelper.BLUETOOTH_NAME, (String)Item.get(ProfileDatabaseHelper.BLUETOOTH_NAME));
				intent.putExtra(ProfileDatabaseHelper.BLUETOOTH_MAC, (String)Item.get(ProfileDatabaseHelper.BLUETOOTH_MAC));
				intent.putExtra(ProfileDatabaseHelper.BLUETOOTH_RSSI, ((Integer)Item.get(ProfileDatabaseHelper.BLUETOOTH_RSSI)).intValue());
				Log.d("Seth Log","Set Rssi = " + ((Integer)Item.get(ProfileDatabaseHelper.BLUETOOTH_RSSI)).intValue());
				intent.putExtra(ProfileDatabaseHelper.PROFILE_VALUE, ((Integer)Item.get(ProfileDatabaseHelper.PROFILE_VALUE)).intValue());
				intent.putExtra(ProfileDatabaseHelper.ENABLED, (String)Item.get(ProfileDatabaseHelper.ENABLED));
				mContext.startActivity(intent);
			}});
        
        mWlanDeviceListConfigView.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(mContext, SetWlanProfileActivity.class);
				HashMap<String, Object> Item = WlanConfigListArray.get(arg2);
				intent.putExtra(ProfileDatabaseHelper.WLAN_NAME, (String)Item.get(ProfileDatabaseHelper.WLAN_NAME));
				intent.putExtra(ProfileDatabaseHelper.WLAN_MAC, (String)Item.get(ProfileDatabaseHelper.WLAN_MAC));
				intent.putExtra(ProfileDatabaseHelper.WLAN_RSSI, ((Integer)Item.get(ProfileDatabaseHelper.WLAN_RSSI)).intValue());
				intent.putExtra(ProfileDatabaseHelper.PROFILE_VALUE, ((Integer)Item.get(ProfileDatabaseHelper.PROFILE_VALUE)).intValue());
				intent.putExtra(ProfileDatabaseHelper.WLAN_PHONE_NUMBER, (String)Item.get(ProfileDatabaseHelper.WLAN_PHONE_NUMBER));
				intent.putExtra(ProfileDatabaseHelper.ENABLED, (String)Item.get(ProfileDatabaseHelper.ENABLED));
				mContext.startActivity(intent);
			}});
        
        mBtDeviceListScanningView.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(mContext, SetBtProfileActivity.class);
				HashMap<String, Object> Item = NewScanningListArray.get(position);
				intent.putExtra(ProfileDatabaseHelper.BLUETOOTH_NAME, (String)Item.get(ProfileDatabaseHelper.BLUETOOTH_NAME));
				intent.putExtra(ProfileDatabaseHelper.BLUETOOTH_MAC, (String)Item.get(ProfileDatabaseHelper.BLUETOOTH_MAC));
				intent.putExtra(ProfileDatabaseHelper.BLUETOOTH_RSSI, ((Integer)Item.get(ProfileDatabaseHelper.BLUETOOTH_RSSI)).intValue());
				intent.putExtra(ProfileDatabaseHelper.PROFILE_VALUE, ((Integer)Item.get(ProfileDatabaseHelper.PROFILE_VALUE)).intValue());
				intent.putExtra(ProfileDatabaseHelper.ENABLED, (String)Item.get(ProfileDatabaseHelper.ENABLED));
				intent.putExtra("currentRssi", ((String)Item.get("currentRssi")));
				mContext.startActivity(intent);
		}});
        
        mWlanDeviceListScanningView.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(mContext, SetWlanProfileActivity.class);
				HashMap<String, Object> Item = WlanNewScanningListArray.get(position);
				intent.putExtra(ProfileDatabaseHelper.WLAN_NAME, (String)Item.get(ProfileDatabaseHelper.WLAN_NAME));
				intent.putExtra(ProfileDatabaseHelper.WLAN_MAC, (String)Item.get(ProfileDatabaseHelper.WLAN_MAC));
				intent.putExtra(ProfileDatabaseHelper.WLAN_RSSI, ((Integer)Item.get(ProfileDatabaseHelper.WLAN_RSSI)).intValue());
				intent.putExtra(ProfileDatabaseHelper.PROFILE_VALUE, ((Integer)Item.get(ProfileDatabaseHelper.PROFILE_VALUE)).intValue());
				intent.putExtra(ProfileDatabaseHelper.WLAN_PHONE_NUMBER, (String)Item.get(ProfileDatabaseHelper.WLAN_PHONE_NUMBER));
				intent.putExtra(ProfileDatabaseHelper.ENABLED, (String)Item.get(ProfileDatabaseHelper.ENABLED));
				intent.putExtra("currentRssi", ((String)Item.get("currentRssi")));
				mContext.startActivity(intent);
		}});
        
    }
    public void onResume()
    {
    	initConfigDataFromDatebase();
    	initWlanConfigDataFromDatebase();
    	Configadapter.notifyDataSetChanged();
    	WlanConfigadapter.notifyDataSetChanged();
		Intent b_intent = new Intent();
		b_intent.setAction("jp.aplix.contextaware.ConfigReload");
		sendBroadcast(b_intent);
		Intent w_intent = new Intent();
		b_intent.setAction("jp.aplix.contextaware.WlanConfigReload");
		sendBroadcast(w_intent); 
    	super.onResume();
    }
    public void onDestroy()
    {
    	super.onDestroy();
    	//clearDateBase();
    	if(has_registerWlanReceiver)
    	{
	    	unregisterReceiver(WlanDeviceFoundReceiver);
	        has_registerWlanReceiver = false;
    	}
    }
    
    public boolean onCreateOptionsMenu(Menu menu) {
    	menu.add(0, MENU_SETTING, Menu.NONE, R.string.setting);
    	menu.add(0, MENU_ABOUT, Menu.NONE, R.string.about);
		return super.onCreateOptionsMenu(menu);
    }
    
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch(item.getItemId())
    	{
	    	case MENU_SETTING:
	    	{
	    		showDialog(SCAN_INTERVAL_DIALOG_ID);
	    	}
	    	break;
	    	case MENU_ABOUT:
	    	{
	    		Toast.makeText(mContext, getString(R.string.about), Toast.LENGTH_SHORT).show();
	    	}
	    	break;
    	}
		return super.onOptionsItemSelected(item);
	}
    
    public Dialog onCreateDialog(int id)
    {
    	switch(id)
    	{
	    	case SCAN_INTERVAL_DIALOG_ID:
	    	{
	    		SharedPreferences prefs = getSharedPreferences("ContextAware", MODE_PRIVATE);
	    		mInterval = prefs.getInt("interval", 1);
	    		LayoutInflater factory = LayoutInflater.from(mContext);
				View layout = factory.inflate(R.layout.interval_setting_bar, null);
				mIntervalSeekBar = (SeekBar)layout.findViewById(R.id.seekbar);
				mSeekBarTextView = (TextView)layout.findViewById(R.id.status);
				mIntervalSeekBar.setMax(SCAN_INTERVAL_MAX - SCAN_INTERVAL_MIN);
				mIntervalSeekBar.setProgress(mInterval - SCAN_INTERVAL_MIN);
				mSeekBarTextView.setText(mInterval + getString(R.string.minute));
				mIntervalSeekBar
				.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
					public void onProgressChanged(SeekBar seekBar,
						int progress, boolean fromUser) {
						mSeekBarTextView.setText((progress + SCAN_INTERVAL_MIN) + getString(R.string.minute));
					}

					public void onStartTrackingTouch(SeekBar seekBar) {
					}

					public void onStopTrackingTouch(SeekBar seekBar) {
					}
				});
				return new AlertDialog.Builder(mContext)
				.setTitle(getString(R.string.scan_interval))
				.setView(layout)
				.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener(){

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						mInterval = mIntervalSeekBar.getProgress() + SCAN_INTERVAL_MIN;
						mSeekBarTextView.setText(String.valueOf(mInterval) + getString(R.string.minute));
						SharedPreferences prefs = getSharedPreferences("ContextAware", MODE_PRIVATE);
				    	prefs.edit().putInt("interval", mInterval).commit();
					}})
				.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener(){

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						
					}})
				.create();	
	    	}
	    	case TURN_WLAN_DIALOG_ID:
	    	{
	    		return new AlertDialog.Builder(mContext)
	    		.setTitle(getString(R.string.wlan))
	    		.setMessage(getString(R.string.turn_on_wlan))
	    		.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener(){

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						boolean success = mWifiManager.setWifiEnabled(true);

			    		if(!has_registerWlanReceiver)
			    		{
			    			IntentFilter intentFilter = new IntentFilter();
			    			intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);  
			    			registerReceiver(WlanDeviceFoundReceiver, intentFilter);
			    			has_registerWlanReceiver = true;
			    		}
					}})
				.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener(){

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						mProgress.dismiss();
					        Toast.makeText(mContext, getString(R.string.fail_to_scan_WLAN), Toast.LENGTH_SHORT).show();
					}})
				.create();
	    	}
    	}
    	return super.onCreateDialog(id);
    }
    
    public Handler mToastHandler = new Handler() {
    	@Override
        public void handleMessage(Message msg) {
            String str = null;
            switch(msg.what){
            case TOAST_MSG_SUCCESS:
            	str = getString(R.string.success);
            	break;
            case TOAST_MSG_ERROR:
            	str = getString(R.string.error);
            	break;
            case TOAST_MSG_ENABLE_BT_ERROR:
            	str = getString(R.string.fail_to_scan_BT);
            	break;
            default:
            	break;
            }

            if (str != null) {
                Toast.makeText(getApplicationContext(), str,Toast.LENGTH_LONG).show();
            }
        }
    };
    
    private void initConfigDataFromDatebase(){
    	ConfigListArray.clear();
    	Cursor c = mContext.getContentResolver().query(ProfileInfoProvider.CONTENT_URI_BT_PROFILES, null, null, null, null);
    	if(c == null || c.getCount() < 1){
    		return;
    	}
        for (int i = 0; i < c.getCount(); i++) {
        	c.moveToNext();
            HashMap<String, Object> user = new HashMap<String, Object>();
            user.put(ProfileDatabaseHelper.BLUETOOTH_NAME, c.getString(c.getColumnIndex(ProfileDatabaseHelper.BLUETOOTH_NAME)));
            user.put(ProfileDatabaseHelper.BLUETOOTH_MAC, c.getString(c.getColumnIndex(ProfileDatabaseHelper.BLUETOOTH_MAC)));
            int configrssi = c.getInt(c.getColumnIndex(ProfileDatabaseHelper.BLUETOOTH_RSSI));
            Log.d("Seth Log","rssi value = " + configrssi);
            int rssi = (configrssi == -60 ? 0 : 1);
            Log.d("Seth Log","rssi = " + rssi);
            user.put(ProfileDatabaseHelper.BLUETOOTH_RSSI, rssi);
            user.put(ProfileDatabaseHelper.PROFILE_VALUE, c.getInt(c.getColumnIndex(ProfileDatabaseHelper.PROFILE_VALUE)));
            user.put(ProfileDatabaseHelper.ENABLED, c.getString(c.getColumnIndex(ProfileDatabaseHelper.ENABLED)));
            ConfigListArray.add(user);
        }
        
    }
    
    private void initWlanConfigDataFromDatebase(){
    	
        WlanConfigListArray.clear();
    	Cursor c = mContext.getContentResolver().query(ProfileInfoProvider.CONTENT_URI_WLAN_PROFILES, null, null, null, null);
    	if(c == null || c.getCount() < 1){
    		return;
    	}
        for (int i = 0; i < c.getCount(); i++) {
        	c.moveToNext();
            HashMap<String, Object> user = new HashMap<String, Object>();
            user.put(ProfileDatabaseHelper.WLAN_NAME, c.getString(c.getColumnIndex(ProfileDatabaseHelper.WLAN_NAME)));
            user.put(ProfileDatabaseHelper.WLAN_MAC, c.getString(c.getColumnIndex(ProfileDatabaseHelper.WLAN_MAC)));
            int configrssi = c.getInt(c.getColumnIndex(ProfileDatabaseHelper.WLAN_RSSI));
            int rssi = (configrssi == -60 ? 0 : 1);
            user.put(ProfileDatabaseHelper.WLAN_RSSI, rssi);
            user.put(ProfileDatabaseHelper.PROFILE_VALUE, c.getInt(c.getColumnIndex(ProfileDatabaseHelper.PROFILE_VALUE)));
            user.put(ProfileDatabaseHelper.WLAN_PHONE_NUMBER, c.getString(c.getColumnIndex(ProfileDatabaseHelper.WLAN_PHONE_NUMBER)));
            user.put(ProfileDatabaseHelper.ENABLED, c.getString(c.getColumnIndex(ProfileDatabaseHelper.ENABLED)));
            WlanConfigListArray.add(user);
        }
    }
    
    private void clearBlueTooth()
    {
    	mContext.getContentResolver().delete(ProfileInfoProvider.CONTENT_URI_BT_PROFILES, null, null);
    }
    
    private void clearWlan()
    {
    	mContext.getContentResolver().delete(ProfileInfoProvider.CONTENT_URI_WLAN_PROFILES, null, null);
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	Log.d("ContextAware","Start bt already");
    	if(requestCode == REQUEST_ENABLE_BT){
			BluetoothAdapter  _myBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
	    	if(_myBluetoothAdapter == null){
	    		Log.d("ContextAware","cannot get any device for bluetooth");
	    	}else{
	    		if (!_myBluetoothAdapter.isEnabled()) {
	    			mToastHandler.sendEmptyMessage(TOAST_MSG_ENABLE_BT_ERROR);
				mProgress.dismiss();
	    		}else{
	    			Log.d("ContextAware","Start bt scanning bluetooth");
	    			if(has_registerBtReceiver == false){
	    				_myBluetoothAdapter.startDiscovery();
	    				IntentFilter intentFilter = new IntentFilter();
	    				intentFilter.addAction(BluetoothDevice.ACTION_FOUND);   
	    				intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);  
 	    				registerReceiver(BlueToothDeviceFoundReceiver, intentFilter);
	    			}
	    		}
	    	}
    	}
    	super.onActivityResult(requestCode, resultCode, data);
    }
    private final BroadcastReceiver BlueToothDeviceFoundReceiver = new BroadcastReceiver(){
        @Override
        public void onReceive(Context context, Intent intent){
    		String action = intent.getAction();
    		if(action == null){
    			return;
    		}
    		Log.d("ContextAware","action = " + action);
    		if(action.equals(BluetoothDevice.ACTION_FOUND)){
    			BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
    			Short rssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI,(short) 0);
    			String bt_address = device.getAddress();
    			String bt_name = device.getName();
    			HashMap<String, Object> user = new HashMap<String, Object>();
                     user.put(ProfileDatabaseHelper.BLUETOOTH_NAME, bt_name);
                     user.put(ProfileDatabaseHelper.BLUETOOTH_MAC,bt_address );
                     user.put("currentRssi",rssi.toString() + "db");
                     user.put(ProfileDatabaseHelper.BLUETOOTH_RSSI, 0);
                     user.put(ProfileDatabaseHelper.PROFILE_VALUE, 0);
                     user.put(ProfileDatabaseHelper.ENABLED, "0");
                     NewScanningListArray.add(user);
                     Scanningadapter.notifyDataSetChanged();
    			Log.d("ContextAware","new found bluetooth mac = " +
   					 bt_address + " name =  " + 
   					 bt_name + " rssi = " + rssi);
    		}else if(action.equals(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)){
    			unregisterReceiver(BlueToothDeviceFoundReceiver);
    			has_registerBtReceiver = false;
    			mProgress.dismiss();
    		}
        }
    };
    
    private final BroadcastReceiver WlanDeviceFoundReceiver = new BroadcastReceiver(){
        @Override
        public void onReceive(Context context, Intent intent)
        {
    		String action = intent.getAction();
    		if(action == null){
    			return;
    		}
    		Log.d("jayce","action = " + action);

    		if (action.equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {
    			Log.e("jayce", "WifiManager.SCAN_RESULTS_AVAILABLE_ACTION");
    			WlanNewScanningListArray.clear();
    			List<ScanResult> scanResults = mWifiManager.getScanResults();
                    for (int i = 0; null != scanResults && i < scanResults.size(); i++) {
                         ScanResult mScanResult = scanResults.get(i);
                         String wlan_name = mScanResult.SSID;
             			 String wlan_address = mScanResult.BSSID;
             			 int wlan_rssi = mScanResult.level;
                         Log.e("ContextAware", "name = " + mScanResult.SSID);
                         Log.e("ContextAware", "address = " + mScanResult.BSSID);
                         HashMap<String, Object> user = new HashMap<String, Object>();
                         user.put(ProfileDatabaseHelper.WLAN_NAME, wlan_name);
                         user.put(ProfileDatabaseHelper.WLAN_MAC, wlan_address );
                         user.put("currentRssi", wlan_rssi + "db");
                         user.put(ProfileDatabaseHelper.WLAN_RSSI, 0);
                         user.put(ProfileDatabaseHelper.PROFILE_VALUE, 0);
                         user.put(ProfileDatabaseHelper.WLAN_PHONE_NUMBER, "");
                         user.put(ProfileDatabaseHelper.ENABLED, "0");
                         WlanNewScanningListArray.add(user);
                         WlanScanningadapter.notifyDataSetChanged();
	               }
                   //mWifiLock.release();
                   //unregisterReceiver(WlanDeviceFoundReceiver);
	              // has_registerWlanReceiver = false;
	        	  if(mProgress.isShowing())
	        	  {
	        		  mProgress.dismiss();
	        	  }
               }
       }
   };
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}
}
