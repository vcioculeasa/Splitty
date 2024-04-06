package client.scenes;

import client.utils.*;
import com.google.inject.Inject;
import commons.Event;
import commons.Expense;
import commons.Participant;
import commons.ParticipantPayment;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;

import java.net.URL;
import java.util.*;

public class DebtsCtrl implements Initializable {
    private final ServerUtils serverUtils;
    private final ConfigInterface config;
    private final MainCtrl mainCtrl;
    private final LanguageManager languageManager;
    private final Alert alert;
    private final CurrencyConverter currencyConverter;
    @FXML
    private Accordion menu;
    @FXML
    private Button back;

    /**
     * @param mainCtrl
     * @param config
     * @param languageManager
     * @param serverUtils
     * @param alert
     */
    @Inject
    public DebtsCtrl(MainCtrl mainCtrl,
                     ConfigInterface config,
                     LanguageManager languageManager,
                     ServerUtils serverUtils,
                     CurrencyConverter currencyConverter,
                     Alert alert) {
        this.mainCtrl = mainCtrl;
        this.config = config;
        this.languageManager = languageManager;
        this.serverUtils = serverUtils;
        this.currencyConverter = currencyConverter;
        this.alert = alert;
    }

    /**
     * trying to get the mark received button into a green checkstyle, throws errors for now
     *
     * @param button
     */
    public static void animation(Button button) {
        Label checkMark = new Label("\u2714"); // Unicode for checkmark symbol
        checkMark.setStyle("-fx-text-fill: green; " +
                "-fx-font-size: 24px; -fx-opacity: 0;"); // Initially invisible
        Timeline timeline = new Timeline();

        // Animate button text opacity to 0
        timeline.getKeyFrames().add(new KeyFrame(Duration.seconds(0.5),
                new KeyValue(button.opacityProperty(), 0)));

        // Animate check mark opacity to 1
        timeline.getKeyFrames().add(new KeyFrame(Duration.seconds(0.5),
                new KeyValue(checkMark.opacityProperty(), 1)));

        // Change the button text after animation completes
        timeline.setOnFinished(e -> button.setText("\u2714"));

        // Play the animation
        timeline.play();
    }

    /**
     * @param url            The location used to resolve relative paths for the root object, or
     *                       {@code null} if the location is not known.
     * @param resourceBundle The resources used to localize the root object, or {@code null} if
     *                       the root object was not localized.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        back.textProperty().bind(languageManager.bind("debts.setButton"));
    }

    /**
     * loads the data for the scene
     */
    public void refresh() {
        menu.getPanes().clear();
        setTitles(mainCtrl.getEvent());
    }

    /**
     * return to overview
     */
    public void goBack() {
        mainCtrl.showOverview();
    }

    /**
     * goes through all participants payments that have to be paid
     *
     * @param event to search for money splits
     */
    public void setTitles(Event event) {
        Map<Participant, Double> shares = new HashMap<>();
        for (Participant participant : event.getParticipantsList()) {
            shares.put(participant, calculateShare(participant));
        }
        PriorityQueue<DebtPair> positive = new PriorityQueue<>();
        positive.addAll(shares.entrySet().stream().filter(x -> x.getValue() > 0)
                .map(pair -> new DebtPair(pair.getKey(), pair.getValue())).toList());
        PriorityQueue<DebtPair> negative = new PriorityQueue<>();
        negative.addAll(shares.entrySet().stream().filter(x -> x.getValue() < 0)
                .map(pair -> new DebtPair(pair.getKey(), Math.abs(pair.getValue()))).toList());
        List<Debt> debts = new ArrayList<>();
        while (!positive.isEmpty() && !negative.isEmpty()) {
            DebtPair owed = positive.poll();
            DebtPair owes = negative.poll();
            if (owed.getValue() > owes.getValue()) {
                debts.add(new Debt(owes.getKey(), owed.getKey(), owes.getValue()));
                owed.setValue(owed.getValue() - owes.getValue());
                positive.add(owed);
            } else if (owed.getValue() < owes.getValue()) {
                debts.add(new Debt(owes.getKey(), owed.getKey(), owed.getValue()));
                owes.setValue(owes.getValue() - owed.getValue());
                negative.add(owes);
            } else {
                debts.add(new Debt(owes.getKey(), owed.getKey(), owed.getValue()));
            }
        }
        for (Debt debt : debts) {
            if (!debt.getDebtor().getName().equals(debt.getCreditor().getName())) {
                String title = String.format("%s: %.2f %s => %s",
                        debt.getDebtor().getName(),
                        debt.getSum(),
                        getCurrency(),
                        debt.getCreditor().getName());
                TitledPane tp = new TitledPane(title, null);
                menu.getPanes().add(tp);
                AnchorPane anchorPane = new AnchorPane();
                Label info = new Label();
                Button mark = new Button();
                if (debt.getCreditor().getBic().equals("\u2714") ||
                        debt.getCreditor().getIban().equals("")) {
                    info.textProperty().bind(languageManager.bind("debts.unavailable"));
                    mark.setVisible(false);
                } else {
                    //info.textProperty().bind(languageManager.bind("debts.available"));
                    String data = debt.getCreditor().getName() + "\nIBAN: " +
                            debt.getCreditor().getIban() + "\nBIC: " +
                            debt.getCreditor().getBic();

                    info.setText(data);
                    mark.textProperty().bind(languageManager.bind("debts.send"));
                    mark.setOnAction(x ->
                    {
                        mark.textProperty().bind(languageManager.bind("debts.check"));
                    });
                }
                anchorPane.getChildren().add(info);
                anchorPane.getChildren().add(mark);
                anchorPane.setTopAnchor(info, 10.0);
                anchorPane.setLeftAnchor(info, 10.0);

                // Position the button relative to the label
                anchorPane.setTopAnchor(mark,
                        AnchorPane.getTopAnchor(info) + info.getPrefHeight() + 30.0);
                anchorPane.setLeftAnchor(mark,
                        AnchorPane.getLeftAnchor(info) + info.getPrefWidth() + 300.0);
                tp.setContent(anchorPane);
            }
        }
    }

    /**
     * Method that calculates the share per person and returns it as a double.
     * @param current - the participant to calculate the share for.
     * @return - the share of that participant.
     */
    private double calculateShare(Participant current) {
        double participantShare = 0;
        Event curr = mainCtrl.getEvent();
        List<Expense> expenses = curr.getExpensesList();
        for (Expense expense : expenses) {
            String currency = expense.getCurrency();
            Date date = expense.getDate();
            String base = getCurrency();
            for (ParticipantPayment p : expense.getSplit()) {
                if (p.getParticipant().equals(current)
                        && !expense.getPayee().equals(current)) {
                    participantShare -= currencyConverter.convert(date,
                            currency, base, p.getPaymentAmount());
                } else if (expense.getPayee().equals(current)
                        && !p.getParticipant().equals(current)) {
                    participantShare += currencyConverter.convert(date,
                            currency, base, p.getPaymentAmount());
                }
            }
        }
        return Math.round(participantShare*100.0)/100.0;
    }

    /**
     * Method that gets the currency from the config.
     * @return - the correct currency from the config.
     */
    private String getCurrency() {
        String currencyString = config.getProperty("currency");
        if (currencyString == null || currencyString.isEmpty()) currencyString = "EUR";
        return currencyString;
    }

}
