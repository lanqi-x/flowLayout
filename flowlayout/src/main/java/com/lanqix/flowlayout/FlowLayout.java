package com.lanqix.flowlayout;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lanqi on 2017/6/3.
 */
public class FlowLayout extends ViewGroup {
    private int DEFAULT_SPACING;
    /**
     * 横向间隔
     */
    private int mHorizontalSpacing;
    /**
     * 纵向间隔
     */
    private int mVerticalSpacing;
    /**
     * 是否需要布局，只用于第一次
     */
    boolean mNeedLayout = true;
    /**
     * 当前行已用的宽度，由子View宽度加上横向间隔
     */
    private int mUsedWidth = 0;
    /**
     * 代表每一行的集合
     */
    private final List<Line> mLines = new ArrayList<>();
    private Line mLine = null;

    /**
     * 每行剩余空间的分配模式（SURPLUSSPACINGMODE_AUTO不处理，在最右边留空白，
     * SURPLUSSPACINGMODE_SHARE平分到每个子View的宽度，
     * SURPLUSSPACINGMODE_SPACE平分到每个子View的外边距）
     */
    private int surplusSpacingMode;

    private Context context;
    /**
     * 最大的行数
     */
    private int mMaxLinesCount = Integer.MAX_VALUE;

    public final static int SURPLUSSPACINGMODE_AUTO = 0;
    public final static int SURPLUSSPACINGMODE_SHARE = 1;
    public final static int SURPLUSSPACINGMODE_SPACE = 2;

    protected FlowAdapter mAdapter;

    public FlowLayout(Context context) {
        super(context);
        init(context, null);
    }

    public int dp2px(float dp) {
        float density = getResources().getDisplayMetrics().density;
        return (int) (density * dp + 0.5);
    }

    public FlowLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public FlowLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        this.context = context;
        DEFAULT_SPACING = dp2px(10);

