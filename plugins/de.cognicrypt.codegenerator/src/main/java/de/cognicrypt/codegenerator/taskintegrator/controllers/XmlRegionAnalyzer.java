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

import java.util.ArrayList;
import java.util.List;

import de.cognicrypt.codegenerator.taskintegrator.controllers.XmlRegion.XmlRegionType;

/**
 * A class that builds style ranges from a XML input.
 * 
 * @author Vincent Zurczak
 * @version 1.0 (tag version)
 */
public class XmlRegionAnalyzer {

	private int offset;

	/**
	 * Analyzes a XML document.
	 * 
	 * @param xml
	 *        the XML text (may be an invalid XML document)
	 * @return a non-null list of XML positions
	 */
	public List<XmlRegion> analyzeXml(String xml) {

		this.offset = 0;
		List<XmlRegion> positions = new ArrayList<XmlRegion>();
		while (this.offset < xml.length()) {

			// White spaces
			analyzeWhitespaces(xml, positions);
			if (this.offset >= xml.length())
				break;

			// "<" can be several things
			char c = xml.charAt(this.offset);
			if (c == '<') {
				if (analyzeInstruction(xml, positions))
					continue;
				if (analyzeComment(xml, positions))
					continue;
				if (analyzeMarkup(xml, positions))
					continue;
				if (analyzeCData(xml, positions))
					continue;

				positions.add(new XmlRegion(XmlRegionType.UNEXPECTED, this.offset, xml.length()));
				break;
			}

			// "/" and "/>" can only indicate a mark-up
			else if (c == '/' && xml.charAt(this.offset + 1) == '>' || c == '>') {
				if (analyzeMarkup(xml, positions))
					continue;

				positions.add(new XmlRegion(XmlRegionType.UNEXPECTED, this.offset, xml.length()));
				break;
			}

			// Other things can be...
			if (analyzeAttribute(xml, positions))
				continue;
			if (analyzeAttributeValue(xml, positions))
				continue;
			if (analyzeMarkupValue(xml, positions))
				continue;

			positions.add(new XmlRegion(XmlRegionType.UNEXPECTED, this.offset, xml.length()));
			break;
		}

		return positions;
	}

	/**
	 * Tries to analyze a XML instruction.
	 * 
	 * @param xml
	 *        the XML text
	 * @param positions
	 *        the positions already found
	 * @return true if it recognized a XML instruction
	 */
	boolean analyzeInstruction(String xml, List<XmlRegion> positions) {

		boolean result = false;
		int newPos = this.offset;
		if (newPos < xml.length() && xml.charAt(newPos) == '<' && ++newPos < xml.length() && xml.charAt(newPos) == '?') {

			while (++newPos < xml.length() && xml.charAt(newPos) != '>')
				newPos = xml.indexOf('?', newPos);

			if (xml.charAt(newPos) == '>') {
				positions.add(new XmlRegion(XmlRegionType.INSTRUCTION, this.offset, newPos + 1));
				this.offset = newPos + 1;
				result = true;
			}
		}

		return result;
	}

	/**
	 * Tries to analyze a XML comment.
	 * 
	 * @param xml
	 *        the XML text
	 * @param positions
	 *        the positions already found
	 * @return true if it recognized a XML instruction
	 */
	boolean analyzeComment(String xml, List<XmlRegion> positions) {

		boolean result = false;
		int newPos = this.offset;
		if (xml.charAt(newPos) == '<' && ++newPos < xml.length() && xml.charAt(newPos) == '!' && ++newPos < xml.length() && xml.charAt(newPos) == '-' && ++newPos < xml
			.length() && xml.charAt(newPos) == '-') {

			int seq = 0;
			while (seq != 3 && ++newPos < xml.length()) {
				char c = xml.charAt(newPos);
				seq = c == '-' && seq < 2 || c == '>' && seq == 2 ? seq + 1 : 0;
			}

			if (seq == 3)
				newPos++;

			positions.add(new XmlRegion(XmlRegionType.COMMENT, this.offset, newPos));
			this.offset = newPos;
			result = true;
		}

		return result;
	}

