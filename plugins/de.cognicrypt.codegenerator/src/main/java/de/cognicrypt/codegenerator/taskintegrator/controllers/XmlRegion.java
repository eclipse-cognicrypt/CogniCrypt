/********************************************************************************
 * Copyright (c) 2015-2018 TU Darmstadt
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/

/****************************************************************************
 *
 * Copyright (c) 2012, Vincent Zurczak - All rights reserved.
 * This source file is released under the terms of the BSD license.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the University of California, Berkeley nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE REGENTS AND CONTRIBUTORS ``AS IS'' AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE REGENTS AND CONTRIBUTORS BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 *****************************************************************************/

package de.cognicrypt.codegenerator.taskintegrator.controllers;

/**
 * A XML region, with a type, a start position (included) and an end position (excluded).
 * <p>
 * A XML region is limited in the range [start, end[
 * </p>
 *
 * @author Vincent Zurczak
 * @version 1.0 (tag version)
 */
public class XmlRegion {

	public enum XmlRegionType {
		INSTRUCTION, COMMENT, CDATA, MARKUP, ATTRIBUTE, MARKUP_VALUE, ATTRIBUTE_VALUE, WHITESPACE, UNEXPECTED;
	}

	private final XmlRegionType xmlRegionType;
	private final int start;
	private int end;

	/**
	 * Constructor.
	 * 
	 * @param xmlRegionType
	 * @param start
	 */
	public XmlRegion(XmlRegionType xmlRegionType, int start) {
		this.xmlRegionType = xmlRegionType;
		this.start = start;
	}

	/**
	 * Constructor.
	 * 
	 * @param xmlRegionType
	 * @param start
	 * @param end
	 */
	public XmlRegion(XmlRegionType xmlRegionType, int start, int end) {
		this(xmlRegionType, start);
		this.end = end;
	}

	/**
	 * @return the end
	 */
	public int getEnd() {
		return this.end;
	}

	/**
	 * @param end
	 *        the end to set
	 */
	public void setEnd(int end) {
		this.end = end;
	}

	/**
	 * @return the xmlRegionType
	 */
	public XmlRegionType getXmlRegionType() {
		return this.xmlRegionType;
	}

	/**
	 * @return the start
	 */
	public int getStart() {
		return this.start;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object #toString()
	 */
	@Override
	public String toString() {
		return this.xmlRegionType + " [" + this.start + ", " + this.end + "[";
	}
}
