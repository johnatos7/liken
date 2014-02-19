package com.liken.constant;

import android.graphics.Color;

import com.liken.R;

public class Constants {

	public String getTopicName(int id) {
		if (id == 0) {

			return "Design";
		} else if (id == 1) {

			return "Culture";
		} else if (id == 2) {

			return "Movies";
		} else if (id == 3) {

			return "Music";
		} else if (id == 4) {

			return "Tech";
		} else if (id == 5) {

			return "Random";
		} else if (id == 6) {

			return "Philosophy";
		} else if (id == 7) {

			return "Sports";
		} else if (id == 8) {

			return "Business";
		} else if (id == 9) {

			return "Games";
		} else if (id == 10) {

			return "Science";
		} else if (id == 11) {

			return "Literature";
		}

		return null;
	}

	public int getTopicColor(int id) {
		if (id == 0) {
			return Color.parseColor("#ff00a8");
			
		} else if (id == 1) {
			return Color.parseColor("#ffcb16");
		
		} else if (id == 2) {
			return Color.parseColor("#000000");
			
		} else if (id == 3) {
			return Color.parseColor("#00d8ff");
			
		} else if (id == 4) {
			return Color.parseColor("#989898");
			
		} else if (id == 5) {
			return Color.parseColor("#97dc35");
	
		} else if (id == 6) {
			return Color.parseColor("#8f00c4");
		
		} else if (id == 7) {
			return Color.parseColor("#da9030");
		
		} else if (id == 8) {
			return Color.parseColor("#0061f2");
			
		} else if (id == 9) {
			return Color.parseColor("#f20000");
			
		} else if (id == 10) {
			return Color.parseColor("#b2007e");
		
		} else if (id == 11) {
			return Color.parseColor("#d1bf8f");
		
		}

		return (Integer) null;

	}

}
