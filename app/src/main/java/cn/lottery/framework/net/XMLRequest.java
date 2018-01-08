package cn.lottery.framework.net;

import com.android.volley.Request;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.StringReader;
import java.io.UnsupportedEncodingException;


public class XMLRequest extends Request<XmlPullParser> {

    private SuccessListener<XmlPullParser> mSuccessListener;
    private ErrorListener mErrorListener;

    public XMLRequest(int method, String url, SuccessListener<XmlPullParser> successListener,
                      ErrorListener errorListener) {
        super(method, url, null);
        mSuccessListener = successListener;
        mErrorListener = errorListener;
    }


    @Override
    protected Response<XmlPullParser> parseNetworkResponse(NetworkResponse response) {
        try {
            String xmlString = new String(response.data,HttpHeaderParser.parseCharset(response.headers));
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser xmlPullParser = factory.newPullParser();
            xmlPullParser.setInput(new StringReader(xmlString));
            return Response.success(xmlPullParser, HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (XmlPullParserException e) {
            return Response.error(new ParseError(e));
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
    protected void deliverResponse(XmlPullParser response) {
        mSuccessListener.onSuccessResponse(response, getTag());
    }
}
