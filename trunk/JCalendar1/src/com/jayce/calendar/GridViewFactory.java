package com.jayce.calendar;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ViewSwitcher.ViewFactory;

public class GridViewFactory implements ViewFactory{ 
        LayoutInflater mInflater; 
        public GridViewFactory(Context context) { 
            mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE); 
        } 
         
        public View makeView() { 
           return mInflater.inflate(R.layout.grid_view, null); 
       } 
   } 