        if (attrs != null) {
            TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.FlowLayout);
            mMaxLinesCount = ta.getInt(R.styleable.FlowLayout_maxLines, Integer.MAX_VALUE);
            mHorizontalSpacing = ta.getDimensionPixelSize(R.styleable.FlowLayout_horizontalSpacing, DEFAULT_SPACING);
            mVerticalSpacing = ta.getDimensionPixelSize(R.styleable.FlowLayout_verticalSpacing, DEFAULT_SPACING);
            surplusSpacingMode = ta.getInt(R.styleable.FlowLayout_surplusSpacingMode, SURPLUSSPACINGMODE_AUTO);
            ta.recycle();
        } else {
            mHorizontalSpacing = DEFAULT_SPACING;
            mVerticalSpacing = DEFAULT_SPACING;
        }
    }

    public void setHorizontalSpacing(int spacing) {
        if (mHorizontalSpacing != spacing) {
            mHorizontalSpacing = spacing;
            requestLayoutInner();
        }
    }

    public void setSurplusSpacingMode(int surplusSpacingMode) {
        switch (surplusSpacingMode) {
            case SURPLUSSPACINGMODE_AUTO:
            case SURPLUSSPACINGMODE_SHARE:
                if (this.surplusSpacingMode != surplusSpacingMode) {
                    this.surplusSpacingMode = surplusSpacingMode;
                    requestLayoutInner();
                }
                break;
        }
    }

    public int getSurplusSpacingMode() {
        return surplusSpacingMode;
    }

    public void setVerticalSpacing(int spacing) {
        if (mVerticalSpacing != spacing) {
            mVerticalSpacing = spacing;
            requestLayoutInner();
        }
    }

    public void setMaxLines(int count) {
        if (mMaxLinesCount != count) {
            mMaxLinesCount = count;
            requestLayoutInner();
        }
    }

    public int getmMaxLines() {
        return mMaxLinesCount;
    }

    private Handler handler;

    @Override
    public Handler getHandler() {
        if (handler == null) {
            handler = new Handler(context.getMainLooper());
        }
        return handler;
    }


    private void requestLayoutInner() {
        getHandler().post(new Runnable() {
            @Override
            public void run() {
                requestLayout();
            }
        });
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int sizeWidth = MeasureSpec.getSize(widthMeasureSpec)
                - getPaddingRight() - getPaddingLeft();
        int sizeHeight = MeasureSpec.getSize(heightMeasureSpec)
                - getPaddingTop() - getPaddingBottom();

        int modeWidth = MeasureSpec.getMode(widthMeasureSpec);
        int modeHeight = MeasureSpec.getMode(heightMeasureSpec);

        restoreLine();// 还原数据，以便重新记录
        final int count = getChildCount();
        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            if (child.getVisibility() == GONE) {
                continue;
            }
            int childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(sizeWidth,
                    modeWidth == MeasureSpec.EXACTLY ? MeasureSpec.AT_MOST
                            : modeWidth);
            int childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(
                    sizeHeight,
                    modeHeight == MeasureSpec.EXACTLY ? MeasureSpec.AT_MOST
                            : modeHeight);
            // 测量child
            child.measure(childWidthMeasureSpec, childHeightMeasureSpec);

            if (mLine == null) {
                mLine = new Line();
            }
            int childWidth = child.getMeasuredWidth();
            mUsedWidth += childWidth;// 增加使用的宽度
            if (mUsedWidth <= sizeWidth) {// 使用宽度小于总宽度，该child属于这一行。
                mLine.addView(child);// 添加child
                mUsedWidth += mHorizontalSpacing;// 加上间隔
                if (mUsedWidth >= sizeWidth) {// 加上间隔后如果大于等于总宽度，需要换行
                    if (!newLine()) {
                        break;
                    }
                }
            } else {// 使用宽度大于总宽度。需要换行
                // 如果这行一个child都没有，那么就加上去，以保证每行都有至少有一个child
                if (mLine.getViewCount() == 0) {
                    mLine.addView(child);// 添加child
                    if (!newLine()) {// 换行
                        break;
                    }
                } else {// 如果该行有数据了，就直接换行
                    if (!newLine()) {// 换行
                        break;
                    }
                    // 在新的一行，因为这一行一个child都没有，先加上去，以保证每行都有至少有一个child
                    mLine.addView(child);
                    mUsedWidth += childWidth + mHorizontalSpacing;
                }
            }
        }

        if (mLine != null && mLine.getViewCount() > 0
                && !mLines.contains(mLine)) {
            // 由于前面采用判断长度是否超过最大宽度来决定是否换行，则最后一行可能因为还没达到最大宽度，所以需要验证后加入集合中
            mLines.add(mLine);
        }

        int totalWidth = MeasureSpec.getSize(widthMeasureSpec);
        int totalHeight = 0;
        final int linesCount = mLines.size();
        for (int i = 0; i < linesCount; i++) {// 加上所有行的高度
            totalHeight += mLines.get(i).mHeight;
        }
        totalHeight += mVerticalSpacing * (linesCount - 1);// 加上所有间隔的高度
        totalHeight += getPaddingTop() + getPaddingBottom();// 加上padding
        // 设置布局的宽高，宽度直接采用父view传递过来的最大宽度，而不用考虑子view是否填满宽度，因为该布局的特性就是填满一行后，再换行
        // 高度根据设置的模式来决定采用所有子View的高度之和还是采用父view传递过来的高度
        setMeasuredDimension(totalWidth,
                resolveSize(totalHeight, heightMeasureSpec));
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (!mNeedLayout || changed) {// 没有发生改变就不重新布局
            mNeedLayout = false;
            int left = getPaddingLeft();// 获取最初的左上点
            int top = getPaddingTop();
            final int linesCount = mLines.size();
            for (int i = 0; i < linesCount; i++) {
                final Line oneLine = mLines.get(i);
                oneLine.layoutView(left, top);// 布局每一行
                top += oneLine.mHeight + mVerticalSpacing;// 为下一行的top赋值
            }
        }
    }

    /**
     * 还原所有数据
     */
    private void restoreLine() {
        mLines.clear();
        mLine = new Line();
        mUsedWidth = 0;
    }

    /**
     * 新增加一行
     */
    private boolean newLine() {
        mLines.add(mLine);
        if (mLines.size() < mMaxLinesCount) {
            mLine = new Line();
            mUsedWidth = 0;
            return true;
        }
        return false;
        // ==========================================================================
    }

    // Inner/Nested Classes
    // ==========================================================================

    /**
     * 代表着一行，封装了一行所占高度，该行子View的集合，以及所有View的宽度总和
     */
    class Line {
        int mWidth = 0;// 该行中所有的子View累加的宽度
        int mHeight = 0;// 该行中所有的子View中高度最高的那个子View的高度
        List<View> views = new ArrayList<>();

        public void addView(View view) {// 往该行中添加一个
            views.add(view);
            mWidth += view.getMeasuredWidth();
            int childHeight = view.getMeasuredHeight();
            mHeight = mHeight < childHeight ? childHeight : mHeight;// 高度等于一行中最高的View
        }

        public int getViewCount() {
            return views.size();
        }

        public void layoutView(int l, int t) {// 布局
            int left = l;
            int top = t;
            int count = getViewCount();
            // 总宽度
            int layoutWidth = getMeasuredWidth() - getPaddingLeft()
                    - getPaddingRight();
            // 剩余的宽度，是除了View和间隙的剩余空间
            int surplusWidth = layoutWidth - mWidth - mHorizontalSpacing
                    * (count - 1);
//            刚好一行，或者有剩余空间
            if (surplusWidth >= 0) {// 剩余空间
                int splitSpacing = 0;
                if (FlowLayout.this.surplusSpacingMode == SURPLUSSPACINGMODE_SHARE) {
                    splitSpacing = (int) (surplusWidth / count + 0.5);
                } else if (FlowLayout.this.surplusSpacingMode == SURPLUSSPACINGMODE_SPACE) {
                    splitSpacing = (int) (surplusWidth / (count - 1) + 0.5);
                }
                for (int i = 0; i < count; i++) {
                    final View view = views.get(i);
                    int childWidth = view.getMeasuredWidth();
                    int childHeight = view.getMeasuredHeight();
                    // 计算出每个View的顶点，是由最高的View和该View高度的差值除以2
                    int topOffset = (int) ((mHeight - childHeight) / 2.0 + 0.5);
                    if (topOffset < 0) {
                        topOffset = 0;
                    }
                    if (FlowLayout.this.surplusSpacingMode == SURPLUSSPACINGMODE_SHARE) {
//                     把剩余空间平均到每个View上
                        childWidth = childWidth + splitSpacing;
                        view.getLayoutParams().width = childWidth;
                        if (splitSpacing > 0) {// View的长度改变了，需要重新measure
                            int widthMeasureSpec = MeasureSpec.makeMeasureSpec(
                                    childWidth, MeasureSpec.EXACTLY);
                            int heightMeasureSpec = MeasureSpec.makeMeasureSpec(
                                    childHeight, MeasureSpec.EXACTLY);
                            view.measure(widthMeasureSpec, heightMeasureSpec);
                        }
                    } else if (FlowLayout.this.surplusSpacingMode == SURPLUSSPACINGMODE_SPACE) {
                        if (i != 0) {
                            left += splitSpacing;
                        }
                    }
                    // 布局View
                    view.layout(left, top + topOffset, left + childWidth, top
                            + topOffset + childHeight);
                    left += childWidth + mHorizontalSpacing; // 为下一个View的left赋值
                }
            } else {
//                子View的宽大于FlowLayout的宽度
                if (count == 1) {
                    View view = views.get(0);
                    view.layout(left, top, left + view.getMeasuredWidth(), top
                            + view.getMeasuredHeight());
                } else {
                    // 如果运行到这来，那么就是出现bug了
                }
            }
        }
    }

    public void setAdapter(FlowAdapter mAdapter) {
        deleteAdapter();
        this.mAdapter = mAdapter;
        if (this.observer == null) {
            this.observer = new FlowObserver();
        }
        this.mAdapter.registerAdapterDataObserver(this.observer);
        addViewFormAdapter();
    }

    public void deleteAdapter() {
        if (this.mAdapter != null && this.observer != null) {
            try {
                this.mAdapter.unregisterAdapterDataObserver(observer);
            } catch (Exception e) {
            }
        }
        this.mAdapter = null;
    }


    private FlowObserver observer;

    private void addViewFormAdapter() {
        removeAllViews();
        for (int i = 0; i < mAdapter.getItemCount(); i++) {
            View view = mAdapter.onCreateViewHolder(FlowLayout.this, mAdapter.getItemViewType(i));
            mAdapter.onBindViewHolder(view, i);
            addView(view);
        }
    }

    private class FlowObserver extends FlowDataSetObserver {
        @Override
        public void onChanged() {
            super.onChanged();
            addViewFormAdapter();
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount) {
            super.onItemRangeChanged(positionStart, itemCount);
            int position = positionStart;
            for (int i = 0; i < itemCount; i++) {
                if (position >= mAdapter.getItemCount()) {
                    throw new IllegalArgumentException("数组越界,改变位置为" + position
                            + "childCount=" + getChildCount());
                }
                mAdapter.onBindViewHolder(getChildAt(position), position);
                ++position;
            }
        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            super.onItemRangeInserted(positionStart, itemCount);
            int position = positionStart;
            for (int i = 0; i < itemCount; i++) {
                if (position >= mAdapter.getItemCount()) {
                    throw new IllegalArgumentException("数组越界，插入位置为" + position
                            + "adapter.getItemCount()=" + mAdapter.getItemCount());
                }
                View view = mAdapter.onCreateViewHolder(FlowLayout.this, mAdapter.getItemViewType(position));
                mAdapter.onBindViewHolder(view, position);
                addView(view, position);
                ++position;
            }
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            super.onItemRangeRemoved(positionStart, itemCount);
            int position = positionStart;
            for (int i = 0; i < itemCount; i++) {
                if (position >= getChildCount()) {
                    throw new IllegalArgumentException("数组越界，移除位置为" + position
                            + "childCount=" + getChildCount());
                }
                removeViewAt(position);
                ++position;
            }
        }
    }
}
