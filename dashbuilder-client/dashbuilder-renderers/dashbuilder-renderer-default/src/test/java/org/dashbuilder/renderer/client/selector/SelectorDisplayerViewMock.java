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
package org.dashbuilder.renderer.client.selector;

import org.dashbuilder.displayer.client.DisplayerViewMock;

public class SelectorDisplayerViewMock
        extends DisplayerViewMock<SelectorDisplayer>
        implements SelectorDisplayer.View {

    @Override
    public void showSelectHint(String column) {

    }

    @Override
    public void showResetHint(String column) {

    }

    @Override
    public void addItem(String id, String value) {

    }

    @Override
    public void setSelectedIndex(int index) {

    }

    @Override
    public int getSelectedIndex() {
        return 0;
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    @Override
    public void setItemTitle(int id, String title) {

    }

    @Override
    public void setFilterEnabled(boolean enabled) {

    }

    @Override
    public String getGroupsTitle() {
        return null;
    }

    @Override
    public String getColumnsTitle() {
        return null;
    }
}
