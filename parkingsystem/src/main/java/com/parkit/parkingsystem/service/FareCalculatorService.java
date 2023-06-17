package com.parkit.parkingsystem.service;

import java.text.DecimalFormat;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.model.Ticket;

public class FareCalculatorService {

    public void calculateFare(Ticket ticket, boolean discount){

    	
    	
        if( (ticket.getOutTime() == null) || (ticket.getOutTime().before(ticket.getInTime())) ){
            throw new IllegalArgumentException("Out time provided is incorrect:"+ticket.getOutTime().toString());
        }

        double inHour = ticket.getInTime().getTime();
        double outHour = ticket.getOutTime().getTime();
        System.out.println("inhour ve outhour " + ticket.getInTime() + " ve " + ticket.getOutTime());
        // on arrondi le nombre d'heure à deux décimales après la virgule pour des résultats correctes
        double duration = Math.round(((outHour - inHour)/1000/60/60)*100.0)/100.0;
        System.out.println("duration switch " + duration);
        System.out.println("duratiionnnn " + duration);
        if (isLessThan30Min(duration)) { // control if duration is superior at 30min
        	ticket.setPrice(0.0);
        }
        else {
        	switch (ticket.getParkingSpot().getParkingType()){
            case CAR: {
            	if(discount) {
                    ticket.setPrice((duration * Fare.CAR_RATE_PER_HOUR)*95/100);
            	}else {
                ticket.setPrice(duration * Fare.CAR_RATE_PER_HOUR);
            	}
                System.out.println("PRICE TICKET " + ticket.getPrice());
                break;
            }
            case BIKE: {
            	if(discount) {
                    ticket.setPrice((duration * Fare.BIKE_RATE_PER_HOUR)*95/100);
            	}else {
                ticket.setPrice(duration * Fare.BIKE_RATE_PER_HOUR);
            	}
                System.out.println("test firts ilmplelmented? ");
                break;
            }
            default: throw new IllegalArgumentException("Unkown Parking Type");
        }
        }
    }
    
    public void calculateFare(Ticket ticket) {
    	calculateFare(ticket, false);
    }
    
    public boolean isLessThan30Min(double duration) {
    	if (duration < 0.5)
    		return true;
    	else
    		return false;
    	
    }
}