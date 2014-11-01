package net.three_headed_monkey.ui;

import android.content.Intent;

import net.three_headed_monkey.R;
import net.three_headed_monkey.data.ServiceInfo;
import net.three_headed_monkey.test_utils.RobolectricGradleTestRunner;
import net.three_headed_monkey.test_utils.TestBase;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.shadows.ShadowActivity;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

@Ignore("Robolectric doesn't support recyclerview yet")
@RunWith(RobolectricGradleTestRunner.class)
public class ServiceListActivityTest extends TestBase {

    ServiceListActivity_ activity;
    ShadowActivity shadowActivity;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        activity = Robolectric.buildActivity(ServiceListActivity_.class).create().resume().get();
        shadowActivity = Robolectric.shadowOf(activity);
        application.serviceSettings.getAll().clear();
    }

    @Test
    public void testListShowsItems() {
        application.serviceSettings.add(new ServiceInfo("url1", 1111, "somekey1"));
        application.serviceSettings.add(new ServiceInfo("url2", 2222, "somekey2"));
        application.serviceSettings.add(new ServiceInfo("url3", 3333, "somekey3"));

        assertThat(activity.service_list_rv.getAdapter(), equalTo(activity.adapter));
        assertThat(activity.adapter.getItemCount(), equalTo(3));
    }

    @Test
    public void testAddButtonShouldStartActivity() {
        activity.getActionBar().setSelectedNavigationItem(R.id.action_add);
        Intent intent = shadowActivity.getNextStartedActivity();
        assertThat(intent.getComponent().getClassName(), equalTo(ServiceAddActivity_.class.getName()));
    }

}
