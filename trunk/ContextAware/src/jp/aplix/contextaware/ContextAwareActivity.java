package jp.aplix.contextaware;

import java.util.ArrayList;
import java.util.HashMap;

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
import android.database.Cursor;
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

public class ContextAwareActivity extends Activity {
	private Context mContext;
	private ProgressDialog mProgress;
	private Button mScanButton;
	private CheckBox mStartCheckBox;
	private Button mRestConfigButton;
	private ListView mDeviceListConfigView;
	private ListView mDeviceListScanningView;
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
    private static final int SCAN_INTERVAL_MAX = 10;
    private static final int SCAN_INTERVAL_MIN = 1;
    
    public final static int REQUEST_ENABLE_BT = 10000;
    private static boolean has_registerBtReceiver = false;
    private ArrayList<HashMap<String, Object>> ConfigListArray = new ArrayList<HashMap<String, Object>>();
    private ArrayList<HashMap<String, Object>> NewScanningListArray = new ArrayList<HashMap<String, Object>>();
    DeviceItemAdapter Configadapter;
    DeviceItemAdapter Scanningadapter;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        mContext = this;
        mStartCheckBox = (CheckBox)findViewById(R.id.start_checkbox);
        mScanButton = (Button)findViewById(R.id.scan_button);
        mRestConfigButton = (Button)findViewById(R.id.RestConfig_button);
        
        mDeviceListConfigView = (ListView)findViewById(R.id.device_listview);
        Configadapter = new DeviceItemAdapter(this,ConfigListArray);
        mDeviceListConfigView.setAdapter(Configadapter);
        
        mDeviceListScanningView = (ListView)findViewById(R.id.deviceScanning_listview);
        Scanningadapter = new DeviceItemAdapter(this,NewScanningListArray);
        mDeviceListScanningView.setAdapter(Scanningadapter);
        
