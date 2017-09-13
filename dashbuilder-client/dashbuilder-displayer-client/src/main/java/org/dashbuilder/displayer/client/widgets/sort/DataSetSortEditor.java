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

import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.dashbuilder.dataset.DataSetMetadata;
import org.dashbuilder.dataset.sort.ColumnSort;
import org.dashbuilder.dataset.sort.DataSetSort;
import org.dashbuilder.dataset.sort.SortOrder;
import org.dashbuilder.displayer.client.events.DataSetSortChangedEvent;
import org.jboss.errai.common.client.api.IsElement;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.uberfire.client.mvp.UberElement;

public class DataSetSortEditor implements IsElement {

    public interface View extends UberElement<DataSetSortEditor> {

        void addColumnSortEditor(HTMLElement editor);
    }

    private View view;
    DataSetSort sortOp = null;
    DataSetMetadata metadata = null;
    SyncBeanManager beanManager;
    Event<DataSetSortChangedEvent> changeEvent;

    @Inject
    public DataSetSortEditor(View view, SyncBeanManager beanManager, Event<DataSetSortChangedEvent> changeEvent) {
        this.view = view;
        this.beanManager = beanManager;
        this.changeEvent = changeEvent;
        this.view = view;
        this.view.init(this);
    }

    @Override
    public HTMLElement getElement() {
        return view.getElement();
    }

    public void init(DataSetSort sortOp, DataSetMetadata metadata) {
        this.sortOp = sortOp;
        this.metadata = metadata;
        for (ColumnSort columnSort : sortOp.getColumnSortList()) {

        }


    }

    public void createColumnSort() {
        ColumnSortEditor columnSortEditor = beanManager.lookupBean(ColumnSortEditor.class).newInstance();
        columnSortEditor.init(null, null, SortOrder.ASCENDING);
        view.addColumnSortEditor(columnSortEditor.getElement());
    }

    // View notifications

    public void onAddNew() {
        ColumnSortEditor columnSortEditor = beanManager.lookupBean(ColumnSortEditor.class).newInstance();
        columnSortEditor.init(null, null, SortOrder.ASCENDING);
        view.addColumnSortEditor(columnSortEditor.getElement());
    }
}
