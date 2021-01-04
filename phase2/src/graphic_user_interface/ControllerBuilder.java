package graphic_user_interface;

import event_system.*;
import room_system.IRoomController;
import room_system.RoomController;
import room_system.RoomService;
import room_system.RoomServiceEngine;
import user_system.*;
import message_system.*;

public class ControllerBuilder implements IControllerBuilder {
    private final IEventController event_controller;
    private final IUserController user_controller;
    private final IMessageController message_controller;
    private final IRoomController room_controller;

    private final String database_filename = "jdbc:sqlite:assets/UserData.db";

    public ControllerBuilder(){

        // create new services
        UserService userService = new UserServiceEngine();
        MessageService messageService = new MessageServiceEngine();
        EventService eventService = new EventServiceEngine();
        RoomService roomService = new RoomServiceEngine();

        // create new controllers
        user_controller = new UserController(database_filename, userService);
        event_controller = new EventController(database_filename, userService, eventService,roomService);
        message_controller = new MessageController(database_filename, userService, eventService, messageService);
        room_controller = new RoomController(database_filename, roomService);

    }

    // Returns true iff all loaders do not run into issues.
    @Override
    public boolean loadControllers(){
        boolean status;
        status = user_controller.load();
        status = status && event_controller.load();
        status = status && message_controller.load();
        status = status && room_controller.load();
        return status;
    }

    // Returns true iff all savers do not run into issues.
    @Override
    public boolean saveControllers(){
        boolean status;
        status = user_controller.save();
        status = status && event_controller.save();
        status = status && message_controller.save();
        status = status && room_controller.save();
        return status;
    }

    @Override
    public IEventController getEventController() {
        return event_controller;
    }

    @Override
    public IUserController getUserController() {
        return user_controller;
    }

    @Override
    public IMessageController getMessageController() {
        return message_controller;
    }

    @Override
    public IRoomController getRoomController() {
        return room_controller;
    }
}
