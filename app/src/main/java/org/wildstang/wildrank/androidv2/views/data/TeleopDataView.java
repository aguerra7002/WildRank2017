package org.wildstang.wildrank.androidv2.views.data;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.couchbase.lite.Document;

import org.wildstang.wildrank.androidv2.R;
import org.wildstang.wildrank.androidv2.models.MatchModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class TeleopDataView extends View {
    private List<Document> matchDocs;

    Paint textPaint, boulderPaint, scaledPaint, scoredPaint, notScoredPaint, outlinePaint, smallTextPaint, idlePaint;

    Bitmap lowBar;
    Bitmap portcullis;
    Bitmap moat;
    Bitmap ramparts;
    Bitmap drawBridge;
    Bitmap sallyPort;
    Bitmap roughTerrain;
    Bitmap rockWall;
    Bitmap quadRamp;


    public TeleopDataView(Context context, AttributeSet attrs) {
        super(context, attrs);
        textPaint = new Paint();
        textPaint.setTextSize(25);
        textPaint.setColor(Color.BLACK);
        smallTextPaint = new Paint();
        smallTextPaint.setTextSize(15);
        textPaint.setColor(Color.BLACK);
        boulderPaint = new Paint();
        boulderPaint.setColor(Color.DKGRAY);
        outlinePaint = new Paint();
        outlinePaint.setColor(Color.BLACK);
        outlinePaint.setStyle(Paint.Style.STROKE);
        scoredPaint = new Paint();
        scoredPaint.setColor(Color.argb(150, 0, 255, 0));
        notScoredPaint = new Paint();
        notScoredPaint.setColor(Color.argb(150, 255, 0, 0));
        idlePaint = new Paint();
        idlePaint.setColor(Color.argb(150, 0, 0, 255));
        lowBar = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.lowbar);
        portcullis = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.portcullis);
        moat = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.moat);
        ramparts = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.ramparts);
        drawBridge = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.drawbridge);
        sallyPort = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.sallyport);
        roughTerrain = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.roughterrain);
        rockWall = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.rockwall);
        quadRamp = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.quadramp);
    }

    public void acceptNewTeamData(List<Document> matchDocs) {
        if (matchDocs == null || matchDocs.isEmpty()) {
            invalidate();
            return;
        }
        // Sorts the matches by match number
        Collections.sort(matchDocs, new MatchDocumentComparator());

        this.matchDocs = matchDocs;

        invalidate();
    }

    @Override
    public void onDraw(Canvas c) { // code for drawing everything
        if (matchDocs == null || matchDocs.isEmpty()) {
            c.drawText("No data exists for this team.", 100, 100, textPaint);
        } else {
            int matchNum = 0;
            int x;
            c.drawText("H: ", 5, 60, textPaint);
            c.drawText("L: ", 5, 160, textPaint);
            for (Document doc : matchDocs) {
                Map<String, Object> data = (Map<String, Object>) doc.getProperty("data");
                matchNum++;
                x = (getWidth() - 25) * matchNum / matchDocs.size();
                if (matchDocs.size() > 9) {
                    c.drawCircle((float) scaledAt(.5, x, matchNum) + 25, 50, 25, boulderPaint);
                    c.drawCircle((float) scaledAt(.5, x, matchNum) + 25, 50, 25, outlinePaint);
                    Integer highGoalMade = (Integer) data.get("teleop-highGoalMade");
                    Integer highGoalMissed = (Integer) data.get("teleop-highGoalMissed");
                    if (highGoalMade == 0 && highGoalMissed == 0) {
                        c.drawText("N/A", (float) scaledAt(.5, x, matchNum) + 10, 56, textPaint);
                    } else {
                        double angle = (highGoalMissed * 360) / (highGoalMade + highGoalMissed);
                        c.drawArc(new RectF((float) scaledAt(.5, x, matchNum), 25, (float) scaledAt(.5, x, matchNum) + 50, 75), (float) (angle / 2), (float) (angle * -1), true, notScoredPaint);
                        c.drawArc(new RectF((float) scaledAt(.5, x, matchNum), 25, (float) scaledAt(.5, x, matchNum) + 50, 75), (float) (angle / 2), (float) (360 - (angle)), true, scoredPaint);
                        c.drawText(highGoalMade.toString(), (float) scaledAt(.5, x, matchNum) - 12, 57, smallTextPaint);
                        c.drawText(highGoalMissed.toString(), (float) scaledAt(.5, x, matchNum) + 52, 57, smallTextPaint);
                    }

                    c.drawCircle((float) scaledAt(.5, x, matchNum) + 25, 150, 25, boulderPaint);
                    c.drawCircle((float) scaledAt(.5, x, matchNum) + 25, 150, 25, outlinePaint);
                    Integer lowGoalMade = (Integer) data.get("teleop-lowGoalMade");
                    Integer lowGoalMissed = (Integer) data.get("teleop-lowGoalMissed");
                    if (lowGoalMade == 0 && lowGoalMissed == 0) {
                        c.drawText("N/A", (float) scaledAt(.5, x, matchNum) + 10, 156, smallTextPaint);
                    } else {
                        double angle2 = (lowGoalMissed * 360) / (lowGoalMade + lowGoalMissed);
                        c.drawArc(new RectF((float) scaledAt(.5, x, matchNum), 125, (float) scaledAt(.5, x, matchNum) + 50, 175), (float) (angle2 / 2), (float) (angle2 * -1), true, notScoredPaint);
                        c.drawArc(new RectF((float) scaledAt(.5, x, matchNum), 125, (float) scaledAt(.5, x, matchNum) + 50, 175), (float) (angle2 / 2), (float) (360 - (angle2)), true, scoredPaint);
                        c.drawText(lowGoalMade.toString(), (float) scaledAt(.5, x, matchNum) - 12, 157, smallTextPaint);
                        c.drawText(lowGoalMissed.toString(), (float) scaledAt(.5, x, matchNum) + 52, 157, smallTextPaint);
                    }
                } else {
                    c.drawCircle((float) scaledAt(.5, x, matchNum) + 25, 50, 30, boulderPaint);
                    c.drawCircle((float) scaledAt(.5, x, matchNum) + 25, 50, 30, outlinePaint);
                    Integer highGoalMade = (Integer) data.get("teleop-highGoalMade");
                    Integer highGoalMissed = (Integer) data.get("teleop-highGoalMissed");
                    if (highGoalMade == 0 && highGoalMissed == 0) {
                        c.drawText("N/A", (float) scaledAt(.5, x, matchNum) + 5, 60, textPaint);
                    } else {
                        double angle = (highGoalMissed * 360) / (highGoalMade + highGoalMissed);
                        c.drawArc(new RectF((float) scaledAt(.5, x, matchNum) - 5, 20, (float) scaledAt(.5, x, matchNum) + 55, 80), (float) (angle / 2), (float) (angle * -1), true, notScoredPaint);
                        c.drawArc(new RectF((float) scaledAt(.5, x, matchNum) - 5, 20, (float) scaledAt(.5, x, matchNum) + 55, 80), (float) (angle / 2), (float) (360 - (angle)), true, scoredPaint);
                        c.drawText(highGoalMade.toString(), (float) scaledAt(.5, x, matchNum) - 21, 60, textPaint);
                        c.drawText(highGoalMissed.toString(), (float) scaledAt(.5, x, matchNum) + 58, 60, textPaint);
                    }

                    c.drawCircle((float) scaledAt(.5, x, matchNum) + 25, 150, 30, boulderPaint);
                    c.drawCircle((float) scaledAt(.5, x, matchNum) + 25, 150, 30, outlinePaint);
                    Integer lowGoalMade = (Integer) data.get("teleop-lowGoalMade");
                    Integer lowGoalMissed = (Integer) data.get("teleop-lowGoalMissed");
                    if (lowGoalMade == 0 && lowGoalMissed == 0) {
                        c.drawText("N/A", (float) scaledAt(.5, x, matchNum) + 5, 160, textPaint);
                    } else {
                        double angle2 = (lowGoalMissed * 360) / (lowGoalMade + lowGoalMissed);
                        c.drawArc(new RectF((float) scaledAt(.5, x, matchNum) - 5, 120, (float) scaledAt(.5, x, matchNum) + 55, 180), (float) (angle2 / 2), (float) (angle2 * -1), true, notScoredPaint);
                        c.drawArc(new RectF((float) scaledAt(.5, x, matchNum) - 5, 120, (float) scaledAt(.5, x, matchNum) + 55, 180), (float) (angle2 / 2), (float) (360 - (angle2)), true, scoredPaint);
                        c.drawText(lowGoalMade.toString(), (float) scaledAt(.5, x, matchNum) - 21, 160, textPaint);
                        c.drawText(lowGoalMissed.toString(), (float) scaledAt(.5, x, matchNum) + 58, 160, textPaint);
                    }
                }

                Integer lowBarCrosses = (Integer) data.get("teleop-lowBar");
                int currentHeight = 300;

                c.drawBitmap(lowBar, new Rect(0,0, lowBar.getWidth(), lowBar.getHeight()), new Rect((int) scaledAt(.5, x, matchNum), 260, 50, 50), scoredPaint);
                c.drawText("LB: " + lowBarCrosses.toString(), (float) scaledAt(.5, x, matchNum), 260, textPaint);
                if ((boolean) data.get("defense-portcullis")) {
                    Integer PCCrosses = (Integer) data.get("teleop-portcullis");
                    c.drawText("PC: " + PCCrosses.toString(), (float) scaledAt(.5, x, matchNum), currentHeight, textPaint);
                    currentHeight += 50;
                }
                if ((boolean) data.get("defense-quadRamp")) {
                    Integer QRCrosses = (Integer) data.get("teleop-quadRamp");
                    c.drawText("QR: " + QRCrosses.toString(), (float) scaledAt(.5, x, matchNum), currentHeight, textPaint);
                    currentHeight += 50;
                }
                if ((boolean) data.get("defense-moat")) {
                    Integer MOCrosses = (Integer) data.get("teleop-moat");
                    c.drawText("MO: " + MOCrosses.toString(), (float) scaledAt(.5, x, matchNum), currentHeight, textPaint);
                    currentHeight += 50;
                }
                if ((boolean) data.get("defense-ramparts")) {
                    Integer RPCrosses = (Integer) data.get("teleop-ramparts");
                    c.drawText("RP: " + RPCrosses.toString(), (float) scaledAt(.5, x, matchNum), currentHeight, textPaint);
                    currentHeight += 50;
                }
                if ((boolean) data.get("defense-drawbridge")) {
                    Integer DRCrosses = (Integer) data.get("teleop-drawbridge");
                    c.drawText("DR: " + DRCrosses.toString(), (float) scaledAt(.5, x, matchNum), currentHeight, textPaint);
                    currentHeight += 50;
                }
                if ((boolean) data.get("defense-sallyport")) {
                    Integer SLCrosses = (Integer) data.get("teleop-sallyport");
                    c.drawText("SL: " + SLCrosses.toString(), (float) scaledAt(.5, x, matchNum), currentHeight, textPaint);
                    currentHeight += 50;
                }
                if ((boolean) data.get("defense-roughTerrain")) {
                    Integer RTCrosses = (Integer) data.get("teleop-roughTerrain");
                    c.drawText("RT: " + RTCrosses.toString(), (float) scaledAt(.5, x, matchNum), currentHeight, textPaint);
                    currentHeight += 50;
                }
                if ((boolean) data.get("defense-rockWall")) {
                    Integer RWCrosses = (Integer) data.get("teleop-rockWall");
                    c.drawText("RW: " + RWCrosses.toString(), (float) scaledAt(.5, x, matchNum), currentHeight, textPaint);
                    currentHeight += 50;
                }

                c.drawText("Challenged", (float) scaledAt(.5, x, matchNum) - 10, 510, smallTextPaint);
                c.drawText("Scaled", (float) scaledAt(.5, x, matchNum) + 2, 550, smallTextPaint);
                if ((boolean) data.get("teleop-scaleSuccessful")) {
                    c.drawRect((float) scaledAt(0, x, matchNum) + 25, 535, (float) scaledAt(1, x, matchNum) + 25, 555, scoredPaint);
                    c.drawRect((float) scaledAt(0, x, matchNum) + 25, 495, (float) scaledAt(1, x, matchNum) + 25, 515, idlePaint);
                } else if ((boolean) data.get("teleop-challenged")) {
                    c.drawRect((float) scaledAt(0, x, matchNum) + 25, 495, (float) scaledAt(1, x, matchNum) + 25, 515, scoredPaint);
                    if ((boolean) data.get("teleop-attemptedScale")) {
                        c.drawRect((float) scaledAt(0, x, matchNum) + 25, 535, (float) scaledAt(1, x, matchNum) + 25, 555, notScoredPaint);
                    } else {
                        c.drawRect((float) scaledAt(0, x, matchNum) + 25, 535, (float) scaledAt(1, x, matchNum) + 25, 555, idlePaint);
                    }
                } else {
                    if ((boolean) data.get("teleop-attemptedScale")) {
                        c.drawRect((float) scaledAt(0, x, matchNum) + 25, 535, (float) scaledAt(1, x, matchNum) + 25, 555, notScoredPaint);
                        c.drawRect((float) scaledAt(0, x, matchNum) + 25, 495, (float) scaledAt(1, x, matchNum) + 25, 515, notScoredPaint);
                    } else {
                        c.drawRect((float) scaledAt(0, x, matchNum) + 25, 535, (float) scaledAt(1, x, matchNum) + 25, 555, idlePaint);
                        c.drawRect((float) scaledAt(0, x, matchNum) + 25, 495, (float) scaledAt(1, x, matchNum) + 25, 515, notScoredPaint);
                    }
                }

                // Draw line to separate matches

                c.drawLine((getWidth() - 25) * matchNum / matchDocs.size() + 25, 0, (getWidth() - 25) * matchNum / matchDocs.size() + 25, getHeight(), outlinePaint);
            }
        }
    }

    private double scaledAt(double percent, int totalWidth, int matchNum) {
        double oneWidth = (double) totalWidth / (double) matchNum;
        return totalWidth - oneWidth + (oneWidth * percent);
    }

    class MatchDocumentComparator implements Comparator<Document> {

        @Override
        public int compare(Document lhs, Document rhs) {
            try {
                int lhsMatchNumber = Integer.parseInt(((String) lhs.getProperty("match_key")).replaceAll("[0-9]+[a-zA-Z]+_[a-zA-Z]+", ""));
                int rhsMatchNumber = Integer.parseInt(((String) rhs.getProperty("match_key")).replaceAll("[0-9]+[a-zA-Z]+_[a-zA-Z]+", ""));
                Log.d("wildrank", "lhs: " + lhsMatchNumber + ", rhs: " + rhsMatchNumber);
                return (lhsMatchNumber - rhsMatchNumber);
            } catch (Exception e) {
                e.printStackTrace();
                return 0;
            }
        }
    }
}
