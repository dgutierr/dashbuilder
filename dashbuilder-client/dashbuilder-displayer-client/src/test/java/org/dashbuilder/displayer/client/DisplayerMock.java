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

import org.dashbuilder.displayer.DisplayerConstraints;

public class DisplayerMock extends AbstractDisplayer<DisplayerMock.View> {

    public interface View extends DisplayerView<DisplayerMock> {

    }

    private View view;
    private DisplayerConstraints constraints;

    public DisplayerMock(View view, DisplayerConstraints constraints) {
        this.view = view;
        this.constraints = constraints;
    }

    @Override
    public View getView() {
        return view;
    }

    @Override
    public DisplayerConstraints createDisplayerConstraints() {
        return constraints;
    }

    @Override
    protected void createVisualization() {
        // Mock
    }

    @Override
    protected void updateVisualization() {
        // Mock
    }
}
