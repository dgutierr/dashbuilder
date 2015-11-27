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
import org.dashbuilder.displayer.client.DisplayerViewMock;

public class TableDisplayerViewMock
        extends DisplayerViewMock<TableDisplayer>
        implements TableDisplayer.View {

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
    public void createTable(int pageSize) {

    }

    @Override
    public void redrawTable() {

    }

    @Override
    public void setWidth(int width) {

    }

    @Override
    public void setSortEnabled(boolean enabled) {

    }

    @Override
    public void setTotalRows(int rows) {

    }

    @Override
    public void setCurrentPageRows(int rows) {

    }

    @Override
    public void setPagerEnabled(boolean enabled) {

    }

    @Override
    public void addColumn(ColumnType columnType, String columnId, String columnName, int index, boolean selectEnabled, boolean sortEnabled) {

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

    @Override
    public void gotoFirstPage() {

    }

    @Override
    public int getLastOffset() {
        return 0;
    }
}
