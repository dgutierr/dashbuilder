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

public abstract class GoogleCategoriesDisplayerViewMock<P extends GoogleCategoriesDisplayer>
        extends GoogleChartDisplayerViewMock<P>
        implements GoogleCategoriesDisplayer.View<P> {

    @Override
    public void setFilterEnabled(boolean filterEnabled) {

    }

    @Override
    public void setBgColor(String bgColor) {

    }

    @Override
    public void setShowXLabels(boolean showXLabels) {

    }

    @Override
    public void setShowYLabels(boolean showYLabels) {

    }

    @Override
    public void setXAxisTitle(String xAxisTitle) {

    }

    @Override
    public void setYAxisTitle(String yAxisTitle) {

    }

    @Override
    public void setColors(String[] colors) {

    }

    @Override
    public void setAnimationOn(boolean animationOn) {

    }

    @Override
    public void setAnimationDuration(int animationDuration) {

    }

    @Override
    public void nodata() {

    }

    @Override
    public void drawChart() {

    }
}