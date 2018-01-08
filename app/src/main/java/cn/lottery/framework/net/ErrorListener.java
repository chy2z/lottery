package cn.lottery.framework.net;

import com.android.volley.VolleyError;

public interface ErrorListener {
	 public void onErrorResponse(VolleyError error, Object tag);
}
