package net.forlogic.samplelogingoogle;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.plus.People;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.google.android.gms.plus.model.people.PersonBuffer;

import java.util.ArrayList;
import java.util.List;

public class CircleFriendsActivity extends AppCompatActivity implements OnRecyclerViewItemClickListener {

    private GoogleApiClient mGoogleApiClient;

    private PersonBuffer mPersonBuffer;
    private List<Person> mPeople = new ArrayList<>();

    private RecyclerView mRecvFriends;

    private CircleFriendsAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_circle_friends);

        mRecvFriends = (RecyclerView) findViewById(R.id.recv_friends);

        mGoogleApiClient = GoogleApiUtils.connectToGoogleApi(this, new GoogleApiClient.ConnectionCallbacks() {
            @Override
            public void onConnected(Bundle bundle) {
                retrivePeople();
            }

            @Override
            public void onConnectionSuspended(int i) {
            }
        }, new GoogleApiClient.OnConnectionFailedListener() {
            @Override
            public void onConnectionFailed(ConnectionResult connectionResult) {
                Toast.makeText(CircleFriendsActivity.this, "Falha ao se conectar à api do Google!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void retrivePeople() {
        PendingResult<People.LoadPeopleResult> result = Plus.PeopleApi.loadVisible(mGoogleApiClient, null);
        result.setResultCallback(new ResultCallback<People.LoadPeopleResult>() {
            @Override
            public void onResult(People.LoadPeopleResult loadPeopleResult) {
                loadPeople(loadPeopleResult);
            }
        });
    }

    private void loadPeople(final People.LoadPeopleResult loadPeopleResult) {
        if (loadPeopleResult.getStatus().isSuccess()) {
            mPersonBuffer = loadPeopleResult.getPersonBuffer();
            for (Person p : mPersonBuffer) {
                mPeople.add(p);
            }
            afterLoadPeople();
        } else {
            Toast.makeText(CircleFriendsActivity.this, "Falha ao carregar a lista de amigos!", Toast.LENGTH_SHORT).show();
        }
    }

    private void afterLoadPeople() {
        mAdapter = new CircleFriendsAdapter(this, mPeople);
        LinearLayoutManager layMan = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecvFriends.setLayoutManager(layMan);
        mAdapter.setOnItemClickListener(this);
        mRecvFriends.setAdapter(mAdapter);
    }

    @Override
    public void onItemClick(View clickedView, int position) {
        Person clickedFriend = mAdapter.getItem(position);
        Intent i = new Intent(this, CircleFriendDataActivity.class);
        i.putExtra(CircleFriendDataActivity.EXTRA_PERSON_ID, clickedFriend.getId());
        startActivity(i);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mAdapter != null)
            mAdapter.stopLoadImages();
        if (mPersonBuffer != null)
            mPersonBuffer.release();
        mGoogleApiClient.disconnect();
    }
}
