package org.wildstang.wildrank.androidv2.views.data;

import android.content.Context;
import android.util.AttributeSet;

import com.couchbase.lite.Document;

import org.wildstang.wildrank.androidv2.interfaces.IMatchDataView;

import java.util.List;
import java.util.Map;

/**
 * Created by Janine on 3/7/2016.
 */
public class MatchDataAverageRampartsCrossesView extends MatchDataView implements IMatchDataView {


    public MatchDataAverageRampartsCrossesView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void calculateFromDocuments(List<Document> documents) {
        if (documents == null) {
            return;
        } else if (documents.size() == 0) {
            return;
        }

//        Observable foulsObservable = Observable.from(documents)
//                .map(doc -> (Map<String, Object>) doc.getProperty("data"))
//                .map(data -> data.get("teleop-lowBar"))
//                .filter(fouls -> fouls != null)
//                .map(fouls -> (int) fouls);
//
//        MathObservable.averageInteger(foulsObservable)
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(sum -> setValueText("" + sum), error -> Log.d("wildrank", this.getClass().getName()));
        double rampartsCrosses = 0;
        double matches = 0;
        for (Document document: documents) {
            Map<String, Object> data = (Map<String, Object>) document.getProperty("data");
            if (data == null) {
                return;
            }
            int cross = (boolean) data.get("defense-ramparts") == true ? 1 : 0;
            matches += cross;//Number of Matches?
            rampartsCrosses += (int) data.get("teleop-ramparts");
        }

        if (rampartsCrosses == 0 && matches == 0) {
            setValueText("N/A");
        } else {
            double averageCrosses = (rampartsCrosses / (matches));
            setValueText(formatNumberAsString(averageCrosses));
        }

    }
}
