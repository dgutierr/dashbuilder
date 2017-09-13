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

import javax.inject.Inject;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.OptionElement;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import org.dashbuilder.dataset.sort.SortOrder;
import org.jboss.errai.common.client.dom.Anchor;
import org.jboss.errai.common.client.dom.DOMUtil;
import org.jboss.errai.common.client.dom.Node;
import org.jboss.errai.common.client.dom.Option;
import org.jboss.errai.common.client.dom.Select;
import org.jboss.errai.ui.client.local.api.IsElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;

@Templated
public class ColumnSortEditorView implements ColumnSortEditor.View, IsElement {

    @Inject
    @DataField
    Select columnSelect;

    @Inject
    @DataField
    Select orderSelect;

    @Inject
    @DataField
    Anchor removeAnchor;

    ColumnSortEditor presenter;

    @Override
    public void init(ColumnSortEditor presenter) {
        this.presenter = presenter;
    }

    @Override
    public void clearColumnList() {
        DOMUtil.removeAllChildren(columnSelect);
    }

    @Override
    public void clearOrderList() {
        DOMUtil.removeAllChildren(orderSelect);
    }

    @Override
    public void addColumn(String column, boolean selected) {
        OptionElement ol = Document.get().createOptionElement();
        ol.setValue(column);
        ol.setText(column);
        ol.setSelected(selected);
        columnSelect.appendChild((Node) ol);
    }

    @Override
    public void addOrder(String order, boolean selected) {
        OptionElement ol = Document.get().createOptionElement();
        ol.setValue(order);
        ol.setText(order);
        ol.setSelected(selected);
        orderSelect.appendChild((Node) ol);
    }

    @EventHandler("columnSelect")
    private void onColumnChange(ChangeEvent event) {
        OptionElement ol = (OptionElement) columnSelect.getOptions().item(columnSelect.getSelectedIndex());
        presenter.onColumnChange(ol.getValue());
    }

    @EventHandler("orderSelect")
    private void onOrderChange(ChangeEvent event) {
        OptionElement ol = (OptionElement) orderSelect.getOptions().item(orderSelect.getSelectedIndex());
        presenter.onOrderChange(ol.getValue());
    }

    @EventHandler("removeAnchor")
    private void onDelete(ClickEvent event) {
        presenter.onRemove();
    }
}
