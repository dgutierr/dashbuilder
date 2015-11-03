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

import org.dashbuilder.dataset.date.Month;
import org.dashbuilder.dataset.date.TimeFrame;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.mvp.Command;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class TimeFrameEditorTest {

    @Mock
    TimeFrameEditor.View timeFrameView;

    @Mock
    TimeInstantEditor fromEditor;

    @Mock
    TimeInstantEditor toEditor;

    @Mock
    Command changeCommand;

    public static final TimeFrame TEN_DAYS = TimeFrame.parse("begin[year March] till 10day");
    public static final TimeFrame LAST_DAY = TimeFrame.parse("now -1day till now");
    public static final TimeFrame CURRENT_YEAR = TimeFrame.parse("begin[year] till end[year]");
    public static final TimeFrame UNDEFINED = null;

    @Test
    public void testViewInitialization() {
        TimeFrameEditor timeFrameEditor = new TimeFrameEditor(timeFrameView, fromEditor, toEditor, TEN_DAYS);

        assertEquals(timeFrameView, timeFrameEditor.view);
        verify(timeFrameView).init(timeFrameEditor);
        verify(timeFrameView).clearFirstMonthSelector();
        verify(timeFrameView, times(Month.values().length)).addFirstMonthItem(any(Month.class));
        verify(timeFrameView).setSelectedFirstMonthIndex(Month.MARCH.getIndex() - 1);
    }

    @Test
    public void testNullInitialization() {
        TimeFrameEditor timeFrameEditor = new TimeFrameEditor(timeFrameView, fromEditor, toEditor, UNDEFINED);

        assertEquals(timeFrameView, timeFrameEditor.view);
        verify(timeFrameView).init(timeFrameEditor);
        verify(timeFrameView).clearFirstMonthSelector();
        verify(timeFrameView, times(Month.values().length)).addFirstMonthItem(any(Month.class));
        verify(timeFrameView).setSelectedFirstMonthIndex(Month.JANUARY.getIndex() - 1);
    }

    @Test
    public void testFirstMonthAvailable() {
        TimeFrameEditor timeFrameEditor = new TimeFrameEditor(timeFrameView, fromEditor, toEditor, CURRENT_YEAR);
        assertEquals(timeFrameEditor.isFirstMonthAvailable(), true);
    }

    @Test
    public void testFirstMonthUnavailable() {
        TimeFrameEditor timeFrameEditor = new TimeFrameEditor(timeFrameView, fromEditor, toEditor, LAST_DAY);
        assertEquals(timeFrameEditor.isFirstMonthAvailable(), false);
    }
}