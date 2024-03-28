package server.api;

import commons.Event;
import commons.Expense;
import commons.Participant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import server.database.EventRepository;
import server.database.ExpenseRepository;
import server.database.ParticipantPaymentRepository;


import java.util.Collections;

import java.util.List;
import java.util.Objects;

@Service
public class ExpenseService {
    private final EventRepository eventRepo;
    private final ExpenseRepository expenseRepo;
    private final ParticipantPaymentRepository ppRepo;

    /**
     * Constructor for the ExpenseService
     * @param eventRepo the repo of events
     * @param expenseRepo the repo of expenses
     */
    @Autowired
    public ExpenseService(EventRepository eventRepo,
                          ExpenseRepository expenseRepo,
                          ParticipantPaymentRepository ppRepo){
        this.eventRepo = eventRepo;
        this.expenseRepo = expenseRepo;
        this.ppRepo = ppRepo;
    }

    /**
     * Lists all expenses in a certain event
     * @param id the id of the event to list all expenses of
     * @return whether the expenses could be listed
     */
    public ResponseEntity<List<Expense>> getAllExpenses(long id) {
        if (id < 0) {
            return ResponseEntity.badRequest().build();
        }
        if (!eventRepo.existsById(id)){
            return ResponseEntity.notFound().build();
        }
        Event event = eventRepo.findById(id).get();
        List<Expense> expenses = event.getExpensesList();
        return ResponseEntity.ok(expenses);
    }

    /**
     * Sums the total of all expenses within an event
     * @param id the id of the event
     * @return whether the total could be returned
     */
    public ResponseEntity<Double> getTotal(long id) {
        if (id < 0){
            return ResponseEntity.badRequest().build();
        }
        if (!eventRepo.existsById(id)){
            return ResponseEntity.notFound().build();
        }
        Event event = eventRepo.findById(id).get();
        List<Expense> expenses = event.getExpensesList();
        double totalExpense = 0.0;
        for (Expense expense : expenses) {
            totalExpense += expense.getAmount();
        }
        return ResponseEntity.ok(totalExpense);
    }

