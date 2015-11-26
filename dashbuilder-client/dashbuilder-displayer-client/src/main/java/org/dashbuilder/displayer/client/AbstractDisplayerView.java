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
package org.dashbuilder.displayer.client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import org.dashbuilder.common.client.StringUtils;
import org.dashbuilder.common.client.error.ClientRuntimeError;
import org.dashbuilder.dataset.ColumnType;
import org.dashbuilder.dataset.DataColumn;
import org.dashbuilder.dataset.client.resources.i18n.DayOfWeekConstants;
import org.dashbuilder.dataset.client.resources.i18n.MonthConstants;
import org.dashbuilder.dataset.date.DayOfWeek;
import org.dashbuilder.dataset.date.Month;
import org.dashbuilder.dataset.filter.DataSetFilter;
import org.dashbuilder.dataset.group.ColumnGroup;
import org.dashbuilder.dataset.group.DataSetGroup;
import org.dashbuilder.dataset.group.DateIntervalType;
import org.dashbuilder.dataset.group.GroupStrategy;
import org.dashbuilder.dataset.group.Interval;
import org.dashbuilder.dataset.sort.SortOrder;
import org.dashbuilder.displayer.ColumnSettings;
import org.dashbuilder.displayer.client.formatter.ValueFormatter;
import org.dashbuilder.displayer.client.resources.i18n.CommonConstants;
import org.dashbuilder.displayer.client.resources.i18n.DisplayerConstants;

public abstract class AbstractDisplayerView<P extends AbstractDisplayer> extends Composite implements DisplayerView<P> {

    private FlowPanel panel = new FlowPanel();
    private Label label = new Label();
    private Timer refreshTimer = null;
    private P presenter = null;

    protected static Map<String,NumberFormat> numberPatternMap = new HashMap<String, NumberFormat>();
    protected static Map<String,DateTimeFormat> datePatternMap = new HashMap<String, DateTimeFormat>();

    public static final String[] _jsMalicious = {"document.", "window.", "alert(", "eval(", ".innerHTML"};

    public AbstractDisplayerView() {
        initWidget(panel);
    }

    public void setPresenter(P presenter) {
        this.presenter = presenter;
    }

    public P getPresenter() {
        return presenter;
    }

    public void setRootWidget(Widget widget) {
        panel.add(widget);
    }

    @Override
    public void clear() {
        panel.clear();
    }

    @Override
    public void setId(String id) {
        panel.getElement().setId(id);
    }

    @Override
    public void showLoading() {
        displayMessage(DisplayerConstants.INSTANCE.initializing());
    }

    @Override
    public void errorMissingSettings() {
        displayMessage(DisplayerConstants.INSTANCE.error() + DisplayerConstants.INSTANCE.error_settings_unset());
    }

    @Override
    public void errorMissingHandler() {
        displayMessage(DisplayerConstants.INSTANCE.error() + DisplayerConstants.INSTANCE.error_handler_unset());
    }

    @Override
    public void errorDataSetNotFound(String dataSetUUID) {
        displayMessage(CommonConstants.INSTANCE.dataset_lookup_dataset_notfound(dataSetUUID));
    }

    @Override
    public void error(ClientRuntimeError e) {
        displayMessage(DisplayerConstants.INSTANCE.error() + e.getMessage());

        if (e.getThrowable() != null) {
            GWT.log(e.getMessage(), e.getThrowable());
        } else {
            GWT.log(e.getMessage());
        }
    }

    @Override
    public void enableRefreshTimer(int seconds) {
        if (refreshTimer == null) {
            refreshTimer = new Timer() {
                public void run() {
                    if (presenter.isDrawn()) {
                        presenter.redraw();
                    }
                }
            };
        }
        refreshTimer.schedule(seconds * 1000);
    }

    @Override
    public void cancelRefreshTimer() {
        if (refreshTimer != null) {
            refreshTimer.cancel();
        }
    }

    @Override
    public String applyExpression(String val, String expr) {
        if (StringUtils.isBlank(expr)) {
            return val;
        }
        for (String keyword : _jsMalicious) {
            if (expr.contains(keyword)) {
                presenter.afterError(DisplayerConstants.INSTANCE.displayer_keyword_not_allowed(expr));
                throw new RuntimeException(DisplayerConstants.INSTANCE.displayer_keyword_not_allowed(expr));
            }
        }
        try {
            return evalExpression(val, expr);
        } catch (Exception e) {
            presenter.afterError(DisplayerConstants.INSTANCE.displayer_expr_invalid_syntax(expr), e);
            throw new RuntimeException(DisplayerConstants.INSTANCE.displayer_expr_invalid_syntax(expr));
        }
    }

    protected native String evalExpression(String val, String expr) /*-{
        value = val;
        return eval(expr) + '';
    }-*/;

    @Override
    public Date parseDate(String pattern, String d) {
        DateTimeFormat df = getDateFormat(pattern);
        return df.parse(d);
    }

    @Override
    public String formatDate(String pattern, Date d) {
        DateTimeFormat df = getDateFormat(pattern);
        return df.format(d);
    }

    @Override
    public String formatNumber(String pattern, Number n) {
        NumberFormat f = getNumberFormat(pattern);
        return f.format(n);
    }

    @Override
    public String formatDayOfWeek(DayOfWeek dayOfWeek) {
        return DayOfWeekConstants.INSTANCE.getString(dayOfWeek.name());
    }

    @Override
    public String formatMonth(Month month) {
        return MonthConstants.INSTANCE.getString(month.name());
    }

    protected NumberFormat getNumberFormat(String pattern) {
        if (StringUtils.isBlank(pattern)) {
            return getNumberFormat(ColumnSettings.NUMBER_PATTERN);
        }
        NumberFormat format = numberPatternMap.get(pattern);
        if (format == null) {
            numberPatternMap.put(pattern, format = NumberFormat.getFormat(pattern));
        }
        return format;
    }

    protected DateTimeFormat getDateFormat(String pattern) {
        if (StringUtils.isBlank(pattern)) {
            return getDateFormat(ColumnSettings.DATE_PATTERN);
        }
        DateTimeFormat format = datePatternMap.get(pattern);
        if (format == null) {
            datePatternMap.put(pattern, format = DateTimeFormat.getFormat(pattern));
        }
        return format;
    }

    public void displayMessage(String msg) {
        panel.clear();
        panel.add(label);
        label.setText(msg);
    }
}