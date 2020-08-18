package cordova.plugin.mediacaptureplus;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class LightboxRecyclerViewAdapter extends RecyclerView.Adapter<LightboxRecyclerViewAdapter.ViewHolder> {

    private List<Bitmap> lightboxImages;
    private LayoutInflater mInflater;

    LightboxRecyclerViewAdapter(Context context, ArrayList<Bitmap> lightboxImages) {
        this.mInflater = LayoutInflater.from(context);
        this.lightboxImages = lightboxImages;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        String package_name = parent.getContext().getPackageName();
        Resources resources = parent.getContext().getResources();
        View view = mInflater.inflate(resources.getIdentifier("recview_lightbox", "layout", package_name), parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView/Buttons in each row
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Bitmap image = lightboxImages.get(position);
        holder.lightboxImage.setImageBitmap(image);
    }

    @Override
    public int getItemCount() {
        return lightboxImages.size();
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView lightboxImage;

        ViewHolder(View itemView) {
            super(itemView);

            String package_name = itemView.getContext().getPackageName();
            Resources resources = itemView.getContext().getResources();
            lightboxImage = itemView.findViewById(resources.getIdentifier("img_lightbox", "id", package_name));

        }
    }
}