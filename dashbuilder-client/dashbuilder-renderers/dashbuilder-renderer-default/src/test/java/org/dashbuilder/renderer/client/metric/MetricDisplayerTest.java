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
package org.dashbuilder.renderer.client.metric;

import org.dashbuilder.dataset.filter.DataSetFilter;
import org.dashbuilder.dataset.filter.FilterFactory;
import org.dashbuilder.dataset.group.AggregateFunctionType;
import org.dashbuilder.displayer.DisplayerSettings;
import org.dashbuilder.displayer.DisplayerSettingsFactory;
import org.dashbuilder.displayer.client.AbstractDisplayerTest;
import org.dashbuilder.displayer.client.DataSetHandlerImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.*;
import static org.dashbuilder.dataset.ExpenseReportsData.*;

@RunWith(MockitoJUnitRunner.class)
public class MetricDisplayerTest extends AbstractDisplayerTest {

    public MetricDisplayer createMetricDisplayer(DisplayerSettings settings) {
        MetricDisplayer displayer = new MetricDisplayer(spy(new MetricViewMock()));
        displayer.setDisplayerSettings(settings);
        displayer.setDataSetHandler(new DataSetHandlerImpl(clientServices, settings.getDataSetLookup()));
        return displayer;
    }

    @Test
    public void testDraw() {
        DisplayerSettings engExpenses = DisplayerSettingsFactory.newMetricSettings()
                .dataset(EXPENSES)
                .filter(COLUMN_DEPARTMENT, FilterFactory.equalsTo("Engineering"))
                .column(COLUMN_AMOUNT, AggregateFunctionType.SUM)
                .filterOff(true)
                .buildSettings();

        MetricDisplayer presenter = createMetricDisplayer(engExpenses);
        MetricDisplayer.View view = presenter.getView();
        presenter.draw();

        verify(view).show(engExpenses);
        verify(view).update("7,650.16");
    }

    @Test
    public void testNoData() {
        DisplayerSettings empty = DisplayerSettingsFactory.newMetricSettings()
                .dataset(EXPENSES)
                .filter(COLUMN_ID, FilterFactory.isNull())
                .column(COLUMN_AMOUNT)
                .buildSettings();

        MetricDisplayer presenter = createMetricDisplayer(empty);
        MetricDisplayer.View view = presenter.getView();
        presenter.draw();

        verify(view).show(empty);
        verify(view).nodata();
        verify(view, never()).update(anyString());
    }
}