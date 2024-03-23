package client.utils;

import client.scenes.MainCtrl;
import commons.Expense;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class ExpenseListCell extends ListCell<Expense> {
    private Label expenseName;
    private Label paidLabel;
    private Label payeeName;
    private Label forLabel;
    private Label price;
    private Label currency;
    private Label date;
    private Label payers;
    private Button edit;
    private HBox details;
    private VBox vBox;
    private HBox hBox;
    private Region autogrowLeft;
    private Region autogrowRight;

    /**
     * Constructor for the RecentEventCell.
     */
    public ExpenseListCell(MainCtrl mainCtrl) {
        super();
        expenseName = new Label();
        paidLabel = new Label("paid");
        payeeName = new Label();
        forLabel = new Label("for");
        price = new Label();
        currency = new Label();
        date = new Label();
        payers = new Label();
        edit = new Button();
        edit.setText("✎");
        edit.setOnAction(param -> {
            //TODO: add edit expense functionality
        });
        autogrowLeft = new Region();
        autogrowRight = new Region();
        details = new HBox(payeeName, paidLabel, price, currency, forLabel, expenseName);
        vBox = new VBox(details, payers);
        hBox = new HBox(date, autogrowLeft, vBox, autogrowRight, edit);
        hBox.setSpacing(5);
        vBox.setSpacing(5);
        details.setSpacing(3);
        payeeName.setStyle("-fx-font-weight: 700;");
        expenseName.setStyle("-fx-font-weight: 700;");
        price.setStyle("-fx-font-weight: 700;");
        currency.setStyle("-fx-font-weight: 700;");
        date.setAlignment(Pos.CENTER);
        edit.setAlignment(Pos.CENTER);
        HBox.setHgrow(autogrowLeft, Priority.ALWAYS);
        HBox.setHgrow(autogrowRight, Priority.ALWAYS);
    }

    /**
     * Updates the item in the list to have the event name and the open and close buttons.
     * @param item - item in the list
     * @param empty - whether the item is empty or not
     */
    @Override
    protected void updateItem(Expense item, boolean empty) {
        super.updateItem(item, empty);
        if (empty || item == null) {
            setText(null);
            setGraphic(null);
        } else {
            try {
                expenseName.setText(this.getItem().getTitle());
            }
            catch (NullPointerException e) {
                expenseName.setText("<no title>");
            }
            try {
                payeeName.setText(this.getItem().getPayee().getName());
            }
            catch (NullPointerException e) {
                payeeName.setText("<no payee>");
            }
            try {
                price.setText(Double.toString(this.getItem().getAmount()));
            }
            catch (NullPointerException e) {
                price.setText("<no price>");
            }
            catch (NumberFormatException e) {
                price.setText("<invalid price>");
            }
            try {
                currency.setText(this.getItem().getCurrency());
            }
            catch (NullPointerException e) {
                currency.setText("<no currency>");
            }
            StringBuilder sb = new StringBuilder();
            if (!this.getItem().getSplit().isEmpty()) {
                sb.append("(");
                for (int i = 0; i < this.getItem().getSplit().size() - 1; i++) {
                    sb.append(this.getItem().getSplit().get(i)).append(", ");
                }
                sb.append(this.getItem().getSplit().get(this.getItem().getSplit().size() - 1));
                sb.append(")");
            }
            else {
                sb.append("none");
            }
            payers.setText(sb.toString());
            try {
                date.setText(String.valueOf(this.getItem().getDate()));
            }
            catch (NullPointerException e) {
                date.setText("<no date>");
            }
            setGraphic(hBox);
        }
    }
}
