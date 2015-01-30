package net.three_headed_monkey.ui;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import net.three_headed_monkey.R;
import net.three_headed_monkey.communication.OutgoingBroadcastCommandCommunication;
import net.three_headed_monkey.communication.OutgoingCommandCommunicationFactory;
import net.three_headed_monkey.service.CommandExecutorService;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

@EActivity(R.layout.activity_command_shell)
public class CommandShellActivity extends Activity {

    @ViewById
    TextView text_shell;
    @ViewById
    EditText edit_command;
    @ViewById
    Button button_execute;

    BroadcastCommunicationReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        IntentFilter filter = new IntentFilter(OutgoingBroadcastCommandCommunication.INTENT_ACTION);
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        receiver = new BroadcastCommunicationReceiver();
        registerReceiver(receiver, filter);
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(receiver);
        super.onDestroy();
    }

    @Click(R.id.button_execute)
    public void executeCommand() {
        String commandStr = edit_command.getText().toString();
        commandStr = commandStr.trim();
        if (commandStr.isEmpty())
            return;

        Intent intent = new Intent(this, CommandExecutorService.class);
        intent.putExtra(CommandExecutorService.INTENT_COMMAND_STRING_PARAM, commandStr);
        intent.putExtra(CommandExecutorService.INTENT_OUTGOING_COMMUNICATION_TYPE_PARAM, OutgoingCommandCommunicationFactory.OUTGOING_COMMUNICATION_TYPE_BROADCAST);

        text_shell.append("> " + commandStr + "\n");
        edit_command.setText("");
        startService(intent);
    }

    public class BroadcastCommunicationReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String text = intent.getStringExtra(OutgoingBroadcastCommandCommunication.INTENT_MESSAGE_PARAM);
            text_shell.append(text + "\n");
        }

    }

}
