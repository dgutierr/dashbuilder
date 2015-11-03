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
package org.dashbuilder.displayer.client.widgets.filter;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.uberfire.client.mvp.UberView;
import org.uberfire.mvp.Command;

public class LikeToFunctionEditor implements IsWidget {

    interface View extends UberView<LikeToFunctionEditor> {
        void setPattern(String pattern);
        void setCaseSensitive(boolean caseSensitive);
        String getPattern();
        boolean isCaseSensitive();
    }

    Command onChangeCommand = new Command() { public void execute() {} };
    View view;

    public LikeToFunctionEditor() {
        this(new LikeToFunctionEditorView());
    }

    public LikeToFunctionEditor(View view) {
        this.view = view;
        this.view.init(this);
    }

    @Override
    public Widget asWidget() {
        return view.asWidget();
    }

    public void setOnChangeCommand(Command onChangeCommand) {
        this.onChangeCommand = onChangeCommand;
    }

    public void setPattern(String pattern) {
        view.setPattern(pattern);
    }

    public void setCaseSensitive(boolean caseSensitive) {
        view.setCaseSensitive(caseSensitive);
    }

    public String getPattern() {
        return view.getPattern();
    }

    public boolean isCaseSensitive() {
        return view.isCaseSensitive();
    }

    void viewUpdated() {
        onChangeCommand.execute();
    }
}
