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
package org.dashbuilder.displayer.client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gwt.user.client.ui.Widget;
import org.dashbuilder.common.client.StringUtils;
import org.dashbuilder.common.client.error.ClientRuntimeError;
import org.dashbuilder.dataset.ColumnType;
import org.dashbuilder.dataset.DataColumn;
import org.dashbuilder.dataset.DataSet;
import org.dashbuilder.dataset.ValidationError;
import org.dashbuilder.dataset.client.DataSetReadyCallback;
import org.dashbuilder.dataset.date.DayOfWeek;
import org.dashbuilder.dataset.date.Month;
import org.dashbuilder.dataset.filter.DataSetFilter;
import org.dashbuilder.dataset.group.ColumnGroup;
import org.dashbuilder.dataset.group.DataSetGroup;
import org.dashbuilder.dataset.group.DateIntervalPattern;
import org.dashbuilder.dataset.group.DateIntervalType;
import org.dashbuilder.dataset.group.GroupStrategy;
import org.dashbuilder.dataset.group.Interval;
import org.dashbuilder.dataset.sort.SortOrder;
import org.dashbuilder.displayer.ColumnSettings;
import org.dashbuilder.displayer.DisplayerConstraints;
import org.dashbuilder.displayer.DisplayerSettings;
import org.dashbuilder.displayer.client.formatter.ValueFormatter;

/**
 * Base class for implementing custom displayers.
 * <p>Any derived class must implement:
 * <ul>
 *     <li>The draw(), redraw() & close() methods.</li>
 *     <li>The capture of events coming from the DisplayerListener interface.</li>
 * </ul>
 */
public abstract class AbstractDisplayer<V extends DisplayerView> implements Displayer {

    protected DataSet dataSet;
    protected DataSetHandler dataSetHandler;
    protected DisplayerSettings displayerSettings;
    protected DisplayerConstraints displayerConstraints;
    protected List<DisplayerListener> listenerList = new ArrayList<DisplayerListener>();
    protected Map<String,List<Interval>> columnSelectionMap = new HashMap<String,List<Interval>>();
    protected Map<String,ValueFormatter> formatterMap = new HashMap<String, ValueFormatter>();
    protected DataSetFilter currentFilter = null;
    protected boolean refreshEnabled = true;
    protected boolean drawn = false;

    @Override
    public Widget asWidget() {
        return getView().asWidget();
    }

    /**
     * The implementation of the DisplayerView interface (to be provide by the displayer implementation)
     */
    public abstract V getView();

    /**
     * The constrains of this displayer (to be provide by the displayer implementation)
     */
    public abstract DisplayerConstraints createDisplayerConstraints();

    /**
     * The Create the widget used by concrete displayer implementation.
     */
    protected abstract void createVisualization();

    /**
     * Update the widget used by concrete Google displayer implementation.
     */
    protected abstract void updateVisualization();

    public DisplayerConstraints getDisplayerConstraints() {
        if (displayerConstraints == null) {
            displayerConstraints = createDisplayerConstraints();
        }
        return displayerConstraints;
    }

    public DisplayerSettings getDisplayerSettings() {
        return displayerSettings;
    }

    public void setDisplayerSettings(DisplayerSettings displayerSettings) {
        checkDisplayerSettings(displayerSettings);
        this.displayerSettings = displayerSettings;
    }

    public void checkDisplayerSettings(DisplayerSettings displayerSettings) {
        DisplayerConstraints constraints = getDisplayerConstraints();
        if (displayerConstraints != null) {
            ValidationError error = constraints.check(displayerSettings);
            if (error != null) {
                throw error;
            }
        }
    }

    public DataSetHandler getDataSetHandler() {
        return dataSetHandler;
    }

    public void setDataSetHandler(DataSetHandler dataSetHandler) {
        this.dataSetHandler = dataSetHandler;
    }

    public void addListener(DisplayerListener... listeners) {
        for (DisplayerListener listener : listeners) {
            listenerList.add(listener);
        }
    }

    public String getDisplayerId() {
        String id = displayerSettings.getUUID();
        if (!StringUtils.isBlank(id)) {
            return id;
        }

        id = displayerSettings.getTitle();
        if (!StringUtils.isBlank(id)) {
            int hash = id.hashCode();
            return Integer.toString(hash < 0 ? hash*-1 : hash);
        }
        return null;
    }

    // DRAW & REDRAW

    @Override
    public boolean isDrawn() {
        return drawn;
    }

