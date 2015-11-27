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

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;
import org.dashbuilder.common.client.StringUtils;
import org.dashbuilder.displayer.DisplayerSettings;
import org.dashbuilder.displayer.client.AbstractDisplayerView;
import org.dashbuilder.renderer.client.resources.i18n.MetricConstants;
import org.gwtbootstrap3.client.ui.Heading;
import org.gwtbootstrap3.client.ui.Jumbotron;
import org.gwtbootstrap3.client.ui.html.Paragraph;

public class MetricView extends AbstractDisplayerView<MetricDisplayer> implements MetricDisplayer.View {

    @UiField
    protected Jumbotron heroPanel;

    @UiField
    protected FocusPanel centerPanel;

    @UiField
    protected Paragraph titlePanel;

    @UiField
    protected Panel metricPanel;

    @UiField
    protected Heading metricHeading;

    interface Binder extends UiBinder<Widget, MetricView> {}
    private static Binder uiBinder = GWT.create(Binder.class);

    @Override
    public void init(MetricDisplayer presenter) {
        super.setPresenter(presenter);
        super.setVisualization(uiBinder.createAndBindUi(this));
    }

    @Override
    public void show(DisplayerSettings displayerSettings) {
        int w = displayerSettings.getChartWidth();
        int h = displayerSettings.getChartHeight();
        int mtop = displayerSettings.getChartMarginTop();
        int mbottom = displayerSettings.getChartMarginBottom();
        int mleft = displayerSettings.getChartMarginLeft();
        int mright = displayerSettings.getChartMarginRight();

        // Hero panel (size)
        Style style = heroPanel.getElement().getStyle();
        style.setPadding(0, Style.Unit.PX);
        style.setPropertyPx("borderRadius", 6);
        style.setWidth(w, Style.Unit.PX);
        style.setHeight(h, Style.Unit.PX);
        style.setFontWeight(Style.FontWeight.BOLD);
        style.setTextAlign(Style.TextAlign.CENTER);
        style.setVerticalAlign(Style.VerticalAlign.MIDDLE);
        if (!StringUtils.isBlank(displayerSettings.getChartBackgroundColor())) {
            style.setBackgroundColor("#" + displayerSettings.getChartBackgroundColor());
        }

        // Center panel (paddings)
        style = centerPanel.getElement().getStyle();
        style.setPaddingTop(mtop, Style.Unit.PX);
        style.setPaddingBottom(mbottom, Style.Unit.PX);
        style.setPaddingLeft(mleft, Style.Unit.PX);
        style.setPaddingRight(mright, Style.Unit.PX);

        // Filter option
        if (displayerSettings.isFilterEnabled()) {
            style.setCursor(Style.Cursor.POINTER);
            centerPanel.addClickHandler(new ClickHandler() {
                public void onClick(ClickEvent clickEvent) {
                    getPresenter().updateFilter();
                }
            });
        } else {
            style.setCursor(Style.Cursor.DEFAULT);
        }

        // Title panel
        titlePanel.getElement().getStyle().setProperty("fontWeight", "400");
        titlePanel.setVisible(displayerSettings.isTitleVisible());
        titlePanel.setText(displayerSettings.getTitle());
    }

    @Override
    public void update(String value) {
        metricHeading.setText(value);
    }

    @Override
    public void nodata() {
        metricHeading.setText(MetricConstants.INSTANCE.metricDisplayer_noDataAvailable());
    }

    @Override
    public String getColumnsTitle() {
        return MetricConstants.INSTANCE.metricDisplayer_columnsTitle();
    }
}
