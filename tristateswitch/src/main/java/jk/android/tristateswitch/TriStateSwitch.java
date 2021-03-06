package jk.android.tristateswitch;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

/**
 * A custom switch toggle that has three sides: left, middle, right
 * @author Jamil Kafi
 * @version 1.0
 */
public class TriStateSwitch extends View {

    // ******************************************

    private static final String TAG = TriStateSwitch.class.getSimpleName();

    public enum SIDE {LEFT, MIDDLE, RIGHT}

    public static final int THUMB_SHAPE_RECTANGLE = 0;
    public static final int THUMB_SHAPE_CIRCLE = 1;

    private final int DEFAULT_RECTANGULAR_VIEW_CORNER_RADII = 32;
    private final int DEFAULT_RECTANGULAR_THUMB_CORNER_RADII = 32;
    private int DEFAULT_CIRCULAR_VIEW_CORNER_RADII = 0;      // To be calculated later
    private int DEFAULT_CIRCULAR_THUMB_CORNER_RADII = 0;     // To be calculated later

    private final String WIDTH_PROPERTY = "width";
    private final String HEIGHT_PROPERTY = "height";

    private int defaultWidthPx = 80;
    private int defaultHeightPx = 40;

    private int defaultWidthDp = 80;
    private int defaultHeightDp = 40;

    private int thumbColor = Color.WHITE;
    private int neutralColor = Color.GRAY;
    private int leftSideColor = Color.GRAY;
    private int rightSideColor = Color.GRAY;
    private Integer thumbSpeed = 500;   // 500 ms
    private Integer thumbShape;
    private int shapeTransformationSpeed = 500;

    private RectF outerViewShape;
    private AnimatableRectF thumbViewShape;
    private Paint viewPaint, thumbPaint;

    private int viewInnerPadding = 16;

    private int rectangularViewCornerRadii = 32;
    private int circularViewCornerRadii = 0;        // To be calculated later
    private int rectangularThumbCornerRadii = 32;
    private int circularThumbCornerRadii = 0;       // To be calculated later

    private SIDE side = SIDE.MIDDLE;

    private ICallback iCallback;



    // ******************************************

    /**
     * Simple constructor to use when creating a view from code.
     *
     * @param context The Context the view is running in, through which it can
     *                access the current theme, resources, etc.
     */
    public TriStateSwitch(Context context) {
        // super(context);
        this(context, null);
    }

    /**
     * Constructor that is called when inflating a view from XML. This is called
     * when a view is being constructed from an XML file, supplying attributes
     * that were specified in the XML file. This version uses a default style of
     * 0, so the only attribute values applied are those in the Context's Theme
     * and the given AttributeSet.
     * The method onFinishInflate() will be called after all children have been
     * added.
     *
     * @param context The Context the view is running in, through which it can
     *                access the current theme, resources, etc.
     * @param attrs   The attributes of the XML tag that is inflating the view.
     * @see #TriStateSwitch(Context, AttributeSet, int)
     */
    public TriStateSwitch(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        // setWillNotDraw(false);   // used only when the parent is a ViewGroup (like LinearLayout, RelativeLayout, ....)

        if (attrs != null) {
            TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.TriStateSwitch, 0, 0);
            try {
                if (typedArray.hasValue(R.styleable.TriStateSwitch_thumbColor)) {
                    thumbColor = typedArray.getColor(R.styleable.TriStateSwitch_thumbColor, getResources().getColor(android.R.color.white));
                }
                if (typedArray.hasValue(R.styleable.TriStateSwitch_neutralColor)) {
                    neutralColor = typedArray.getColor(R.styleable.TriStateSwitch_neutralColor, getResources().getColor(android.R.color.darker_gray));
                }
                if (typedArray.hasValue(R.styleable.TriStateSwitch_leftSideColor)) {
                    leftSideColor = typedArray.getColor(R.styleable.TriStateSwitch_leftSideColor, 0);
                }
                if (typedArray.hasValue(R.styleable.TriStateSwitch_rightSideColor)) {
                    rightSideColor = typedArray.getColor(R.styleable.TriStateSwitch_rightSideColor, 0);
                }
                if (typedArray.hasValue(R.styleable.TriStateSwitch_thumbSpeed)) {
                    thumbSpeed = typedArray.getInteger(R.styleable.TriStateSwitch_thumbSpeed, 500);
                }
                if (typedArray.hasValue(R.styleable.TriStateSwitch_thumbShape)) {
                    thumbShape = typedArray.getInteger(R.styleable.TriStateSwitch_thumbShape, 0);
                }

            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
            } finally {
                typedArray.recycle();
            }
        }

