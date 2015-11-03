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
import org.dashbuilder.dataset.filter.CoreFunctionFilter;
import org.dashbuilder.dataset.filter.CoreFunctionType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.mvp.Command;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ColumnFilterEditorTest {

    @Mock
    ColumnFilterEditor.View filterView;

    @Mock
    FilterParamEditorFactory paramEditorFactory;

    @Mock
    TextParameterEditor textParameterEditor;

    @Mock
    NumberParameterEditor numberParameterEditor;

    @Mock
    DateParameterEditor dateParameterEditor;

    @Mock
    TimeFrameEditor timeFrameEditor;

    @Mock
    LikeToFunctionEditor likeToFunctionEditor;

    @Mock
    DataSetMetadata metadata;

    @Mock
    Command changeCommand;

    @Mock
    Command deleteCommand;

    @Before
    public void init() {
        when(paramEditorFactory.createTextInputWidget()).thenReturn(textParameterEditor);
        when(paramEditorFactory.createNumberInputWidget()).thenReturn(numberParameterEditor);
        when(paramEditorFactory.createDateInputWidget()).thenReturn(dateParameterEditor);
        when(paramEditorFactory.createLikeToFunctionWidget()).thenReturn(likeToFunctionEditor);
        when(paramEditorFactory.createTimeFrameWidget(any(TimeFrame.class))).thenReturn(timeFrameEditor);
    }

    protected ColumnFilterEditor setupEditor(ColumnType columnType, CoreFunctionType functionType, Comparable... params) {
        when(metadata.getColumnType("col")).thenReturn(columnType);

        CoreFunctionFilter filter = new CoreFunctionFilter("col", functionType, params);
        ColumnFilterEditor filterEditor = new ColumnFilterEditor(filterView, paramEditorFactory, metadata, filter);
        filterEditor.setOnFilterChangeCommand(changeCommand);
        filterEditor.setOnFilterDeleteCommand(deleteCommand);

        assertEquals(filterView, filterEditor.getView());
        return filterEditor;
    }

    @Test
    public void testTextParam() {
        setupEditor(ColumnType.LABEL, CoreFunctionType.EQUALS_TO, "Test");

        int n = CoreFunctionType.getSupportedTypes(ColumnType.LABEL).size();
        verify(filterView).clearFunctionSelector();
        verify(filterView, times(n)).addToFunctionSelector(any(CoreFunctionType.class));
        verify(filterView, never()).addToFunctionSelector(CoreFunctionType.TIME_FRAME);

        verify(filterView).clearFilterConfig();
        verify(filterView).addFilterConfigWidget(textParameterEditor);
        verify(filterView).setCurrentFunctionSelected("col = Test");
    }

    @Test
    public void testNumberParam() throws Exception {
        double number = 1000.23;
        NumberFormat numberFormat = NumberFormat.getNumberInstance();
        when(filterView.formatNumber(number)).thenReturn(numberFormat.format(number));
        setupEditor(ColumnType.NUMBER, CoreFunctionType.EQUALS_TO, number);

        int n = CoreFunctionType.getSupportedTypes(ColumnType.NUMBER).size();
        verify(filterView).clearFunctionSelector();
        verify(filterView, times(n)).addToFunctionSelector(any(CoreFunctionType.class));
        verify(filterView, never()).addToFunctionSelector(CoreFunctionType.TIME_FRAME);
        verify(filterView, never()).addToFunctionSelector(CoreFunctionType.LIKE_TO);

        verify(filterView).clearFilterConfig();
        verify(filterView).addFilterConfigWidget(numberParameterEditor);
        verify(filterView).setCurrentFunctionSelected("col = " + numberFormat.format(number));
    }

    @Test
    public void testDateParam() throws Exception {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        String dateStr = "23-11-2020 23:59:59";
        Date d = dateFormat.parse(dateStr);
        when(filterView.formatDate(d)).thenReturn(dateStr);
        setupEditor(ColumnType.DATE, CoreFunctionType.EQUALS_TO, d);

        int n = CoreFunctionType.getSupportedTypes(ColumnType.DATE).size();
        verify(filterView).clearFunctionSelector();
        verify(filterView, times(n)).addToFunctionSelector(any(CoreFunctionType.class));
        verify(filterView).addToFunctionSelector(CoreFunctionType.TIME_FRAME);
        verify(filterView, never()).addToFunctionSelector(CoreFunctionType.LIKE_TO);

        verify(filterView).clearFilterConfig();
        verify(filterView).addFilterConfigWidget(dateParameterEditor);
        verify(filterView).setCurrentFunctionSelected("col = " + dateStr);
    }

    @Test
    public void testNotEquals() {
        setupEditor(ColumnType.LABEL, CoreFunctionType.NOT_EQUALS_TO, "Test");
        verify(filterView).setCurrentFunctionSelected("col != Test");
    }

    @Test
    public void testBetween() {
        setupEditor(ColumnType.LABEL, CoreFunctionType.BETWEEN, "A", "B");
        verify(filterView).setCurrentFunctionSelected("col [A  B]");
    }

    @Test
    public void testGreaterOrEquals() {
        setupEditor(ColumnType.LABEL, CoreFunctionType.GREATER_OR_EQUALS_TO, "Test");
        verify(filterView).setCurrentFunctionSelected("col >= Test");
    }

    @Test
    public void testGreaterThan() {
        setupEditor(ColumnType.LABEL, CoreFunctionType.GREATER_THAN, "Test");
        verify(filterView).setCurrentFunctionSelected("col > Test");
    }
    @Test
    public void testLowerOrEquals() {
        setupEditor(ColumnType.LABEL, CoreFunctionType.LOWER_OR_EQUALS_TO, "Test");
        verify(filterView).setCurrentFunctionSelected("col <= Test");
    }

    @Test
    public void testLowerThan() {
        setupEditor(ColumnType.LABEL, CoreFunctionType.LOWER_THAN, "Test");
        verify(filterView).setCurrentFunctionSelected("col < Test");
    }

    @Test
    public void testNull() {
        setupEditor(ColumnType.LABEL, CoreFunctionType.IS_NULL);
        verify(filterView).setCurrentFunctionSelected("col = null ");
    }

    @Test
    public void testNotNull() {
        setupEditor(ColumnType.LABEL, CoreFunctionType.NOT_NULL);
        verify(filterView).setCurrentFunctionSelected("col != null ");
    }

    @Test
    public void testLikeTo() {
        setupEditor(ColumnType.LABEL, CoreFunctionType.LIKE_TO, "Test");
        verify(filterView).clearFilterConfig();
        verify(filterView).setCurrentFunctionSelected("col like Test");
        verify(filterView).addFilterConfigWidget(likeToFunctionEditor);
    }

    @Test
    public void testTimeFrame() {
        setupEditor(ColumnType.DATE, CoreFunctionType.TIME_FRAME, "begin[year February] till now");
        verify(filterView).clearFilterConfig();
        verify(filterView).setCurrentFunctionSelected("col = begin[year February] till now");
        verify(filterView).addFilterConfigWidget(timeFrameEditor);
    }
}