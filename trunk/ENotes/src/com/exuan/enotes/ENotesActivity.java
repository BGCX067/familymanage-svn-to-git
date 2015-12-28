
package com.exuan.enotes;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Calendar;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class ENotesActivity extends Activity {

    public static final String PREFERENCES_NAME = "enotes_prefs";
    private static final String SD_FOLDER_NAME = "pirate_notes";
    public static String[] mWeek;
    private Context mContext;

    private ListView mListViewFolders;
    private ListView mListViewNotes;
    private ImageView mTextViewAddFolder;
    private ImageView mTextViewAddNote;
    private Dialog mDialog;
    private TextView mTextViewTitle;
    private LinearLayout mLinearLayoutDeleteFolder;
    private LinearLayout mLinearLayoutDeleteNote;
    private TextView mTextViewDeleteFolder;
    private TextView mTextViewDeleteNote;
    private TextView mTextViewCancelFolder;
    private TextView mTextViewCancelNote;
    private TextView mTextViewMoveNote;
    private ProgressDialog mProgress;

    private FoldersAdapter mFoldersAdapter;
    private NotesAdapter mNotesAdapter;

    private int mCurrentFolder = 0;
    private int mFolderID = 1;
    private boolean mIsSDOn;

    private boolean mEditFolder = false;
    private boolean mEditNote = false;
    private static final int DIALOG_FOLDER_NAME_ID = 0;
    private static final int DIALOG_CONFIRM_DELETE_ID = 1;
    private static final int DIALOG_RENAME_FOLDER_ID = 2;
    private static final int DIALOG_CONFIRM_EXPORT_ID = 3;
    private static final int DIALOG_SELECT_FOLDER_ID = 4;

    private static final int REQUEST_CODE_SHARE = 1;
    private static final int EXPORT_FAILED = 0;
    private static final int EXPORT_SUCCEED = 1;
    private static final int IMPORT_FAILED = 2;
    private static final int IMPORT_SUCCEED = 3;

    private static final int MENU_DELETE_FOLDER = 0;
    private static final int MENU_DELETE_NOTE = MENU_DELETE_FOLDER + 1;
    private static final int MENU_RENAME_FOLDER = MENU_DELETE_NOTE + 1;
    private static final int MENU_SD_EXPORT = MENU_RENAME_FOLDER + 1;
    private static final int MENU_SD_IMPORT = MENU_SD_EXPORT + 1;
    private static final int MENU_SHARE = MENU_SD_IMPORT + 1;
    private static final int MENU_ABOUT = MENU_SHARE + 1;

    public static boolean[] mFolderCheck = null;
    public static boolean[] mNoteCheck = null;
    private int mWidth;
    public static Integer[] mThumbIds = {
            R.drawable.bk_red, R.drawable.bk_orange,
            R.drawable.bk_yellow, R.drawable.bk_green,
            R.drawable.bk_grey, R.drawable.bk_blue,
            R.drawable.bk_purple
    };

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main);
        mContext = this;
        mWidth = getWindowManager().getDefaultDisplay().getWidth();
        mWeek = mContext.getResources().getStringArray(R.array.week_array);
        initData();
        mIsSDOn = Environment.getExternalStorageState()
                .equals(android.os.Environment.MEDIA_MOUNTED);
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_MEDIA_MOUNTED);
        intentFilter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
        intentFilter.addAction(Intent.ACTION_MEDIA_REMOVED);
        intentFilter.addAction(Intent.ACTION_MEDIA_BAD_REMOVAL);
        intentFilter.addDataScheme("file");
        registerReceiver(mSDReceiver, intentFilter);
        mListViewFolders = (ListView) findViewById(R.id.listview_folsers);
        mListViewFolders.setLayoutParams(new LinearLayout.LayoutParams(mWidth / 4,
                LinearLayout.LayoutParams.MATCH_PARENT));
        mListViewNotes = (ListView) findViewById(R.id.listview_notes);
        mTextViewAddFolder = (ImageView) findViewById(R.id.textview_add_folder);
        mTextViewAddNote = (ImageView) findViewById(R.id.textview_add_note);
        mTextViewTitle = (TextView) findViewById(R.id.textview_title);
        mLinearLayoutDeleteFolder = (LinearLayout) findViewById(R.id.linearlayout_delete_folder);
        mLinearLayoutDeleteNote = (LinearLayout) findViewById(R.id.linearlayout_delete_note);
        mTextViewDeleteFolder = (TextView) findViewById(R.id.textview_delete_folder);
        mTextViewDeleteFolder.setPadding(0, 0, mWidth / 6, 0);
        mTextViewDeleteNote = (TextView) findViewById(R.id.textview_delete_note);
        mTextViewCancelFolder = (TextView) findViewById(R.id.textview_cancel_folder);
        mTextViewCancelFolder.setPadding(mWidth / 6, 0, 0, 0);
        mTextViewCancelNote = (TextView) findViewById(R.id.textview_cancel_note);
        mTextViewMoveNote = (TextView) findViewById(R.id.textview_move_note);
        mTextViewMoveNote.setPadding(mWidth / 6, 0, mWidth / 6, 0);
        Cursor folderCursor = getContentResolver().query(NotesProvider.CONTENT_URI_FOLDERS, null, null,
                null, null);
        mFoldersAdapter = new FoldersAdapter(mContext, folderCursor);
        folderCursor = null;
        mListViewFolders.setAdapter(mFoldersAdapter);
        Cursor noteCursor = getContentResolver().query(NotesProvider.CONTENT_URI_NOTES, null,
                NotesDatabaseHelper.FOLDER_ID + " = '" + mFolderID + "'", null,
                NotesDatabaseHelper.MODIFY_TIME + " DESC");
        mNotesAdapter = new NotesAdapter(mContext, noteCursor);
        noteCursor = null;
        mListViewNotes.setAdapter(mNotesAdapter);
        mTextViewAddFolder.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                if (mEditFolder)
                {
                    setDeleteFolderMode(false);
                }
                else if (mEditNote)
                {
                    setDeleteNoteMode(false);
                }
                else
                {
                    removeDialog(DIALOG_FOLDER_NAME_ID);
                    showDialog(DIALOG_FOLDER_NAME_ID);
                }
            }
        });

        mTextViewAddNote.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                if (mEditFolder)
                {
                    setDeleteFolderMode(false);
                }
                else if (mEditNote)
                {
                    setDeleteNoteMode(false);
                }
                else
                {
                    Intent intent = new Intent(mContext, EDetailActivity.class);
                    intent.putExtra(NotesDatabaseHelper.FOLDER_ID, mFolderID);
                    startActivity(intent);
                }
            }
        });

        mListViewFolders.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                    long arg3) {
                // TODO Auto-generated method stub
                Cursor folderCursor = mFoldersAdapter.getCursor();
                folderCursor.moveToPosition(arg2);
                mCurrentFolder = arg2;
                folderCursor.moveToPosition(mCurrentFolder);
                mFolderID = folderCursor.getInt(folderCursor.getColumnIndex("_id"));
                updateView();
                mNoteCheck = new boolean[mNotesAdapter.getCount()];
            }
        });

        mListViewNotes.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                    long arg3) {
                if (mEditFolder)
                {
                    setDeleteFolderMode(false);
                }
                else
                {
                    // TODO Auto-generated method stub
                    Cursor noteCursor = mNotesAdapter.getCursor();
                    noteCursor.moveToPosition(arg2);
                    int id = noteCursor.getInt(noteCursor.getColumnIndex("_id"));
                    Intent intent = new Intent(mContext, EDetailActivity.class);
                    intent.putExtra("_id", id);
                    intent.putExtra(NotesDatabaseHelper.FOLDER_ID, mFolderID);
                    startActivity(intent);
                }
            }
        });

        mListViewFolders.setOnItemLongClickListener(new OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                    int arg2, long arg3) {
                if (0 == arg2)
                {
                    return true;
                }
                final int po = arg2;
                // TODO Auto-generated method stub
                new AlertDialog.Builder(mContext)
                        .setTitle(R.string.delete)
                        .setMessage(R.string.delete_folder)
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // TODO Auto-generated method stub
                                Cursor folderCursor = mFoldersAdapter.getCursor();
                                folderCursor.moveToPosition(po);
                                int id = folderCursor.getInt(folderCursor.getColumnIndex("_id"));
                                Cursor c = mContext.getContentResolver().query(
                                        NotesProvider.CONTENT_URI_NOTES, null,
                                        NotesDatabaseHelper.FOLDER_ID + " = '" + id + "'", null,
                                        null);
                                int count = c.getCount();
                                if (count > 0)
                                {
                                    mFolderCheck = new boolean[folderCursor.getCount()];
                                    mFolderCheck[po] = true;
                                    removeDialog(DIALOG_CONFIRM_DELETE_ID);
                                    showDialog(DIALOG_CONFIRM_DELETE_ID);
                                }
                                else
                                {
                                    mContext.getContentResolver().delete(
                                            NotesProvider.CONTENT_URI_FOLDERS,
                                            "_id = '" + id + "'", null);
                                    mCurrentFolder = 0;
                                    mFolderID = 1;
                                    updateView();
                                }
                                c.close();
                            }
                        })
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // TODO Auto-generated method stub

                            }
                        })
                        .create()
                        .show();
                return true;
            }
        });

        mListViewNotes.setOnItemLongClickListener(new OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                    int arg2, long arg3) {
                final int po = arg2;
                // TODO Auto-generated method stub
                new AlertDialog.Builder(mContext)
                        .setTitle(R.string.delete)
                        .setMessage(R.string.delete_note)
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // TODO Auto-generated method stub
                                Cursor noteCursor = mNotesAdapter.getCursor();
                                noteCursor.moveToPosition(po);
                                int id = noteCursor.getInt(noteCursor.getColumnIndex("_id"));
                                mContext.getContentResolver()
                                        .delete(NotesProvider.CONTENT_URI_NOTES,
                                                "_id = '" + id + "'", null);
                                noteCursor = getContentResolver().query(
                                        NotesProvider.CONTENT_URI_NOTES, null,
                                        NotesDatabaseHelper.FOLDER_ID + " = '" + mFolderID + "'",
                                        null, NotesDatabaseHelper.MODIFY_TIME + " DESC");
                                EDetailActivity.cancelAlarm(mContext, id);
                                updateView();
                            }
                        })
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // TODO Auto-generated method stub

                            }
                        })
                        .create()
                        .show();
                return true;
            }
        });

        mTextViewDeleteFolder.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                int count = 0;
                for (int i = 0; i < mFolderCheck.length; i++)
                {
                    if (mFolderCheck[i])
                    {
                        Cursor folderCursor = mFoldersAdapter.getCursor();
                        folderCursor.moveToPosition(i);
                        int id = folderCursor.getInt(folderCursor.getColumnIndex("_id"));
                        Cursor c = mContext.getContentResolver().query(
                                NotesProvider.CONTENT_URI_NOTES, null,
                                NotesDatabaseHelper.FOLDER_ID + " = '" + id + "'", null, null);
                        count += c.getCount();
                        c.close();
                    }
                }
                if (count > 0)
                {
                    removeDialog(DIALOG_CONFIRM_DELETE_ID);
                    showDialog(DIALOG_CONFIRM_DELETE_ID);
                }
                else
                {
                    for (int i = 0; i < mFolderCheck.length; i++)
                    {
                        if (mFolderCheck[i])
                        {
                            Cursor folderCursor = mFoldersAdapter.getCursor();
                            folderCursor.moveToPosition(i);
                            int id = folderCursor.getInt(folderCursor.getColumnIndex("_id"));
                            mContext.getContentResolver().delete(NotesProvider.CONTENT_URI_FOLDERS,
                                    "_id = '" + id + "'", null);
                        }
                    }
                    mCurrentFolder = 0;
                    mFolderID = 1;
                    updateView();
                    setDeleteFolderMode(false);
                }
            }
        });

        mTextViewDeleteNote.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                for (int i = 0; i < mNoteCheck.length; i++)
                {
                    if (mNoteCheck[i])
                    {
                        Cursor noteCursor = mNotesAdapter.getCursor();
                                noteCursor.moveToPosition(i);
                        int id = noteCursor.getInt(noteCursor.getColumnIndex("_id"));
                        mContext.getContentResolver().delete(NotesProvider.CONTENT_URI_NOTES,
                                "_id = '" + id + "'", null);
                        EDetailActivity.cancelAlarm(mContext, id);
                    }
                }
                updateView();
                setDeleteNoteMode(false);
            }
        });

        mTextViewCancelFolder.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                setDeleteFolderMode(false);
            }
        });

        mTextViewCancelNote.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                setDeleteNoteMode(false);
            }
        });

        mTextViewMoveNote.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                removeDialog(DIALOG_SELECT_FOLDER_ID);
                showDialog(DIALOG_SELECT_FOLDER_ID);
            }
        });
    }

    public void onResume()
    {
        super.onResume();
        if (mEditFolder || mEditNote)
        {
            return;
        }
        else
        {
            updateView();
        }
    }

    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_BACK)
        {
            if (mEditFolder)
            {
                setDeleteFolderMode(false);
                return true;
            }
            else if (mEditNote)
            {
                setDeleteNoteMode(false);
                return true;
            }
            finish();
            return true;
        }
        return false;
    }

    public void onDestroy()
    {
        super.onDestroy();
        mFoldersAdapter.changeCursor(null);
        mNotesAdapter.changeCursor(null);
        unregisterReceiver(mSDReceiver);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, MENU_DELETE_FOLDER, 0, R.string.delete_folder).setIcon(
                R.drawable.menu_delete_folder);
        menu.add(0, MENU_DELETE_NOTE, 0, R.string.delete_note).setIcon(
                R.drawable.menu_delete_note);
        menu.add(0, MENU_RENAME_FOLDER, 0, R.string.rename_folder).setIcon(
                R.drawable.menu_rename_folder);
        menu.add(0, MENU_SD_EXPORT, 0, R.string.sd_export).setIcon(
                R.drawable.menu_export);
        menu.add(0, MENU_SD_IMPORT, 0, R.string.sd_import).setIcon(
                R.drawable.menu_export);
        menu.add(0, MENU_SHARE, 0, R.string.share).setIcon(
                R.drawable.menu_share);
        menu.add(0, MENU_ABOUT, 0, R.string.about).setIcon(
                R.drawable.menu_about);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case MENU_DELETE_FOLDER: {
                if (mEditNote)
                {
                    setDeleteNoteMode(false);
                }
                else if (!mEditFolder)
                {
                    setDeleteFolderMode(true);
                }
                return true;
            }
            case MENU_DELETE_NOTE: {
                if (mEditFolder)
                {
                    setDeleteFolderMode(false);
                }
                else if (!mEditNote)
                {
                    setDeleteNoteMode(true);
                }
                return true;
            }
            case MENU_RENAME_FOLDER: {
                removeDialog(DIALOG_RENAME_FOLDER_ID);
                showDialog(DIALOG_RENAME_FOLDER_ID);
                return true;
            }
            case MENU_SD_EXPORT: {
                if (!mIsSDOn)
                {
                    Toast.makeText(mContext, getString(R.string.plugin_sd), Toast.LENGTH_LONG)
                            .show();
                }
                else
                {
                    if (mProgress == null)
                    {
                        mProgress = new ProgressDialog(mContext);
                        mProgress.setMessage(getString(R.string.exporting));
                        mProgress.show();
                    }
                    new ExportThread().start();
                }
                return true;
            }
            case MENU_SD_IMPORT: {
                if (!mIsSDOn)
                {
                    Toast.makeText(mContext, getString(R.string.plugin_sd), Toast.LENGTH_LONG)
                            .show();
                }
                else
                {
                    if (mProgress == null)
                    {
                        mProgress = new ProgressDialog(mContext);
                        mProgress.setMessage(getString(R.string.exporting));
                        mProgress.show();
                    }
                    new ImportThread().start();
                }
                return true;
            }
            case MENU_SHARE: {
                Intent intent = new Intent("android.intent.action.SEND");
                intent.setType("text/*");
                intent.putExtra("android.intent.extra.SUBJECT", getString(R.string.app_name));
                intent.putExtra("android.intent.extra.TEXT", getString(R.string.share_content));
                Intent i = Intent.createChooser(intent, getString(R.string.share_way));
                startActivityForResult(i, REQUEST_CODE_SHARE);
                return true;
            }
            case MENU_ABOUT: {
                Intent i = new Intent(mContext, EAboutActivity.class);
                startActivity(i);
                return true;
            }
        }
        return false;
    }

    public void onConfigurationChanged(Configuration newConfig)
    {
        try
        {
            super.onConfigurationChanged(newConfig);
        } catch (Exception ex)
        {

        }
    }

    private void setDeleteFolderMode(boolean mode)
    {
        if (mode)
        {
            Cursor folderCursor = mFoldersAdapter.getCursor();
            mFolderCheck = new boolean[folderCursor.getCount()];
        }
        else
        {
            mFolderCheck = null;
        }
        mEditFolder = mode;
        mFoldersAdapter.updateMode(mode);
        if (mode)
        {
            mLinearLayoutDeleteFolder.setVisibility(View.VISIBLE);
        }
        else
        {
            mLinearLayoutDeleteFolder.setVisibility(View.GONE);
        }
    }

    private void setDeleteNoteMode(boolean mode)
    {
        if (mode)
        {
            mNoteCheck = new boolean[mNotesAdapter.getCursor().getCount()];
        }
        else
        {
            mNoteCheck = null;
        }
        mEditNote = mode;
        mNotesAdapter.updateMode(mode);
        if (mode)
        {
            mLinearLayoutDeleteNote.setVisibility(View.VISIBLE);
        }
        else
        {
            mLinearLayoutDeleteNote.setVisibility(View.GONE);
        }
    }

    public Dialog onCreateDialog(int id)
    {
        switch (id)
        {
            case DIALOG_FOLDER_NAME_ID: {
                final EditText edit = new EditText(mContext);
                mDialog = new AlertDialog.Builder(mContext)
                        .setTitle(R.string.set_folder_name)
                        .setView(edit)
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                    int whichButton) {
                                if (edit.getText().toString().trim().length() == 0)
                                {
                                    try
                                    {
                                        Field field = mDialog.getClass().getSuperclass()
                                                .getDeclaredField("mShowing");
                                        field.setAccessible(true);
                                        field.set(mDialog, false);
                                    }
                                    catch (Exception e)
                                    {
                                    }
                                    Toast.makeText(mContext,
                                            mContext.getString(R.string.input_folder_name),
                                            Toast.LENGTH_SHORT).show();
                                }
                                else
                                {

                                    if (getContentResolver().query(
                                            NotesProvider.CONTENT_URI_FOLDERS,
                                            null,
                                            NotesDatabaseHelper.FOLDER_NAME + " = '"
                                                    + edit.getText().toString() + "'",
                                            null, null).getCount() > 0)
                                    {
                                        try
                                        {
                                            Field field = mDialog.getClass().getSuperclass()
                                                    .getDeclaredField("mShowing");
                                            field.setAccessible(true);
                                            field.set(mDialog, false);
                                        }
                                        catch (Exception e)
                                        {
                                        }
                                        Toast.makeText(mContext,
                                                getString(R.string.folder_name_exist),
                                                Toast.LENGTH_SHORT).show();
                                    }
                                    else
                                    {
                                        try
                                        {
                                            Field field = mDialog.getClass().getSuperclass()
                                                    .getDeclaredField("mShowing");
                                            field.setAccessible(true);
                                            field.set(mDialog, true);
                                        }
                                        catch (Exception e)
                                        {
                                        }
                                        ContentValues values = new ContentValues();
                                        values.put(NotesDatabaseHelper.FOLDER_NAME, edit.getText()
                                                .toString());
                                        getContentResolver().insert(
                                                NotesProvider.CONTENT_URI_FOLDERS, values);
                                        updateView();
                                    }
                                }
                            }
                        })
                        .setNegativeButton(R.string.cancel,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                            int whichButton) {
                                        try
                                        {
                                            Field field = mDialog.getClass().getSuperclass()
                                                    .getDeclaredField("mShowing");
                                            field.setAccessible(true);
                                            field.set(mDialog, true);
                                        }
                                        catch (Exception e)
                                        {
                                        }
                                    }
                                })
                        .create();
                return mDialog;
            }
            case DIALOG_RENAME_FOLDER_ID: {
                final EditText edit = new EditText(mContext);
                Cursor folderCursor = mFoldersAdapter.getCursor();
                folderCursor.moveToPosition(mCurrentFolder);
                String title = folderCursor.getString(folderCursor
                        .getColumnIndex(NotesDatabaseHelper.FOLDER_NAME));
                edit.setText(title);
                mDialog = new AlertDialog.Builder(mContext)
                        .setTitle(R.string.set_folder_name)
                        .setView(edit)
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                    int whichButton) {
                                if (edit.getText().toString().trim().length() == 0)
                                {
                                    try
                                    {
                                        Field field = mDialog.getClass().getSuperclass()
                                                .getDeclaredField("mShowing");
                                        field.setAccessible(true);
                                        field.set(mDialog, false);
                                    }
                                    catch (Exception e)
                                    {
                                    }
                                    Toast.makeText(mContext,
                                            mContext.getString(R.string.input_folder_name),
                                            Toast.LENGTH_SHORT).show();
                                }
                                else
                                {

                                    if (getContentResolver().query(
                                            NotesProvider.CONTENT_URI_FOLDERS,
                                            null,
                                            NotesDatabaseHelper.FOLDER_NAME + " = '"
                                                    + edit.getText().toString() + "'",
                                            null, null).getCount() > 0)
                                    {
                                        try
                                        {
                                            Field field = mDialog.getClass().getSuperclass()
                                                    .getDeclaredField("mShowing");
                                            field.setAccessible(true);
                                            field.set(mDialog, false);
                                        }
                                        catch (Exception e)
                                        {
                                        }
                                        Toast.makeText(mContext,
                                                getString(R.string.folder_name_exist),
                                                Toast.LENGTH_SHORT).show();
                                    }
                                    else
                                    {
                                        try
                                        {
                                            Field field = mDialog.getClass().getSuperclass()
                                                    .getDeclaredField("mShowing");
                                            field.setAccessible(true);
                                            field.set(mDialog, true);
                                        }
                                        catch (Exception e)
                                        {
                                        }
                                        ContentValues values = new ContentValues();
                                        values.put(NotesDatabaseHelper.FOLDER_NAME, edit.getText()
                                                .toString());
                                        getContentResolver().update(
                                                NotesProvider.CONTENT_URI_FOLDERS, values,
                                                "_id ='" + mFolderID + "'", null);
                                        updateView();
                                    }
                                }
                            }
                        })
                        .setNegativeButton(R.string.cancel,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                            int whichButton) {
                                        try
                                        {
                                            Field field = mDialog.getClass().getSuperclass()
                                                    .getDeclaredField("mShowing");
                                            field.setAccessible(true);
                                            field.set(mDialog, true);
                                        }
                                        catch (Exception e)
                                        {
                                        }
                                    }
                                })
                        .create();
                return mDialog;
            }
            case DIALOG_CONFIRM_DELETE_ID: {
                mDialog = new AlertDialog.Builder(mContext)
                        .setMessage(getString(R.string.confirm_delete))
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                    int whichButton) {
                                for (int i = 0; i < mFolderCheck.length; i++)
                                {
                                    if (mFolderCheck[i])
                                    {
                                        Cursor folderCursor = mFoldersAdapter.getCursor();
                                        folderCursor.moveToPosition(i);
                                        int id = folderCursor.getInt(folderCursor
                                                .getColumnIndex("_id"));
                                        Cursor note = getContentResolver().query(
                                                NotesProvider.CONTENT_URI_NOTES, new String[] {
                                                    "_id"
                                                },
                                                NotesDatabaseHelper.FOLDER_ID + " = '" + id + "'",
                                                null, null);
                                        while (note.moveToNext())
                                        {
                                            int nid = note.getInt(note.getColumnIndex("_id"));
                                            EDetailActivity.cancelAlarm(mContext, nid);
                                        }
                                        mContext.getContentResolver().delete(
                                                NotesProvider.CONTENT_URI_NOTES,
                                                NotesDatabaseHelper.FOLDER_ID + " = '" + id + "'",
                                                null);
                                        mContext.getContentResolver().delete(
                                                NotesProvider.CONTENT_URI_FOLDERS,
                                                "_id = '" + id + "'", null);
                                        note.close();
                                    }
                                }
                                mCurrentFolder = 0;
                                mFolderID = 1;
                                updateView();
                                setDeleteFolderMode(false);
                            }
                        })
                        .setNegativeButton(R.string.cancel,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                            int whichButton) {
                                    }
                                })
                        .create();
                return mDialog;
            }
            case DIALOG_CONFIRM_EXPORT_ID: {
                mDialog = new AlertDialog.Builder(mContext)
                        .setTitle(R.string.succeed)
                        .setMessage(getString(R.string.export_succeed))
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                    int whichButton) {
                            }
                        })
                        .create();
                return mDialog;
            }
            case DIALOG_SELECT_FOLDER_ID: {
                final Cursor c = getContentResolver().query(NotesProvider.CONTENT_URI_FOLDERS,
                        null, null, null, null);
                String[] name = new String[c.getCount()];
                for (int i = 0; i < c.getCount(); i++)
                {
                    c.moveToPosition(i);
                    name[i] = c.getString(c.getColumnIndex(NotesDatabaseHelper.FOLDER_NAME));
                }
                return new AlertDialog.Builder(mContext)
                        .setTitle(R.string.select_folder)
                        .setItems(name, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                    int whichButton) {
                                Cursor folderCursor = mFoldersAdapter.getCursor();
                                folderCursor.moveToPosition(whichButton);
                                int newID = folderCursor.getInt(folderCursor
                                        .getColumnIndex("_id"));
                                if (newID != mFolderID)
                                {
                                    ContentValues values = new ContentValues();
                                    values.put(NotesDatabaseHelper.FOLDER_ID, newID);
                                    for (int i = 0; i < mNoteCheck.length; i++)
                                    {
                                        if (mNoteCheck[i])
                                        {
                                            Cursor noteCursor = mNotesAdapter.getCursor();
                                            noteCursor.moveToPosition(i);
                                            int id = noteCursor.getInt(noteCursor
                                                    .getColumnIndex("_id"));
                                            mContext.getContentResolver().update(
                                                    NotesProvider.CONTENT_URI_NOTES, values,
                                                    "_id ='" + id + "'", null);
                                        }
                                    }
                                    updateView();
                                }
                                setDeleteNoteMode(false);
                            }
                        })
                        .create();
            }
        }
        return super.onCreateDialog(id);
    }

    private void initData()
    {
        SharedPreferences pref = mContext.getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE);
        final String KEY_INIT = "inited";
        if (!pref.getBoolean(KEY_INIT, false))
        {
            ContentValues values = new ContentValues();
            values.put(NotesDatabaseHelper.FOLDER_NAME, getString(R.string.folder_default));
            getContentResolver().insert(NotesProvider.CONTENT_URI_FOLDERS, values);
            pref.edit().putBoolean(KEY_INIT, true).commit();
        }
    }

    private void updateView()
    {
        Cursor foldersCursor = getContentResolver().query(NotesProvider.CONTENT_URI_FOLDERS, null, null,
                null, null);
        mFoldersAdapter.updateData(foldersCursor, mFolderID);
        Cursor notesCursor = getContentResolver().query(NotesProvider.CONTENT_URI_NOTES, null,
                NotesDatabaseHelper.FOLDER_ID + " = '" + mFolderID + "'", null,
                NotesDatabaseHelper.MODIFY_TIME + " DESC");
        mNotesAdapter.updateData(notesCursor);
        foldersCursor.moveToPosition(mCurrentFolder);
        String title = foldersCursor.getString(foldersCursor
                .getColumnIndex(NotesDatabaseHelper.FOLDER_NAME))
                + "(" + notesCursor.getCount() + ")";
        mTextViewTitle.setText(title);
    }

    class ExportThread extends Thread
    {
        public void run()
        {
            try
            {
                String sd = Environment.getExternalStorageDirectory().getPath();
                File root = new File(sd + File.separator + SD_FOLDER_NAME);
                if (!root.exists())
                {
                    root.mkdir();
                }
                Cursor note = mContext.getContentResolver().query(NotesProvider.CONTENT_URI_NOTES,
                        null, null, null, null);
                for (int i = 0; i < note.getCount(); i++)
                {
                    note.moveToPosition(i);
                    int nid = note.getInt(note.getColumnIndex("_id"));
                    long time = note.getLong(note.getColumnIndex(NotesDatabaseHelper.MODIFY_TIME));
                    Calendar c = Calendar.getInstance();
                    c.setTimeInMillis(time);
                    String filename = ""
                            + c.get(Calendar.YEAR)
                            +
                            ((c.get(Calendar.MONTH) + 1) < 10 ? ("0" + (c.get(Calendar.MONTH) + 1))
                                    : (c.get(Calendar.MONTH) + 1))
                            +
                            (c.get(Calendar.DAY_OF_MONTH) < 10 ? ("0" + c
                                    .get(Calendar.DAY_OF_MONTH)) : c.get(Calendar.DAY_OF_MONTH))
                            +
                            "_"
                            +
                            (c.get(Calendar.HOUR_OF_DAY) < 10 ? ("0" + c.get(Calendar.HOUR_OF_DAY))
                                    : c.get(Calendar.HOUR_OF_DAY))
                            +
                            (c.get(Calendar.MINUTE) < 10 ? ("0" + c.get(Calendar.MINUTE)) : c
                                    .get(Calendar.MINUTE))
                            + "_" + nid + ".txt";
                    File no = new File(root.getPath() + File.separator + filename);
                    if (no.exists())
                    {
                        no.delete();
                    }
                    no.createNewFile();
                    FileWriter fw = new FileWriter(no);
                    BufferedWriter bw = new BufferedWriter(fw);
                    String content = note
                            .getString(note.getColumnIndex(NotesDatabaseHelper.DETAIL));
                    bw.write(content);
                    bw.newLine();
                    bw.write(String.valueOf(time));
                    bw.close();
                    fw.close();
                }
                
                note.close();
                Message msg = new Message();
                msg.what = EXPORT_SUCCEED;
                mHandler.sendMessage(msg);
            } catch (Exception e)
            {
                e.printStackTrace();
                Message msg = new Message();
                msg.what = EXPORT_FAILED;
                mHandler.sendMessage(msg);
            }
        }
    }

    class ImportThread extends Thread
    {
        public void run()
        {
            try
            {
                String sd = Environment.getExternalStorageDirectory().getPath();
                File root = new File(sd + File.separator + SD_FOLDER_NAME);
                if (!root.exists())
                {
                    root.mkdir();
                    Message msg = new Message();
                    msg.what = IMPORT_FAILED;
                    mHandler.sendMessage(msg);
                    return;
                }
                else
                {
                    File old[] = root.listFiles();
                    if (0 >= old.length) {
                        Message msg = new Message();
                        msg.what = IMPORT_FAILED;
                        mHandler.sendMessage(msg);
                        return;
                    }
                    ArrayList<ContentValues> list = new ArrayList<ContentValues>();
                    for (int i = 0; i < old.length; i++)
                    {
                        File file = old[i];
                        if (null != file && 0 < file.length()) {
                            String detail = "";
                            BufferedReader input = new BufferedReader(new FileReader(file));
                            StringBuffer buffer = new StringBuffer();
                            String text;
                            while ((text = input.readLine()) != null)
                                buffer.append(text);
                            detail = buffer.toString();
                            input.close();
                            if (null != detail && 0 < detail.trim().length()) {
                                Cursor note = mContext.getContentResolver().query(
                                        NotesProvider.CONTENT_URI_NOTES,
                                        null, NotesDatabaseHelper.DETAIL + "='" + detail + "'",
                                        null, null);
                                // not repeat with existed, insert it to database
                                if (null == note || 0 >= note.getCount()) {
                                    Calendar c = Calendar.getInstance();
                                    String name = file.getName();

                                    String year = name.substring(0, 4);
                                    c.set(Calendar.YEAR, Integer.valueOf(year));

                                    String month = name.substring(4, 6);
                                    c.set(Calendar.MONTH, Integer.valueOf(month) - 1);

                                    String day = name.substring(6, 8);
                                    c.set(Calendar.DAY_OF_MONTH, Integer.valueOf(day));

                                    String hour = name.substring(9, 11);
                                    c.set(Calendar.HOUR_OF_DAY, Integer.valueOf(hour));

                                    String minite = name.substring(11, 13);
                                    c.set(Calendar.MINUTE, Integer.valueOf(minite));

                                    long time = c.getTimeInMillis();Log.e("JAYCE", "time:" + time);
                                    ContentValues value = new ContentValues();
                                    value.put(NotesDatabaseHelper.FOLDER_ID, 1);
                                    value.put(NotesDatabaseHelper.MODIFY_TIME, time);
                                    value.put(NotesDatabaseHelper.DETAIL, detail);
                                    list.add(value);
                                } else {
                                    file.delete();
                                }
                                if (null != note) {
                                    note.close();
                                }
                            }
                        }
                    }
                    ContentResolver resolver = getContentResolver();
                    ContentValues[] values = new ContentValues[list.size()];
                    list.toArray(values);
                    resolver.bulkInsert(NotesProvider.CONTENT_URI_NOTES, values);
                    Message msg = new Message();
                    msg.what = IMPORT_SUCCEED;
                    mHandler.sendMessage(msg);
                }
            } catch (Exception e)
            {
                e.printStackTrace();
                Message msg = new Message();
                msg.what = IMPORT_FAILED;
                mHandler.sendMessage(msg);
            }
        }
    }

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                case EXPORT_FAILED:
                {
                    if (mProgress != null)
                    {
                        mProgress.dismiss();
                        mProgress = null;
                    }
                    Toast.makeText(mContext, getString(R.string.export_failed), Toast.LENGTH_LONG)
                            .show();
                }
                    break;
                case EXPORT_SUCCEED:
                {
                    if (mProgress != null)
                    {
                        mProgress.dismiss();
                        mProgress = null;
                    }
                    removeDialog(DIALOG_CONFIRM_EXPORT_ID);
                    showDialog(DIALOG_CONFIRM_EXPORT_ID);
                }
                    break;
                case IMPORT_FAILED:
                {
                    Toast.makeText(mContext, R.string.no_files_import, Toast.LENGTH_LONG).show();
                    if (mProgress != null)
                    {
                        mProgress.dismiss();
                        mProgress = null;
                    }
                }
                break;
                case IMPORT_SUCCEED:
                {
                    Cursor c = getContentResolver().query(NotesProvider.CONTENT_URI_NOTES,
                            null,
                            NotesDatabaseHelper.FOLDER_ID + " = '" + 1 + "'", null,
                            NotesDatabaseHelper.MODIFY_TIME + " DESC");
                    mNotesAdapter.updateData(c);
                    if (mProgress != null)
                    {
                        mProgress.dismiss();
                        mProgress = null;
                    }
                }
            }
        }
    };

    private BroadcastReceiver mSDReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context arg0, Intent intent) {
            // TODO Auto-generated method stub
            if (intent.getAction().equals("android.intent.action.MEDIA_MOUNTED"))
            {
                mIsSDOn = true;
            }
            else if (intent.getAction().equals("android.intent.action.MEDIA_REMOVED") ||
                    intent.getAction().equals("android.intent.action.MEDIA_UNMOUNTED") ||
                    intent.getAction().equals("android.intent.action.MEDIA_BAD_REMOVAL"))
            {
                mIsSDOn = false;
            }
        }
    };
}
