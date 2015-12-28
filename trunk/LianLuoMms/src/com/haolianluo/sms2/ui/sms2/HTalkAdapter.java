package com.haolianluo.sms2.ui.sms2;

import java.util.ArrayList;
import java.util.List;

import com.haolianluo.sms2.model.HAddressBookManager;
import com.haolianluo.sms2.model.HSms;
import com.lianluo.core.util.ToolsUtil;
import com.haolianluo.sms2.R;

import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class HTalkAdapter extends BaseAdapter {
	
	private List<HSms> list = null;
	private LayoutInflater mLayoutInflater = null;
	private int size = 0;
	ListView lv;
	TextView tv;
	String address = null;
	public HTalkAdapter(LayoutInflater layoutInflater,ListView lv,TextView tv,String address){
		mLayoutInflater = layoutInflater;
		list = new ArrayList<HSms>();
		this.lv = lv;
		this.tv = tv;
		this.address = address;
    }
	
	
	@Override
	public int getCount() {
		return size;
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
    
	public List<HSms> getList() {
		return list;
	}
	
	public HSms get(int position) {
		return (HSms)getItem(position);
	}
	
	public void add(HSms sms) {
		list.add(sms);
	}
	
	public void clear() {
		list.clear();
	}
	
	public int size() {
		return list.size();
	}
	
	public void remove(HSms sms){
		list.remove(sms);
	}
	
	public void remove(int position){
		list.remove(position);
		notifyDataSetChanged();
	}
	
	public void set(int position,HSms sms){
		list.set(position, sms);
	}
	
	public void add(int position,HSms sms){
		list.add(position, sms);
	}
	

	
	Holder holder = null;
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		if(convertView == null){
			holder = new Holder();
			holder.abm = new HAddressBookManager(mLayoutInflater.getContext());
			convertView = mLayoutInflater.inflate(R.layout.htalkadapter, null);
			holder.rl_left = (RelativeLayout) convertView.findViewById(R.id.left);
			holder.rl_right = (RelativeLayout) convertView.findViewById(R.id.right);
			holder.tv_time = (TextView) convertView.findViewById(R.id.time);
			holder.tv_body = (TextView) convertView.findViewById(R.id.body);
			holder.tv_address = (TextView) convertView.findViewById(R.id.address);
			holder.tv_timeRight = (TextView) convertView.findViewById(R.id.timeRight);
			holder.tv_bodyRight = (TextView) convertView.findViewById(R.id.bodyRight);
			holder.tv_addressRight = (TextView) convertView.findViewById(R.id.addressRight);
//			holder.tv_date = (TextView) convertView.findViewById(R.id.tv_date);
			convertView.setTag(holder);
		}else{
			holder = (Holder) convertView.getTag();
		}
		HSms model = null;
		if(list == null || list.size() == 0){
			return convertView;
		}else{
			model = list.get(position);
		}
		holder.tv_addressRight.setText("");
		String appendName = holder.abm.getAppendName(model.name, model.address);
		if(model.type.equals("1")){
			holder.rl_left.setVisibility(View.VISIBLE);
			holder.rl_right.setVisibility(View.GONE);
			drawTime(holder.tv_time, model);//时间
			holder.tv_body.setText(model.body);
			holder.tv_address.setText(appendName);
		}else{
			holder.rl_left.setVisibility(View.GONE);
			holder.rl_right.setVisibility(View.VISIBLE);
			drawTime(holder.tv_timeRight, model);//时间
			
			if(model.type.equals("3")){
				holder.tv_bodyRight.setText(drawFlag(model.body, getString(R.string.draft)));//内容
				
			}else{
				holder.tv_bodyRight.setText(model.body);
			}
			
			holder.tv_addressRight.setText(appendName);
			
		}
		return convertView;
	}

	
	private  class Holder {
		RelativeLayout rl_left;
		RelativeLayout rl_right;
		TextView tv_time;
		TextView tv_body;
		TextView tv_address;
		TextView tv_timeRight;
		TextView tv_bodyRight;
		TextView tv_addressRight;
		HAddressBookManager abm;
	}
	
	private String  getString(int id) {
		return mLayoutInflater.getContext().getString(id);
	}
	
	 private SpannableStringBuilder drawFlag(String name,String draft) {
		 SpannableStringBuilder style = null;
		 style=new SpannableStringBuilder(draft+" "+name);  
		 style.setSpan(new ForegroundColorSpan(0xFF4ca809),0,draft.length(),Spannable.SPAN_EXCLUSIVE_INCLUSIVE);  
		return style;
	 }
	
	private void drawTime(TextView time, HSms model) {
		String getListLongTime = ToolsUtil.getCurrentTime(Long.valueOf(model.time));
		String currentTime = ToolsUtil.getCurrentTime().substring(5, 11);
		String currentTimePosition = getListLongTime.substring(5, 11);
		if(currentTime.equals(currentTimePosition)){
			time.setText(getListLongTime.substring(11,16));
		}else{
			time.setText(currentTimePosition);
		}
	}
	
	@Override
	public void notifyDataSetChanged() {
		super.notifyDataSetChanged();
		size = list.size();
		if(lv != null){
			lv.setSelection(size);
		}
		if(tv != null && list.size()>0 && address != null){
			HAddressBookManager abk = new HAddressBookManager(mLayoutInflater.getContext());
			String address = list.get(size-1).address;
			String mName = abk.getNameByNumber(address);
			String name = abk.getAppendName(mName, address);
			tv.setText(name);
		}
	}

}
