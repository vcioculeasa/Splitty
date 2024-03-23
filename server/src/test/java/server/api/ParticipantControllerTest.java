package server.api;

import commons.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.http.HttpStatus.OK;
import static server.api.PasswordService.setPassword;


public class ParticipantControllerTest {

    public TestEventRepository eventRepository;
    public TestParticipantRepository participantRepository;
    public Participant valid;
    public Event baseEvent;
    public Participant invalid;
    public final Participant baseParticipant = new Participant("Chris Smith",
            "chrismsmith@gmail.com","NL85RABO5253446745",
            "HBUKGB4B");
    public List<Participant> participantList;
    public Date creationDate;
    public Date lastActivity;

    ParticipantController participantController;
    ParticipantService participantService;


    @BeforeEach
    public void init(){
        eventRepository = new TestEventRepository();
        participantRepository = new TestParticipantRepository();
        participantService = new ParticipantService(
                eventRepository, participantRepository);
        participantController = new ParticipantController(participantService);
        valid = new Participant("John Doe",
                "jdoe@gmail.com","NL85RABO5253446745",
                "HBUKGB4B");
        invalid = new Participant("Jane Doe",
                "janedoe.com","NL85RABO5253446745",
                "HBUKGB4B");

        participantList = new ArrayList<>();
        participantRepository.save(baseParticipant);
        participantRepository.save(valid);
        participantList.add(baseParticipant);
        participantList.add(valid);
        creationDate = new Date(124, 4, 20);
        lastActivity = new Date(124, 4, 25);
        Date date = new Date();
        Timestamp timestamp2 = new Timestamp(date.getTime());
        baseEvent = new Event("Mock Event",timestamp2,timestamp2);
        eventRepository.save(baseEvent);
        eventRepository.getById(0L).setParticipantsList(participantList);


    }

    @Test
    public void importParticipant(){
        Event event = new Event("Title4", null, null);
        Participant p = new Participant("j doe", "example@email.com","NL85RABO5253446745", "HBUKGB4B");
        Participant other = new Participant("John Doe",
                "jdoe@gmail.com","NL85RABO5253446745",
                "HBUKGB4B");
        ParticipantPayment pp = new ParticipantPayment(other, 25);
        List<ParticipantPayment> split = List.of(pp);
        Tag t = new Tag("red", "red");
        Expense e= new Expense(50, "USD", "exampleExpense", "description",
                null,split ,t, p);
        event.getParticipantsList().add(p);
        event.getParticipantsList().add(other);
        event.getExpensesList().add(e);
        Tag one = new Tag("food", "#93c47d");
        Tag two = new Tag("entrance fees", "#4a86e8");
        Tag three = new Tag("travel", "#e06666");
        event.setTagsList(List.of(t, one, two, three));
        setPassword("password");
        event.setInviteCode(5);
        assertEquals(OK, participantController.addPriorParticipant("password", p).getStatusCode());
    }

    @Test
    public void testGetController(){
        participantRepository.flush();
        eventRepository.flush();
        participantController.getParticipants(0);
        List<String> called = List.of("existsById", "findById","findById");
        assertEquals(participantRepository.calledMethods.size(), 0);
        assertEquals(eventRepository.calledMethods.size(), 3);
        assertEquals(eventRepository.calledMethods, called);
    }

    @Test
    public void getAllControllerTest(){
        participantRepository.flush();
        eventRepository.flush();
        participantController.getParticipant(0,0);
        List<String> called = List.of("existsById", "findById","findById","existsById", "findById","findById");
        assertEquals(eventRepository.calledMethods, called);
        assertEquals(participantRepository.calledMethods.size(), 0);
        assertEquals(eventRepository.calledMethods.size(), 6);
    }

