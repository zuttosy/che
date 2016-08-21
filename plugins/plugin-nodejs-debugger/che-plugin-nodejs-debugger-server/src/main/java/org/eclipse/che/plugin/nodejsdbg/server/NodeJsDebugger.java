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
import org.eclipse.che.api.debug.shared.model.DebuggerInfo;
import org.eclipse.che.api.debug.shared.model.Location;
import org.eclipse.che.api.debug.shared.model.SimpleValue;
import org.eclipse.che.api.debug.shared.model.StackFrameDump;
import org.eclipse.che.api.debug.shared.model.Variable;
import org.eclipse.che.api.debug.shared.model.VariablePath;
import org.eclipse.che.api.debug.shared.model.action.ResumeAction;
import org.eclipse.che.api.debug.shared.model.action.StartAction;
import org.eclipse.che.api.debug.shared.model.action.StepIntoAction;
import org.eclipse.che.api.debug.shared.model.action.StepOutAction;
import org.eclipse.che.api.debug.shared.model.action.StepOverAction;
import org.eclipse.che.api.debug.shared.model.impl.DebuggerInfoImpl;
import org.eclipse.che.api.debug.shared.model.impl.SimpleValueImpl;
import org.eclipse.che.api.debug.shared.model.impl.StackFrameDumpImpl;
import org.eclipse.che.api.debug.shared.model.impl.VariableImpl;
import org.eclipse.che.api.debug.shared.model.impl.VariablePathImpl;
import org.eclipse.che.api.debug.shared.model.impl.event.BreakpointActivatedEventImpl;
import org.eclipse.che.api.debug.shared.model.impl.event.DisconnectEventImpl;
import org.eclipse.che.api.debug.shared.model.impl.event.SuspendEventImpl;
import org.eclipse.che.api.debugger.server.Debugger;
import org.eclipse.che.api.debugger.server.exceptions.DebuggerException;
import org.eclipse.che.plugin.nodejsdbg.server.exception.NodeJsDebuggerException;
import org.eclipse.che.plugin.nodejsdbg.server.exception.NodeJsDebuggerParseException;
import org.eclipse.che.plugin.nodejsdbg.server.exception.NodeJsDebuggerTerminatedException;
import org.eclipse.che.plugin.nodejsdbg.server.parser.GdbContinue;
import org.eclipse.che.plugin.nodejsdbg.server.parser.GdbInfoBreak;
import org.eclipse.che.plugin.nodejsdbg.server.parser.GdbInfoLine;
import org.eclipse.che.plugin.nodejsdbg.server.parser.GdbInfoProgram;
import org.eclipse.che.plugin.nodejsdbg.server.parser.GdbPrint;
import org.eclipse.che.plugin.nodejsdbg.server.parser.GdbRun;
import org.eclipse.che.plugin.nodejsdbg.server.parser.DbgVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static java.nio.file.Files.exists;
import static java.util.Collections.singletonList;

/**
 * Connects to Node js debug.
 *
 * @author Anatoliy Bazko
 */
public class NodeJsDebugger implements Debugger {
    private static final Logger LOG                 = LoggerFactory.getLogger(NodeJsDebugger.class);
    private static final int    CONNECTION_ATTEMPTS = 5;

    private final String name;
    private final String version;
    private final String source;

    private final NodeJsDebugConnector connector;
    private final DebuggerCallback     debuggerCallback;

    NodeJsDebugger(String name,
                   String version,
                   String source,
                   NodeJsDebugConnector connector,
                   DebuggerCallback debuggerCallback) {
        this.name = name;
        this.version = version;
        this.source = source;
        this.connector = connector;
        this.debuggerCallback = debuggerCallback;
    }

    public String getName() {
        return name;
    }

    public String getVersion() {
        return version;
    }

    public String getSource() {
        return source;
    }

    public static NodeJsDebugger newInstance(String source,
                                             DebuggerCallback debuggerCallback) throws DebuggerException {
        if (!exists(Paths.get(source))) {
            throw new DebuggerException("Can't start debug: source " + source + " not found");
        }

        for (int i = 0; i < CONNECTION_ATTEMPTS - 1; i++) {
            try {
                return init(source, debuggerCallback);
            } catch (DebuggerException e) {
                LOG.error("Connection attempt " + i + ": " + e.getMessage(), e);
            }
        }

        return init(source, debuggerCallback);
    }

