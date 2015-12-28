/**
 * Copyright (c) 2009, Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.haolianluo.sms2.ui;

import android.app.ListActivity;
import android.app.SearchManager;
import android.content.AsyncQueryHandler;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.TelephonyMy;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.style.StyleSpan;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.haolianluo.sms2.R;
import com.haolianluo.sms2.data.HConst;
import com.haolianluo.sms2.mms.data.Contact;
import com.haolianluo.sms2.mms.data.ContactList;
import com.haolianluo.sms2.mms.data.Conversation;
import com.haolianluo.sms2.ui.sms2.HTalkActivity;
import com.haolianluo.sms2.ui.sms2.HUpdateDialog;

/***
 * Presents a List of search results.  Each item in the list represents a thread which
 * matches.  The item contains the contact (or phone number) as the "title" and a
 * snippet of what matches, below.  The snippet is taken from the most recent part of
 * the conversation that has a match.  Each match within the visible portion of the
 * snippet is highlighted.
 */

public class SearchActivity extends ListActivity
{
    AsyncQueryHandler mQueryHandler;

    /*
     * Subclass of TextView which displays a snippet of text which matches the full text and
     * highlights the matches within the snippet.
     */
    public static class TextViewSnippet extends TextView {
        private static String sEllipsis = "\u2026";

        private static int sTypefaceHighlight = Typeface.BOLD;

        private String mFullText;
        private String mTargetString;

        public TextViewSnippet(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        public TextViewSnippet(Context context) {
            super(context);
        }

        public TextViewSnippet(Context context, AttributeSet attrs, int defStyle) {
            super(context, attrs, defStyle);
        }

        /**
         * We have to know our width before we can compute the snippet string.  Do that
         * here and then defer to super for whatever work is normally done.
         */
        @Override
        protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        	if(mFullText == null) {
        		mFullText = "";
        	}
            String fullTextLower = mFullText.toLowerCase();
            String targetStringLower = mTargetString.toLowerCase();

            int startPos = fullTextLower.indexOf(targetStringLower);
            int searchStringLength = targetStringLower.length();
            int bodyLength = fullTextLower.length();

            TextPaint tp = getPaint();

            float searchStringWidth = tp.measureText(mTargetString);
            float textFieldWidth = getWidth();

            String snippetString = null;
            if (searchStringWidth > textFieldWidth) {
                snippetString = mFullText.substring(startPos, startPos + searchStringLength);
            } else {
                float ellipsisWidth = tp.measureText(sEllipsis);
                textFieldWidth -= (2F * ellipsisWidth); // assume we'll need one on both ends

                int offset = -1;
                int start = -1;
                int end = -1;
                /* TODO: this code could be made more efficient by only measuring the additional
                 * characters as we widen the string rather than measuring the whole new
                 * string each time.
                 */
                while (true) {
                    offset += 1;

                    int newstart = Math.max(0, startPos - offset);
                    int newend = Math.min(bodyLength, startPos + searchStringLength + offset);

                    if (newstart == start && newend == end) {
                        // if we couldn't expand out any further then we're done
                        break;
                    }
                    start = newstart;
                    end = newend;

                    // pull the candidate string out of the full text rather than body
                    // because body has been toLower()'ed
                    String candidate = mFullText.substring(start, end);
                    if (tp.measureText(candidate) > textFieldWidth) {
                        // if the newly computed width would exceed our bounds then we're done
                        // do not use this "candidate"
                        break;
                    }

                    snippetString = String.format(
                            "%s%s%s",
                            start == 0 ? "" : sEllipsis,
                            candidate,
                            end == bodyLength ? "" : sEllipsis);
                }
            }

            String snippetStringLower = snippetString.toLowerCase();
            SpannableString spannable = new SpannableString(snippetString);
            int start = 0;
            while (true) {
                int index = snippetStringLower.indexOf(targetStringLower, start);
                if (index == -1) {
                    break;
                }
//              spannable.setSpan(new ForegroundColorSpan(sHighlightColor), index, index+searchStringLength, 0);
                spannable.setSpan(new StyleSpan(sTypefaceHighlight), index, index+searchStringLength, 0);
                start = index + searchStringLength;
            }
            setText(spannable);

            // do this after the call to setText() above
            super.onLayout(changed, left, top, right, bottom);
        }

