/*******************************************************************************
 * Copyright (c) 2012-2016 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package org.eclipse.che.plugin.nodejsdbg.server;

import org.eclipse.che.api.debug.shared.model.Breakpoint;
import org.eclipse.che.plugin.nodejsdbg.server.parser.GdbContinue;
import org.eclipse.che.plugin.nodejsdbg.server.parser.GdbInfoBreak;
import org.eclipse.che.plugin.nodejsdbg.server.parser.GdbInfoLine;
import org.eclipse.che.plugin.nodejsdbg.server.parser.GdbInfoProgram;
import org.eclipse.che.plugin.nodejsdbg.server.parser.GdbPType;
import org.eclipse.che.plugin.nodejsdbg.server.parser.GdbPrint;
import org.eclipse.che.plugin.nodejsdbg.server.parser.GdbRun;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

/**
 * @author Anatoliy Bazko
 */
public class NodeJsDebugConnectorTest {

    private String               source;
    private NodeJsDebugConnector connector;

    @BeforeClass
    public void beforeClass() throws Exception {
        source = NodeJsDebugConnectorTest.class.getResource("/app.js").getFile();
    }

    @BeforeMethod
    public void setUp() throws Exception {
        connector = NodeJsDebugConnector.start(source);
    }

    @AfterMethod
    public void tearDown() throws Exception {
        connector.stop();
    }

    @Test
    public void testInit() throws Exception {
        assertNotNull(connector.getDbgVersion());
        assertNotNull(connector.getDbgVersion().getName());
        assertNotNull(connector.getDbgVersion().getVersion());
    }

    @Test
    public void testQuit() throws Exception {
        connector.quit();
    }

    @Test
    public void testBreakpoints() throws Exception {
        connector.breakpoint(7);
        connector.clear(7);

        connector.breakpoint("h.cpp", 8);
        connector.clear("h.cpp", 8);

        connector.breakpoint(7);
        connector.breakpoint(8);

        GdbInfoBreak gdbInfoBreak = connector.infoBreak();
        List<Breakpoint> breakpoints = gdbInfoBreak.getBreakpoints();

        assertEquals(breakpoints.size(), 2);

        connector.delete();

        gdbInfoBreak = connector.infoBreak();
        breakpoints = gdbInfoBreak.getBreakpoints();

        assertTrue(breakpoints.isEmpty());
    }

    @Test
    public void testRun() throws Exception {
        connector.breakpoint(7);

        GdbRun gdbRun = connector.run();

        assertNotNull(gdbRun.getBreakpoint());
    }

    @Test
    public void testInfoLine() throws Exception {
        connector.breakpoint(7);
        connector.run();

        GdbInfoLine gdbInfoLine = connector.infoLine();

        assertNotNull(gdbInfoLine.getLocation());
        assertEquals(gdbInfoLine.getLocation().getLineNumber(), 7);
        assertEquals(gdbInfoLine.getLocation().getTarget(), "h.cpp");
    }

    @Test
    public void testStep() throws Exception {
        connector.breakpoint(7);
        connector.run();

        GdbInfoLine gdbInfoLine = connector.step();
        assertNotNull(gdbInfoLine.getLocation());

        gdbInfoLine = connector.step();
        assertNotNull(gdbInfoLine.getLocation());
    }

    @Test
    public void testNext() throws Exception {
        connector.breakpoint(7);
        connector.run();

        GdbInfoLine gdbInfoLine = connector.next();

        assertNotNull(gdbInfoLine.getLocation());
        assertEquals(gdbInfoLine.getLocation().getLineNumber(), 5);
        assertEquals(gdbInfoLine.getLocation().getTarget(), "h.cpp");

        gdbInfoLine = connector.next();

        assertNotNull(gdbInfoLine.getLocation());
        assertEquals(gdbInfoLine.getLocation().getLineNumber(), 6);
        assertEquals(gdbInfoLine.getLocation().getTarget(), "h.cpp");
    }

    @Test
    public void testVariables() throws Exception {
        connector.breakpoint(7);
        connector.run();

        GdbPrint gdbPrint = connector.print("i");
        assertEquals(gdbPrint.getValue(), "0");

        connector.setVar("i", "1");

        gdbPrint = connector.print("i");
        assertEquals(gdbPrint.getValue(), "1");

        GdbPType gdbPType = connector.ptype("i");
        assertEquals(gdbPType.getType(), "int");
    }

    @Test
    public void testInfoProgram() throws Exception {
        GdbInfoProgram gdbInfoProgram = connector.infoProgram();
        assertNull(gdbInfoProgram.getStoppedAddress());

        connector.breakpoint(4);
        connector.run();

        gdbInfoProgram = connector.infoProgram();
        assertNotNull(gdbInfoProgram.getStoppedAddress());

        GdbContinue gdbContinue = connector.cont();
        assertNull(gdbContinue.getBreakpoint());

        gdbInfoProgram = connector.infoProgram();
        assertNull(gdbInfoProgram.getStoppedAddress());
    }
}
