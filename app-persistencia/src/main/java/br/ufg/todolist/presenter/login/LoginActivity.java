package br.ufg.todolist.presenter.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import br.ufg.todolist.presenter.list.TodoListActivity;
import br.ufg.todolist.R;
import br.ufg.todolist.data.EasySharedPreferences;
import br.ufg.todolist.model.FormProblemException;
import br.ufg.todolist.model.User;
import br.ufg.todolist.presenter.BaseActivity;
import br.ufg.todolist.web.WebLogin;

public class LoginActivity extends BaseActivity {

    private final int MIN_PASSWORD = 6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        setStringFromEdit(R.id.username,EasySharedPreferences.getStringFromKey(
                this, EasySharedPreferences.KEY_EMAIL));
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    public void login(View v){

        hideKeyboard();

        try{
            checkEmail();
            checkPassword();
        } catch (FormProblemException e){
            showAlert(e.getMessage());
            return;
        }

        String password = getStringFromEdit(R.id.password);
        String email = getStringFromEdit(R.id.username);

        showDialogWithMessage(getString(R.string.load_login));

        tryLogin(password,email);
    }

    private void checkPassword() throws FormProblemException{
        String password = getStringFromEdit(R.id.password);
        if("".equals(password)){
            throw new FormProblemException(getString(R.string.error_password_empty));
        }

        if (password.length() < MIN_PASSWORD){
            throw new FormProblemException(getString(R.string.error_password_small));
        }
    }

    private void checkEmail() throws FormProblemException{
        String email = getStringFromEdit(R.id.username);
        if("".equals(email)){
            throw new FormProblemException(getString(R.string.error_password_empty));
        }
    }

    private void tryLogin(String password, String email) {
        WebLogin webLogin = new WebLogin(email,password);
        webLogin.call();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(User user) {
        dismissDialog();
        storeCredentials(user);
        goToHome();
    }

    private void storeCredentials(User user){
        EasySharedPreferences.setStringFromKey(this,EasySharedPreferences.KEY_EMAIL,user.getEmail());
        EasySharedPreferences.setStringFromKey(this,EasySharedPreferences.KEY_TOKEN,user.getToken());
    }

    private void goToHome() {
        Intent intent = new Intent(this,TodoListActivity.class);
        startActivity(intent);
        finish();

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(Exception exception) {
        dismissDialog();
        showAlert(exception.getMessage());
    }
}