    /**
     * Draw the displayer by executing first the lookup call to retrieve the target data set
     */
    @Override
    public void draw() {
        if (displayerSettings == null) {
            getView().errorMissingSettings();
        }
        else if (dataSetHandler == null) {
            getView().errorMissingHandler();
        }
        else if (!isDrawn()) {
            try {
                drawn = true;
                getView().showLoading();

                beforeLoad();
                beforeDataSetLookup();
                dataSetHandler.lookupDataSet(new DataSetReadyCallback() {
                    public void callback(DataSet result) {
                        try {
                            dataSet = result;
                            afterDataSetLookup(result);
                            createVisualization();

                            // Set the id of the container panel so that the displayer can be easily located
                            // by testing tools for instance.
                            String id = getDisplayerId();
                            if (!StringUtils.isBlank(id)) {
                                getView().setId(id);
                            }
                            // Draw done
                            afterDraw();
                        } catch (Exception e) {
                            // Give feedback on any initialization error
                            showError(new ClientRuntimeError(e));
                        }
                    }
                    public void notFound() {
                        getView().errorDataSetNotFound(displayerSettings.getDataSetLookup().getDataSetUUID());
                    }

                    @Override
                    public boolean onError(final ClientRuntimeError error) {
                        showError(error);
                        return false;
                    }
                });
            } catch (Exception e) {
                showError(new ClientRuntimeError(e));
            }
        }
    }

    /**
     * Just reload the data set and make the current displayer to redraw.
     */
    @Override
    public void redraw() {
        if (!isDrawn()) {
            draw();
        } else {
            try {
                beforeLoad();
                beforeDataSetLookup();
                dataSetHandler.lookupDataSet(new DataSetReadyCallback() {
                    public void callback(DataSet result) {
                        try {
                            dataSet = result;
                            afterDataSetLookup(result);
                            updateVisualization();

                            // Redraw done
                            afterRedraw();
                        } catch (Exception e) {
                            // Give feedback on any initialization error
                            showError(new ClientRuntimeError(e));
                        }
                    }
                    public void notFound() {
                        String uuid = displayerSettings.getDataSetLookup().getDataSetUUID();
                        getView().errorDataSetNotFound(uuid);
                        afterError("Data set not found: " + uuid);
                    }

                    @Override
                    public boolean onError(final ClientRuntimeError error) {
                        showError(error);
                        return false;
                    }
                });
            } catch (Exception e) {
                showError(new ClientRuntimeError(e));
            }
        }
    }

    public void showError(ClientRuntimeError error) {
        getView().error(error);
        afterError(error);
    }

    /**
     * Close the displayer
     */
    @Override
    public void close() {
        getView().clear();

        // Close done
        afterClose();
    }

    /**
     * Call back method invoked just before the data set lookup is executed.
     */
    protected void beforeDataSetLookup() {
    }

    /**
     * Call back method invoked just after the data set lookup is executed.
     */
    protected void afterDataSetLookup(DataSet dataSet) {
    }

    // REFRESH TIMER

    @Override
    public void setRefreshOn(boolean enabled) {
        boolean changed = enabled != refreshEnabled;
        refreshEnabled = enabled;
        if (changed) {
            updateRefreshTimer();
        }
    }

    @Override
    public boolean isRefreshOn() {
        return refreshEnabled;
    }

    protected void updateRefreshTimer() {
        if (isDrawn()) {
            int seconds = displayerSettings.getRefreshInterval();
            if (refreshEnabled && seconds > 0) {
                getView().enableRefreshTimer(seconds);
            } else {
                getView().cancelRefreshTimer();
            }
        }
    }

    // LIFECYCLE CALLBACKS

    protected void beforeLoad() {
        for (DisplayerListener listener : listenerList) {
            listener.onDataLookup(this);
        }
    }

    protected void afterDraw() {
        updateRefreshTimer();
        for (DisplayerListener listener : listenerList) {
            listener.onDraw(this);
        }
    }

    protected void afterRedraw() {
        updateRefreshTimer();
        for (DisplayerListener listener : listenerList) {
            listener.onRedraw(this);
        }
    }

    protected void afterClose() {
        setRefreshOn(false);

        for (DisplayerListener listener : listenerList) {
            listener.onClose(this);
        }
    }

    protected void afterError(final String message) {
        afterError(new ClientRuntimeError(message, null));
    }

    protected void afterError(final String message, final Throwable error) {
        afterError(new ClientRuntimeError(message, error));
    }
    protected void afterError(final Throwable error) {
        afterError(new ClientRuntimeError(error));
    }

