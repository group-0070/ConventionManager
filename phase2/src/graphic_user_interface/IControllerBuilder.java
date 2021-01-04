package graphic_user_interface;

import event_system.IEventController;
import room_system.IRoomController;
import message_system.IMessageController;
import user_system.IUserController;

public interface IControllerBuilder {
    boolean loadControllers();
    boolean saveControllers();
    IEventController getEventController();
    IUserController getUserController();
    IMessageController getMessageController();
    IRoomController getRoomController();
}
