package cordova.plugin.mediacaptureplus;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView; 

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class QuestionRecyclerViewAdapter extends RecyclerView.Adapter<QuestionRecyclerViewAdapter.ViewHolder> {

    private List<String> questionData;
    private List<Integer> answerData;
    private LayoutInflater mInflater;

    QuestionRecyclerViewAdapter(Context context, ArrayList<String> questionData, ArrayList<Integer> answerData) {
        this.mInflater = LayoutInflater.from(context);
        this.questionData = questionData;
        this.answerData = answerData;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        String package_name = parent.getContext().getPackageName();
        Resources resources = parent.getContext().getResources();
        View view = mInflater.inflate(resources.getIdentifier("recview_question", "layout", package_name), parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView/Buttons in each row
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        String info = questionData.get(position);
        holder.questionText.setText(info);
    }

    @Override
    public int getItemCount() {
        return questionData.size();
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView questionText;

        ViewHolder(View itemView) {
            super(itemView);

            String package_name = itemView.getContext().getPackageName();
            Resources resources = itemView.getContext().getResources();
            questionText = itemView.findViewById(resources.getIdentifier("txt_question", "id", package_name));

        }
    }
}