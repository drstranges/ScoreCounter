package ua.napps.scorekeeper.log;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.Group;
import androidx.core.app.ActivityOptionsCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import ua.napps.scorekeeper.R;
import ua.napps.scorekeeper.settings.LocalSettings;
import ua.napps.scorekeeper.utils.Singleton;
import ua.napps.scorekeeper.utils.ViewUtil;

public class LogActivity extends AppCompatActivity {

    private LogAdapter mAdapter;
    private Group emptyState;

    public static void start(Activity activity) {
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity);
        Intent intent = new Intent(activity, LogActivity.class);
        activity.startActivity(intent, options.toBundle());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);

        // get back button
        Toolbar toolbar = findViewById(R.id.toolbar_log_main);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // setup recycler view and adapter
        RecyclerView mRecyclerView = findViewById(R.id.rv_log_main);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mRecyclerView.getContext(),
                mLayoutManager.getOrientation());
        mRecyclerView.addItemDecoration(dividerItemDecoration);

        mRecyclerView.setHasFixedSize(true);

        mAdapter = new LogAdapter(Singleton.getInstance().getLogEntries());
        mRecyclerView.setAdapter(mAdapter);

        emptyState = findViewById(R.id.g_empty_history);
        emptyState.setVisibility(mAdapter.getItemCount() > 0 ? View.GONE : View.VISIBLE);
        findViewById(R.id.btn_close).setOnClickListener(v -> finishAfterTransition());

        boolean isLightTheme = LocalSettings.isLightTheme();
        if (isLightTheme) {
            ViewUtil.setLightStatusBar(this);
        } else {
            ViewUtil.clearLightStatusBar(this);
        }
        ViewUtil.setNavBarColor(this, isLightTheme);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (mAdapter != null && mAdapter.getItemCount() > 0) {
            getMenuInflater().inflate(R.menu.menu_delete, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_delete) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.dialog_confirmation_question)
                    .setPositiveButton(R.string.dialog_yes, (dialog, which) -> {
                        Singleton.getInstance().clearLogEntries();
                        mAdapter.notifyDataSetChanged();
                        emptyState.setVisibility(View.VISIBLE);
                    })
                    .setNegativeButton(R.string.dialog_no, (dialog, which) -> dialog.dismiss());
            builder.create().show();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