	/**
	 * Tries to analyze a XML mark-up.
	 * 
	 * @param xml
	 *        the XML text
	 * @param positions
	 *        the positions already found
	 * @return true if it recognized a XML instruction
	 */
	boolean analyzeMarkup(String xml, List<XmlRegion> positions) {

		int newPos = this.offset;
		boolean result = false;

		// "<..."
		if (xml.charAt(newPos) == '<') {

			// Do not process a CData section or a comment as a mark-up
			if (newPos + 1 < xml.length() && xml.charAt(newPos + 1) == '!')
				return false;

			// Mark-up name
			char c = '!';
			while (newPos < xml.length() && (c = xml.charAt(newPos)) != '>' && !Character.isWhitespace(c))
				newPos++;

			if (c == '>')
				newPos++;

			positions.add(new XmlRegion(XmlRegionType.MARKUP, this.offset, newPos));
			this.offset = newPos;
			result = true;
		}

		// "/>"
		else if (xml.charAt(newPos) == '/' && ++newPos < xml.length() && xml.charAt(newPos) == '>') {

			positions.add(new XmlRegion(XmlRegionType.MARKUP, this.offset, ++newPos));
			this.offset = newPos;
			result = true;
		}

		// "attributes... >"
		else if (xml.charAt(newPos) == '>') {
			positions.add(new XmlRegion(XmlRegionType.MARKUP, this.offset, ++newPos));
			this.offset = newPos;
			result = true;
		}

		return result;
	}

	/**
	 * Tries to analyze a XML attribute.
	 * 
	 * @param xml
	 *        the XML text
	 * @param positions
	 *        the positions already found
	 * @return true if it recognized a XML instruction
	 */
	boolean analyzeAttribute(String xml, List<XmlRegion> positions) {

		// An attribute value follows a mark-up
		for (int i = positions.size() - 1; i >= 0; i--) {
			XmlRegion xr = positions.get(i);
			if (xr.getXmlRegionType() == XmlRegionType.WHITESPACE)
				continue;

			if (xr.getXmlRegionType() == XmlRegionType.ATTRIBUTE_VALUE)
				break;

			if (xr.getXmlRegionType() == XmlRegionType.MARKUP) {
				char c = xml.charAt(xr.getEnd() - 1);
				if (c != '>')
					break;
			}

			return false;
		}

		// Analyze what we have...
		boolean result = false;
		int newPos = this.offset;
		char c;
		while (newPos < xml.length() && (c = xml.charAt(newPos)) != '=' && c != '/' && c != '>' && !Character.isWhitespace(c))
			newPos++;

		// Found one?
		if (newPos != this.offset) {
			positions.add(new XmlRegion(XmlRegionType.ATTRIBUTE, this.offset, newPos));
			this.offset = newPos;
			result = true;
		}

		return result;
	}

	/**
	 * Tries to analyze a mark-up's value.
	 * 
	 * @param xml
	 *        the XML text
	 * @param positions
	 *        the positions already found
	 * @return true if it recognized a XML instruction
	 */
	boolean analyzeMarkupValue(String xml, List<XmlRegion> positions) {

		// A mark-up value follows a mark-up
		for (int i = positions.size() - 1; i >= 0; i--) {
			XmlRegion xr = positions.get(i);
			if (xr.getXmlRegionType() == XmlRegionType.WHITESPACE)
				continue;

			if (xr.getXmlRegionType() == XmlRegionType.MARKUP || xr.getXmlRegionType() == XmlRegionType.COMMENT) {
				char c = xml.charAt(xr.getEnd() - 1);
				if (c == '>')
					break;
			}

			return false;
		}

		// Read...
		boolean result = false;
		int newPos = this.offset;
		while (newPos < xml.length() && xml.charAt(newPos) != '<')
			newPos++;

		// We read something and this something is not only made up of white spaces
		if (this.offset != newPos) {

			// We must here repair the list if the previous position is made up of white spaces
			XmlRegion xr = positions.get(positions.size() - 1);
			int start = this.offset;
			if (xr.getXmlRegionType() == XmlRegionType.WHITESPACE) {
				start = xr.getStart();
				positions.remove(xr);
			}

			positions.add(new XmlRegion(XmlRegionType.MARKUP_VALUE, start, newPos));
			this.offset = newPos;
			result = true;
		}

		return result;
	}

