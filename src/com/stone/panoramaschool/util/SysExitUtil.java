package com.stone.panoramaschool.util;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;

public final class SysExitUtil {
	//����һ��public static��list������activity 
    public static List activityList = new ArrayList(); 
    
    
      //finish����list�е�activity 
    public static void exit(){    
        int siz=activityList.size();     
        for(int i=0;i<siz;i++){        
            if(activityList.get(i)!=null){            
                ((Activity) activityList.get(i)).finish();        
                }     
            } 
    }
}
