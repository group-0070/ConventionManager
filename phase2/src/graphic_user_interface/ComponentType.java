package graphic_user_interface;

public enum ComponentType {
    MENU("/graphic_user_interface/menu/MenuView.fxml"),
    MESSAGE("/graphic_user_interface/message/MessageView.fxml"),
    LOGIN("/graphic_user_interface/login/LoginView.fxml"),
    CREATION("/graphic_user_interface/account_creation/AccountCreationView.fxml"),
    SCHEDULE("/graphic_user_interface/schedule/ScheduleView.fxml"),
    EVENT("/graphic_user_interface/events/EventsView.fxml"),
    MY_EVENT("/graphic_user_interface/events/EventsView.fxml"),
    CANCEL("/graphic_user_interface/cancel_events/CancelEventsView.fxml");
    String value;

    ComponentType(String value){
        this.value=value;
    }
    public String getLoc(){
        return this.value;
    }
}