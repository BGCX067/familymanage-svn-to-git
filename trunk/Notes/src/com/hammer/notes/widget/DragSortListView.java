package com.hammer.notes.widget;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.Log;
import android.view.*;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.*;

import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;
import com.hammer.notes.NotesActivity;
import com.hammer.notes.NotesDatabaseHelper;
import com.hammer.notes.NotesProvider;
import com.hammer.notes.R;
import com.hammer.notes.utils.ToolsUtil;

public class DragSortListView extends ListView {
	
	private RelativeLayout mCurrentItem;
	private int mCurrentPosition;
	// Drag states
	public final static int NO_DRAG = 0;
	public final static int SRC_EXP = 1;
	public final static int SRC_ABOVE = 2;
	public final static int SRC_BELOW = 3;
	private int mDragState = NO_DRAG;
	
	private ArrayList<Integer> mHeaderHeights = new ArrayList<Integer>();
	private int mHeadersTotalHeight = 0;
	
	private ImageView mFloatView;
	private int mFloatBGColor;
	private WindowManager mWindowManager;
	private WindowManager.LayoutParams mWindowParams;
	private long mID = 0;
	
	private int mExpDragPos;
	private int mSrcDragPos;
	private int mDragPointX;    // at what x offset inside the item did the user grab it
	private int mDragPointY;    // at what y offset inside the item did the user grab it
	private int mXOffset;  // the difference between screen coordinates and coordinates in this view
	private int mYOffset;  // the difference between screen coordinates and coordinates in this view
	private DragListener mDragListener;
	private DropListener mDropListener;
	private int mUpScrollStartY;
	private int mDownScrollStartY;
	private float mDownScrollStartYF;
	private float mUpScrollStartYF;
	private GestureDetector mGestureDetector;
	private Rect mTempRect = new Rect();
	private Bitmap mDragBitmap;
	//private final int mTouchSlop;
	private int mItemHeightCollapsed = 1;
	private int mExpandedChildHeight; //state var
	private int mFloatViewHeight;
	private int mFloatViewHeightHalf;
	private Drawable mTrashcan;

	private View[] mSampleViewTypes = new View[1];

	private DragScroller mDragScroller;
	private float mDragUpScrollStartFrac = 1.0f / 3.0f;
	private float mDragDownScrollStartFrac = 1.0f / 3.0f;
	private float mDragUpScrollHeight;
	private float mDragDownScrollHeight;
	
	private float mMaxScrollSpeed = 0.3f; // pixels per millisec
	private DragScrollProfile mScrollProfile = new DragScrollProfile() {
		@Override
		public float getSpeed(float w, long t) {
			return mMaxScrollSpeed * w;
		}
	};
	
	private int mLastY;
	private int mDownY;
	private Context mContext;
	private NotesActivity mActivity;
	private AdapterWrapper mAdapterWrapper;
	private static final int TO_RIGHT = 0;
	private static final int TO_LEFT = 1;
	private int mDirection = -1;
	private View mRightView = null;
	private boolean mIsAnimating = false;
	
	public DragSortListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		mActivity = (NotesActivity)mContext;
		if (attrs != null) {
			TypedArray a = getContext().obtainStyledAttributes(attrs,
					R.styleable.DragSortListView, 0, 0);

			mItemHeightCollapsed = a.getDimensionPixelSize(
					R.styleable.DragSortListView_collapsed_height, mItemHeightCollapsed);

			mFloatBGColor = a.getColor(R.styleable.DragSortListView_float_background_color,
					0x00000000);

			float frac = a.getFloat(R.styleable.DragSortListView_drag_scroll_start,
					mDragUpScrollStartFrac);
			setDragScrollStart(frac);

			mMaxScrollSpeed = a.getFloat(
					R.styleable.DragSortListView_max_drag_scroll_speed, mMaxScrollSpeed);

			a.recycle();
		}
		//this.setSmoothScrollbarEnabled(true);
		mDragScroller = new DragScroller();
		setOnScrollListener(mDragScroller);
		
