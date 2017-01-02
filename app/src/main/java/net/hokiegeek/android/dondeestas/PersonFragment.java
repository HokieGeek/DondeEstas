package net.hokiegeek.android.dondeestas;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import net.hokiegeek.android.dondeestas.data.Person;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * and {@link OnAddFollowingListener}
 * interface.
 */
public class PersonFragment extends Fragment {

    private OnListFragmentInteractionListener listListener;

    private OnAddFollowingListener addFollowingListener;

    private OnFragmentLoadedListener fragmentLoadedListener;

    private PersonRecyclerViewAdapter adapter;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public PersonFragment() {
    }

    public static PersonFragment newInstance() {
        return new PersonFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_person_list, container, false);

        final Context context = view.getContext();

        // Set the adapter
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.followingList);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        adapter = new PersonRecyclerViewAdapter(listListener);
        recyclerView.setAdapter(adapter);

        // Set the add button
        FloatingActionButton myFab = (FloatingActionButton) view.findViewById(R.id.fabAddFollowing);
        myFab.setOnClickListener(new View.OnClickListener() {
            // Set the Edit text to use
            final EditText followPerson = new EditText(context);

            {
                followPerson.setHint(R.string.dialog_request_following_text_hint);
                followPerson.setMaxLines(1);
            }

            final AlertDialog dlg = new AlertDialog.Builder(context)
                            .setTitle("Add a person")
                            .setView(followPerson)
                            .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    addFollowingListener.onFollowPeople(new ArrayList<>(Arrays.asList(followPerson.getText().toString())));
                                    followPerson.setText("");
                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    followPerson.setText("");
                                }
                            }).create();

            public void onClick(View v) {
                dlg.show();
            }
        });

        fragmentLoadedListener.onFragmentLoaded(this);

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof OnListFragmentInteractionListener) {
            listListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }

        if (context instanceof OnAddFollowingListener) {
            addFollowingListener = (OnAddFollowingListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnAddFollowingListener");
        }

        if (context instanceof OnFragmentLoadedListener) {
            fragmentLoadedListener = (OnFragmentLoadedListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentLoadedListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listListener = null;
        addFollowingListener = null;
    }

    public void updateItems(List<Person> items) {
        if (adapter != null) {
            adapter.updateItems(items);
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    public interface OnListFragmentInteractionListener {
        void onListFragmentInteraction(Person item);
    }

    public interface OnAddFollowingListener {
        void onFollowPeople(List<String> ids);
    }
}
