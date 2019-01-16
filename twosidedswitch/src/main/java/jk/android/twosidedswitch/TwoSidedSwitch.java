package jk.android.twosidedswitch;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class TwoSidedSwitch extends View {

    // ******************************************

    private final String TAG = TwoSidedSwitch.class.getSimpleName();

    private int desiredWidth = 100;
    private int desiredHeight = 40;

    private boolean showLabel;
    private Integer thumbShape;

    private RectF outerViewShape, thumbViewShape;
    private Paint viewPaint, thumbPaint;

    private int padding = 8;

    private int viewCornerRadii = 48;
    private int thumbCornerRadii = 16;

    // ******************************************

    /**
     * Simple constructor to use when creating a view from code.
     *
     * @param context The Context the view is running in, through which it can
     *                access the current theme, resources, etc.
     */
    public TwoSidedSwitch(Context context) {
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
     * @see #TwoSidedSwitch(Context, AttributeSet, int)
     */
    public TwoSidedSwitch(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        // setWillNotDraw(false);   // used only when the parent is a ViewGroup (like LinearLayout, RelativeLayout, ....)

        if (attrs != null) {
            TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.TwoSidedSwitch, 0, 0);
            try {
                showLabel = typedArray.getBoolean(R.styleable.TwoSidedSwitch_showLabel, false);
                thumbShape = typedArray.getInteger(R.styleable.TwoSidedSwitch_thumbShape, 1);
            } catch (Exception e) {
                Log.d(TAG, e.getMessage());
            } finally {
                typedArray.recycle();
            }
        }

        outerViewShape = new RectF(padding ,
                padding,
                this.getMeasuredWidth() - padding,
                this.getMeasuredHeight() - padding); this is not working

        if (thumbShape == 0) {
            thumbViewShape = createThumbShape();
        }

        viewPaint = new Paint();
        viewPaint.setColor(Color.GRAY);
        viewPaint.setStyle(Paint.Style.FILL);
        viewPaint.setAntiAlias(true);

        thumbPaint = new Paint();
        thumbPaint.setColor(Color.GREEN);
        thumbPaint.setStyle(Paint.Style.FILL);
        thumbPaint.setAntiAlias(true);

        // thumbPaint.setShadowLayer(24, 0, 0, Color.RED);
        // Important for certain APIs
        // setLayerType(LAYER_TYPE_SOFTWARE, thumbPaint);

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
     * @see #TwoSidedSwitch(Context, AttributeSet)
     */
    public TwoSidedSwitch(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
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
     * @see #TwoSidedSwitch(Context, AttributeSet, int)
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public TwoSidedSwitch(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        // super(context, attrs, defStyleAttr, defStyleRes);
        this(context, attrs);
    }

    // ******************************************
    // ****************************************** Re-implementation
    // ******************************************

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        /*
        Note: when overriding the onMeasure(), he call to super.onMeasure() should be removed
        and after doing the necessary measurements setMeasuredDimension(int width, int height)
        should be called.
         */

        // width/height suggested by the platform
        int suggestedMinWidth = this.getSuggestedMinimumWidth();
        int suggestedMinHeight = this.getSuggestedMinimumHeight();

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSizeInPixels = MeasureSpec.getSize(widthMeasureSpec);

        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSizeInPixels = MeasureSpec.getSize(heightMeasureSpec);


        // ToDo
        // Override onMeasure to restrict the size of the custom view
        // WRAP_CONTENT and MATCH_PARENT are NOT allowed
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // draw the outer shape
        canvas.drawRoundRect(outerViewShape,
                viewCornerRadii,
                viewCornerRadii,
                viewPaint);

        // draw the thumb
        if (thumbShape == 0) {  // draw a rectangular thumb
            canvas.drawRoundRect(thumbViewShape,
                    thumbCornerRadii,
                    thumbCornerRadii,
                    thumbPaint);
        } else {    // draw a circular thumb
            canvas.drawCircle(this.getMeasuredWidth() / 2,
                    this.getMeasuredHeight() / 2,
                    (this.getMeasuredHeight() / 2) - 24,
                    thumbPaint);
        }

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

    // ******************************************
    // ****************************************** Helper methods
    // ******************************************

    private RectF createThumbShape() {
        int thumbSize = (this.getMeasuredHeight() / 2) - padding;
        int thumbLeft = (this.getMeasuredWidth() / 2) - (thumbSize / 2);
        int thumbRight = thumbLeft + thumbSize;
        int thumbTop = (this.getMeasuredHeight() / 2) - (thumbSize / 2);
        int thumbBottom = thumbTop + thumbSize;

        return new RectF(thumbLeft, thumbTop, thumbRight, thumbBottom);
    }
}
