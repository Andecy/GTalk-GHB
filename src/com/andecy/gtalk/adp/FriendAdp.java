package com.andecy.gtalk.adp;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.andecy.gtalk.R.id;
import com.andecy.gtalk.R.layout;
import com.andecy.gtalk.bean.Constant;
import com.andecy.gtalk.bean.User;

public class FriendAdp extends BaseAdapter {
	private List<User> list;
	private Context context;

	public FriendAdp(Context context, List<User> list) {
		// TODO Auto-generated constructor stub
		this.context = context;
		this.list = list;
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
			convertView = View.inflate(context, layout.listitem_friend, null);
			holder.tv_name = (TextView) convertView
					.findViewById(id.tv_friend_name);
			holder.iv_qq = (ImageView) convertView
					.findViewById(id.iv_friend_qq);

			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}
		holder.tv_name.setText(list.get(position).getName());
		holder.iv_qq.setBackgroundColor(Constant.getRandColor(0, 255));
		return convertView;
	}

	class Holder {
		TextView tv_name;
		ImageView iv_qq;
	}
}
