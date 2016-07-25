package net.forlogic.samplelogingoogle;


import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;

public final class GoogleApiUtils {

//    private static final String SERVER_CLIENT_ID = "239962782101-ugr43l2vvc5mqo4b8nkpm2ngp8rl3h5t.apps.googleusercontent.com";
    private static final String SERVER_CLIENT_ID = "239962782101-bbm8c8da65l0c0jjrvh4sea9hb2asvpt.apps.googleusercontent.com";

    private static final GoogleSignInOptions GSO = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestProfile()
            .requestIdToken(SERVER_CLIENT_ID)
            .requestScopes(Plus.SCOPE_PLUS_LOGIN)
            .requestScopes(Plus.SCOPE_PLUS_PROFILE)
            .build();

    private GoogleApiUtils() {
    }

    public static GoogleSignInOptions getGoogleSignInOptions() {
        return GSO;
    }

    public static GoogleApiClient connectToGoogleApi(@NonNull FragmentActivity activity,
                                                     @NonNull GoogleSignInOptions gso,
                                                     @NonNull GoogleApiClient.ConnectionCallbacks connectionCallbacks,
                                                     @NonNull GoogleApiClient.OnConnectionFailedListener connectionFailedListener) {
        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(activity, connectionCallbacks, connectionFailedListener)
                .enableAutoManage(activity, connectionFailedListener)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .addApi(Plus.API)
                .build();
        googleApiClient.connect();

        return googleApiClient;
    }

    public static GoogleApiClient connectToGoogleApi(@NonNull FragmentActivity activity,
                                                     @NonNull GoogleApiClient.ConnectionCallbacks connectionCallbacks,
                                                     @NonNull GoogleApiClient.OnConnectionFailedListener connectionFailedListener) {
        return connectToGoogleApi(activity, GSO, connectionCallbacks,connectionFailedListener);
    }

    public static String getProfilePhotoUrl(String photoUrlPath, int imageSize) {
        return photoUrlPath.replace("sz=50", "sz=" + imageSize);
    }

    public static String getStringGender(Person person) {
        String gender = "";
        switch (person.getGender()) {
            case Person.Gender.MALE:
                gender = "Masculino";
                break;
            case Person.Gender.FEMALE:
                gender = "Feminino";
                break;
            case Person.Gender.OTHER:
                gender = "Outro";
                break;
        }
        return gender;
    }

    public static String getStringRelationshipStatus(Person person) {
        String relationshipStatus = "";
        switch (person.getRelationshipStatus()) {
            case Person.RelationshipStatus.ENGAGED:
                relationshipStatus = person.getGender() == Person.Gender.FEMALE ? "Noiva" : "Noivo";
                break;
            case Person.RelationshipStatus.IN_A_RELATIONSHIP:
                relationshipStatus = "Namorando";
                break;
            case Person.RelationshipStatus.IN_CIVIL_UNION:
                relationshipStatus = "Em uma união civil";
                break;
            case Person.RelationshipStatus.IN_DOMESTIC_PARTNERSHIP:
                relationshipStatus = "Morando junto";
                break;
            case Person.RelationshipStatus.ITS_COMPLICATED:
                relationshipStatus = person.getGender() == Person.Gender.FEMALE ? "Enrolada" : "Enrolado";
                break;
            case Person.RelationshipStatus.MARRIED:
                relationshipStatus = person.getGender() == Person.Gender.FEMALE ? "Casada" : "Casado";
                break;
            case Person.RelationshipStatus.OPEN_RELATIONSHIP:
                relationshipStatus = "Em um relacionamento aberto";
                break;
            case Person.RelationshipStatus.SINGLE:
                relationshipStatus = person.getGender() == Person.Gender.FEMALE ? "Solteira" : "Solteiro";
                break;
            case Person.RelationshipStatus.WIDOWED:
                relationshipStatus = person.getGender() == Person.Gender.FEMALE ? "Viúva" : "Viúvo";
                break;
        }
        return relationshipStatus;
    }
}