    protected void afterError(final ClientRuntimeError error) {
        for (DisplayerListener listener : listenerList) {
            listener.onError(this, error);
        }
    }

    // CAPTURE EVENTS RECEIVED FROM OTHER DISPLAYERS

    @Override
    public void onDataLookup(Displayer displayer) {
        // Do nothing
    }

    @Override
    public void onDraw(Displayer displayer) {
        // Do nothing
    }

    @Override
    public void onRedraw(Displayer displayer) {
        // Do nothing
    }

    @Override
    public void onClose(Displayer displayer) {
        // Do nothing
    }

    @Override
    public void onError(final Displayer displayer, ClientRuntimeError error) {
        // Do nothing
    }

    @Override
    public void onFilterEnabled(Displayer displayer, DataSetGroup groupOp) {
        if (displayerSettings.isFilterListeningEnabled()) {
            if (dataSetHandler.filter(groupOp)) {
                redraw();
            }
        }
    }

    @Override
    public void onFilterEnabled(Displayer displayer, DataSetFilter filter) {
        if (displayerSettings.isFilterListeningEnabled()) {
            if (dataSetHandler.filter(filter)) {
                redraw();
            }
        }
    }

    @Override
    public void onFilterReset(Displayer displayer, List<DataSetGroup> groupOps) {
        if (displayerSettings.isFilterListeningEnabled()) {
            boolean applied = false;
            for (DataSetGroup groupOp : groupOps) {
                if (dataSetHandler.unfilter(groupOp)) {
                    applied = true;
                }
            }
            if (applied) {
                redraw();
            }
        }
    }

    @Override
    public void onFilterReset(Displayer displayer, DataSetFilter filter) {
        if (displayerSettings.isFilterListeningEnabled()) {
            if (dataSetHandler.unfilter(filter)) {
                redraw();
            }
        }
    }

    // DATA COLUMN VALUES SELECTION, FILTER & NOTIFICATION

    /**
     * Get the set of columns being filtered.
     */
    public Set<String> filterColumns() {
        return columnSelectionMap.keySet();
    }

    /**
     * Get the current filter intervals for the given data set column.
     *
     * @param columnId The column identifier.
     * @return A list of intervals.
     */
    public List<Interval> filterIntervals(String columnId) {
        List<Interval> selected = columnSelectionMap.get(columnId);
        if (selected == null) return new ArrayList<Interval>();
        return selected;
    }

    /**
     * Get the current filter selected interval indexes for the given data set column.
     *
     * @param columnId The column identifier.
     * @return A list of interval indexes
     */
    public List<Integer> filterIndexes(String columnId) {
        List<Integer> result = new ArrayList<Integer>();
        List<Interval> selected = columnSelectionMap.get(columnId);
        if (selected == null) return result;

        for (Interval interval : selected) {
            result.add(interval.getIndex());
        }
        return result;
    }

    /**
     * Updates the current filter values for the given data set column.
     *
     * @param columnId The column to filter for.
     * @param row The row selected.
     */
    public void filterUpdate(String columnId, int row) {
        filterUpdate(columnId, row, null);
    }

    /**
     * Updates the current filter values for the given data set column.
     *
     * @param columnId The column to filter for.
     * @param row The row selected.
     * @param maxSelections The number of different selectable values available.
     */
    public void filterUpdate(String columnId, int row, Integer maxSelections) {
        if (displayerSettings.isFilterEnabled()) {

            Interval intervalSelected = dataSetHandler.getInterval(columnId, row);
            if (intervalSelected != null) {

                List<Interval> selectedIntervals = columnSelectionMap.get(columnId);
                if (selectedIntervals == null) {
                    selectedIntervals = new ArrayList<Interval>();
                    selectedIntervals.add(intervalSelected);
                    columnSelectionMap.put(columnId, selectedIntervals);
                    filterApply(columnId, selectedIntervals);
                }
                else if (selectedIntervals.contains(intervalSelected)) {
                    selectedIntervals.remove(intervalSelected);
                    if (!selectedIntervals.isEmpty()) {
                        filterApply(columnId, selectedIntervals);
                    }
                    else {
                        filterReset(columnId);
                    }
                }
                else {
                    if (displayerSettings.isFilterSelfApplyEnabled()) {
                        columnSelectionMap.put(columnId, selectedIntervals = new ArrayList<Interval>());
                    }
                    selectedIntervals.add(intervalSelected);
                    if (maxSelections != null && maxSelections > 0 && selectedIntervals.size() >= maxSelections) {
                        filterReset(columnId);
                    }
                    else {
                        filterApply(columnId, selectedIntervals);
                    }
                }
            }
        }
    }

