package com.haolianluo.sms2;

import java.util.ArrayList;
import java.util.List;

import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.haolianluo.sms2.data.HConst;
import com.haolianluo.sms2.data.HSmsApplication;
import com.haolianluo.sms2.model.HAddressBookManager;
import com.haolianluo.sms2.model.HSearch;
import com.lianluo.core.util.ToolsUtil;

public class HSearchAdapter extends BaseAdapter{
	
	private List<HSearch> list = null; 
	private LayoutInflater mLayoutInflater = null;
	private int size = 0;
	
	public HSearchAdapter(LayoutInflater layoutInflater){
		mLayoutInflater = layoutInflater;
		list = new ArrayList<HSearch>();
		
	}
	public void set(int position ,HSearch search) {
		list.set(position,search);
		notifyDataSetChanged();
	}
	
	public HSearch get(int position) {
		return (HSearch)getItem(position);
	}
	
	public void addAll(List<HSearch> list) {
		this.list.addAll(list);
	}
	
	public void add(HSearch search) {
		list.add(search);
	}
	
	public void add(int location,HSearch search){
		list.add(location, search);
	}
	
	public void clear() {
		list.clear();
	}
	
	public void remove(int position){
		list.remove(position);
	}
	
	public int size() {
		return list.size();
	}
	
	@Override
	public int getCount() {
		return size;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	
	int checkMargin = 0;
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if(convertView == null){
			convertView = mLayoutInflater.inflate(R.layout.hthread_adapter,null);
			holder = new ViewHolder();
			holder.tv_name = (TextView) convertView.findViewById(R.id.tv_thread_name);
			TextPaint tp = holder.tv_name.getPaint(); 
			tp.setFakeBoldText(true);
			holder.tv_body = (TextView) convertView.findViewById(R.id.tv_thread_body);
			holder.tv_time = (TextView) convertView.findViewById(R.id.tv_thread_time);
			holder.iv_new = (TextView) convertView.findViewById(R.id.iv_thread_newsms);
//			holder.iv_head = (ImageView) convertView.findViewById(R.id.iv_thread_head);
			holder.abk = new HAddressBookManager(mLayoutInflater.getContext());
			holder.cb_mark = (CheckBox) convertView.findViewById(R.id.cb_mark);
			holder.markMms = (ImageView) convertView.findViewById(R.id.mmsflag);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}
		HSearch model = list.get(position);
		if(HConst.isMark){
			holder.cb_mark.setVisibility(View.VISIBLE);
		}else{
			holder.cb_mark.setVisibility(View.GONE);
		}
		checkOnClickListener(holder,model);
		if(model.thread.mark.equals("0")){
			holder.cb_mark.setChecked(false);
		}else{
			holder.cb_mark.setChecked(true);
		}
		
		
//		holder.iv_head.setBackgroundResource(R.drawable.def_head);
//		holder.iv_head.setImageBitmap(null);
//		if(HConst.iscollect){
//			holder.iv_head.setVisibility(View.GONE);
//		}else{
//			holder.iv_head.setVisibility(View.VISIBLE);
//			if(model.thread.headbm == null){
//				holder.iv_head.setBackgroundResource(R.drawable.def_head);
//			}else{
//				holder.iv_head.setImageBitmap(model.thread.headbm);
//			}
//		}
		
		
//		if(model.thread.headbm == null){
//			holder.iv_head.setVisibility(View.GONE);
//		}else{
//			holder.iv_head.setVisibility(View.VISIBLE);
//			holder.iv_head.setImageBitmap(model.thread.headbm);
//		}
		
		String appendName = holder.abk.getAppendName(model.thread.name, model.thread.address);
		if(model.thread.type.equals("3")){
			if(model.thread.count != null && Integer.parseInt(model.thread.count) > 1){
				//holder.tv_name.setText(appendName + "("+model.thread.count+")");
				holder.tv_name.setText(appendName);
			}else{
				holder.tv_name.setText(appendName);
			}
			holder.tv_body.setText(getString(R.string.draft) + ":" + model.thread.sms.body);
		}else if(model.thread.type.equals("5")){
			if(model.thread.count != null && Integer.parseInt(model.thread.count) > 1){
				//holder.tv_name.setText(drawFlag(appendName,getString(R.string.sendFail),"("+model.thread.count+")"));
				holder.tv_name.setText(drawFlag(appendName,getString(R.string.sendFail),null));
			}else{
				holder.tv_name.setText(drawFlag(appendName,getString(R.string.sendFail),null));
			}
			holder.tv_body.setText(model.thread.sms.body);
		}else{
			if(Integer.parseInt(model.thread.count) > 1){
				//holder.tv_name.setText(appendName+"("+model.thread.count+")");
				holder.tv_name.setText(appendName);
			}else{
				holder.tv_name.setText(appendName);
			}
			holder.tv_body.setText(model.thread.sms.body);
		}
//		if(model.thread.ismms.equals("0")){
//			holder.tv_body.setText(model.thread.sms.body);
//		}else{
//			holder.tv_body.setText(getString(R.string.mms)+" "+model.thread.sms.body);
//		}
		
		if(model.thread.sms.read.equals("1")){
			holder.iv_new.setVisibility(View.GONE);
		}else{
			holder.iv_new.setVisibility(View.VISIBLE);
			holder.iv_new.setText(model.thread.smsNoReadCount(model.thread.sms.threadid, mLayoutInflater.getContext()));
		}
		if(model.thread.ismms.equals("1")){
			holder.markMms.setVisibility(View.VISIBLE);
		}else{
			holder.markMms.setVisibility(View.GONE);
		}

		   String currentYear = ToolsUtil.getCurrentTime().substring(5, 11);
		   String getListLongTime = ToolsUtil.getCurrentTime(Long.valueOf(model.thread.sms.time));
	       String currentYearPosion = getListLongTime.substring(5, 11);
	       if(currentYear.equals(currentYearPosion)){//年月日相等显示时分秒
	       	holder.tv_time.setText(getListLongTime.substring(11,16));
	       }else{//不等则显示年月日
	       	holder.tv_time.setText(currentYearPosion);
	       }
	     
		return convertView;
	}

	private String  getString(int id) {
		return mLayoutInflater.getContext().getString(id);
	}
	
	
	private class ViewHolder {
//		ImageView iv_head = null;
		TextView iv_new = null;
		TextView tv_name = null;
		TextView tv_body = null;
		TextView tv_time = null;
		CheckBox cb_mark = null;
		ImageView markMms = null;
		HAddressBookManager abk = null;
	}
	
	private void checkOnClickListener(final ViewHolder holder,final HSearch model){
		holder.cb_mark.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(model.thread.mark.equals("1")){
					model.thread.mark = "0";
					holder.cb_mark.setChecked(false);
				}else{
					model.thread.mark = "1";
					holder.cb_mark.setChecked(true);
				}
			}
		});
	}
	
	 private SpannableStringBuilder drawFlag(String name,String draft,String count) {
		 SpannableStringBuilder style = null;
		 if(count != null){
			 style=new SpannableStringBuilder(name+" "+draft+count);  
		 }else{
			 style=new SpannableStringBuilder(name+" "+draft);  
		 }
		 style.setSpan(new ForegroundColorSpan(0xFF4ca809),name.length(),name.length()+draft.length()+1,Spannable.SPAN_EXCLUSIVE_INCLUSIVE);  
		return style;
	 }
	
	
	 
	@Override
	public void notifyDataSetChanged() {
		size = list.size();
		super.notifyDataSetChanged();
	}

}
