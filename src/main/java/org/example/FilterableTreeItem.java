package org.example;

import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.scene.control.TreeItem;

import java.util.function.Predicate;

public class FilterableTreeItem<T> extends TreeItem<T> {
    private final ObservableList<TreeItem<T>> sourceChildren = FXCollections.observableArrayList();
    private final ObjectProperty<Predicate<T>> predicate = new SimpleObjectProperty<>();

    public FilterableTreeItem(T value) {
        super(value);

        FilteredList<TreeItem<T>> filteredChildren = new FilteredList<>(sourceChildren);
        filteredChildren.predicateProperty().bind(Bindings.createObjectBinding(this::initPredicate, predicate));

        filteredChildren.addListener((ListChangeListener<TreeItem<T>>) c -> {
            while (c.next()) {
                if(c.wasRemoved()) {
                    getChildren().removeAll(c.getRemoved());
                }
                if(c.wasAdded()) {
                    getChildren().addAll(c.getAddedSubList());
                }
            }
        });
    }

    private Predicate<TreeItem<T>> initPredicate(){
        return child -> {
            if (child instanceof FilterableTreeItem) {
                ((FilterableTreeItem<T>) child).predicateProperty().set(predicate.get());
            }
            if (predicate.get() == null || !child.getChildren().isEmpty()) {
                return true;
            }
            return predicate.get().test(child.getValue());
        };
    }

    public ObservableList<TreeItem<T>> getSourceChildren() {
        return sourceChildren;
    }

    public ObjectProperty<Predicate<T>> predicateProperty() {
        return predicate;
    }

}
