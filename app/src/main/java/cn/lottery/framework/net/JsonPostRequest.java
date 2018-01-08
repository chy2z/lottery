package cn.lottery.framework.net;

import java.io.UnsupportedEncodingException;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;

public class JsonPostRequest extends Request<JSONObject>
{

	private Map<String, String> mMap;
	private SuccessListener<JSONObject> mSuccessListener;
	private ErrorListener mErrorListener;

	public JsonPostRequest(String url, Map<String, String> map, SuccessListener<JSONObject> successListener,
			ErrorListener errorListener)
	{
		super(Method.POST, url, null);
		mSuccessListener = successListener;
		mErrorListener = errorListener;
		mMap = map;
	}

	@Override
	protected Map<String, String> getParams() throws AuthFailureError
	{
		return mMap;
	}

	@Override
	protected Response<JSONObject> parseNetworkResponse(NetworkResponse response)
	{
		try
		{
			String jsonString = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
			return Response.success(new JSONObject(jsonString), HttpHeaderParser.parseCacheHeaders(response));
		} catch (UnsupportedEncodingException e)
		{
			return Response.error(new ParseError(e));
		} catch (JSONException je)
		{
			return Response.error(new ParseError(je));
		}
	}

	@Override
	public void deliverError(VolleyError error)
	{
		if (mErrorListener != null)
		{
			mErrorListener.onErrorResponse(error, getTag());
		}
	}

	@Override
	protected void deliverResponse(JSONObject response)
	{
		mSuccessListener.onSuccessResponse(response, getTag());
	}

}
