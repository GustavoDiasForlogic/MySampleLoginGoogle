package net.forlogic.samplelogingoogle;

import android.app.Activity;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.plus.model.people.Person;

import java.util.List;

/**
 * Created by gustavo.dias on 07/06/2016.
 */
public class CircleFriendsAdapter extends RecyclerView.Adapter<CircleFriendsAdapter.VH> {

    private List<Person> mFriendsList;

    private Activity mActivity;

    private OnRecyclerViewItemClickListener mItemClickListener;

    private boolean mStopLoadImages = false;

    public CircleFriendsAdapter(Activity activity, List<Person> friendsList) {
        mActivity = activity;
        mFriendsList = friendsList;
    }

    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        mItemClickListener = listener;
    }

    public void stopLoadImages() {
        mStopLoadImages = true;
    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        return new VH(mActivity.getLayoutInflater().inflate(R.layout.circle_friends_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(VH holder, int position) {
        final Person person = mFriendsList.get(position);

        holder.mTxtDisplayName.setText(person.getDisplayName());

        final ImageView imgProfilePhoto = holder.mImgProfilePhoto;

        if (mStopLoadImages)
            return;
        new Thread() {
            @Override
            public void run() {
                if (mStopLoadImages)
                    return;
                String profilePhotoUrl = GoogleApiUtils.getProfilePhotoUrl(person.getImage().getUrl(), imgProfilePhoto.getWidth());
                final Bitmap profilePhoto = Utils.getBitmapFromURL(profilePhotoUrl);
                if (mStopLoadImages) {
                    if (profilePhoto != null)
                        profilePhoto.recycle();
                    return;
                }
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        imgProfilePhoto.setImageBitmap(profilePhoto);
                    }
                });
            }
        }.start();
    }

    public Person getItem(int position) {
        return mFriendsList.get(position);
    }

    @Override
    public int getItemCount() {
        return mFriendsList.size();
    }

    public class VH extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView mImgProfilePhoto;
        private TextView mTxtDisplayName;

        public VH(View itemView) {
            super(itemView);

            mImgProfilePhoto = (ImageView) itemView.findViewById(R.id.img_profile_photo);
            mTxtDisplayName = (TextView) itemView.findViewById(R.id.txv_display_name);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mItemClickListener != null)
                mItemClickListener.onItemClick(v, getAdapterPosition());
        }
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        stopLoadImages();
    }
}
