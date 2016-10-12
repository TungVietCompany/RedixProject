package redix.booxtown.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;

import redix.booxtown.R;
import redix.booxtown.activity.HomeActivity;
import redix.booxtown.activity.MainAllActivity;
import redix.booxtown.adapter.AdapterInteractThreadDetails;
import redix.booxtown.controller.CommentController;
import redix.booxtown.controller.Information;
import redix.booxtown.controller.NotificationController;
import redix.booxtown.controller.ObjectCommon;
import redix.booxtown.controller.ThreadController;
import redix.booxtown.controller.UserController;
import redix.booxtown.custom.MenuBottomCustom;
import redix.booxtown.model.Comment;
import redix.booxtown.model.CommentBook;
import redix.booxtown.model.Notification;
import redix.booxtown.model.Thread;
import redix.booxtown.model.Topic;
import redix.booxtown.model.User;

/**
 * Created by Administrator on 28/08/2016.
 */
public class InteractThreadDetailsFragment extends Fragment
{
    RecyclerView recyclerView;
    LinearLayoutManager linearLayoutManager;
    List<Comment> arr_commet = new ArrayList<>();
    boolean loading = true,
            isLoading = true;
    private int previousTotal = 0;
    private int visibleThreshold = 5;

    Thread threads;
    Topic topic;
    TextView txt_title,txt_count_thread,txt_author_thread,txt_title_thread,txt_content_thread;
    String type_fragment,session_id;
    AdapterInteractThreadDetails adapter;
    List<String> listUser= new ArrayList<>();
    EditText edit_message;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.interact_thread_detail_fragment, container, false);
        init(view);
        txt_title.setText("Interact");
        ImageView imageView_back=(ImageView) getActivity().findViewById(R.id.img_menu);
        imageView_back.setImageResource(R.drawable.btn_sign_in_back);
        imageView_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(topic !=null) {
                    if (type_fragment.equals("NotificationFragment")){
                        HomeActivity homeActivity = (HomeActivity) getActivity();
                        homeActivity.getTxtTitle().setText("Notifications");
                        homeActivity.callFragment(new NotificationFragment());
                    }else {
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("thread", topic);
                        ThreadFragment fragment = new ThreadFragment();
                        fragment.setArguments(bundle);
                        callFragment(fragment);
                    }
                }
                else {
                    HomeActivity homeActivity = (HomeActivity) getActivity();
                    homeActivity.getTxtTitle().setText("Notifications");
                    homeActivity.callFragment(new NotificationFragment());
                }
            }
        });
        //----------------------------------------------
        threads = (Thread) getArguments().getSerializable("thread");
        topic = (Topic) getArguments().getSerializable("interact");
        type_fragment = getArguments().getString("type_fragment");
        //--------------------------------------------------

        txt_author_thread=(TextView) view.findViewById(R.id.txt_author_interact_thread_detail);
        txt_title_thread.setText(threads.getTitle().substring(0,1).toUpperCase()+threads.getTitle().substring(1,threads.getTitle().length()) +"");
        txt_content_thread.setText(threads.getDescription());
        txt_author_thread.setText("Added by "+threads.getUsername());
        txt_count_thread.setText("("+threads.getNum_comment()+")");
        //-----------------------------------------------------------

        SharedPreferences pref = getContext().getSharedPreferences("MyPref", Context.MODE_PRIVATE);
        session_id = pref.getString("session_id", null);
        edit_message = (EditText)view.findViewById(R.id.edit_message);
        final ImageView btn_send_comment_interact = (ImageView)view.findViewById(R.id.btn_send_comment_interact);
            btn_send_comment_interact.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(!edit_message.getText().toString().trim().equals("")) {
                        insertComment insertComment1 = new insertComment(getContext());
                        insertComment1.execute(session_id, edit_message.getText().toString(), threads.getId());
                        edit_message.setText("");
                        arr_commet.clear();
                        commentAsync getcomment = new commentAsync(getContext(),threads.getId(),15,0);
                        getcomment.execute();
//                        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//                        Date date = new Date();
//                        System.out.println(dateFormat.format(date));
//                        Comment comment = new Comment("",
//                                edit_message.getText().toString(),dateFormat.format(date),threads.getId(),threads.getUser_id()
//                        ,threads.getUsername());
                    }
                }
            });
        //---------------------------------------------------------------
        populatRecyclerView(threads.getId());
        implementScrollListener(threads.getId());
        return view;
    }
    public void callFragment(Fragment fragment ){
        FragmentManager manager = getActivity().getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        //Khi được goi, fragment truyền vào sẽ thay thế vào vị trí FrameLayout trong Activity chính
        transaction.replace(R.id.frame_main_all, fragment);
        transaction.commit();
    }

    public void init(View view){
        recyclerView = (RecyclerView)view.findViewById(R.id.rv_comment_thread);
        linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);

        txt_title=(TextView) getActivity().findViewById(R.id.txt_title);
        txt_title_thread =(TextView) view.findViewById(R.id.txt_title_thread_detail);
        txt_count_thread=(TextView) view.findViewById(R.id.txt_count_thread_detail);
        txt_content_thread =(TextView) view.findViewById(R.id.txt_contern_thread_details);
    }

    private void populatRecyclerView(String thread_id) {
        commentAsync getcomment = new commentAsync(getContext(),thread_id,15,0);
        getcomment.execute();
        if(arr_commet.size() == 0) {
            adapter = new AdapterInteractThreadDetails(getContext(), arr_commet);
            recyclerView.setAdapter(adapter);
        }else{
            adapter.notifyDataSetChanged();
        }
    }

    private void implementScrollListener(final String thread_id) {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int visibleItemCount = recyclerView.getChildCount();
                int totalItemCount = linearLayoutManager.getItemCount();
                int firstVisibleItem = linearLayoutManager.findFirstVisibleItemPosition();
                if (loading) {
                    if (totalItemCount > previousTotal) {
                        loading = false;
                        previousTotal = totalItemCount;
                    }
                }
                if (!loading && (totalItemCount - visibleItemCount)
                        <= (firstVisibleItem + visibleThreshold) && isLoading) {
                    // End has been reached
                    Comment commentBook= arr_commet.get(arr_commet.size()-1);
                    commentAsync getcomment = new commentAsync(getContext(),thread_id,15,Integer.parseInt(commentBook.getId()));
                    getcomment.execute();
                    // Do something
                    loading = true;
                }
            }
        });
    }

    class commentAsync extends AsyncTask<String,Void,List<Comment>>{

        Context context;
        ProgressDialog dialog;
        String thread_id;
        int top,from;
        public commentAsync(Context context,String thread_id,int top,int from){
            this.context = context;
            this.thread_id = thread_id;
            this.top = top;
            this.from =from;
        }
        @Override
        protected void onPreExecute() {
            dialog = new ProgressDialog(context);
            dialog.setMessage(Information.noti_dialog);
            dialog.show();
        }

        @Override
        protected List<Comment> doInBackground(String... strings) {
            CommentController commentController = new CommentController();
            return commentController.getTopComment(thread_id,top,from);
        }

        @Override
        protected void onPostExecute(List<Comment> comments) {
            try {
                if(comments.size() >0){
                    arr_commet.addAll(comments);
                    adapter.notifyDataSetChanged();
                    if(!listUser.contains(threads.getUser_id())) {
                        listUser.add(threads.getUser_id());
                    }
                    for(int i=0; i< comments.size(); i++){
                        if(!listUser.contains(comments.get(i).getUser_id())){
                            listUser.add(comments.get(i).getUser_id());
                        }
                    }
                    dialog.dismiss();
                    isLoading = true;
                }else {
                    isLoading = false;
                    dialog.dismiss();
                }
            }catch (Exception e){
                dialog.dismiss();
            }
        }
    }
    public class ThreadSync extends AsyncTask<Void,Void,Boolean> {
        ProgressDialog dialog;
        Context context;
        String session_id;
        int thread_id;
        public ThreadSync(Context context,String session_id,int thread_id){
            this.context = context;
            this.session_id=session_id;
            this.thread_id=thread_id;

        }

        @Override
        protected void onPreExecute() {
            dialog = new ProgressDialog(context);
            dialog.setMessage("Please wait...");
            dialog.setIndeterminate(true);
            dialog.show();
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            ThreadController topicController = new ThreadController();
            return topicController.changeStatusUnreadThread(session_id,thread_id);
        }

        @Override
        protected void onPostExecute(final Boolean topics) {
            try{
                if(topics){
                    //RecyclerViewHolder holder=  new RecyclerViewHolder(itemView);
                    // holder.txt_count_interact.setTextColor(context.getResources().getColor(R.color.color_topic_interact));
                }

            }catch (Exception e){

            }
            dialog.dismiss();

        }
    }
    class insertComment extends AsyncTask<String,Void,Boolean>{

        Context context;
        ProgressDialog dialog;
        public insertComment(Context context){
            this.context = context;
        }
        @Override
        protected void onPreExecute() {
            dialog = new ProgressDialog(context);
            dialog.setMessage("please waiting...");
            dialog.show();
        }

        @Override
        protected Boolean doInBackground(String... strings) {
            CommentController comment = new CommentController();
            return comment.insertComment(strings[0],strings[1],strings[2],"0","0");
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            try {
                if(aBoolean == true){
                    //Toast.makeText(context,"success",Toast.LENGTH_SHORT).show();
//                    int count= threads.getNum_comment()+1;
                    SharedPreferences pref = context.getSharedPreferences("MyPref", Context.MODE_PRIVATE);
                    String session_id = pref.getString("session_id", null);
                    ThreadSync changeStatus = new ThreadSync(context, session_id, Integer.parseInt(threads.getId()));
                    changeStatus.execute();
                    UserID us= new UserID(getContext());
                    us.execute(session_id);

                    dialog.dismiss();
                }else {
                    //Toast.makeText(context,"no success",Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            }catch (Exception e){
                dialog.dismiss();
            }
        }
    }
    class UserID extends AsyncTask<String,Void,String>{
        Context context;
        public UserID(Context context){
            this.context=context;
        }
        ProgressDialog dialog;
        @Override
        protected String doInBackground(String... strings) {
            UserController userController  = new UserController(context);
            String user_id = userController.getUserID(strings[0]);
            return user_id;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(getActivity());
            dialog.setMessage("Please wait...");
            dialog.setIndeterminate(true);
            dialog.show();
        }

        @Override
        protected void onPostExecute(String user_ID) {
            try {
                //if(!threads.getUser_id().equals(user_ID)) {
                    SharedPreferences pref = context.getSharedPreferences("MyPref", Context.MODE_PRIVATE);
                    String firstName = pref.getString("firstname", "");

                    List<Hashtable> list = new ArrayList<>();
                    for(int i=0; i<listUser.size(); i++) {
                        String s= listUser.get(i);
                        if(!listUser.get(i).equals(user_ID)) {
                            Notification notification = new Notification("Thread Commented",topic.getId() + "::" + threads.getId() , "10");
                            Hashtable obj = ObjectCommon.ObjectDymanic(notification);
                            obj.put("user_id", listUser.get(i));
                            obj.put("messages",firstName+ " commmented on a thread you following");

                            list.add(obj);
                        }
                    }

                    NotificationController controller = new NotificationController();
                    controller.sendNotification(list);

                //}
            }catch (Exception e){
                String ssss= e.getMessage();
                //Toast.makeText(context,"no data",Toast.LENGTH_LONG).show();
            }
            dialog.dismiss();
        }
    }


}

