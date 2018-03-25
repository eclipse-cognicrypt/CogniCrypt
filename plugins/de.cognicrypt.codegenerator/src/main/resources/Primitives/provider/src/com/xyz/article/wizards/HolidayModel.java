/*
 * Licensed Material - Property of IBM 
 * (C) Copyright IBM Corp. 2002 - All Rights Reserved. 
 */
package com.xyz.article.wizards;

/**
 * Model class containing the data for the holiday wizard
 */
public class HolidayModel 
{
	public static final String copyright = "(c) Copyright IBM Corporation 2002.";
		
	// date of departure as a String day, month, year
	protected String departureDate;
	
	// date of return as a String day, month, year
	protected String returnDate;

	// holiday destination
	protected String destination;
	
	// holiday departure point
	protected String departure;
	
	// flag indicating whether the user will use the plane
	protected boolean usePlane;
	
	// true if the flisgts need to be reset
	protected boolean resetFlights;

	// flight details for the holiday by plane
	protected String selectedFlight;
	
	// flight price 
	float price;
	
	// seat choice  for the holiday be plane
	protected String seatChoice;
	
	// rental company for the holiday by car
	protected String rentalCompany;
	
	// rental car price
	protected String carPrice;
	
	// flag indicating whether the user buys insurance when traveling by car
	protected boolean buyInsurance;

	// flag is set if a folder called Discounts is selected 
	// when the wizard is started; a discount is offered in this case
	boolean discounted = false;	

	public String toString()
	{
		String s;
		s= "Your holiday: \n";
		if (usePlane) s= s+"Flying from ";
		else s = s+"Driving from ";
		s= s +departure+ " to "+  destination+
			"\nleaving on "+departureDate +" returning on "+returnDate;
		if (usePlane)
			s = s + "\nflight: " + selectedFlight + "\nseat: "+seatChoice+
				"\nprice: "+price;
		else {
			s = s+ "\nrental company " + rentalCompany +
				 "\nprice "+ carPrice;
			if (buyInsurance) s = s+ "\nbuy insurance from the rental company";
			else  s = s +"\ndo not buy insurance from the rental company";
		}
		return s;	
	}
}
