package net.forlogic.samplelogingoogle;

import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.plus.People;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.google.android.gms.plus.model.people.PersonBuffer;

import java.util.Iterator;

public class CircleFriendDataActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, ResultCallback<People.LoadPeopleResult> {

    public static final String EXTRA_PERSON_ID = "PERSON_ID";

    private GoogleApiClient mGoogleApiClient;
    private PersonBuffer mPersonBuffer;
    private Person mPerson;

    private ImageView mImgPhoto;
    private TextView mTxvInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_circle_friend_data);
        if (!getIntent().hasExtra(EXTRA_PERSON_ID)) {
            finish();
            return;
        }

        mImgPhoto = (ImageView) findViewById(R.id.img_friend_data_photo);
        mTxvInfo = (TextView) findViewById(R.id.txv_friend_data_info);

        mGoogleApiClient = GoogleApiUtils.connectToGoogleApi(this, this, this);
    }

    @Override
    public void onConnected(Bundle bundle) {
        String personId = getIntent().getStringExtra(EXTRA_PERSON_ID);
        Plus.PeopleApi.load(mGoogleApiClient, personId).setResultCallback(this);
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Toast.makeText(CircleFriendDataActivity.this, "Falha ao se conectar com o Google!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResult(People.LoadPeopleResult loadPeopleResult) {
        if (loadPeopleResult.getStatus().isSuccess()) {
            mPersonBuffer = loadPeopleResult.getPersonBuffer();

            Iterator<Person> it = mPersonBuffer.iterator();
            if (it.hasNext()) {
                mPerson = it.next();
            } else {
                Toast.makeText(CircleFriendDataActivity.this, "Perfil não encontrado!", Toast.LENGTH_SHORT).show();
            }

            showProfile();
        } else {
            Toast.makeText(CircleFriendDataActivity.this, "Falha ao carregar perfil!", Toast.LENGTH_SHORT).show();
        }
    }

    private void showProfile() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String photoUrlPath = mPerson.getImage().getUrl();
                photoUrlPath = GoogleApiUtils.getProfilePhotoUrl(photoUrlPath, MainActivity.PROFILE_IMAGE_SIZE);

                final Bitmap photo = Utils.getBitmapFromURL(photoUrlPath);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mImgPhoto.setImageBitmap(photo);
                    }
                });
            }
        }).start();

        StringBuilder profileInfo = new StringBuilder();
        Person.Name name = mPerson.getName();
        profileInfo.append("Nome: ")
                .append(name.getGivenName() == null ? "" : name.getGivenName())
                .append(" ")
                .append(name.getMiddleName() == null ? "" : name.getMiddleName())
                .append(" ")
                .append(name.getFamilyName() == null ? "" : name.getFamilyName())
                .append("\n");
//        displayAcc.append("E-mail: ").append(mPerson.getEmail()).append("\n");
        profileInfo.append("Data de nascimento: ").append(mPerson.getBirthday()).append("\n");
        profileInfo.append("Localização atual: ").append(mPerson.getCurrentLocation()).append("\n");
        profileInfo.append("Sexo: ").append(GoogleApiUtils.getStringGender(mPerson)).append("\n");
        profileInfo.append("Língua: ").append(mPerson.getLanguage()).append("\n");
        profileInfo.append("Apelido: ").append(mPerson.getNickname()).append("\n");
        profileInfo.append("Relacionamento: ").append(GoogleApiUtils.getStringRelationshipStatus(mPerson)).append("\n");

        mTxvInfo.setText(profileInfo.toString());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPersonBuffer.release();
        mGoogleApiClient.disconnect();
    }
}
