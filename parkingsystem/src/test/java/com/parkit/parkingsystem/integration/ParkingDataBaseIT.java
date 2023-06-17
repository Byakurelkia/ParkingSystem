package com.parkit.parkingsystem.integration;

import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Date;


@ExtendWith(MockitoExtension.class)
public class ParkingDataBaseIT {

    private static DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();
    private static ParkingSpotDAO parkingSpotDAO;
    private static TicketDAO ticketDAO;
    private static DataBasePrepareService dataBasePrepareService;

    @Mock
    private static InputReaderUtil inputReaderUtil;

    @BeforeAll
    private static void setUp() throws Exception{
        parkingSpotDAO = new ParkingSpotDAO();
        parkingSpotDAO.dataBaseConfig = dataBaseTestConfig;
        ticketDAO = new TicketDAO();
        ticketDAO.dataBaseConfig = dataBaseTestConfig;
        dataBasePrepareService = new DataBasePrepareService();
    }

    @BeforeEach
    private void setUpPerTest() throws Exception {
        when(inputReaderUtil.readSelection()).thenReturn(1);
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
        dataBasePrepareService.clearDataBaseEntries();

    }

    @AfterAll
    private static void tearDown(){

    }


    @Test
    public void testParkingLotExit() throws Exception{

    	System.out.println("testParkingLotEXIT TEST START ");
    	//testParkingACar(); // ---> si le etst est lancé avec cette méthode, 
    	//je n'arrive pas à visualiser les données consistés à la base, ils sont enregistré puis le test étant relancé plus bas ça remet le tout à 0
        
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        parkingService.processIncomingVehicle();
        parkingService.processExitingVehicle();
        boolean value = ticketDAO.getAvailableTicket("ABCDEF");
        assertThat(value).isEqualTo(true);
        //TODO: check that the fare generated and out time are populated correctly in the database --> DO
        System.out.println("testParkingLotEXIT TEST FINISH ");
    }
    
    @Test
    public void testParkingACar() throws Exception{

        System.out.println("testParkingACar TEST START");

        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        parkingService.processIncomingVehicle();        
        Ticket ticket = ticketDAO.getTicket("ABCDEF");
        boolean value = ticket.getParkingSpot().isAvailable();
        assertThat(ticket.getId()).isEqualTo(1);
        assertThat(value).isEqualTo(false);
        
        //TODO: check that a ticket is actually saved in DB and Parking table is updated with availability-> DO
        
    }
    
    @Test
    public void  testParkingLotExitRecurringUser() throws Exception {
    	System.out.println("TEST PARKING LOT EXIT RECURRENT USER START "+ "\n");
    	Connection con = null;
    	try {
    		con = dataBaseTestConfig.getConnection();
        	Date inTime = new Date();
        	int month = inTime.getMonth() +1;
        	String sql = "INSERT INTO TICKET(ID,PARKING_NUMBER,VEHICLE_REG_NUMBER,PRICE,IN_TIME) values(10,3,'ABCDEF',0.0,'"+"2023"+
        			"-"+ month+ "-" + inTime.getDate() + " " + inTime.getHours()+ ":"+inTime.getMinutes()+":"+inTime.getSeconds()+"')";
        	System.out.println(sql);
        	PreparedStatement ps = con.prepareStatement(sql);
        	ps.executeUpdate();
        	con.close();
    	}catch(Exception e) {
    		System.out.println(e);
    	}
    	ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        parkingService.processIncomingVehicle(); //message bienvenue user recurrent OK
        parkingService.processExitingVehicle();// prix avec remis user recurrent OK
    	System.out.println("TEST PARKING LOT EXIT RECURRENT USER FINISH "+ "\n");
    }

    

}
