package cn.lottery.framework.widget.gridview;

import cn.lottery.R;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.widget.GridView;

public class XGridView extends GridView{
    public XGridView(Context context, AttributeSet attrs) { 
          super(context, attrs); 
      } 
   
      public XGridView(Context context) { 
          super(context); 
      } 
   
      public XGridView(Context context, AttributeSet attrs, int defStyle) { 
          super(context, attrs, defStyle); 
      } 
   
      @Override 
      public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) { 
   
          int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, 
                  MeasureSpec.AT_MOST); 
          super.onMeasure(widthMeasureSpec, expandSpec); 
      } 
      
      @Override
      protected void dispatchDraw(Canvas canvas){
          super.dispatchDraw(canvas);
          View localView1 = getChildAt(0);
          if(localView1!=null) {
              int column = getWidth() / localView1.getWidth();
              int childCount = getChildCount();
              Paint localPaint;
              localPaint = new Paint();
              localPaint.setStyle(Paint.Style.STROKE);
              localPaint.setColor(getContext().getResources().getColor(R.color.gridview_cell_border));
          
         /* for (int i = 0; i < childCount; i++) {//遍历子view
              View cellView = getChildAt(i);//获取子view
              if (i < 4) {//第一行
                  canvas.drawLine(cellView.getLeft(), cellView.getTop(), cellView.getRight(), cellView.getTop(), localPaint);
              }
              if (i % column == 0) {//第一列
                  canvas.drawLine(cellView.getLeft(), cellView.getTop(), cellView.getLeft(), cellView.getBottom(), localPaint);
              }
              if ((i + 1) % column == 0) {//第三列
                  //画子view底部横线
                  canvas.drawLine(cellView.getLeft(), cellView.getBottom(), cellView.getRight(), cellView.getBottom(), localPaint);
                  canvas.drawLine(cellView.getRight(), cellView.getTop(), cellView.getRight(), cellView.getBottom(), localPaint);
              } else if ((i + 1) > (childCount - (childCount % column))) {//如果view是最后一行
                  //画子view的右边竖线
                  canvas.drawLine(cellView.getRight(), cellView.getTop(), cellView.getRight(), cellView.getBottom(), localPaint);
                  canvas.drawLine(cellView.getLeft(), cellView.getBottom(), cellView.getRight(), cellView.getBottom(), localPaint);
              } else {//如果view不是最后一行
                  //画子view的右边竖线
                  canvas.drawLine(cellView.getRight(), cellView.getTop(), cellView.getRight(), cellView.getBottom(), localPaint);
                  //画子view的底部横线
                  canvas.drawLine(cellView.getLeft(), cellView.getBottom(), cellView.getRight(), cellView.getBottom(), localPaint);
              }
          }*/

              for (int i = 0; i < childCount; i++) {//遍历子view
                  View cellView = getChildAt(i);//获取子view
                  if (i < 3) {//第一行
                      canvas.drawLine(cellView.getLeft(), cellView.getTop(), cellView.getRight(), cellView.getTop(), localPaint);
                  }
                  if (i % column == 0) {//第一列
                      canvas.drawLine(cellView.getLeft(), cellView.getTop(), cellView.getLeft(), cellView.getBottom(), localPaint);
                  }
                  if ((i + 1) % column == 0) {//第三列
                      //画子view底部横线
                      canvas.drawLine(cellView.getLeft(), cellView.getBottom(), cellView.getRight(), cellView.getBottom(), localPaint);
                      canvas.drawLine(cellView.getRight(), cellView.getTop(), cellView.getRight(), cellView.getBottom(), localPaint);
                  } else if ((i + 1) > (childCount - (childCount % column))) {//如果view是最后一行
                      //画子view的右边竖线
                      canvas.drawLine(cellView.getRight(), cellView.getTop(), cellView.getRight(), cellView.getBottom(), localPaint);
                      canvas.drawLine(cellView.getLeft(), cellView.getBottom(), cellView.getRight(), cellView.getBottom(), localPaint);
                  } else {//如果view不是最后一行
                      //画子view的右边竖线
                      canvas.drawLine(cellView.getRight(), cellView.getTop(), cellView.getRight(), cellView.getBottom(), localPaint);
                      //画子view的底部横线
                      canvas.drawLine(cellView.getLeft(), cellView.getBottom(), cellView.getRight(), cellView.getBottom(), localPaint);
                  }
              }
          }
      }
}