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

import javax.enterprise.event.Event;

import org.dashbuilder.dataset.DataSet;
import org.dashbuilder.dataset.ExpenseReportsData;
import org.dashbuilder.dataset.client.ClientDataSetManager;
import org.dashbuilder.dataset.client.DataSetClientServices;
import org.dashbuilder.dataset.client.ClientFactory;
import org.dashbuilder.dataset.events.DataSetModifiedEvent;
import org.dashbuilder.dataset.events.DataSetPushOkEvent;
import org.dashbuilder.dataset.events.DataSetPushingEvent;
import org.dashbuilder.dataset.service.DataSetDefServices;
import org.dashbuilder.dataset.service.DataSetExportServices;
import org.dashbuilder.dataset.service.DataSetLookupServices;
import org.dashbuilder.displayer.DisplayerSettings;
import org.dashbuilder.displayer.client.formatter.ValueFormatterRegistry;
import org.jboss.errai.common.client.api.Caller;
import org.junit.Before;
import org.mockito.Mock;

import static org.mockito.Mockito.*;

public abstract class AbstractDisplayerTest {

    @Mock
    Event<DataSetPushingEvent> dataSetPushingEvent;

    @Mock
    Event<DataSetPushOkEvent> dataSetPushOkEvent;

    @Mock
    Event<DataSetModifiedEvent> dataSetModifiedEvent;

    @Mock
    Caller<DataSetLookupServices> dataSetLookupServices;

    @Mock
    Caller<DataSetDefServices> dataSetDefServices;

    @Mock
    Caller<DataSetExportServices> dataSetExportServices;

    @Mock
    RendererManager rendererManager;

    @Mock
    RendererLibrary rendererLibrary;

    @Mock
    ValueFormatterRegistry formatterRegistry;

    ClientFactory clientFactory;
    DataSetClientServices clientServices;
    ClientDataSetManager clientDataSetManager;
    DisplayerLocator displayerLocator;
    DataSet expensesDataSet;

    public static final String EXPENSES = "expenses";

    public void initClientFactory() {
        clientFactory = ClientFactory.get();
        clientFactory.setClientDateFormatter(new ClientDateFormatterMock());
        clientFactory.setChronometer(new ChronometerMock());
    }

    public void initClientDataSetManager() {
        clientDataSetManager = clientFactory.getClientDataSetManager();
    }

    public void initDataSetClientServices() {
        clientServices = new DataSetClientServices(
                clientDataSetManager,
                clientFactory.getAggregateFunctionManager(),
                clientFactory.getIntervalBuilderLocator(),
                dataSetPushingEvent,
                dataSetPushOkEvent,
                dataSetModifiedEvent,
                dataSetLookupServices,
                dataSetDefServices,
                dataSetExportServices);
    }

    public void initDisplayerLocator() {
        displayerLocator = new DisplayerLocator(clientServices,
                clientDataSetManager,
                rendererManager,
                formatterRegistry);
    }

    public void registerExpensesDataSet() throws Exception {
        expensesDataSet = ExpenseReportsData.INSTANCE.toDataSet();
        expensesDataSet.setUUID(EXPENSES);
        clientDataSetManager.registerDataSet(expensesDataSet);
    }

    @Before
    public void init() throws Exception {
        initClientFactory();
        initClientDataSetManager();
        initDataSetClientServices();
        initDisplayerLocator();
        registerExpensesDataSet();

        when(rendererManager.getRendererForDisplayer(any(DisplayerSettings.class))).thenReturn(rendererLibrary);
        when(rendererLibrary.lookupDisplayer(any(DisplayerSettings.class))).thenReturn(createNewDisplayer());
    }

    public abstract Displayer createNewDisplayer();
}