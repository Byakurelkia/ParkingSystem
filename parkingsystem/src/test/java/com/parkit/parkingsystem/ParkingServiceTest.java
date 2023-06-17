package com.parkit.parkingsystem;

import com.parkit.parkingsystem.config.DataBaseConfig;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Date;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ParkingServiceTest {

    private static ParkingService parkingService;

    @Mock
    private static InputReaderUtil inputReaderUtil;
    @Mock
    private static ParkingSpotDAO parkingSpotDAO;
    @Mock
    private static TicketDAO ticketDAO;
    @Mock
    private static DataBaseConfig dataBaseConfig;
    

    @BeforeEach
    private void setUpPerTest() {
    	System.out.println();
    	System.out.print("Set Up Per TEST");

        try {
            lenient().when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");// si pas lenient donne erreur sur test get next parking number if available

            ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);
            Ticket ticket = new Ticket();
            ticket.setInTime(new Date(System.currentTimeMillis() - (60*60*1000)));
            ticket.setParkingSpot(parkingSpot);
            ticket.setVehicleRegNumber("ABCDEF");
            lenient().when(ticketDAO.getTicket(anyString())).thenReturn(ticket); // si pas lenient() lignes 71 72 donne erreur mockito 
            lenient().when(ticketDAO.updateTicket(any(Ticket.class))).thenReturn(true);

            lenient().when(parkingSpotDAO.updateParking(any(ParkingSpot.class))).thenReturn(true);

            parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        } catch (Exception e) {
            e.printStackTrace();
            throw  new RuntimeException("Failed to set up test mock objects");
        }
    }

    @Test
    public void processExitingVehicleTest() throws Exception{
    	System.out.println();
    	System.out.println("processExitingVehicleTest TEST ");
        parkingService.processExitingVehicle();
        verify(parkingSpotDAO, Mockito.times(1)).updateParking(any(ParkingSpot.class));
        verify(ticketDAO).getNbTicket("ABCDEF");
    }
    
    @Test
    public void testProcessIncomingVehicle() throws Exception {
    	System.out.println();
    	System.out.println("testProcessIncomingVehicle TEST ");
    	when(inputReaderUtil.readSelection()).thenReturn(1);
        when(ticketDAO.getNbTicket("ABCDEF")).thenReturn(3); // le message d'acceuil pour les users réguliers est ok 
        when(parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR)).thenReturn(9); // si je ne mets pas cette ligne test ok mais donne erreur sur le DB

    	parkingService.processIncomingVehicle();

    }
    
    @Test
    public void processExitingVehicleTestUnableUpdate() { // marche - ne rentre pas au bloc if lorsque updateTicket renvoi false
    	System.out.println();
    	System.out.println("processExitingVehicleTestUnableUpdate TEST ");
    	when(ticketDAO.updateTicket(any(Ticket.class))).thenReturn(false);
    	
    	parkingService.processExitingVehicle();
    }
    
    @Test
    public void testGetNextParkingNumberIfAvailable() {
    	System.out.println();
    	System.out.println("testGetNextParkingNumberIfAvailable TEST ");
    	when(inputReaderUtil.readSelection()).thenReturn(1);
    	when(parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR)).thenReturn(1);
    	
    	int id = parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR);
    	boolean available = parkingService.getNextParkingNumberIfAvailable().isAvailable();
    	
    	parkingService.getNextParkingNumberIfAvailable();
    	
    	assertThat(id).isEqualTo(1);
    	assertThat(available).isEqualTo(true);
    	 
    }
    
    @Test 
    public void testGetNextParkingNumberIfAvailableParkingNumberNotFound() {
    	System.out.println();
    	System.out.println("testGetNextParkingNumberIfAvailableParkingNumberNotFound TEST");
    	when(inputReaderUtil.readSelection()).thenReturn(1);
    	when(parkingSpotDAO.getNextAvailableSlot(any(ParkingType.class))).thenReturn(0);
    	Object nullOrNot = parkingService.getNextParkingNumberIfAvailable();
    	parkingService.getNextParkingNumberIfAvailable();
    	assertThat(nullOrNot).isEqualTo(null);
    }
    
    @Test
    public void testGetNextParkingNumberIfAvailableParkingNumberWrongArgument() {
    	System.out.println();
    	System.out.println("testGetNextParkingNumberIfAvailableParkingNumberWrongArgument TEST ");
    	when(inputReaderUtil.readSelection()).thenReturn(3);
    	Object nullOrNot = parkingService.getNextParkingNumberIfAvailable();
    	parkingService.getNextParkingNumberIfAvailable();
    	assertThat(nullOrNot).isEqualTo(null);

    }

}
