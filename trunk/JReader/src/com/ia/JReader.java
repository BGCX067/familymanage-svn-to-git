package com.ia;

import java.io.*;
import java.util.*;

import javax.microedition.midlet.*;
import javax.microedition.rms.RecordStore;
import javax.microedition.io.*;
import javax.microedition.io.file.*;
import javax.microedition.lcdui.*;

public class JReader extends MIDlet implements CommandListener
{
	private static final Command CMD_EXIT = new Command("Exit", Command.EXIT, 2);
	private static final Command CMD_BACK = new Command("Back", Command.BACK, 3);
	private static final String MEGA_ROOT = "/";
	private static final String UP_DIR = "..";
	private static final String SEP_STR = "/";
	private static final char SEP = '/';
	private String curDirName;
	
	private Display display;
	Displayable dis;
	private List mainlist;
	
	private Image dirIcon;
	private Image fileIcon;
	
	public static RecordStore record;
	
	public JReader()
	{
		curDirName = MEGA_ROOT;
		try
		{
			dirIcon = Image.createImage("/icons/dir.png");
		}
		catch(IOException e)
		{
			dirIcon = null;
		}
		try
		{
			fileIcon = Image.createImage("/icons/file.png");
		}
		catch(IOException e)
		{
			fileIcon = null;
		}
		
		display = Display.getDisplay(this);
		
		String[] options = {"FileBrowser", "LastRead"};
		mainlist = new List("Choose type", Choice.IMPLICIT, options, null);
		mainlist.addCommand(CMD_EXIT);
		mainlist.setCommandListener(this);
		dis = mainlist;
	}
	
	public void destroyApp(boolean arg0) {
		// TODO Auto-generated method stub
		HistoryRecord.closeRecord(record);
		HistoryRecord.deleteRecordStore();
		notifyDestroyed();
	}

	public void pauseApp() {
		// TODO Auto-generated method stub
		
	}

	public void startApp() {
		// TODO Auto-generated method stub
		display.setCurrent(dis);
	}

	public void commandAction(Command arg0, Displayable arg1) {
		// TODO Auto-generated method stub
		if(arg1.equals(mainlist))		{
			if(arg0 == List.SELECT_COMMAND)
			{
				if(arg1.equals(mainlist))
				{
					switch(((List)arg1).getSelectedIndex())
					{
					case 0:
						{
							try
							{
								showCurDir();
							}
							catch (SecurityException e) 
							{
					            Alert alert =
					                new Alert("Error", "You are not authorized to access the restricted API", null,
					                    AlertType.ERROR);
					            alert.setTimeout(Alert.FOREVER);
	
					            Form form = new Form("Cannot access FileConnection");
					            form.append(new StringItem(null,
					                    "You cannot run this MIDlet with the current permissions. " +
					                    "Sign the MIDlet suite, or run it in a different security domain"));
					            form.addCommand(CMD_EXIT);
					            form.setCommandListener(this);
					            dis = form;
					            display.setCurrent(alert, dis);
							}
						}
						break;
					case 1:
						{
							
						}
						break;
					}
				}
			}
		}
		else
		{
			if(arg0 == List.SELECT_COMMAND)
			{
				List cur = (List)arg1;
				final String curFile = cur.getString(cur.getSelectedIndex());
				new Thread(new Runnable()
				{
					public void run() {
						// TODO Auto-generated method stub
						if(curFile.endsWith(SEP_STR) || curFile.equals(UP_DIR))
						{
							traverseDir(curFile);
						}
						else
						{
							String filePath = "file://localhost/" + curDirName + curFile;
							showFile(filePath);
						}
					}}		
				).start();
			}
			if(arg0 == CMD_BACK)
			{
				showCurDir();
			}
		}
		if(arg0 == CMD_EXIT)
		{
			destroyApp(false);
		}
	}
	
	void showCurDir()
	{
		Enumeration e;
		FileConnection curDir = null;
		List browser;
		try
		{
			if(MEGA_ROOT.equals(curDirName))
			{
				e = FileSystemRegistry.listRoots();
				browser = new List(curDirName, List.IMPLICIT);
			}
			else
			{
				curDir = (FileConnection)Connector.open("file://localhost/" + curDirName);
				e = curDir.list();
				browser = new List(curDirName, List.IMPLICIT);
				browser.append(UP_DIR, dirIcon);
			}
			while(e.hasMoreElements())
			{
				String fileName = (String)e.nextElement();
				if(fileName.charAt(fileName.length() - 1) == SEP)
				{
					browser.append(fileName, dirIcon);
				}
				else if(fileName.length()> 4 && (fileName.substring(fileName.length() - 4, fileName.length()).equalsIgnoreCase(".txt")))
				{
					browser.append(fileName, fileIcon);
				}
			}
			browser.addCommand(CMD_EXIT);
			browser.setCommandListener(this);
			if(curDir != null)
			{
				curDir.close();
			}
			dis = browser;
			display.setCurrent(dis);
		}
		catch(IOException ioe)
		{
			ioe.printStackTrace();
		}
	}
	
	void traverseDir(String fileName)
	{System.out.println("curDirName1:" + curDirName);
		if(curDirName.equals(MEGA_ROOT))
		{
			if(fileName.equals(UP_DIR))
			{
				return;
			}
			curDirName = fileName;
		}
		else if(fileName.equals(UP_DIR))
		{
			int i = curDirName.lastIndexOf(SEP, curDirName.length() - 2);
			if(-1 != i)
			{
				curDirName = curDirName.substring(0, i + 1);
			}
			else
			{
				curDirName = MEGA_ROOT;
			}
		}
		else
		{
			curDirName = curDirName + fileName;
		}System.out.println("curDirName2:" + curDirName);
		showCurDir();
	}
	
	void showFile(String filePath)
	{
		ReaderCanvas rc = new ReaderCanvas(filePath, 0);
		rc.addCommand(CMD_BACK);
		rc.setCommandListener(this);
		dis = rc;
		display.setCurrent(dis);
	}
}