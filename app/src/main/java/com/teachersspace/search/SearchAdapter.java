package com.teachersspace.search;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.teachersspace.R;
import com.teachersspace.models.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> {
    private static final String TAG = "SearchAdapter";
    private List<User> searchList; // all contactable users
    private ArrayList<User> resultList; // filtered users
    private final SearchFragment searchFragment;

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final Button buttonView;
        private final TextView textView;

        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View
            buttonView = view.findViewById(R.id.contactItemButton);
            textView = view.findViewById(R.id.contactNoResults);
        }

        public Button getButtonView() {
            return buttonView;
        }
        public TextView getTextView() {
            return textView;
        }
    }

    /**
     * Initialize the dataset of the Adapter.
     * @param dataSet List<User> containing the data to populate views to be used by RecyclerView.
     */
    public SearchAdapter(List<User> dataSet, SearchFragment fragment) {
        Log.d(TAG, "dataset: " + dataSet);
        searchList = dataSet;
        searchFragment = fragment;
        resultList = (ArrayList<User>) dataSet;
    }

    /**
     * Create new views (invoked by the layout manager)
     * @param parent Parent ViewGroup
     * @param viewType The view type of the new View.
     * @return viewHolder
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.contact_item, parent, false);

        return new ViewHolder(view);
    }

    /**
     * Replace the contents of a view (invoked by the layout manager)
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        Button singleContactButton = holder.getButtonView();
        TextView noResultsText = holder.getTextView();
        User user = resultList.get(position);
        if (position == 0 && user.getUid().equals("000") && user.getName().equals("noResults")) {
            singleContactButton.setVisibility(View.GONE);
            noResultsText.setVisibility(View.VISIBLE);
        } else {
            noResultsText.setVisibility(View.GONE);
            singleContactButton.setVisibility(View.VISIBLE);
            singleContactButton.setText(user.getName());
            singleContactButton.setOnClickListener(searchFragment.searchIndividualListenerFactory(position));
        }

    }
    /**
     * @return the size of dataset (invoked by the layout manager)
     */
    @Override
    public int getItemCount() {
        return resultList.size();
    }

    public void updateLocalData(List<User> newData) {
        if (searchList.size() == 0) searchList = new ArrayList<>(newData); // initial storing of contactable users
        resultList.clear();
        if (newData.size() == 0) resultList.addAll(searchList);
        else resultList.addAll(newData);
        this.notifyDataSetChanged();
        // TODO: https://stackoverflow.com/questions/68602157/it-will-always-be-more-efficient-to-use-more-specific-change-events-if-you-can
    }

    /**
     * Filter contacts by query string
     * @param query String to search for
     */
    public List<User> filter(String query) {
        Log.d(TAG, "searchList: " + searchList);
        Log.d(TAG, "resultList: " + resultList);
        query = query.toLowerCase(Locale.getDefault());

        if (query.length() == 0) {
            Log.i(TAG, "Query length 0");
            resultList.clear();
            resultList.addAll(searchList);
        } else {
            // Possible issue: resultList must be consistent in length after query
            // Added non matches after to maintain length
            List<User> matches = new ArrayList<>();
            List<User> unmatches = new ArrayList<>();
            for (User user: searchList) {
                String username = user.getName().toLowerCase(Locale.getDefault());
                Log.d(TAG,String.format("username: %s, contains '%s': %s", username, query, username.contains(query)) );
                if (username.contains(query)) matches.add(user);
                else unmatches.add(user);
            }
            Log.d(TAG, String.format("Added matches: %s", matches));
            resultList.clear();
            if(matches.size() != 0) resultList.addAll(matches);
            else resultList.add(new User("000", "noResults", "", User.UserType.TEACHER));

            Log.i(TAG, "Filtered by query");
        }
        this.notifyDataSetChanged();
        Log.d(TAG, "Fetched: " + resultList);
        return resultList;
    }
}
