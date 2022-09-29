package org.example;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;

import java.util.function.Predicate;

public class SimpleApplication extends Application {

    /**
     * Text input to set a filter string.
     */
    private final TextField filterInput = new TextField();

    /**
     * Text input to set a name for a new element.
     */
    private final TextField newElementInput = new TextField();

    /**
     * {@link TreeView} that shows our {@link Element tree elements}.
     */
    private final TreeView<Element> treeView = new TreeView<>();

    private final FilterableTreeItem<Element> rootItem = new FilterableTreeItem<>((new Element("Root")));

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void init() throws Exception {
        super.init();
        filterInput.setPromptText("Enter some string to filter for matching elements.");
        filterInput.setTooltip(new Tooltip("Enter some string to filter for matching elements."));
        newElementInput.setPromptText("Input a name for a new item.");
        newElementInput.setTooltip(new Tooltip("Input a name for a new item."));

        /*
         * Update the predicate with the new filter string.
         */
        filterInput.textProperty().addListener((observable, oldValue, newValue) -> rootItem.predicateProperty().set(buildNewPredicate(newValue.trim())));
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Hello World!");
        primaryStage.setScene(buildScene());
        primaryStage.setMinWidth(300);
        primaryStage.setMinHeight(200);
        primaryStage.show();
    }

    /**
     * Builds a new {@link Predicate} using the input string from {@link #filterInput}.
     * @return the newly created Predicate
     */
    @SuppressWarnings("unused")
    private Predicate<Element> buildNewPredicate(){
        return buildNewPredicate(filterInput.getText().trim());
    }

    /**
     * Builds a new {@link Predicate} using the given string.
     *
     * @param string input string for the new Predicate
     * @return the newly created Predicate
     */
    private Predicate<Element> buildNewPredicate(String string){
        return element -> {
            @SuppressWarnings("")
            var result = string.isBlank() || element.getText().contains(string);
            return result;
        };
    }

    private Scene buildScene() {

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(4, 4, 4, 4));
        grid.setHgap(4);
        grid.setVgap(4);

        Label inputLabel = new Label();
        inputLabel.setText("Filter string: ");

        grid.add(inputLabel, 0, 0);
        GridPane.setHalignment(inputLabel, HPos.CENTER);
        grid.add(filterInput, 1, 0);
        GridPane.setHgrow(filterInput, Priority.ALWAYS);

        Button addNewElementButton = new Button();
        addNewElementButton.setText("Add new element");
        addNewElementButton.setOnAction(this::handleAddNewElementButton);

        grid.add(newElementInput, 0, 1);
        GridPane.setHgrow(newElementInput, Priority.ALWAYS);
        grid.add(addNewElementButton, 1, 1);
        GridPane.setHalignment(addNewElementButton, HPos.CENTER);

        rootItem.setExpanded(true);
        treeView.setRoot(rootItem);
        grid.add(treeView, 0, 2, 2, 1);
        GridPane.setVgrow(treeView, Priority.ALWAYS);

        return new Scene(grid);
    }

    /**
     * Adds a new item to the Tree View.
     *
     * @param actionEvent the action event
     */
    private void handleAddNewElementButton(ActionEvent actionEvent) {
        FilterableTreeItem<Element> newItem = new FilterableTreeItem<>(new Element(newElementInput.getText().trim()));
        rootItem.getSourceChildren().add(newItem);
    }
}
