package client.scenes;

import client.commands.ICommand;
import client.utils.LanguageManager;
import com.sun.javafx.collections.ObservableListWrapper;
import commons.Event;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Pair;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.Stack;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(ApplicationExtension.class)
class MainCtrlTest {

    //Needed for the tests to run headless.
    static {
        System.setProperty("testfx.robot", "glass");
        System.setProperty("testfx.headless", "true");
        System.setProperty("prism.order", "sw");
        System.setProperty("prism.text", "t2k");
        System.setProperty("java.awt.headless", "true");
    }

    LanguageManager languageManager;
    MainCtrl sut;
    Stage stage;
    String stageTitle;
    String titleKey;
    StringProperty sp;
    StringBinding sb;

    @Start
    void setUp(Stage ignored) {
        languageManager = mock(LanguageManager.class);
        sut = new MainCtrl(new TestConfig(), languageManager);
        stage = mock(Stage.class);
        sb = Bindings.createStringBinding(() -> "Test");
        sp = new SimpleStringProperty();
        when(stage.getTitle()).thenReturn(stageTitle);
        doAnswer(mock -> {
            stageTitle = (String) mock.getArguments()[0];
            return null;
        }).when(stage).setTitle(anyString());
        doAnswer(mock -> {
            titleKey = (String) mock.getArguments()[0];
            return sb;
        }).when(languageManager).bind((String) any());
        when(stage.titleProperty()).thenReturn(sp);
        sut.setPrimaryStage(stage);
    }

    /**
     * Below are tests for all show methods
     */

    /**
     * Tests that the title of the stage was changed and that there was a scene set.
     */
    @Test
    void showOverview() {
        sut.showOverview();
        assertEquals(null, stageTitle);
        verify(stage).setScene(any());
    }


    /**
     * Tests that the title of the stage was bound to "startScreen.windowTitle",
     * the value of that stringBinding is "Test", there was a call to setScene on the stage,
     * and that there was a call to stage.titleProperty().
     */
    @Test
    void showStartMenu() {
        sut.history=new Stack<>();
        sut.showStartMenu();
        assertEquals("startScreen.windowTitle", titleKey);
        assertEquals("Test", sb.get());
        verify(stage).setScene(any());
        verify(stage).titleProperty();
    }

    @Test //TODO
    void showStartMenu2() {

    }

    /**
     * Tests that the title of the stage was changed and that there was a scene set.
     */
    @Test
    void showParticipant() {
        sut.showParticipant();
        assertEquals(null, stageTitle);
        verify(stage).setScene(any());
    }
    @Test //TODO
    void showParticipant2() {

    }

    @Test
    void showEditParticipant() {
        sut.showEditParticipant();
        assertEquals(null, stageTitle);
        verify(stage).setScene(any());
    }
    @Test //TODO
    void showEditParticipant2() {

    }
    @Test
    void showEditExpense(){
        sut.showEditExpense();
        assertEquals(null, stageTitle);
        verify(stage).setScene(any());
    }
    @Test //TODO
    void showEditExpense2(){

    }
    @Test
    void showDebts(){
        sut.showDebts();
        assertEquals(null, stageTitle);
        verify(stage).setScene(any());
    }
    @Test //TODO
    void showDebts2(){

    }
    //TODO: showAddExpense, showConnectToServer, showExpenseConfirmation, showParticipantConfirmation
    //TODO: pickLocation, getSettingsCtrl, getSettings,


    @Test
    void showEditTransfer(){
        sut.showEditTransfer();
        assertEquals(null, stageTitle);
        verify(stage).setScene(any());
    }
    @Test
    void showTransfer(){
        sut.showTransfer();
        assertEquals(null, stageTitle);
        verify(stage).setScene(any());
    }

    @Test
    void showInvitation(){
        sut.showInvitation();
        assertEquals(null, stageTitle);
        verify(stage).setScene(any());
    }

    @Test
    void showStatistics(){
        sut.showStatistics();
        assertEquals(null, stageTitle);
        verify(stage).setScene(any());
    }

