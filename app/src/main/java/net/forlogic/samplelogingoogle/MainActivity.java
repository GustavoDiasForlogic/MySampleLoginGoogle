package net.forlogic.samplelogingoogle;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;

@SuppressWarnings("StringBufferReplaceableByString")
public class MainActivity extends FragmentActivity implements GoogleApiClient.OnConnectionFailedListener, View.OnClickListener, GoogleApiClient.ConnectionCallbacks {

    private static final String LOG_TAG_SIGN_IN = "Google Sign In";

    private static final int REQUEST_SIGN_IN = 1;

    public static final int PROFILE_IMAGE_SIZE = 300;

    private GoogleApiClient mGoogleApiClient;

    private SignInButton mSignInButton;
    private TextView mTxtSignInResult;
    private ImageView mImgProfilePhoto;
    private Button mBtnLogout, mBtnListFriends;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSignInButton = (SignInButton) findViewById(R.id.sign_in_button);
        mTxtSignInResult = (TextView) findViewById(R.id.txv_sign_in_result);
        mImgProfilePhoto = (ImageView) findViewById(R.id.img_profile_photo);
        mBtnLogout = (Button) findViewById(R.id.btn_logout);
        mBtnListFriends = (Button) findViewById(R.id.btn_list_friends);

        GoogleSignInOptions gso = GoogleApiUtils.getGoogleSignInOptions();
        mGoogleApiClient = GoogleApiUtils.connectToGoogleApi(this, gso, this, this);

        mSignInButton.setSize(SignInButton.SIZE_STANDARD);
        mSignInButton.setScopes(gso.getScopeArray());
        mSignInButton.setOnClickListener(this);
        mBtnLogout.setOnClickListener(this);
        mBtnListFriends.setOnClickListener(this);
    }

    @Override
    public void onConnected(Bundle bundle) {
        // Se o usuário já fez login alguma vez, ele já autorizou, então já faz o login automático
        OptionalPendingResult<GoogleSignInResult> pendingResult = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
        if (pendingResult.isDone()) {
            // Já tem resultado imediato disponível
            GoogleSignInResult result = pendingResult.get();
            if (result.isSuccess())
                handleSignInResult(result);
        } else {
            pendingResult.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(GoogleSignInResult result) {
                    if (result.isSuccess())
                        handleSignInResult(result);
                }
            });
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        StringBuilder errorMessage = new StringBuilder();
        errorMessage.append("Failed to connect to the Google API\n");
        errorMessage.append("Error ").append(connectionResult.getErrorCode()).append(": ");
        errorMessage.append(connectionResult.getErrorMessage());
        Log.e(LOG_TAG_SIGN_IN, errorMessage.toString());

        Toast.makeText(MainActivity.this, "Falha ao se conectar à API do Google!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v) {
        if (v == mSignInButton) {
            singIn();
        } else if (v == mBtnLogout) {
            PendingResult<Status> result = Auth.GoogleSignInApi.signOut(mGoogleApiClient);
            result.setResultCallback(new ResultCallback<Status>() {
                @Override
                public void onResult(Status status) {
                    String msg = status.isSuccess() ? "Logout feito!" : "Falha ao fazer logout!";
                    Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                }
            });
        } else if (v == mBtnListFriends) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.GET_ACCOUNTS) == PackageManager.PERMISSION_GRANTED)
                startActivity(new Intent(this, CircleFriendsActivity.class));
            else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.GET_ACCOUNTS}, 0);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
            startActivity(new Intent(this, CircleFriendsActivity.class));
    }

    private void singIn() {
        Intent i = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(i, REQUEST_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            GoogleSignInAccount account = result.getSignInAccount();
            displayAccount(account);
        } else {
            Log.e(LOG_TAG_SIGN_IN, "Could not sign in to Google");
            Toast.makeText(MainActivity.this, "Falha ao fazer login no google!", Toast.LENGTH_SHORT).show();
        }
    }

    private void displayAccount(final GoogleSignInAccount account) {
        final Person person = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);

        StringBuilder displayAcc = new StringBuilder();
        displayAcc.append("Display name: ").append(account.getDisplayName()).append("\n");
        displayAcc.append("E-mail: ").append(account.getEmail()).append("\n");
        if (person != null) {
            displayAcc.append("Data de nascimento: ").append(person.getBirthday()).append("\n");
            displayAcc.append("Localização atual: ").append(person.getCurrentLocation()).append("\n");
            displayAcc.append("Sexo: ").append(GoogleApiUtils.getStringGender(person)).append("\n");
            displayAcc.append("Língua: ").append(person.getLanguage()).append("\n");
            displayAcc.append("Apelido: ").append(person.getNickname()).append("\n");
            displayAcc.append("Relacionamento: ").append(GoogleApiUtils.getStringRelationshipStatus(person)).append("\n");
        }
        mTxtSignInResult.setText(displayAcc.toString());

        new Thread(new Runnable() {
            @Override
            public void run() {
//                String photoUrlPath = account.getPhotoUrl().toString();
                String photoUrlPath = person != null ? person.getImage().getUrl() : account.getPhotoUrl().toString();
                photoUrlPath = GoogleApiUtils.getProfilePhotoUrl(photoUrlPath, PROFILE_IMAGE_SIZE);

                final Bitmap profileImage = Utils.getBitmapFromURL(photoUrlPath);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mImgProfilePhoto.setImageBitmap(profileImage);
                    }
                });
            }
        }).start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mGoogleApiClient.disconnect();
    }
}
