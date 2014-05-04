package com.example.easydebtmark;

import java.util.List;

public class DebtPerson {
	private String name;
	private float totalOwed;
	private float totalPaid;
	private List<DebtDetailed> debt;
	
	public String getName() {
		return name;
	}

	public float getTotalOwed() {
		return totalOwed;
	}

	public float getTotalPaid() {
		return totalPaid;
	}

	public List<DebtDetailed> getDebt() {
		return debt;
	}

	public DebtPerson(String name, float totalOwed, float totalPaid){
		this.name = name;
		this.totalOwed = totalOwed;
		this.totalPaid = totalPaid;
	}
}
