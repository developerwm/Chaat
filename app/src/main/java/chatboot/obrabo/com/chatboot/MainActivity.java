package chatboot.obrabo.com.chatboot;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.JsonElement;
import com.lotadata.moments.Moments;
import com.lotadata.moments.MomentsClient;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import ai.api.AIListener;
import ai.api.android.AIConfiguration;
import ai.api.android.AIService;
import ai.api.model.AIError;
import ai.api.model.AIResponse;
import ai.api.model.Result;
import chatboot.obrabo.com.chatboot.model.CommentsModel;
import chatboot.obrabo.com.chatboot.model.request.ChatBoot;
import chatboot.obrabo.com.chatboot.ui.adapter.CommentsAdapter;

public class MainActivity extends AppCompatActivity implements AIListener {

    public static final int PICK_IMAGE = 1;
    private int positionImage;
    private CircularProgressView pComments;
    private RecyclerView rectComments;
    private EditText edtMsg;
    private FloatingActionButton fabAddMessage;
    private LinearLayoutManager mLinearLayoutManager;
    private CommentsModel commentsModel ;
    private List<CommentsModel> listComments = new ArrayList<>();
    private CommentsAdapter commentsAdapter;
    private int idPost, idPolitic, idUser, typeUser = 1;
    private String messageStr = "", typeUserVoter;
    private static final int REQUEST_INTERNET = 200;
    private Moments mMomentsClient;
    private AIService aiService;
    private int position = 1;
    private  String[] dataQuestion =  {
        "Would you like to contact your city government?",
        "What would you like to discuss?",
        "Could you please describe the issue in a few words?",
        "Could you also upload a picture to support your description?",
        "Thank you for alerting the city. We will get back to you shortly."
    };
    private ChatBoot chatBoot = new ChatBoot();
    private TextView textView;
    protected List<String> mPermissions = new ArrayList<String>();
    protected static final int PERMISSIONS_REQUEST = 100;
    protected BroadcastReceiver receiver = null;
    protected int count = 0;
    protected Context mContext = null;
    protected String mPayloadSent = "# Signals sent = ";
    private static final String PAYLOAD_COUNT_PREFS = "payloadCount.prefs";
    private static final String PAYLOAD_COUNT_KEY = "payloadCount";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initUI();
    }

    private void initUI(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initViews();
        //Mandatory permissions
        mPermissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        //Optional permissions
        //mPermissions.add(Manifest.permission.READ_PHONE_STATE);
       // mPermissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);

       // registerReceiver();
       // checkPermissionsAndLaunch();
    }

    private void initViews(){
        pComments = (CircularProgressView) findViewById(R.id.pComments);
        rectComments = (RecyclerView) findViewById(R.id.rectComments);

        mLinearLayoutManager = new LinearLayoutManager(this);
        rectComments.setLayoutManager(mLinearLayoutManager);

        commentsAdapter = new CommentsAdapter(listComments, MainActivity.this);
        rectComments.setAdapter(commentsAdapter);

        edtMsg = (EditText) findViewById(R.id.edtMsg);
        fabAddMessage = (FloatingActionButton) findViewById(R.id.fabAddMessage);
        fabAddMessage.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                sendMessage();
            }
        });

        // init chat
        commentsModel = new CommentsModel();
        commentsModel.setMessage(dataQuestion[0].toLowerCase());
        commentsModel.setTypeUser("boot");
        commentsAdapter.addComment(commentsModel, rectComments);

        initIA();
        validateOS();
    }

    private void initIA(){
        final AIConfiguration config = new AIConfiguration("e01629dfbd014e5096f19be63718968b",
                AIConfiguration.SupportedLanguages.English,
                AIConfiguration.RecognitionEngine.System);

        aiService = AIService.getService(this, config);
        aiService.setListener(this);
    }

    public void selectImage(int position){
        positionImage = position;
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
    }

    private void initializeLotaDataSDK() {

        mMomentsClient = MomentsClient.getInstance(this);
        if(mMomentsClient!=null){
            Toast.makeText(this,"Initialization Successful",Toast.LENGTH_LONG).show();
            mMomentsClient.recordEvent("Start SDK");
        } else {
            Toast.makeText(this,"Erro sdk lotadata",Toast.LENGTH_LONG).show();
        }
    }

    private void sendMessage(){
        switch (position) {
            case 1:
                aiService.startListening();
                break;
            case 2:
                if (!edtMsg.getText().toString().isEmpty()) {
                    commentsModel = new CommentsModel();
                    commentsModel.setTypeUser("user");
                    commentsModel.setMessage(edtMsg.getText().toString());
                    commentsAdapter.addComment(commentsModel, rectComments);
                    edtMsg.setText("");

                    commentsModel = new CommentsModel();
                    commentsModel.setTypeUser("boot");
                    commentsModel.setMessage(dataQuestion[2].toLowerCase());
                    commentsAdapter.addComment(commentsModel, rectComments);

                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            aiService.startListening();
                        }
                    }, 200);

                    position++;
                    break;
                }
            case 3:
                if (!edtMsg.getText().toString().isEmpty()) {
                    commentsModel = new CommentsModel();
                    commentsModel.setTypeUser("user");
                    commentsModel.setMessage(edtMsg.getText().toString());
                    commentsAdapter.addComment(commentsModel, rectComments);
                    edtMsg.setText("");

                    commentsModel = new CommentsModel();
                    commentsModel.setTypeUser("boot");
                    commentsModel.setMessage(dataQuestion[3].toLowerCase());
                    commentsAdapter.addComment(commentsModel, rectComments);

                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            aiService.startListening();
                        }
                    }, 200);

                    position++;
                    Log.e("position-==",""+position);
                    break;
                }
            }
    }

    @Override
    public void onResult(AIResponse response) {
        Result result = response.getResult();

        // Get parameters
        String parameterString = "";
        if (result.getParameters() != null && !result.getParameters().isEmpty()) {
            for (final Map.Entry<String, JsonElement> entry : result.getParameters().entrySet()) {
                parameterString += "(" + entry.getKey() + ", " + entry.getValue();
            }
        }
        switch (position){
            case 1:
                if(result.getResolvedQuery().equals("yes")){
                    commentsModel = new CommentsModel();
                    commentsModel.setTypeUser("user");
                    commentsModel.setMessage("Yes");
                    chatBoot.setAnswer1("Yes");
                    commentsAdapter.addComment(commentsModel, rectComments);

                    commentsModel = new CommentsModel();
                    commentsModel.setTypeUser("boot_select");
                    commentsModel.setMessage(dataQuestion[1].toLowerCase());
                    commentsAdapter.addComment(commentsModel, rectComments);

                    position++;
                } else{
                    commentsModel = new CommentsModel();
                    commentsModel.setTypeUser("boot");
                    commentsModel.setMessage(getResources().getString(R.string.notice_boot_one));
                    commentsAdapter.addComment(commentsModel, rectComments);
                }
                break;
            case 3:
                if(result.getResolvedQuery().equals("yes")){
                    commentsModel = new CommentsModel();
                    commentsModel.setTypeUser("user");
                    commentsModel.setMessage("Yes");
                    commentsAdapter.addComment(commentsModel, rectComments);
                    chatBoot.setAnswer3("Yes");
                } else{
                    commentsModel = new CommentsModel();
                    commentsModel.setTypeUser("boot");
                    commentsModel.setMessage(getResources().getString(R.string.notice_boot_one));
                    commentsAdapter.addComment(commentsModel, rectComments);
                }
                break;
            case 4:
                if(result.getResolvedQuery().equals("yes")){
                    commentsModel = new CommentsModel();
                    commentsModel.setTypeUser("boot_image");
                    commentsModel.setMessage("Yes");
                    commentsModel.setImage(null);
                    commentsAdapter.addComment(commentsModel, rectComments);
                    chatBoot.setAnswer4("Yes");

                } else{
                    commentsModel = new CommentsModel();
                    commentsModel.setTypeUser("boot");
                    commentsModel.setMessage(getResources().getString(R.string.notice_boot_one));
                    commentsAdapter.addComment(commentsModel, rectComments);
                }
                break;
        }
    }

    private void validateOS() {
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.RECORD_AUDIO}, REQUEST_INTERNET);
        }
    }

    public void setValueMessage(String value){
        chatBoot.setAnswer2(value);
        edtMsg.setText(value);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_IMAGE) {
            //TODO: action
            Uri imageUri = data.getData();
            commentsModel = new CommentsModel();
            commentsModel.setTypeUser("boot_image");
            commentsModel.setMessage("Yes");
            commentsModel.setImage(imageUri);
            commentsAdapter.updateImage(commentsModel, rectComments, positionImage, dataQuestion[4].toLowerCase());
            chatBoot.setImage(imageUri.toString());

            FirebaseDatabase database =  FirebaseDatabase.getInstance();
            DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("chatBoot");
            String userId = mDatabase.push().getKey();
            DatabaseReference mRef =  database.getReference().child("ChatBoot").child(userId);
            mRef.setValue(chatBoot);
        }
    }


    @Override
    public void onError(AIError error) {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                aiService.startListening();
            }
        }, 200);
    }

    @Override
    public void onAudioLevel(float level) {

    }

    @Override
    public void onListeningStarted() {

    }

    @Override
    public void onListeningCanceled() {

    }

    @Override
    public void onListeningFinished() {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    protected void registerReceiver()
    {
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                count++;
                //We can't ensure that onDestroy will be called. So, update counter now.
                updatePayloadSentCount();
                Toast.makeText(mContext, "Signal sent", Toast.LENGTH_SHORT).show();
                textView.setText(mPayloadSent+count);
            }
        };

        LocalBroadcastManager.getInstance(this).registerReceiver((receiver),
                new IntentFilter(Moments.PAYLOAD_SENT)
        );
    }

    /**
     * Update payload send coun
     */
    private void updatePayloadSentCount() {
        SharedPreferences sharedpreferences = this.getSharedPreferences(PAYLOAD_COUNT_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(PAYLOAD_COUNT_KEY, String.valueOf(count));
        editor.commit();
    }

    private int getPastCount() {

        SharedPreferences sharedpreferences = this.getSharedPreferences(PAYLOAD_COUNT_PREFS, Context.MODE_PRIVATE);
        String value = sharedpreferences.getString(PAYLOAD_COUNT_KEY, "0");
        return Integer.valueOf(value);
    }

    protected void checkPermissionsAndLaunch()
    {
        for(String perm : mPermissions)
        {
            final String[] permissions = { perm };
            if(ActivityCompat.checkSelfPermission(this, perm) != PackageManager.PERMISSION_GRANTED)
            {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    MainActivity.this.requestPermissions(permissions, PERMISSIONS_REQUEST);
                    return;
                }
            }
        }
        initializeLotaDataSDK();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode != PERMISSIONS_REQUEST) {
            return;
        }

        /**
         * Permission request not interrupted, and all permissions granted
         */
        if (permissions.length > 0 && grantResults.length > 0) {
            boolean granted = true;
            for(int i = 0; i < permissions.length; i++)
            {
                granted &= grantResults[i] == PackageManager.PERMISSION_GRANTED;
            }

            if(granted)
            {
              //  checkPermissionsAndLaunch();
                return;
            }
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.LotaDataDialog);

        builder.setTitle(getResources().getString(R.string.dialog_permission_title));
        builder.setMessage(getResources().getString(R.string.dialog_permission_message));

        builder.setNeutralButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
