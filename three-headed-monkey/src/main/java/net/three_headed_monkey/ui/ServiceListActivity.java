package net.three_headed_monkey.ui;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;

import net.three_headed_monkey.R;
import net.three_headed_monkey.ThreeHeadedMonkeyApplication;
import net.three_headed_monkey.data.ServiceInfo;
import net.three_headed_monkey.ui.adapter.ServiceAdapter;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.App;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

@EActivity(R.layout.activity_service_list)
public class ServiceListActivity extends Activity {

    @ViewById
    RecyclerView service_list_rv;

    @App
    ThreeHeadedMonkeyApplication application;


    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView.Adapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initList();
    }

    public void initList() {
        layoutManager = new LinearLayoutManager(this);
        service_list_rv.setLayoutManager(layoutManager);

        adapter = new ServiceAdapter(application.serviceSettings, this);
        service_list_rv.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_service_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_add) {
            ServiceAddActivity_.intent(this).start();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