    @Test
    void showManageTags(){
        sut.showManageTags();
        assertEquals(null, stageTitle);
        verify(stage).setScene(any());
    }

    @Test
    void showEditTag(){
        sut.showEditTag();
        assertEquals(null, stageTitle);
        verify(stage).setScene(any());
    }

    @Test
    void showSettings(){
        sut.showSettings();
        assertEquals(null, stageTitle);
        verify(stage).setScene(any());
    }

    @Test
    void showConfirmations(){
        //Mocks of all the controllers
        StartScreenCtrl startScreenCtrl = mock(StartScreenCtrl.class);
        Parent startScreen = spy(Parent.class);
        ParticipantCtrl participantCtrl = mock(ParticipantCtrl.class);
        Parent participant = spy(Parent.class);
        OverviewCtrl overviewCtrl = mock(OverviewCtrl.class);
        Parent overview = spy(Parent.class);
        AddExpenseCtrl addExpenseCtrl = mock(AddExpenseCtrl.class);
        Parent addExpense = spy(Parent.class);
        InvitationCtrl invitationCtrl = mock(InvitationCtrl.class);
        Parent invitation = spy(Parent.class);
        EditParticipantCtrl editParticipantCtrl = mock(EditParticipantCtrl.class);
        Parent editParticipant = spy(Parent.class);
        SettingsCtrl settingsCtrl = mock(SettingsCtrl.class);
        Parent settings = spy(Parent.class);
        StatisticsCtrl statisticsCtrl = mock(StatisticsCtrl.class);
        Parent statistics = spy(Parent.class);
        EditExpenseCtrl editExpenseCtrl = mock(EditExpenseCtrl.class);
        Parent editExpense = spy(Parent.class);
        ConnectToServerCtrl connectToServerCtrl = mock(ConnectToServerCtrl.class);
        Parent connectToServer = spy(Parent.class);
        DebtsCtrl debtsCtrl = mock(DebtsCtrl.class);
        Parent debts = spy(Parent.class);
        ManageTagsCtrl manageTagsCtrl = mock(ManageTagsCtrl.class);
        Parent manageTags = spy(Parent.class);
        EditTagCtrl editTagCtrl = mock(EditTagCtrl.class);
        Parent editTag = spy(Parent.class);
        Parent transfer = spy(Parent.class);
        AddTransferCtrl transferCtrl = mock(AddTransferCtrl.class);
        EditTransferCtrl editTransferCtrl = mock(EditTransferCtrl.class);
        Parent editTransfer = spy(Parent.class);
        sut.initialize(stage,
                new Pair<>(startScreenCtrl, startScreen),
                new Pair<>(participantCtrl, participant),
                new Pair<>(overviewCtrl, overview),
                new Pair<>(addExpenseCtrl, addExpense),
                new Pair<>(invitationCtrl,invitation),
                new Pair<>(editParticipantCtrl,editParticipant),
                new Pair<>(settingsCtrl, settings),
                new Pair<>(statisticsCtrl,statistics),
                new Pair<>(editExpenseCtrl, editExpense),
                new Pair<>(connectToServerCtrl, connectToServer),
                new Pair<>(debtsCtrl, debts),
                new Pair<>(manageTagsCtrl, manageTags),
                new Pair<>(editTagCtrl, editTag),
                new Pair<>(transferCtrl, transfer),
                new Pair<>(editTransferCtrl,editTransfer));
        sut.showExpenseConfirmation();
        verify(overviewCtrl).showNotification("overview.confirmExpenseAdd");
        sut.showInviteConfirmation();
        verify(overviewCtrl).showNotification("overview.confirmInvite");
        sut.showEditConfirmation();
        verify(overviewCtrl).showNotification("overview.confirmEdits");
        sut.showParticipantConfirmation();
        verify(overviewCtrl).showNotification("overview.confirmParticipantAdd");
    }

