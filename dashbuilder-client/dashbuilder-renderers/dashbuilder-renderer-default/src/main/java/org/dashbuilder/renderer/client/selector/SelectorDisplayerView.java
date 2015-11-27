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
package org.dashbuilder.renderer.client.selector;

import com.google.gwt.dom.client.NodeList;
import com.google.gwt.dom.client.OptionElement;
import com.google.gwt.dom.client.SelectElement;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import org.dashbuilder.displayer.client.AbstractDisplayerView;
import org.dashbuilder.renderer.client.resources.i18n.SelectorConstants;
import org.gwtbootstrap3.client.ui.ListBox;

public class SelectorDisplayerView extends AbstractDisplayerView<SelectorDisplayer> implements SelectorDisplayer.View {

    protected ListBox listBox = new ListBox();
    protected boolean hintEnabled = false;

    @Override
    public void init(SelectorDisplayer presenter) {
        super.setPresenter(presenter);
        super.setRootWidget(listBox);
    }

    @Override
    public void showSelectHint(String column) {
        showHint("- " + SelectorConstants.INSTANCE.selectorDisplayer_select() + " " + column + " -");
    }

    @Override
    public void showResetHint(String column) {
        showHint("- " + SelectorConstants.INSTANCE.selectorDisplayer_reset() + " " + column + " -");
    }

    protected void showHint(String hint) {
        if (hintEnabled) {
            SelectElement selectElement = SelectElement.as(listBox.getElement());
            NodeList<OptionElement> options = selectElement.getOptions();
            options.getItem(0).setText(hint);
        } else {
            listBox.addItem(hint);
            hintEnabled = true;
        }
    }

    @Override
    public void addItem(String id, String value) {
        listBox.addItem(value, id);
    }

    @Override
    public void setSelectedIndex(int index) {
        listBox.setSelectedIndex(index + (hintEnabled ? 1 : 0));
    }

    @Override
    public int getSelectedIndex() {
        return listBox.getSelectedIndex() - (hintEnabled ? 1 : 0);
    }

    @Override
    public int getItemCount() {
        return listBox.getItemCount() - (hintEnabled ? 1 : 0);
    }

    @Override
    public void setItemTitle(int index, String title) {
        SelectElement selectElement = SelectElement.as(listBox.getElement());
        NodeList<OptionElement> options = selectElement.getOptions();
        OptionElement optionElement = options.getItem(index + (hintEnabled ? 1: 0));
        if (optionElement != null) {
            optionElement.setTitle(title);
        }
    }

    @Override
    public void setFilterEnabled(boolean enabled) {
        if (enabled) {
            listBox.addChangeHandler(new ChangeHandler() {
                public void onChange(ChangeEvent event) {
                    getPresenter().onItemSelected();
                }
            });
        }
    }

    @Override
    public String getGroupsTitle() {
        return SelectorConstants.INSTANCE.selectorDisplayer_groupsTitle();
    }

    @Override
    public String getColumnsTitle() {
        return SelectorConstants.INSTANCE.selectorDisplayer_columnsTitle();
    }
}
