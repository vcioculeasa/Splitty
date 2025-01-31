package server.api;

import commons.Expense;
import commons.Participant;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;


@RestController
@RequestMapping("/api/events/{id}/expenses")
public class ExpenseController {
    private final ExpenseService expenseService;

    private final GerneralServerUtil serverUtil;

    private final SimpMessagingTemplate messagingTemplate;
    /**
     * Constructor for the ExpenseController
     * @param expenseService the associated service for the expense class
     */
    public ExpenseController(ExpenseService expenseService,
                             @Qualifier("serverUtilImpl") GerneralServerUtil serverUtil,
                             SimpMessagingTemplate messagingTemplate) {
        this.expenseService = expenseService;
        this.serverUtil = serverUtil;
        this.messagingTemplate = messagingTemplate;
    }

    /**
     * @param id the id of the event to list all expenses from
     * @return the list of all expenses within an event
     */
    @GetMapping(path = { "" })
    public ResponseEntity<List<Expense>> getAllExpenses(@PathVariable("id") long id){
        return expenseService.getAllExpenses(id);
    }
    /**
     * @param id the id of the event to list all expenses from
     * @return the list of all expenses within an event
     */
    @GetMapping(path = { "/{expenseID}" })
    public ResponseEntity<Expense> getExpense(@PathVariable("id") long id,
                                              @PathVariable("expenseID") long expenseID){
        return expenseService.getExpense(id, expenseID);
    }

    /**
     * @param id the id of the event we want to add the expense to
     * @param expense the expense to be added to the event
     * @return whether the expense could be added
     */
    @PostMapping(path = {""})
    public ResponseEntity<Expense> add(@PathVariable("id") long id,
                                       @RequestBody Expense expense) {
        var resp = expenseService.add(id, expense, serverUtil);
        if (resp.getStatusCode().equals(HttpStatusCode.valueOf(200))) {
            messagingTemplate.convertAndSend("/topic/events/" + id + "/expenses",
                    Objects.requireNonNull(resp.getBody()));
        }
        return resp;
    }

    /**
     * @param expense the new contents of the expense
     * @param expenseId the id of the expense to be changed
     * @param id the id of the event which the expense is associated with
     * @return whether the title could be changed
     */
    @PutMapping(path = {"/{expenseId}"})
    public ResponseEntity<Void> changeExpense(@RequestBody Expense expense,
                                            @PathVariable("expenseId") long expenseId,
                                            @PathVariable("id") long id){
        var resp = expenseService.changeTitle(expense, expenseId, id,serverUtil);
        if (resp.getStatusCode().equals(HttpStatusCode.valueOf(200))) {
            String dest = "/topic/events/" + id + "/expenses/" + expenseId;
            System.out.println(dest);
            messagingTemplate.convertAndSend(dest, expense);
        }
        return resp;
    }

    /**
     * @param amount the new amount of the expense
     * @param expenseId the id of the expense to be changed
     * @param id the id of the event
     * @return whether the amount could be updated
     */
    @PutMapping(path = {"/{expenseId}/amount"})
    public ResponseEntity<Void> changeAmount(@RequestBody double amount,
                                            @PathVariable("expenseId") long expenseId,
                                             @PathVariable("id") long id){
        return expenseService.changeAmount(amount, expenseId, id,serverUtil);
    }

    /**
     * @param payee the new payee of the expense
     * @param expenseId the id of the expense
     * @param id the id of the event
     * @return whether the payee could be updated
     */
    @PutMapping(path = {"/{expenseId}/payee"})
    public ResponseEntity<Void> changePayee(@RequestBody Participant payee,
                                            @PathVariable("expenseId") long expenseId,
                                            @PathVariable("id") long id){
        return expenseService.changePayee(payee, expenseId, id,serverUtil);
    }

    /**
     * @param title the new title of the expense
     * @param expenseId the id of the expense
     * @param id the id of the event
     * @return whether the payee could be updated
     */
    @PutMapping(path = {"/{expenseId}/title"})
    public ResponseEntity<Void> changeTitle(@RequestBody String title,
                                            @PathVariable("expenseId") long expenseId,
                                            @PathVariable("id") long id){
        return expenseService.changeTitle(title, expenseId, id,serverUtil);
    }

    /**
     * @param expenseId the id of the expense to be deleted
     * @param id the id of the event
     * @return whether the expense was deleted
     */
    @DeleteMapping(path = {"/{expenseId}"})
    public ResponseEntity<Expense> deleteExpense(@PathVariable("expenseId") long expenseId,
                                              @PathVariable("id") long id){
        var resp = expenseService.deleteExpense(expenseId, id,serverUtil);
        if (resp.getStatusCode().equals(HttpStatusCode.valueOf(200))) {
            String dest = "/topic/events/" + id + "/expenses/" + expenseId;
            System.out.println(dest);
            Expense del = new Expense();
            del.setDescription("deleted");
            messagingTemplate.convertAndSend(dest, del);
        }
        return resp;
    }
}