    @Test
    void gettersAndSetters(){
        //Mocks of all the controllers
        StartScreenCtrl startScreenCtrl = mock(StartScreenCtrl.class);
        Parent startScreen = spy(Parent.class);
        ParticipantCtrl participantCtrl = mock(ParticipantCtrl.class);
        Parent participant = spy(Parent.class);
        OverviewCtrl overviewCtrl = mock(OverviewCtrl.class);
        Parent overview = spy(Parent.class);
        AddExpenseCtrl addExpenseCtrl = mock(AddExpenseCtrl.class);
        Parent addExpense = spy(Parent.class);
        InvitationCtrl invitationCtrl = mock(InvitationCtrl.class);
        Parent invitation = spy(Parent.class);
        EditParticipantCtrl editParticipantCtrl = mock(EditParticipantCtrl.class);
        Parent editParticipant = spy(Parent.class);
        SettingsCtrl settingsCtrl = mock(SettingsCtrl.class);
        Parent settings = spy(Parent.class);
        StatisticsCtrl statisticsCtrl = mock(StatisticsCtrl.class);
        Parent statistics = spy(Parent.class);
        EditExpenseCtrl editExpenseCtrl = mock(EditExpenseCtrl.class);
        Parent editExpense = spy(Parent.class);
        ConnectToServerCtrl connectToServerCtrl = mock(ConnectToServerCtrl.class);
        Parent connectToServer = spy(Parent.class);
        DebtsCtrl debtsCtrl = mock(DebtsCtrl.class);
        Parent debts = spy(Parent.class);
        ManageTagsCtrl manageTagsCtrl = mock(ManageTagsCtrl.class);
        Parent manageTags = spy(Parent.class);
        EditTagCtrl editTagCtrl = mock(EditTagCtrl.class);
        Parent editTag = spy(Parent.class);
        Parent transfer = spy(Parent.class);
        AddTransferCtrl transferCtrl = mock(AddTransferCtrl.class);
        EditTransferCtrl editTransferCtrl = mock(EditTransferCtrl.class);
        Parent editTransfer = spy(Parent.class);
        sut.initialize(stage,
                new Pair<>(startScreenCtrl, startScreen),
                new Pair<>(participantCtrl, participant),
                new Pair<>(overviewCtrl, overview),
                new Pair<>(addExpenseCtrl, addExpense),
                new Pair<>(invitationCtrl,invitation),
                new Pair<>(editParticipantCtrl,editParticipant),
                new Pair<>(settingsCtrl, settings),
                new Pair<>(statisticsCtrl,statistics),
                new Pair<>(editExpenseCtrl, editExpense),
                new Pair<>(connectToServerCtrl, connectToServer),
                new Pair<>(debtsCtrl, debts),
                new Pair<>(manageTagsCtrl, manageTags),
                new Pair<>(editTagCtrl, editTag),
                new Pair<>(transferCtrl, transfer),
                new Pair<>(editTransferCtrl,editTransfer));
        sut.addToHistory(mock(ICommand.class));
        assertNotNull(sut.getHistory());
        assertEquals(sut.getSettingsCtrl(), settingsCtrl);
        assertEquals(sut.getTagCtrl(), editTagCtrl);
        assertEquals(sut.getEditTransferCtrl(), editTransferCtrl);
        assertEquals(sut.getTransferCtrl(), transferCtrl);
    }
    /**
     * Tests that the stage that was set is returned when calling the getter for it.
     */
    @Test
    void getPrimaryStage() {
        assertEquals(stage, sut.getPrimaryStage());
    }

