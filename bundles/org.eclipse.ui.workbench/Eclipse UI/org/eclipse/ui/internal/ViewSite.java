/*******************************************************************************
 * Copyright (c) 2000, 2003 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.ui.internal;

import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.internal.registry.IViewDescriptor;
import org.eclipse.ui.part.ViewPart;

/**
 * A view container manages the services for a view.
 */
public class ViewSite extends PartSite
	implements IViewSite
{
/**
 * Creates a new ViewSite.
 */
public ViewSite(IViewPart view, WorkbenchPage page, IViewDescriptor desc) {
	super(view, page);
	setConfigurationElement(desc.getConfigurationElement());
}
/**
 * Returns the view.
 */
public IViewPart getViewPart() {
	return (IViewPart)getPart();
}
	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchPartSite#progressEnd(org.eclipse.core.runtime.jobs.Job)
	 */
	public void progressEnd(Job job) {
		PartPane pane = getPane();
		if(pane instanceof ViewPane)
			((ViewPane) pane).progressEnd(job);
		
		IViewPart viewPart = getViewPart();
		if(viewPart instanceof ViewPart)
			((ViewPart) viewPart).progressEnd(job);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchPartSite#progressStart(org.eclipse.core.runtime.jobs.Job)
	 */
	public void progressStart(Job job) {
		PartPane pane = getPane();
		if(pane instanceof ViewPane)
			((ViewPane) pane).progressStart(job);
		
		IViewPart viewPart = getViewPart();
		if(viewPart instanceof ViewPart)
			((ViewPart) viewPart).progressStart(job);
	}

}
