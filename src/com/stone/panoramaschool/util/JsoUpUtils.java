package com.stone.panoramaschool.util;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.stone.panoramaschool.entity.Spot;

/**
 *com.stone.panoramaschool.util
 *
 * @author stone
 *
 * 2015年4月14日/下午2:37:52
 */
public class JsoUpUtils {

	private static List<Spot>list;
	private static Elements blogList;
	private static Spot spot;
	
	public static List<Spot> getSpot(String d){
		list=new ArrayList<Spot>();
		Document doc = Jsoup.parse(d);
		//获取元素
		blogList = doc.getElementsByClass("tr33 t_one");
		for (Element blogItem : blogList) {
			spot=new Spot();
			spot.setSpotName(blogItem.getElementById("link4").html());
			spot.setSpotURL(blogItem.getElementById("link4").attr("href"));
			spot.setScore(""+5);
			//spot.setAutherName(blogItem.);
			
			
		}
		
		
		return list;
	}
	
}
