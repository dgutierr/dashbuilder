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
package org.dashbuilder.displayer.client;

import org.dashbuilder.displayer.DisplayerSettings;
import org.dashbuilder.displayer.DisplayerSettingsFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.dashbuilder.dataset.group.AggregateFunctionType.SUM;
import static org.mockito.Mockito.*;
import static org.junit.Assert.*;
import static org.dashbuilder.dataset.ExpenseReportsData.*;

@RunWith(MockitoJUnitRunner.class)
public class DisplayerTest extends AbstractDisplayerTest {

    @Mock
    DisplayerMock.View view;

    @Override
    public Displayer createNewDisplayer() {
        return new DisplayerMock(view, null);
    }

    @Test
    public void testLookupDisplayer() {
        DisplayerSettings settings = DisplayerSettingsFactory.newPieChartSettings()
                .dataset(EXPENSES)
                .group(COLUMN_DATE)
                .column(COLUMN_DATE)
                .column(COLUMN_AMOUNT, SUM, "sum1")
                .width(400).height(250)
                .margins(10, 10, 10, 0)
                .filterOn(true, true, true)
                .buildSettings();

        Displayer displayer = displayerLocator.lookupDisplayer(settings);
        displayer.draw();
        assertNotNull(displayer);
        assertNotNull(displayer.getDataSetHandler());
    }
}