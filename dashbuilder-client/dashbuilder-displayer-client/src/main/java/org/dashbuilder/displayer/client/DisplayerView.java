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

import java.util.Date;

import org.dashbuilder.common.client.error.ClientRuntimeError;
import org.dashbuilder.dataset.date.DayOfWeek;
import org.dashbuilder.dataset.date.Month;
import org.dashbuilder.displayer.DisplayerSettings;
import org.uberfire.client.mvp.UberView;

/**
 * The GWT side of a Displayer
 */
public interface DisplayerView<P extends Displayer> extends UberView<P> {

    void errorMissingSettings();

    void errorMissingHandler();

    void showLoading();

    void clear();

    void setId(String id);

    void errorDataSetNotFound(String uuid);

    void error(ClientRuntimeError error);

    String formatDate(String pattern, Date d);

    Date parseDate(String pattern, String d);

    String formatNumber(String pattern, Number n);

    String formatDayOfWeek(DayOfWeek dayOfWeek);

    String formatMonth(Month month);

    boolean isRefreshTimerOn();

    void enableRefreshTimer(int seconds);

    void cancelRefreshTimer();

    String applyExpression(String value, String expression);
}