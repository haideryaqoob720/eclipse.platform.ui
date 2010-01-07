/**
 * Copyright (c) 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *      IBM Corporation - initial API and implementation
 */
package org.eclipse.e4.ui.model.application;

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Part Descriptor Container</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.e4.ui.model.application.MPartDescriptorContainer#getDescriptors <em>Descriptors</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.e4.ui.model.application.MApplicationPackage#getPartDescriptorContainer()
 * @model
 * @generated
 */
public interface MPartDescriptorContainer {
	/**
	 * Returns the value of the '<em><b>Descriptors</b></em>' containment reference list.
	 * The list contents are of type {@link org.eclipse.e4.ui.model.application.MPartDescriptor}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Descriptors</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Descriptors</em>' containment reference list.
	 * @see org.eclipse.e4.ui.model.application.MApplicationPackage#getPartDescriptorContainer_Descriptors()
	 * @model containment="true"
	 * @generated
	 */
	EList<MPartDescriptor> getDescriptors();

} // MPartDescriptorContainer
