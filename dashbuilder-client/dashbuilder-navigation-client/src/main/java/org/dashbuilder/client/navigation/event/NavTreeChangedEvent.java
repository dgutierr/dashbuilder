/*
 * Copyright 2016 JBoss, by Red Hat, Inc
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
package org.dashbuilder.client.navigation.event;

import org.dashbuilder.navigation.NavTree;

/**
 * Event fired when the application navigation tree is changed
 */
public class NavTreeChangedEvent {

    private NavTree navTree;

    public NavTreeChangedEvent(NavTree navTree) {
        this.navTree = navTree;
    }

    public NavTree getNavTree() {
        return navTree;
    }
}
