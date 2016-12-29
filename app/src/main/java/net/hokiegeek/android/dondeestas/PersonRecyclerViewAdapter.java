package net.hokiegeek.android.dondeestas;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.hokiegeek.android.dondeestas.PersonFragment.OnListFragmentInteractionListener;
import net.hokiegeek.android.dondeestas.data.Person;
// import net.hokiegeek.android.dondeestas.dummy.DummyContent.Person;

import java.util.ArrayList;
import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Person} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 */
public class PersonRecyclerViewAdapter extends RecyclerView.Adapter<PersonRecyclerViewAdapter.ViewHolder> {
    private final List<Person> people;

    private final OnListFragmentInteractionListener listener;

    public PersonRecyclerViewAdapter(OnListFragmentInteractionListener listener) {
        this.people = new ArrayList<>();
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_person, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = people.get(position);
        holder.mIdView.setText(people.get(position).getId());
        // holder.mContentView.setText(people.get(position).content);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != listener) {
                    listener.onListFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return people.size();
    }

    public void updateItems(List<Person> items) {
        Log.v(Util.TAG, "PersonRecyclerViewAdapter.updateItems()");

        int oldListSize = this.people.size();
        boolean removed = this.people.retainAll(items);

        boolean inserted = false;
        items.removeAll(this.people);
        if (!items.isEmpty()) {
            this.people.addAll(items);
            inserted = true;
        }

        if (removed) {
            notifyItemRangeRemoved(0, oldListSize);
        }
        if (inserted) {
            notifyItemRangeInserted(0, this.people.size());
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mIdView;
        // public final TextView mContentView;
        public Person mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = (TextView) view.findViewById(R.id.id);
            // mContentView = (TextView) view.findViewById(R.id.content);
        }

        /*
        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
        */
    }
}
