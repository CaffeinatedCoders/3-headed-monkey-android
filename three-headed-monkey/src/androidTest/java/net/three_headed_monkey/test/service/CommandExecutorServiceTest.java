package net.three_headed_monkey.test.service;

import android.content.Intent;
import android.test.ServiceTestCase;

import net.three_headed_monkey.ThreeHeadedMonkeyApplication;
import net.three_headed_monkey.commands.Command;
import net.three_headed_monkey.commands.CommandPrototypeManager;
import net.three_headed_monkey.commands.HelpCommand;
import net.three_headed_monkey.commands.LogCommand;
import net.three_headed_monkey.communication.OutgoingCommandCommunication;
import net.three_headed_monkey.service.CommandExecutorService;


public class CommandExecutorServiceTest extends ServiceTestCase<CommandExecutorService> {

    ThreeHeadedMonkeyApplication application;
    DummyCommandCommunication dummyCommunication = null;

    public CommandExecutorServiceTest() {
        super(CommandExecutorService.class);
    }


    @Override
    protected void setUp() throws Exception {
        super.setUp();
        application = new ThreeHeadedMonkeyApplication();
        setApplication(application);
//        application.onCreate();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Starts the Command executor service with a string, waits some time, and then checks if there is an response
     */
    public void testCompleteCommandLifecycle() {
        initDummyCommand();
        assertNull(dummyCommunication.lastMessage);
        Intent intent = new Intent();
        intent.putExtra(CommandExecutorService.INTENT_COMMAND_STRING_PARAM, "TestCommand");
        startService(intent);
        try {
            Thread.sleep(500,0);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertNotNull(dummyCommunication.lastMessage);
        assertEquals("I ran!", dummyCommunication.lastMessage);
    }

    /**
     * Inits the dummy Command
     */
    private void initDummyCommand() {
        application.commandPrototypeManager = new CommandPrototypeManager(application);
        dummyCommunication = new DummyCommandCommunication(application);
//        application.commandPrototypeManager.initPrototypes();
        application.commandPrototypeManager.getPrototypeCommandList().add(new LogCommand(application));
        application.commandPrototypeManager.getPrototypeCommandList().add(new DummyCommand(application));
        application.commandPrototypeManager.getPrototypeCommandList().add(new HelpCommand(application));
    }

    class DummyCommandCommunication extends OutgoingCommandCommunication {
        public String lastMessage = null;

        public DummyCommandCommunication(ThreeHeadedMonkeyApplication application) {
            super(application);
        }

        @Override
        public void sendMessage(String text) {
            lastMessage = text;
        }

        @Override
        public void notifyCommandFinished() {

        }
    }

    class DummyCommand extends Command {

        public DummyCommand(ThreeHeadedMonkeyApplication application) {
            super(application);
        }

        @Override
        public OutgoingCommandCommunication getOutgoingCommandCommunication() {
            return dummyCommunication;
        }

        @Override
        protected void doExecute(String command) {
            sendResponse("I ran!");
        }

        @Override
        protected boolean respondsToCommand(String command) {
            return command.equals("TestCommand");
        }

        @Override
        protected String getShortUsageText() {
            return null;
        }
    }

}
