package org.dashbuilder.client.cms.widget;

import org.dashbuilder.client.cms.resources.i18n.ContentManagerI18n;
import org.dashbuilder.client.navigation.plugin.PerspectivePluginManager;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.ext.plugin.model.Plugin;
import org.uberfire.ext.plugin.model.PluginType;

import java.util.Arrays;
import java.util.Collections;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class PerspectivesExplorerTest {
    @Mock
    PerspectivesExplorer.View viewM;
    @Mock
    PerspectivePluginManager perspectivePluginManagerM;
    @Mock
    PlaceManager placeManagerM;
    @Mock
    ContentManagerI18n i18nM;

    @Test
    public void showShouldDisplayEmptyMessage_whenNoPerspectivesAvailable() {
        when(perspectivePluginManagerM.getPerspectivePlugins())
                .thenReturn(Collections.emptyList());

        PerspectivesExplorer testedPE = new PerspectivesExplorer(viewM, perspectivePluginManagerM, placeManagerM, i18nM);
        testedPE.show();

        verify(viewM).clear();
        verify(viewM).showEmpty(anyString());
    }

    @Test
    public void showShouldAddPerspectivesToViewInAlphabeticOrder_whenPerspectivesAvailable() {
        Plugin a = mkPlugin("a"),
                b = mkPlugin("B"),
                c = mkPlugin("cEE");

        when(perspectivePluginManagerM.getPerspectivePlugins())
                .thenReturn(Arrays.asList(b, c, a)); //Return list NOT sorted by name
        InOrder inOrder = inOrder(viewM);

        PerspectivesExplorer testedPE = new PerspectivesExplorer(viewM, perspectivePluginManagerM, placeManagerM, i18nM);
        testedPE.show();

        inOrder.verify(viewM).clear();
        inOrder.verify(viewM).addPerspective(eq("a"), any());
        inOrder.verify(viewM).addPerspective(eq("B"), any());
        inOrder.verify(viewM).addPerspective(eq("cEE"), any());
    }

    private Plugin mkPlugin(String name) {
        return new Plugin(name, PluginType.PERSPECTIVE, null);
    }
}