	/**
	 * Tries to analyze a XML attribute's value.
	 * 
	 * @param xml
	 *        the XML text
	 * @param positions
	 *        the positions already found
	 * @return true if it recognized a XML instruction
	 */
	boolean analyzeAttributeValue(String xml, List<XmlRegion> positions) {

		// An attribute value follows an attribute
		for (int i = positions.size() - 1; i >= 0; i--) {
			XmlRegion xr = positions.get(i);
			if (xr.getXmlRegionType() == XmlRegionType.WHITESPACE)
				continue;

			if (xr.getXmlRegionType() == XmlRegionType.ATTRIBUTE)
				break;

			return false;
		}

		// Analyze what we have
		boolean result = false;
		int newPos = this.offset;
		if (xml.charAt(newPos) == '=') {
			analyzeWhitespaces(xml, positions);

			int cpt = 0;
			char previous = '!';
			while (++newPos < xml.length()) {
				char c = xml.charAt(newPos);
				if (previous != '\\' && c == '"')
					cpt++;

				previous = c;
				if (cpt == 2) {
					newPos++;
					break;
				}
			}

			positions.add(new XmlRegion(XmlRegionType.ATTRIBUTE_VALUE, this.offset, newPos));
			this.offset = newPos;
			result = true;
		}

		return result;
	}

	/**
	 * Tries to analyze a CDATA section.
	 * 
	 * @param xml
	 *        the XML text
	 * @param positions
	 *        the positions already found
	 * @return true if it recognized a XML instruction
	 */
	boolean analyzeCData(String xml, List<XmlRegion> positions) {

		boolean result = false;
		int newPos = this.offset;
		if (xml.charAt(newPos) == '<' && ++newPos < xml.length() && xml.charAt(newPos) == '!' && ++newPos < xml.length() && xml.charAt(newPos) == '[' && ++newPos < xml
			.length() && xml.charAt(newPos) == 'C' && ++newPos < xml.length() && xml.charAt(newPos) == 'D' && ++newPos < xml.length() && xml.charAt(newPos) == 'A' && ++newPos < xml
				.length() && xml.charAt(newPos) == 'T' && ++newPos < xml.length() && xml.charAt(newPos) == 'A' && ++newPos < xml.length() && xml.charAt(newPos) == '[') {

			int cpt = 0;
			while (++newPos < xml.length()) {
				char c = xml.charAt(newPos);
				if (cpt < 2 && c == ']' || cpt == 2 && c == '>')
					cpt++;
				else
					cpt = 0;

				if (cpt == 3) {
					newPos++;
					break;
				}
			}

			positions.add(new XmlRegion(XmlRegionType.CDATA, this.offset, newPos));
			this.offset = newPos;
			result = true;
		}

		return result;
	}

	/**
	 * Tries to analyze white spaces.
	 * <p>
	 * If white spaces are found, a XML position is stored and the offset is updated.
	 * </p>
	 *
	 * @param xml
	 *        the XML text
	 * @param positions
	 *        the positions already found
	 */
	void analyzeWhitespaces(String xml, List<XmlRegion> positions) {

		int i = this.offset;
		while (i < xml.length() && Character.isWhitespace(xml.charAt(i)))
			i++;

		if (i != this.offset) {
			positions.add(new XmlRegion(XmlRegionType.WHITESPACE, this.offset, i));
			this.offset = i;
		}
	}
}
