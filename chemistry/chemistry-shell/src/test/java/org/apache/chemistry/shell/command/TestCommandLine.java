package org.apache.chemistry.shell.command;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.apache.chemistry.shell.app.Application;
import org.apache.chemistry.shell.command.Cmd;
import org.apache.chemistry.shell.command.Command;
import org.apache.chemistry.shell.command.CommandException;
import org.apache.chemistry.shell.command.CommandLine;
import org.apache.chemistry.shell.command.CommandRegistry;

public class TestCommandLine extends Assert {

    CommandRegistry reg;

    @Before
    public void setUp() {
        Map<String, Command> map = new HashMap<String,Command>();
        map.put("cmd", new DummyCommand());
        reg = new CommandRegistry(map);
    }

    @Test
    public void testWithoutOption() throws CommandException {
        String line = "cmd value";
        CommandLine commandLine = new CommandLine(reg, line);

        assertEquals("cmd", commandLine.getCommand().getName());
        assertEquals("value", commandLine.getParameterValue("arg1"));
        assertNull(commandLine.getParameter("-r"));
        assertNull(commandLine.getParameter("-o"));
        //assertEquals("tutu", commandLine.getParameterValue("titi"));
    }

    @Test
    public void testWithOption() throws CommandException {
        String line = "cmd -r -o option value";
        CommandLine commandLine = new CommandLine(reg, line);

        assertEquals("cmd", commandLine.getCommand().getName());
        assertEquals("value", commandLine.getParameterValue("arg1"));
        assertEquals("option", commandLine.getParameterValue("-o"));
        assertNotNull(commandLine.getParameter("-r"));
        //assertEquals("tutu", commandLine.getParameterValue("titi"));
    }

    @Ignore // this is not a test, you dummy Maven!
    @Cmd(syntax="cmd [-r] [-o:*] arg1 [titi:file?tutu]", synopsis="")
    class DummyCommand extends Command {
        @Override
        public void run(Application app, CommandLine cmdLine) throws Exception {}
    }

}