		mGestureDetector = new GestureDetector(getContext(), new SimpleOnGestureListener() {
			
			@Override
	        public boolean onSingleTapUp(MotionEvent e) {
	        	onSingleTap(e);
	            return true;
	        }
			
			public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY)
			{
				if(mIsDeleteMode)
				{
					return true;
				}
				int distanceX = (int) e1.getX() - (int) e2.getX();
	            int distanceY = (int) e1.getY() - (int) e2.getY();
	            int x = Math.abs(distanceX);
	            int y = Math.abs(distanceY);
	        	if(x > y)
	        	{
	        		
	        		if(distanceX < 0)//to right
	        		{
	        			/*
	        			int w = mCurrentItem.getWidth() / 2;
	        			//if(e1.getX() < w)
	        			{
	        				LinearLayout ll = (LinearLayout) mCurrentItem.findViewById(R.id.linearlayout_detail);
	        	            LinearLayout le = (LinearLayout) mCurrentItem.findViewById(R.id.linearlayout_edit);
	        	            //ImageView iv = (ImageView) mCurrentItem.findViewById(R.id.imageview_clip);
	        				mDirection = TO_RIGHT;
	        				mActivity.setFoldMode(true);
		        			if(ll.getLeft() < w)
		        			{
		        				Animation anim = new TranslateAnimation(ll.getLeft(), w, 0, 0);
		        				anim.setDuration(300);
		        				anim.setFillAfter(true);
		        				ll.startAnimation(anim);
		        				mRightView = mCurrentItem;
		        			}
		        			le.setVisibility(View.VISIBLE);
		        			mActivity.mUpMode = mCurrentPosition;
		    				mActivity.mUpAdapter.notifyDataSetChanged();
		        			//iv.setBackgroundResource(R.drawable.clip_up);
	        			}
	        			*/
	        			hScrollToRight();
	        		}
	        		else
	        		{
	        			/*
	        			LinearLayout ll = (LinearLayout) mCurrentItem.findViewById(R.id.linearlayout_edit_mask);
	        			if(ll.getAlpha() < 0.5)
	        			{
	        				ll.setAlpha(ll.getAlpha() + 0.05f);
	        			}
	        			*/
	        			hScrollToLeft();
	        		}
	        	}
				return true;
			}
			
			public boolean onScroll(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY)
	        {
				if(mIsDeleteMode)
				{
					return true;
				}
				if(mDirection == TO_RIGHT)
				{
					return true;
				}
	        	int distanceX = (int) e1.getX() - (int) e2.getX();
	            int distanceY = (int) e1.getY() - (int) e2.getY();
	            int x = Math.abs(distanceX);
	            int y = Math.abs(distanceY);
	            
	        	if(x > y)
	        	{
	        		if(distanceX < 0)//to right
	        		{
	        			/*
	        			int w = mCurrentItem.getWidth() / 2;
	        			LinearLayout ll = (LinearLayout) mCurrentItem.findViewById(R.id.linearlayout_detail);
        	            LinearLayout le = (LinearLayout) mCurrentItem.findViewById(R.id.linearlayout_edit);
        	            //ImageView iv = (ImageView) mCurrentItem.findViewById(R.id.imageview_clip);
        				mDirection = TO_RIGHT;
        				mActivity.setFoldMode(true);
	        			if(ll.getLeft() < w)
	        			{
	        				Animation anim = new TranslateAnimation(ll.getLeft(), w, 0, 0);
	        				anim.setDuration(300);
	        				anim.setFillAfter(true);
	        				ll.startAnimation(anim);
	        				anim.setAnimationListener(mAnimateListener);
	        				mRightView = mCurrentItem;
	        			}
	        			le.setVisibility(View.VISIBLE);
	    				mActivity.mUpMode = mCurrentPosition;
	    				mActivity.mUpAdapter.notifyDataSetChanged();
	        			//iv.setBackgroundResource(R.drawable.clip_up);
	        			 * 
	        			 */
	        			hScrollToRight();
	        		}
	        		else
	        		{
	        			/*
	        			LinearLayout ll = (LinearLayout) mCurrentItem.findViewById(R.id.linearlayout_edit_mask);
	        			if(ll.getAlpha() < 0.5)
	        			{
	        				ll.setAlpha(ll.getAlpha() + 0.005f);
	        			}
	        			*/
	        			hScrollToLeft();
	        		}
	        	}
	        	return true;
	        }
			
			public void onLongPress(MotionEvent e)
	        {
				if(mIsDeleteMode)
				{
					return;
				}
				LinearLayout ll = (LinearLayout) mCurrentItem.findViewById(R.id.linearlayout_detail);
				if(mActivity.mUpMode != NotesActivity.MODE_SHOW && null != mActivity && null != mActivity.mUpListView)
				{
					mActivity.mUpMode = NotesActivity.MODE_SHOW;
					mActivity.mUpAdapter.notifyDataSetChanged();
				}
				mIsLongPress = true;
				int x = mDragX;
				int y = mDragY;
				mLastY = y;
				mDownY = y;
				final int numHeaders = getHeaderViewsCount();
				if ((mCurrentPosition + numHeaders) == AdapterView.INVALID_POSITION || mCurrentPosition < 0 || (mCurrentPosition + numHeaders) >= getCount()) {
					return;//break;
				}
				
				mDragPointX = x - mCurrentItem.getLeft();
				mDragPointY = y - mCurrentItem.getTop();
		        final int rawX = (int) e.getRawX();
		        final int rawY = (int) e.getRawY();
				mXOffset = rawX - x;
				mYOffset = rawY - y;

				ll.setDrawingCacheEnabled(true);
				// Create a copy of the drawing cache so that it does not get recycled
				// by the framework when the list tries to clean up memory
				Bitmap bitmap = Bitmap.createScaledBitmap(ll.getDrawingCache(), 
						ll.getDrawingCache().getWidth(), ll.getDrawingCache().getHeight(), true);
				//Bitmap bitmap = Bitmap.createBitmap(item.getDrawingCache());
				ll.setDrawingCacheEnabled(false);

			    mFloatViewHeight = mCurrentItem.getHeight();
			    mFloatViewHeightHalf = mFloatViewHeight / 2;
				
				mExpDragPos = mCurrentPosition + numHeaders;
				mSrcDragPos = mCurrentPosition + numHeaders;
				setDragMode(true);
				startDragging(bitmap, x, y);
	        }
		});
		
	}

	private NotesAdapter mAdapter;
	public boolean mIsLongPress = false;
	
	public void setMaxScrollSpeed(float max) {
		mMaxScrollSpeed = max;
	}
	
	public void setAdapter() {
		mAdapter = new NotesAdapter(mContext, mActivity.mNotesCursor);
		mAdapterWrapper = new AdapterWrapper(null, null, mAdapter);
		super.setAdapter(mAdapterWrapper);
		dispatchTouchEvent(MotionEvent.obtain(0, 0, MotionEvent.ACTION_CANCEL, 0f, 0f, 0));
	}
	
	public ListAdapter getInputAdapter() {
		if (mAdapterWrapper == null) {
			return null;
		} else {
			return mAdapterWrapper.getAdapter();
		}
	}

	
	private class AdapterWrapper extends HeaderViewListAdapter {
		private ListAdapter mAdapter;
		
		public AdapterWrapper(ArrayList<FixedViewInfo> headerViewInfos,
				ArrayList<FixedViewInfo> footerViewInfos, ListAdapter adapter) {
			super(headerViewInfos, footerViewInfos, adapter);
			mAdapter = adapter;
		}
		
		public ListAdapter getAdapter() {
			return mAdapter;
		}

	
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			RelativeLayout v;
			View child;
			RelativeLayout.LayoutParams pa = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
			if (convertView != null) {

				v = (RelativeLayout) convertView;
				View oldChild = v.getChildAt(0);

				child = mAdapter.getView(position, oldChild, v);
				if (child != oldChild) {
					v.removeViewAt(0);
					child.setLayoutParams(pa);
					v.addView(child);
				}

			} else {
				AbsListView.LayoutParams params =
					new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT,
							AbsListView.LayoutParams.WRAP_CONTENT);
				v = new RelativeLayout(getContext());
				v.setLayoutParams(params);
				child = mAdapter.getView(position, null, v);
				child.setLayoutParams(pa);
				v.addView(child);
			}
			ViewGroup.LayoutParams lp = v.getLayoutParams();
			final int numHeaders = getHeaderViewsCount();
			final int srcAdapter = mSrcDragPos - numHeaders;
			final int expAdapter = mExpDragPos - numHeaders;
			boolean itemIsNormal = position != srcAdapter && position != expAdapter;
			boolean listHasExpPos = mDragState == SRC_ABOVE || mDragState == SRC_BELOW;
			boolean itemNeedsWC = itemIsNormal || !listHasExpPos;
			int oldHeight = lp.height;
			if (itemNeedsWC && lp.height != ViewGroup.LayoutParams.WRAP_CONTENT) {
				lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
			} else if (listHasExpPos) {
				if (position == srcAdapter && lp.height != mItemHeightCollapsed) {
					lp.height = mItemHeightCollapsed;
				} else if (position == expAdapter) {
					int spec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
					child.measure(spec, spec);

					mExpandedChildHeight = child.getMeasuredHeight();
					int height = mExpandedChildHeight + mFloatViewHeight;
					if (lp.height != height) {
						lp.height = height;
					}

					if (mDragState == SRC_ABOVE) {
						v.setGravity(Gravity.TOP);
					} else {
						v.setGravity(Gravity.BOTTOM);
					}
				}
			}

			if (lp.height != oldHeight) {
				v.setLayoutParams(lp);
			}

      		final int itemnum = position;
			int oldVis = v.getVisibility();
			int vis = oldVis;

			if (position == srcAdapter && mDragState != NO_DRAG) {
				if (vis == View.VISIBLE) {
					vis = View.INVISIBLE;
				}
			} else if (vis == View.INVISIBLE) {
				vis = View.VISIBLE;
			}

			if (vis != oldVis) {
				v.setVisibility(vis);
			}
			v.setClickable(true);
			v.setLongClickable(true);
			v.setOnTouchListener(new OnTouchListener(){

				@Override
				public boolean onTouch(View v, MotionEvent event) {
					// TODO Auto-generated method stub
					if(mIsScrollEvent && mPresEventType == MotionEvent.ACTION_MOVE && MotionEvent.ACTION_CANCEL == event.getAction())
					{
						mIsScrollEvent = true;
						mPresEventType = MotionEvent.ACTION_CANCEL;
					}
					else
					{
						mIsScrollEvent = false;
					}
					mIsInterDown = false;
					if(null != mRightView && mIsRight)
					{
						mIsTouchOnAnimate = true;
						releaseFold();
						return true;
					}
					
					mCurrentItem = (RelativeLayout) v;
					mCurrentPosition = itemnum;
					return mGestureDetector.onTouchEvent(event);
				}});
			return v;
		}
	}

	private int getItemHeight(int position) {

		final int first = getFirstVisiblePosition();
		final int last = getLastVisiblePosition();

		if (position >= first && position <= last) {
			return getChildAt(position - first).getHeight();
		} else {
			final ListAdapter adapter = getAdapter();
			int type = adapter.getItemViewType(position);
			// There might be a better place for checking for the following
			final int typeCount = adapter.getViewTypeCount();
			if (typeCount != mSampleViewTypes.length) {
				mSampleViewTypes = new View[typeCount];
			}

			View v;
			if (type >= 0) {
				if (mSampleViewTypes[type] == null) {
					v = adapter.getView(position, null, this);
					mSampleViewTypes[type] = v;
				} else {
					v = adapter.getView(position, mSampleViewTypes[type], this);
				}
			} else {
				v = adapter.getView(position, null, this);
			}

			ViewGroup.LayoutParams lp = v.getLayoutParams();
			final int height = lp == null ? 0 : lp.height;
			if (height > 0) {
				return height;
			} else {
				int spec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
				v.measure(spec, spec);
				return v.getMeasuredHeight();
			}
        
		}
	}

	private int getVisualItemHeight(int position) {
		final int divHeight = getDividerHeight();

		if (position == mExpDragPos) {
			return mFloatViewHeight + divHeight;
		}

		int height;
		
		switch (mDragState) {
			case SRC_ABOVE:
				if (position >= mSrcDragPos && position < mExpDragPos) {
					height = getItemHeight(position + 1);
					if (position == mSrcDragPos) {
						height += mItemHeightCollapsed + divHeight;
					}
					if (position == mExpDragPos - 1) {
						height -= mFloatViewHeight;
					}

					return height + divHeight;
				}
				break;
			case SRC_BELOW:
				if (position <= mSrcDragPos && position > mExpDragPos) {
					height = getItemHeight(position - 1);
					if (position == mSrcDragPos) {
						height += mItemHeightCollapsed + divHeight;
					}
					if (position == mExpDragPos + 1) {
						height -= mFloatViewHeight;
					}

					return height + divHeight;
				}
				break;
			default:
		}

		return getItemHeight(position) + getDividerHeight();
	}

	private int getDragEdge(int vposition, int vtop) {
		if (vposition == 0) {
			return vtop;
		}

		int edge;
		if (vposition <= mExpDragPos) {
			edge = vtop + (mFloatViewHeight - getVisualItemHeight(vposition - 1)) / 2;
			if (mDragState == SRC_EXP) {
				edge -= mItemHeightCollapsed;
			}
		} else {
			edge = vtop + (getVisualItemHeight(vposition) - mFloatViewHeight) / 2;
			if (mDragState == SRC_EXP) {
				edge += mItemHeightCollapsed;
			}
		}

		return edge;
	}

	private int getFloatPosition(int y, int position, int top) {
		final int floatViewMidY = Math.max(mFloatViewHeightHalf + getPaddingTop(),
				Math.min(getHeight() - getPaddingBottom() - mFloatViewHeightHalf,
        y - mDragPointY + mFloatViewHeightHalf));
		
		// get closest visual item top and position
		int visItemTop;
		int visItemPos;
		final int divHeight = getDividerHeight();
		switch (mDragState) {
			case SRC_ABOVE:
				visItemTop = top;
				if (position == mSrcDragPos + 1) {
					visItemTop -= mItemHeightCollapsed + divHeight;
				}
				
				if (position > mSrcDragPos && position <= mExpDragPos) {
					visItemPos = position - 1;
				} else {
					visItemPos = position;
				}
				break;
			case SRC_BELOW:
				visItemTop = top;
				if (position == mSrcDragPos) { 
					if (position == getCount() - 1) {
						int aboveHeight = getItemHeight(position - 1);
						if (position - 1 == mExpDragPos) {
							visItemTop -= aboveHeight - mFloatViewHeight;
						} else {
							visItemTop -= aboveHeight + divHeight;
						}
						visItemPos = position;
						break;
					}
					visItemTop += mItemHeightCollapsed + divHeight;
				}

				if (position <= mSrcDragPos && position > mExpDragPos) {
					visItemPos = position + 1;
				} else {
					visItemPos = position;
        }
				break;
			default:
				visItemTop = top;
				visItemPos = position;
		}
		int edge = getDragEdge(visItemPos, visItemTop);
		if (floatViewMidY < edge) {
			while (visItemPos >= 0) {
				visItemPos--;

				if (visItemPos <= 0) {
					visItemPos = 0;
					break;
				}

				visItemTop -= getVisualItemHeight(visItemPos);
				edge = getDragEdge(visItemPos, visItemTop);
				if (floatViewMidY >= edge) {
					break;
				}
			}
		} else {
			final int count = getCount();
			while (visItemPos < count) {
				if (visItemPos == count - 1) {
					break;
				}

				visItemTop += getVisualItemHeight(visItemPos);
				edge = getDragEdge(visItemPos + 1, visItemTop);
				if (floatViewMidY < edge) {
					break;
				}
				visItemPos++;
			}
		}

		final int numHeaders = getHeaderViewsCount();
		if (visItemPos < numHeaders - 0) {
			return numHeaders;
		} else if (visItemPos >= getCount()) {
			return getCount() - 1;
		}
		return visItemPos;
	}
	
	private boolean mIsTouchOnAnimate = false;
	private boolean mIsRight = false;
	private boolean mIsInterDown = false; 
	private int mTapChild = 0;
	private int mTapIndex = 0;
	
	public void onSingleTap(MotionEvent ev)
	{
		Rect r = new Rect();
		this.getChildAt(0).getGlobalVisibleRect(r);
		int first = this.getFirstVisiblePosition();
		int index = first;
		int count = 0;
		int child = 0;
		if(0 == first)
		{
			if(mDragY > r.height())
			{
				count = (int) ((mDragY - r.height())/this.getChildAt(1).getHeight());
				child = count + 1;
				index += count;
			}
			else
			{
				return;
			}
		}
		else
		{
			if(mDragY > r.height())
			{
				count = (int) ((mDragY - r.height())/this.getChildAt(1).getHeight());
				child = count + 1;
				index += count;
			}
			else
			{
				child = 0;
				index -= 1;
			}
		}
		
		ViewGroup view = (ViewGroup)this.getChildAt(child);
		
		if(null != view)
		{
			if(mIsDeleteMode)
			{
				if(110 > mDragX)
				{
					ImageView v = (ImageView) view.findViewById(R.id.checkbox_delete);
					mActivity.mNotesCursor.moveToPosition(index);
					long id = mActivity.mNotesCursor.getLong(mActivity.mNotesCursor.getColumnIndex("_id"));
					if(mDeleteList.contains(id))
					{
						v.setImageResource(R.drawable.select_circle);
						mDeleteList.remove(id);
					}
					else
					{
						v.setImageResource(R.drawable.selected);
						mDeleteList.add(id);
					}
					if(1 == mDeleteList.size())
					{
						if(!mActivity.mSendImageView.isEnabled())
						{
							mActivity.mSendImageView.setEnabled(true);
							mActivity.mSendImageView.setAlpha(255);
						}
					}
					else
					{
						if(mActivity.mSendImageView.isEnabled())
						{
							mActivity.mSendImageView.setEnabled(false);
							mActivity.mSendImageView.setAlpha(100);
						}
					}
				}
			}
			else
			{
				mTapIndex = index;
				mTapChild = child;
				mActivity.mNotesCursor.moveToPosition(index);
	        	mID = mActivity.mNotesCursor.getLong(mActivity.mNotesCursor.getColumnIndex("_id"));
	        	Rect r_item = new Rect();
	    		view.getGlobalVisibleRect(r_item);
	    		Rect r_list = new Rect();
	    		this.getGlobalVisibleRect(r_list);
	    		float percent = 0;
	    		if(r_item.top == r_list.top)
	    		{
	    			percent = ((float)(r_item.height() - view.getHeight()))/r_list.height();
	    		}
	    		else
	    		{
	    			percent = ((float)(r_item.top - r_list.top + 1.4 * child))/r_list.height();
	    		}
	    		mActivity.showItem(mID, percent);
			}
		}
	}
	
	public void hideTapChild()
	{
		View child = this.getChildAt(mTapChild);
		if(null != child)
		{
			child.setVisibility(View.INVISIBLE);
		}
	}
	
	public int getTapIndex()
	{
		return mTapIndex;
	}
	
	public void showTapChild()
	{
		View child = this.getChildAt(mTapChild);
		if(null != child)
		{
			child.setVisibility(View.VISIBLE);
		}
	}
	
	private void hScrollToRight()
	{
		int w = mCurrentItem.getWidth() / 2;
		LinearLayout ll = (LinearLayout) mCurrentItem.findViewById(R.id.linearlayout_detail);
        LinearLayout le = (LinearLayout) mCurrentItem.findViewById(R.id.linearlayout_edit);
        //ImageView iv = (ImageView) mCurrentItem.findViewById(R.id.imageview_clip);
		mDirection = TO_RIGHT;
		mActivity.setFoldMode(true);
		if(ll.getLeft() < w)
		{
			Animation anim = new TranslateAnimation(ll.getLeft(), w, 0, 0);
			anim.setDuration(300);
			anim.setFillAfter(true);
			ll.startAnimation(anim);
			anim.setAnimationListener(mAnimateListener);
			mRightView = mCurrentItem;
		}
		le.setVisibility(View.VISIBLE);
		mActivity.mUpMode = mCurrentPosition;
		mActivity.mUpAdapter.notifyDataSetChanged();
		//iv.setBackgroundResource(R.drawable.clip_up);
	}
	
	private void hScrollToLeft()
	{
		LinearLayout ll = (LinearLayout) mCurrentItem.findViewById(R.id.linearlayout_edit_mask);
		if(ll.getAlpha() < 0.5)
		{
			ll.setAlpha(ll.getAlpha() + 0.005f);
		}
	}
	
	private int mPresEventType = -1;
	private boolean mIsScrollEvent = false;
	
	private void onCustomScroll(MotionEvent ev)
	{
		if(mDirection == TO_RIGHT)
		{
			return;
		}
		if(Math.abs(ev.getX() - mDragX) > Math.abs(ev.getY() - mDragY))
		{
			if(mIsDeleteMode)
			{
				return;
			}
			Rect r = new Rect();
			this.getChildAt(0).getGlobalVisibleRect(r);
			int first = this.getFirstVisiblePosition();
			int index = first;
			int count = 0;
			int child = 0;
			if(0 == first)
			{
				if(mDragY > r.height())
				{
					count = (int) ((mDragY - r.height())/this.getChildAt(1).getHeight());
					child = count + 1;
					index += count;
				}
				else
				{
					return;
				}
			}
			else
			{
				if(mDragY > r.height())
				{
					count = (int) ((mDragY - r.height())/this.getChildAt(1).getHeight());
					child = count + 1;
					index += count;
				}
				else
				{
					child = 0;
					index -= 1;
				}
			}
			
			RelativeLayout view = (RelativeLayout)this.getChildAt(child);
			if(null != view && index >= 0)
			{
				mCurrentItem = view;
				mCurrentPosition = index;
				//horizontal
				if(ev.getX() > mDragX)
				{
					//to right
					hScrollToRight();
				}
				else
				{
					//to left
					hScrollToLeft();
				}
			}
		}
		else
		{/*
			//vertical
			if(ev.getY() > mDragY)
			{
				//to up
				if(0 < this.getFirstVisiblePosition())
				{
					this.scrollBy(0, -20);
					mActivity.mUpListView.scrollBy(0, -20);
				}
			}
			else
			{
				if(this.getChildCount() - 1 > this.getFirstVisiblePosition())
				{
					//to down
					this.scrollBy(0, 20);
					mActivity.mUpListView.scrollBy(0, 20);
				}
			}
			*/
		}
	}
	
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		mPresEventType = ev.getAction();
		if(mActivity.mIsAnimating)
		{
			return true;
		}
		if(mIsAnimating)
		{
			mIsTouchOnAnimate = true;
			return true;
		}
		if(mPresEventType == MotionEvent.ACTION_MOVE)
		{
			mIsScrollEvent = true;
		}
		if(MotionEvent.ACTION_UP != ev.getAction())
		{
			mIsInterDown = false;
		}
		if(MotionEvent.ACTION_MOVE == ev.getAction() && mIsTouchOnAnimate)
		{
			return true;
		}
		if(MotionEvent.ACTION_DOWN == ev.getAction())
		{
			if(!mIsRight)
			{
				mIsInterDown = true;
			}
			mDragX = (int) ev.getX();
			mDragY = (int) ev.getY();
		}
		else if(MotionEvent.ACTION_UP == ev.getAction())
		{
			if(!mIsAnimating)
			{
				mIsTouchOnAnimate = false;
			}
			if(mIsScrollEvent)
			{
				mIsScrollEvent = false;
				onCustomScroll(ev);
			}
			else if(mIsInterDown)
			{
				mIsInterDown = false;
				onSingleTap(ev);
			}
			else if(mIsLongPress)
			{
				dropFloatView();
				mIsLongPress = false;
				MotionEvent ev2 = MotionEvent.obtain(ev);
		        ev2.setAction(MotionEvent.ACTION_CANCEL);
		        super.onInterceptTouchEvent(ev2);
			}
		}
		return super.onInterceptTouchEvent(ev);
	}
	
	private int mDragX = 0;
	private int mDragY = 0;
	
	public void setDragScrollStart(float heightFraction) {
		setDragScrollStarts(heightFraction, heightFraction);
	}
	
	public void setDragScrollStarts(float upperFrac, float lowerFrac) {
		if (lowerFrac > 0.5f) {
			mDragDownScrollStartFrac = 0.5f;
		} else {
			mDragDownScrollStartFrac = lowerFrac;
		}

		if (upperFrac > 0.5f) {
			mDragUpScrollStartFrac = 0.5f;
		} else {
			mDragUpScrollStartFrac = upperFrac;
		}

		if (getHeight() != 0) {
			updateScrollStarts();
		}
	}

	private void updateScrollStarts() {
		final int padTop = getPaddingTop();
		final int listHeight = getHeight() - padTop - getPaddingBottom();
		float heightF = (float) listHeight;
		
		mUpScrollStartYF = padTop + mDragUpScrollStartFrac * heightF;
		mDownScrollStartYF = padTop + (1.0f - mDragDownScrollStartFrac) * heightF;

		mUpScrollStartY = (int) mUpScrollStartYF;
		mDownScrollStartY = (int) mDownScrollStartYF;
		mDragUpScrollHeight = mUpScrollStartYF - padTop;
		mDragDownScrollHeight = padTop + listHeight - mDownScrollStartYF;
	}
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		updateScrollStarts();
	}

	private int getViewHeight(View v) {

	    // measure to get height of header item
	    ViewGroup.LayoutParams lp = v.getLayoutParams();
	    final int height = lp == null ? 0 : lp.height;
	    if (height > 0) {
				return height;
	    } else {
	    	int spec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
	    	v.measure(spec, spec);
			return v.getMeasuredHeight();
	    }

	}
	
	@Override
	  public void addHeaderView(View v, Object data, boolean isSelectable) {
	    super.addHeaderView(v, data, isSelectable);
			
		mHeaderHeights.add(getViewHeight(v));

	    mHeadersTotalHeight += mHeaderHeights.get(mHeaderHeights.size() - 1);
	}
	
	private void dropFloatView() {
		mDragScroller.stopScrolling(true);
		if (mDropListener != null && mExpDragPos >= 0 && mExpDragPos < getCount()) {
			final int numHeaders = getHeaderViewsCount();
			mDropListener.drop(mSrcDragPos - numHeaders, mExpDragPos - numHeaders);
		}

		int top = getChildAt(0).getTop();
		int firstPos = getFirstVisiblePosition();

		View expView = getChildAt(mExpDragPos - firstPos);
		if (expView != null) {
			ViewGroup.LayoutParams lp = expView.getLayoutParams();
			lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
			expView.requestLayout();
		}

		if (mSrcDragPos < firstPos) {
			setSelectionFromTop(firstPos - 1, top - getPaddingTop());
		} else if (mSrcDragPos <= getLastVisiblePosition()) {
			View srcView = getChildAt(mSrcDragPos - firstPos);
			ViewGroup.LayoutParams lp = srcView.getLayoutParams();
			lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
			srcView.requestLayout();
			
			srcView.setVisibility(View.VISIBLE);
		}
		//removeFloatView();
		//mDragState = NO_DRAG;
	}
	
	private void updateListState() {
		if (mFloatView == null) {
			mDragState = NO_DRAG;
			return;
		} else if (mExpDragPos == mSrcDragPos) {
			mDragState = SRC_EXP;
		} else if (mSrcDragPos < mExpDragPos) {
			mDragState = SRC_ABOVE;
		} else {
			mDragState = SRC_BELOW;
		}
	}
	
	private void expandItem(int position) {
	final int first = getFirstVisiblePosition();
		
		RelativeLayout v = (RelativeLayout) getChildAt(position - first);
		if (v != null && mFloatView != null) {
			ViewGroup.LayoutParams lp = v.getLayoutParams();
			int oldHeight = lp.height;
			if (lp.height == mItemHeightCollapsed && position == mSrcDragPos) {
				lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
				if(mFromPosition < mToPosition)
				{
					RelativeLayout v1 = (RelativeLayout) getChildAt(position - first - 1);
					if(null != v1)
					{
						v1.setGravity(Gravity.BOTTOM);
						Animation anim = new TranslateAnimation(0, 0, mFloatViewHeight, 0);
						anim.setDuration(300);
						v1.startAnimation(anim);
						if (lp.height != oldHeight) {
							v1.requestLayout();
						}
					}
				}
				else
				{
					RelativeLayout v1 = (RelativeLayout) getChildAt(position - first + 1);
					if(null != v1)
					{
						v1.setGravity(Gravity.TOP);
						Animation anim = new TranslateAnimation(0, 0, -mFloatViewHeight, 0);
						anim.setDuration(300);
						v1.startAnimation(anim);
						if (lp.height != oldHeight) {
							v1.requestLayout();
						}
					}
				}
			} else if (lp.height == ViewGroup.LayoutParams.WRAP_CONTENT && position != mExpDragPos) {
				// expanding normal item
				lp.height = v.getHeight() + mFloatViewHeight;
				if (position > mSrcDragPos) {
					
					if(mFromPosition < mToPosition)
					{
						v.setGravity(Gravity.TOP);
						Animation anim = new TranslateAnimation(0, 0, v.getHeight(), 0);
						anim.setDuration(300);
						v.startAnimation(anim);
						
					}
					else
					{
						RelativeLayout v1 = (RelativeLayout) getChildAt(position - first + 1);
						if(null != v1)
						{
							Animation anim = new TranslateAnimation(0, 0, -v.getHeight(), 0);
							anim.setDuration(300);
							v1.startAnimation(anim);
							if (lp.height != oldHeight) {
								v1.requestLayout();
							}
						}
					}
				} else if (position < mSrcDragPos) {
					if(mFromPosition > mToPosition)
					{
						v.setGravity(Gravity.BOTTOM);
						Animation anim = new TranslateAnimation(0, 0, -v.getHeight(), 0);
						anim.setDuration(300);
						v.startAnimation(anim);
						
					}
					else
					{
						RelativeLayout v1 = (RelativeLayout) getChildAt(position - first - 1);
						if(null != v1)
						{
							Animation anim = new TranslateAnimation(0, 0, v.getHeight(), 0);
							anim.setDuration(300);
							v1.startAnimation(anim);
							if (lp.height != oldHeight) {
								v1.requestLayout();
							}
						}
					}
					
				}
			}
			else {
				Log.e("drag", "3");
		      }
			if (lp.height != oldHeight) {
				v.requestLayout();
			}
		}
	}
	
	private int mFromPosition = -1;
	private int mToPosition = -1;
	
	private void collapseItem(int position) {
		View v = getChildAt(position - getFirstVisiblePosition());
		if (v != null) {
			ViewGroup.LayoutParams lp = v.getLayoutParams();
			int oldHeight = lp.height;
			if (position == mSrcDragPos) {
				lp.height = mItemHeightCollapsed;
			} else if (position == mExpDragPos) {
				// to save time, assume collapsing an expanded item
				lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
			}
			if (lp.height != oldHeight) {
				v.requestLayout();
			}
		}
	}
	
	private boolean shuffleItems(int floatPos) {
		if (floatPos != mExpDragPos) {
			mFromPosition = mExpDragPos;
			mToPosition = floatPos;
			collapseItem(mExpDragPos);
			expandItem(floatPos);
			if (mDragListener != null) {
				final int numHeaders = getHeaderViewsCount();
				mDragListener.drag(mExpDragPos - numHeaders, floatPos - numHeaders);
			}
			
			mExpDragPos = floatPos;
			updateListState();
			return true;
		} else {
			return false;
		}
	}
	
	@Override
	protected void layoutChildren() {
		if (mFloatView == null) {
			super.layoutChildren();
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		mIsInterDown = false;
		if(mIsAnimating)
		{
			return true;
		}
		if(MotionEvent.ACTION_UP != ev.getAction() && mIsTouchOnAnimate)
		{
			return true;
		}
		if(MotionEvent.ACTION_UP == ev.getAction())
		{
			if(!mIsAnimating)
			{
				mIsTouchOnAnimate = false;
			}
		}
		if(mIsScrollEvent && MotionEvent.ACTION_MOVE == ev.getAction())
		{
			mPresEventType = MotionEvent.ACTION_MOVE;
			mIsScrollEvent = true;
		}
		else if(mIsScrollEvent && MotionEvent.ACTION_UP == ev.getAction())
		{
			mIsScrollEvent = false;
			onCustomScroll(ev);
		}
		else
		{
			mIsScrollEvent = false;
		}
		if ((mDragListener != null || mDropListener != null) && mFloatView != null) {
			int action = ev.getAction();

			final int x = (int) ev.getX();
			final int y = (int) ev.getY();

			switch (action & MotionEvent.ACTION_MASK) {
			case MotionEvent.ACTION_UP:
			case MotionEvent.ACTION_CANCEL:
				Rect r = mTempRect;
				mFloatView.getDrawingRect(r);
				//mDragScroller.stopScrolling(true);
				dropFloatView();
				mIsLongPress = false;
				break;

			case MotionEvent.ACTION_DOWN:
				//doExpansion();
				break;
			case MotionEvent.ACTION_MOVE:
				if (mLastY == mDownY) {
					final View item = getChildAt(mSrcDragPos - getFirstVisiblePosition());
					if (item != null) {
						item.setVisibility(INVISIBLE);
					}
				}
				
				dragView(x, y);

				if (!mDragScroller.isScrolling()) {
					final int first = getFirstVisiblePosition();
					final View startView = getChildAt(mExpDragPos - first);
					int startPos;
					int startTop;
					if (startView == null) {
						startPos = first + getChildCount() / 2;
						startTop = getChildAt(startPos - first).getTop();
						Log.d("mobeta", "startView was null");
					} else {
						startPos = mExpDragPos;
						startTop = startView.getTop();
					}
					boolean shuffled = shuffleItems(getFloatPosition(y, startPos, startTop));
					if (shuffled) {
						super.layoutChildren();
					}
				}

				int currentScrollDir = mDragScroller.getScrollDir();

				if (y > mLastY && y > mDownScrollStartY && currentScrollDir != DragScroller.DOWN) {
					if (currentScrollDir != DragScroller.STOP) {
						mDragScroller.stopScrolling(true);
					}
					mDragScroller.startScrolling(DragScroller.DOWN);
				}
				else if (y < mLastY && y < mUpScrollStartY && currentScrollDir != DragScroller.UP) {
					if (currentScrollDir != DragScroller.STOP) {
						mDragScroller.stopScrolling(true);
					}

					mDragScroller.startScrolling(DragScroller.UP);
				}
				else if (y >= mUpScrollStartY && y <= mDownScrollStartY && mDragScroller.isScrolling()) {
					mDragScroller.stopScrolling(true);
				}
				break;
			}
			mLastY = y;
			return true;
		}
		return super.onTouchEvent(ev);
	}

	private void startDragging(Bitmap bm, int x, int y) {
		if(getParent() != null)
			getParent().requestDisallowInterceptTouchEvent(true);
		//removeFloatView();
		mWindowParams = new WindowManager.LayoutParams();
		mWindowParams.gravity = Gravity.TOP | Gravity.LEFT;
		mWindowParams.x = x - mDragPointX + mXOffset;
		mWindowParams.y = y - mDragPointY + mYOffset;
		mWindowParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
		mWindowParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
		mWindowParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
		| WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
		| WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
		| WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
		| WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;
		mWindowParams.format = PixelFormat.TRANSLUCENT;
		mWindowParams.alpha = 0.8f;
		mWindowParams.windowAnimations = 0;

		Context context = getContext();
		ImageView v = new ImageView(context);
		//int backGroundColor = context.getResources().getColor(R.color.dragndrop_background);
		v.setBackgroundColor(mFloatBGColor);
		//v.setBackgroundResource(R.drawable.playlist_tile_drag);
		v.setPadding(0, 0, 0, 0);
		v.setImageBitmap(bm);
		mDragBitmap = bm;

		mWindowManager = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
		mWindowManager.addView(v, mWindowParams);
		mFloatView = v;
		mDragState = SRC_EXP;
	}

	private void dragView(int x, int y) {
		mWindowParams.x = mXOffset + getPaddingLeft();
		final int numHeaders = getHeaderViewsCount();
		
		final int firstPos = getFirstVisiblePosition();
		final int lastPos = getLastVisiblePosition();
		int limit = getPaddingTop();
		if (firstPos < numHeaders) {
			limit = getChildAt(numHeaders - firstPos - 1).getBottom();
		}
		int footerLimit = getHeight() - getPaddingBottom();
		if (lastPos >= getCount()) {
			footerLimit = getChildAt(getCount() - firstPos).getTop();
		}
		if (y - mDragPointY < limit) {
			mWindowParams.y = mYOffset + limit;
		} else if (y - mDragPointY + mFloatViewHeight > footerLimit) {
			mWindowParams.y = mYOffset + footerLimit - mFloatViewHeight;
		} else {
			mWindowParams.y = y - mDragPointY + mYOffset;
		}
		
		mWindowManager.updateViewLayout(mFloatView, mWindowParams);

		if (mTrashcan != null) {
			int width = mFloatView.getWidth();
			if (y > getHeight() * 3 / 4) {
				mTrashcan.setLevel(2);
			} else if (width > 0 && x > width / 4) {
				mTrashcan.setLevel(1);
			} else {
				mTrashcan.setLevel(0);
			}
		}
	}

	private void removeFloatView() {
		
		if (mFloatView != null) {
			mFloatView.setVisibility(GONE);
			WindowManager wm = (WindowManager)getContext().getSystemService(Context.WINDOW_SERVICE);
			wm.removeView(mFloatView);
			mFloatView.setImageDrawable(null);
			mFloatView = null;
		}
		if (mDragBitmap != null) {
			mDragBitmap.recycle();
			mDragBitmap = null;
		}
		if (mTrashcan != null) {
			mTrashcan.setLevel(0);
		}
	}

	public void setTrashcan(Drawable trash) {
		mTrashcan = trash;
	}

	public void setDragListener(DragListener l) {
		mDragListener = l;
	}

	public void setDropListener(DropListener l) {
		mDropListener = l;
	}

	public interface DragListener {
		void drag(int from, int to);
	}
	
	public interface DropListener {
		void drop(int from, int to);
	}
	
	public void setDragScrollProfile(DragScrollProfile ssp) {
		if (ssp != null) {
			mScrollProfile = ssp;
		}
	}
	
	public interface DragScrollProfile {
		float getSpeed(float w, long t);
	}
	
	private class DragScroller implements Runnable, AbsListView.OnScrollListener {
		private boolean mAbort;
		private long mPrevTime;
		private int dy;
		private float dt;
		private long tStart;
		private int scrollDir;
		public final static int STOP = -1;
		public final static int UP = 0;
		public final static int DOWN = 1;
		private float mScrollSpeed; // pixels per ms
		private boolean mScrolling = false;
		private int mLastHeader;
		private int mFirstFooter;
		
		public boolean isScrolling() {
			return mScrolling;
		}

		public int getScrollDir() {
			return mScrolling ? scrollDir : STOP;
		}

		public DragScroller() {
		}
		
		public void startScrolling(int dir) {
			if (!mScrolling) {
				mAbort = false;
				mScrolling = true;
				tStart = SystemClock.uptimeMillis();
				mPrevTime = tStart;
				mLastHeader = getHeaderViewsCount() -1;
				mFirstFooter = getCount();
				scrollDir = dir;
				post(this);
			}
		}
		
		public void stopScrolling(boolean now) {
			if (now) {
				DragSortListView.this.removeCallbacks(this);
				mScrolling = false;
			} else {
				mAbort = true;
			}
		}
		
		
		@Override
		public void run() {
			if (mAbort) {
				mScrolling = false;
				return;
			}
			
			if (scrollDir == UP) {
				mScrollSpeed = mScrollProfile.getSpeed((mUpScrollStartYF - mLastY) / mDragUpScrollHeight, mPrevTime);
			} else {
				mScrollSpeed = -mScrollProfile.getSpeed((mLastY - mDownScrollStartYF) / mDragDownScrollHeight, mPrevTime);
			}
			dt = SystemClock.uptimeMillis() - mPrevTime;
			dy = (int) Math.round(mScrollSpeed * dt);

			if (dy == 0) {
				mPrevTime += dt;
				post(this);
				return;
			}

			final int first = getFirstVisiblePosition();
			final int last = getLastVisiblePosition();

			final int count = getCount();

			final int padTop = getPaddingTop();
			final int listHeight = getHeight() - padTop - getPaddingBottom();

			int movePosition;
			if (dy > 0) {
				if (first == 0 && getChildAt(0).getTop() == padTop) {
					mScrolling = false;
					return;
				}
				movePosition = first;
				dy = Math.min(listHeight, dy);
			} else {
				if (last == count - 1 &&
						getChildAt(last - first).getBottom() <= listHeight + padTop) {
					mScrolling = false;
					return;
				}
				movePosition = last;
				dy = Math.max(-listHeight, dy);
			}

			final int oldTop = getChildAt(movePosition - first).getTop();
			int newTop = oldTop + dy;
			int newFloatPos = getFloatPosition(mLastY, movePosition, newTop);
			if (newFloatPos != mExpDragPos) {
				if (scrollDir == DOWN && newFloatPos == movePosition) {
					newTop -= mFloatViewHeight + getDividerHeight();
				} else if (newFloatPos < movePosition) {
					if (scrollDir == UP || (scrollDir == DOWN && movePosition == mExpDragPos)) {
						newTop += mFloatViewHeight + getDividerHeight();
					}
				}
			}
			shuffleItems(newFloatPos);
			setSelectionFromTop(movePosition, newTop - getPaddingTop());
			DragSortListView.super.layoutChildren();
			mPrevTime += dt;
			post(this);
		}

		@Override
		public void onScroll(AbsListView view, int firstVisibleItem,
				int visibleItemCount, int totalItemCount) {
				mIsScrollEvent = false;
			if (mScrolling && visibleItemCount != 0) {
				if (firstVisibleItem <= mLastHeader) {
					int dragViewTop = mLastY - mDragPointY;
					int lastHeaderBottom = getChildAt(mLastHeader - firstVisibleItem).getBottom();
					if (dragViewTop < lastHeaderBottom) {
						mWindowParams.y = mYOffset + lastHeaderBottom;
						mWindowManager.updateViewLayout(mFloatView, mWindowParams);
					}
				} else if (firstVisibleItem + visibleItemCount > mFirstFooter) {
					int dragViewBottom = mLastY - mDragPointY + mFloatViewHeight;
					int firstFooterTop = getChildAt(mFirstFooter - firstVisibleItem).getTop();
					if (dragViewBottom > firstFooterTop) {
						mWindowParams.y = mYOffset + firstFooterTop - mFloatViewHeight;
						mWindowManager.updateViewLayout(mFloatView, mWindowParams);
					}
				}
			}
			if(null != mContext)
			{
				{
					if(null != view)
					{
						if(null != mActivity.mUpListView)
						{
							int index = view.getFirstVisiblePosition();
							View v = view.getChildAt(0);
							int top = (v == null) ? 0 : v.getTop();
							mActivity.mUpListView.setSelectionFromTop(index, top);
						}
					}
				}
			}
		}
		
		
		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {
		}
	}
	
	public void releaseFold()
	{
		if(null != mCurrentItem && null != mRightView)
		{
			
			mDirection = TO_LEFT;
			if(mRightView == mCurrentItem)
			{
				LinearLayout ll = (LinearLayout) mCurrentItem.findViewById(R.id.linearlayout_detail);
				LinearLayout lm = (LinearLayout) mRightView.findViewById(R.id.linearlayout_edit_mask);
            	lm.setVisibility(View.VISIBLE);
            	lm.setAlpha(0);
				int w = mCurrentItem.getWidth() / 2;
				Animation anim = new TranslateAnimation(w, 0, 0, 0);
				anim.setDuration(300);
				anim.setFillAfter(true);
				anim.setAnimationListener(mAnimateListener);
				ll.startAnimation(anim);
			}
		}
	}
	
	public void releaseFoldForce()
	{
		if(null != mCurrentItem && null != mRightView)
		{
			LinearLayout ll = (LinearLayout) mCurrentItem.findViewById(R.id.linearlayout_detail);
			mDirection = TO_LEFT;
			if(mRightView == mCurrentItem)
			{
				Animation anim = new TranslateAnimation(1, 0, 0, 0);
				anim.setDuration(1);
				anim.setFillAfter(true);
				anim.setAnimationListener(mAnimateListener);
				ll.startAnimation(anim);
			}
		}
	}
	
	private Animation.AnimationListener mAnimateListener = new Animation.AnimationListener(){

		@Override
		public void onAnimationEnd(Animation animation) {
			// TODO Auto-generated method stub
            if(mDirection == TO_LEFT)
            {
            	LinearLayout le = (LinearLayout) mRightView.findViewById(R.id.linearlayout_edit);
                //ImageView iv = (ImageView) mRightView.findViewById(R.id.imageview_clip);
                le.setVisibility(View.GONE);
				//iv.setBackgroundResource(R.drawable.clip_down);
				mRightView = null;
				mActivity.setFoldMode(false);
				mActivity.mUpMode = NotesActivity.MODE_NORMAL;
				mActivity.mUpAdapter.notifyDataSetChanged();
            }
            else if(mDirection == TO_RIGHT)
            {
            	mIsRight = true;
            	LinearLayout le = (LinearLayout) mRightView.findViewById(R.id.linearlayout_edit_mask);
            	le.setVisibility(View.GONE);
            }
            if(mClickEventType > 0)
            {
            	ContentValues values = new ContentValues();
            	switch(mClickEventType)
            	{
	            	case CLICK_SEND:
	            	{
	            		Intent intent = new Intent("android.intent.action.SEND");
						intent.setType("text/*");
						intent.putExtra("android.intent.extra.SUBJECT", mContext.getString(R.string.app_name));
						intent.putExtra("android.intent.extra.TEXT", (String)mClickExtraValue);
						Intent i = Intent.createChooser(intent, mContext.getString(R.string.send_via));
						mActivity.startActivityForResult(i, REQUEST_CODE_SEND);
						mClickExtraValue = null;
	            	}
	            	break;
	            	case CLICK_FAV:
	            	{
	            		values.put(NotesDatabaseHelper.FAVORITE, (Integer)mClickExtraValue);
		        		mContext.getContentResolver().update(NotesProvider.CONTENT_URI_NOTES, values, "rowid='" + mClickEventID + "'", null);
		        		mActivity.mNotesCursor = mContext.getContentResolver().query(NotesProvider.CONTENT_URI_NOTES, null, mActivity.QUERY_EDIT_SELECTION, 
				        		null, NotesDatabaseHelper.POSITION + " DESC");
						changeCursor();
						mClickExtraValue = null;
	            	}
	            	break;
	            	case CLICK_DELETE:
	            	{
	            		mContext.getContentResolver().delete(NotesProvider.CONTENT_URI_NOTES, 
								//"_id='" + mId + "'", null);
								"rowid='" + mClickEventID + "'", null);
						mActivity.mNotesCursor = mContext.getContentResolver().query(NotesProvider.CONTENT_URI_NOTES, null, mActivity.QUERY_EDIT_SELECTION, 
				        		null, NotesDatabaseHelper.POSITION + " DESC");
						changeCursor();
	            	}
	            	break;
            	}
            	mClickEventID = -1;
            	mClickEventType = -1;
            }
            mIsAnimating = false;
		}

		@Override
		public void onAnimationRepeat(Animation animation) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onAnimationStart(Animation animation) {
			// TODO Auto-generated method stub
			mIsAnimating = true;
			mIsRight = false;
		}};
	
	private static final int CLICK_SEND = 1;
	private static final int CLICK_FAV = 2;
	private static final int CLICK_DELETE = 3;
	
	private int mClickEventType = -1;
	private long mClickEventID = -1;
	private Object mClickExtraValue = null;
	private static final int REQUEST_CODE_SEND = 0;
	
	public class NotesAdapter extends CursorAdapter
	{
		private static final String TAG = "NotesAdapter";
		private static final int ITEM_TAG_ID = R.id.list_item;
		private static final int FAV_TAG_ID = R.id.button_edit;
		private static final int DELETE_TAG_ID = R.id.button_delete;
		private static final int SEND_TAG_ID = R.id.button_send;
		private static final int CHECK_TAG_ID = R.id.checkbox_delete;

		private Formatter mFormatter;
	    private StringBuilder mStringBuilder;
	    private FrameLayout.LayoutParams mParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
	    
		public NotesAdapter(Context context, Cursor c) {
			// TODO Auto-generated constructor stub
			super(context, c);
			mStringBuilder = new StringBuilder(50);
	        mFormatter = new Formatter(mStringBuilder);
	        mParams.leftMargin = 0;
		}

		@Override
		public void bindView(View view, Context context, Cursor cursor) {
			// TODO Auto-generated method stub
			ViewHolder holder = (ViewHolder)view.getTag();
			long id = cursor.getLong(cursor.getColumnIndex("_id"));
			long time = cursor.getLong(cursor.getColumnIndex(NotesDatabaseHelper.MODIFY_TIME));
			
			holder.mTimeTextView.setText(ToolsUtil.buildTime(time, mContext, mFormatter, mStringBuilder));
			holder.mAgoTextView.setText(ToolsUtil.buildAgoText(mContext, time));
			
			String detail = cursor.getString(cursor.getColumnIndex(NotesDatabaseHelper.DETAIL));
			holder.mSummaryTextView.setText(detail);
			holder.mEditLinearLayout.setVisibility(View.GONE);
			holder.mLinearLayoutMask.setVisibility(View.VISIBLE);
			holder.mLinearLayoutMask.setAlpha(0f);
			if(mIsDragMode)
			{
				//holder.mClipImageView.setBackgroundResource(R.drawable.clip_blank);
				/*
				if(mActivity.mUpMode != NotesActivity.MODE_SHOW && null != mActivity && null != mActivity.mUpListView)
				{
					mActivity.mUpMode = NotesActivity.MODE_SHOW;
					mActivity.mUpAdapter.notifyDataSetChanged();
				}
				*/
			}
			else
			{
				//holder.mClipImageView.setBackgroundResource(R.drawable.clip_down);
			}
			view.setTag(ITEM_TAG_ID, cursor.getLong(cursor.getColumnIndex("_id")));
			holder.mDeleteButton.setTag(DELETE_TAG_ID, holder.mDeleteButton);
			holder.mEditButton.setTag(FAV_TAG_ID, holder.mEditButton);
			holder.mSendButton.setTag(SEND_TAG_ID, holder.mSendButton);
			holder.mDeleteCheckBox.setTag(CHECK_TAG_ID, holder.mDeleteCheckBox);
			
			int fav = cursor.getInt(cursor.getColumnIndex(NotesDatabaseHelper.FAVORITE));
			holder.mFavImageView.setVisibility((1 == fav) ? View.VISIBLE : View.INVISIBLE);
			
			onClickListener listener = new onClickListener(cursor.getPosition());
			holder.mDeleteButton.setOnClickListener(listener);
			holder.mEditButton.setOnClickListener(listener);
			holder.mSendButton.setOnClickListener(listener);
			holder.mDeleteCheckBox.setOnClickListener(listener);
			if(mDeleteList.contains(id))
			{
				holder.mDeleteCheckBox.setImageResource(R.drawable.selected);
			}
			else
			{
				holder.mDeleteCheckBox.setImageResource(R.drawable.select_circle);
			}
			if(mIsDeleteMode)
			{
				holder.mDeleteCheckBox.setVisibility(View.VISIBLE);
				//holder.mClipImageView.setVisibility(View.GONE);
				holder.mLinearLayout.setPadding(10, 10, 0, 0);
			}
			else
			{
				holder.mDeleteCheckBox.setVisibility(View.GONE);
				//holder.mClipImageView.setVisibility(View.VISIBLE);
				holder.mLinearLayout.setPadding(15, 10, 15, 0);
			}
		}

		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {
			// TODO Auto-generated method stub
			ViewHolder holder = new ViewHolder();
			View v = LayoutInflater.from(mContext).inflate(R.layout.list_notes_item, null);
			holder.mTimeTextView = (TextView)v.findViewById(R.id.textview_time);
			holder.mSummaryTextView = (TextView)v.findViewById(R.id.textview_summary);
			holder.mLinearLayout = (LinearLayout) v.findViewById(R.id.linearlayout_detail);
			holder.mDeleteButton = (Button)v.findViewById(R.id.button_delete);
			holder.mEditButton = (Button)v.findViewById(R.id.button_edit);
			holder.mSendButton = (Button)v.findViewById(R.id.button_send);
			holder.mEditLinearLayout = (LinearLayout) v.findViewById(R.id.linearlayout_edit);
			//holder.mClipImageView = (ImageView)v.findViewById(R.id.imageview_clip);
			holder.mDeleteCheckBox = (ImageView)v.findViewById(R.id.checkbox_delete);
			holder.mAgoTextView = (TextView)v.findViewById(R.id.textview_ago);
			holder.mFavImageView = (ImageView)v.findViewById(R.id.imageview_fav);
			holder.mLinearLayoutMask = (LinearLayout)v.findViewById(R.id.linearlayout_edit_mask);
			v.setTag(holder);
	    	return v;
		}
		
		public void updateMode(boolean delete)
		{
			this.notifyDataSetChanged();
		}
		
		private class ViewHolder
		{
			LinearLayout mLinearLayout;
			TextView mTimeTextView;
			TextView mSummaryTextView;
			Button mEditButton;
			Button mSendButton;
			Button mDeleteButton;
			LinearLayout mEditLinearLayout;
			//ImageView mClipImageView;
			ImageView mDeleteCheckBox;
			TextView mAgoTextView;
			ImageView mFavImageView;
			LinearLayout mLinearLayoutMask;
		}

		private class onClickListener implements OnClickListener{

			private int mPosition = 0;
			
			public onClickListener(int position) {
				// TODO Auto-generated constructor stub
				mPosition = position;
			}
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mIsInterDown = false;
				if(0 > mPosition)
				{
					return;
				}
				
				mActivity.mNotesCursor.moveToPosition(mPosition);
				final long id = mActivity.mNotesCursor.getLong(mActivity.mNotesCursor.getColumnIndex("_id"));
				mClickEventID = id;
				if(null != v.getTag(DELETE_TAG_ID))
				{
					releaseFold();
					mClickEventType = CLICK_DELETE;
				}
				else if(null != v.getTag(FAV_TAG_ID))
				{
					ViewGroup vg = (ViewGroup)(v.getParent().getParent());
					View f = vg.findViewById(R.id.imageview_fav);
		        	if(View.VISIBLE == f.getVisibility())
		        	{
		        		releaseFold();
		        		mClickEventType = CLICK_FAV;
		        		mClickExtraValue = 0;
		        	}
		        	else
		        	{
		        		releaseFold();
		        		mClickEventType = CLICK_FAV;
		        		mClickExtraValue = 1;
		        	}
				}
				else if(null != v.getTag(SEND_TAG_ID))
				{
					releaseFold();
					String detail = mActivity.mNotesCursor.getString(mActivity.mNotesCursor.getColumnIndex(NotesDatabaseHelper.DETAIL));
					if(null != detail && detail.trim().length() > 0)
					{
						mClickEventType = CLICK_SEND;
						mClickExtraValue = detail;
					}
				}
			}
			
		}
	}

	public void changeCursor() {
		// TODO Auto-generated method stub
		mAdapter.changeCursor(mActivity.mNotesCursor);
		mActivity.mUpAdapter.changeCursor(mActivity.mNotesCursor);
	}

	private boolean mIsDragMode = false;
	private boolean mIsDeleteMode = false;
	public void setDeleteMode(boolean b) {
		// TODO Auto-generated method stub
		if(b)
		{
			mDeleteList.clear();
		}
		mIsDeleteMode = b;
		mAdapter.notifyDataSetInvalidated();
	}
	
	public void setDragMode(boolean b) {
		// TODO Auto-generated method stub
		mIsDragMode = b;
		mAdapter.notifyDataSetChanged();
	}
	
	public boolean getDeleteMode()
	{
		return mIsDeleteMode;
	}
	
	public List<Long> getDeleteList()
	{
		return mDeleteList;
	}
	
	private List<Long> mDeleteList = new ArrayList<Long>();

	public void removeFloat() {
		// TODO Auto-generated method stub
		removeFloatView();
		mDragState = NO_DRAG;
		if(null != mActivity && null != mActivity.mUpListView && mActivity.mUpMode != NotesActivity.MODE_NORMAL)
		{
			mActivity.mUpMode = NotesActivity.MODE_NORMAL;
			mActivity.mUpAdapter.notifyDataSetChanged();
			//mActivity.mUpListView.setVisibility(View.GONE);
		}
	}
}
