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

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.google.gwt.user.client.ui.Widget;
import org.dashbuilder.common.client.error.ClientRuntimeError;
import org.dashbuilder.dataset.date.DayOfWeek;
import org.dashbuilder.dataset.date.Month;

public class DisplayerViewMock<P extends Displayer> implements DisplayerView<P> {

    @Override
    public Widget asWidget() {
        return null;
    }

    @Override
    public void init(P presenter) {

    }

    @Override
    public String applyExpression(String value, String expression) {
        return value;
    }

    @Override
    public void cancelRefreshTimer() {

    }

    @Override
    public void enableRefreshTimer(int seconds) {

    }

    @Override
    public String formatMonth(Month month) {
        return month.name();
    }

    @Override
    public String formatDayOfWeek(DayOfWeek dayOfWeek) {
        return dayOfWeek.name();
    }

    @Override
    public String formatNumber(String pattern, Number n) {
        DecimalFormat df = new DecimalFormat(pattern);
        return df.format(n);
    }

    @Override
    public Date parseDate(String pattern, String d) {
        try {
            SimpleDateFormat df = new SimpleDateFormat(pattern);
            return df.parse(d);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String formatDate(String pattern, Date d) {
        SimpleDateFormat df = new SimpleDateFormat(pattern);
        return df.format(d);
    }

    @Override
    public void error(ClientRuntimeError error) {

    }

    @Override
    public void errorDataSetNotFound(String uuid) {

    }

    @Override
    public void setId(String id) {

    }

    @Override
    public void clear() {

    }

    @Override
    public void showLoading() {

    }

    @Override
    public void errorMissingHandler() {

    }

    @Override
    public void errorMissingSettings() {

    }
}
