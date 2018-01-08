package cn.lottery.framework.net;

public interface SuccessListener<T> {
	 public void onSuccessResponse(T response, Object tag);
}
