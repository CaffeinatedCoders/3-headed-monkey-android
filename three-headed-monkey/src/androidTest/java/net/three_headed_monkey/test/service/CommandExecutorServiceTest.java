package net.three_headed_monkey.test.service;

import android.content.Intent;
import android.test.AndroidTestCase;
import android.test.ApplicationTestCase;
import android.test.ServiceTestCase;

import net.three_headed_monkey.ThreeHeadedMonkeyApplication;
import net.three_headed_monkey.commands.Command;
import net.three_headed_monkey.commands.CommandPrototypeManager;
import net.three_headed_monkey.communication.OutgoingCommunication;
import net.three_headed_monkey.service.CommandExecutorService;


public class CommandExecutorServiceTest extends ServiceTestCase<CommandExecutorService> {

    ThreeHeadedMonkeyApplication application;
    DummyCommunication dummyCommunication = null;

    public CommandExecutorServiceTest() {
        super(CommandExecutorService.class);
    }


    @Override
    protected void setUp() throws Exception {
        super.setUp();
        application = new ThreeHeadedMonkeyApplication();
        setApplication(application);
//        application.onCreate();
        application.commandPrototypeManager = new CommandPrototypeManager(application);
        dummyCommunication = new DummyCommunication(application);
        application.commandPrototypeManager.initPrototypes();
        application.commandPrototypeManager.getPrototypeCommandList().add(new DummyCommand(application));
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Starts the Command executor service with a string, waits some time, and then checks if there is an response
     */
    public void testCompleteCommandLifecycle() {
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

    class DummyCommunication extends OutgoingCommunication {
        public String lastMessage = null;

        public DummyCommunication(ThreeHeadedMonkeyApplication application) {
            super(application);
        }

        @Override
        public void sendMessage(String text) {
            lastMessage = text;
        }
    }

    class DummyCommand extends Command {

        public DummyCommand(ThreeHeadedMonkeyApplication application) {
            super(application);
        }

        @Override
        public OutgoingCommunication getOutgoingCommunication() {
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
