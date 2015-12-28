package com.haolianluo.sms2.ui.sms2;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.haolianluo.sms2.R;
import com.haolianluo.sms2.mms.data.Contact;
import com.haolianluo.sms2.ui.ConversationListItem;
import com.haolianluo.sms2.ui.ConversationListItemData;

public class HThreadListItem extends ConversationListItem {
	
	private ImageView mTheadHead;
	private TextView mTextViewNewMsg;
	private TextView mTextViewIsmms;

	public HThreadListItem(Context context) {
        super(context);
    }

    public HThreadListItem(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    
    @Override
    protected void onFinishInflate() {
    	super.onFinishInflate();
    	mTheadHead = (ImageView) findViewById(R.id.iv_thread_head);
    	mTextViewNewMsg = (TextView)findViewById(R.id.tv_thread_unread);
    	mTextViewIsmms = (TextView)findViewById(R.id.tv_thread_ismms);
    }
    
    @Override
    protected void updateAvatarView() {
    	super.updateAvatarView();
    	ConversationListItemData ch = mConversationHeader;
    	Drawable avatarDrawable;
    	sDefaultContactImage = SkinActivity.mSkinManage.getDrawable(mContext, R.drawable.def_head);
        if (ch.getContacts().size() == 1) {
            Contact contact = ch.getContacts().get(0);
            avatarDrawable = contact.getAvatar(mContext, sDefaultContactImage);
        } else {
            avatarDrawable = sDefaultContactImage;
        }
        mTheadHead.setImageDrawable(avatarDrawable);
        boolean hasError = ch.hasError();
        if(ch.isRead())
        {
        	mTextViewNewMsg.setVisibility(View.GONE);
        }
        else
        {
        	if(hasError) {
        		mTextViewNewMsg.setVisibility(View.GONE);
        	} else {
        		mTextViewNewMsg.setVisibility(View.VISIBLE);
        		if(ch.getUnreadCount() > 0)
        		{
        			mTextViewNewMsg.setText("" + ch.getUnreadCount());
        		}
        	}
        }
        if(ch.getIsMMS())
        {
        	mTextViewIsmms.setVisibility(View.VISIBLE);
        }
        else
        {
        	mTextViewIsmms.setVisibility(View.GONE);
        }
    }
    
}
