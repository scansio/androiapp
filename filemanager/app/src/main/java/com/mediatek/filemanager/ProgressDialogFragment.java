package com.mediatek.filemanager;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.text.TextUtils.TruncateAt;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewParent;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.mediatek.filemanager.service.ProgressInfo;

public class ProgressDialogFragment extends DialogFragment {
    private static final String CANCEL = "cancel";
    private static final String CURRENTNUM = "currentNumber";
    private static final String MESSAGE = "message";
    private static final String PROGRESS = "progress";
    private static final String STYLE = "style";
    public static final String TAG = "ProgressDialogFragment";
    private static final String TITLE = "title";
    private static final String TOTAL = "total";
    private static final String TOTALNUM = "totalNumber";
    private OnClickListener mCancelListener = null;
    private TextView mProgressNum = null;
    private int mSavedCurrentNum = 0;
    private int mSavedTotalNum = 0;
    private int mViewDirection = 0;

    public static ProgressDialogFragment newInstance(int style, int title, int message, int cancel) {
        ProgressDialogFragment f = new ProgressDialogFragment();
        Bundle args = new Bundle();
        args.putInt(STYLE, style);
        args.putInt(TITLE, title);
        args.putInt(CANCEL, cancel);
        args.putInt(MESSAGE, message);
        f.setArguments(args);
        return f;
    }

    public void onSaveInstanceState(Bundle outState) {
        outState.putAll(getArguments());
        ProgressDialog dialog = (ProgressDialog) getDialog();
        if (dialog != null) {
            outState.putInt(TOTAL, dialog.getMax());
            outState.putInt(PROGRESS, dialog.getProgress());
            outState.putInt(CURRENTNUM, this.mSavedCurrentNum);
            outState.putInt(TOTALNUM, this.mSavedTotalNum);
        }
        super.onSaveInstanceState(outState);
    }

    public void onResume() {
        super.onResume();
        if (getDialog() != null) {
            ((Button) getDialog().findViewById(16908315)).setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    if (ProgressDialogFragment.this.mCancelListener != null) {
                        ProgressDialogFragment.this.mCancelListener.onClick(v);
                    }
                    ((ProgressDialog) ProgressDialogFragment.this.getDialog()).setMessage(ProgressDialogFragment.this.getString(R.string.wait));
                    v.setVisibility(4);
                }
            });
        }
    }

    public void setCancelListener(OnClickListener listener) {
        this.mCancelListener = listener;
    }

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle args;
        setCancelable(false);
        ProgressDialog dialog = new ProgressDialog(getActivity());
        dialog.setCancelable(false);
        dialog.setIndeterminate(false);
        if (savedInstanceState == null) {
            args = getArguments();
        } else {
            args = savedInstanceState;
        }
        if (args != null) {
            dialog.setProgressStyle(args.getInt(STYLE, 1));
            int title = args.getInt(TITLE, -1);
            if (title != -1) {
                dialog.setTitle(title);
            }
            int cancel = args.getInt(CANCEL, -1);
            if (cancel != -1) {
                dialog.setButton(-3, getString(cancel), (Message) null);
            }
            int message = args.getInt(MESSAGE, -1);
            if (message != -1) {
                dialog.setMessage(getString(message));
            }
            int total = args.getInt(TOTAL, -1);
            if (total != -1) {
                dialog.setMax(total);
            }
            int progress = args.getInt(PROGRESS, -1);
            if (progress != -1) {
                dialog.setProgress(progress);
            }
            if (this.mProgressNum != null) {
                int currentNum = args.getInt(CURRENTNUM, -1);
                int totalNum = args.getInt(TOTALNUM, -1);
                if (!(currentNum == -1 || totalNum == -1)) {
                    this.mProgressNum.setText(currentNum + MountPointManager.SEPARATOR + totalNum);
                }
            }
        }
        dialog.setOnKeyListener(new OnKeyListener() {
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == 84) {
                    return true;
                }
                return false;
            }
        });
        return dialog;
    }

    public void onStart() {
        super.onStart();
        if (this.mProgressNum == null) {
            ProgressDialog mDialog = (ProgressDialog) getDialog();
            this.mProgressNum = new TextView(mDialog.getContext());
            TextView view = (TextView) mDialog.findViewById(16908923);
            this.mProgressNum.setLayoutParams(view.getLayoutParams());
            view.setVisibility(8);
            this.mProgressNum.setTextColor(view.getCurrentTextColor());
            this.mProgressNum.setSingleLine();
            ViewParent vParent = view.getParent();
            if (vParent instanceof RelativeLayout) {
                ((RelativeLayout) vParent).addView(this.mProgressNum);
            }
        }
    }

    public void setProgress(ProgressInfo progeressInfo) {
        ProgressDialog progressDialog = (ProgressDialog) getDialog();
        if (progressDialog != null && progeressInfo != null) {
            TextView messageView = (TextView) progressDialog.findViewById(16908299);
            if (messageView != null) {
                messageView.setSingleLine();
                messageView.setEllipsize(TruncateAt.MIDDLE);
            }
            progressDialog.setProgress(progeressInfo.getProgeress());
            String message = progeressInfo.getUpdateInfo();
            if (!TextUtils.isEmpty(message)) {
                progressDialog.setMessage(message);
            }
            progressDialog.setMax((int) progeressInfo.getTotal());
            this.mSavedCurrentNum = progeressInfo.getCurrentNumber();
            this.mSavedTotalNum = (int) progeressInfo.getTotalNumber();
            if (this.mProgressNum == null) {
                return;
            }
            if (this.mViewDirection == 0) {
                this.mProgressNum.setText(this.mSavedCurrentNum + MountPointManager.SEPARATOR + this.mSavedTotalNum);
            } else if (this.mViewDirection == 1) {
                this.mProgressNum.setText(this.mSavedCurrentNum + "\\" + this.mSavedTotalNum);
            }
        }
    }

    public void setViewDirection(int direction) {
        this.mViewDirection = direction;
    }
}
