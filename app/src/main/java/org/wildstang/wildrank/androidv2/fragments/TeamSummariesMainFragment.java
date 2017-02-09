package org.wildstang.wildrank.androidv2.fragments;

import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Document;
import com.couchbase.lite.Query;
import com.couchbase.lite.QueryEnumerator;
import com.couchbase.lite.QueryRow;

import org.wildstang.wildrank.androidv2.R;
import org.wildstang.wildrank.androidv2.adapters.TeamListAdapter;
import org.wildstang.wildrank.androidv2.adapters.TeamSummariesFragmentPagerAdapter;
import org.wildstang.wildrank.androidv2.data.DatabaseManager;
import org.wildstang.wildrank.androidv2.views.SlidingTabs;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TeamSummariesMainFragment extends Fragment {
    private ListView teamList;
    private ViewPager pager;
    private SlidingTabs tabs;

    private TeamListAdapter listAdapter;
    private TeamSummariesFragmentPagerAdapter pagerAdapter;

    private String selectedTeamKey;
    private int sortKey;

    private static final int NUMERICAL_SORT = 0;
    private static final int NUMERICAL_SORT_BACKWARDS = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_summaries_main, container, false);
        teamList = (ListView) view.findViewById(R.id.teams_list);
        pager = (ViewPager) view.findViewById(R.id.view_pager);
        tabs = (SlidingTabs) view.findViewById(R.id.tabs);
        sortKey = NUMERICAL_SORT;
        pager.setOffscreenPageLimit(10);
        teamList.setOnItemClickListener((parent, view1, position, id) -> {
            teamList.setItemChecked(position, true);
            QueryRow row = (QueryRow) parent.getItemAtPosition(position);
            onTeamSelected(row.getDocument());
        });

        pager.setAdapter(new TeamSummariesFragmentPagerAdapter(getFragmentManager()));
        tabs.setViewPager(pager);

        String team = PreferenceManager.getDefaultSharedPreferences(getActivity()).getString("assignedTeam", "red_1");

        if (team.contains("red")) {
            tabs.setBackgroundColor(getResources().getColor(R.color.material_red));
        } else {
            tabs.setBackgroundColor(getResources().getColor(R.color.material_blue));
        }
        return view;
    }


    @Override
    public void onResume() {
        super.onResume();

        try {
            runQuery();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getActivity(), "Error querying the team list. Check logcat!", Toast.LENGTH_LONG).show();
        }
    }

    private void runQuery() throws Exception {
        Query query = DatabaseManager.getInstance(getActivity()).getAllTeams();

        QueryEnumerator enumerator = query.run();

        Log.d("wildrank", "team query count: " + enumerator.getCount());
        System.out.println(enumerator.getRow(0).getDocument().getProperties().toString());
        List<QueryRow> queryRows = sort(enumerator, sortKey);


        Parcelable state = teamList.onSaveInstanceState();
        listAdapter = new TeamListAdapter(getActivity(), queryRows, false);
        teamList.setAdapter(listAdapter);
        teamList.onRestoreInstanceState(state);
    }

    private void onTeamSelected(Document doc) {
        loadInfoForTeam((String) doc.getProperty("key"));
    }

    private ArrayList<QueryRow> sort(QueryEnumerator enumerator, int sortMode) {

        QueryRow[] queryRows = new QueryRow[enumerator.getCount()];

        switch (sortMode) {
            case NUMERICAL_SORT:
                for (int i = 0; i < enumerator.getCount(); i++) {
                    queryRows[i] = enumerator.getRow(i);
                }
                break;

            case NUMERICAL_SORT_BACKWARDS:
                for (int i = 0; i < enumerator.getCount(); i++) {
                    queryRows[enumerator.getCount() - 1 - i] = enumerator.getRow(i);
                }
                break;



            default:
                for (int i = 0; i < enumerator.getCount(); i++) {
                    queryRows[i] = enumerator.getRow(i);
                }
                break;
        }




        ArrayList<QueryRow> q = new ArrayList<>();
        Integer[] midwestTeams = new Integer[]{81,93, 876, 1038,1091,1094,1625,1732,1736,1781,2022,2039,2040,
        2081,2169,2220,2358,2481,2704,2709,3352,3528,4021,4096,4143,4156,4212,4213,4296,4314,4645,4646,4655,
        4787,5442,5541,5822,5847};
        ArrayList<Integer> midwestTeamsList = new ArrayList<Integer>();
        for (int j = 0; j < midwestTeams.length; j++) {
            midwestTeamsList.add(midwestTeams[j]);
        }
        int numRemoved = 0;
        for (int i = 0; i < queryRows.length; i++) {
            q.add(queryRows[i]);
            //System.out.println(q.get(i - numRemoved).getDocument().getProperties().get("team_number"));
            System.out.println(q.toString());
            if (midwestTeamsList.contains(q.get(i - numRemoved).getDocument().getProperties().get("team_number"))) {

                q.remove(i - numRemoved);
                numRemoved++;
            }
        }


        return q;
    }


    private void loadInfoForTeam(String teamKey) {
        try {
            DatabaseManager db = DatabaseManager.getInstance(getActivity());
            Document teamDocument = db.getTeamFromKey(teamKey);
            Document pitDocument = db.getInternalDatabase().getExistingDocument("pit:" + teamKey);
            List<Document> matchDocuments = db.getMatchResultsForTeam(teamKey);
            ((TeamSummariesFragmentPagerAdapter) pager.getAdapter()).acceptNewTeamData(teamKey, teamDocument, pitDocument, matchDocuments);
        } catch (CouchbaseLiteException | IOException e) {
            e.printStackTrace();
            Toast.makeText(getActivity(), "Error loading data for team. Check LogCat.", Toast.LENGTH_LONG).show();
        }
    }
}
