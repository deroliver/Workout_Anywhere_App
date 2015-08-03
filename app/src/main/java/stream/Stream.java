package stream;

import android.app.ListActivity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.practice.derikpc.workoutanywhere.R;

import java.util.ArrayList;


public class Stream extends ListActivity {

    private ArrayList<StreamObject> streamObjects;
    private MyListAdapter myListAdapter;

    StreamObject Derik;
    StreamObject Leslie;
    StreamObject Corey;

    View context;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_stream);

        initializeObjects();
        insertPost(Derik);
        insertPost(Leslie);
        insertPost(Corey);


       // Button comments = (Button) findViewById(R.id.see_comments_button);
       // comments.setOnClickListener(seeCommentsListener);

        this.myListAdapter = new MyListAdapter(this, streamObjects);
        setListAdapter(myListAdapter);

    }

    public void initializeObjects() {
        streamObjects = new ArrayList<StreamObject>();
        Derik = new StreamObject();
        Leslie = new StreamObject();
        Corey = new StreamObject();

        ImageView imageView = new ImageView(getApplication());

        Derik.setName("Derik Oliver");
        Derik.setUpdate("I just worked out. Today was leg day... Glad that it's over!");
        Derik.setnLikes(1);
        Derik.setnComments(1);
       // Derik.setPicture(getResources().getDrawable(R.drawable.my_profile_pic_test));

        Leslie.setName("Andrew Rollins");
        Leslie.setUpdate("I just worked out. Today was leg day... Glad that it's over");
        Leslie.setnLikes(1);
        Leslie.setnComments(1);
        //Leslie.setPicture(getResources().getDrawable(R.drawable.andrew_profile_pic));

        Corey.setName("Dylan Sicklesteel");
        Corey.setUpdate("I just worked out. Today was leg day... Glad that it's over");
        Corey.setnLikes(1);
        Corey.setnComments(1);
       // Corey.setPicture(getResources().getDrawable(R.drawable.icon_profile));
    }

    public void insertPost(StreamObject update) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View newPostRow = inflater.inflate(R.layout.stream_object, null);

        ImageButton like = (ImageButton) newPostRow.findViewById(R.id.like_post_button);
        like.setOnClickListener(likePostListener);

        streamObjects.add(update);
    }


    private static class MyListAdapter extends ArrayAdapter<StreamObject> {

        private Context context;
        private ArrayList<StreamObject> allStreamObjects;

        private LayoutInflater mInflater;
        private boolean mNotifyOnChange = true;


        public MyListAdapter(Context context, ArrayList<StreamObject> mStreamObjects) {
            super(context, R.layout.stream_object);
            this.context = context;
            this.allStreamObjects = new ArrayList<StreamObject>(mStreamObjects);
            this.mInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return allStreamObjects.size();
        }

        @Override
        public StreamObject getItem(int position) {
            return allStreamObjects.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getPosition(StreamObject item) {
            return allStreamObjects.indexOf(item);
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public int getItemViewType(int position) {
            return 1;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final ViewHolder holder;
            int type = getItemViewType(position);

            if(convertView == null) {
                convertView = mInflater.inflate(R.layout.stream_object, parent, false);
                holder = new ViewHolder();
                holder.personName = (TextView) convertView.findViewById(R.id.stream_object_name);
                holder.personPicture = ((ImageView) convertView.findViewById(R.id.stream_object_picture));
                holder.statusUpdate = (TextView) convertView.findViewById(R.id.stream_object_update_text);
                holder.nLikes = (TextView) convertView.findViewById(R.id.stream_object_nlikes);
                holder.nComments = (TextView) convertView.findViewById(R.id.stream_object_nlikes);
                holder.likeButton = (ImageButton) convertView.findViewById(R.id.like_post_button);
                holder.seeCommentsButton = (ImageButton) convertView.findViewById(R.id.see_comments_button);
               // holder.likeButton.setOnClickListener(new CustomOnClickListener(position));
                convertView.setTag(holder);

            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.personName.setText(allStreamObjects.get(position).getName());
            holder.statusUpdate.setText(allStreamObjects.get(position).getUpdate());
            holder.personPicture.setImageDrawable(allStreamObjects.get(position).getPicture());
            holder.nLikes.setText((allStreamObjects.get(position).getnLikes()).toString());
            holder.nComments.setText((allStreamObjects.get(position).getnComments()).toString());
            holder.pos = position;
            return convertView;
        }

    }

    static class ViewHolder {
        TextView personName;
        ImageView personPicture;
        TextView statusUpdate;
        TextView nLikes;
        TextView nComments;
        ImageButton likeButton;
        ImageButton seeCommentsButton;
        int pos;
    }


    public OnClickListener likePostListener = new OnClickListener() {
        @Override
        public void onClick(View v) {


            //Integer nLikes = Integer.parseInt(nLikesTextView.getText().toString());
            //nLikes++;
        }
    };

    private class CustomOnClickListener implements OnClickListener {
        private int pos;

        public CustomOnClickListener(int pos) {
            this.pos = pos;
        }

        @Override
        public void onClick(View v) {
            Toast.makeText(getApplication(), "You Clicked Like", Toast.LENGTH_LONG).show();

            int nLikes = streamObjects.get(pos).getnLikes();
            streamObjects.get(pos).setnLikes(12);
           // MyListAdapter mAdapter = (MyListAdapter)
            //myListAdapter.notifyDataSetChanged();

        }
    }
}








