        public void setText(String fullText, String target) {
            mFullText = fullText;
            mTargetString = target;
            requestLayout();
        }
    }

    BroadcastReceiver killOneself = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			finish();
		}
	};
    BroadcastReceiver updateDialog = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			intent.setClass(context, HUpdateDialog.class);
			context.startActivity(intent);
		}
	};
	
	@Override
	protected void onDestroy() {
		unregisterReceiver(killOneself);
		unregisterReceiver(updateDialog);
		super.onDestroy();
	}
	
    public void onCreate(Bundle icicle)
    {
        super.onCreate(icicle);
        setContentView(R.layout.search_activity);

        registerReceiver(killOneself, new IntentFilter(HConst.ACTION_KILL_ONESELF));
		registerReceiver(updateDialog, new IntentFilter(HConst.ACTION_UPDATE_DIALOG));
		
        String searchStringParameter = getIntent().getStringExtra(SearchManager.QUERY).trim();
        final String searchString = 
        	searchStringParameter != null ? searchStringParameter.trim() : searchStringParameter;
        ContentResolver cr = getContentResolver();
        
        final TextView empty_msg = (TextView) findViewById(R.id.empty);
        empty_msg.setText(R.string.loading);

        final ListView listView = getListView();
        listView.setSelector(android.R.drawable.list_selector_background);
        listView.setItemsCanFocus(true);
        listView.setFocusable(true);
        listView.setClickable(true);
        Z(searchString);

        // I considered something like "searching..." but typically it will
        // flash on the screen briefly which I found to be more distracting
        // than beneficial.
        // This gets updated when the query completes.
        setTitle("");

        // When the query completes cons up a new adapter and set our list adapter to that.
        mQueryHandler = new AsyncQueryHandler(cr) {
            protected void onQueryComplete(int token, Object cookie, Cursor c) {
            	
            	String thread_id = "thread_id";
            	String address = "address";
            	String body = "body";
            	String _id = "_id";
            	
            	//TODO 为什么这样改
            	/**
            	 * 小米 2.3.5 短信数据库和原生短信数据库字段一样
            	 */
//            	if(Build.MODEL.equals("MI-ONE Plus")){
//            		thread_id = "thread_id";
//            		address = "suggest_text_1";
//            		body = "suggest_text_2"; 
//            		_id = "_id";
//            	}
            	
            	
                final int threadIdPos = c.getColumnIndex(thread_id);
                final int addressPos  = c.getColumnIndex(address);
                final int bodyPos     = c.getColumnIndex(body);
                final int rowidPos    = c.getColumnIndex(_id);

                int cursorCount = c.getCount();
                if(cursorCount == 0) {
                	empty_msg.setText(R.string.search_empty);
                	listView.setVisibility(View.GONE);
                } else {
                	listView.setVisibility(View.VISIBLE);
                }
                
                setTitle(getResources().getQuantityString(
                        R.plurals.search_results_title,
                        cursorCount,
                        cursorCount,
                        searchString));

                setListAdapter(new CursorAdapter(SearchActivity.this, c) {
                    @Override
                    public void bindView(View view, Context context, Cursor cursor) {
                        final TextView title = (TextView)(view.findViewById(R.id.title));
                        final TextViewSnippet snippet = (TextViewSnippet)(view.findViewById(R.id.subtitle));

                        final long threadId = cursor.getLong(threadIdPos);
                        final long rowid = cursor.getLong(rowidPos);
                        String add = cursor.getString(addressPos);
                        if(add == null)
                        {
                        	add = "";
                        }
                        StringBuffer address = new StringBuffer(add);
                        if(address.toString().equals(""))
                        {
                        	//搜索草稿的情况，主动去寻找关联的address
                        	Conversation conversation = null;
                        	        if (threadId > 0) {
                        	            conversation = Conversation.get(SearchActivity.this, threadId, false);
                        	        }
                        			ContactList contactList = conversation.getRecipients();
                        			final int size = contactList.size();	//联系人的个数
                        			for(int i = 0; i < size; i ++)
                        			{
                        				address.append(contactList.get(i).getNameAndNumber());
                        				if((i + 1) < size)
                        				{
                        					address.append(",");
                        				}
                        			}
                        			title.setText(address.toString());
                        }else
                        {
	                        Contact contact = Contact.get(address.toString(), false);
	                        String titleString = contact.getNameAndNumber();
	                        title.setText(titleString);
                        }
                        
                        Contact.addListener(new Contact.UpdateListener() {
                            public void onUpdate(final Contact updated) {
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        title.setText(updated.getNameAndNumber());
                                    }
                                });
                            }
                        });
                        
                        snippet.setText(cursor.getString(bodyPos), searchString);

                        // if the user touches the item then launch the compose message
                        // activity with some extra parameters to highlight the search
                        // results and scroll to the latest part of the conversation
                        // that has a match.

                        view.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View v) {
//                                final Intent onClickIntent = new Intent(SearchActivity.this, ComposeMessageActivity.class);
                            	final Intent onClickIntent = new Intent(SearchActivity.this, HTalkActivity.class);
                            	onClickIntent.putExtra("thread_id", threadId);
                                onClickIntent.putExtra("highlight", searchString);
                                onClickIntent.putExtra("select_id", rowid);
                                startActivity(onClickIntent);
                            }
                        });
                    }

                    @Override
                    public View newView(Context context, Cursor cursor, ViewGroup parent) {
                        LayoutInflater inflater = LayoutInflater.from(context);
                        View v = inflater.inflate(R.layout.search_item, parent, false);
                        return v;
                    }

                });

                // ListView seems to want to reject the setFocusable until such time
                // as the list is not empty.  Set it here and request focus.  Without
                // this the arrow keys (and trackball) fail to move the selection.
                listView.setFocusable(true);
                listView.setFocusableInTouchMode(true);
                listView.requestFocus();
            }
        };

        // don't pass a projection since the search uri ignores it
        Uri uri = TelephonyMy.MmsSms.SEARCH_URI.buildUpon().appendQueryParameter("pattern", searchString).build();
        //content://mms-sms/search?pattern=10
        
        System.out.println("uri------>>>" + uri);

        // kick off a query for the threads which match the search string
