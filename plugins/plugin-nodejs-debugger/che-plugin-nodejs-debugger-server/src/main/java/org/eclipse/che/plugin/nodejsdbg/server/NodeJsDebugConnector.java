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

import org.eclipse.che.api.debugger.server.exceptions.DebuggerException;
import org.eclipse.che.commons.annotation.Nullable;
import org.eclipse.che.plugin.nodejsdbg.server.exception.NodeJsDebuggerException;
import org.eclipse.che.plugin.nodejsdbg.server.exception.NodeJsDebuggerTerminatedException;
import org.eclipse.che.plugin.nodejsdbg.server.parser.DbgOutput;
import org.eclipse.che.plugin.nodejsdbg.server.parser.DbgVersion;
import org.eclipse.che.plugin.nodejsdbg.server.parser.GdbBreak;
import org.eclipse.che.plugin.nodejsdbg.server.parser.GdbClear;
import org.eclipse.che.plugin.nodejsdbg.server.parser.GdbContinue;
import org.eclipse.che.plugin.nodejsdbg.server.parser.GdbDelete;
import org.eclipse.che.plugin.nodejsdbg.server.parser.GdbInfoArgs;
import org.eclipse.che.plugin.nodejsdbg.server.parser.GdbInfoBreak;
import org.eclipse.che.plugin.nodejsdbg.server.parser.GdbInfoLine;
import org.eclipse.che.plugin.nodejsdbg.server.parser.GdbInfoLocals;
import org.eclipse.che.plugin.nodejsdbg.server.parser.GdbInfoProgram;
import org.eclipse.che.plugin.nodejsdbg.server.parser.GdbPType;
import org.eclipse.che.plugin.nodejsdbg.server.parser.GdbPrint;
import org.eclipse.che.plugin.nodejsdbg.server.parser.GdbRun;
import org.eclipse.che.plugin.nodejsdbg.server.parser.GdbTargetRemote;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.constraints.NotNull;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;

/**
 * @author Anatoliy Bazko
 */
public class NodeJsDebugConnector extends NodeJsConnector {
    private static final Logger LOG              = LoggerFactory.getLogger(NodeJsConnector.class);
    private static final String PROCESS_NAME     = "node";
    private static final String OUTPUT_SEPARATOR = "debug> ";

    private final BufferedWriter processWriter;
    private       DbgVersion     dbgVersion;

    NodeJsDebugConnector(String source) throws IOException {
        super(OUTPUT_SEPARATOR, PROCESS_NAME, "debug", source);

        this.processWriter = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
//        this.dbgVersion = version();
//
//        try {
//            gdbVersion = GdbVersion.parse(grabGdbOutput());
//        } catch (InterruptedException | DebuggerException e) {
//            LOG.error(e.getMessage(), e);
//            gdbVersion = new GdbVersion("Unknown", "Unknown");
//        }
    }

    /**
     * Starts GDB.
     */
    public static NodeJsDebugConnector start(String source) throws IOException {
        return new NodeJsDebugConnector(source);
    }

    /**
     * `version` command.
     */
    public DbgVersion version() throws IOException, NodeJsDebuggerException, InterruptedException {
        sendCommand("version", false);
        return null;
    }

    /**
     * `quit` command.
     */
    public void quit() throws IOException, NodeJsDebuggerException, InterruptedException {
        try {
            sendCommand("quit", false);
        } finally {
            stop();
        }
    }


    public DbgVersion getDbgVersion() {
        return dbgVersion;
    }

    /**
     * `run` command.
     */
    public GdbRun run() throws IOException, InterruptedException, DebuggerException {
        DbgOutput dbgOutput = sendCommand("run");
        return GdbRun.parse(dbgOutput);
    }

    /**
     * `set var` command.
     */
    public void setVar(String varName, String value) throws IOException, InterruptedException, DebuggerException {
        String command = "set var " + varName + "=" + value;
        sendCommand(command);
    }

    /**
     * `ptype` command.
     */
    public GdbPType ptype(String variable) throws IOException, InterruptedException, DebuggerException {
        DbgOutput dbgOutput = sendCommand("ptype " + variable);
        return GdbPType.parse(dbgOutput);
    }

    /**
     * `print` command.
     */
    public GdbPrint print(String variable) throws IOException, InterruptedException, DebuggerException {
        DbgOutput dbgOutput = sendCommand("print " + variable);
        return GdbPrint.parse(dbgOutput);
    }

