package cn.lottery.app.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import cn.lottery.R;
import cn.lottery.app.activity.login.LoginActivity;
import cn.lottery.app.activity.trace.TraceDynamicImageActivity;
import cn.lottery.app.activity.web.WebPageActivity;
import cn.lottery.framework.Config;
import cn.lottery.framework.widget.RoundedBitmapDisplayer.MyRoundedBitmapDisplayer;
import cn.lottery.framework.widget.editText.EditTextIme;

/**
 * 首页动态
 */
public class HomePageDynamicListViewAdapter extends BaseAdapter {
	private LayoutInflater inflater;
	private ArrayList<HashMap<String, Object>> data;
	private Context mcontext;
	private ImageLoader imageload;
    private int type;

	public static final int Default_Type=0;
	public static final int DynamicDetial_Type=1;
	public static final int MyDynamic_Type = 2;
	public static final int Friend_Type = 3;
	public static final int SpecialWatch_Type = 4;

	public HomePageDynamicListViewAdapter(Context context,int type, ArrayList<HashMap<String, Object>> data,ImageLoader _imageload) {
		super();
		this.inflater = LayoutInflater.from(context);
		this.data = data;
		this.mcontext=context;
		this.imageload=_imageload;		
		this.type=type;
	}


	public interface CommentListener {
		/**
		 * 回复
		 *
         */
		void addReComment(int positon,EditText comment,TextView tv,String content,String dynamicId,String replyUserId,String userId,String user_token,String uuid);

		/**
		 * 评论
		 *
         */
		void addComment(int positon,EditText comment,TextView tv,String content,String dynamicId,String userId,String user_token);

		/**
		 * 点赞
		 */
		void addLike(int positon,ImageView img,TextView tv,String dynamicId,String userid,String user_token);

		/**
		 * 关注
         */
		void addWatch(int positon,Button watch, String dynamicId,String userid,String user_token);

		/**
		 * 显示动态详情
         */
		void showDetail(int positon,String dynamicId,String userid,String user_token);
	}

	private CommentListener listener;

	public void setCommentListener(CommentListener listener) {
		this.listener = listener;
	}

	public ArrayList<HashMap<String, Object>> getData(){
		return data;
	}

	@Override
	public int getCount() {
		return data.size();
	}

	@Override
	public Object getItem(int position) {
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		
		final ViewHolder holder;
		
		if (convertView == null) {
			
			holder = new ViewHolder();
			
			convertView = inflater.inflate(R.layout.item_dynamic_listview, null);
			
			holder.userImg=(ImageView) convertView.findViewById(R.id.userImg);
			
			holder.nickName=(TextView) convertView.findViewById(R.id.nickName);
			
			holder.createTime=(TextView) convertView.findViewById(R.id.createTime);

			holder.content=(TextView) convertView.findViewById(R.id.content);

			holder.address=(TextView) convertView.findViewById(R.id.address);

			holder.likeCount=(TextView) convertView.findViewById(R.id.likeCount);

			holder.ButComment=(Button) convertView.findViewById(R.id.ButComment);

			holder.ButWatch=(Button) convertView.findViewById(R.id.ButWatch);

			holder.contentImg1=(ImageView) convertView.findViewById(R.id.contentImg1);
			holder.contentImg2=(ImageView) convertView.findViewById(R.id.contentImg2);
			holder.contentImg3=(ImageView) convertView.findViewById(R.id.contentImg3);
			holder.contentImg4=(ImageView) convertView.findViewById(R.id.contentImg4);
			holder.contentImg5=(ImageView) convertView.findViewById(R.id.contentImg5);
			holder.contentImg6=(ImageView) convertView.findViewById(R.id.contentImg6);
			holder.contentImg7=(ImageView) convertView.findViewById(R.id.contentImg7);
			holder.contentImg8=(ImageView) convertView.findViewById(R.id.contentImg8);
			holder.contentImg9=(ImageView) convertView.findViewById(R.id.contentImg9);

			holder.layoutReplay=(LinearLayout) convertView.findViewById(R.id.layoutReplay);

			holder.layoutComment=(LinearLayout) convertView.findViewById(R.id.layoutComment);

			holder.layoutLike=(LinearLayout) convertView.findViewById(R.id.layoutLike);

			holder.zan=(ImageView)convertView.findViewById(R.id.zan);

			holder.layoutimgRow1=(LinearLayout) convertView.findViewById(R.id.layoutimgRow1);
			holder.layoutimgRow2=(LinearLayout) convertView.findViewById(R.id.layoutimgRow2);
			holder.layoutimgRow3=(LinearLayout) convertView.findViewById(R.id.layoutimgRow3);

			holder.release=(Button) convertView.findViewById(R.id.release);

			holder.comment=(EditTextIme) convertView.findViewById(R.id.comment);

			convertView.setTag(holder);
			
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		if (data.size()>0) {
			holder.nickName.setText(data.get(position).get("nickName").toString());

			holder.createTime.setText(data.get(position).get("createTime").toString());

			holder.content.setText(data.get(position).get("content").toString());

			if(type!=DynamicDetial_Type) { //动态详情
				holder.content.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						if (!Config.userToken.equals("")) {
							listener.showDetail(position, data.get(position).get("id").toString(), Config.customerID, Config.userToken);
						} else {
							Intent intent = new Intent(mcontext, LoginActivity.class);
							mcontext.startActivity(intent);
						}
					}
				});
			}

