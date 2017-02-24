package org.dashbuilder.client.navigation.plugin;

import org.dashbuilder.client.navigation.NavigationManager;
import org.dashbuilder.client.navigation.event.PerspectivePluginsChangedEvent;
import org.dashbuilder.navigation.NavTree;
import org.dashbuilder.navigation.impl.NavTreeBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.ext.plugin.event.PluginDeleted;

import javax.enterprise.event.Event;

import static org.dashbuilder.navigation.workbench.NavWorkbenchCtx.perspective;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PerspectivePluginManagerTest {

    private static final String ID_TO_DELETE = "Persp1";
    public static final NavTree TEST_NAV_TREE = new NavTreeBuilder()
            .item(ID_TO_DELETE, "name1", "desciption1", true, perspective(ID_TO_DELETE))
            .build();
    @Mock
    private NavigationManager navigationManagerMock;
    @Mock
    private PluginDeleted pluginDeletedEventMock;
    @Mock
    private Event<PerspectivePluginsChangedEvent> perspectiveChangedEventMock;

    @Test
    public void whenPluginDeleted_itIsRemovedFromNavTree() {
        NavTree testTree = TEST_NAV_TREE.cloneTree();

        assertNotNull(testTree.getItemById(ID_TO_DELETE));

        when(navigationManagerMock.getNavTree()).thenReturn(testTree);
        when(pluginDeletedEventMock.getPluginName()).thenReturn(ID_TO_DELETE);

        PerspectivePluginManager testedPluginManager = new PerspectivePluginManager(
                null, null, navigationManagerMock,
                null, null, perspectiveChangedEventMock
        );
        testedPluginManager.onPlugInDeleted(pluginDeletedEventMock);

        assertNull("Plugin should be removed from the tree when PluginDeleted event occurs", testTree.getItemById(ID_TO_DELETE));
        verify(navigationManagerMock).saveNavTree(anyObject(), eq(null));
        verify(perspectiveChangedEventMock).fire(anyObject());
    }
}
