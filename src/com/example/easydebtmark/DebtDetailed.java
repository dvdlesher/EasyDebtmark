package com.example.easydebtmark;

import java.util.List;

import android.media.Image;

public class DebtDetailed {
	private Image image;
	private String date;
	private Float owed;
	private Float paid;
	private String details;
	
	public DebtDetailed(String in_date, float in_owed, float in_paid, String in_details){
		date = in_date;
		owed = in_owed;
		paid = in_paid;
		details = in_details;
	}
}
