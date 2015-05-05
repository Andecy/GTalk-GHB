package com.andecy.gtalk.adp;

import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.andecy.gtalk.R.id;
import com.andecy.gtalk.R.layout;
import com.andecy.gtalk.bean.ChatMsg;

public class MsgAdp extends BaseAdapter {
	private List<ChatMsg> list;
	private Context context;
	private static final String TAG = "MsgAdp";

	public MsgAdp(Context context, List<ChatMsg> list) {
		// TODO Auto-generated constructor stub
		this.context = context;
		this.list = list;
	}

	public void addMsg(String myMsg, String taMsg) {
		ChatMsg msg = new ChatMsg();
		msg.setMyMsg(myMsg);
		msg.setTaMsg(taMsg);
		list.add(msg);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		Holder holder;
		if (convertView == null) {
			holder = new Holder();
			convertView = View.inflate(context, layout.listitem_chat, null);
			holder.tv_myMsg = (TextView) convertView
					.findViewById(id.tv_chat_mymsg);
			holder.iv_myQQ = (ImageView) convertView
					.findViewById(id.iv_chat_myqq);
			holder.tv_taMsg = (TextView) convertView
					.findViewById(id.tv_chat_tamsg);
			holder.iv_taQQ = (ImageView) convertView
					.findViewById(id.iv_chat_taqq);

			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}
		String msg = list.get(position).getMyMsg();
		String tmsg = list.get(position).getTaMsg();
		Log.d(TAG, "msg--->" + msg);
		Log.d(TAG, "tmsg--->" + tmsg);
//		if (!msg.equals(null)) {
			holder.tv_myMsg.setVisibility(View.VISIBLE);
			holder.iv_myQQ.setVisibility(View.VISIBLE);
			holder.tv_myMsg.setText(msg);
//		}
//		if (!tmsg.equals(null)) {
		holder.tv_taMsg.setVisibility(View.VISIBLE);
		holder.iv_taQQ.setVisibility(View.VISIBLE);
		holder.tv_taMsg.setText(tmsg);
//		}
		return convertView;
	}

	class Holder {
		TextView tv_myMsg;
		ImageView iv_myQQ;
		TextView tv_taMsg;
		ImageView iv_taQQ;

	}

}
