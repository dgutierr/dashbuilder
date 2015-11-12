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

import org.dashbuilder.dataset.date.TimeAmount;
import org.dashbuilder.dataset.group.DateIntervalType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.mvp.Command;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class TimeAmountEditorTest {

    @Mock
    TimeAmountEditor.View timeAmountView;

    @Mock
    Command changeCommand;

    @Test
    public void testViewInitialization() {
        TimeAmount timeAmount = new TimeAmount(10, DateIntervalType.DAY);
        new TimeAmountEditor(timeAmountView, timeAmount);

        verify(timeAmountView).clearIntervalTypeSelector();
        verify(timeAmountView, atLeastOnce()).addIntervalTypeItem(any(DateIntervalType.class));
        verify(timeAmountView).setSelectedTypeIndex(any(Integer.class));
        verify(timeAmountView).setQuantity(10);
    }

    @Test
    public void testNullInitialization() {
        new TimeAmountEditor(timeAmountView, null);

        verify(timeAmountView).clearIntervalTypeSelector();
        verify(timeAmountView, atLeastOnce()).addIntervalTypeItem(any(DateIntervalType.class));
        verify(timeAmountView, never()).setSelectedTypeIndex(any(Integer.class));
        verify(timeAmountView).setQuantity(0);
    }

    @Test
    public void testDecreaseQuantity() {
        TimeAmount timeAmount = new TimeAmount(10, DateIntervalType.DAY);
        TimeAmountEditor timeAmountEditor = new TimeAmountEditor(timeAmountView, timeAmount);
        timeAmountEditor.setOnChangeCommand(changeCommand);
        timeAmountEditor.decreaseQuantity();

        verify(timeAmountView).setQuantity(9);
        verify(changeCommand).execute();

        assertEquals(timeAmount.getQuantity(), 9);
    }

    @Test
    public void testIncreaseQuantity() {
        TimeAmount timeAmount = new TimeAmount(10, DateIntervalType.DAY);
        TimeAmountEditor timeAmountEditor = new TimeAmountEditor(timeAmountView, timeAmount);
        timeAmountEditor.setOnChangeCommand(changeCommand);
        timeAmountEditor.increaseQuantity();

        verify(timeAmountView).setQuantity(11);
        verify(changeCommand).execute();

        assertEquals(timeAmount.getQuantity(), 11);
    }
}