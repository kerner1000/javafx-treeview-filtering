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

    // Do not convert this to a local variable. Thinks will break.
    private final FilteredList<TreeItem<T>> filteredChildren = new FilteredList<>(sourceChildren);
    private final ObjectProperty<Predicate<T>> predicate = new SimpleObjectProperty<>();

    public FilterableTreeItem(T value) {
        super(value);

        filteredChildren.predicateProperty().bind(Bindings.createObjectBinding(this::buildFilterableListPredicate, predicate));

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

    private Predicate<? super TreeItem<T>> buildFilterableListPredicate() {
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