    /**
     * Filter the values of the given column.
     *
     * @param columnId The name of the column to filter.
     * @param intervalList A list of interval selections to filter for.
     */
    public void filterApply(String columnId, List<Interval> intervalList) {
        if (displayerSettings.isFilterEnabled()) {

            // For string column filters, init the group interval selection operation.
            DataSetGroup groupOp = dataSetHandler.getGroupOperation(columnId);
            groupOp.setSelectedIntervalList(intervalList);

            // Notify to those interested parties the selection event.
            if (displayerSettings.isFilterNotificationEnabled()) {
                for (DisplayerListener listener : listenerList) {
                    listener.onFilterEnabled(this, groupOp);
                }
            }
            // Drill-down support
            if (displayerSettings.isFilterSelfApplyEnabled()) {
                dataSetHandler.drillDown(groupOp);
                redraw();
            }
        }
    }

    /**
     * Apply the given filter
     *
     * @param filter A filter
     */
    public void filterApply(DataSetFilter filter) {
        if (displayerSettings.isFilterEnabled()) {

            this.currentFilter = filter;

            // Notify to those interested parties the selection event.
            if (displayerSettings.isFilterNotificationEnabled()) {
                for (DisplayerListener listener : listenerList) {
                    listener.onFilterEnabled(this, filter);
                }
            }
            // Drill-down support
            if (displayerSettings.isFilterSelfApplyEnabled()) {
                dataSetHandler.filter(filter);
                redraw();
            }
        }
    }

    /**
     * Clear any filter on the given column.
     *
     * @param columnId The name of the column to reset.
     */
    public void filterReset(String columnId) {
        if (displayerSettings.isFilterEnabled()) {

            columnSelectionMap.remove(columnId);
            DataSetGroup groupOp = dataSetHandler.getGroupOperation(columnId);

            // Notify to those interested parties the reset event.
            if (displayerSettings.isFilterNotificationEnabled()) {
                for (DisplayerListener listener : listenerList) {
                    listener.onFilterReset(this, Arrays.asList(groupOp));
                }
            }
            // Apply the selection to this displayer
            if (displayerSettings.isFilterSelfApplyEnabled()) {
                dataSetHandler.drillUp(groupOp);
                redraw();
            }
        }
    }

    /**
     * Clear any filter.
     */
    public void filterReset() {
        if (displayerSettings.isFilterEnabled()) {

            List<DataSetGroup> groupOpList = new ArrayList<DataSetGroup>();
            for (String columnId : columnSelectionMap.keySet()) {
                DataSetGroup groupOp = dataSetHandler.getGroupOperation(columnId);
                groupOpList.add(groupOp);

            }
            columnSelectionMap.clear();

            // Notify to those interested parties the reset event.
            if (displayerSettings.isFilterNotificationEnabled()) {
                for (DisplayerListener listener : listenerList) {
                    if (currentFilter != null) {
                        listener.onFilterReset(this, currentFilter);
                    }
                    listener.onFilterReset(this, groupOpList);
                }
            }
            // Apply the selection to this displayer
            if (displayerSettings.isFilterSelfApplyEnabled()) {
                boolean applied = false;

                if (currentFilter != null) {
                    if (dataSetHandler.unfilter(currentFilter)) {
                        applied = true;
                    }
                }
                for (DataSetGroup groupOp : groupOpList) {
                    if (dataSetHandler.drillUp(groupOp)) {
                        applied = true;
                    }
                }
                if (applied) {
                    redraw();
                }
            }
            if (currentFilter != null) {
                currentFilter = null;
            }
        }
    }

    // DATA COLUMN SORT

    /**
     * Set the sort order operation to apply to the data set.
     *
     * @param columnId The name of the column to sort.
     * @param sortOrder The sort order.
     */
    public void sortApply(String columnId, SortOrder sortOrder) {
        dataSetHandler.sort(columnId, sortOrder);
    }


    // DATA FORMATTING

