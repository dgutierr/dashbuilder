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
package org.dashbuilder.displayer.client.widgets;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.dashbuilder.displayer.DisplayerSubType;
import org.dashbuilder.displayer.DisplayerType;
import org.dashbuilder.displayer.client.events.DisplayerTypeSelectedEvent;
import org.uberfire.client.mvp.UberView;

@Dependent
public class DisplayerTypeSelector implements IsWidget {

    public interface View extends UberView<DisplayerTypeSelector> {
        void clear();
        void show(DisplayerType type);
        void select(DisplayerType type);
    }

    View view = null;
    DisplayerType selectedType = DisplayerType.BARCHART;
    DisplayerSubtypeSelector subtypeSelector;
    Event<DisplayerTypeSelectedEvent> typeSelectedEvent;

    @Inject
    public DisplayerTypeSelector(View view,
                                 DisplayerSubtypeSelector subtypeSelector,
                                 Event<DisplayerTypeSelectedEvent> typeSelectedEvent) {
        this.view = view;
        this.subtypeSelector = subtypeSelector;
        this.typeSelectedEvent = typeSelectedEvent;
    }

    @Override
    public Widget asWidget() {
        return view.asWidget();
    }

    public DisplayerType getSelectedType() {
        return selectedType;
    }

    public DisplayerSubType getSelectedSubType() {
        return subtypeSelector.getSelectedSubtype();
    }

    public DisplayerSubtypeSelector getSubtypeSelector() {
        return subtypeSelector;
    }

    @PostConstruct
    protected void init() {
        view.init(this);
        view.clear();
        view.show(DisplayerType.BARCHART);
        view.show(DisplayerType.PIECHART);
        view.show(DisplayerType.LINECHART);
        view.show(DisplayerType.AREACHART);
        view.show(DisplayerType.BUBBLECHART);
        view.show(DisplayerType.METERCHART);
        view.show(DisplayerType.METRIC);
        view.show(DisplayerType.MAP);
        view.show(DisplayerType.TABLE);
        view.select(selectedType);
    }

    public void select(DisplayerType selectedType, DisplayerSubType selectedSubtype) {
        this.selectedType = selectedType;
        view.select(selectedType);
        subtypeSelector.select(selectedType, selectedSubtype);
    }

    void onSelect(DisplayerType type) {
        selectedType = type;
        subtypeSelector.select(type, null);
        typeSelectedEvent.fire(new DisplayerTypeSelectedEvent(selectedType));
    }
}
