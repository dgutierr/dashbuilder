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
package org.dashbuilder.renderer.google.client;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;

import org.dashbuilder.dataset.ColumnType;
import org.dashbuilder.dataset.DataColumn;
import org.dashbuilder.dataset.group.Interval;
import org.dashbuilder.displayer.ColumnSettings;
import org.dashbuilder.displayer.client.AbstractDisplayer;
import org.dashbuilder.displayer.client.Displayer;
import org.dashbuilder.displayer.client.DisplayerView;

public abstract class GoogleDisplayer<V extends GoogleDisplayer.View> extends AbstractDisplayer<V> {

    public interface View<P extends GoogleDisplayer> extends DisplayerView<P> {

        void dataClear();

        void dataRowCount(int rowCount);

        void dataAddColumn(ColumnType type, String id, String name);

        void dataSetValue(int row, int column, Date value);

        void dataSetValue(int row, int column, double value);

        void dataSetValue(int row, int column, String value);

        void dataFormatDateColumn(String pattern, int column);

        void dataFormatNumberColumn(String pattern, int column);

        String getGroupsTitle();

        String getColumnsTitle();

        void showTitle(String title);

        void clearFilterStatus();

        void addFilterValue(String value);

        void addFilterReset();
    }

    @Override
    protected void createVisualization() {
        if (displayerSettings.isTitleVisible()) {
            getView().showTitle(displayerSettings.getTitle());
        }
    }

    protected void updateFilterStatus() {
        getView().clearFilterStatus();
        Set<String> columnFilters = filterColumns();
        if (displayerSettings.isFilterEnabled() && !columnFilters.isEmpty()) {

            for (String columnId : columnFilters) {
                List<Interval> selectedValues = filterIntervals(columnId);
                DataColumn column = dataSet.getColumnById(columnId);
                for (Interval interval : selectedValues) {
                    String formattedValue = formatInterval(interval, column);
                    getView().addFilterValue(formattedValue);
                }
            }
            getView().addFilterReset();
        }
    }

    // Data generation

    public void populateData() {

        getView().dataClear();
        getView().dataRowCount(dataSet.getRowCount());

        List<DataColumn> columns = dataSet.getColumns();
        for (int i = 0; i < columns.size(); i++) {
            DataColumn dataColumn = columns.get(i);
            String columnId = dataColumn.getId();
            ColumnType columnType = dataColumn.getColumnType();
            ColumnSettings columnSettings = displayerSettings.getColumnSettings(dataColumn);

            getView().dataAddColumn(dataColumn.getColumnType(), columnSettings.getColumnName(), columnId);

            List columnValues = dataColumn.getValues();
            for (int j = 0; j < columnValues.size(); j++) {
                Object value = columnValues.get(j);

                if (ColumnType.DATE.equals(columnType)) {
                    if (value == null) {
                        getView().dataSetValue(j, i, new Date());
                    }
                    else {
                        getView().dataSetValue(j, i, (Date) value);
                    }
                }
                else if (ColumnType.NUMBER.equals(columnType)) {
                    if (value == null) {
                        getView().dataSetValue(j, i, 0d);
                    } else {
                        value = getView().applyExpression(value.toString(), columnSettings.getValueExpression());
                        getView().dataSetValue(j, i, Double.parseDouble(value.toString()));
                    }
                }
                else {
                    value = super.formatValue(j, i);
                    getView().dataSetValue(j, i, value.toString());
                }
            }
        }

        // Format the table values
        for (int i = 0; i < dataSet.getColumns().size(); i++) {
            DataColumn dataColumn = columns.get(i);
            ColumnSettings columnSettings = displayerSettings.getColumnSettings(dataColumn);
            String pattern = columnSettings.getValuePattern();

            if (ColumnType.DATE.equals(dataColumn.getColumnType())) {
                getView().dataFormatDateColumn(pattern, i);
            }
            else if (ColumnType.NUMBER.equals(dataColumn.getColumnType())) {
                getView().dataFormatNumberColumn(pattern, i);
            }
        }
    }

    // View notifications

    void onFilterResetClicked() {
        filterReset();

        // Update the displayer view in order to reflect the current selection
        // (only if not has already been redrawn in the previous filterUpdate() call)
        if (!displayerSettings.isFilterSelfApplyEnabled()) {
            updateVisualization();
        }
    }





    protected GoogleRendrer googleRenderer;

    public GoogleDisplayer setRenderer(GoogleRenderer googleRenderer) {
        this.googleRenderer = googleRenderer;
        return this;
    }

    /**
     * GCharts drawing is done asynchronously via its renderer (see ready() method below)
     */
    @Override
    public void draw() {
        if (googleRenderer == null)  {
            afterError("Google renderer not set");
        }
        else if (!isDrawn())  {
            List<Displayer> displayerList = new ArrayList<Displayer>();
            displayerList.add(this);
            googleRenderer.draw(displayerList);
        }
    }

    /**
     * Invoked asynchronously by the GoogleRenderer when the displayer is ready for being displayed
     */
    void ready() {
        super.draw();
    }
}
