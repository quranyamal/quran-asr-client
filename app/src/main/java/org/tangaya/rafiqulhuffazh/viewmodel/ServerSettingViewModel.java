package org.tangaya.rafiqulhuffazh.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.content.SharedPreferences;
import android.databinding.ObservableField;
import android.support.annotation.NonNull;
import android.util.Log;

import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketFactory;
import com.neovisionaries.ws.client.WebSocketFrame;

import org.tangaya.rafiqulhuffazh.MyApplication;
import org.tangaya.rafiqulhuffazh.view.navigator.ServerSettingNavigator;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class ServerSettingViewModel extends AndroidViewModel {

    public static String CONNECTION_STATUS_CONNECTED = "connected";
    public static String CONNECTION_STATUS_DISCONNECTED = "disconnected";
    public static String CONNECTION_STATUS_ERROR = "error";
    public static String CONNECTION_STATUS_CONNECTING = "connecting...";

    ServerSettingNavigator mNavigator;
    SharedPreferences sharedPref;

    public final ObservableField<String> hostname = new ObservableField<>();
    public final ObservableField<String> port = new ObservableField<>();
    public final ObservableField<String> connectionStatus = new ObservableField<>();

    public ServerSettingViewModel(@NonNull Application application) {
        super(application);

        sharedPref = ((MyApplication) application).getPreferences();
        hostname.set(sharedPref.getString("SERVER_HOSTNAME", "192.168.1.100"));
        port.set(sharedPref.getString("SERVER_PORT", "8888"));
    }

    public void onActivityCreated(ServerSettingNavigator navigator) {
        mNavigator = navigator;
    }

    public void testConnection() {
        String endpoint = "ws://"+hostname.get()+":"+port.get()+"/client/ws/status";

        try {
            WebSocket ws = new WebSocketFactory().createSocket(endpoint);
            ws.addListener(new WebSocketAdapter() {
                @Override
                public void onConnected(WebSocket websocket, Map<String, List<String>> headers) throws Exception {
                    super.onConnected(websocket, headers);

                    connectionStatus.set(CONNECTION_STATUS_CONNECTED);
                }

                @Override
                public void onDisconnected(WebSocket websocket, WebSocketFrame serverCloseFrame, WebSocketFrame clientCloseFrame, boolean closedByServer) throws Exception {
                    super.onDisconnected(websocket, serverCloseFrame, clientCloseFrame, closedByServer);

                    connectionStatus.set(CONNECTION_STATUS_DISCONNECTED);
                }

                @Override
                public void onConnectError(WebSocket websocket, WebSocketException exception) throws Exception {
                    super.onConnectError(websocket, exception);

                    connectionStatus.set(CONNECTION_STATUS_ERROR);
                }

                @Override
                public void onTextMessage(WebSocket websocket, String text) throws Exception {
                    super.onTextMessage(websocket, text);

                    Log.d("DVM", "onTextMessage. message: " + text);
                }
            });

            ws.connectAsynchronously();
            connectionStatus.set("connecting...");
            Log.d("SSVM", "connecting to " + endpoint);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveSetting() {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("SERVER_HOSTNAME", hostname.get());
        editor.putString("SERVER_PORT", port.get());
        editor.commit();

        mNavigator.onSettingSaved();
    }

    public void cancelSetting() {
        mNavigator.onSettingCancelled();
    }
}
