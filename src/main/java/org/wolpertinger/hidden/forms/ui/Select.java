package org.wolpertinger.hidden.forms.ui;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.vaadin.flow.component.select.data.SelectListDataView;

public class Select<T> extends com.vaadin.flow.component.select.Select<T> {

    @SafeVarargs
    @JsonSetter
    @Override
    public final SelectListDataView<T> setItems(T... items) {
        return super.setItems(items);
    }
}