    @Test
    public void addParticipantTest(){
        participantRepository.flush();
        eventRepository.flush();
        Participant three = new Participant("Ethan", "eyoung@gmail.com",
                "NL85RABO5253446745", "HBUKGB4B");
        participantController.addParticipant(0, three);
        List<String> called = List.of("existsById", "findById", "findById",
                "existsById", "findById", "findById", "existsById",
                "findById", "findById", "findById", "findById",
                "save", "existsById", "getById", "save", "existsById", "getById");
        assertEquals(eventRepository.calledMethods, called);
        assertEquals(participantRepository.calledMethods.size(), 1);
        assertEquals(eventRepository.calledMethods.size(), 17);
    }

    @Test
    public void updateParticipantTest(){
        participantRepository.flush();
        eventRepository.flush();
        Participant p = new Participant("Christina Smith", "cmsmith@yahoo.com",
                "NL85ABNA5253446745", "AMUKGB7B");
        participantController.updateParticipant(0, 0, p);
        List<String> called = List.of("existsById", "findById","findById",
                "existsById", "findById","findById",
                "existsById", "findById","findById",
                "existsById", "findById","findById",
                "existsById", "findById","findById",
                "existsById", "findById","findById",
                "findById", "save", "existsById",
                "getById");
        assertEquals(eventRepository.calledMethods, called);
        assertEquals(participantRepository.participants.get(0).getName(), "Christina Smith");
        assertEquals(participantRepository.participants.size(), 2);
        assertEquals(eventRepository.calledMethods.size(), 22);
    }

    @Test
    public void deleteParticipantsTest(){
        participantRepository.flush();
        eventRepository.flush();
        participantController.deleteParticipant(0,0);
        assertEquals(participantRepository.calledMethods.size(), 1);
        assertEquals(eventRepository.calledMethods.size(), 27);
        List<String> called = List.of("existsById", "findById", "findById",
                "existsById", "findById", "findById", "existsById",
                "findById", "findById", "existsById", "findById",
                "findById", "existsById", "findById", "findById",
                "existsById", "findById", "findById", "findById",
                "findById", "save", "existsById", "getById", "save",
                "existsById", "getById", "getReferenceById");
        assertEquals(eventRepository.calledMethods, called);

    }


    @Test
    public void lastActivityNotChangeTest(){
        Event event = eventRepository.getById(0L);
        Date tmpdate = event.getLastActivity();
        participantController.getParticipant(0L,0L);
        event = eventRepository.getById(0L);
        assertEquals(event.getLastActivity(),tmpdate);
    }
    @Test
    public void lastActivityNotChange2Test(){
        Event event = eventRepository.getById(0L);
        Date tmpdate = event.getLastActivity();
        participantController.getParticipant(0L,0L);
        event = eventRepository.getById(0L);
        assertEquals(event.getLastActivity(),tmpdate);
    }
    @Test
    public void lastActivityAfterDeleteTest() throws InterruptedException {
        Event event = eventRepository.getById(0L);
        Date tmpdate = (Date) event.getLastActivity().clone();
        Thread.sleep(500);
        participantController.deleteParticipant(0L,0L);
        event = eventRepository.getById(0L);
        assertTrue(event.getLastActivity().after(tmpdate));
    }

    @Test
    public void lastActivityAfterChangeTest() throws InterruptedException {
        Event event = eventRepository.getById(0L);
        Date tmpdate = (Date) event.getLastActivity().clone();
        Thread.sleep(500);
        participantController.updateParticipant(0L,0L,new Participant("John Doe",
                "jdoe@gmail.com","NL85RABO5253446745",
                "HBUKGB4B"));
        event = eventRepository.getById(0L);
        assertTrue(event.getLastActivity().after(tmpdate));
    }

    @Test
    public void lastActivityAddTest() throws InterruptedException {
        Event event = eventRepository.getById(0L);
        Date tmpdate = (Date) event.getLastActivity().clone();
        Thread.sleep(500);
        participantController.addParticipant(0L,new Participant("Jon Doe",
                "jdoe@gmail.com","NL85RABO5253446745",
                "HBUKGB4B"));
        event = eventRepository.getById(0L);
        assertTrue(event.getLastActivity().after(tmpdate));
    }

}
