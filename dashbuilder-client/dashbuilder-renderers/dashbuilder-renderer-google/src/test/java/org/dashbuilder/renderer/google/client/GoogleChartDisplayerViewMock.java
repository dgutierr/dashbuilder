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

import org.dashbuilder.displayer.DisplayerSubType;
import org.dashbuilder.displayer.Position;

public abstract class GoogleChartDisplayerViewMock<P extends GoogleChartDisplayer>
        extends GoogleDisplayerViewMock<P>
        implements GoogleChartDisplayer.View<P> {

    @Override
    public void setWidth(int width) {

    }

    @Override
    public void setHeight(int height) {

    }

    @Override
    public void setMarginTop(int marginTop) {

    }

    @Override
    public void setMarginBottom(int marginBottom) {

    }

    @Override
    public void setMarginRight(int marginRight) {

    }

    @Override
    public void setMarginLeft(int marginLeft) {

    }

    @Override
    public void setLegendPosition(Position position) {

    }

    @Override
    public void setSubType(DisplayerSubType subType) {

    }
}
