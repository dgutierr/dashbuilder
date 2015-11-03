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

import java.util.Arrays;
import java.util.List;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.dashbuilder.dataset.date.TimeAmount;
import org.dashbuilder.dataset.date.TimeInstant;
import org.dashbuilder.dataset.group.DateIntervalType;
import org.uberfire.client.mvp.UberView;
import org.uberfire.mvp.Command;

public class TimeInstantEditor implements IsWidget {

    interface View extends UberView<TimeInstantEditor> {

        void clearTimeModeSelector();

        void addTimeModeItem(TimeInstant.TimeMode timeMode);

        void setSelectedTimeModeIndex(int index);

        int getTimeModeSelectedIndex();

        void enableIntervalTypeSelector();

        void disableIntervalTypeSelector();

        void clearIntervalTypeSelector();

        void addIntervalTypeItem(DateIntervalType type);

        void setSelectedIntervalTypeIndex(int index);

        int getSelectedIntervalTypeIndex();
    }

    static List<DateIntervalType> INTERVAL_TYPES = Arrays.asList(
            DateIntervalType.MINUTE,
            DateIntervalType.HOUR,
            DateIntervalType.DAY,
            DateIntervalType.MONTH,
            DateIntervalType.QUARTER,
            DateIntervalType.YEAR,
            DateIntervalType.CENTURY,
            DateIntervalType.MILLENIUM);

    View view;
    TimeInstant timeInstant = null;
    TimeAmountEditor timeAmountEditor = null;
    Command onChangeCommand = new Command() { public void execute() {} };

    public TimeInstantEditor(TimeInstant timeInstant) {
        this(new TimeInstantEditorView(),
            new TimeAmountEditor(timeInstant.getTimeAmount()),
            timeInstant);
    }

    public TimeInstantEditor(View view, TimeAmountEditor timeAmountEditor, TimeInstant timeInstant) {
        this.timeInstant = timeInstant != null ? timeInstant : new TimeInstant();
        this.timeAmountEditor = timeAmountEditor;
        this.view = view;
        this.view.init(this);

        init();
    }

    @Override
    public Widget asWidget() {
        return view.asWidget();
    }

    public TimeInstant getTimeInstant() {
        return timeInstant;
    }

    public TimeAmountEditor getTimeAmountEditor() {
        return timeAmountEditor;
    }

    public void setOnChangeCommand(Command onChangeCommand) {
        this.onChangeCommand = onChangeCommand;

        // Propagate any changes coming from composites
        timeAmountEditor.setOnChangeCommand(onChangeCommand);
    }

    public void init() {
        initTimeModeSelector();
        initIntervalTypeSelector();
    }

    protected void initTimeModeSelector() {
        view.clearTimeModeSelector();
        TimeInstant.TimeMode current = timeInstant.getTimeMode();
        TimeInstant.TimeMode[] modes = TimeInstant.TimeMode.values();
        for (int i=0; i<modes.length ; i++) {
            TimeInstant.TimeMode mode = modes[i];
            view.addTimeModeItem(mode);
            if (current != null && current.equals(mode)) {
                view.setSelectedTimeModeIndex(i);
            }
        }
    }

    protected void initIntervalTypeSelector() {
        view.disableIntervalTypeSelector();
        TimeInstant.TimeMode timeMode = timeInstant.getTimeMode();
        if (timeMode != null && !timeMode.equals(TimeInstant.TimeMode.NOW)) {
            view.enableIntervalTypeSelector();
            view.clearIntervalTypeSelector();
            DateIntervalType current = timeInstant.getIntervalType();
            for (int i = 0; i < INTERVAL_TYPES.size(); i++) {
                DateIntervalType type = INTERVAL_TYPES.get(i);
                view.addIntervalTypeItem(type);
                if (current != null && current.equals(type)) {
                    view.setSelectedIntervalTypeIndex(i);
                }
            }
        }
    }

    void changeTimeMode() {
        int selectedIdx = view.getTimeModeSelectedIndex();

        TimeInstant.TimeMode mode = TimeInstant.TimeMode.getByIndex(selectedIdx);
        timeInstant.setTimeMode(mode);
        TimeAmount timeAmount = timeInstant.getTimeAmount();
        if (timeAmount != null) {
            timeAmount.setQuantity(0);
        }

        onChangeCommand.execute();
        initIntervalTypeSelector();
    }

    void changeIntervalType() {
        int selectedIdx = view.getSelectedIntervalTypeIndex();
        DateIntervalType intervalType = INTERVAL_TYPES.get(selectedIdx);
        timeInstant.setIntervalType(intervalType);

        onChangeCommand.execute();
        initIntervalTypeSelector();
    }
}
