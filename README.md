# flowLayout
自动换行view，实现adapter模式，可限制行数，来个图先

<img src="http://img.blog.csdn.net/20170615151116893?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvbGFucWlfeA==/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/Center" alt="" /><br />

# 方法：
<table border="1">
<tr>
<td>方法名</td>
<td>描述</td>	
<td>所属类</td>	
</tr>
<tr>
<td>setHorizontalSpacing(int spacing)</td>
<td>设置每行view之间的水平间距</td>	
<td>FlowLayout</td>	
</tr>
<tr>
<td>setVerticalSpacing(int spacing)</td>
<td>设置行之间的间距</td>	
<td>FlowLayout</td>	
</tr>
<tr>
<td>setSurplusSpacingMode(int surplusSpacingMode)</td>
<td>设置每行剩余空间的分配方式</td>	
<td>FlowLayout</td>	
</tr>
<tr>
<td>setMaxLines(int count)</td>
<td>设置最大显示行数</td>	
<td>FlowLayout</td>	
</tr>
<tr>
<td>setAdapter(FlowAdapter mAdapter)</td>
<td>设置适配器</td>	
<td>FlowLayout</td>	
</tr>
<tr>
<td>deleteAdapter()</td>
<td>删除适配器</td>	
<td>FlowLayout</td>	
</tr>
<tr>
<td>onCreateViewHolder(FlowLayout flowLayout, int viewType)</td>
<td>创建View，其实暂未这里未提供ViewHolder，所以这里返回的是view</td>	
<td>FlowAdapter</td>	
</tr>
<tr>
<td>onBindViewHolder(T view, int position)</td>
<td>绑定数据，这里的T为onCreateViewHolder方法返回的view</td>	
<td>FlowAdapter</td>	
</tr>
<tr>
<td>notifyItemChanged(int position)</td>
<td>更新指定view的数据，用法跟recycle的adapter一致</td>	
<td>FlowAdapter</td>	
</tr>
<tr>
<td>notifyItemInserted(int position)</td>
<td>指定位置插入view，用法跟recycle的adapter一致</td>	
<td>FlowAdapter</td>	
</tr>
<tr>
<td>notifyItemRemoved(int position)</td>
<td>删除指定位置的view，用法跟recycle的adapter一致</td>	
<td>FlowAdapter</td>	
</tr>
</table>


# Attributes属性
<table border="1">
<tr>
<td>Attributes</td>
<td>描述</td>	
<td>forma</td>	
</tr>
<tr>
<td>surplusSpacingMode</td>
<td>每行剩余空间的分配模式，SURPLUSSPACINGMODE_AUTO不处理，SURPLUSSPACINGMODE_SHARE平均分配到每个view的宽度，SURPLUSSPACINGMODE_SPACE分配到水平间距</td>	
<td>enum</td>	
</tr>
<tr>
<td>horizontalSpacing</td>
<td>水平间距</td>	
<td>dimension</td>	
</tr>

<td>verticalSpacing</td>
<td>垂直间距</td>	
<td>dimension</td>	
</tr>

<td>maxLines</td>
<td>每行剩余空间的分配模式，SURPLUSSPACINGMODE_AUTO不处理，SURPLUSSPACINGMODE_SHARE平均分配到每个view的宽度，SURPLUSSPACINGMODE_SPACE分配到水平间距</td>	
<td>integer</td>	
</tr>

</table>
