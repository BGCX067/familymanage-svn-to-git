/*
 * Copyright (C) 2008 Esmertec AG.
 * Copyright (C) 2008 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.haolianluo.sms2.ui;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.text.style.TextAppearanceSpan;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SkinRelativeLayout;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.haolianluo.sms2.R;
import com.haolianluo.sms2.data.HConst;
import com.haolianluo.sms2.mms.data.Contact;
import com.haolianluo.sms2.mms.data.ContactList;
import com.haolianluo.sms2.ui.sms2.HService;
import com.haolianluo.sms2.ui.sms2.SkinActivity;

/**
 * This class manages the view for given conversation.
 */
public class ConversationListItem extends SkinRelativeLayout implements Contact.UpdateListener {
    private static final String TAG = "ConversationListItem";
    private static final boolean DEBUG = false;

    protected Context mContext;
    private TextView mSubjectView;
    private TextView mFromView;
    private TextView mDateView;
    private View mAttachmentView;
    private View mErrorIndicator;
    private ImageView mPresenceView;
//    private QuickContactBadge mAvatarView;
    
    private CheckBox thread_check;

    static protected Drawable sDefaultContactImage;

    // For posting UI update Runnables from other threads:
    private Handler mHandler = new Handler();

    protected ConversationListItemData mConversationHeader;

    private static final StyleSpan STYLE_BOLD = new StyleSpan(Typeface.BOLD);

    public ConversationListItem(Context context) {
        super(context);
        this.mContext = context;
    }

    public ConversationListItem(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        if (sDefaultContactImage == null) {
            sDefaultContactImage = SkinActivity.mSkinManage.getDrawable(mContext, R.drawable.def_head);
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        
        mFromView = (TextView) findViewById(R.id.from);
        mSubjectView = (TextView) findViewById(R.id.subject);

        mDateView = (TextView) findViewById(R.id.date);
        mAttachmentView = findViewById(R.id.attachment);
        mErrorIndicator = findViewById(R.id.error);
        mPresenceView = (ImageView) findViewById(R.id.presence);
//        mAvatarView = (QuickContactBadge) findViewById(R.id.avatar);
        
        thread_check = (CheckBox) findViewById(R.id.thread_check);
    }

    public void setPresenceIcon(int iconId) {
        if (iconId == 0) {
            mPresenceView.setVisibility(View.GONE);
        } else {
            mPresenceView.setImageResource(iconId);
            mPresenceView.setVisibility(View.VISIBLE);
        }
    }

    public ConversationListItemData getConversationHeader() {
        return mConversationHeader;
    }

    private void setConversationHeader(ConversationListItemData header) {
        mConversationHeader = header;
    }

    /**
     * Only used for header binding.
     */
    public void bind(String title, String explain) {
        mFromView.setText(title);
        mSubjectView.setText(explain);
    }

    private CharSequence formatMessage(ConversationListItemData ch) {
        final int size = android.R.style.TextAppearance_Small;
//        final int color = android.R.styleable.Theme_textColorSecondary;
        String from = ch.getFrom();

        SpannableStringBuilder buf = new SpannableStringBuilder(from);

        if (ch.getMessageCount() > 1) {
            buf.append(" (" + ch.getMessageCount() + ") ");
        }

        int before = buf.length();
        if (ch.hasDraft() && !HConst.iscollect) {
            buf.append(" ");
            buf.append(mContext.getResources().getString(R.string.has_draft));
            buf.setSpan(new TextAppearanceSpan(mContext, size), before,
                    buf.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
            buf.setSpan(new ForegroundColorSpan(
                    mContext.getResources().getColor(R.drawable.text_color_red)),
                    before, buf.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        }

        // Unread messages are shown in bold
        if (!ch.isRead()) {
            buf.setSpan(STYLE_BOLD, 0, buf.length(),
                    Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        }
        return buf;
    }

    protected void updateAvatarView() {
        ConversationListItemData ch = mConversationHeader;

        Drawable avatarDrawable;
        if (ch.getContacts().size() == 1) {
            Contact contact = ch.getContacts().get(0);
            avatarDrawable = contact.getAvatar(mContext, sDefaultContactImage);

//            if (contact.existsInDatabase()) {
//                mAvatarView.assignContactUri(contact.getUri());
//            } else {
//                mAvatarView.assignContactFromPhone(contact.getNumber(), true);
//            }
        } else {
            // TODO get a multiple recipients asset (or do something else)
            avatarDrawable = sDefaultContactImage;
//            mAvatarView.assignContactUri(null);
        }
//        mAvatarView.setImageDrawable(avatarDrawable);
//        mAvatarView.setVisibility(View.VISIBLE);
    }

    private void updateFromView() {
        ConversationListItemData ch = mConversationHeader;
        ch.updateRecipients();
        mFromView.setText(formatMessage(ch));
        setPresenceIcon(ch.getContacts().getPresenceResId());
        updateAvatarView();
    }

    public void onUpdate(Contact updated) {
        mHandler.post(new Runnable() {
            public void run() {
                updateFromView();
            }
        });
    }

    public  void bind(Context context, final ConversationListItemData ch) {

        setConversationHeader(ch);

//        Drawable background = ch.isRead()?
//                mContext.getResources().getDrawable(R.drawable.conversation_item_background_read) :
//                mContext.getResources().getDrawable(R.drawable.conversation_item_background_unread);
//
//        setBackgroundDrawable(background);

        LayoutParams attachmentLayout = (LayoutParams)mAttachmentView.getLayoutParams();
        boolean hasError = ch.hasError();
        // When there's an error icon, the attachment icon is left of the error icon.
        // When there is not an error icon, the attachment icon is left of the date text.
        // As far as I know, there's no way to specify that relationship in xml.
        if (hasError) {
            attachmentLayout.addRule(RelativeLayout.LEFT_OF, R.id.error);
        } else {
            attachmentLayout.addRule(RelativeLayout.LEFT_OF, R.id.date);
        }

        boolean hasAttachment = ch.hasAttachment();
        mAttachmentView.setVisibility(hasAttachment ? VISIBLE : GONE);

        // Date
        mDateView.setText(ch.getDate());
        
        if(HService.htread_check_flag) {
        	thread_check.setVisibility(View.VISIBLE);
        } else {
        	thread_check.setVisibility(View.GONE);
        }
        thread_check.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked) {
					HService.htread_map.put(ch.getThreadId(), true);
				} else {
					//HService.htread_map.put(ch.getThreadId(), false);
					HService.htread_map.remove(ch.getThreadId());
				}
				
			}
		});
        if(HService.htread_map.get(ch.getThreadId()) != null && HService.htread_map.get(ch.getThreadId())) {
        	thread_check.setChecked(true);
        } else {
        	thread_check.setChecked(false);
        }

        // From.
        mFromView.setText(formatMessage(ch));

        // Register for updates in changes of any of the contacts in this conversation.
        ContactList contacts = ch.getContacts();

        Contact.addListener(this);
        setPresenceIcon(contacts.getPresenceResId());

        // Subject
        mSubjectView.setText(ch.getSubject());
        LayoutParams subjectLayout = (LayoutParams)mSubjectView.getLayoutParams();
        // We have to make the subject left of whatever optional items are shown on the right.
        subjectLayout.addRule(RelativeLayout.LEFT_OF, hasAttachment ? R.id.attachment :
            (hasError ? R.id.error : R.id.date));

        // Transmission error indicator.
        mErrorIndicator.setVisibility(hasError ? VISIBLE : GONE);

        updateAvatarView();
    }

    public final void unbind() {
        // Unregister contact update callbacks.
        Contact.removeListener(this);
    }
}