    public String formatInterval(Interval interval, DataColumn column) {

        // Raw values
        if (column == null || column.getColumnGroup() == null) {
            return interval.getName();
        }
        // Date interval
        String type = interval.getType();
        if (StringUtils.isBlank(type)) type = column.getIntervalType();
        if (StringUtils.isBlank(type)) type = column.getColumnGroup().getIntervalSize();
        DateIntervalType intervalType = DateIntervalType.getByName(type);
        if (intervalType != null) {
            ColumnSettings columnSettings = displayerSettings.getColumnSettings(column.getId());
            String pattern = columnSettings != null ? columnSettings.getValuePattern() : ColumnSettings.getDatePattern(intervalType);
            String expression = columnSettings != null ? columnSettings.getValueExpression() : null;

            if (pattern == null) {
                pattern = ColumnSettings.getDatePattern(intervalType);
            }
            if (expression == null && column.getColumnGroup().getStrategy().equals(GroupStrategy.FIXED)) {
                expression = ColumnSettings.getFixedExpression(intervalType);
            }

            return formatDate(intervalType,
                    column.getColumnGroup().getStrategy(),
                    interval.getName(), pattern, expression);
        }
        // Label interval
        ColumnSettings columnSettings = displayerSettings.getColumnSettings(column);
        String expression = columnSettings.getValueExpression();
        if (StringUtils.isBlank(expression)) return interval.getName();
        return getView().applyExpression(interval.getName(), expression);
    }

    public void addFormatter(String columnId, ValueFormatter formatter) {
        formatterMap.put(columnId, formatter);
    }

    public ValueFormatter getFormatter(String columnId) {
        return formatterMap.get(columnId);
    }

    public String formatValue(int row, int column) {
        Object value = dataSet.getValueAt(row, column);
        DataColumn columnObj = dataSet.getColumnByIndex(column);

        ValueFormatter formatter = getFormatter(columnObj.getId());
        if (formatter != null) {
            return formatter.formatValue(dataSet, row, column);
        }
        return formatValue(value, columnObj);
    }

    public String formatValue(Object value, DataColumn column) {

        ValueFormatter formatter = getFormatter(column.getId());
        if (formatter != null) {
            return formatter.formatValue(value);
        }

        ColumnSettings columnSettings = displayerSettings.getColumnSettings(column);
        String pattern = columnSettings.getValuePattern();
        String empty = columnSettings.getEmptyTemplate();
        String expression = columnSettings.getValueExpression();

        if (value == null) {
            return empty;
        }

        // Date grouped columns
        DateIntervalType intervalType = DateIntervalType.getByName(column.getIntervalType());
        if (intervalType != null) {
            ColumnGroup columnGroup = column.getColumnGroup();
            return formatDate(intervalType,
                    columnGroup.getStrategy(),
                    value.toString(), pattern, expression);
        }
        // Label grouped columns, aggregations & raw values
        else {
            ColumnType columnType = column.getColumnType();
            if (ColumnType.DATE.equals(columnType)) {
                Date d = (Date) value;
                return getView().formatDate(pattern, d);
            }
            else if (ColumnType.NUMBER.equals(columnType)) {
                double n = ((Number) value).doubleValue();
                if (!StringUtils.isBlank(expression)) {
                    String r = getView().applyExpression(value.toString(), expression);
                    try {
                        n = Double.parseDouble(r);
                    } catch (NumberFormatException e) {
                        return r;
                    }
                }
                return getView().formatNumber(pattern, n);
            }
            else {
                if (StringUtils.isBlank(expression)) {
                    return value.toString();
                }
                return getView().applyExpression(value.toString(), expression);
            }
        }
    }

    // DATE FORMATTING

    protected String formatDate(DateIntervalType type, GroupStrategy strategy, String date, String pattern, String expression) {
        if (date == null) {
            return null;
        }
        String str = GroupStrategy.FIXED.equals(strategy) ? formatDateFixed(type, date) : formatDateDynamic(type, date, pattern);
        if (StringUtils.isBlank(expression)) {
            return str;
        }
        return getView().applyExpression(str, expression);
    }

    protected String formatDateFixed(DateIntervalType type, String date) {
        if (date == null) {
            return null;
        }
        int index = Integer.parseInt(date);
        if (DateIntervalType.DAY_OF_WEEK.equals(type)) {
            DayOfWeek dayOfWeek = DayOfWeek.getByIndex(index);
            return getView().formatDayOfWeek(dayOfWeek);
        }
        if (DateIntervalType.MONTH.equals(type)) {
            Month month = Month.getByIndex(index);
            return getView().formatMonth(month);
        }
        return date;
    }

    protected String formatDateDynamic(DateIntervalType type, String date, String pattern) {
        if (date == null) return null;
        Date d = parseDynamicGroupDate(type, date);
        return getView().formatDate(pattern, d);
    }

    protected Date parseDynamicGroupDate(DateIntervalType type, String date) {
        String pattern = DateIntervalPattern.getPattern(type);
        return getView().parseDate(pattern, date);
    }
}