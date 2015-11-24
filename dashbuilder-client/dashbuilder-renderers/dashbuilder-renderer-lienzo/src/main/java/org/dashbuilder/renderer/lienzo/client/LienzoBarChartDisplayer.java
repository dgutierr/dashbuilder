/**
 * Copyright (C) 2014 JBoss Inc
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
package org.dashbuilder.renderer.lienzo.client;

import org.dashbuilder.displayer.DisplayerSubType;

public class LienzoBarChartDisplayer extends LienzoXYChartDisplayer<LienzoBarChartDisplayer.View> {

    public interface View extends LienzoXYChartDisplayer.View<LienzoBarChartDisplayer> {

    }

    private View view;

    public LienzoBarChartDisplayer() {
        this(new LienzoBarChartDisplayerView());
    }

    public LienzoBarChartDisplayer(View view) {
        this.view = view;
    }

    @Override
    public View getView() {
        return view;
    }

    @Override
    protected void createVisualization() {
        DisplayerSubType subType = displayerSettings.getSubtype();
        getView().setHorizontal(subType != null &&
                        (DisplayerSubType.COLUMN.equals(subType) ||
                        DisplayerSubType.COLUMN_STACKED.equals(subType)));

        super.createVisualization();
    }
}
