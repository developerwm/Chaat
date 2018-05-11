package chatboot.obrabo.com.chatboot.ui.adapter;

import android.app.Activity;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.adroitandroid.chipcloud.ChipCloud;
import com.adroitandroid.chipcloud.ChipListener;
import com.squareup.picasso.Picasso;
import java.util.List;
import chatboot.obrabo.com.chatboot.MainActivity;
import chatboot.obrabo.com.chatboot.R;
import chatboot.obrabo.com.chatboot.model.CommentsModel;
import chatboot.obrabo.com.chatboot.util.TypewriterView;
import de.hdodenhof.circleimageview.CircleImageView;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.ViewHolder> {

    private List<CommentsModel> items;
    private Activity activity;
    public static final int BOOT = 1;
    public static final int BOOT_SELECT = 3;
    public static final int USER = 2;
    public static final int SPEAKER_EVALUATION = 3;
    public static final int OPEN_TEXT = 4;
    private  String[] options =  {
            "Potholes",
            "Graffiti",
            "Noise",
            "Traffic",
            "Crime"
    };

    public static class ViewHolder extends RecyclerView.ViewHolder {
        //public TextView txtComment, txtLike, txtCountLike;
        public TypewriterView txtComment;
        public TextView txtCommentUser;
        public CircleImageView imgPhoto;
        public ImageView imgLike;
        private ChipCloud chip_cloud;
        public ImageView picture;

        public ViewHolder(View v) {
            super(v);

            txtComment = (TypewriterView) v.findViewById(R.id.txtComment);
            imgPhoto = (CircleImageView) v.findViewById(R.id.imgPhoto);
            chip_cloud =  (ChipCloud) v.findViewById(R.id.chip_cloud);
            picture = (ImageView) v.findViewById(R.id.picture);
        }
    }

    public CommentsAdapter(List<CommentsModel> items, Activity activity) {
        this.items = items;
        this.activity = activity;
    }

    @Override
    public CommentsAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        //View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_comments, viewGroup, false);
        View v;

       // v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_question, viewGroup, false);
        if (i == 1) {
            v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_comments, viewGroup, false);
            return new ViewHolder(v);
        } else if (i == 2) {
            v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_user, viewGroup, false);
            return new ViewHolder(v);
        } else if (i == 3) {
            v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_boot_select, viewGroup, false);
            return new ViewHolder(v);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(final CommentsAdapter.ViewHolder viewHolder, final int i) {
        viewHolder.setIsRecyclable(true);

        if(items.get(i).getTypeUser().equals("user")){
            viewHolder.txtComment.setText("");
            viewHolder.txtComment.setCharacterDelay(0);
            viewHolder.txtComment.displayTextWithAnimation(items.get(i).getMessage());
        } else {
            viewHolder.txtComment.setText("");
            viewHolder.txtComment.setCharacterDelay(50);
            viewHolder.txtComment.displayTextWithAnimation(items.get(i).getMessage());
        }

        switch (items.get(i).getTypeUser()){
            case "boot_select":
                viewHolder.picture.setVisibility(View.GONE);
                new ChipCloud.Configure()
                        .chipCloud(viewHolder.chip_cloud)
                        .labels(options)
                        .mode(ChipCloud.Mode.SINGLE)
                        .allCaps(false)
                        .gravity(ChipCloud.Gravity.CENTER)
                        .chipListener(new ChipListener() {
                            @Override
                            public void chipSelected(int index) {
                                ((MainActivity)activity).setValueMessage(options[index].toLowerCase());
                            }
                            @Override
                            public void chipDeselected(int index) {
                                //...
                            }
                        })
                        .build();
                break;
            case "boot_image":
                viewHolder.chip_cloud.setVisibility(View.GONE);
                viewHolder.picture.setVisibility(View.VISIBLE);
                if(items.get(i).getImage()!= null) {
                    Picasso.with(activity).load(items.get(i).getImage()).into(viewHolder.picture);
                }
                viewHolder.picture.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    ((MainActivity)activity).selectImage(i);
                  }
               });
                viewHolder.txtComment.setVisibility(View.GONE);

                break;
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void addComment(CommentsModel commentsModel, RecyclerView recyclerView){
        int position = items.size();
        items.add(position, commentsModel);
        notifyItemInserted(position);
        recyclerView.scrollToPosition(items.size() - 1);
    }

    public void updateImage(CommentsModel commentsModel, RecyclerView recyclerView, int position,
                            String value){
        items.set(position, commentsModel);
        notifyItemChanged(position);
        recyclerView.scrollToPosition(items.size() - 1);

        CommentsModel commentsModelAdd = new CommentsModel();

        int position2 = items.size();
        commentsModelAdd.setTypeUser("boot");
        commentsModelAdd.setMessage(value);
        items.add(position2, commentsModelAdd);
        notifyItemInserted(position2);
        recyclerView.scrollToPosition(items.size() - 1);
    }

    @Override
    public int getItemViewType(int position) {
        CommentsModel commentsModel = items.get(position);
        if (commentsModel.getTypeUser().equals("boot")) {
            return BOOT;
        } else if (commentsModel.getTypeUser().equals("user")) {
            return USER;
        } else if (commentsModel.getTypeUser().equals("boot_select")) {
           return BOOT_SELECT;
        } else if (commentsModel.getTypeUser().equals("boot_image")) {
            return BOOT_SELECT;
        } else
            return BOOT;
       }
}