package com.codepath.apps.simpletwitterclient;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.apps.simpletwitterclient.models.User;
import com.squareup.picasso.Picasso;

/**
 * Created by nikhil on 10/2/15.
 */
public class ComposeTweetDialog extends DialogFragment {

    private EditText etComposeTweet;
    private Button btPostTweet;

    public interface ComposeTweetDialogListener {
        void onFinishComposeDialog(String inputText);
    }

    public ComposeTweetDialog() {

    }

    public static ComposeTweetDialog newInstance(String title, User user) {
        ComposeTweetDialog dialog = new ComposeTweetDialog();
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putParcelable("user", user);
        dialog.setArguments(args);

        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_compose_tweet, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle args = getArguments();
        String title = args.getString("title", "Compose Tweet");
        User user = args.getParcelable("user");
        getDialog().setTitle(title);

        etComposeTweet = (EditText) view.findViewById(R.id.etComposeTweet);
        etComposeTweet.requestFocus();
        btPostTweet = (Button) view.findViewById(R.id.btPostTweet);
        btPostTweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ComposeTweetDialogListener listener = (ComposeTweetDialogListener) getActivity();
                listener.onFinishComposeDialog(etComposeTweet.getText().toString());
                dismiss();
            }
        });

        TextView tvUsername = (TextView) view.findViewById(R.id.tvUsername);
        tvUsername.setText(user.getScreenName());

        ImageView ivUserProfileImage = (ImageView) view.findViewById(R.id.ivUserProfileImage);
        Picasso.with(getContext())
                .load(user.getProfileImageUrl())
                .into(ivUserProfileImage);

        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }
}
