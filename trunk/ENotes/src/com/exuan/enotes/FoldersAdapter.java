package com.exuan.enotes;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CursorAdapter;
import android.widget.TextView;

public class FoldersAdapter extends CursorAdapter
{
	private Context mContext;
	private int mID;
	private boolean mIsDeleteMode;
	
	public FoldersAdapter(Context context, Cursor c) {
		super(context, c);
		// TODO Auto-generated constructor stub
		mContext = context;
		mID = 0;
		mIsDeleteMode = false;
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		// TODO Auto-generated method stub
		TextView textName = (TextView)view.findViewById(R.id.textview_folder);
		CheckBox checkDelete = (CheckBox)view.findViewById(R.id.checkbox_folder);
		int pos = cursor.getPosition();
		checkDelete.setOnClickListener(
				new CheckedListener(pos));
		if(mIsDeleteMode)
		{
			if(cursor.getInt(cursor.getColumnIndex("_id")) != 1)
			{
				checkDelete.setVisibility(View.VISIBLE);
				checkDelete.setChecked(ENotesActivity.mFolderCheck[pos]);
			}
			else
			{
				checkDelete.setVisibility(View.GONE);
			}
		}
		else
		{
			checkDelete.setVisibility(View.GONE);
		}
		textName.setText(cursor.getString(cursor.getColumnIndex(NotesDatabaseHelper.FOLDER_NAME)));
		if(mID == cursor.getInt(cursor.getColumnIndex("_id")))
		{
			view.setBackgroundResource(R.drawable.folder_select);
		}
		else
		{
			view.setBackgroundResource(R.drawable.transback);
		}
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		// TODO Auto-generated method stub
		View v = LayoutInflater.from(mContext).inflate(R.layout.list_folders_item, null);
    	return v;
	}
	
	private class CheckedListener implements OnClickListener
	{
		private int mPos;
		public CheckedListener(int id)
		{
			mPos = id;
		}
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			CheckBox cb = (CheckBox)v;
			if(!cb.isChecked())
			{
				cb.setChecked(false);
				ENotesActivity.mFolderCheck[mPos] = false;
			}
			else
			{
				cb.setChecked(true);
				ENotesActivity.mFolderCheck[mPos] = true;
			}
		}
		
	}
	
	public void updateData(Cursor c, int id)
	{
		mID = id;
		changeCursor(c);
		this.notifyDataSetChanged();
	}
	
	public void updateMode(boolean delete)
	{
		mIsDeleteMode = delete;
		this.notifyDataSetChanged();
	}
}