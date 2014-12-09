package customview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.magicbox.ashwin.dot_2_dot.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import classed.ImageHolder;

/**
 * Created by ashwin on 22/11/14.
 */
public class DrawingView extends View {
    String TAG=DrawingView.class.getSimpleName();

    //drawing path
    private Path drawPath;
    //drawing and canvas paint
    private Paint drawPaint, canvasPaint;
    //initial color
    private int paintColorWrong = 0xFFFF3300;
    private int paintColorCorrect =0xFF99FF33;
    //canvas
    private Canvas drawCanvas;
    //canvas bitmap
    private Bitmap canvasBitmap;
    //Bitmap Height and Width
    float bitmapHeight,bitmapWidth;
    //Bitmap coordinates
    float[] x;float[] y;

    //Bitmaps
    Bitmap one,two,three,four,five,six,seven,eight,nine;
    //Queue holding the bitmaps in the order they are to be visited
    Queue<ImageHolder> numberQueue;

    //Arraylist holding the current poisions of bitmaps
    List<Integer> nums;

    //Coordinate Store - to store certian coordinates
    private Map<String,Float> coordinateStore;

    public DrawingView(Context context) {
        super(context);
    }
    public DrawingView(Context context, AttributeSet attrs) {
        super(context,attrs);
        Log.i(TAG,"Cnstructor with 2 args");

        setUpDot2Dot();
    }

