package org.eclipse.jface.viewers;

/*
 * (c) Copyright IBM Corp. 2000, 2001.
 * All Rights Reserved.
 */
import org.eclipse.jface.util.Assert;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.widgets.*;
import java.util.*;
import java.util.List; // disambiguate from SWT List

/**
 * A layout for a table.
 * Call <code>addColumnData</code> to add columns.
 */
public class TableLayout extends Layout {

	/**
	 * The list of column layout data (element type:
	 *  <code>ColumnLayoutData</code>).
	 */
	private List columns = new ArrayList();

	/**
	 * Indicates whether <code>layout</code> has yet to be called.
	 */
	private boolean firstTime = true;
/**
 * Creates a new table layout.
 */
public TableLayout() {
}
/**
 * Adds a new column of data to this table layout.
 *
 * @param data the column layout data
 */
public void addColumnData(ColumnLayoutData data) {
	columns.add(data);
}
/* (non-Javadoc)
 * Method declared on Layout.
 */
public Point computeSize(Composite c, int wHint, int hHint, boolean flush) {
	if (wHint != SWT.DEFAULT && hHint != SWT.DEFAULT)
		return new Point(wHint, hHint);

	Table table = (Table) c;
	// To avoid recursions.
	table.setLayout(null);
	// Use native layout algorithm
	Point result = table.computeSize(wHint, hHint, flush);
	table.setLayout(this);

	int width = 0;
	int size = columns.size();
	for (int i = 0; i < size; ++i) {
		ColumnLayoutData layoutData = (ColumnLayoutData) columns.get(i);
		if (layoutData instanceof ColumnPixelData) {
			ColumnPixelData col = (ColumnPixelData) layoutData;
			width += col.width;
		}
		else if (layoutData instanceof ColumnWeightData) {
			ColumnWeightData col = (ColumnWeightData) layoutData;
			width += col.minimumWidth;
		} else {
			Assert.isTrue(false, "Unknown column layout data");//$NON-NLS-1$
		}
	}
	if (width > result.x)
		result.x = width;
	return result;
}
/* (non-Javadoc)
 * Method declared on Layout.
 */
public void layout(Composite c, boolean flush) {
	if (firstTime) {
		firstTimeLayout(c,flush);
	} else {
		lastColumnLayout(c);
	}
}

/*
 * Layout the table according to its columns layout data.
 */
private void firstTimeLayout(Composite c, boolean flush) {	
	Table table = (Table) c;
	int width = table.getClientArea().width;

	// XXX: Layout is being called with an invalid value the first time
	// it is being called on Linux. This method resets the
	// Layout to null so we make sure we run it only when
	// the value is OK.
	if (width <= 1)
		return;
 
	TableColumn[] tableColumns = table.getColumns();
	int size = Math.min(columns.size(), tableColumns.length);
	int[] widths = new int[size];
	int fixedWidth = 0;
	int numberOfWeightColumns = 0;
	int totalWeight = 0;

	// First calc space occupied by fixed columns
	for (int i = 0; i < size; i++) {
		ColumnLayoutData col = (ColumnLayoutData) columns.get(i);
		if (col instanceof ColumnPixelData) {
			int pixels = ((ColumnPixelData) col).width;
			widths[i] = pixels;
			fixedWidth += pixels;
		} else if (col instanceof ColumnWeightData) {
			ColumnWeightData cw = (ColumnWeightData) col;
			numberOfWeightColumns++;
			// first time, use the weight specified by the column data, otherwise use the actual width as the weight
			// int weight = firstTime ? cw.weight : tableColumns[i].getWidth();
			int weight = cw.weight;
			totalWeight += weight;
		} else {
			Assert.isTrue(false, "Unknown column layout data");//$NON-NLS-1$
		}
	}

	// Do we have columns that have a weight
	if (numberOfWeightColumns > 0) {
		// Now distribute the rest to the columns with weight.
		int rest = width - fixedWidth;
		int totalDistributed = 0;
		for (int i = 0; i < size; ++i) {
			ColumnLayoutData col = (ColumnLayoutData) columns.get(i);
			if (col instanceof ColumnWeightData) {
				ColumnWeightData cw = (ColumnWeightData) col;
				// calculate weight as above
				// int weight = firstTime ? cw.weight : tableColumns[i].getWidth();
				int weight = cw.weight;
				int pixels = totalWeight == 0 ? 0 : weight * rest / totalWeight;
				if (pixels < cw.minimumWidth)
					pixels = cw.minimumWidth;
				totalDistributed += pixels;
				widths[i] = pixels;
			}
		}

		// Distribute any remaining pixels to columns with weight.
		int diff = rest - totalDistributed;
		for (int i = 0; diff > 0; ++i) {
			if (i == size)
				i = 0;
			ColumnLayoutData col = (ColumnLayoutData) columns.get(i);
			if (col instanceof ColumnWeightData) {
				++widths[i];
				--diff;
			}
		}
	}

//	System.out.print("Width: " + width);
//	int total = 0;
//	for (int i = 0; i < widths.length; i++) {
//		System.out.print(" " + widths[i]);
//		total += widths[i];
//	}
//	System.out.println(" Total: " + total);

	final ControlListener controlListener = new ControlListener() {
		public void controlMoved(ControlEvent e){}
		public void controlResized(ControlEvent e) {
			columnResized(e);
		}
	};
	DisposeListener disposeListener = new DisposeListener() {
		public void widgetDisposed(DisposeEvent e) {
			((TableColumn)e.widget).removeControlListener(controlListener);
		}
	};
	
	for (int i = 0; i < size; i++) {
		tableColumns[i].addDisposeListener(disposeListener);
		tableColumns[i].addControlListener(controlListener);
		tableColumns[i].setWidth(widths[i]);
	}
	firstTime = false;
}
/*
 * Make sure the last column uses all the client area.
 */
private void lastColumnLayout(Composite c) {
	Table table = (Table) c;
	int width = table.getClientArea().width;
	int columnCount = table.getColumnCount();
	for(int i=0;i<columnCount;i++) {
		width = width - table.getColumn(i).getWidth();
	}
	if(width > 0) {
		TableColumn column = table.getColumn(columnCount - 1);
		column.setWidth(column.getWidth() + width);
	}
}

/*
 * Make sure the columns are not smaller the minimun size.
 */
private void columnResized(ControlEvent e) {
	if(firstTime)
		return;
	TableColumn column = (TableColumn) e.widget;
	Table table = column.getParent();
	//Find the index of column
	int i = 0;
	int columnCount = table.getColumnCount();
	for (; i < columnCount; i++) {
		if (column == table.getColumn(i))
					break;
	}
	ColumnLayoutData layoutData = (ColumnLayoutData)columns.get(i);
	if (column.getWidth() < layoutData.minimumWidth)
		column.setWidth(layoutData.minimumWidth);
	lastColumnLayout(table);
}
}
