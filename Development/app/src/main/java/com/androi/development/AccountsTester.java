package com.androi.development;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorDescription;
import android.accounts.AuthenticatorException;
import android.accounts.OnAccountsUpdateListener;
import android.accounts.OperationCanceledException;
import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources.NotFoundException;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AccountsTester extends Activity implements OnAccountsUpdateListener {
    private AccountManager mAccountManager;
    private Spinner mAccountTypesSpinner;
    private ListView mAccountsListView;
    private AuthenticatorDescription[] mAuthenticatorDescs;
    private EditText mDesiredAuthTokenTypeEditText;
    private EditText mDesiredFeaturesEditText;
    private volatile CharSequence mDialogMessage;
    private Account mLongPressedAccount = null;

    private class AccountArrayAdapter extends ArrayAdapter<Account> {
        protected LayoutInflater mInflater;

        class ViewHolder {
            Account account;
            ImageView icon;
            TextView name;

            ViewHolder() {
            }
        }

        public AccountArrayAdapter(Context context, Account[] accounts) {
            super(context, R.layout.account_list_item, accounts);
            this.mInflater = (LayoutInflater) context.getSystemService("layout_inflater");
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = this.mInflater.inflate(R.layout.account_list_item, null);
                holder = new ViewHolder();
                holder.name = (TextView) convertView.findViewById(R.id.accounts_tester_account_name);
                holder.icon = (ImageView) convertView.findViewById(R.id.accounts_tester_account_type_icon);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            Account account = (Account) getItem(position);
            holder.account = account;
            holder.icon.setVisibility(4);
            for (AuthenticatorDescription desc : AccountsTester.this.mAuthenticatorDescs) {
                if (desc.type.equals(account.type)) {
                    String packageName = desc.packageName;
                    try {
                        holder.icon.setImageDrawable(getContext().createPackageContext(packageName, 0).getResources().getDrawable(desc.iconId));
                        holder.icon.setVisibility(0);
                    } catch (NameNotFoundException e) {
                        Log.d("AccountsTester", "error getting the Package Context for " + packageName, e);
                    }
                }
            }
            holder.name.setText(account.name);
            return convertView;
        }
    }

    class ButtonClickListener implements OnClickListener {
        ButtonClickListener() {
        }

        public void onClick(View v) {
            if (R.id.accounts_tester_get_all_accounts == v.getId()) {
                AccountsTester.this.onAccountsUpdated(AccountsTester.this.mAccountManager.getAccounts());
            } else if (R.id.accounts_tester_get_accounts_by_type == v.getId()) {
                AccountsTester.this.onAccountsUpdated(AccountsTester.this.mAccountManager.getAccountsByType(AccountsTester.this.getSelectedAuthenticator().type));
            } else if (R.id.accounts_tester_add_account == v.getId()) {
                String authTokenType = AccountsTester.this.mDesiredAuthTokenTypeEditText.getText().toString();
                if (TextUtils.isEmpty(authTokenType)) {
                    authTokenType = null;
                }
                String[] requiredFeatures = TextUtils.split(AccountsTester.this.mDesiredFeaturesEditText.getText().toString(), " ");
                if (requiredFeatures.length == 0) {
                    requiredFeatures = null;
                }
                AccountsTester.this.mAccountManager.addAccount(AccountsTester.this.getSelectedAuthenticator().type, authTokenType, requiredFeatures, null, AccountsTester.this, new CallbackToDialog(AccountsTester.this, "add account", null), null);
            } else if (R.id.accounts_tester_edit_properties == v.getId()) {
                AccountsTester.this.mAccountManager.editProperties(AccountsTester.this.getSelectedAuthenticator().type, AccountsTester.this, new CallbackToDialog(AccountsTester.this, "edit properties", null), null);
            } else if (R.id.accounts_tester_get_auth_token_by_type_and_feature == v.getId()) {
                AccountsTester.this.showDialog(6);
            }
        }
    }

    private static class CallbackToDialog implements AccountManagerCallback<Bundle> {
        private final AccountsTester mActivity;
        private final String mLabel;

        /* synthetic */ CallbackToDialog(AccountsTester x0, String x1, AnonymousClass1 x2) {
            this(x0, x1);
        }

        private CallbackToDialog(AccountsTester activity, String label) {
            this.mActivity = activity;
            this.mLabel = label;
        }

        public void run(AccountManagerFuture<Bundle> future) {
            this.mActivity.getAndLogResult(future, this.mLabel);
        }
    }

    private class GetAndInvalidateAuthTokenCallback implements AccountManagerCallback<Bundle> {
        private final Account mAccount;

        /* synthetic */ GetAndInvalidateAuthTokenCallback(AccountsTester x0, Account x1, AnonymousClass1 x2) {
            this(x1);
        }

        private GetAndInvalidateAuthTokenCallback(Account account) {
            this.mAccount = account;
        }

        public void run(AccountManagerFuture<Bundle> future) {
            Bundle result = AccountsTester.this.getAndLogResult(future, "get and invalidate");
            if (result != null) {
                AccountsTester.this.mAccountManager.invalidateAuthToken(this.mAccount.type, result.getString("authtoken"));
            }
        }
    }

    private class TestHasFeaturesCallback implements AccountManagerCallback<Boolean> {
        private TestHasFeaturesCallback() {
        }

        /* synthetic */ TestHasFeaturesCallback(AccountsTester x0, AnonymousClass1 x1) {
            this();
        }

        public void run(AccountManagerFuture<Boolean> future) {
            try {
                Boolean hasFeatures = (Boolean) future.getResult();
                Log.d("AccountsTester", "hasFeatures: " + hasFeatures);
                AccountsTester.this.showMessageDialog("hasFeatures: " + hasFeatures);
            } catch (OperationCanceledException e) {
                Log.d("AccountsTester", "interrupted");
                AccountsTester.this.showMessageDialog("operation was canceled");
            } catch (IOException e2) {
                Log.d("AccountsTester", "error", e2);
                AccountsTester.this.showMessageDialog("operation got an IOException");
            } catch (AuthenticatorException e3) {
                Log.d("AccountsTester", "error", e3);
                AccountsTester.this.showMessageDialog("operation got an AuthenticationException");
            }
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mAccountManager = AccountManager.get(this);
        setContentView(R.layout.accounts_tester);
        ButtonClickListener buttonClickListener = new ButtonClickListener();
        this.mAccountTypesSpinner = (Spinner) findViewById(R.id.accounts_tester_account_types_spinner);
        this.mAccountsListView = (ListView) findViewById(R.id.accounts_tester_accounts_list);
        registerForContextMenu(this.mAccountsListView);
        initializeAuthenticatorsSpinner();
        findViewById(R.id.accounts_tester_get_all_accounts).setOnClickListener(buttonClickListener);
        findViewById(R.id.accounts_tester_get_accounts_by_type).setOnClickListener(buttonClickListener);
        findViewById(R.id.accounts_tester_add_account).setOnClickListener(buttonClickListener);
        findViewById(R.id.accounts_tester_edit_properties).setOnClickListener(buttonClickListener);
        findViewById(R.id.accounts_tester_get_auth_token_by_type_and_feature).setOnClickListener(buttonClickListener);
        this.mDesiredAuthTokenTypeEditText = (EditText) findViewById(R.id.accounts_tester_desired_authtokentype);
        this.mDesiredFeaturesEditText = (EditText) findViewById(R.id.accounts_tester_desired_features);
    }

    private void initializeAuthenticatorsSpinner() {
        this.mAuthenticatorDescs = this.mAccountManager.getAuthenticatorTypes();
        List<String> names = new ArrayList(this.mAuthenticatorDescs.length);
        for (int i = 0; i < this.mAuthenticatorDescs.length; i++) {
            try {
                try {
                    names.add(createPackageContext(this.mAuthenticatorDescs[i].packageName, 0).getString(this.mAuthenticatorDescs[i].labelId));
                } catch (NotFoundException e) {
                }
            } catch (NameNotFoundException e2) {
            }
        }
        this.mAccountTypesSpinner.setAdapter(new ArrayAdapter(this, 17367048, (String[]) names.toArray(new String[names.size()])));
    }

    public void onAccountsUpdated(Account[] accounts) {
        Log.d("AccountsTester", "onAccountsUpdated: \n  " + TextUtils.join("\n  ", accounts));
        this.mAccountsListView.setAdapter(new AccountArrayAdapter(this, accounts));
    }

    protected void onStart() {
        super.onStart();
        this.mAccountManager.addOnAccountsUpdatedListener(this, new Handler(getMainLooper()), true);
    }

    protected void onStop() {
        super.onStop();
        this.mAccountManager.removeOnAccountsUpdatedListener(this);
    }

    private AuthenticatorDescription getSelectedAuthenticator() {
        return this.mAuthenticatorDescs[this.mAccountTypesSpinner.getSelectedItemPosition()];
    }

    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        menu.setHeaderTitle(R.string.accounts_tester_account_context_menu_title);
        AdapterContextMenuInfo info = (AdapterContextMenuInfo) menuInfo;
        getMenuInflater().inflate(R.layout.account_list_context_menu, menu);
        this.mLongPressedAccount = ((ViewHolder) info.targetView.getTag()).account;
    }

    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelable("account", this.mLongPressedAccount);
    }

    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        this.mLongPressedAccount = (Account) savedInstanceState.getParcelable("account");
    }

    public boolean onContextItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.accounts_tester_remove_account) {
            final Account account = this.mLongPressedAccount;
            this.mAccountManager.removeAccount(account, new AccountManagerCallback<Boolean>() {
                public void run(AccountManagerFuture<Boolean> future) {
                    try {
                        Log.d("AccountsTester", "removeAccount(" + account + ") = " + future.getResult());
                    } catch (OperationCanceledException e) {
                    } catch (IOException e2) {
                    } catch (AuthenticatorException e3) {
                    }
                }
            }, null);
        } else if (item.getItemId() == R.id.accounts_tester_clear_password) {
            this.mAccountManager.clearPassword(this.mLongPressedAccount);
            showMessageDialog("cleared");
        } else if (item.getItemId() == R.id.accounts_tester_get_auth_token) {
            showDialog(1);
        } else if (item.getItemId() == R.id.accounts_tester_test_has_features) {
            showDialog(4);
        } else if (item.getItemId() == R.id.accounts_tester_invalidate_auth_token) {
            showDialog(3);
        } else if (item.getItemId() == R.id.accounts_tester_update_credentials) {
            showDialog(2);
        } else if (item.getItemId() == R.id.accounts_tester_confirm_credentials) {
            this.mAccountManager.confirmCredentials(this.mLongPressedAccount, null, this, new CallbackToDialog(this, "confirm credentials", null), null);
        }
        return true;
    }

    protected Dialog onCreateDialog(final int id) {
        final View view;
        Builder builder;
        switch (id) {
            case 1:
            case 2:
            case 3:
            case 4:
                view = LayoutInflater.from(this).inflate(R.layout.get_auth_token_view, null);
                builder = new Builder(this);
                builder.setPositiveButton(R.string.accounts_tester_ok_button, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String authTokenType = ((EditText) view.findViewById(R.id.accounts_tester_auth_token_type)).getText().toString();
                        Account account = AccountsTester.this.mLongPressedAccount;
                        if (id == 1) {
                            AccountsTester.this.mAccountManager.getAuthToken(account, authTokenType, null, AccountsTester.this, new CallbackToDialog(AccountsTester.this, "get auth token", null), null);
                        } else if (id == 3) {
                            AccountsTester.this.mAccountManager.getAuthToken(account, authTokenType, false, new GetAndInvalidateAuthTokenCallback(AccountsTester.this, account, null), null);
                        } else if (id == 4) {
                            AccountsTester.this.mAccountManager.hasFeatures(account, TextUtils.split(authTokenType, ","), new TestHasFeaturesCallback(AccountsTester.this, null), null);
                        } else {
                            AccountsTester.this.mAccountManager.updateCredentials(account, authTokenType, null, AccountsTester.this, new CallbackToDialog(AccountsTester.this, "update", null), null);
                        }
                    }
                });
                builder.setView(view);
                return builder.create();
            case 5:
                builder = new Builder(this);
                builder.setMessage(this.mDialogMessage);
                return builder.create();
            case 6:
                view = LayoutInflater.from(this).inflate(R.layout.get_features_view, null);
                builder = new Builder(this);
                builder.setPositiveButton(R.string.accounts_tester_ok_button, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String authTokenType = ((EditText) view.findViewById(R.id.accounts_tester_auth_token_type)).getText().toString();
                        String features = ((EditText) view.findViewById(R.id.accounts_tester_features)).getText().toString();
                        Account account = AccountsTester.this.mLongPressedAccount;
                        AccountsTester.this.mAccountManager.getAuthTokenByFeatures(AccountsTester.this.getSelectedAuthenticator().type, authTokenType, TextUtils.isEmpty(features) ? null : features.split(" "), AccountsTester.this, null, null, new CallbackToDialog(AccountsTester.this, "get auth token by features", null), null);
                    }
                });
                builder.setView(view);
                return builder.create();
            default:
                return super.onCreateDialog(id);
        }
    }

    private void showMessageDialog(String message) {
        this.mDialogMessage = message;
        removeDialog(5);
        showDialog(5);
    }

    private Bundle getAndLogResult(AccountManagerFuture<Bundle> future, String label) {
        try {
            Bundle bundle = (Bundle) future.getResult();
            bundle.keySet();
            Log.d("AccountsTester", label + ": " + bundle);
            StringBuffer sb = new StringBuffer();
            sb.append(label).append(" result:");
            for (String key : bundle.keySet()) {
                Object value = bundle.get(key);
                if ("authtoken".equals(key)) {
                    value = "<redacted>";
                }
                sb.append("\n  ").append(key).append(" -> ").append(value);
            }
            showMessageDialog(sb.toString());
            return bundle;
        } catch (OperationCanceledException e) {
            Log.d("AccountsTester", label + " failed", e);
            showMessageDialog(label + " was canceled");
            return null;
        } catch (IOException e2) {
            Log.d("AccountsTester", label + " failed", e2);
            showMessageDialog(label + " failed with IOException: " + e2.getMessage());
            return null;
        } catch (AuthenticatorException e3) {
            Log.d("AccountsTester", label + " failed", e3);
            showMessageDialog(label + " failed with an AuthenticatorException: " + e3.getMessage());
            return null;
        }
    }
}
