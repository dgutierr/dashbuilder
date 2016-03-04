package org.dashbuilder.dataprovider.sql.dialect;

import java.util.Collections;

import org.dashbuilder.dataprovider.sql.model.Column;
import org.dashbuilder.dataprovider.sql.model.Select;
import org.dashbuilder.dataprovider.sql.model.Table;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class DB2DialectTest {

    @Spy
    DB2Dialect dialect;

    @Mock
    Select select;

    Table table;

    @Before
    public void setup() {
        when(select.getColumns()).thenReturn(Collections.<Column>emptyList());
        table = new Table("table");
        when(select.getFromTable()).thenReturn(table);
        when(select.getTable()).thenReturn(table);
    }

    @Test
    public void testNoOffsetAndLimit() {
        when(select.getLimit()).thenReturn(0);
        when(select.getOffset()).thenReturn(0);
        final String sql = dialect.getSQL(select);
        assertEquals("SELECT * FROM table", sql);
    }

    @Test
    public void testOffset() {
        when(select.getLimit()).thenReturn(0);
        when(select.getOffset()).thenReturn(1);
        final String sql = dialect.getSQL(select);
        assertEquals("SELECT * FROM (SELECT Q.*, ROWNUMBER() OVER(ORDER BY ORDER OF Q) AS RN FROM (SELECT * FROM table) AS Q)  WHERE RN > 1 ORDER BY RN", sql);
    }

    @Test
    public void testLimit() {
        when(select.getLimit()).thenReturn(1);
        when(select.getOffset()).thenReturn(0);
        final String sql = dialect.getSQL(select);
        assertEquals("SELECT * FROM table FETCH FIRST 1 ROWS ONLY", sql);
    }

    @Test
    public void testOffsetAndLimit() {
        when(select.getLimit()).thenReturn(1);
        when(select.getOffset()).thenReturn(1);
        final String sql = dialect.getSQL(select);
        assertEquals("SELECT * FROM (SELECT Q.*, ROWNUMBER() OVER(ORDER BY ORDER OF Q) AS RN FROM (SELECT * FROM table FETCH FIRST 2 ROWS ONLY) AS Q)  WHERE RN > 1 ORDER BY RN", sql);
    }

}