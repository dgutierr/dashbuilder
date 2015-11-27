/**
 * Copyright 2015 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.dashbuilder.renderer.client.table;

import org.dashbuilder.dataset.ColumnType;
import org.dashbuilder.dataset.filter.FilterFactory;
import org.dashbuilder.dataset.group.AggregateFunctionType;
import org.dashbuilder.dataset.sort.SortOrder;
import org.dashbuilder.displayer.DisplayerSettings;
import org.dashbuilder.displayer.DisplayerSettingsFactory;
import org.dashbuilder.displayer.client.AbstractDisplayerTest;
import org.dashbuilder.displayer.client.DataSetHandlerImpl;
import org.dashbuilder.renderer.client.metric.MetricDisplayer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.dashbuilder.dataset.ExpenseReportsData.*;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class TableDisplayerTest extends AbstractDisplayerTest {

    public TableDisplayer createTableDisplayer(DisplayerSettings settings) {
        return initDisplayer(new TableDisplayer(mock(TableDisplayer.View.class)), settings);
    }

    @Test
    public void testTableDraw() {

        DisplayerSettings allRows = DisplayerSettingsFactory.newTableSettings()
                .dataset(EXPENSES)
                .tableOrderDefault(COLUMN_DEPARTMENT, SortOrder.DESCENDING)
                .tableOrderEnabled(true)
                .tablePageSize(10)
                .tableWidth(1000)
                .filterOn(true, true, true)
                .buildSettings();

        TableDisplayer table = createTableDisplayer(allRows);
        TableDisplayer.View tableView = table.getView();
        table.draw();

        verify(tableView).setWidth(1000);
        verify(tableView).setSortEnabled(true);
        verify(tableView).setTotalRows(50);
        verify(tableView).createTable(10);
        verify(tableView).addColumn(ColumnType.NUMBER, COLUMN_ID, COLUMN_ID, 0, false, true);
        verify(tableView).addColumn(ColumnType.LABEL, COLUMN_CITY, COLUMN_CITY, 1, true, true);
        verify(tableView).addColumn(ColumnType.LABEL, COLUMN_DEPARTMENT, COLUMN_DEPARTMENT, 2, true, true);
        verify(tableView).addColumn(ColumnType.LABEL, COLUMN_EMPLOYEE, COLUMN_EMPLOYEE, 3, true, true);
        verify(tableView).addColumn(ColumnType.DATE, COLUMN_DATE, COLUMN_DATE, 4, false, true);
        verify(tableView).addColumn(ColumnType.NUMBER, COLUMN_AMOUNT, COLUMN_AMOUNT, 5, false, true);
        verify(tableView).gotoFirstPage();
    }

    @Test
    public void testEmptyTableDraw() {

        DisplayerSettings allRows = DisplayerSettingsFactory.newTableSettings()
                .dataset(EXPENSES)
                .filter(COLUMN_ID, FilterFactory.isNull())
                .tablePageSize(10)
                .buildSettings();

        TableDisplayer table = createTableDisplayer(allRows);
        TableDisplayer.View tableView = table.getView();
        table.draw();

        verify(tableView).createTable(10);
        verify(tableView).setTotalRows(0);
        verify(tableView).setPagerEnabled(false);
        verify(tableView, never()).setPagerEnabled(true);

        reset(tableView);
        table.redraw();
        verify(tableView, never()).setPagerEnabled(true);
    }

    @Test
    public void testTableSort() {

        DisplayerSettings allRows = DisplayerSettingsFactory.newTableSettings()
                .dataset(EXPENSES)
                .tablePageSize(5)
                .tableOrderDefault(COLUMN_ID, SortOrder.DESCENDING)
                .buildSettings();

        // Sorted by ID descending by default
        TableDisplayer table = createTableDisplayer(allRows);
        TableDisplayer.View tableView = table.getView();
        table.draw();
        assertEquals(table.getDataSetHandler().getLastDataSet().getValueAt(0, 0), 50d);

        // Sort disabled (no effect)
        allRows.setTableSortEnabled(false);
        table = createTableDisplayer(allRows);
        tableView = table.getView();
        table.draw();
        reset(tableView);
        table.sortBy(COLUMN_ID, SortOrder.DESCENDING);
        verify(tableView, never()).redrawTable();
        assertEquals(table.getDataSetHandler().getLastDataSet().getValueAt(0, 0), 50d);

        // Sort enabled
        allRows.setTableSortEnabled(true);
        table = createTableDisplayer(allRows);
        tableView = table.getView();
        table.draw();
        reset(tableView);
        table.sortBy(COLUMN_ID, SortOrder.ASCENDING);
        verify(tableView).redrawTable();
        assertEquals(table.getDataSetHandler().getLastDataSet().getValueAt(0, 0), 1d);
    }
}