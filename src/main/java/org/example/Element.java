package org.example;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

import java.util.Objects;

public class Element {

    private final String text;

    private final BooleanProperty filtered = new SimpleBooleanProperty();

    public Element(String text) {
        this.text = text;
        filteredProperty().set(false);
    }

    public String getText() {
        return text;
    }

    public BooleanProperty filteredProperty() {
        return filtered;
    }

    public boolean isFiltered() {
        return filtered.get();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Element element = (Element) o;
        return Objects.equals(text, element.text);
    }

    @Override
    public int hashCode() {
        return Objects.hash(text);
    }

    @Override
    public String toString() {
        return getText();
    }
}
