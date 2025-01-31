package server.api;

import commons.Event;
import commons.Expense;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.List;
import java.util.Objects;


@RestController
@RequestMapping("/api/events")

public class EventController {

    private final EventService eventService;

    private final GerneralServerUtil serverUtil;

    private final SimpMessagingTemplate messagingTemplate;


    /**
     * constructor for the EventController
     * @param eventService the service with all the necessary functions for the api
     */
    public EventController(EventService eventService,
                           @Qualifier("serverUtilImpl") GerneralServerUtil serverUtil,
                           SimpMessagingTemplate messagingTemplate) {
        this.eventService = eventService;
        this.serverUtil = serverUtil;
        this.messagingTemplate = messagingTemplate;
    }

    /**
     * Get method to get a specific event from the database
     * @param inviteCode the invite code of that specific event
     * @return the requested event
     */
    @GetMapping(path = { "/{inviteCode}/updates" })
    public DeferredResult<ResponseEntity<Event>> getPolling(
            @PathVariable("inviteCode") long inviteCode) {

        return eventService.getPolling(inviteCode);
    }

    /**
     * Websocket implemmentation of the add
     * @param event new values of event
     */
    @MessageMapping("/events")
    @SendTo("/topic/events")
    public Event changeEvent(Event event) {
        change(event.getInviteCode(),event).getBody();
        return event;
    }

    /***
     * @param id the event of which we want to sum the total of expenses
     * @return the sum of all expenses
     */
    @GetMapping(path = { "/{id}/total" })
    public ResponseEntity<Double> getTotal(@PathVariable("id") long id) {
        return eventService.getTotal(id);
    }


    /**
     * Get method to get a specific event from the database
     * @param inviteCode the invite code of that specific event
     * @return the requested event
     */
    @GetMapping(path = { "/{inviteCode}" })
    public ResponseEntity<Event> get(@PathVariable("inviteCode") long inviteCode) {
        return eventService.getEvent(inviteCode);
    }



    /**
     * @param inviteCode the invite code of the event to check in
     * @param payeeId the id of the participant to check if they're
     *                the payee in the expenses
     * @return the list of expenses the participant was the payee in
     */
    @GetMapping(path = {"/{inviteCode}/payee/{payeeId}"})
    public ResponseEntity<List<Expense>> getInvolvingPayee(@PathVariable("inviteCode")
                                                               long inviteCode,
                                             @PathVariable("payeeId") long payeeId){
        return eventService.getExpensesInvolvingPayee(inviteCode, payeeId);
    }

    /**
     * @param inviteCode the invite code of the event to check in
     * @param partId the id of the participant to check if they're
     *               involved in the expenses
     * @return the list of expenses the participant was involved in
     */
    @GetMapping(path = {"/{inviteCode}/participant/{partId}"})
    public ResponseEntity<List<Expense>> getInvolvingPart(@PathVariable("inviteCode")
                                                              long inviteCode,
                                             @PathVariable("partId") long partId){
        return eventService.getExpensesInvolvingParticipant(inviteCode, partId);
    }

    /**
     * A post method to add and event to the repository
     * @param event an event in the requestBody to add to the repository
     * @return the event if succesfully made
     */
    @PostMapping(path = { "", "/" })
    public ResponseEntity<Event> add(@RequestBody Event event) {
        return eventService.addEvent(event);
    }

    /**
     * Change an existing event
     * @param inviteCode the invite code of the event to change
     * @param event the data that the event should have
     * @return the new changed event
     */
    @PutMapping(path = {"/{inviteCode}" })
    public ResponseEntity<Event> change(@PathVariable("inviteCode") long inviteCode,
                                        @RequestBody Event event) {
        var resp = eventService.changeEvent(inviteCode,event,serverUtil);
        if (resp.getStatusCode().equals(HttpStatusCode.valueOf(200))) {
            messagingTemplate.convertAndSend("/topic/events/" + inviteCode,
                    Objects.requireNonNull(resp.getBody()));
        }
        return resp;
    }

    /**
     * Delete an existing event
     * @param inviteCode the invitecode of the event
     * @return the event that was deleted
     */
    @DeleteMapping(path = {"/{inviteCode}" })
    public ResponseEntity<Event> delete(@PathVariable("inviteCode") long inviteCode) {
        return eventService.deleteEvent(inviteCode);
    }


    /**
     * Endpoint to access and calculated the share for a certain participant
     * @param eventId id of the event the participant is in
     * @param participantId id of the participant
     * @return amount that is owed if calculable
     */
    @GetMapping(path = {"/{invitecode}/share/{participantId}"})
    public ResponseEntity<Double> getShare(@PathVariable("invitecode") Long eventId,
                                           @PathVariable("participantId") Long participantId){
        return eventService.getShare(eventId, participantId);
    }

    /**
     * @param eventId the event to get the debt from
     * @param participantId the participant to calculate the debt of
     * @return the response entity containing the debt as a double
     */
    @GetMapping(path = {"/{invitecode}/debt/{participantId}"})
    public ResponseEntity<Double> getDebt(@PathVariable("invitecode") Long eventId,
                                           @PathVariable("participantId") Long participantId){
        return eventService.getDebt(eventId, participantId);
    }

    /**
     * @param eventId the event to calculate how much the part. is owed
     * @param participantId the participant to calculate the owed of
     * @return the response entity containing how much the part. is owed
     */
    @GetMapping(path = {"/{invitecode}/owed/{participantId}"})
    public ResponseEntity<Double> getOwed(@PathVariable("invitecode") Long eventId,
                                          @PathVariable("participantId") Long participantId){
        return eventService.getOwed(eventId, participantId);
    }

}
