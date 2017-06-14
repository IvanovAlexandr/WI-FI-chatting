package com.example.ivanovalexander.finalproject;

/**
 * Copyright 2014 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.connection.AppIdentifier;
import com.google.android.gms.nearby.connection.AppMetadata;
import com.google.android.gms.nearby.connection.Connections;
import com.google.android.gms.nearby.connection.ConnectionsStatusCodes;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class MainActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        Connections.ConnectionRequestListener,
        Connections.MessageListener,
        Connections.EndpointDiscoveryListener,
        FragmentMain.OnSelectedButtonListener,
        FragmentChat.OnSelectedButtonListener,
        View.OnClickListener {


    private static final String APP_PREFERENCES = "settings";
    private static final String APP_PREFERENCES_NAME = "nickname";
    private static final String APP_PREFERENCES_CHECK = "check";

    final static String TAG_1 = "FRAGMENT_1";
    final static String TAG_2 = "FRAGMENT_2";

    private static final long TIMEOUT_ADVERTISE = 1000L * 30L;
    private static final long TIMEOUT_DISCOVER = 1000L * 30L;
    /**
     * Possible states for this application:
     * IDLE - GoogleApiClient not yet connected, can't do anything.
     * READY - GoogleApiClient connected, ready to use Nearby Connections API.
     * ADVERTISING - advertising for peers to connect.
     * DISCOVERING - looking for a peer that is advertising.
     * CONNECTED - found a peer.
     */

    private static final int STATE_IDLE = 1023;
    private static final int STATE_READY = 1024;
    private static final int STATE_ADVERTISING = 1025;
    private static final int STATE_DISCOVERING = 1026;
    private static final int STATE_CONNECTED = 1027;



    private android.app.FragmentManager myFragmentManager;
    private FragmentMain fragmentMain;
    private FragmentChat fragmentChat;

    public Toolbar mToolbar;

    Random random = new Random();

    private GoogleApiClient mGoogleApiClient;
    /**
     * The current state of the application
     **/
    private int mState = STATE_IDLE;
    /**
     * The endpoint ID of the connected peer, used for messaging
     **/
    private ArrayList<String> mOtherEndpointId;
    private MyListDialog mMyListDialog;

    private String myName = "anonymous";
    private int iconId;
    private boolean isHost = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationIcon(R.drawable.ic_launcher);
        mToolbar.setNavigationOnClickListener(this);

        myFragmentManager = getFragmentManager();
        fragmentMain = new FragmentMain();
        fragmentChat = new FragmentChat();

        if (savedInstanceState == null) {
            android.app.FragmentTransaction fragmentTransaction = myFragmentManager
                    .beginTransaction();
            fragmentTransaction.add(R.id.container, fragmentMain, TAG_1);
            fragmentTransaction.commit();
        }

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Nearby.CONNECTIONS_API)
                .build();

        iconId = random.nextInt(26) + 1;

        mOtherEndpointId = new ArrayList();
    }


    private void loadSettings() {
        SharedPreferences mySharedPreferences = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);

        if (mySharedPreferences.contains(APP_PREFERENCES_CHECK)) {
            if (mySharedPreferences.getBoolean(APP_PREFERENCES_CHECK, false)) {
                if (mySharedPreferences.contains(APP_PREFERENCES_NAME)) {
                    myName = mySharedPreferences.getString(APP_PREFERENCES_NAME, getString(R.string.anonymous));
                }
            }
        }
    }


    private void addBackButton() {
        mToolbar.setNavigationIcon(R.drawable.back2);
    }

    @Override
    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }
    }

    /**
     * Change the application state and update the visibility on on-screen views '
     * based on the new state of the application.
     *
     * @param newState the state to move to (should be NearbyConnectionState)
     */
    private void updateViewVisibility(int newState) {
        mState = newState;
    }

    private boolean isConnectedToNetwork() {
        ConnectivityManager connManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo info = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return (info != null && info.isConnectedOrConnecting());

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        updateViewVisibility(STATE_READY);
    }

    @Override
    public void onConnectionSuspended(int i) {
        updateViewVisibility(STATE_IDLE);

        //try to reconnect
        mGoogleApiClient.reconnect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        updateViewVisibility(STATE_IDLE);
    }

    @Override
    public void onConnectionRequest(final String endpointId, String deviceId, String endpointName, byte[] bytes) {
        //This device is advertising and has received a connection request. Show a dialog asking
        //the user if they would like to connect and accept or reject the request accordingly.
        AlertDialog mConnectionRequestDialog = new AlertDialog.Builder(this)
                .setTitle(R.string.connection_request)
                .setMessage(getString(R.string.want_connect_to) + endpointName + "?")
                .setCancelable(false)
                .setPositiveButton(R.string.connect, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        byte[] bytes = null;
                        Nearby.Connections.acceptConnectionRequest(mGoogleApiClient, endpointId, bytes, MainActivity.this)
                                .setResultCallback(new ResultCallback<Status>() {
                                    @Override
                                    public void onResult(Status status) {
                                        if (status.isSuccess()) {
                                            mOtherEndpointId.add(endpointId);
                                            updateViewVisibility(STATE_CONNECTED);
                                        }
                                    }
                                });
                    }
                })
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Nearby.Connections.rejectConnectionRequest(mGoogleApiClient, endpointId);
                    }
                }).create();

        mConnectionRequestDialog.show();
    }

    @Override
    public void onEndpointFound(String endpointId, String deviceId, String serviceId, String endpointName) {
        //This device is discovering endpoints and has located an advertiser. Display a dialog to
        //the user asking if they want to connect, and send a connection request if they do.

        if (mMyListDialog == null) {
            //Configure the AlertDialog that the MyListDialog wraps
            AlertDialog.Builder builder = new AlertDialog.Builder(this)
                    .setTitle(R.string.endpoints_found)
                    .setCancelable(true)
                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mMyListDialog.dismiss();
                        }
                    });

            //Create the MyListDialog with a listener
            mMyListDialog = new MyListDialog(this, builder, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String selectedEndpointName = mMyListDialog.getItemKey(which);
                    String selectedEndpointId = mMyListDialog.getItemValue(which);

                    MainActivity.this.connectTo(selectedEndpointId, selectedEndpointName);
                    mMyListDialog.dismiss();
                }
            });
        }

        mMyListDialog.addItem(endpointName, endpointId);
        mMyListDialog.show();
    }

    @Override
    public void onEndpointLost(String endpointId) {
        // An endpoint that was previously available for connection is no longer. It may have
        // stopped advertising, gone out of range, or lost connectivity. Dismiss any dialog that
        // was offering a connection.

        if (mMyListDialog != null) {
            mMyListDialog.removeItemByValue(endpointId);
        }
    }

    @Override
    public void onMessageReceived(String endpointId, byte[] bytes, boolean isReliable) {
        // A message has been received from a remote endpoint.
        if (fragmentChat != null)
            fragmentChat.displayMessage(bytes);

        if (isHost) {
            sendMessage(false, new String(bytes));
        }

    }

    @Override
    public void onDisconnected(String endpointId) {
        updateViewVisibility(STATE_READY);
    }

    @Override
    public void onButtonSelected(int buttonIndex) {
        switch (buttonIndex) {
            case 1:
                if (mState == STATE_IDLE) {
                    Toast.makeText(MainActivity.this, R.string.try_again, Toast.LENGTH_SHORT).show();
                    return;
                }
                buttonAdvertising();
                break;
            case 2:
                if (mState == STATE_IDLE) {
                    Toast.makeText(MainActivity.this, R.string.try_again, Toast.LENGTH_SHORT).show();
                    return;
                }
                buttonDiscovery();
                break;
            case 3:
                startChat();
                break;
            case 4:
                startSettings();
                break;
        }


    }

    private void startSettings() {
        Intent intent = new Intent(MainActivity.this, SettingActivity.class);
        startActivity(intent);
    }

    public void buttonAdvertising() {
        Toast.makeText(MainActivity.this, R.string.main_button_advertising, Toast.LENGTH_SHORT).show();
        isHost = true;
        startAdvertising();
    }

    public void buttonDiscovery() {
        Toast.makeText(MainActivity.this, R.string.main_button_discovering, Toast.LENGTH_SHORT).show();
        isHost = false;
        startDiscovery();
    }

    private void startChat() {
        loadSettings();
        android.app.FragmentTransaction fragmentTransaction = myFragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container, fragmentChat, TAG_2);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
        addBackButton();
    }

    private void startAdvertising() {
        if (!isConnectedToNetwork()) {
            return;
        }
        // Advertising with an AppIdentifer lets other devices on the network discover
        // this application and prompt the user to install the application.
        List appIdentifierList = new ArrayList<>();
        appIdentifierList.add(new AppIdentifier(getPackageName()));
        AppMetadata appMetadata = new AppMetadata(appIdentifierList);

        // Advertise for Nearby Connections. This will broadcast the service id defined in
        // AndroidManifest.xml. By passing 'null' for the name, the Nearby Connections API
        // will construct a default name based on device model such as 'LGE Nexus 5'.

        String name = null;
        Nearby.Connections.startAdvertising(mGoogleApiClient, name, appMetadata, TIMEOUT_ADVERTISE, this)
                .setResultCallback(new ResultCallback<Connections.StartAdvertisingResult>() {
                    @Override
                    public void onResult(Connections.StartAdvertisingResult startAdvertisingResult) {
                        if (startAdvertisingResult.getStatus().isSuccess()) {
                            updateViewVisibility(STATE_ADVERTISING);
                        } else {
                            // If the user hits 'Advertise' multiple times in the timeout window,
                            // the error will be STATUS_ALREADY_ADVERTISING
                            int statusCode = startAdvertisingResult.getStatus().getStatusCode();
                            if (statusCode == ConnectionsStatusCodes.STATUS_ALREADY_ADVERTISING) {
                                Toast.makeText(MainActivity.this, R.string.already_advertising, Toast.LENGTH_SHORT).show();
                            } else {
                                updateViewVisibility(STATE_READY);
                            }
                        }
                    }
                });


    }

    private void startDiscovery() {
        if (!isConnectedToNetwork()) {
            return;
        }
        // Discover nearby apps that are advertising with the required service ID.
        String serviceId = getString(R.string.service_id);
        Nearby.Connections.startDiscovery(mGoogleApiClient, serviceId, TIMEOUT_DISCOVER, this)
                .setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        if (status.isSuccess()) {
                            updateViewVisibility(STATE_DISCOVERING);
                        } else {
                            // If the user hits 'Discover' multiple times in the timeout window,
                            // the error will be STATUS_ALREADY_DISCOVERING
                            int statusCode = status.getStatusCode();
                            if (statusCode == ConnectionsStatusCodes.STATUS_ALREADY_DISCOVERING) {
                                Toast.makeText(MainActivity.this, R.string.already_discovering, Toast.LENGTH_SHORT).show();
                            } else {
                                updateViewVisibility(STATE_READY);
                            }
                        }
                    }
                });
    }

    private void connectTo(String endpointId, final String endpointName) {
        //Send a connection request to a remote endpoint. By passing 'null for the name,
        // the Nearby Connections API will construct a default name based on device model
        // such as 'LGE Nexus 5'.

        String myName = null;
        byte[] myPayload = null;
        Nearby.Connections.sendConnectionRequest(mGoogleApiClient, myName, endpointId, myPayload,
                new Connections.ConnectionResponseCallback() {
                    @Override
                    public void onConnectionResponse(String endpointId, Status status, byte[] bytes) {
                        if (status.isSuccess()) {
                            Toast.makeText(MainActivity.this, getString(R.string.connected_to) + endpointName, Toast.LENGTH_SHORT).show();

                            mOtherEndpointId.add(endpointId);
                            updateViewVisibility(STATE_CONNECTED);
                        }
                    }
                }, this);
    }

    private void sendMessage(boolean flag, String msg) {
        //Sends a reliable message, which is guaranteed to be delivered eventually and to respect
        // message ordering from sender to receiver. Nearby.Connections.sendUnreliablemessage
        // should be used for high-frequency messages where guaranteed delivery is not required? such
        // as showing one player's cursor location to another. Unrealiable messages are often
        // delivered faster than reliable messages.
        if (flag) {
            msg += " ";
            msg += myName;
            msg += " ";
            msg += iconId;
            if (isHost) {
                if (fragmentChat != null)
                    fragmentChat.displayMessage(msg.getBytes());
            }
        }
        for (int i = 0; i < mOtherEndpointId.size(); i++) {
            Nearby.Connections.sendReliableMessage(mGoogleApiClient, mOtherEndpointId.get(i), msg.getBytes());
        }
    }

    @Override
    public void onButtonSend() {
        String msg = fragmentChat.sendMessage();
        sendMessage(true, msg);
    }

    @Override
    public void onClick(View v) {
        backMenu();
    }

    private void backMenu() {
        android.app.FragmentTransaction fragmentTransaction = myFragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container, fragmentMain, TAG_1);
        fragmentTransaction.commit();
        mToolbar.setNavigationIcon(R.drawable.ic_launcher);
    }

    @Override
    public void onBackPressed() {
        backToMenu();
    }

    private void backToMenu() {
        myFragmentManager.popBackStack();
        mToolbar.setNavigationIcon(R.drawable.ic_launcher);
    }
}
