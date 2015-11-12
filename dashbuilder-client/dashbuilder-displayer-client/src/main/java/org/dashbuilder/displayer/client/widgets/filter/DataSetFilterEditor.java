/**
 * Copyright (C) 2014 JBoss Inc
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
package org.dashbuilder.displayer.client.widgets.filter;

import javax.enterprise.context.Dependent;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;
import org.dashbuilder.dataset.ColumnType;
import org.dashbuilder.dataset.DataSetMetadata;
import org.dashbuilder.dataset.filter.ColumnFilter;
import org.dashbuilder.dataset.filter.CoreFunctionFilter;
import org.dashbuilder.dataset.filter.CoreFunctionType;
import org.dashbuilder.dataset.filter.DataSetFilter;
import org.dashbuilder.dataset.filter.FilterFactory;
import org.dashbuilder.displayer.client.resources.i18n.CommonConstants;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.Icon;
import org.gwtbootstrap3.client.ui.ListBox;
import org.uberfire.client.mvp.UberView;
import org.uberfire.mvp.Command;

public class DataSetFilterEditor implements IsWidget {

    public interface View extends UberView<DataSetFilterEditor> {

        void showNewFilterHome();

        void showColumnSelector();

        void addColumn(String column);

        int getSelectedColumnIndex();

        void resetSelectedColumn();

        void addColumnFilterEditor(ColumnFilterEditor editor);

        void removeColumnFilterEditor(ColumnFilterEditor editor);
    }

    public static class Factory {

        public ColumnFilterEditor createColumnFilterEditor(DataSetMetadata metadata, ColumnFilter filter) {
            return new ColumnFilterEditor(metadata, filter);
        }
    }

    View view = null;
    Factory factory = null;
    DataSetFilter filter = null;
    DataSetMetadata metadata = null;
    Command onChangeCommand = new Command() { public void execute() {} };

    public DataSetFilterEditor(View view,
                               Factory factory,
                               DataSetFilter filter,
                               DataSetMetadata metadata,
                               Command onChangeCommand) {
        this.view = view;
        this.factory = factory;
        this.filter = filter;
        this.metadata = metadata;
        this.onChangeCommand = onChangeCommand;
        view.init(this);

        init();
    }

    @Override
    public Widget asWidget() {
        return view.asWidget();
    }

    public DataSetFilter getFilter() {
        return filter;
    }

    public void init() {

        view.showNewFilterHome();
        if (metadata != null) {
            for (int i = 0; i < metadata.getNumberOfColumns(); i++) {
                view.addColumn(metadata.getColumnId(i));
            }
        }

        if (filter != null) {
            for (ColumnFilter columnFilter : filter.getColumnFilterList()) {
                ColumnFilterEditor columnFilterEditor = factory.createColumnFilterEditor(metadata, columnFilter);
                columnFilterEditor.setOnFilterChangeCommand(createFilterChangeCommand(columnFilterEditor));
                columnFilterEditor.setOnFilterDeleteCommand(createFilterDeleteCommand(columnFilterEditor));
                view.addColumnFilterEditor(columnFilterEditor);
            }
        }
    }

    public void newFilterStart() {
        view.showColumnSelector();
    }

    public void newFilterCancel() {
        view.showNewFilterHome();
    }

    public void createFilter() {
        int selectedIdx = view.getSelectedColumnIndex();
        String columnId = metadata.getColumnId(selectedIdx);
        ColumnType columnType = metadata.getColumnType(selectedIdx);
        CoreFunctionFilter columnFilter = FilterFactory.createCoreFunctionFilter(
                columnId, columnType,
                ColumnType.DATE.equals(columnType) ? CoreFunctionType.TIME_FRAME : CoreFunctionType.NOT_EQUALS_TO);

        if (filter == null) {
            filter = new DataSetFilter();
        }
        filter.addFilterColumn(columnFilter);

        ColumnFilterEditor columnFilterEditor = factory.createColumnFilterEditor(metadata, columnFilter);
        columnFilterEditor.setOnFilterChangeCommand(createFilterChangeCommand(columnFilterEditor));
        columnFilterEditor.setOnFilterDeleteCommand(createFilterDeleteCommand(columnFilterEditor));
        columnFilterEditor.showFilterConfig();

        view.addColumnFilterEditor(columnFilterEditor);
        view.resetSelectedColumn();
        view.showNewFilterHome();
        fireFilterChanged();
    }

    protected void fireFilterChanged() {
        onChangeCommand.execute();
    }

    protected Command createFilterChangeCommand(final ColumnFilterEditor editor) {
        return new Command() {
            public void execute() {
                fireFilterChanged();
            }
        };
    }

    protected Command createFilterDeleteCommand(final ColumnFilterEditor editor) {
        return new Command() {
            public void execute() {
                view.removeColumnFilterEditor(editor);
                view.showNewFilterHome();
                Integer index = filter.getColumnFilterIdx(editor.getFilter());
                if (index != null) {
                    filter.getColumnFilterList().remove(index.intValue());
                    fireFilterChanged();
                }
            }
        };
    }
}