    private static NodeJsDebugger init(String source,
                                       DebuggerCallback debuggerCallback) throws DebuggerException {

        NodeJsDebugConnector gdb;
        try {
            gdb = NodeJsDebugConnector.start(source);
        } catch (IOException e) {
            throw new DebuggerException("Can't start GDB: " + e.getMessage(), e);
        }

//        try {
//            gdb.file(source);
//            if (port > 0) {
//                gdb.targetRemote(host, port);
//            }
//        } catch (DebuggerException | IOException | InterruptedException e) {
//            try {
//                gdb.quit();
//            } catch (IOException | InterruptedException | NodejsDebuggerException e1) {
//                LOG.error("Can't stop GDB: " + e1.getMessage(), e1);
//            }
//            throw new DebuggerException("Can't initialize GDB: " + e.getMessage(), e);
//        }

        DbgVersion dbgVersion = gdb.getDbgVersion();
        return new NodeJsDebugger(dbgVersion.getVersion(),
                                  dbgVersion.getName(),
                                  source,
                                  gdb,
                                  debuggerCallback);
    }

    @Override
    public DebuggerInfo getInfo() throws DebuggerException {
        return new DebuggerInfoImpl(null, 0, name, version, 0, source);
    }

    @Override
    public void disconnect() {
        debuggerCallback.onEvent(new DisconnectEventImpl());

        try {
            connector.quit();
        } catch (IOException | InterruptedException | NodeJsDebuggerException e) {
            LOG.error(e.getMessage(), e);
        }
    }

    @Override
    public void addBreakpoint(Breakpoint breakpoint) throws DebuggerException {
        try {
            Location location = breakpoint.getLocation();
            if (location.getTarget() == null) {
                connector.breakpoint(location.getLineNumber());
            } else {
                connector.breakpoint(location.getTarget(), location.getLineNumber());
            }

            debuggerCallback.onEvent(new BreakpointActivatedEventImpl(breakpoint));
        } catch (NodeJsDebuggerTerminatedException e) {
            disconnect();
            throw e;
        } catch (IOException | NodeJsDebuggerParseException | InterruptedException e) {
            throw new DebuggerException("Can't add breakpoint: " + breakpoint + ". " + e.getMessage(), e);
        }
    }

    @Override
    public void deleteBreakpoint(Location location) throws DebuggerException {
        try {
            if (location.getTarget() == null) {
                connector.clear(location.getLineNumber());
            } else {
                connector.clear(location.getTarget(), location.getLineNumber());
            }
        } catch (NodeJsDebuggerTerminatedException e) {
            disconnect();
            throw e;
        } catch (IOException | NodeJsDebuggerParseException | InterruptedException e) {
            throw new DebuggerException("Can't delete breakpoint: " + location + ". " + e.getMessage(), e);
        }
    }

    @Override
    public void deleteAllBreakpoints() throws DebuggerException {
        try {
            connector.delete();
        } catch (NodeJsDebuggerTerminatedException e) {
            disconnect();
            throw e;
        } catch (IOException | NodeJsDebuggerParseException | InterruptedException e) {
            throw new DebuggerException("Can't delete all breakpoints. " + e.getMessage(), e);
        }
    }

    @Override
    public List<Breakpoint> getAllBreakpoints() throws DebuggerException {
        try {
            GdbInfoBreak gdbInfoBreak = connector.infoBreak();
            return gdbInfoBreak.getBreakpoints();
        } catch (NodeJsDebuggerTerminatedException e) {
            disconnect();
            throw e;
        } catch (IOException | NodeJsDebuggerParseException | InterruptedException e) {
            throw new DebuggerException("Can't get all breakpoints. " + e.getMessage(), e);
        }
    }

    @Override
    public void start(StartAction action) throws DebuggerException {
        try {
            for (Breakpoint b : action.getBreakpoints()) {
                try {
                    addBreakpoint(b);
                } catch (DebuggerException e) {
                    // can't add breakpoint, skip it
                }
            }

            GdbRun gdbRun = connector.run();
            Breakpoint breakpoint = gdbRun.getBreakpoint();

            if (breakpoint != null) {
                debuggerCallback.onEvent(new SuspendEventImpl(breakpoint.getLocation()));
            } else {
                GdbInfoProgram gdbInfoProgram = connector.infoProgram();
                if (gdbInfoProgram.getStoppedAddress() == null) {
                    disconnect();
                }
            }
        } catch (NodeJsDebuggerTerminatedException e) {
            disconnect();
            throw e;
        } catch (IOException | NodeJsDebuggerParseException | InterruptedException e) {
            throw new DebuggerException("Error during running. " + e.getMessage(), e);
        }
    }

    @Override
    public void stepOver(StepOverAction action) throws DebuggerException {
        try {
            GdbInfoLine gdbInfoLine = connector.next();
            if (gdbInfoLine == null) {
                disconnect();
                return;
            }

            debuggerCallback.onEvent(new SuspendEventImpl(gdbInfoLine.getLocation()));
        } catch (NodeJsDebuggerTerminatedException e) {
            disconnect();
            throw e;
        } catch (IOException | NodeJsDebuggerParseException | InterruptedException e) {
            throw new DebuggerException("Step into error. " + e.getMessage(), e);
        }
    }

