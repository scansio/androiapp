package com.androi.development;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import com.android.internal.util.HexDump;

public class SMSTester extends Activity {
    private final String MOCK_PDU = "07914151551512f2040B916105551511f100006060605130308A04D4F29C0E";
    private EditText mMsg;
    private EditText mSc;
    private EditText mSender;

    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.sms_tester);
        this.mSc = (EditText) findViewById(R.id.sms_tester_mock_sms_sc);
        this.mSender = (EditText) findViewById(R.id.sms_tester_mock_sms_sender);
        this.mMsg = (EditText) findViewById(R.id.sms_tester_mock_sms_msg);
        ((Button) findViewById(R.id.sms_tester_mock_sms_send_msg)).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                String sc = SMSTester.this.mSc.getText().toString();
                String sender = SMSTester.this.mSender.getText().toString();
                String msg = SMSTester.this.mMsg.getText().toString();
                Intent in = new Intent("android.provider.Telephony.MOCK_SMS_RECEIVED");
                if (!TextUtils.isEmpty(sc)) {
                    in.putExtra("scAddr", sc);
                }
                if (!TextUtils.isEmpty(sender)) {
                    in.putExtra("senderAddr", sender);
                }
                if (!TextUtils.isEmpty(msg)) {
                    in.putExtra("msg", msg);
                }
                SMSTester.this.sendBroadcast(in);
            }
        });
        ((Button) findViewById(R.id.sms_tester_mock_sms_send_pdu)).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                byte[][] pdus = new byte[][]{HexDump.hexStringToByteArray("07914151551512f2040B916105551511f100006060605130308A04D4F29C0E")};
                Intent in = new Intent("android.provider.Telephony.MOCK_SMS_RECEIVED");
                in.putExtra("pdus", pdus);
                SMSTester.this.sendBroadcast(in);
            }
        });
    }

    protected void onResume() {
        super.onResume();
    }

    protected void onStop() {
        super.onStop();
    }
}
