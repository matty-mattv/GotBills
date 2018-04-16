package com.example.gotbill;

import android.content.Intent;
import android.content.IntentSender;
import android.hardware.SensorAdditionalInfo;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.provider.DocumentsContract;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.drive.CreateFileActivityOptions;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveClient;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveResourceClient;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Date;

public class DisplayResultActivity extends AppCompatActivity {

    private String date;
    private double[] personBill;
    private String[] comments;

    private static final String TAG = "drive-quickstart";
    private static final int REQUEST_CODE_SIGN_IN = 0;
    private static final int REQUEST_CODE_SAVE_DRIVE = 1;




    private GoogleSignInClient mGoogleSignInClient;
    private DriveClient mDriveClient;
    private DriveResourceClient mDriveResourceClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_result);
        Bundle bundle = getIntent().getExtras();

        date = bundle.getString(MainActivity.EXTRA_DATE);
        personBill = bundle.getDoubleArray(AddInformation.EXTRA_BILL);
        comments = bundle.getStringArray(AddInformation.EXTRA_COMMENTS);

        ( (EditText) findViewById(R.id.matthewResult)).setText( Double.toString( personBill[0] ) );
        ( (EditText) findViewById(R.id.christineResult)).setText( Double.toString( personBill[1] ) );
        ( (EditText) findViewById(R.id.thuResult)).setText( Double.toString( personBill[2] ) );
        ( (EditText) findViewById(R.id.janiceResult)).setText( Double.toString( personBill[3] ) );
        ( (EditText) findViewById(R.id.baraniResult)).setText( Double.toString( personBill[4] ) );

        double totalBill = 0.0;

        for(int i = 0; i < AddInformation.numUsers; ++i ) {
            totalBill += personBill[i];
        }

        ( (EditText) findViewById(R.id.totalBill) ).setText( Double.toString(totalBill) );

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void saveDataToCsvFile(View view) throws IOException {
        File file = new File(this.getFilesDir(), "spreadsheet.csv");
        if(!file.exists()) {
            file.createNewFile();
            String header = "bill_period,name,monthly_charge,comments";
            FileWriter fwriter = new FileWriter(file, true);
            BufferedWriter bwriter = new BufferedWriter(fwriter);
            bwriter.write(header);
            bwriter.newLine();
        }

        FileWriter fwriter = new FileWriter(file, true);
        BufferedWriter bwriter = new BufferedWriter(fwriter);

        bwriter.newLine();
        bwriter.newLine();

        String[] dataInString = getResultsInStringCsvFormat();

        for(int i = 0; i < AddInformation.numUsers; ++i) {
            bwriter.write(dataInString[i]);
            bwriter.newLine();
        }

        fwriter.close();
        //bwriter.close();

        signIn();
    }

    public void shareToGoogleDrive(View view) {
        File file = new File(this.getFilesDir(), "spreadsheet.csv");
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private String[] getResultsInStringCsvFormat() {

        int numUsers = AddInformation.numUsers;
        String[] returnData = new String[numUsers];

        //Append year to date
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy");
        Date year = new Date();
        date += "_" + formatter.format(year).toString();

        String[] name = new String[numUsers];
        name[0] = "Matthew Vu";
        name[1] = "Christine Vu";
        name[2] = "Thu Mai";
        name[3] = "Janice Vu";
        name[4] = "Barani Htut";

        for(int i = 0; i < numUsers; ++i) {
            returnData[i] = date + "," + name[i] + "," + personBill[i] + "," + comments[i];
        }
        return returnData;
    }

    public void signIn() {
        Log.i(TAG, "Start sign in");
        mGoogleSignInClient = buildGoogleSignInClient();
        startActivityForResult(mGoogleSignInClient.getSignInIntent(), REQUEST_CODE_SIGN_IN);
    }

    /** Build a Google SignIn client. */
    private GoogleSignInClient buildGoogleSignInClient() {
        GoogleSignInOptions signInOptions =
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestScopes(Drive.SCOPE_FILE)
                        .build();
        return GoogleSignIn.getClient(this, signInOptions);
    }


    /** Create a new file and save it to Drive. */
    private void saveFileToDrive() throws IOException {
        // Start by creating a new contents, and setting a callback.
        Log.i(TAG, "Creating new contents.");

        mDriveResourceClient
                .createContents()
                .continueWithTask(
                        new Continuation<DriveContents, Task<Void>>() {
                            @Override
                            public Task<Void> then(@NonNull Task<DriveContents> task) throws Exception {
                                return createFileIntentSender(task.getResult());
                            }
                        })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Failed to create new contents.", e);
                            }
                        });
    }

    /**
     * Creates an {@link IntentSender} to start a dialog activity with configured {@link
     * CreateFileActivityOptions} for user to create a new photo in Drive.
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private Task<Void> createFileIntentSender(DriveContents driveContents) throws IOException {
        Log.i(TAG, "New contents created.");
        // Get an output stream for the contents.

        OutputStream outputStream = driveContents.getOutputStream();
//        File file = new File(this.getFilesDir(), "spreadsheet.csv");
//        Path path = file.toPath();
//        Files.copy(path, outputStream);
//        outputStream.flush();

        //Path path =  this.getFilesDir().toPath();
        //Files.copy(path, outputStream);
        //outputStream.flush();

        //Copy all the file over
        try {
            outputStream.write( ("bill_......period,name,monthly_charge,comments").getBytes() );
        } catch (IOException e) {
            Log.w(TAG, "Unable to write file contents.", e);
        }

        String title = "Phone_bill_" + date + ".csv";

        // Create the initial metadata - MIME type and title.
        // Note that the user will be able to change the title later.
        MetadataChangeSet metadataChangeSet =
                new MetadataChangeSet.Builder()
                        .setMimeType("text/csv")
                        .setTitle(title)
                        .build();

        // Set up options to configure and display the create file activity.
        CreateFileActivityOptions createFileActivityOptions =
                new CreateFileActivityOptions.Builder()
                        .setInitialMetadata(metadataChangeSet)
                        .setInitialDriveContents(driveContents)
                        .build();

        return mDriveClient
                .newCreateFileActivityIntentSender(createFileActivityOptions)
                .continueWith(
                        new Continuation<IntentSender, Void>() {
                            @Override
                            public Void then(@NonNull Task<IntentSender> task) throws Exception {
                                startIntentSenderForResult(task.getResult(), REQUEST_CODE_SAVE_DRIVE, null, 0, 0, 0);
                                return null;
                            }
                        });
    }

    /**
     * Creates an {@link IntentSender} to start a dialog activity with configured {@link
     * CreateFileActivityOptions} for user to create a new photo in Drive.
     */

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i(TAG, "Sign in request code");

        if( requestCode == REQUEST_CODE_SIGN_IN) {
            // Called after user is signed in.
            if (resultCode == RESULT_OK) {
                Log.i(TAG, "Signed in successfully.");
                // Use the last signed in account here since it already have a Drive scope.
                mDriveClient = Drive.getDriveClient(this, GoogleSignIn.getLastSignedInAccount(this));
                // Build a drive resource client.
                mDriveResourceClient =
                        Drive.getDriveResourceClient(this, GoogleSignIn.getLastSignedInAccount(this));
                try {
                    saveFileToDrive();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                Log.i(TAG, "Signed in failled.");
            }
        }
        else if (resultCode ==  REQUEST_CODE_SAVE_DRIVE) {
            Log.i(TAG, "Reached end after save.");
        }
    }

}



