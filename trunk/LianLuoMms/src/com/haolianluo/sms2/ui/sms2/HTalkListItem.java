package com.haolianluo.sms2.ui.sms2;

import android.content.Context;
import android.provider.TelephonyMy.Mms;
import android.provider.TelephonyMy.Sms;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.haolianluo.sms2.R;
import com.haolianluo.sms2.ui.MessageListItem;

public class HTalkListItem extends MessageListItem {

	private LinearLayout mMsgListItemGravity;
	
	public HTalkListItem(Context context) {
		super(context);
	}

	public HTalkListItem(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		mMsgListItemGravity = (LinearLayout) findViewById(R.id.msg_list_item_gravity);
	}
	
	@Override
	public SpannableStringBuilder getSpannableStringBuilder(String contact) {
		CharSequence template = mContext.getResources().getText(R.string.name_colon);
    	return new SpannableStringBuilder(TextUtils.replace(template, new String[] { "%s： " }, new CharSequence[] {""}));
	}
	
	@Override
	protected void drawLeftStatusIndicator(int msgBoxId) {
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		switch (msgBoxId) {
        case Mms.MESSAGE_BOX_INBOX:
        	SkinActivity.mSkinManage.setBackground(mMsgListItemGravity, R.drawable.talklayout_left_list_xml);
            mBodyTextView.setGravity(Gravity.LEFT);
            ((LinearLayout.LayoutParams) mMsgListItemGravity.getLayoutParams()).gravity = Gravity.LEFT;
            params.addRule(RelativeLayout.ALIGN_RIGHT);
            mBodyTextView.setLayoutParams(params);
            break;

        case Mms.MESSAGE_BOX_DRAFTS:
        case Sms.MESSAGE_TYPE_FAILED:
        case Sms.MESSAGE_TYPE_QUEUED:
        case Mms.MESSAGE_BOX_OUTBOX:
        	SkinActivity.mSkinManage.setBackground(mMsgListItemGravity, R.drawable.talklayout_right_list_xml);
            mBodyTextView.setGravity(Gravity.LEFT);
            ((LinearLayout.LayoutParams) mMsgListItemGravity.getLayoutParams()).gravity = Gravity.RIGHT;
            params.addRule(RelativeLayout.ALIGN_LEFT);
            mBodyTextView.setLayoutParams(params);
            break;
        default:
        	SkinActivity.mSkinManage.setBackground(mMsgListItemGravity, R.drawable.talklayout_right_list_xml);
            mBodyTextView.setGravity(Gravity.LEFT);
            ((LinearLayout.LayoutParams) mMsgListItemGravity.getLayoutParams()).gravity = Gravity.RIGHT;
            params.addRule(RelativeLayout.ALIGN_LEFT);
            mBodyTextView.setLayoutParams(params);
            break;
		}
	}

}