        init();

    }

    /**
     * Perform inflation from XML and apply a class-specific base style from a
     * theme attribute. This constructor of View allows subclasses to use their
     * own base style when they are inflating. For example, a Button class's
     * constructor would call this version of the super class constructor and
     * supply <code>R.attr.buttonStyle</code> for <var>defStyleAttr</var>; this
     * allows the theme's button style to modify all of the base view attributes
     * (in particular its background) as well as the Button class's attributes.
     *
     * @param context      The Context the view is running in, through which it can
     *                     access the current theme, resources, etc.
     * @param attrs        The attributes of the XML tag that is inflating the view.
     * @param defStyleAttr An attribute in the current theme that contains a
     *                     reference to a style resource that supplies default values for
     *                     the view. Can be 0 to not look for defaults.
     * @see #TriStateSwitch(Context, AttributeSet)
     */
    public TriStateSwitch(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        // super(context, attrs, defStyleAttr);
        this(context, attrs);
    }

    /**
     * Perform inflation from XML and apply a class-specific base style from a
     * theme attribute or style resource. This constructor of View allows
     * subclasses to use their own base style when they are inflating.
     * <p>
     * When determining the final value of a particular attribute, there are
     * four inputs that come into play:
     * <ol>
     * <li>Any attribute values in the given AttributeSet.
     * <li>The style resource specified in the AttributeSet (named "style").
     * <li>The default style specified by <var>defStyleAttr</var>.
     * <li>The default style specified by <var>defStyleRes</var>.
     * <li>The base values in this theme.
     * </ol>
     * <p>
     * Each of these inputs is considered in-order, with the first listed taking
     * precedence over the following ones. In other words, if in the
     * AttributeSet you have supplied <code>&lt;Button * textColor="#ff000000"&gt;</code>
     * , then the button's text will <em>always</em> be black, regardless of
     * what is specified in any of the styles.
     *
     * @param context      The Context the view is running in, through which it can
     *                     access the current theme, resources, etc.
     * @param attrs        The attributes of the XML tag that is inflating the view.
     * @param defStyleAttr An attribute in the current theme that contains a
     *                     reference to a style resource that supplies default values for
     *                     the view. Can be 0 to not look for defaults.
     * @param defStyleRes  A resource identifier of a style resource that
     *                     supplies default values for the view, used only if
     *                     defStyleAttr is 0 or can not be found in the theme. Can be 0
     *                     to not look for defaults.
     * @see #TriStateSwitch(Context, AttributeSet, int)
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public TriStateSwitch(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        // super(context, attrs, defStyleAttr, defStyleRes);
        this(context, attrs);
    }

    // ******************************************

    private void init() {
        outerViewShape = new RectF();
        thumbViewShape = new AnimatableRectF();

        viewPaint = new Paint();
        viewPaint.setColor(neutralColor);
        viewPaint.setStyle(Paint.Style.FILL);
        viewPaint.setAntiAlias(true);

        thumbPaint = new Paint();
        thumbPaint.setColor(thumbColor);
        thumbPaint.setStyle(Paint.Style.FILL);
        thumbPaint.setAntiAlias(true);

        defaultWidthDp = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, defaultWidthPx, getResources().getDisplayMetrics());
        defaultHeightDp = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, defaultHeightPx, getResources().getDisplayMetrics());

        // thumbPaint.setShadowLayer(24, 0, 0, Color.RED);
        // Important for certain APIs
        // setLayerType(LAYER_TYPE_SOFTWARE, thumbPaint);
    }

    // ******************************************
    // ****************************************** Re-implementation
    // ******************************************

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        Log.v("onMeasure w", MeasureSpec.toString(widthMeasureSpec));
        Log.v("onMeasure h", MeasureSpec.toString(heightMeasureSpec));

        /*
        Note: when overriding the onMeasure(), he call to super.onMeasure() should be removed
        and after doing the necessary measurements setMeasuredDimension(int width, int height)
        should be called.

        The widthMeasureSpec and heightMeasureSpec which are the requirements passed to us by the parent.

        See this for help: https://medium.com/@quiro91/custom-view-mastering-onmeasure-a0a0bb11784d
         */

        // width/height suggested by the platform
        int suggestedMinWidth = this.getSuggestedMinimumWidth();
        int suggestedMinHeight = this.getSuggestedMinimumHeight();

        // the following 4 lines are for understanding only
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSizeInPixels = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSizeInPixels = MeasureSpec.getSize(heightMeasureSpec);

        // WRAP_CONTENT and MATCH_PARENT are NOT allowed (will default to desiredWidth and desiredHeight)
        int desiredWidth = suggestedMinWidth + this.getPaddingLeft() + this.getPaddingRight();
        int desiredHeight = suggestedMinHeight + this.getPaddingTop() + this.getPaddingBottom();
        int measuredWidth = measureDimension(desiredWidth, widthMeasureSpec, WIDTH_PROPERTY);
        int measuredHeight = measureDimension(desiredHeight, heightMeasureSpec, HEIGHT_PROPERTY);
        setMeasuredDimension(measuredWidth, measuredHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Draw the outer shape
        if (outerViewShape.right == 0) {
            initViewShape();
        }
        canvas.drawRoundRect(outerViewShape,
                ((thumbShape == THUMB_SHAPE_RECTANGLE) ? rectangularViewCornerRadii : circularViewCornerRadii),
                ((thumbShape == THUMB_SHAPE_RECTANGLE) ? rectangularViewCornerRadii : circularViewCornerRadii),
                viewPaint);

        // Draw the thumb
        if (thumbViewShape.left == 0) {
            initThumbShape();
        }
        canvas.drawRoundRect(thumbViewShape,
                ((thumbShape == THUMB_SHAPE_RECTANGLE) ? rectangularThumbCornerRadii : circularThumbCornerRadii),
                ((thumbShape == THUMB_SHAPE_RECTANGLE) ? rectangularThumbCornerRadii : circularThumbCornerRadii),
                thumbPaint);

    }

    @Override
    protected void onFinishInflate() {  // OPTIONAL
        super.onFinishInflate();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {    // OPTIONAL
        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {    // OPTIONAL
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onAttachedToWindow() {   // OPTIONAL
        super.onAttachedToWindow();
    }

    @Override
    protected void onDetachedFromWindow() { // OPTIONAL
        super.onDetachedFromWindow();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // float initialX = 0;
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                // initialX = event.getX();
                break;
            case MotionEvent.ACTION_UP:
                // Log.d(TAG, String.format("rawX: %s, halfWidth: %s", event.getX(), this.getWidth() / 2));
                repositionThumb(event);
                break;
            case MotionEvent.ACTION_MOVE:
                /*float currentX = event.getX();
                if (currentX > initialX) {
                    thumbViewShape.offsetTo(currentX, thumbViewShape.top);
                    invalidate();
                    requestLayout();
                }
                initialX = currentX;*/
                break;

        }

        return true;
    }

    // ******************************************
    // ****************************************** Getters & Setters
    // ******************************************


    public ICallback getCallback() {
        return iCallback;
    }

    public void setCallback(ICallback iCallback) {
        this.iCallback = iCallback;
    }

    public SIDE getSide() {
        return side;
    }

    public void setSide(SIDE side) {
        this.side = side;

        int destinationX;
        switch (side) {
            case LEFT:
                destinationX = viewInnerPadding;
                break;
            case MIDDLE:
                destinationX = this.getMeasuredWidth() / 2;
                break;
            case RIGHT:
                destinationX = this.getMeasuredWidth() - viewInnerPadding;
                break;
            default:
                destinationX = 0;
                break;
        }

        MotionEvent motionEvent = MotionEvent.obtain(100, 100, MotionEvent.ACTION_UP, destinationX, thumbViewShape.centerY(), 0);
        repositionThumb(motionEvent);
    }

    public int getThumbColor() {
        return thumbColor;
    }

    public void setThumbColor(int thumbColor) {
        this.thumbColor = thumbColor;
        thumbPaint.setColor(thumbColor);
        invalidate();
    }

    public int getNeutralColor() {
        return neutralColor;
    }

    public void setNeutralColor(int neutralColor) {
        this.neutralColor = neutralColor;
        viewPaint.setColor(neutralColor);
        invalidate();
    }

    public int getLeftSideColor() {
        return leftSideColor;
    }

    public void setLeftSideColor(int color) {
        leftSideColor = color;
        viewPaint.setColor(neutralColor);
        invalidate();
    }

    public int getRightSideColor() {
        return rightSideColor;
    }

    public void setRightSideColor(int rightSideColor) {
        this.rightSideColor = rightSideColor;
        viewPaint.setColor(neutralColor);
        invalidate();
    }

    public Integer getThumbSpeed() {
        return thumbSpeed;
    }

    public void setThumbSpeed(Integer thumbSpeed) {
        this.thumbSpeed = thumbSpeed;
    }

    public Integer getThumbShape() {
        return thumbShape;
    }

    /**
     * Changes the shape of the thumb.
     * @param thumbShape the new shape, can be either TriStateSwitch.THUMB_SHAPE_RECTANGLE or TriStateSwitch.THUMB_SHAPE_CIRCLE
     */
    public void setThumbShape(Integer thumbShape, boolean animateTransformation) {
        // ToDo implement the transformation animation
        if (thumbShape == THUMB_SHAPE_RECTANGLE || thumbShape == THUMB_SHAPE_CIRCLE) {
            if (!this.thumbShape.equals(thumbShape)) {

                this.thumbShape = thumbShape;
                if (animateTransformation) {
                    animateViewShapeMorphing();
                } else {
                    invalidate();
                }
            }
        }
    }

    public int getShapeTransformationSpeed() {
        return shapeTransformationSpeed;
    }

    public void setShapeTransformationSpeed(int shapeTransformationSpeed) {
        this.shapeTransformationSpeed = shapeTransformationSpeed;
    }

    // ******************************************
    // ****************************************** Helper methods
    // ******************************************

    private int measureDimension(int desiredSize, int measureSpec, String property) {
        int result;

        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        /* The following code is applicable and valid for other different views,
            but for the current view we will force fixed dimensions to avoid problems */
        /*if (specMode == MeasureSpec.EXACTLY) {  // the user has chosen a fixed size, so use the suggested size
            result = specSize;
        } else {    // the user has chosen either match_parent or wrap_content
            result = desiredSize;
            if (specMode == MeasureSpec.AT_MOST) {
                result = Math.min(result, specSize);
            }
        }*/

        if (property.equals(WIDTH_PROPERTY)) {
            result = defaultWidthDp;
        } else {
            result = defaultHeightDp;
        }

        return result;
    }

    private void initViewShape() {
        Log.d(TAG, "initializing outer view coordinates...");
        outerViewShape.set(viewInnerPadding ,
                viewInnerPadding,
                this.getMeasuredWidth() - viewInnerPadding,
                this.getMeasuredHeight() - viewInnerPadding);
        circularViewCornerRadii = (int) outerViewShape.height() / 2;
        DEFAULT_CIRCULAR_VIEW_CORNER_RADII = circularViewCornerRadii;
    }

    private void initThumbShape() {
        Log.d(TAG, "initializing thumb coordinates...");
        // int thumbSize = (this.getMeasuredHeight() / 2) - padding;
        /*int thumbLeft = (this.getMeasuredWidth() / 2) - (thumbSize / 2);
        int thumbRight = thumbLeft + thumbSize;
        int thumbTop = (this.getMeasuredHeight() / 2) - (thumbSize / 2);
        int thumbBottom = thumbTop + thumbSize;*/

        int thumbLeft = this.getMeasuredWidth() / 3;
        int thumbRight = thumbLeft + (this.getMeasuredWidth() / 3);
        int thumbTop = 24;
        int thumbBottom = this.getMeasuredHeight() - 24;

        thumbViewShape.set(thumbLeft, thumbTop, thumbRight, thumbBottom);

        circularThumbCornerRadii = (int) thumbViewShape.width() / 2;
        DEFAULT_CIRCULAR_THUMB_CORNER_RADII = circularThumbCornerRadii;

    }

    private void repositionThumb(MotionEvent event) {
        float thumbWidth = thumbViewShape.width();
        if (event.getX() < (outerViewShape.width() / 3)) {
            // thumbViewShape.offsetTo(outerViewShape.left + viewInnerPadding, thumbViewShape.top);
            if (thumbViewShape.left > (outerViewShape.width() / 3)) {
                animateThumb(thumbViewShape.left,
                        (outerViewShape.left + viewInnerPadding),
                        thumbViewShape.right,
                        (outerViewShape.left + thumbWidth + viewInnerPadding),
                        SIDE.LEFT);
            }
        } else if (event.getX() > ((outerViewShape.width()/* - viewInnerPadding*/) - (outerViewShape.width() / 3))) {
            // thumbViewShape.offsetTo((outerViewShape.right - thumbViewShape.width() - viewInnerPadding), thumbViewShape.top);
            if (thumbViewShape.left < outerViewShape.centerX()) {
                animateThumb(thumbViewShape.left,
                        (outerViewShape.width() - thumbWidth),
                        thumbViewShape.right,
                        outerViewShape.width(),
                        SIDE.RIGHT);
            }
        } else {
            // thumbViewShape.offsetTo(((outerViewShape.width() / 2) - (thumbViewShape.width() / 2) + viewInnerPadding), thumbViewShape.top);
            if ((thumbViewShape.left < (outerViewShape.width() / 3)) || (thumbViewShape.left > outerViewShape.centerX())) {
                animateThumb(thumbViewShape.left,
                        (this.getMeasuredWidth() / 3),
                        thumbViewShape.right,
                        (this.getMeasuredWidth() - (this.getMeasuredWidth() / 3)),
                        SIDE.MIDDLE);
            }
        }
    }

    private void animateThumb(float startLeft, float endLeft, float startRight, float endRight, final SIDE side) {

        this.side = side;

        ObjectAnimator leftAnimator = ObjectAnimator.ofFloat(thumbViewShape, "left", startLeft, endLeft);
        ObjectAnimator rightAnimator = ObjectAnimator.ofFloat(thumbViewShape, "right", startRight, endRight);

        ObjectAnimator colorAnimator = ObjectAnimator.ofObject(viewPaint, "color", new ArgbEvaluator(), destinationColor(side));

        leftAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                // Invalidate the view to perform a re-draw.
                postInvalidate();
            }
        });

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(this.thumbSpeed);
        animatorSet.setInterpolator(new FastOutSlowInInterpolator());
        animatorSet.playTogether(leftAnimator, rightAnimator, colorAnimator);
        animatorSet.addListener(new AnimatorListenerAdapter() {

            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);

                // Deliver side change started event.
                if (iCallback != null) {
                    iCallback.onSideChangeStarted(side);
                }
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);

                // Deliver side change ended event.
                if (iCallback != null) {
                    iCallback.onSideChangeEnded(side);
                }
            }
        });
        animatorSet.start();
    }

    private void animateViewShapeMorphing() {
        Log.d(TAG, "animating view transformation");
        ValueAnimator viewRadiiAnimator = (thumbShape == THUMB_SHAPE_RECTANGLE)
                ? ValueAnimator.ofInt(DEFAULT_CIRCULAR_VIEW_CORNER_RADII, DEFAULT_RECTANGULAR_VIEW_CORNER_RADII)
                : ValueAnimator.ofInt(DEFAULT_RECTANGULAR_VIEW_CORNER_RADII, DEFAULT_CIRCULAR_VIEW_CORNER_RADII);
        viewRadiiAnimator.setDuration(shapeTransformationSpeed);
        viewRadiiAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                // Log.d(TAG, String.format("transformation update: %d", (int) animation.getAnimatedValue()));
                if (thumbShape == THUMB_SHAPE_RECTANGLE) {
                    rectangularViewCornerRadii = (int) animation.getAnimatedValue();
                    rectangularThumbCornerRadii = (int) animation.getAnimatedValue();
                } else {
                    circularViewCornerRadii = (int) animation.getAnimatedValue();
                    circularThumbCornerRadii = (int) animation.getAnimatedValue();
                }
                invalidate();
            }
        });

        /*ValueAnimator thumbRadiiAnimator = (thumbShape == THUMB_SHAPE_RECTANGLE)
                ? ValueAnimator.ofInt(DEFAULT_CIRCULAR_THUMB_CORNER_RADII, DEFAULT_RECTANGULAR_THUMB_CORNER_RADII)
                : ValueAnimator.ofInt(DEFAULT_RECTANGULAR_THUMB_CORNER_RADII, DEFAULT_CIRCULAR_THUMB_CORNER_RADII);*/
        viewRadiiAnimator.start();
    }

    private int destinationColor(SIDE side) {
        switch (side) {
            case LEFT:
                return leftSideColor;
            case MIDDLE:
                return neutralColor;
            case RIGHT:
                return rightSideColor;
            default:
                return 0;
        }
    }

    // ******************************************
    // ****************************************** Callbacks & Listeners
    // ******************************************

    public static abstract class ICallback {
        public abstract void onSideChangeEnded(SIDE side);
        public void onSideChangeStarted(SIDE side) {
            Log.d(TAG, String.format("onSideChangeStarted(%s)", side.name()));
        }
    }

    // ******************************************
    // ****************************************** Custom classes
    // ******************************************

    private class AnimatableRectF extends RectF{
        public AnimatableRectF() {
            super();
        }

        public AnimatableRectF(float left, float top, float right, float bottom) {
            super(left, top, right, bottom);
        }

        public AnimatableRectF(RectF r) {
            super(r);
        }

        public AnimatableRectF(Rect r) {
            super(r);
        }

        public void setTop(float top){
            this.top = top;
        }
        public void setBottom(float bottom){
            this.bottom = bottom;
        }
        public void setRight(float right){
            this.right = right;
        }
        public void setLeft(float left){
            this.left = left;
        }

    }
}
