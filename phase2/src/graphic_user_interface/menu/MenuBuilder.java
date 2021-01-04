package graphic_user_interface.menu;

import graphic_user_interface.*;
import javafx.scene.Scene;
import javafx.scene.control.SplitPane;

import java.util.HashMap;

public class MenuBuilder {
    private final ComponentType[] order = new ComponentType[]{ComponentType.EVENT, ComponentType.MY_EVENT,
            ComponentType.SCHEDULE, ComponentType.MESSAGE, ComponentType.CANCEL, ComponentType.CREATION};
    private final HashMap<ComponentType, Component> components = new HashMap<>();
    private final ComponentBuilder builder = new ComponentBuilder();
    private Scene menuScene = null;
    private Component menuComponent = null;

    /**
     * Builds GUI components and stores them in a hashmap.
     */
    public void buildComponents(IControllerBuilder controllerBuilder){
        menuComponent = builder.createComponent(controllerBuilder, ComponentType.MENU.getLoc());
        for (ComponentType cType: order){
            components.put(cType, builder.createComponent(controllerBuilder, cType.getLoc()));
        }
    }

    /**
     * Builds GUI components in the given order.
     */
    public void buildPanes(){
        for (ComponentType cType: order){
            ((GUIMenuController) menuComponent.getPresenter()).addPane(components.get(cType).getParent(), cType);
        }
    }

    /**
     * Builds the scene for the menu.
     */
    public void buildScene(){
        SplitPane root = (SplitPane)menuComponent.getParent();
        menuScene = new Scene(root);
    }

    /**
     * Returns the component hashmap.
     * @return HashMap that maps the component types to the component.
     */
    public HashMap<ComponentType, Component> getComponentsMap(){
        return components;
    }

    /**
     * Returns the menu component built.
     * @return the menu component.
     */
    public Component getMenuComponent(){
        return menuComponent;
    }

    /**
     * Returns the Scene built by the menu.
     * @return Scene for the menu.
     */
    public Scene getScene(){
        return menuScene;
    }
}
