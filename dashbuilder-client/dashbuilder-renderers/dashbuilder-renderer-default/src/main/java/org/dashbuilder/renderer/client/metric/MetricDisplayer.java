/**
 * Copyright (C) 2015 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.dashbuilder.renderer.client.metric;

import java.util.List;

import org.dashbuilder.dataset.ColumnType;
import org.dashbuilder.dataset.DataSetLookupConstraints;
import org.dashbuilder.dataset.filter.DataSetFilter;
import org.dashbuilder.displayer.DisplayerAttributeDef;
import org.dashbuilder.displayer.DisplayerAttributeGroupDef;
import org.dashbuilder.displayer.DisplayerConstraints;
import org.dashbuilder.displayer.DisplayerSettings;
import org.dashbuilder.displayer.client.AbstractDisplayer;
import org.dashbuilder.displayer.client.DisplayerView;

public class MetricDisplayer extends AbstractDisplayer<MetricDisplayer.View> {

    public interface View extends DisplayerView<MetricDisplayer> {

        void show(DisplayerSettings displayerSettings);

        void nodata();

        void update(String value);

        String getColumnsTitle();
    }

    protected View view;
    protected boolean filterOn = false;

    public MetricDisplayer() {
        this(new MetricView());
    }

    public MetricDisplayer(View view) {
        this.view = view;
    }

    @Override
    public View getView() {
        return view;
    }

    @Override
    public DisplayerConstraints createDisplayerConstraints() {

        DataSetLookupConstraints lookupConstraints = new DataSetLookupConstraints()
                .setGroupAllowed(false)
                .setMaxColumns(1)
                .setMinColumns(1)
                .setFunctionRequired(true)
                .setExtraColumnsAllowed(false)
                .setColumnsTitle(view.getColumnsTitle())
                .setColumnTypes(new ColumnType[] {
                        ColumnType.NUMBER});

        return new DisplayerConstraints(lookupConstraints)
                .supportsAttribute(DisplayerAttributeDef.TYPE)
                .supportsAttribute(DisplayerAttributeDef.RENDERER)
                .supportsAttribute(DisplayerAttributeGroupDef.COLUMNS_GROUP)
                .supportsAttribute(DisplayerAttributeGroupDef.FILTER_GROUP)
                .supportsAttribute(DisplayerAttributeGroupDef.REFRESH_GROUP)
                .supportsAttribute(DisplayerAttributeGroupDef.GENERAL_GROUP)
                .supportsAttribute(DisplayerAttributeDef.CHART_WIDTH)
                .supportsAttribute(DisplayerAttributeDef.CHART_HEIGHT)
                .supportsAttribute(DisplayerAttributeDef.CHART_BGCOLOR)
                .supportsAttribute(DisplayerAttributeGroupDef.CHART_MARGIN_GROUP);
    }

    @Override
    protected void createVisualization() {
        view.show(displayerSettings);
        updateVisualization();
    }

    @Override
    protected void updateVisualization() {
        if (dataSet.getRowCount() == 0) {
            view.nodata();
        } else {
            String valueStr = super.formatValue(0, 0);
            view.update(valueStr);
        }
    }

    public boolean isFilterOn() {
        return filterOn;
    }

    public void updateFilter() {
        if (filterOn) {
            filterReset();
        } else {
            if (displayerSettings.isFilterEnabled()) {
                filterApply();
            }
        }
    }

    public DataSetFilter fetchFilter() {
        if (displayerSettings.getDataSetLookup() == null) {
            return null;
        }
        List<DataSetFilter> filterOps = displayerSettings.getDataSetLookup().getOperationList(DataSetFilter.class);
        if (filterOps == null || filterOps.isEmpty()) {
            return null;
        }
        DataSetFilter filter = new DataSetFilter();
        for (DataSetFilter filterOp : filterOps) {
            filter.getColumnFilterList().addAll(filterOp.getColumnFilterList());
        }
        return filter;
    }

    public void filterApply() {
        filterOn = true;
        DisplayerSettings clone = displayerSettings.cloneInstance();
        clone.setChartBackgroundColor("DDDDDD");
        view.show(clone);

        DataSetFilter filter = fetchFilter();
        super.filterApply(filter);
    }

    @Override
    public void filterReset() {
        filterOn = false;
        view.show(displayerSettings);

        DataSetFilter filter = fetchFilter();
        if (filter != null) {
            super.filterReset();
        }
    }
}