//        mQueryHandler.startQuery(0, null, uri, null, null, null, null);
//        this.b.startQuery(0, null, this.h, this.i, this.j, null, this.k);
        mQueryHandler.startQuery(0, null, TelephonyMy.Sms.CONTENT_URI, arrayOfString_i, str_j, null, Str_k);
    }
    
    String[] arrayOfString_i;
    String str_j;
    String Str_k;

    private void Z(String str3)
    {
      String str2;
      Bundle localBundle = getIntent().getBundleExtra("app_data");
      if (localBundle != null)
        localBundle.getString("SeachType");
      String str1 = getIntent().getStringExtra("query");
      if (str1 == null)
        str1 = getIntent().getStringExtra("intent_extra_data_key");
      if (str1 != null)
        str2 = str1.trim();
      while (true){
        str1.trim();
        String str4 = " body like '%" + str3 + "%' " + " or address like '%" + str3 + "%' ";
        String[] arrayOfString = new String[6];
        arrayOfString[0] = "_id";
        arrayOfString[1] = "thread_id";
        arrayOfString[2] = "address";
        arrayOfString[3] = "body";
        arrayOfString[4] = "date";
        arrayOfString[5] = "protocol";
        arrayOfString_i = arrayOfString;
        String str5 = "SELECT pdu._id as _id,thread_id,addr.address as address,part.text as body,pdu.date as date, -100 as protocol FROM pdu,part,addr WHERE ((part.mid=pdu._id) AND (addr.msg_id=pdu._id) AND ((addr.type=151 AND pdu.msg_box != 1 ) OR (addr.type=137 AND pdu.msg_box == 1 ))  AND (part.ct='text/plain')) AND " + str4;
        str_j = " 1=1) AND " + str4 + "UNION  " + str5 + "GROUP BY (thread_id";
        Str_k = "date DESC,thread_id ASC";
        return;
      }
    }
    
    
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
    	if(keyCode == KeyEvent.KEYCODE_SEARCH)
    	{
    		return true;
    	}
        return super.onKeyDown(keyCode, event);
    }
}