    @Override
    public void stepInto(StepIntoAction action) throws DebuggerException {
        try {
            GdbInfoLine gdbInfoLine = connector.step();
            if (gdbInfoLine == null) {
                disconnect();
                return;
            }

            debuggerCallback.onEvent(new SuspendEventImpl(gdbInfoLine.getLocation()));
        } catch (NodeJsDebuggerTerminatedException e) {
            disconnect();
            throw e;
        } catch (IOException | NodeJsDebuggerParseException | InterruptedException e) {
            throw new DebuggerException("Step into error. " + e.getMessage(), e);
        }
    }

    @Override
    public void stepOut(StepOutAction action) throws DebuggerException {
        try {
            GdbInfoLine gdbInfoLine = connector.finish();
            if (gdbInfoLine == null) {
                disconnect();
                return;
            }

            debuggerCallback.onEvent(new SuspendEventImpl(gdbInfoLine.getLocation()));
        } catch (NodeJsDebuggerTerminatedException e) {
            disconnect();
            throw e;
        } catch (IOException | NodeJsDebuggerParseException | InterruptedException e) {
            throw new DebuggerException("Step out error. " + e.getMessage(), e);
        }
    }

    @Override
    public void resume(ResumeAction action) throws DebuggerException {
        try {
            GdbContinue gdbContinue = connector.cont();
            Breakpoint breakpoint = gdbContinue.getBreakpoint();

            if (breakpoint != null) {
                debuggerCallback.onEvent(new SuspendEventImpl(breakpoint.getLocation()));
            } else {
                GdbInfoProgram gdbInfoProgram = connector.infoProgram();
                if (gdbInfoProgram.getStoppedAddress() == null) {
                    disconnect();
                }
            }
        } catch (NodeJsDebuggerTerminatedException e) {
            disconnect();
            throw e;
        } catch (IOException | NodeJsDebuggerParseException | InterruptedException e) {
            throw new DebuggerException("Resume error. " + e.getMessage(), e);
        }
    }

    @Override
    public void setValue(Variable variable) throws DebuggerException {
        try {
            List<String> path = variable.getVariablePath().getPath();
            if (path.isEmpty()) {
                throw new DebuggerException("Variable path is empty");
            }
            connector.setVar(path.get(0), variable.getValue());
        } catch (NodeJsDebuggerTerminatedException e) {
            disconnect();
            throw e;
        } catch (IOException | NodeJsDebuggerParseException | InterruptedException e) {
            throw new DebuggerException("Can't set value for " + variable.getName() + ". " + e.getMessage(), e);
        }
    }

    @Override
    public SimpleValue getValue(VariablePath variablePath) throws DebuggerException {
        try {
            List<String> path = variablePath.getPath();
            if (path.isEmpty()) {
                throw new DebuggerException("Variable path is empty");
            }

            GdbPrint gdbPrint = connector.print(path.get(0));
            return new SimpleValueImpl(Collections.emptyList(), gdbPrint.getValue());
        } catch (NodeJsDebuggerTerminatedException e) {
            disconnect();
            throw e;
        } catch (IOException | NodeJsDebuggerParseException | InterruptedException e) {
            throw new DebuggerException("Can't get value for " + variablePath + ". " + e.getMessage(), e);
        }
    }

    @Override
    public String evaluate(String expression) throws DebuggerException {
        try {
            GdbPrint gdbPrint = connector.print(expression);
            return gdbPrint.getValue();
        } catch (NodeJsDebuggerTerminatedException e) {
            disconnect();
            throw e;
        } catch (IOException | NodeJsDebuggerParseException | InterruptedException e) {
            throw new DebuggerException("Can't evaluate '" + expression + "'. " + e.getMessage(), e);
        }
    }

    /**
     * Dump frame.
     */
    @Override
    public StackFrameDump dumpStackFrame() throws DebuggerException {
        try {
            Map<String, String> locals = connector.infoLocals().getVariables();
            locals.putAll(connector.infoArgs().getVariables());

            List<Variable> variables = new ArrayList<>(locals.size());
            for (Map.Entry<String, String> e : locals.entrySet()) {
                String varName = e.getKey();
                String varValue = e.getValue();
                String varType;
                try {
                    varType = connector.ptype(varName).getType();
                } catch (NodeJsDebuggerParseException pe) {
                    LOG.warn(pe.getMessage(), pe);
                    varType = "";
                }

                VariablePath variablePath = new VariablePathImpl(singletonList(varName));
                VariableImpl variable = new VariableImpl(varType, varName, varValue, true, variablePath, Collections.emptyList(), true);
                variables.add(variable);
            }

            return new StackFrameDumpImpl(Collections.emptyList(), variables);
        } catch (NodeJsDebuggerTerminatedException e) {
            disconnect();
            throw e;
        } catch (IOException | NodeJsDebuggerParseException | InterruptedException e) {
            throw new DebuggerException("Can't dump stack frame. " + e.getMessage(), e);
        }
    }
}