			holder.address.setText(data.get(position).get("address").toString());

			if(type==MyDynamic_Type||type==Friend_Type) { //我的动态和好友动态隐藏关注
				holder.ButWatch.setVisibility(View.GONE);
			}
			else{

				holder.ButWatch.setText(data.get(position).get("watchStatus").toString());

				if(holder.ButWatch.getText().toString().equals("未关注")){
					holder.ButWatch.setTextColor(mcontext.getResources().getColor(R.color.txt_gray));
					holder.ButWatch.setBackgroundResource(R.drawable.graybox);
				}
				else{
					holder.ButWatch.setTextColor(mcontext.getResources().getColor(R.color.dyred));
					holder.ButWatch.setBackgroundResource(R.drawable.redbox);
				}

				holder.ButWatch.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						if (!Config.userToken.equals("")) {
							listener.addWatch(position,holder.ButWatch,data.get(position).get("id").toString(), Config.customerID, Config.userToken);
						} else {
							Intent intent = new Intent(mcontext, LoginActivity.class);
							mcontext.startActivity(intent);
						}
					}
				});
			}

			holder.likeCount.setText("("+data.get(position).get("likeCount").toString()+")");
			holder.likeCount.setTag(data.get(position).get("likeCount").toString());

			if(!(boolean)data.get(position).get("isLike")){
				holder.zan.setImageResource(R.drawable.zancancel);
			}
			else{
				holder.zan.setImageResource(R.drawable.zan);
			}

			//防止EditText抢占焦点
			holder.layoutLike.setOnTouchListener(new View.OnTouchListener() {
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					if (event.getAction() == MotionEvent.ACTION_DOWN) {

					} else if (event.getAction() == MotionEvent.ACTION_UP) {
						if(!Config.userToken.equals("")) {
							listener.addLike(position,holder.zan,holder.likeCount,data.get(position).get("id").toString(),Config.customerID,Config.userToken);
						}
						else{
							Intent intent = new Intent(mcontext, LoginActivity.class);
							mcontext.startActivity(intent);
						}
					}
					return false;
				}
			});

			holder.ButComment.setTag(data.get(position).get("id").toString());
			holder.ButComment.setVisibility(View.GONE);
			holder.ButComment.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if(!Config.userToken.equals("")) {
						holder.layoutComment.setVisibility(View.VISIBLE);
						holder.release.setVisibility(View.VISIBLE);
						holder.comment.setEnabled(true);
						holder.comment.setTag(holder.ButComment.getTag());
						holder.comment.setHint("评论");
						holder.comment.setFocusable(true);
						holder.comment.setFocusableInTouchMode(true);
						holder.comment.requestFocus();
						InputMethodManager imm = (InputMethodManager) mcontext.getSystemService(Context.INPUT_METHOD_SERVICE);
						imm.toggleSoftInput(0, InputMethodManager.SHOW_IMPLICIT);
					}
					else{
						Intent intent = new Intent(mcontext, LoginActivity.class);
						mcontext.startActivity(intent);
					}
				}
			});

			holder.comment.setOnTouchListener(new View.OnTouchListener() {
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					if (event.getAction() == MotionEvent.ACTION_UP) {
						holder.ButComment.performClick();
					}
					return false;
				}
			});

			holder.comment.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					holder.ButComment.performClick();
				}
			});

			holder.release.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					String content=holder.comment.getText().toString();
					if(content.equals("")){
						Toast.makeText(mcontext,"请输入内容", Toast.LENGTH_SHORT).show();
						return;
					}
					if(!Config.userToken.equals("")) {
						TextView tvColor = null;
						SpannableStringBuilder style = null;
						LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
								LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
						params.setMargins(0, 5, 0, 5);
						try {
							Boolean isComment=false;
							if (holder.comment.getHint().toString().equals("评论")) {
								isComment = true;
							}
							else{
								isComment=false;
							}
							JSONObject job =null;
							String replay;
							String nickName;
							if (isComment) {
								replay = Config.nickname + ":" + content;
								nickName=Config.nickname;
								int bstart = replay.indexOf(Config.nickname);
								int bend = bstart + Config.nickname.length();
								style = new SpannableStringBuilder(replay);
								style.setSpan(new ForegroundColorSpan(Color.parseColor("#1f7ed7")), bstart, bend, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
							} else {
								job= (JSONObject) holder.comment.getTag();
								nickName = job.getString("nickName");
								replay = Config.nickname + " 回复 " + nickName + ":" + content;
								int bstart = replay.indexOf(Config.nickname);
								int bend = bstart + Config.nickname.length();
								int fstart = replay.indexOf(" " + nickName);
								int fend = fstart + (" " + nickName).length();
								style = new SpannableStringBuilder(replay);
								style.setSpan(new ForegroundColorSpan(Color.parseColor("#1f7ed7")), bstart, bend, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
								style.setSpan(new ForegroundColorSpan(Color.parseColor("#1f7ed7")), fstart, fend, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
							}
							tvColor = new TextView(mcontext);
							tvColor.setBackgroundResource(R.drawable.selector_menu_item);
							tvColor.setLayoutParams(params);
							tvColor.setText(style);
							tvColor.setEnabled(false);
							tvColor.setOnClickListener(new View.OnClickListener() {
								@Override
								public void onClick(View v) {
									try {
										TextView t=(TextView)v;
										JSONObject job = (JSONObject)t.getTag();
										holder.layoutComment.setVisibility(View.VISIBLE);
										holder.comment.setTag(job);
										holder.comment.setHint("回复" + job.getString("nickName") + ":");
										holder.comment.setFocusable(true);
										holder.comment.setFocusableInTouchMode(true);
										holder.comment.requestFocus();
										InputMethodManager imm = (InputMethodManager) mcontext.getSystemService(Context.INPUT_METHOD_SERVICE);
										imm.toggleSoftInput(0, InputMethodManager.SHOW_IMPLICIT);
									} catch (JSONException e) {
										e.printStackTrace();
									}
								}
							});
							holder.layoutReplay.addView(tvColor);
							holder.comment.setText("");
							if(isComment) {
								listener.addComment(position,holder.comment,tvColor,content,holder.comment.getTag().toString() , Config.customerID, Config.userToken);
							}else{
								listener.addReComment(position,holder.comment,tvColor,content,job.getString("dyId"),job.getString("userId"),Config.customerID, Config.userToken,job.getString("uuid"));
							}

							//holder.release.setVisibility(View.GONE);
						}
						catch (JSONException e) {
							e.printStackTrace();
						}
					}
					else{
						Intent intent = new Intent(mcontext, LoginActivity.class);
						mcontext.startActivity(intent);
					}
				}
			});

			holder.userImg.setScaleType(ScaleType.FIT_XY);
				holder.userImg.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						Intent intent = new Intent(mcontext, WebPageActivity.class);
						intent.putExtra("httpURL",Config.H5_URL_PREFIX+"/kamang/html/my/personalInfo.html?userId=" +data.get(position).get("userId").toString() + "&accountId=" + data.get(position).get("accountId").toString());
						mcontext.startActivity(intent);
					}
				});


			imageload.displayImage(data.get(position).get("userImg").toString(),holder.userImg, new DisplayImageOptions.Builder()
			.showImageOnLoading(R.drawable.login_head)
			.showImageForEmptyUri(R.drawable.login_head)
			.showImageOnFail(R.drawable.login_head)
			.cacheInMemory(true)
			.cacheOnDisk(true)
			.considerExifParams(true)
			.bitmapConfig(Bitmap.Config.ARGB_8888)
			.displayer(new MyRoundedBitmapDisplayer(0))
			.build());

			//=====================start 回复=================================
			holder.layoutReplay.removeAllViews();

			try {

				JSONArray commentList = (JSONArray) data.get(position).get("commentList");

				JSONObject job = null;

				TextView tvColor=null;

				String replay="";

				SpannableStringBuilder style=null;

				LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
						LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
				params.setMargins(0, 5, 0, 5);

				for (int j = 0; j < commentList.length(); j++) {
					job=commentList.getJSONObject(j);
					if(job.getString("reUserId").equals("")){
						replay=job.getString("nickName")+":"+job.getString("content");
						int bstart=replay.indexOf(job.getString("nickName"));
						int bend=bstart+job.getString("nickName").length();
						style=new SpannableStringBuilder(replay);
						style.setSpan(new ForegroundColorSpan(Color.parseColor("#1f7ed7")),bstart,bend, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
					}
					else{
						replay=job.getString("nickName")+" 回复 "+job.getString("reNickName")+":"+job.getString("content");
						int bstart=replay.indexOf(job.getString("nickName"));
						int bend=bstart+job.getString("nickName").length();
						int fstart=replay.indexOf(" "+job.getString("reNickName"));
						int fend=fstart+(" "+job.getString("reNickName")).length();
						style=new SpannableStringBuilder(replay);
						style.setSpan(new ForegroundColorSpan(Color.parseColor("#1f7ed7")),bstart,bend, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
						style.setSpan(new ForegroundColorSpan(Color.parseColor("#1f7ed7")),fstart,fend,Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
					}
					tvColor= new TextView(mcontext);
					tvColor.setTag(job);
					tvColor.setBackgroundResource(R.drawable.selector_menu_item);
					tvColor.setLayoutParams(params);
					tvColor.setText(style);
					tvColor.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							if(!Config.userToken.equals("")) {
								try {
									TextView t=(TextView)v;
									JSONObject job = (JSONObject)t.getTag();
									holder.layoutComment.setVisibility(View.VISIBLE);
									holder.release.setVisibility(View.VISIBLE);
									holder.comment.setEnabled(true);
									holder.comment.setTag(job);
									holder.comment.setHint("回复" + job.getString("nickName") + ":");
									holder.comment.setFocusable(true);
									holder.comment.setFocusableInTouchMode(true);
									holder.comment.requestFocus();
									InputMethodManager imm = (InputMethodManager) mcontext.getSystemService(Context.INPUT_METHOD_SERVICE);
									imm.toggleSoftInput(0, InputMethodManager.SHOW_IMPLICIT);
								} catch (JSONException e) {
									e.printStackTrace();
								}
							}
							else{
								Intent intent = new Intent(mcontext, LoginActivity.class);
								mcontext.startActivity(intent);
							}
						}
					});
					holder.layoutReplay.addView(tvColor);
				}
			}
			catch (JSONException e) {
			   e.printStackTrace();
		    }

			//=====================end 回复=====================================

			//=====================图像=========================================

			if(data.get(position).get("layoutimgRow1").toString().equals("1")) {
				holder.layoutimgRow1.setVisibility(View.VISIBLE);
				if (!data.get(position).get("contentImg1").toString().equals("")) {
					holder.contentImg1.setVisibility(View.VISIBLE);
					holder.contentImg1.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							Intent intent = new Intent(mcontext, TraceDynamicImageActivity.class);
							intent.putExtra("url",data.get(position).get("imgUrlAll").toString());
							intent.putExtra("imgIndex","0");
							mcontext.startActivity(intent);
						}
					});
					imageload.displayImage(data.get(position).get("contentImg1").toString(),holder.contentImg1, new DisplayImageOptions.Builder()
							.showImageOnLoading(R.drawable.login_head)
							.showImageForEmptyUri(R.drawable.login_head)
							.showImageOnFail(R.drawable.login_head)
							.cacheInMemory(true)
							.cacheOnDisk(true)
							.considerExifParams(true)
							.bitmapConfig(Bitmap.Config.RGB_565)
							.displayer(new SimpleBitmapDisplayer())
							.build());
				}
				else{
					holder.contentImg1.setVisibility(View.INVISIBLE);
				}

				if (!data.get(position).get("contentImg2").toString().equals("")) {
					holder.contentImg2.setVisibility(View.VISIBLE);
					holder.contentImg2.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							Intent intent = new Intent(mcontext, TraceDynamicImageActivity.class);
							intent.putExtra("url",data.get(position).get("imgUrlAll").toString());
							intent.putExtra("imgIndex","1");
							mcontext.startActivity(intent);
						}
					});
					imageload.displayImage(data.get(position).get("contentImg2").toString(),holder.contentImg2, new DisplayImageOptions.Builder()
							.showImageOnLoading(R.drawable.login_head)
							.showImageForEmptyUri(R.drawable.login_head)
							.showImageOnFail(R.drawable.login_head)
							.cacheInMemory(true)
							.cacheOnDisk(true)
							.considerExifParams(true)
							.bitmapConfig(Bitmap.Config.RGB_565)
							.displayer(new SimpleBitmapDisplayer())
							.build());
				}
				else{
					holder.contentImg2.setVisibility(View.INVISIBLE);
				}
				if (!data.get(position).get("contentImg3").toString().equals("")) {
					holder.contentImg3.setVisibility(View.VISIBLE);
					holder.contentImg3.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							Intent intent = new Intent(mcontext, TraceDynamicImageActivity.class);
							intent.putExtra("url",data.get(position).get("imgUrlAll").toString());
							intent.putExtra("imgIndex","2");
							mcontext.startActivity(intent);
						}
					});
					imageload.displayImage(data.get(position).get("contentImg3").toString(),holder.contentImg3, new DisplayImageOptions.Builder()
							.showImageOnLoading(R.drawable.login_head)
							.showImageForEmptyUri(R.drawable.login_head)
							.showImageOnFail(R.drawable.login_head)
							.cacheInMemory(true)
							.cacheOnDisk(true)
							.considerExifParams(true)
							.bitmapConfig(Bitmap.Config.RGB_565)
							.displayer(new SimpleBitmapDisplayer())
							.build());
				}
				else{
					holder.contentImg3.setVisibility(View.INVISIBLE);
				}
			}
			else{
				holder.layoutimgRow1.setVisibility(View.GONE);
				holder.contentImg1.setVisibility(View.GONE);
				holder.contentImg2.setVisibility(View.GONE);
				holder.contentImg3.setVisibility(View.GONE);
			}

			if(data.get(position).get("layoutimgRow2").toString().equals("1")){
				holder.layoutimgRow2.setVisibility(View.VISIBLE);
				if (!data.get(position).get("contentImg4").toString().equals("")) {
					holder.contentImg4.setVisibility(View.VISIBLE);
					holder.contentImg4.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							Intent intent = new Intent(mcontext, TraceDynamicImageActivity.class);
							intent.putExtra("url",data.get(position).get("imgUrlAll").toString());
							intent.putExtra("imgIndex","3");
							mcontext.startActivity(intent);
						}
					});
					imageload.displayImage(data.get(position).get("contentImg4").toString(),holder.contentImg4, new DisplayImageOptions.Builder()
							.showImageOnLoading(R.drawable.login_head)
							.showImageForEmptyUri(R.drawable.login_head)
							.showImageOnFail(R.drawable.login_head)
							.cacheInMemory(true)
							.cacheOnDisk(true)
							.considerExifParams(true)
							.bitmapConfig(Bitmap.Config.RGB_565)
							.displayer(new SimpleBitmapDisplayer())
							.build());
				}
				else{
					holder.contentImg4.setVisibility(View.INVISIBLE);
				}

				if (!data.get(position).get("contentImg5").toString().equals("")) {
					holder.contentImg5.setVisibility(View.VISIBLE);
					holder.contentImg5.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							Intent intent = new Intent(mcontext, TraceDynamicImageActivity.class);
							intent.putExtra("url",data.get(position).get("imgUrlAll").toString());
							intent.putExtra("imgIndex","4");
							mcontext.startActivity(intent);
						}
					});
					imageload.displayImage(data.get(position).get("contentImg5").toString(),holder.contentImg5, new DisplayImageOptions.Builder()
							.showImageOnLoading(R.drawable.login_head)
							.showImageForEmptyUri(R.drawable.login_head)
							.showImageOnFail(R.drawable.login_head)
							.cacheInMemory(true)
							.cacheOnDisk(true)
							.considerExifParams(true)
							.bitmapConfig(Bitmap.Config.RGB_565)
							.displayer(new SimpleBitmapDisplayer())
							.build());
				}
				else{
					holder.contentImg5.setVisibility(View.INVISIBLE);
				}

				if (!data.get(position).get("contentImg6").toString().equals("")) {
					holder.contentImg6.setVisibility(View.VISIBLE);
					holder.contentImg6.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							Intent intent = new Intent(mcontext, TraceDynamicImageActivity.class);
							intent.putExtra("url",data.get(position).get("imgUrlAll").toString());
							intent.putExtra("imgIndex","5");
							mcontext.startActivity(intent);
						}
					});
					imageload.displayImage(data.get(position).get("contentImg6").toString(),holder.contentImg6, new DisplayImageOptions.Builder()
							.showImageOnLoading(R.drawable.login_head)
							.showImageForEmptyUri(R.drawable.login_head)
							.showImageOnFail(R.drawable.login_head)
							.cacheInMemory(true)
							.cacheOnDisk(true)
							.considerExifParams(true)
							.bitmapConfig(Bitmap.Config.RGB_565)
							.displayer(new SimpleBitmapDisplayer())
							.build());
				}
				else{
					holder.contentImg6.setVisibility(View.INVISIBLE);
				}
			}
			else{
				    holder.layoutimgRow2.setVisibility(View.GONE);
					holder.contentImg4.setVisibility(View.GONE);
					holder.contentImg5.setVisibility(View.GONE);
					holder.contentImg6.setVisibility(View.GONE);
			}

			if(data.get(position).get("layoutimgRow3").toString().equals("1")){
				holder.layoutimgRow3.setVisibility(View.VISIBLE);
				if (!data.get(position).get("contentImg7").toString().equals("")) {
					holder.contentImg7.setVisibility(View.VISIBLE);
					holder.contentImg7.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							Intent intent = new Intent(mcontext, TraceDynamicImageActivity.class);
							intent.putExtra("url",data.get(position).get("imgUrlAll").toString());
							intent.putExtra("imgIndex","6");
							mcontext.startActivity(intent);
						}
					});
					imageload.displayImage(data.get(position).get("contentImg7").toString(),holder.contentImg7, new DisplayImageOptions.Builder()
							.showImageOnLoading(R.drawable.login_head)
							.showImageForEmptyUri(R.drawable.login_head)
							.showImageOnFail(R.drawable.login_head)
							.cacheInMemory(true)
							.cacheOnDisk(true)
							.considerExifParams(true)
							.bitmapConfig(Bitmap.Config.RGB_565)
							.displayer(new SimpleBitmapDisplayer())
							.build());
				}
				else{
					holder.contentImg7.setVisibility(View.INVISIBLE);
				}

				if (!data.get(position).get("contentImg8").toString().equals("")) {
					holder.contentImg8.setVisibility(View.VISIBLE);
					holder.contentImg8.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							Intent intent = new Intent(mcontext, TraceDynamicImageActivity.class);
							intent.putExtra("url",data.get(position).get("imgUrlAll").toString());
							intent.putExtra("imgIndex","7");
							mcontext.startActivity(intent);
						}
					});
					imageload.displayImage(data.get(position).get("contentImg8").toString(),holder.contentImg8, new DisplayImageOptions.Builder()
							.showImageOnLoading(R.drawable.login_head)
							.showImageForEmptyUri(R.drawable.login_head)
							.showImageOnFail(R.drawable.login_head)
							.cacheInMemory(true)
							.cacheOnDisk(true)
							.considerExifParams(true)
							.bitmapConfig(Bitmap.Config.RGB_565)
							.displayer(new SimpleBitmapDisplayer())
							.build());
				}
				else{
					holder.contentImg8.setVisibility(View.INVISIBLE);
				}

				if (!data.get(position).get("contentImg9").toString().equals("")) {
					holder.contentImg9.setVisibility(View.VISIBLE);
					holder.contentImg9.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							Intent intent = new Intent(mcontext, TraceDynamicImageActivity.class);
							intent.putExtra("url",data.get(position).get("imgUrlAll").toString());
							intent.putExtra("imgIndex","8");
							mcontext.startActivity(intent);
						}
					});
					imageload.displayImage(data.get(position).get("contentImg9").toString(),holder.contentImg9, new DisplayImageOptions.Builder()
							.showImageOnLoading(R.drawable.login_head)
							.showImageForEmptyUri(R.drawable.login_head)
							.showImageOnFail(R.drawable.login_head)
							.cacheInMemory(true)
							.cacheOnDisk(true)
							.considerExifParams(true)
							.bitmapConfig(Bitmap.Config.RGB_565)
							.displayer(new SimpleBitmapDisplayer())
							.build());
				}
				else{
					holder.contentImg9.setVisibility(View.INVISIBLE);
				}
			}
			else{
				holder.layoutimgRow3.setVisibility(View.GONE);
				holder.contentImg7.setVisibility(View.GONE);
				holder.contentImg8.setVisibility(View.GONE);
				holder.contentImg9.setVisibility(View.GONE);
			}
		}
		
		return convertView;
	}

	class ViewHolder {
		ImageView userImg;
		TextView nickName;
		TextView createTime;
		TextView content;
		TextView address;
		TextView likeCount;
		Button ButWatch;
		Button ButComment;
		ImageView contentImg1;
		ImageView contentImg2;
		ImageView contentImg3;
		ImageView contentImg4;
		ImageView contentImg5;
		ImageView contentImg6;
		ImageView contentImg7;
		ImageView contentImg8;
		ImageView contentImg9;
		LinearLayout layoutimgRow1;
		LinearLayout layoutimgRow2;
		LinearLayout layoutimgRow3;
		LinearLayout layoutReplay;
		LinearLayout layoutComment;
		LinearLayout layoutLike;
		Button release;
		EditTextIme comment;
		ImageView zan;
	}
}