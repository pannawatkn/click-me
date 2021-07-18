package panphajed.ssru.clickme.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import panphajed.ssru.clickme.Class.Score;
import panphajed.ssru.clickme.R;

public class ScoreAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater inflater;
    private List<Score> scores;

    public ScoreAdapter(Context context, List<Score> scores) {
        this.mContext = context;
        this.scores = scores;

    }

    @Override
    public int getCount() {
        return scores.size();
    }

    @Override
    public Object getItem(int location) {
        return scores.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View scoreView, ViewGroup parent) {
        ViewHolder holder;
        if (inflater == null) {
            inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        if (scoreView == null) {

            scoreView = inflater.inflate(R.layout.row_scoreboard, parent, false);
            holder = new ViewHolder();
            holder.rank = scoreView.findViewById(R.id.rankRow);
            holder.name = scoreView.findViewById(R.id.nameRow);
            holder.score = scoreView.findViewById(R.id.scoreAndMaxComboRow);

            scoreView.setTag(holder);

        }
        else {
            holder = (ViewHolder) scoreView.getTag();
        }

        final Score mScore = scores.get(position);
        holder.rank.setText(mScore.getRank());
        holder.name.setText(mScore.getName());
        holder.score.setText(mScore.getScore());

        return scoreView;
    }

    public static class ViewHolder {

        TextView rank;
        TextView name;
        TextView score;

    }
}