    /**
     * `continue` command.
     */
    public GdbContinue cont() throws IOException, InterruptedException, DebuggerException {
        DbgOutput dbgOutput = sendCommand("continue");
        return GdbContinue.parse(dbgOutput);
    }

    /**
     * `step` command.
     */
    public GdbInfoLine step() throws IOException, InterruptedException, DebuggerException {
        sendCommand("step");
        return infoLine();
    }

    /**
     * `finish` command.
     */
    public GdbInfoLine finish() throws IOException, InterruptedException, DebuggerException {
        sendCommand("finish");
        return infoLine();
    }

    /**
     * `next` command.
     */
    @Nullable
    public GdbInfoLine next() throws IOException, InterruptedException, DebuggerException {
        sendCommand("next");

        GdbInfoProgram gdbInfoProgram = infoProgram();
        if (gdbInfoProgram.getStoppedAddress() == null) {
            return null;
        }

        return infoLine();
    }

    /**
     * `break` command
     */
    public void breakpoint(@NotNull String file, int lineNumber) throws IOException,
                                                                        InterruptedException,
                                                                        DebuggerException {
        String command = "break " + file + ":" + lineNumber;
        DbgOutput dbgOutput = sendCommand(command);
        GdbBreak.parse(dbgOutput);
    }

    /**
     * `break` command
     */
    public void breakpoint(int lineNumber) throws IOException, InterruptedException, DebuggerException {
        String command = "break " + lineNumber;
        DbgOutput dbgOutput = sendCommand(command);
        GdbBreak.parse(dbgOutput);
    }

    /**
     * `clear` command.
     */
    public void clear(@NotNull String file, int lineNumber) throws IOException, InterruptedException, DebuggerException {
        String command = "clear " + file + ":" + lineNumber;
        DbgOutput dbgOutput = sendCommand(command);

        GdbClear.parse(dbgOutput);
    }

    /**
     * `clear` command.
     */
    public void clear(int lineNumber) throws IOException, InterruptedException, DebuggerException {
        String command = "clear " + lineNumber;
        DbgOutput dbgOutput = sendCommand(command);

        GdbClear.parse(dbgOutput);
    }

    /**
     * `delete` command.
     */
    public void delete() throws IOException, InterruptedException, DebuggerException {
        DbgOutput dbgOutput = sendCommand("delete");
        GdbDelete.parse(dbgOutput);
    }

    /**
     * `target remote` command.
     */
    public void targetRemote(String host, int port) throws IOException, InterruptedException, DebuggerException {
        String command = "target remote " + (host != null ? host : "") + ":" + port;
        DbgOutput dbgOutput = sendCommand(command);
        GdbTargetRemote.parse(dbgOutput);
    }

    /**
     * `info break` command.
     */
    public GdbInfoBreak infoBreak() throws IOException, InterruptedException, DebuggerException {
        DbgOutput dbgOutput = sendCommand("info break");
        return GdbInfoBreak.parse(dbgOutput);
    }

    /**
     * `info args` command.
     */
    public GdbInfoArgs infoArgs() throws IOException, InterruptedException, DebuggerException {
        DbgOutput dbgOutput = sendCommand("info args");
        return GdbInfoArgs.parse(dbgOutput);
    }

    /**
     * `info locals` command.
     */
    public GdbInfoLocals infoLocals() throws IOException, InterruptedException, DebuggerException {
        DbgOutput dbgOutput = sendCommand("info locals");
        return GdbInfoLocals.parse(dbgOutput);
    }

    /**
     * `info line` command.
     */
    public GdbInfoLine infoLine() throws IOException, InterruptedException, DebuggerException {
        DbgOutput dbgOutput = sendCommand("info line");
        return GdbInfoLine.parse(dbgOutput);
    }

    /**
     * `info program` command.
     */
    public GdbInfoProgram infoProgram() throws IOException, InterruptedException, DebuggerException {
        DbgOutput dbgOutput = sendCommand("info program");
        return GdbInfoProgram.parse(dbgOutput);
    }

    private DbgOutput sendCommand(String command) throws IOException,
                                                         NodeJsDebuggerTerminatedException,
                                                         InterruptedException {
        return sendCommand(command, true);
    }

    private synchronized DbgOutput sendCommand(String command, boolean grabOutput) throws IOException,
                                                                                          NodeJsDebuggerTerminatedException,
                                                                                          InterruptedException {
        LOG.debug(command);

        processWriter.write(command);
        processWriter.newLine();
        processWriter.flush();

        return grabOutput ? grabOutput() : null;
    }

}