    public void setUpDot2Dot()
    {
        /*Probably you have to sprinkle  the bitmap images on the screen
        inside this function
         */
        drawPath = new Path();
        drawPaint = new Paint();

        //Set Paint Color
        drawPaint.setColor(paintColorCorrect);

        //Set Initial Path properties
        drawPaint.setAntiAlias(true);
        drawPaint.setStrokeWidth(5);
        drawPaint.setStyle(Paint.Style.STROKE);
        drawPaint.setStrokeJoin(Paint.Join.ROUND);
        drawPaint.setStrokeCap(Paint.Cap.ROUND);

        //instantiate canvasPaint
        canvasPaint = new Paint(Paint.DITHER_FLAG);

        //Initialze the coordinate store
        coordinateStore=new HashMap<String, Float>();

        //Initialize the nums Arraylist
        nums=new ArrayList<Integer>();


    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
    //view given size
        super.onSizeChanged(w, h, oldw, oldh);
        //Get the coordinates for putting the bit map images
        initialiseBitmapHeightWidth(w, h);
        sprinkle(w,h);

        one= Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.one),(int) bitmapWidth,(int) bitmapHeight,true);
        two= Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.two),(int) bitmapWidth,(int) bitmapHeight,true);
        three= Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.three),(int) bitmapWidth,(int) bitmapHeight,true);
        four= Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.four),(int) bitmapWidth,(int) bitmapHeight,true);
        five= Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.five),(int) bitmapWidth,(int) bitmapHeight,true);
        six= Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.six),(int) bitmapWidth,(int) bitmapHeight,true);
        seven= Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.seven),(int) bitmapWidth,(int) bitmapHeight,true);
        eight= Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.eight),(int) bitmapWidth,(int) bitmapHeight,true);
        nine= Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.nine),(int) bitmapWidth,(int) bitmapHeight,true);

        //Add numbers to nums
        for(int i=0;i<=8;i++)
        {
            nums.add(i);
        }


        //Randomly assign each bit map one of the coordinates
         rollDice();

        canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        drawCanvas = new Canvas(canvasBitmap);
    }
    @Override
    protected void onDraw(Canvas canvas) {
    //draw view

        canvas.drawBitmap(one,x[nums.get(0)],y[nums.get(0)],null);
        canvas.drawBitmap(two,x[nums.get(1)],y[nums.get(1)],null);
        canvas.drawBitmap(three,x[nums.get(2)],y[nums.get(2)],null);
        canvas.drawBitmap(four,x[nums.get(3)],y[nums.get(3)],null);
        canvas.drawBitmap(five,x[nums.get(4)],y[nums.get(4)],null);
        canvas.drawBitmap(six,x[nums.get(5)],y[nums.get(5)],null);
        canvas.drawBitmap(seven,x[nums.get(6)],y[nums.get(6)],null);
        canvas.drawBitmap(eight,x[nums.get(7)],y[nums.get(7)],null);
        canvas.drawBitmap(nine,x[nums.get(8)],y[nums.get(8)],null);
        Log.i(TAG,"onDraw()");
        canvas.drawPath(drawPath, drawPaint);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        //detect user touch
        float touchX = event.getX();
        float touchY = event.getY();

        if(coordinateStore.get("lastX")!=null)
        {
            drawPath.setLastPoint(coordinateStore.get("lastX"),coordinateStore.get("lastY"));
        }



        //previous points

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //check if the coordinates are within the area of the bit map that was dequeued
                boolean isNumberCorrect=isCorrectTouch(touchX,touchY);
                if(isNumberCorrect)
                {
                    //Surround the number with a green rectangle
                    drawPath.addRect(numberQueue.element().getX(),numberQueue.element().getY(),numberQueue.element().getX()+bitmapWidth,numberQueue.element().getY()+bitmapHeight,Path.Direction.CCW);
                    numberQueue.remove();
                    coordinateStore.put("lastX",touchX);
                    coordinateStore.put("lastY",touchY);
                }
                if(completedSuccessfully())
                {
                    //Toast
                    Toast.makeText(getContext(),"Successfylly Completed!",Toast.LENGTH_LONG).show();
                }
                //if not then warn the user
                //if it is then paint the touched area
                drawPath.moveTo(touchX, touchY);
                break;
            case MotionEvent.ACTION_MOVE:

                drawPath.lineTo(touchX, touchY);

                if(isCorrectTouch(touchX,touchY))
                {
                    drawPath.addRect(numberQueue.element().getX(),numberQueue.element().getY(),numberQueue.element().getX()+bitmapWidth,numberQueue.element().getY()+bitmapHeight,Path.Direction.CCW);
                    drawPath.moveTo(touchX, touchY);
                    numberQueue.remove();
                    coordinateStore.put("lastX",touchX);
                    coordinateStore.put("lastY",touchY);
                }
                if(completedSuccessfully())
                {
                    //Toast
                    Toast.makeText(getContext(),"Successfylly Completed!",Toast.LENGTH_LONG).show();
                }




                break;
            case MotionEvent.ACTION_UP:
                drawCanvas.drawPath(drawPath, drawPaint);
                //drawPath.reset();
                break;
            default:
                return false;
        }
        invalidate();
        return true;
    }
    public void sprinkle(int width,int height)
    {


        Log.i(TAG,"Width"+Float.toString(bitmapWidth));
        float bitMapConstantW=(2*bitmapWidth)+0.8f;
        float bitMapConstantH=(3*bitmapHeight)+0.12f;
        float constant=width/10;
        x=new float[9];
        y=new float[9];

        x[0]=this.getX();
        y[0]=this.getY();

        x[1]=x[0]+bitMapConstantH;
        y[1]=y[0]+constant/2;

        x[2]=x[0]+bitmapWidth;
        y[2]=y[0]+bitmapHeight+constant;

        x[3]=this.getX()+width-2*bitmapWidth;
        y[3]=y[2]-constant;

        x[4]=x[3]-2*constant;
        y[4]=y[3]+3*constant;

        x[5]=this.getX();
        y[5]=this.getY()+height-2*bitmapHeight-constant;

        x[6]=x[5]+4*constant;
        y[6]=y[5]-4*constant;

        x[7]=this.getX()+width-2*bitmapWidth;
        y[7]=this.getY()+height-2*bitmapHeight-1.5F*constant;

        x[8]=this.getX()+width/2;
        y[8]=this.getY()+height-2*bitmapHeight-2*constant;






    }

    public void rollDice()
    {
        //Shuffle the positions of the bitmaps
        Collections.shuffle(nums);

        //Fill up the numberQueue with new ImaageHolder Objects as the coordinates have changed
        numberQueue=new LinkedList<ImageHolder>();
        numberQueue.add(new ImageHolder("one",x[nums.get(0)],y[nums.get(0)],one));
        numberQueue.add(new ImageHolder("one",x[nums.get(1)],y[nums.get(1)],two));
        numberQueue.add(new ImageHolder("one",x[nums.get(2)],y[nums.get(2)],three));
        numberQueue.add(new ImageHolder("one",x[nums.get(3)],y[nums.get(3)],four));
        numberQueue.add(new ImageHolder("one",x[nums.get(4)],y[nums.get(4)],five));
        numberQueue.add(new ImageHolder("one",x[nums.get(5)],y[nums.get(5)],six));
        numberQueue.add(new ImageHolder("one",x[nums.get(6)],y[nums.get(6)],seven));
        numberQueue.add(new ImageHolder("one",x[nums.get(7)],y[nums.get(7)],eight));
        numberQueue.add(new ImageHolder("one",x[nums.get(8)],y[nums.get(8)],nine));

        //Clear out the Path and Paint
        drawPath.reset();

        //Call the onDraw() function
        invalidate();
    }
    public void initialiseBitmapHeightWidth(int availableWidth,int availableHeight)
    {
        bitmapHeight=availableHeight/9;
        bitmapWidth=availableWidth/9;
    }

    public boolean isCorrectTouch(float x, float y)
    {
        if(numberQueue.isEmpty())
            return false;
        ImageHolder imgHolder=numberQueue.element();
        if(x>=imgHolder.getX() && x<=imgHolder.getX()+bitmapWidth && y>=imgHolder.getY() && y<=imgHolder.getY()+bitmapHeight)
            return true;
        else
            return false;
    }

    public boolean completedSuccessfully()
    {
        if(numberQueue.isEmpty())
            return true;
        return false;
    }
}
