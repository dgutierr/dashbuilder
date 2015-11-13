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
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.dashbuilder.dataset.ColumnType;
import org.dashbuilder.dataset.DataSetMetadata;
import org.dashbuilder.dataset.filter.ColumnFilter;
import org.dashbuilder.dataset.filter.CoreFunctionFilter;
import org.dashbuilder.dataset.filter.CoreFunctionType;
import org.dashbuilder.dataset.filter.DataSetFilter;
import org.dashbuilder.dataset.filter.FilterFactory;
import org.dashbuilder.displayer.client.events.ColumnFilterChangedEvent;
import org.dashbuilder.displayer.client.events.ColumnFilterDeletedEvent;
import org.dashbuilder.displayer.client.events.DataSetFilterChangedEvent;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.uberfire.client.mvp.UberView;

@Dependent
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

    View view = null;
    DataSetFilter filter = null;
    DataSetMetadata metadata = null;
    SyncBeanManager beanManager;
    Event<DataSetFilterChangedEvent> changeEvent;

    @Inject
    public DataSetFilterEditor(View view,
                               SyncBeanManager beanManager,
                               Event<DataSetFilterChangedEvent> changeEvent) {
        this.view = view;
        this.beanManager = beanManager;
        this.changeEvent = changeEvent;
        view.init(this);
    }

    @Override
    public Widget asWidget() {
        return view.asWidget();
    }

    public DataSetFilter getFilter() {
        return filter;
    }

    public void init(DataSetFilter filter, DataSetMetadata metadata) {
        this.filter = filter;
        this.metadata = metadata;
        view.showNewFilterHome();
        if (metadata != null) {
            for (int i = 0; i < metadata.getNumberOfColumns(); i++) {
                view.addColumn(metadata.getColumnId(i));
            }
        }

        if (filter != null) {
            for (ColumnFilter columnFilter : filter.getColumnFilterList()) {
                ColumnFilterEditor columnFilterEditor = beanManager.lookupBean(ColumnFilterEditor.class).newInstance();
                columnFilterEditor.init(metadata, columnFilter);
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

        ColumnFilterEditor columnFilterEditor = beanManager.lookupBean(ColumnFilterEditor.class).newInstance();
        columnFilterEditor.init(metadata, columnFilter);
        columnFilterEditor.showFilterConfig();

        view.addColumnFilterEditor(columnFilterEditor);
        view.resetSelectedColumn();
        view.showNewFilterHome();
        changeEvent.fire(new DataSetFilterChangedEvent(filter));
    }

    protected void onColumnFilterChanged(@Observes ColumnFilterChangedEvent event) {
        changeEvent.fire(new DataSetFilterChangedEvent(filter));
    }

    protected void onColumnFilterDeleted(@Observes final ColumnFilterDeletedEvent event) {
        ColumnFilterEditor editor = event.getColumnFilterEditor();
        view.removeColumnFilterEditor(editor);
        view.showNewFilterHome();
        beanManager.destroyBean(editor);

        Integer index = filter.getColumnFilterIdx(editor.getFilter());
        if (index != null) {
            filter.getColumnFilterList().remove(index.intValue());
            changeEvent.fire(new DataSetFilterChangedEvent(filter));
        }
    }
}
