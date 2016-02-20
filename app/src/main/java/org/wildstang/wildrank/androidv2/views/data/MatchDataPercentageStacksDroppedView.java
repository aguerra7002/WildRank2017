package org.wildstang.wildrank.androidv2.views.data;

import android.content.Context;
import android.util.AttributeSet;

import com.couchbase.lite.Document;

import org.wildstang.wildrank.androidv2.interfaces.IMatchDataView;
import org.wildstang.wildrank.androidv2.models.MatchModel;

import java.util.List;
import java.util.Map;

public class MatchDataPercentageStacksDroppedView extends MatchDataView implements IMatchDataView {

    public MatchDataPercentageStacksDroppedView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void calculateFromDocuments(List<Document> documents) {
        if (documents == null) {
            return;
        } else if (documents.size() == 0) {
            return;
        }

        double totalHighGoalMisses = 0;
        double totalhighGoalCount = 0;
        for (Document document : documents) {
            Map<String, Object> data = (Map<String, Object>) document.getProperty("data");
            if (data == null) {
                return;
            }
            List<Map<String, Object>> stacks = (List<Map<String, Object>>) data.get("matches");
            for (Map<String, Object> stack : stacks) {
                totalhighGoalCount += (int) stack.get(MatchModel.HIGH_GOAL_COUNT_KEY);
                totalHighGoalMisses += (int) stack.get(MatchModel.HIGH_GOAL_MISSED_KEY);

            }
        }
        if (totalhighGoalCount == 0) {
            setValueText("N/A");
        } else {
            double percentage = (totalhighGoalCount / (totalHighGoalMisses + totalhighGoalCount));
            setValueText(formatPercentageAsString(percentage));
        }
    }
}