    /**
     * Adds an expense to the event
     *
     * @param id         the id of the event to be added to
     * @param expense    the expense to be added
     * @param serverUtil
     * @return whether the expense could be added to the event
     */
    public ResponseEntity<Expense> add(long id, Expense expense, GerneralServerUtil serverUtil) {
        if (id < 0){
            return ResponseEntity.badRequest().build();
        }
        if (!eventRepo.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        if (expense == null || expense.getTitle() == null ||
                Objects.equals(expense.getTitle(), "") ||
                expense.getAmount() == 0 || expense.getPayee() == null) {
            return ResponseEntity.badRequest().build();
        }
        if (expense.getSplit() != null) {
            ppRepo.saveAll(expense.getSplit());
        }
        Event event = eventRepo.findById(id).get();
        List<Expense> expenseList = event.getExpensesList();
        expenseList.add(expense);
        event.setExpensesList(expenseList);
        expenseRepo.save(expense);
        serverUtil.updateDate(eventRepo,id);
        eventRepo.save(event);
        return ResponseEntity.ok(expense);
    }

    /**
     * @param title      the new title of the expense
     * @param expenseId  the id of the expense to be edited
     * @param id         the id of the event which contains the expense
     * @param serverUtil
     * @return whether the title could be changed
     */
    public ResponseEntity<Void> changeTitle(String title, long expenseId,
                                            long id, GerneralServerUtil serverUtil) {
        if (id < 0){
            return ResponseEntity.badRequest().build();
        }
        if (!eventRepo.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        if (expenseId < 0){
            return ResponseEntity.badRequest().build();
        }
        if (!expenseRepo.existsById(expenseId)) {
            return ResponseEntity.notFound().build();
        }
        if (title == null || Objects.equals(title, "")) {
            return ResponseEntity.badRequest().build();
        }
        Expense change = expenseRepo.findById(expenseId).get();
        change.setTitle(title);
        expenseRepo.save(change);
        Event event = eventRepo.findById(id).get();
        serverUtil.updateDate(eventRepo,id);
        eventRepo.save(event);
        return ResponseEntity.ok(null);
    }

    /**
     * @param amount     the new amount of the expense
     * @param expenseId  the id of the expense to be edited
     * @param id         the id of the event
     * @param serverUtil
     * @return whether the amount could be updated
     */
    public ResponseEntity<Void> changeAmount(double amount,
                                             long expenseId, long id,
                                             GerneralServerUtil serverUtil){
        if (id < 0) {
            return ResponseEntity.badRequest().build();
        }
        if (!eventRepo.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        if (expenseId < 0){
            return ResponseEntity.badRequest().build();
        }
        if (!expenseRepo.existsById(expenseId)) {
            return ResponseEntity.notFound().build();
        }
        if (amount <= 0.0) {
            return ResponseEntity.badRequest().build();
        }
        Expense change = expenseRepo.findById(expenseId).get();
        change.setAmount(amount);
        expenseRepo.save(change);
        serverUtil.updateDate(eventRepo,id);
        return ResponseEntity.ok(null);
    }

    /**
     * @param payee      the new payee of the expense
     * @param expenseId  the id of the expense to be edited
     * @param id         the id of the event
     * @param serverUtil
     * @return whether the payee could be updated
     */
    public ResponseEntity<Void> changePayee(Participant payee, long expenseId,
                                            long id, GerneralServerUtil serverUtil){
        if (id < 0){
            return ResponseEntity.badRequest().build();
        }
        if (!eventRepo.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        if (expenseId < 0){
            return ResponseEntity.badRequest().build();
        }
        if (!expenseRepo.existsById(expenseId)) {
            return ResponseEntity.notFound().build();
        }
        if (payee == null || Objects.equals(payee.getName(), "")
                || Objects.equals(payee.getName(), null)) {
            return ResponseEntity.badRequest().build();
        }
        Expense change = expenseRepo.findById(expenseId).get();
        change.setPayee(payee);
        expenseRepo.save(change);
        serverUtil.updateDate(eventRepo,id);
        return ResponseEntity.ok(null);
    }

    /**
     * @param expenseId  the id of the expense to be deleted
     * @param id         the id of the event
     * @param serverUtil
     * @return whether the expense was deleted
     */
    public ResponseEntity<Expense> deleteExpense(long expenseId, long id,
                                                 GerneralServerUtil serverUtil){
        if (id < 0){
            return ResponseEntity.badRequest().build();
        }
        if (!eventRepo.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        if (expenseId < 0){
            return ResponseEntity.badRequest().build();
        }
        if (!expenseRepo.existsById(expenseId)) {
            return ResponseEntity.notFound().build();
        }
        Event event = eventRepo.findById(id).get();
        Expense expense = expenseRepo.findById(expenseId).get();
        List<Expense> expenseList = event.getExpensesList();
        expenseList.remove(expense);
        event.setExpensesList(expenseList);
        serverUtil.updateDate(eventRepo,id);
        eventRepo.save(event);
        expenseRepo.deleteAllById(Collections.singleton(expenseId));
        return ResponseEntity.ok(expense);
    }


    /**
     * Method to check if the imported expense is valid
     * @param expense expense being imported
     * @return expense if it is valid or error code if not
     */
    public ResponseEntity<Expense> validateExpense(Expense expense) {
        if(expense == null || expense.getAmount()<0
                || expense.getTitle() == null
                || Objects.equals(expense.getTitle(), "")
                || expense.getPayee() == null){
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(expense);
    }

    /**
     * Method to add an expense to the repository from a JSON import
     * @param expense expense to be added to the expenseRepository
     * @return the expense in a ResponseEntity
     */
    public ResponseEntity<Expense> addCreatedExpense(Expense expense) {
        return ResponseEntity.ok(expenseRepo.save(expense));
    }


}
