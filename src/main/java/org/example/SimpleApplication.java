package org.example;

import javafx.application.Application;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
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
     * A {@link Predicate} to check if an instance of {@link Element} should be shown in the TreeView or not.
     */
    private final Predicate<Element> predicate = element -> {
        var filterInputString = filterInput.getText().trim();
        @SuppressWarnings("")
        var result = filterInputString.isBlank() || element.getText().contains(filterInputString);
        return result;
    };

    /**
     * An {@link Observable} that is monitored for changes that should fire a
     * {@link javafx.collections.ListChangeListener.Change}.
     * <p>
     * Whenever {@link Element#filteredProperty()} changes, {@link #baseList} will fire a change event.
     *
     * @param item the element that is checked for changes
     * @return the Observable that should trigger a list update
     */
    Observable test(TreeItem<Element> item) {
        return item.getValue().filteredProperty();
    }

    /**
     * The base list holding all TreeItems. It is an {@link ObservableList} that will fire change events when
     * {@link #test(TreeItem)}/ {@link Element#filteredProperty()} changes.
     */
    private final ObservableList<TreeItem<Element>> baseList = FXCollections.observableArrayList(item ->
            new Observable[]{test(item)});

    /**
     * A {@link Predicate} that must match in order to be contained in {@link #treeContent}.
     */
    private final Predicate<TreeItem<Element>> filteredListPredicate = element -> !element.getValue().isFiltered();

    /**
     * A filtered view to {@link #baseList}. It 'shows'/ contains all elements that are contained in {@link #baseList}
     * that match predicate {@link #filteredListPredicate}.
     */
    private final FilteredList<TreeItem<Element>> treeContent = new FilteredList<>(baseList, filteredListPredicate);

    /**
     * {@link TreeView} that shows our {@link Element tree elements}.
     */
    private final TreeView<Element> treeView = new TreeView<>();

    private final TreeItem<Element> rootItem = new TreeItem<>(new Element("Root"));

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
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Hello World!");
        primaryStage.setScene(buildScene());
        primaryStage.setMinWidth(300);
        primaryStage.setMinHeight(200);
        primaryStage.show();
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

        /*
         * Register a listener to tree content changes.
         * We cannot call rootItem#setChildren() to set our filtered list here directly,
         * that is why we need to modify via get().add() and get().remove().
         */
        treeContent.addListener((ListChangeListener<TreeItem<Element>>) c -> {
            System.err.println("treeContent: " + c);
            while (c.next()) {
                if (c.wasAdded()) {
                    rootItem.getChildren().addAll(c.getAddedSubList());
                } else if (c.wasRemoved()) {
                    System.err.println(rootItem.getChildren().removeAll(c.getRemoved()));
                }
            }
            System.err.println("treeContent now:" + treeContent);
        });

        /*
         * Debug listener.
         */
        baseList.addListener((ListChangeListener<TreeItem<Element>>) c -> {
                    System.err.println("BaseList:" + c);
                    System.err.println("BaseList now:" + baseList);
                }
        );

        /*
         * Here is the filtering actually triggert.
         * When the filter input changes,
         * all elements in base list are checked and their filtered property is set accordingly,
         * which will trigger a change event in the base list,
         * which updates the filtered list,
         * which updates the root element children (which are effectively the tree view content).
         */
        filterInput.textProperty().addListener((observable, oldValue, newValue) -> baseList.forEach(e -> e.getValue().filteredProperty().set(!predicate.test(e.getValue()))));

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
        baseList.add(new TreeItem<>(new Element(newElementInput.getText().trim())));
    }
}
