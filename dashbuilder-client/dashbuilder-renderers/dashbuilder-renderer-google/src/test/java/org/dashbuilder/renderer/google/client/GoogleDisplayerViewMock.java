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
package org.dashbuilder.renderer.google.client;

import java.util.Date;

import org.dashbuilder.dataset.ColumnType;
import org.dashbuilder.displayer.client.DisplayerViewMock;

public abstract class GoogleDisplayerViewMock<P extends GoogleDisplayer>
        extends DisplayerViewMock<P>
        implements GoogleDisplayer.View<P> {

    @Override
    public void draw() {

    }

    @Override
    public void dataClear() {

    }

    @Override
    public void dataRowCount(int rowCount) {

    }

    @Override
    public void dataAddColumn(ColumnType type, String id, String name) {

    }

    @Override
    public void dataSetValue(int row, int column, Date value) {

    }

    @Override
    public void dataSetValue(int row, int column, double value) {

    }

    @Override
    public void dataSetValue(int row, int column, String value) {

    }

    @Override
    public void dataFormatDateColumn(String pattern, int column) {

    }

    @Override
    public void dataFormatNumberColumn(String pattern, int column) {

    }

    @Override
    public String getGroupsTitle() {
        return null;
    }

    @Override
    public String getColumnsTitle() {
        return null;
    }

    @Override
    public void showTitle(String title) {

    }

    @Override
    public void clearFilterStatus() {

    }

    @Override
    public void addFilterValue(String value) {

    }

    @Override
    public void addFilterReset() {

    }
}
