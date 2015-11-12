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
package org.dashbuilder.displayer.client.widgets.filter;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.dashbuilder.dataset.ColumnType;
import org.dashbuilder.dataset.DataSetMetadata;
import org.dashbuilder.dataset.date.TimeFrame;
import org.dashbuilder.dataset.filter.ColumnFilter;
import org.dashbuilder.dataset.filter.CoreFunctionFilter;
import org.dashbuilder.dataset.filter.CoreFunctionType;
import org.dashbuilder.dataset.filter.DataSetFilter;
import org.dashbuilder.dataset.filter.FilterFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.uberfire.mvp.Command;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class DataSetFilterEditorTest {

    @Mock
    ColumnFilterEditor columnFilterEditor;

    @Mock
    DataSetFilterEditor.View filterView;

    @Mock
    DataSetMetadata metadata;

    @Mock
    Command changeCommand;


    public class MockFactory extends DataSetFilterEditor.Factory {

        @Override
        public ColumnFilterEditor createColumnFilterEditor(DataSetMetadata metadata, ColumnFilter filter) {
            return columnFilterEditor;
        }
    }

    @Before
    public void setup() {
        when(metadata.getNumberOfColumns()).thenReturn(3);
        when(metadata.getColumnId(0)).thenReturn("column1");
        when(metadata.getColumnId(1)).thenReturn("column2");
        when(metadata.getColumnId(2)).thenReturn("column3");
        when(metadata.getColumnType(0)).thenReturn(ColumnType.LABEL);
        when(metadata.getColumnType(1)).thenReturn(ColumnType.NUMBER);
        when(metadata.getColumnType(2)).thenReturn(ColumnType.DATE);
    }

    @Test
    public void testViewInitialization() {
        DataSetFilter filter = new DataSetFilter();
        ColumnFilter filter1 = FilterFactory.equalsTo("column1", "Test");
        filter.addFilterColumn(filter1);

        DataSetFilterEditor filterEditor = new DataSetFilterEditor(filterView, new MockFactory(), filter, metadata, changeCommand);

        assertEquals(filterView, filterEditor.view);
        verify(filterView).showNewFilterHome();
        verify(filterView).addColumn("column1");
        verify(filterView).addColumn("column2");
        verify(filterView).addColumn("column3");
        verify(filterView, times(filter.getColumnFilterList().size())).addColumnFilterEditor(any(ColumnFilterEditor.class));
    }

    @Test
    public void testWorkflow() {
        DataSetFilterEditor filterEditor = new DataSetFilterEditor(filterView, new MockFactory(), null, metadata, changeCommand);
        reset(filterView);

        filterEditor.newFilterStart();
        verify(filterView).showColumnSelector();

        filterEditor.newFilterCancel();
        verify(filterView).showNewFilterHome();
    }

    @Test
    public void testCreateLabelFilter() {
        DataSetFilterEditor filterEditor = new DataSetFilterEditor(filterView, new MockFactory(), null, metadata, changeCommand);
        reset(filterView);
        when(filterView.getSelectedColumnIndex()).thenReturn(0);

        filterEditor.createFilter();
        verify(changeCommand).execute();

        DataSetFilter filter = filterEditor.getFilter();
        assertNotNull(filter);
        assertEquals(filter.getColumnFilterList().size(), 1);

        ColumnFilter expected = FilterFactory.createCoreFunctionFilter("column1", ColumnType.LABEL, CoreFunctionType.NOT_EQUALS_TO);
        assertEquals(filter.getColumnFilterList().get(0), expected);
    }

    @Test
    public void testCreateDateFilter() {
        DataSetFilterEditor filterEditor = new DataSetFilterEditor(filterView, new MockFactory(), null, metadata, changeCommand);
        reset(filterView);
        when(filterView.getSelectedColumnIndex()).thenReturn(2);

        filterEditor.createFilter();
        verify(changeCommand).execute();

        DataSetFilter filter = filterEditor.getFilter();
        assertNotNull(filter);
        assertEquals(filter.getColumnFilterList().size(), 1);

        ColumnFilter expected = FilterFactory.createCoreFunctionFilter("column3", ColumnType.DATE, CoreFunctionType.TIME_FRAME);
        assertEquals(filter.getColumnFilterList().get(0), expected);
    }
}