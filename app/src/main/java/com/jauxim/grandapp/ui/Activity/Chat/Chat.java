package com.jauxim.grandapp.ui.Activity.Chat;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jauxim.grandapp.Constants;
import com.jauxim.grandapp.R;
import com.jauxim.grandapp.Utils.DataUtils;
import com.jauxim.grandapp.models.UserModel;
import com.jauxim.grandapp.networking.Service;
import com.jauxim.grandapp.ui.Activity.BaseActivity;
import com.scaledrone.lib.Listener;
import com.scaledrone.lib.Member;
import com.scaledrone.lib.Room;
import com.scaledrone.lib.RoomListener;
import com.scaledrone.lib.Scaledrone;

import java.util.Random;

import javax.inject.Inject;

import butterknife.ButterKnife;

public class Chat extends BaseActivity implements RoomListener {

    @Inject
    public Service service;

    private ChatPresenter presenter;

    // replace this with a real channelID from Scaledrone dashboard
    private String channelID = "p0gOL0KDsTT7G0Pd";
    private String roomName;

    private Scaledrone scaledrone;
    private MessageAdapter messageAdapter;

    private EditText editText;
    private ListView messagesView;

    private UserModel user;
    private String activityId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat);

        getDeps().inject(this);
        ButterKnife.bind(this);

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                activityId = null;
            } else {
                activityId = extras.getString(Constants.ACTIVITY_ID);
            }
        } else {
            activityId = (String) savedInstanceState.getSerializable(Constants.ACTIVITY_ID);
        }

        Log.d("Log", " Room Id = " + activityId);

        roomName = "observable-" + activityId;

        editText = (EditText) findViewById(R.id.editText);

        messageAdapter = new MessageAdapter(this);
        messagesView = (ListView) findViewById(R.id.messages_view);
        messagesView.setAdapter(messageAdapter);

        user = DataUtils.getUserInfo(this);

        presenter = new ChatPresenter(service, user, messagesView, messageAdapter);

        Log.d("Log", " User Id = " + user.getId());
        Log.d("Log", " User Name = " + user.getCompleteName());

        MemberData data = new MemberData(user.getCompleteName(), getRandomColor());

        Log.d("Log", " Data Name = " + data.toString());

        scaledrone = new Scaledrone(channelID, data);
        scaledrone.connect(new Listener() {
            @Override
            public void onOpen() {
                Log.d("Log","Scaledrone connection open");
                scaledrone.subscribe(roomName, Chat.this);
            }

            @Override
            public void onOpenFailure(Exception ex) {
                System.err.println(ex);
            }

            @Override
            public void onFailure(Exception ex) {
                System.err.println(ex);
            }

            @Override
            public void onClosed(String reason) {
                System.err.println(reason);
            }
        });

        hideProgress();
        getMessageHistorial(roomName);
        showProgress();
    }

    public void sendMessage(View view) {
        String message = editText.getText().toString();
        if (message.length() > 0) {

            message = user.getId() + ";" + message;

            Log.d("Log", " Room message Name = " + roomName);
            Log.d("Log", " Message Name = " + message);

            String auth = DataUtils.getAuthToken( this);

            presenter.incrementActMessage(activityId, auth);
            scaledrone.publish(roomName, message);
            editText.getText().clear();
        }
    }

    @Override
    public void onOpen(Room room) {
        Log.d("Log", " Conneted to room");
        System.out.println("Conneted to room");
    }

    @Override
    public void onOpenFailure(Room room, Exception ex) {
        Log.d("Log", " Conneted to room FAILURE  " + ex.toString());
        System.err.println(ex);
    }

    public void getMessageHistorial(String activityId) {
        String auth = DataUtils.getAuthToken( this);

        Log.d("Log", " Historial 1");

        presenter.getHistorial(activityId, auth);

        Log.d("Log", " Historial 2");
    }

    @Override
    public void onMessage(Room room, final JsonNode json, final Member member) {
        final ObjectMapper mapper = new ObjectMapper();
        try {
            Log.d("Log", " Member Data " + member.getClientData());
            Log.d("Log", " Message " + json.asText());

            final MemberData data = mapper.treeToValue(member.getClientData(), MemberData.class);
            Log.d("Log", " Member Data " + data.toString());

            String messUserId[] = json.asText().split(";");
            Log.d("Log", " User Id from message = " + messUserId[0]);

            boolean belongsToCurrentUser = user.getId().equals(messUserId[0]);
            final Message message = new Message(messUserId[1], data, belongsToCurrentUser);

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    messageAdapter.add(message);
                    messagesView.setSelection(messagesView.getCount() - 1);
                }
            });
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    private String getRandomColor() {
        Random r = new Random();
        StringBuffer sb = new StringBuffer("#");
        while(sb.length() < 7){
            sb.append(Integer.toHexString(r.nextInt()));
        }
        return sb.toString().substring(0, 7);
    }
}