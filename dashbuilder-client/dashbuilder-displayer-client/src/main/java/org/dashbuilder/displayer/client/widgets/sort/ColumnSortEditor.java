/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.dashbuilder.displayer.client.widgets.sort;

import java.util.List;
import javax.inject.Inject;

import org.dashbuilder.dataset.sort.SortOrder;
import org.jboss.errai.common.client.api.IsElement;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.uberfire.client.mvp.UberElement;
import org.uberfire.mvp.Command;

public class ColumnSortEditor implements IsElement {

    public interface View extends UberElement<ColumnSortEditor> {

        void clearColumnList();

        void clearOrderList();

        void addColumn(String column, boolean selected);

        void addOrder(String order, boolean selected);
    }

    private View view;
    private String selectedColumn;
    private SortOrder selectedOrder;
    private Command onChangeCommand;
    private Command onRemoveCommand;

    @Inject
    public ColumnSortEditor(View view) {
        this.view = view;
        this.view.init(this);
    }

    @Override
    public HTMLElement getElement() {
        return view.getElement();
    }

    public void init(List<String> columnList, String columnSelected, SortOrder orderSelected) {
        view.clearColumnList();
        columnList.forEach(column -> view.addColumn(column, column.equals(columnSelected)));

        view.clearOrderList();
        view.addOrder(SortOrder.ASCENDING.toString(), SortOrder.ASCENDING.equals(orderSelected));
        view.addOrder(SortOrder.DESCENDING.toString(), SortOrder.DESCENDING.equals(orderSelected));
    }

    public String getSelectedColumn() {
        return selectedColumn;
    }

    public SortOrder getSelectedOrder() {
        return selectedOrder;
    }

    public void setOnChangeCommand(Command onChangeCommand) {
        this.onChangeCommand = onChangeCommand;
    }

    public void setOnRemoveCommand(Command onRemoveCommand) {
        this.onRemoveCommand = onRemoveCommand;
    }

    // View callbacks

    void onColumnChange(String column) {
        selectedColumn = column;
        if (onChangeCommand != null) {
            onChangeCommand.execute();
        }
    }

    void onOrderChange(String order) {
        selectedOrder = SortOrder.valueOf(order);
        if (onChangeCommand != null) {
            onChangeCommand.execute();
        }
    }

    void onRemove() {
        if (onRemoveCommand != null) {
            onRemoveCommand.execute();
        }
    }
}
