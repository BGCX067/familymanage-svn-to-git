package com.hammer.notes;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Formatter;
import java.util.List;
import com.hammer.notes.utils.ToolsUtil;
import com.hammer.notes.widget.DragSortListView;
import com.hammer.notes.widget.SoftEditText;
//import com.weibo.net.Weibo;

import android.net.Uri;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class NotesActivity extends Activity implements OnClickListener{
	private static final String TAG = "NotesActivity";
	private Button mModifyButton;
	private ImageView mAddButton;
	public DragSortListView mNotesListView;
	private Context mContext;
	public Cursor mNotesCursor;
	private EditText mSearchEditText;
	private QueryHandler mQueryHandler;
	private ImageView mDeleteBatchButton;
	private LinearLayout mSearchLinearLayout;
	public ImageView mSendImageView;
	private FrameLayout mListFrameLayout;
	private ImageView mDeleteSearchImageView;
	private LinearLayout mScrollLinearLayout;
	
	private static final int TOKEN_QUERY = 0;
	private static final int TOKEN_UPDATE = 1;
	
	private static final int REQUEST_CODE_SEND = 0;
	public String QUERY_EDIT_SELECTION = null;
	private boolean mIsDrop = false;
	private DragSortListView.DropListener onDrop =
	      new DragSortListView.DropListener() {
	        @Override
	        public void drop(int from, int to) {
	        	mNotesListView.setDragMode(false);
	        	if(from != to)
	        	{
	        		mIsDrop = true;
		        	mNotesCursor.moveToPosition(from);
		        	int from_p = mNotesCursor.getInt(mNotesCursor.getColumnIndex(NotesDatabaseHelper.POSITION));
		        	mNotesCursor.moveToPosition(to);
		        	int to_p = mNotesCursor.getInt(mNotesCursor.getColumnIndex(NotesDatabaseHelper.POSITION));
		        	ContentValues values = new ContentValues();
		        	values.put("from", from_p);
		        	values.put("to", to_p);
		        	mQueryHandler.startUpdate(TOKEN_UPDATE, null, NotesProvider.CONTENT_URI_NOTES, values, "UPDATE_POSITION", null);
	        	}
	        	else
	        	{
	        		mIsDrop = false;
	        		mNotesListView.removeFloat();
	        	}
	        }
	      };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_notes);
        mContext = this;
        mSearchLinearLayout = (LinearLayout) LayoutInflater.from(mContext).inflate(R.layout.search, null);
        mNotesListView = (DragSortListView)findViewById(R.id.listview_notes);
        mListFrameLayout = (FrameLayout) findViewById(R.id.framelayout_list);
        mAddButton = (ImageView)findViewById(R.id.button_add);
        mModifyButton = (Button)findViewById(R.id.button_modify);
        mSearchEditText = (EditText)mSearchLinearLayout.findViewById(R.id.edittext_search);
        mDeleteSearchImageView = (ImageView)mSearchLinearLayout.findViewById(R.id.imageview_delete);
        mDeleteBatchButton = (ImageView)findViewById(R.id.button_delete_batch);
        mSendImageView = (ImageView)findViewById(R.id.button_send);
        
        initAnimView();
        
        mSearchEditText.setCursorVisible(false);
        mSearchEditText.addTextChangedListener(mTextWatcher);
        mModifyButton.setOnClickListener(this);
        mAddButton.setOnClickListener(this);
        mDeleteBatchButton.setOnClickListener(this);
        mSearchEditText.setOnClickListener(this);
        mSendImageView.setOnClickListener(this);
        mDeleteSearchImageView.setOnClickListener(this);
        
        mSendImageView.setFocusable(true);
        mSearchEditText.setFocusableInTouchMode(true);
        mSearchEditText.requestFocus();
        
        mQueryHandler = new QueryHandler(getContentResolver());
        //TextView tv_empty = (TextView) findViewById(R.id.textview_empty);
        //mNotesListView.setEmptyView(tv_empty);
        mNotesListView.setDropListener(onDrop);
        mNotesListView.addHeaderView(mSearchLinearLayout, null, false);
        //mNotesAdapter = new NotesAdapter(mContext, mNotesCursor);
        mNotesListView.setAdapter();
        mQueryHandler.startQuery(TOKEN_QUERY, null, NotesProvider.CONTENT_URI_NOTES, null, QUERY_EDIT_SELECTION, null, NotesDatabaseHelper.POSITION + " DESC");
        /*
        new Thread(){
        	public void run()
        	{
        		for(int i = 0; i < 1000; i++)
        		{
	        		ContentValues values = new ContentValues();
	        		values.put(NotesDatabaseHelper.DETAIL, i);
	        		values.put(NotesDatabaseHelper.POSITION, (i+1));
	        		getContentResolver().insert(NotesProvider.CONTENT_URI_NOTES, values);
        		}
        	}
        }.start();*/
        mUpListView = (ListView)findViewById(R.id.listview_clip);
        TextView tv = new TextView(mContext);
        tv.setLayoutParams(new AbsListView.LayoutParams(1, 63));
        mUpListView.addHeaderView(tv, null, false);
        mUpAdapter = new UpCursorAdapter(this, mNotesCursor);
        mUpListView.setAdapter(mUpAdapter);
        //showItem(1);
        mUpListView.setOnTouchListener(new OnTouchListener(){

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				return true;
			}});
    }

    public void onConfigurationChanged(Configuration newConfig) 
    {
    	try 
    	{
    		super.onConfigurationChanged(newConfig);
    	}
    	catch (Exception ex)
    	{

    	}
    }
    
    public ListView mUpListView;
    public UpCursorAdapter mUpAdapter;
    public int mUpMode = MODE_NORMAL;
    public static final int MODE_NORMAL = -1;
    public static final int MODE_SHOW = -2;
    
    public class UpCursorAdapter extends CursorAdapter
    {
    	
		public UpCursorAdapter(Context context, Cursor c) {
			super(context, c);
			// TODO Auto-generated constructor stub
		}

		@Override
		public void bindView(View view, Context context, Cursor cursor) {
			// TODO Auto-generated method stub
			ViewHolder holder = (ViewHolder)view.getTag();
			if(MODE_NORMAL == mUpMode)
			{
				holder.mClipImageView.setBackgroundResource(R.drawable.clip_down);
			}
			else if(MODE_SHOW == mUpMode)
			{
				holder.mClipImageView.setBackgroundResource(R.drawable.clip_up);
			}
			else if(0 <= mUpMode)
			{
				if(mUpMode == cursor.getPosition())
				{
					holder.mClipImageView.setBackgroundResource(R.drawable.clip_up);
				}
				else
				{
					holder.mClipImageView.setBackgroundResource(R.drawable.clip_down);
				}
			}
		}

		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {
			// TODO Auto-generated method stub
			ViewHolder holder = new ViewHolder();
			View v = LayoutInflater.from(context).inflate(R.layout.list_clip_item, null);
			holder.mClipImageView = (ImageView) v.findViewById(R.id.imageview_clip);
			v.setTag(holder);
			return v;
		}
    	
		private class ViewHolder
		{
			ImageView mClipImageView;
		}
    }
  /*  
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_notes, menu);
        return true;
    }
*/
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
    	mNotesCursor = getContentResolver().query(NotesProvider.CONTENT_URI_NOTES, null, QUERY_EDIT_SELECTION, 
        		null, NotesDatabaseHelper.POSITION + " DESC");
    	mNotesListView.changeCursor();
    	super.onActivityResult(requestCode, resultCode, data);
    }
    
    private boolean mIsCursor = false;
    public boolean mIsInputMethodShow = false;
    
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		/*
		else if(v == mSinaButton)
		{
			String text = mNoteEditText.getEditableText().toString();
			if(null != text && text.trim().length() > 0)
			{
				mWeibo = Weibo.getInstance();
				mWeibo.setupConsumerConfig(CONSUMER_KEY, CONSUMER_SECRET);
				mWeibo.setRedirectUrl("http://www.jayce.com/piratenote");
				mWeibo.authorize((Activity) mContext, new AuthDialogListener());
			}
			else
			{
				Toast.makeText(mContext, getString(R.string.input_content), Toast.LENGTH_LONG).show();
			}
		}*/
		if(v == mDeleteSearchImageView)
		{
			mSearchEditText.setText("");
		}
		else if(v == mMaskEditText)
		{
			if(mID > 0)
			{
				if(mIsSpan)
				{
					mIsSpan = false;
					return;
				}
				else
				{
					mNoteEditText.setVisibility(View.VISIBLE);
					mMaskEditText.setVisibility(View.GONE);
					mNoteEditText.requestFocus();
					mNoteEditText.post(new Runnable() {
						@Override
						public void run() {
						InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
						imm.showSoftInput(mNoteEditText, InputMethodManager.SHOW_IMPLICIT);
						mIsInputMethodShow = true;
						}
						});
				}
			}
		}
		if(v == mNoteEditText)
		{	
			mNoteEditText.setFocusable(true);
			mNoteEditText.setFocusableInTouchMode(true);
			mNoteEditText.requestFocus();
			
			mNoteEditText.post(new Runnable() {
				@Override
				public void run() {
				InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.showSoftInput(mNoteEditText, InputMethodManager.SHOW_IMPLICIT);
				mIsInputMethodShow = true;
				}
				});
		}
		else if(v == mSearchEditText)
		{
			mSearchEditText.setCursorVisible(true);
			mIsCursor = true;
			mSearchEditText.setBackgroundResource(R.drawable.searchbox_actived);
		}
		else if(v == mAddButton)
		{
			showItem(0, -0.13f);
		}
		else if(v == mModifyButton)
		{
			if(mIsShowItem)
			{
				if(mIsAnimating)
				{
					return;
				}
				else
				{
					mAddButton.setVisibility(View.VISIBLE);
					mDeleteBatchButton.setVisibility(View.GONE);
					mSendImageView.setVisibility(View.GONE);
					mModifyButton.setText(getString(R.string.modify));
					mModifyButton.setEnabled(false);
					mAddButton.setEnabled(false);
					mIsShowItem = false;
					if(mIsInputMethodShow)
					{
						mIsInputMethodShow = false;
						InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
						imm.hideSoftInputFromWindow(mNoteEditText.getWindowToken(), 0);
						mHandler.postDelayed(new Runnable(){

							@Override
							public void run() {
								backToMain();
								mIsShowItem = false;
							}
						}, 250);
					}
					else
					{
						backToMain();
					}
				}
			}
			else if(mIsFoldMode)
			{
				mNotesListView.releaseFold();
				setFoldMode(false);
			}
			else
			{
				if(!mNotesListView.getDeleteMode())
				{
					setDeleteMode(true);
				}
				else
				{
					setDeleteMode(false);
				}
			}
		}
		else if(v == mDeleteBatchButton)
		{
			if(mIsShowItem)
			{
				if(mIsAnimating)
				{
					return;
				}
				else
				{
					mAddButton.setVisibility(View.VISIBLE);
					mDeleteBatchButton.setVisibility(View.GONE);
					mSendImageView.setVisibility(View.GONE);
					mModifyButton.setEnabled(false);
					mAddButton.setEnabled(false);
					if(mIsInputMethodShow)
					{
						mIsInputMethodShow = false;
						InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
						imm.hideSoftInputFromWindow(mNoteEditText.getWindowToken(), 0);
						mHandler.postDelayed(new Runnable(){

							@Override
							public void run() {
								// TODO Auto-generated method stub
								if(mID > 0)
								{
									mContext.getContentResolver().delete(NotesProvider.CONTENT_URI_NOTES, 
											//"_id='" + mID + "'", null);
											"rowid='" + mID + "'", null);
								}
								backToMain();
								mIsShowItem = false;
							}}, 250);
					}
					else
					{
						if(mID > 0)
						{
							mContext.getContentResolver().delete(NotesProvider.CONTENT_URI_NOTES, 
									//"_id='" + mID + "'", null);
									"rowid='" + mID + "'", null);
						}
						backToMain();
					}
				}
			}
			else if(mNotesListView.getDeleteMode())
			{
				List<Long> list = mNotesListView.getDeleteList();
				if(list.size() > 0)
				{
					StringBuilder sb = new StringBuilder("rowid in (");
					sb.append(list.get(0));
					for(int i = 1; i < list.size(); i++)
					{
						sb.append(",");
						sb.append(list.get(i));
					}
					sb.append(")");
					getContentResolver().delete(NotesProvider.CONTENT_URI_NOTES, sb.toString(), null);
				}
				String s = mSearchEditText.getText().toString();
				if(null != s && s.length() > 0)
				{
					QUERY_EDIT_SELECTION = NotesDatabaseHelper.DETAIL + " like '%" + s + "%'" ;
					mQueryHandler.startQuery(TOKEN_QUERY, null, NotesProvider.CONTENT_URI_NOTES, null, QUERY_EDIT_SELECTION, null, NotesDatabaseHelper.POSITION + " DESC");
				}
				else
				{
					QUERY_EDIT_SELECTION = null;
					mQueryHandler.startQuery(TOKEN_QUERY, null, NotesProvider.CONTENT_URI_NOTES, null, QUERY_EDIT_SELECTION, null, NotesDatabaseHelper.POSITION + " DESC");
				}
				setDeleteMode(false);
			}
		}
		else if(v == mSendImageView)
		{
			if(mIsShowItem)
			{
				if(mIsAnimating)
				{
					return;
				}
				else
				{
					if(mIsInputMethodShow)
					{
						mIsInputMethodShow = false;
						InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
						imm.hideSoftInputFromWindow(mNoteEditText.getWindowToken(), 0);
						mHandler.postDelayed(new Runnable(){

							@Override
							public void run() {
								// TODO Auto-generated method stub
								String text = mNoteEditText.getEditableText().toString();
								if(null != text && text.trim().length() > 0)
								{
									Intent intent = new Intent("android.intent.action.SEND");
									intent.setType("text/*");
									intent.putExtra("android.intent.extra.SUBJECT", mContext.getString(R.string.app_name));
									intent.putExtra("android.intent.extra.TEXT", text);
									Intent i = Intent.createChooser(intent, mContext.getString(R.string.send_via));
									((Activity)mContext).startActivityForResult(i, REQUEST_CODE_SEND);
								}
								else
								{
									Toast.makeText(mContext, getString(R.string.input_content), Toast.LENGTH_LONG).show();
								}
							}}, 250);
					}
					else
					{
						String text = mNoteEditText.getEditableText().toString();
						if(null != text && text.trim().length() > 0)
						{
							Intent intent = new Intent("android.intent.action.SEND");
							intent.setType("text/*");
							intent.putExtra("android.intent.extra.SUBJECT", mContext.getString(R.string.app_name));
							intent.putExtra("android.intent.extra.TEXT", text);
							Intent i = Intent.createChooser(intent, mContext.getString(R.string.send_via));
							((Activity)mContext).startActivityForResult(i, REQUEST_CODE_SEND);
						}
						else
						{
							Toast.makeText(mContext, getString(R.string.input_content), Toast.LENGTH_LONG).show();
						}
					}
				}
			}
			else if(mNotesListView.getDeleteList().size() == 1)
			{
				long id = mNotesListView.getDeleteList().get(0);
				Cursor c = getContentResolver().query(NotesProvider.CONTENT_URI_NOTES, null, "rowid='" + id + "'", null, null);
				if(null != c)
				{
					if(c.getCount() > 0)
					{
						c.moveToNext();
						String detail = c.getString(c.getColumnIndex(NotesDatabaseHelper.DETAIL));
						if(null != detail && detail.trim().length() > 0)
						{
							Intent intent = new Intent("android.intent.action.SEND");
							intent.setType("text/*");
							intent.putExtra("android.intent.extra.SUBJECT", mContext.getString(R.string.app_name));
							intent.putExtra("android.intent.extra.TEXT", detail);
							Intent i = Intent.createChooser(intent, mContext.getString(R.string.send_via));
							((Activity)mContext).startActivityForResult(i, REQUEST_CODE_SEND);
						}
					}
					c.close();
				}
			}
		}
	}
	
	private void setDeleteMode(boolean b)
	{
		mNotesListView.setDeleteMode(b);
		mDeleteBatchButton.setVisibility(b ? View.VISIBLE : View.GONE);
		mSendImageView.setVisibility(b ? View.VISIBLE : View.GONE);
		mAddButton.setVisibility(b ? View.GONE : View.VISIBLE);
		mUpListView.setVisibility(b ? View.GONE : View.VISIBLE);
		mModifyButton.setText(b ? getString(R.string.cancel) : getString(R.string.modify));
		if(b)
		{
			mSendImageView.setEnabled(false);
			mSendImageView.setAlpha(100);
		}
	}
	
	public void onDestroy()
	{
		super.onDestroy();
		mNotesCursor.close();
	}
	
	private TextWatcher mTextWatcher = new TextWatcher(){

		@Override
		public void afterTextChanged(Editable s) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			// TODO Auto-generated method stub
			mNotesListView.releaseFoldForce();
			mQueryHandler.cancelOperation(0);
			if(null != s && s.length() > 0)
			{
				mDeleteSearchImageView.setVisibility(View.VISIBLE);
				QUERY_EDIT_SELECTION = NotesDatabaseHelper.DETAIL + " like '%" + s + "%'" ;
				mQueryHandler.startQuery(TOKEN_QUERY, null, NotesProvider.CONTENT_URI_NOTES, null, QUERY_EDIT_SELECTION, null, NotesDatabaseHelper.POSITION + " DESC");
			}
			else
			{
				mDeleteSearchImageView.setVisibility(View.GONE);
				QUERY_EDIT_SELECTION = null;
				mQueryHandler.startQuery(TOKEN_QUERY, null, NotesProvider.CONTENT_URI_NOTES, null, QUERY_EDIT_SELECTION, null, NotesDatabaseHelper.POSITION + " DESC");
			}
		}};
		
	private class QueryHandler extends AsyncQueryHandler {

        public QueryHandler(ContentResolver cr) {
            super(cr);
        }

        @Override
        protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
        	mNotesCursor = cursor;
        	mNotesListView.changeCursor();
        	if(mIsDrop)
        	{
        		mIsDrop = false;
        		mNotesListView.removeFloat();
        	}
        }
        
        protected void onUpdateComplete(int token, Object cookie, int result)
        {
        	mQueryHandler.startQuery(TOKEN_QUERY, null, NotesProvider.CONTENT_URI_NOTES, null, QUERY_EDIT_SELECTION, null, NotesDatabaseHelper.POSITION + " DESC");
        }
	}
	
	private void backToMain()
	{
		if(mIsAnimating)
		{
			return;
		}
		mIsAnimating = true;
		//mEditScrollView.setVisibility(View.VISIBLE);
		
		mScrollLinearLayout.setVisibility(View.VISIBLE);
		if(mID > 0)
		{
			mUpMode = mNotesListView.getTapIndex();
			mUpAdapter.notifyDataSetChanged();
		}
		mNoteSurfaceView.setVisibility(View.VISIBLE);
    	mHandler.post(new Runnable(){

			@Override
			public void run() {
				// TODO Auto-generated method stub
				final Bitmap bitmap = ToolsUtil.takeScreenshot(mScrollLinearLayout);//(mEditScrollView);
				//mEditScrollView.setVisibility(View.INVISIBLE);
				mScrollLinearLayout.setVisibility(View.INVISIBLE);
				mNoteSurfaceView.queueEvent(new Runnable()
				{
					@Override
					public void run()
					{
						mNoteRenderer.switchMode();
						mNoteRenderer.loadTexture(bitmap);
					}
				});
				if(mID > 0)
				{
					mNotesListView.hideTapChild();
				}
			}});
    	mIsLeft = false;
		mIsRightAnimate = true;
		saveItem();
		mListFrameLayout.setVisibility(View.VISIBLE);
		mNotesCursor = getContentResolver().query(NotesProvider.CONTENT_URI_NOTES, null, QUERY_EDIT_SELECTION, 
        		null, NotesDatabaseHelper.POSITION + " DESC");
    	mNotesListView.changeCursor();
		Animation anim_l = new TranslateAnimation(-480, 0, 0, 0);
		anim_l.setDuration(300);
		mListFrameLayout.startAnimation(anim_l);
	}
	
	private void saveItem()
	{
		if(mID > 0)
		{
			String text = mNoteEditText.getEditableText().toString();
			if(null != text && text.trim().length() > 0)
			{
				if(!text.equals(mOldDetail))
				{
					ContentValues values = new ContentValues();
					values.put(NotesDatabaseHelper.DETAIL, text);
					values.put(NotesDatabaseHelper.MODIFY_TIME, System.currentTimeMillis());
					getContentResolver().update(NotesProvider.CONTENT_URI_NOTES, values, 
							//"_id='" + mID + "'", null);
							"rowid='" + mID + "'", null);
				}
			}
			else
			{
				//getContentResolver().delete(NotesProvider.CONTENT_URI_NOTES, "_id='" + mID + "'", null);
				getContentResolver().delete(NotesProvider.CONTENT_URI_NOTES, "rowid='" + mID + "'", null);
			}
		}
		else
		{
			String text = mNoteEditText.getEditableText().toString();
			if(null != text && text.trim().length() > 0)
			{
				ContentValues values = new ContentValues();
				values.put(NotesDatabaseHelper.DETAIL, text);
				values.put(NotesDatabaseHelper.MODIFY_TIME, System.currentTimeMillis());
				Cursor c = getContentResolver().query(NotesProvider.CONTENT_URI_NOTES, null, null, null, null);
				int pos = 1;
				if(null != c)
				{
					pos += c.getCount();
					c.close();
				}
				getContentResolver().insert(NotesProvider.CONTENT_URI_NOTES, values);
			}
		}
	}
	
	public void onBackPressed()
	{
		mIsInputMethodShow = false;
		if(mIsShowItem)
		{
			if(mIsAnimating)
			{
				return;
			}
			else
			{
				mAddButton.setVisibility(View.VISIBLE);
				mDeleteBatchButton.setVisibility(View.GONE);
				mSendImageView.setVisibility(View.GONE);
				mModifyButton.setText(getString(R.string.modify));
				mModifyButton.setEnabled(false);
				mAddButton.setEnabled(false);
				backToMain();
			}
		}
		else if(null != mSearchEditText.getText().toString() && mSearchEditText.getText().toString().trim().length() > 0)
		{
			mIsCursor = false;
			mSearchEditText.setText("");
			mSearchEditText.setCursorVisible(false);
			mSearchEditText.setBackgroundResource(R.drawable.searchbox_normal);
			mQueryHandler.cancelOperation(TOKEN_QUERY);
			QUERY_EDIT_SELECTION = null;
			mQueryHandler.startQuery(TOKEN_QUERY, null, NotesProvider.CONTENT_URI_NOTES, null, QUERY_EDIT_SELECTION, null, NotesDatabaseHelper.POSITION + " DESC");
		}
		else if(mIsCursor)
		{
			mSearchEditText.setCursorVisible(false);
			mSearchEditText.setBackgroundResource(R.drawable.searchbox_normal);
			mIsCursor = false;
		}
		else if(mNotesListView.getDeleteMode())
		{
			setDeleteMode(false);
		}  
		else
		{
			super.onBackPressed();
		}
	}
	
	private boolean mIsFoldMode = false;
	
	public void setFoldMode(boolean b)
	{
		mIsFoldMode = b;
		mModifyButton.setText(mIsFoldMode ? getString(R.string.cancel) : getString(R.string.modify));
		mAddButton.setVisibility(mIsFoldMode ? View.GONE : View.VISIBLE);
	}
	
	private boolean mIsShowItem = false;
	
	private void initAnimView()
	{
		mNoteSurfaceView = (GLSurfaceView) findViewById(R.id.surfaceview);
		mNoteEditText = (SoftEditText) findViewById(R.id.edittext_note);
        mAnimTextView = (EditText)findViewById(R.id.edittext_anim);
        mMaskEditText = (EditText)findViewById(R.id.edittext_mask);
        mAgoTextView = (TextView)findViewById(R.id.textview_ago);
        mTimeTextView = (TextView)findViewById(R.id.textview_time);
        mEditScrollView = (ScrollView)findViewById(R.id.scrollview_edit);
        mScrollLinearLayout = (LinearLayout) findViewById(R.id.linearlayout_edit_scroll);
        
        mNoteEditText.setOnClickListener(this);
        mMaskEditText.setOnClickListener(this);
        
        mNoteSurfaceView.setEGLContextClientVersion(2);
        mNoteSurfaceView.setEGLConfigChooser(8, 8, 8, 8, 16, 0);
        mNoteSurfaceView.getHolder().setFormat(PixelFormat.TRANSLUCENT);
        mNoteSurfaceView.setZOrderOnTop(true);
        mNoteRenderer = new NoteRenderer(mContext, mHandler);
        mNoteSurfaceView.setRenderer(mNoteRenderer);
	}
	
	private float mTranslatePercent = 0f;
	
	public boolean mIsAnimating = false;
	
	public void showItem(long id, float percent)
	{
		if(mIsAnimating)
		{
			return;
		}
		mModifyButton.setText(getString(R.string.back));
		mAddButton.setVisibility(View.GONE);
		mDeleteBatchButton.setVisibility(View.VISIBLE);
		mSendImageView.setVisibility(View.VISIBLE);
		mModifyButton.setEnabled(false);
		mDeleteBatchButton.setEnabled(false);
		mSendImageView.setEnabled(false);
		
		mIsAnimating = true;
		mTranslatePercent = percent;
		mIsLeftAnimate = true;
		mIsShowItem = true;
		mIsLeft = true;
		//mEditScrollView.setVisibility(View.VISIBLE);
		mScrollLinearLayout.setVisibility(View.VISIBLE);
		mAnimTextView.setVisibility(View.VISIBLE);
		mMaskEditText.setVisibility(View.GONE);
		mNoteEditText.setVisibility(View.GONE);
		mNoteSurfaceView.setVisibility(View.VISIBLE);
        
        mID = id;
        long time = Calendar.getInstance().getTimeInMillis();
        if(mID > 0)
        {
        	Cursor c = getContentResolver().query(NotesProvider.CONTENT_URI_NOTES, null, 
        			//"_id ='" + mID + "'", null, null);
        			"rowid ='" + mID + "'", null, null);
        	if(null != c && c.getCount() > 0)
        	{
        		c.moveToNext();
        		mOldDetail = c.getString(c.getColumnIndex(NotesDatabaseHelper.DETAIL));
        		mAnimTextView.setText(mOldDetail);
        		mNoteEditText.setText(mOldDetail);
        		mMaskEditText.setText(mOldDetail + "\n");
        		int len = mOldDetail.length();
    			mNoteEditText.setSelection(len);
        		CharSequence text = mMaskEditText.getText();  
                if (text instanceof Spannable) {  
                    int end = text.length();
                    Spannable sp = (Spannable) mMaskEditText.getText();  
                    URLSpan[] spans = sp.getSpans(0, end, URLSpan.class);  
                    SpannableStringBuilder style = new SpannableStringBuilder(text);  
                    style.clearSpans();// should clear old spans  
                    for (URLSpan span : spans) {  
                    	MySpan mySpan = new MySpan(span.getURL());  
                        style.setSpan(mySpan, sp.getSpanStart(span), sp.getSpanEnd(span), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);  
                    }  
                    mMaskEditText.setText(style); 
                }
                
                time = c.getLong(c.getColumnIndex(NotesDatabaseHelper.MODIFY_TIME));
                mAgoTextView.setText(ToolsUtil.buildAgoText(mContext, time));
        	}
        }
        else
        {
        	mOldDetail = "";
        	mMaskEditText.setText(mOldDetail);
        	mNoteEditText.setText(mOldDetail);
        	mAnimTextView.setText(mOldDetail);
        	//mAnimTextView.setVisibility(View.GONE);
        	mMaskEditText.setVisibility(View.GONE);
        	mNoteEditText.setVisibility(View.GONE);
        }
        mStringBuilder = new StringBuilder(50);
        mFormatter = new Formatter(mStringBuilder);
        mTimeTextView.setText(ToolsUtil.buildTime(time, mContext, mFormatter, mStringBuilder));
		mHandler.post(new Runnable(){

			@Override
			public void run() {
				// TODO Auto-generated method stub
				final Bitmap bitmap = ToolsUtil.takeScreenshot(mScrollLinearLayout);//(mEditScrollView);
				//mEditScrollView.setVisibility(View.INVISIBLE);
				mScrollLinearLayout.setVisibility(View.INVISIBLE);
				mNoteSurfaceView.queueEvent(new Runnable()
				{
					@Override
					public void run()
					{
						mNoteRenderer.setTranslatePercent(mTranslatePercent);
						mNoteRenderer.loadTexture(bitmap);
					}
				});
				if(mID > 0)
				{
					mNotesListView.hideTapChild();
				}
			}});
        Animation anim = new TranslateAnimation(0, -480, 0, 0);
		anim.setDuration(300);
		anim.setAnimationListener(mAnimListener);
		mListFrameLayout.startAnimation(anim);
	}
	
	private Handler mHandler = new Handler(){
		public void handleMessage(Message msg)
   	 	{
			switch(msg.what)
   		 	{
   		 		case MSG_ANIMATE_END_LEFT:
	    		{
	    			if(mIsLeftAnimate)
	    			{
	    				mIsLeftAnimate = false;
	    				if(mID > 0)
	    				{
	    					mMaskEditText.setVisibility(View.VISIBLE);
	    				}
	    				else
	    				{
	    					mNoteEditText.setVisibility(View.VISIBLE);
	    				}
	    				mAnimTextView.setVisibility(View.GONE);
		    			//mEditScrollView.setVisibility(View.VISIBLE);
	    				mScrollLinearLayout.setVisibility(View.VISIBLE);
		        		new Thread(){
							public void run()
		        			{
		        				try {
		        					sleep(50);
		        					mHandler.sendEmptyMessage(MSG_ANIMATE_HIDE_LEFT);
		    					} catch (InterruptedException e) {
		    						// TODO Auto-generated catch block
		    						e.printStackTrace();
		    					}
		        			}
		        		}.start();
	    			}
	    		}
	    		break;
	    		case MSG_ANIMATE_HIDE_LEFT:
	    		{
	    			//mEditScrollView.
		    		mNoteSurfaceView.queueEvent(new Runnable(){
	
						@Override
						public void run() {
							// TODO Auto-generated method stub
							mNoteRenderer.releaseTexture();
						}});
	    			mNoteSurfaceView.setVisibility(View.GONE);
	    			if(mID <= 0)
	    			{
	    				mNoteEditText.requestFocus();
		    			mNoteEditText.post(new Runnable() {
							@Override
							public void run() {
							InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
							imm.showSoftInput(mNoteEditText, InputMethodManager.SHOW_IMPLICIT);
							mIsInputMethodShow = true;
							}
							});
	    			}
	    			mModifyButton.setEnabled(true);
	    			mDeleteBatchButton.setEnabled(true);
	    			mSendImageView.setEnabled(true);
	    			mHandler.postDelayed(new Runnable(){

						@Override
						public void run() {
							// TODO Auto-generated method stub
							mIsAnimating = false;
						}}, 100);
	    			
	    		}
	    		break;
	    		case MSG_ANIMATE_END_RIGHT:
	    		{
	    			if(mIsRightAnimate)
	    			{
	    				mIsRightAnimate = false;
	    				if(mID > 0)
	    				{
	    					mNotesListView.showTapChild();
	    				}
		    			new Thread(){
							public void run()
		        			{
		        				try {
		        					sleep(50);
		        					mHandler.sendEmptyMessage(MSG_ANIMATE_HIDE_RIGHT);
		    					} catch (InterruptedException e) {
		    						// TODO Auto-generated catch block
		    						e.printStackTrace();
		    					}
		        			}
		        		}.start();
	    			}
	    		}
	    		break;
	    		case MSG_ANIMATE_HIDE_RIGHT:
	    		{
	    			mNoteSurfaceView.queueEvent(new Runnable(){

						@Override
						public void run() {
							// TODO Auto-generated method stub
							mNoteRenderer.releaseTexture();
						}});
	    			mNoteSurfaceView.setVisibility(View.GONE);
	    			//mEditScrollView.setVisibility(View.VISIBLE);
	    			mScrollLinearLayout.setVisibility(View.VISIBLE);
	    			mModifyButton.setEnabled(true);
	    			mAddButton.setEnabled(true);
	    			mUpMode = MODE_NORMAL;
	    			mUpAdapter.notifyDataSetChanged();
	    			mHandler.postDelayed(new Runnable(){

						@Override
						public void run() {
							// TODO Auto-generated method stub
							mIsAnimating = false;
						}}, 100);
	    			mIsShowItem = false;
	    		}
	    		break;
   		 	}
   	 	}
	};
	
	private boolean mIsLeft = false;
	
	private Animation.AnimationListener mAnimListener = new Animation.AnimationListener(){

		@Override
		public void onAnimationEnd(Animation arg0) {
			// TODO Auto-generated method stub
			if(mIsLeft)
			{
				mListFrameLayout.setVisibility(View.GONE);
			}
		}

		@Override
		public void onAnimationRepeat(Animation animation) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onAnimationStart(Animation animation) {
			// TODO Auto-generated method stub
			
		}};
	
    @Override
    protected void onResume() 
    {
        // The activity must call the GL surface view's onResume() on activity onResume().
        super.onResume();
        if(null != mNoteSurfaceView)
        {
        	mNoteSurfaceView.onResume();
        }
    }

    @Override
    protected void onPause() 
    {
        // The activity must call the GL surface view's onPause() on activity onPause().
        super.onPause();
        if(null != mNoteSurfaceView)
        {
        	mNoteSurfaceView.onPause();
        }
    }
	
    private  class MySpan extends ClickableSpan {  
		  
        private String mSpan;  
  
        MySpan(String span) {  
        	mSpan = span;  
        }  
  
        @Override  
        public void onClick(View widget) {  
        	mIsSpan = true;
        	Intent intent = new Intent(Intent.ACTION_VIEW);
        	intent.setData(Uri.parse(mSpan));
        	startActivity(intent);
        }  
    }  
	
	//private RelativeLayout mMainRelativeLayout;
	private SoftEditText mNoteEditText;
	private ScrollView mEditScrollView;
	private TextView mTitleEditText;
	FrameLayout mEditLinearLayout;
	GLSurfaceView mNoteSurfaceView;
	NoteRenderer mNoteRenderer;
	private Button mBackButton;
	//private Button mSaveButton;
	//private NoteEditFrameLayout mFrameLayout;
	private TextView mMaskTextView;
	private ImageView mSendButton;
	private ImageView mDeleteButton;
	//private Button mSinaButton;
	private EditText mAnimTextView;
	private EditText mMaskEditText;
	private TextView mAgoTextView;
	private TextView mTimeTextView;
	
	//private Weibo mWeibo;
	private static final String CONSUMER_KEY = "406618199";
	private static final String CONSUMER_SECRET = "2aee355937e3124ac3deb1fa13c16f17";
	
	public static final int MSG_ANIMATE_END_LEFT = 0;
	public static final int MSG_ANIMATE_HIDE_LEFT = 1;
	public static final int MSG_ANIMATE_END_RIGHT = 2;
	public static final int MSG_ANIMATE_HIDE_RIGHT = 3;
	private Formatter mFormatter;
    private StringBuilder mStringBuilder;
	private long mID = 0;
	private String mOldDetail;
	private boolean mIsSpan = false;
	private boolean mIsLeftAnimate = false;
	private boolean mIsRightAnimate = false;
	
	
}
