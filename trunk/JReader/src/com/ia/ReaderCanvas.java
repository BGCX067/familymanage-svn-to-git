package com.ia;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;
import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;

import com.sun.midp.io.j2me.storage.RandomAccessStream;

public class ReaderCanvas extends Canvas
{
	private static final int MAX_WIDTH = 220;
	private static final int PAGE_UP = 0;
	private static final int PAGE_DOWN = 1;
	
	private static final int MAX_PAGE = 3;
	private static final int PAGE_LINE = 15;
	private static final int LINE_HEIGHT = 20;
	private static final int LINE_START = 5;
	
	private String[] curPage;
	private String[] cachePage;
	private String[] downPage;
	
	private boolean isEnd;
	
	private int page;
	
	private FileConnection fc;
	InputStream is;
	private boolean isCacheOK;
	private boolean isKeyOK;
	
	private String filePath;
	private int filePosition;
	private int count;
	int tempCount;
	
	private final Font font = Font.getFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_MEDIUM);
	
	public ReaderCanvas(String path, int position)
	{
		page = 0;
		isEnd = false;
		filePath = path;
		filePosition = position;
		System.out.println(path);
		byte[] b = HistoryRecord.stringToByte(path);
		String s = HistoryRecord.byteToString(b);
		System.out.println(s);
		if(s.equalsIgnoreCase(path))
		{
			System.out.println("equals");
		}
		readFile(filePath);
		setFullScreenMode(true);
		curPage = downPage;
		if(!isEnd)
		{
			new CacheThread().start();
		}
		isKeyOK = true;
	}
	
	protected void paint(Graphics arg0) {
		// TODO Auto-generated method stub
		if(isEnd && 3 <= page)
		{
			return;
		}
		arg0.setFont(font);
		arg0.setColor(0xffffff); 
		arg0.fillRect(0, 0, getWidth(),getHeight());
		arg0.setColor(0); 
		for(int i = 0,j = 0; i < PAGE_LINE; i++, j += LINE_HEIGHT)
		{
			if(null == curPage[PAGE_LINE * page + i])
			{
				break;
			}
			arg0.drawString(curPage[PAGE_LINE * page + i], LINE_START, j, 0);
			
		}
	}
	
	protected void keyPressed(int keyCode)
	{
		if(!isKeyOK)
		{
			return;
		}
		else
		{
			isKeyOK = false;
			System.out.println("key pressed" + String.valueOf(keyCode));
			int keyAction = getGameAction(keyCode);
			new KeyThread(keyAction).start();
		}
	}
	
	class KeyThread extends Thread
	{
		private int mKeyAction;
		public KeyThread(int keyAction)
		{
			mKeyAction = keyAction;
		}
		
		public void run()
		{
			
				switch(mKeyAction)
				{
					case Canvas.RIGHT:
					{
						try 
						{System.out.println("page a:" + page + " isend:" + String.valueOf(isEnd));
							if(isEnd && 2 <= page)
							{
								page++;
								return;
							}
							if(2 == page)
							{
								while(!isCacheOK)
								{
									Thread.sleep(10);
								}
								if(curPage == downPage)
								{
									curPage = cachePage;
								}
								else
								{
									curPage = downPage;
								}
								page = 0;
								if(!isEnd)
								{
									new CacheThread().start();
								}
							}
							else
							{
								page += 1;
							}
						} catch (Exception e) {
							// TODO Auto-generated catch block
							System.out.println("exception");
						}System.out.println("page b:" + page);
						repaint();
					}
					break;
					case Canvas.LEFT:
					{
						if(0 < page && 2 >= page)
						{
							page--;
						}
						repaint();
					}
					break;
				}
				isKeyOK = true;
		}
	}
	
	int stringWidth(String str)
	{
		if(null != str && str.length() > 0)
		{
			return font.stringWidth(str);
		}
		return 0;
	}
	
	void readFile(String filePath)
	{
		try
		{
			fc = (FileConnection)Connector.open(filePath);
			if(!fc.exists())
			{
				throw new IOException("File does not exist !");
			}
			is = fc.openInputStream();
			is.skip(filePosition);
			downPage = pageTurn();
		}
		catch(Exception e)
		{
			
		}
	}
	
	synchronized String[] pageTurn() throws Exception
	{
		String[] tmp = new String[45];
		for(int i = 0; i < MAX_PAGE * PAGE_LINE && !isEnd; i++)
		{
			tmp[i] = readLine();
		}
		return tmp;
	}
	
	String readLine() throws Exception
	{
		int width = 0;
		int l = 0;
		byte[] temp = new byte[1];
		String line = "";
		int co = 0;
		ByteArrayOutputStream baos = new ByteArrayOutputStream(); 
		while(width < MAX_WIDTH)
		{
			 if((co = is.read(temp, 0, 1)) != -1)
			 {
				 count += co;
				if((temp[0]&0x80) == 0x0)
				{
					l = 0;
				}
				else if((temp[0]&0xE0) == 0xC0)
				{
					l = 1;
				}
				else if((temp[0]&0xF0) == 0xE0)
				{
					l = 2;
				}
				else if((temp[0]&0xF8) == 0xF0)
				{
					l = 3;
				}
				else if((temp[0]&0xFC) == 0xF8)
				{
					l = 4;
				}
				else if((temp[0]&0xFE) == 0xFC)
				{
					l = 5;
				}
				
				baos.write(temp);
				if(l > 0)
				{
					byte[] b = new byte[l];
					
					if((co = is.read(b, 0, l)) != -1)
					{
						count += co;
						baos.write(b);
					}
					else
					{
						width = MAX_WIDTH;
						isEnd = true;
					}
				}
				byte[] data = baos.toByteArray(); 
				
				line = new String(data, "UTF-8"); 
				width = stringWidth(line);
			}
			else
			{
				isEnd = true;
				break; 
			}
		}
		baos.close();
		return line;
	}
	
	void updateCurrent()
	{
		for(int i = 0; i < 45; i++)
		{
			curPage[i] = cachePage[i];
		}
	}
	
	class CacheThread extends Thread{

		public void run()
		{
			// TODO Auto-generated method stub
			isCacheOK = false;
			try {
				if(curPage == downPage)
				{
					cachePage = pageTurn();
				}
				else
				{
					downPage = pageTurn();
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			isCacheOK = true;
		}
	};
	
}
