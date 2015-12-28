package com.aplixcorp.intelliprofile;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;


public class BtAdapter extends BaseAdapter {
	private Context context;
	private LinearLayout peopleLineLayout;
	private ArrayList<HashMap<String, Object>> DeviceInfo = null;
	private LayoutInflater mInflater = null;
	public BtAdapter(Context c,ArrayList<HashMap<String, Object>> users) {
		   this.context = c;
		   DeviceInfo = users;
		   mInflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		if(DeviceInfo != null){
			return DeviceInfo.size();
		}else{
			return 0;
		}
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		   // TODO Auto-generated method stub
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
		    convertView = mInflater.inflate(R.layout.bt_list_item, null);
		    holder.imgBlueTooth = (ImageView) convertView
		      .findViewById(R.id.DeviceImage);
		    holder.DeviceName = (TextView) convertView
		      .findViewById(R.id.DeviceName);
		    holder.DeviceMacAddress = (TextView) convertView
		      .findViewById(R.id.DeviceMacAddress);
		    holder.imgMark = (ImageView) convertView
		      .findViewById(R.id.MarkImage);
		    holder.DeviceRssiValue = (TextView)convertView.findViewById(R.id.DeviceRssiValue);
		   } else {
			   holder = (ViewHolder) convertView.getTag();
		   }
		   HashMap Item = DeviceInfo.get(position);
		   holder.DeviceName.setText((String)Item.get(ProfileDatabaseHelper.BLUETOOTH_NAME));
		   holder.DeviceMacAddress.setText((String)Item.get(ProfileDatabaseHelper.BLUETOOTH_MAC));
		   holder.imgMark.setVisibility(View.INVISIBLE);
		   String CurDeviceRssiValue = (String)Item.get("currentRssi");
		   if(CurDeviceRssiValue != null){
			   holder.DeviceRssiValue.setVisibility(View.VISIBLE);
			   holder.imgMark.setVisibility(View.INVISIBLE);
			   holder.DeviceRssiValue.setText(CurDeviceRssiValue);
		   }else{
			   holder.DeviceRssiValue.setVisibility(View.INVISIBLE);
		   holder.imgMark.setVisibility(View.VISIBLE);
		   if(((String)Item.get(ProfileDatabaseHelper.ENABLED)).equals("1")){
			   holder.imgMark.setImageResource(R.drawable.mark_true);
		   }else{
			   holder.imgMark.setImageResource(R.drawable.mark_false);
			   }
		   }
			   
		   holder.deviceMap = Item;
		   convertView.setTag(holder);
		   //convertView.setOnClickListener(viewOnClick);
		   return convertView;
	}
	private static class ViewHolder {
		ImageView imgBlueTooth;
		TextView DeviceName;
		TextView DeviceMacAddress;
		ImageView imgMark;
		TextView DeviceRssiValue;
		TableLayout mTableLayout;
		HashMap deviceMap;
	}
}
