package cn.lottery.framework.widget.scrollView;

import android.content.Context;  
import android.util.AttributeSet;  
import android.widget.ScrollView;  
 
/**
 * 监听滚动到底部
 * @author chy
 *
 */
public class BottomScrollView extends ScrollView {  
  
    private OnScrollListener onScrollToBottom;
    
    private boolean isBottom=false;
      
    public BottomScrollView(Context context, AttributeSet attrs) {  
        super(context, attrs);  
    }  
  
    public BottomScrollView(Context context) {  
        super(context);  
    }  
  
    /**
     * scrollX     新的X滚动像素值
　　   * scrollY     新的Y滚动像素值
　 　  * clampedX        当scrollX被over-scroll的边界限制时，值为true
　　   * clampedY        当scrollY被over-scroll的边界限制时，值为true
     */
    @Override  
    protected void onOverScrolled(int scrollX, int scrollY, boolean clampedX,  
            boolean clampedY) {  
        super.onOverScrolled(scrollX, scrollY, clampedX, clampedY);  
        if(scrollY != 0){           
        	if(isBottom!=clampedY&&null != onScrollToBottom){
        		  isBottom=clampedY;
        		  if(clampedY&&scrollY>450){
        	          onScrollToBottom.onScrollBottomListener(scrollX, scrollY, clampedX, clampedY);
        		  }
        	}

            if(null!=onScrollToBottom)
                onScrollToBottom.onScrollListener(scrollX, scrollY, clampedX, clampedY);
        }
    }  
      
    public void setOnScrollToBottomLintener(OnScrollListener listener){
        onScrollToBottom = listener;  
    }

    /**
     * 滚动监听接口
     */
    public interface OnScrollListener{
        /**
         * 滚动到底部
         * @param scrollX
         * @param scrollY
         * @param clampedX
         * @param isBottom
         */
        public void onScrollBottomListener(int scrollX, int scrollY, boolean clampedX, boolean isBottom);

        /**
         * 滚动
         * @param scrollX
         * @param scrollY
         * @param clampedX
         * @param clampedY
         */
        public void onScrollListener(int scrollX, int scrollY, boolean clampedX,boolean clampedY);
    }  
}  