        initConfigDataFromDatebase();
        
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
					mStartCheckBox.setText(getString(R.string.stop));
					Intent b_intent = new Intent();
		    		b_intent.setAction("jp.aplix.contextaware.StartContextAware");
					sendBroadcast(b_intent);
					SharedPreferences prefs = getSharedPreferences("ContextAware", MODE_PRIVATE);
			    	prefs.edit().putString("StartContextAware", "true").commit();
				}
				else
				{
					Toast.makeText(mContext, getString(R.string.has_stopped), Toast.LENGTH_SHORT).show();
					mStartCheckBox.setText(getString(R.string.start));
					Intent b_intent = new Intent();
		    		b_intent.setAction("jp.aplix.contextaware.StopContextAware");
					sendBroadcast(b_intent);
					SharedPreferences prefs = getSharedPreferences("ContextAware", MODE_PRIVATE);
			    	prefs.edit().putString("StartContextAware", "false").commit();
				}
			}});
        
        mScanButton.setOnClickListener(new OnClickListener(){

		@Override
		public void onClick(View v) {
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
        mRestConfigButton.setOnClickListener(new OnClickListener(){

    		@Override
    		public void onClick(View v) {
    	    	Configadapter.notifyDataSetChanged();
    			Intent b_intent = new Intent();
    			b_intent.setAction("jp.aplix.contextaware.ConfigReload");
    			clearDateBase();
    			ConfigListArray.clear();
    			Configadapter.notifyDataSetChanged();
    		}});
        
        mDeviceListConfigView.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(mContext, SetProfileActivity.class);
				HashMap<String, Object> Item = ConfigListArray.get(position);
				intent.putExtra(ProfileDatabaseHelper.BLUETOOTH_NAME, (String)Item.get(ProfileDatabaseHelper.BLUETOOTH_NAME));
				intent.putExtra(ProfileDatabaseHelper.BLUETOOTH_MAC, (String)Item.get(ProfileDatabaseHelper.BLUETOOTH_MAC));
				intent.putExtra(ProfileDatabaseHelper.BLUETOOTH_RSSI, ((Integer)Item.get(ProfileDatabaseHelper.BLUETOOTH_RSSI)).intValue());
				intent.putExtra(ProfileDatabaseHelper.PROFILE_VALUE, ((Integer)Item.get(ProfileDatabaseHelper.PROFILE_VALUE)).intValue());
				intent.putExtra(ProfileDatabaseHelper.ENABLED, (String)Item.get(ProfileDatabaseHelper.ENABLED));
				mContext.startActivity(intent);
			}});
	        mDeviceListScanningView.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(mContext, SetProfileActivity.class);
				HashMap<String, Object> Item = NewScanningListArray.get(position);
				intent.putExtra(ProfileDatabaseHelper.BLUETOOTH_NAME, (String)Item.get(ProfileDatabaseHelper.BLUETOOTH_NAME));
				intent.putExtra(ProfileDatabaseHelper.BLUETOOTH_MAC, (String)Item.get(ProfileDatabaseHelper.BLUETOOTH_MAC));
				intent.putExtra(ProfileDatabaseHelper.BLUETOOTH_RSSI, ((Integer)Item.get(ProfileDatabaseHelper.BLUETOOTH_RSSI)).intValue());
				intent.putExtra(ProfileDatabaseHelper.PROFILE_VALUE, ((Integer)Item.get(ProfileDatabaseHelper.PROFILE_VALUE)).intValue());
				intent.putExtra(ProfileDatabaseHelper.ENABLED, (String)Item.get(ProfileDatabaseHelper.ENABLED));
				intent.putExtra("currentRssi", ((String)Item.get("currentRssi")));
				mContext.startActivity(intent);
		}});
        
    }
    public void onResume()
    {
    	initConfigDataFromDatebase();
    	Configadapter.notifyDataSetChanged();
		Intent b_intent = new Intent();
		b_intent.setAction("jp.aplix.contextaware.ConfigReload");
		sendBroadcast(b_intent);    	
    	super.onResume();
    }
    public void onDestroy()
    {
    	super.onDestroy();
    	//clearDateBase();
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
				View layout = factory.inflate(R.layout.delay_setting_bar, null);
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
    	}
    	return super.onCreateDialog(id);
    }
    
    public Handler mToastHandler = new Handler() {
    	@Override
        public void handleMessage(Message msg) {
            String str = null;
            switch(msg.what){
            case TOAST_MSG_SUCCESS:
            	str = getString(R.string.Success);
            	break;
            case TOAST_MSG_ERROR:
            	str = getString(R.string.Error);
            	break;
            case TOAST_MSG_ENABLE_BT_ERROR:
            	str = "Failed to enable BlueTooth,try agin later";
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
    	Cursor c = mContext.getContentResolver().query(ProfileInfoProvider.CONTENT_URI_PROFILES, null, null, null, null);
        for (int i = 0; i < c.getCount(); i++) {
        	c.moveToNext();
            HashMap<String, Object> user = new HashMap<String, Object>();
            user.put(ProfileDatabaseHelper.BLUETOOTH_NAME, c.getString(c.getColumnIndex(ProfileDatabaseHelper.BLUETOOTH_NAME)));
            user.put(ProfileDatabaseHelper.BLUETOOTH_MAC, c.getString(c.getColumnIndex(ProfileDatabaseHelper.BLUETOOTH_MAC)));
            user.put(ProfileDatabaseHelper.BLUETOOTH_RSSI, c.getInt(c.getColumnIndex(ProfileDatabaseHelper.BLUETOOTH_RSSI)));
            user.put(ProfileDatabaseHelper.PROFILE_VALUE, c.getInt(c.getColumnIndex(ProfileDatabaseHelper.PROFILE_VALUE)));
            user.put(ProfileDatabaseHelper.ENABLED, c.getString(c.getColumnIndex(ProfileDatabaseHelper.ENABLED)));
            ConfigListArray.add(user);
        }
    }
    
    private void clearDateBase()
    {
    	mContext.getContentResolver().delete(ProfileInfoProvider.CONTENT_URI_PROFILES, null, null);
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
}