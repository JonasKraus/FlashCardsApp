package de.uulm.einhoernchen.flashcardsapp.Activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;

import java.util.ArrayList;
import java.util.List;

import de.uulm.einhoernchen.flashcardsapp.Database.DbManager;
import de.uulm.einhoernchen.flashcardsapp.Fragment.Adapter.RecyclerViewAdapterUsers;
import de.uulm.einhoernchen.flashcardsapp.Fragment.Dataset.ContentUsers;
import de.uulm.einhoernchen.flashcardsapp.Fragment.Dataset.ContentUsersOfUserGroup;
import de.uulm.einhoernchen.flashcardsapp.Fragment.Interface.OnFragmentInteractionListenerUser;
import de.uulm.einhoernchen.flashcardsapp.Model.User;
import de.uulm.einhoernchen.flashcardsapp.R;
import de.uulm.einhoernchen.flashcardsapp.Util.Globals;

public class UsersActivity extends AppCompatActivity implements OnFragmentInteractionListenerUser {

    private DbManager db = Globals.getDb();
    private ArrayList<User> checkedUsers = new ArrayList<>();
    private TextView textViewToolbarCheckedUsers;
    private List<User> usersOfGroup;
    private Long groupId = null;

    @Override
    protected void onPostResume() {
        super.onPostResume();

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle extras = getIntent().getExtras();

        Globals.setFragmentManager(getSupportFragmentManager());

        if (extras != null && extras.containsKey("group_id")) {

            groupId = extras.getLong("group_id");

            ContentUsersOfUserGroup contentUsersOfUserGroup = new ContentUsersOfUserGroup();
            contentUsersOfUserGroup.collectItemsFromDb(false, groupId);
            contentUsersOfUserGroup.collectItemsFromServer(false, groupId);

        } else if (extras != null && extras.containsKey("create_message")) {


            // directly call this content to go faster
            ContentUsers contentUsers = new ContentUsers();
            contentUsers.setCreateMessage(true);
            contentUsers.collectItemsFromDb(false);
            contentUsers.collectItemsFromServer(false);

        } else {

            // directly call this content to go faster
            ContentUsers contentUsers = new ContentUsers();
            contentUsers.collectItemsFromDb(false);
            contentUsers.collectItemsFromServer(false);
        }


        setContentView(R.layout.activity_users);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        textViewToolbarCheckedUsers = (TextView) findViewById(R.id.textView_checked_users);

        /*
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getApplicationContext(), UserGroupDetailsActivity.class);
                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList("data", checkedUsers);

                if (groupId != null) {

                    intent.putExtra("group_id", groupId);
                }

                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        */
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }


    /**
     * Sets the list of checked users to the  toolbar textview
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2017-01-29
     */
    private void setCheckedUsersList() {

        String text = "";

        for (int i = 0; i < checkedUsers.size(); i++) {

            text += checkedUsers.get(i).getName();

            if (i < checkedUsers.size() - 1) {
                text += (", ");
            }
        }

        textViewToolbarCheckedUsers.setText(text);
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onUserListFragmentInteraction(RecyclerViewAdapterUsers.ViewHolder item) {

        TextDrawable drawable;
        ColorGenerator generator = ColorGenerator.MATERIAL; // or use DEFAULT
        // generate random color

        if (item.imageView.getTag().equals("checked")) {

            checkedUsers.remove(item.mItem);

            final int color = generator.getColor(item.mItem.getId());

            drawable = TextDrawable.builder()
                    .buildRound(item.mItem.getName().charAt(0) + "", color); // radius in px
            item.imageView.setTag("unchecked");

        } else {

            checkedUsers.add(item.mItem);

            drawable = TextDrawable.builder()
                    .buildRound(String.valueOf("✓"), Color.GRAY); // radius in px
            item.imageView.setTag("checked");
        }

        item.imageView.setImageDrawable(drawable);

        setCheckedUsersList();
    }


    public Long getGroupId() {
        return groupId;
    }
}
