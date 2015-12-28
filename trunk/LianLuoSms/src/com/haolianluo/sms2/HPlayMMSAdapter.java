package com.haolianluo.sms2;

import java.util.List;

import com.haolianluo.sms2.model.HMmsModel;
import com.lianluo.core.util.HLog;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class HPlayMMSAdapter extends BaseAdapter {
	
	private LayoutInflater mLayoutInflater;
	private List<HMmsModel> mList;
	
	public HPlayMMSAdapter(LayoutInflater layoutInflater,List<HMmsModel> list){
		mLayoutInflater = layoutInflater;
		mList = list;
	}

	@Override
	public int getCount() {
		HLog.i("----------------" + mList.size());
		return mList.size();
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Holder holder = null;
		if(convertView == null){
			holder = new Holder();
			convertView = mLayoutInflater.inflate(R.layout.play_mms_adapter,null);
			holder.imageView = (ImageView) convertView.findViewById(R.id.mmsImageView);
			holder.textView = (TextView) convertView.findViewById(R.id.mmsTextView);
			convertView.setTag(holder);
		}else{
			holder = (Holder) convertView.getTag();
		}
		
		String cid = mList.get(position).ct;
		String preStr = cid.split("/")[0];
		if(preStr.equals("application")){//什么也不显示
			holder.imageView.setVisibility(View.GONE);
			holder.textView.setVisibility(View.GONE);
		}else if(preStr.equals("image")){//显示图片
			holder.imageView.setVisibility(View.VISIBLE);
			holder.textView.setVisibility(View.GONE);
			Bitmap bitmap = getBitmap("content://mms/part/" + mList.get(position)._id);
			holder.imageView.setImageBitmap(bitmap);
		}else if(preStr.equals("text")){//显示文本
			holder.imageView.setVisibility(View.GONE);
			holder.textView.setVisibility(View.VISIBLE);
			holder.textView.setText(mList.get(position).text);
		}else{
			//目前不支持读取视频或者音频文件
			holder.imageView.setVisibility(View.GONE);
			holder.textView.setVisibility(View.VISIBLE);
			holder.textView.setText("");
		}
		return convertView;
	}
	
	private class Holder {
		TextView textView;
		ImageView imageView;
	}
	
	private Bitmap getBitmap(String uri){
		//Bitmap bitmap = null;
		BitmapDrawable bd  = null;
		try {
			bd = new BitmapDrawable(mLayoutInflater.getContext().getContentResolver().openInputStream(Uri.parse(uri)));
			System.gc();
			//bitmap = BitmapFactory.decodeStream(mLayoutInflater.getContext().getContentResolver().openInputStream(Uri.parse(uri)));//图片大的时候会抛异常
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bd.getBitmap();
	}

}