    /**
     * First, this method sets up mocks and spies of the parameters of the initialize method.
     * Then it calls that method, followed by testing whether the values were set in the MainCtrl.
     */
    @Test
    void initialize() {
        //Mocks of all the controllers
        StartScreenCtrl startScreenCtrl = mock(StartScreenCtrl.class);
        Parent startScreen = spy(Parent.class);
        ParticipantCtrl participantCtrl = mock(ParticipantCtrl.class);
        Parent participant = spy(Parent.class);
        OverviewCtrl overviewCtrl = mock(OverviewCtrl.class);
        Parent overview = spy(Parent.class);
        AddExpenseCtrl addExpenseCtrl = mock(AddExpenseCtrl.class);
        Parent addExpense = spy(Parent.class);
        InvitationCtrl invitationCtrl = mock(InvitationCtrl.class);
        Parent invitation = spy(Parent.class);
        EditParticipantCtrl editParticipantCtrl = mock(EditParticipantCtrl.class);
        Parent editParticipant = spy(Parent.class);
        SettingsCtrl settingsCtrl = mock(SettingsCtrl.class);
        Parent settings = spy(Parent.class);
        StatisticsCtrl statisticsCtrl = mock(StatisticsCtrl.class);
        Parent statistics = spy(Parent.class);
        EditExpenseCtrl editExpenseCtrl = mock(EditExpenseCtrl.class);
        Parent editExpense = spy(Parent.class);
        ConnectToServerCtrl connectToServerCtrl = mock(ConnectToServerCtrl.class);
        Parent connectToServer = spy(Parent.class);
        DebtsCtrl debtsCtrl = mock(DebtsCtrl.class);
        Parent debts = spy(Parent.class);
        ManageTagsCtrl manageTagsCtrl = mock(ManageTagsCtrl.class);
        Parent manageTags = spy(Parent.class);
        EditTagCtrl editTagCtrl = mock(EditTagCtrl.class);
        Parent editTag = spy(Parent.class);
        Parent transfer = spy(Parent.class);
        AddTransferCtrl transferCtrl = mock(AddTransferCtrl.class);
        EditTransferCtrl editTransferCtrl = mock(EditTransferCtrl.class);
        Parent editTransfer = spy(Parent.class);
        sut.initialize(stage,
                new Pair<>(startScreenCtrl, startScreen),
                new Pair<>(participantCtrl, participant),
                new Pair<>(overviewCtrl, overview),
                new Pair<>(addExpenseCtrl, addExpense),
                new Pair<>(invitationCtrl,invitation),
                new Pair<>(editParticipantCtrl,editParticipant),
                new Pair<>(settingsCtrl, settings),
                new Pair<>(statisticsCtrl,statistics),
                new Pair<>(editExpenseCtrl, editExpense),
                new Pair<>(connectToServerCtrl, connectToServer),
                new Pair<>(debtsCtrl, debts),
                new Pair<>(manageTagsCtrl, manageTags),
                new Pair<>(editTagCtrl, editTag),
                new Pair<>(transferCtrl, transfer),
                new Pair<>(editTransferCtrl,editTransfer));
        assertEquals(stage, sut.getPrimaryStage());

        //Test if the controllers were initialised
        assertEquals(startScreenCtrl, sut.getStartScreenCtrl());
        assertEquals(startScreen, sut.getStartScreen().getRoot());
        assertEquals(participantCtrl, sut.getParticipantCtrl());
        assertEquals(participant, sut.getParticipant().getRoot());
        assertEquals(overviewCtrl, sut.getOverviewCtrl());
        assertEquals(overview, sut.getOverview().getRoot());
        assertEquals(invitationCtrl, sut.getInvitationCtrl());
        assertEquals(invitation, sut.getInvitation().getRoot());
        assertEquals(editParticipantCtrl, sut.getEditparticipantCtrl());
        assertEquals(editParticipant, sut.getEditparticipant().getRoot());
        assertEquals(editExpenseCtrl, sut.getEditExpenseCtrl());
        assertEquals(editExpense, sut.getEditExpense().getRoot());
        assertEquals(addExpenseCtrl, sut.getAddExpenseCtrl());
        assertEquals(addExpense, sut.getAddExpense().getRoot());
        assertEquals(debtsCtrl, sut.getDebtsCtrl());
        assertEquals(debts, sut.getDebts().getRoot());
    }

    @Test
    void setEvent() {
        Event event = new Event("Mock Event", new Date(), new Date());
        sut.setEvent(event);

        assertEquals(event, sut.getEvent());
    }
}