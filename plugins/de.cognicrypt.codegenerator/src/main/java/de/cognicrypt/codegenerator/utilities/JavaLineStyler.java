/********************************************************************************
 * Copyright (c) 2015-2018 TU Darmstadt
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/

/**
 * Copyright 2015-2017 Technische Universitaet Darmstadt
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.cognicrypt.codegenerator.utilities;

import java.io.IOException;
import java.io.StringReader;
import java.util.Vector;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.LineStyleEvent;
import org.eclipse.swt.custom.LineStyleListener;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;

public class JavaLineStyler implements LineStyleListener {

	JavaScanner scanner = new JavaScanner();

	int[] tokenColors;

	Color[] colors;

	Vector<int[]> blockComments = new Vector<int[]>();

	public static final int EOF = -1;

	public static final int EOL = 10;

	public static final int WORD = 0;

	public static final int WHITE = 1;

	public static final int KEY = 2;

	public static final int COMMENT = 3;

	public static final int STRING = 5;

	public static final int OTHER = 6;

	public static final int NUMBER = 7;

	public static final int MAXIMUM_TOKEN = 8;

	public JavaLineStyler() {
		initializeColors();
		this.scanner = new JavaScanner();
	}

	Color getColor(final int type) {
		if (type < 0 || type >= this.tokenColors.length) {
			return null;
		}
		return this.colors[this.tokenColors[type]];
	}

	boolean inBlockComment(final int start, final int end) {
		for (int i = 0; i < this.blockComments.size(); i++) {
			final int[] offsets = this.blockComments.elementAt(i);
			// start of comment in the line
			if ((offsets[0] >= start) && (offsets[0] <= end)) {
				return true;
			}
			// end of comment in the line
			if ((offsets[1] >= start) && (offsets[1] <= end)) {
				return true;
			}
			if ((offsets[0] <= start) && (offsets[1] >= end)) {
				return true;
			}
		}
		return false;
	}

	void initializeColors() {
		final Display display = Display.getDefault();
		this.colors = new Color[] { new Color(display, new RGB(0, 0, 0)), // black
				new Color(display, new RGB(66, 111, 66)), // green
				new Color(display, new RGB(0, 0, 255)), // blue
				new Color(display, new RGB(128, 0, 64)) // purple
		};
		this.tokenColors = new int[MAXIMUM_TOKEN];
		this.tokenColors[WORD] = 0;
		this.tokenColors[WHITE] = 0;
		this.tokenColors[KEY] = 3;
		this.tokenColors[COMMENT] = 1;
		this.tokenColors[STRING] = 2;
		this.tokenColors[OTHER] = 0;
		this.tokenColors[NUMBER] = 0;
	}

	void disposeColors() {
		for (int i = 0; i < this.colors.length; i++) {
			this.colors[i].dispose();
		}
	}

	/**
	 * Event.detail line start offset (input) Event.text line text (input) LineStyleEvent.styles Enumeration of StyleRanges, need to be in order. (output) LineStyleEvent.background
	 * line background color (output)
	 */
	@Override
	public void lineGetStyle(final LineStyleEvent event) {
		final Vector<StyleRange> styles = new Vector<StyleRange>();
		int token;
		StyleRange lastStyle;

		// If the line is part of a block comment, create one style for the entire line.
		if (inBlockComment(event.lineOffset, event.lineOffset + event.lineText.length())) {
			styles.addElement(new StyleRange(event.lineOffset, event.lineText.length(), getColor(COMMENT), null));
			event.styles = new StyleRange[styles.size()];
			styles.copyInto(event.styles);
			return;
		}
		final Color defaultFgColor = ((Control) event.widget).getForeground();
		this.scanner.setRange(event.lineText);
		token = this.scanner.nextToken();
		while (token != EOF) {
			if (token == OTHER) {
				// do nothing for non-colored tokens
			} else if (token != WHITE) {
				final Color color = getColor(token);
				// Only create a style if the token color is different than the
				// widget's default foreground color and the token's style is
				// not bold. Keywords are bolded.
				if ((!color.equals(defaultFgColor)) || (token == KEY)) {
					final StyleRange style = new StyleRange(this.scanner.getStartOffset() + event.lineOffset, this.scanner.getLength(), color, null);
					if (token == KEY) {
						style.fontStyle = SWT.BOLD;
					}
					if (styles.isEmpty()) {
						styles.addElement(style);
					} else {
						// Merge similar styles. Doing so will improve performance.
						lastStyle = styles.lastElement();
						if (lastStyle.similarTo(style) && (lastStyle.start + lastStyle.length == style.start)) {
							lastStyle.length += style.length;
						} else {
							styles.addElement(style);
						}
					}
				}
			} else if ((!styles.isEmpty()) && ((lastStyle = styles.lastElement()).fontStyle == SWT.BOLD)) {
				final int start = this.scanner.getStartOffset() + event.lineOffset;
				lastStyle = styles.lastElement();
				// A font style of SWT.BOLD implies that the last style
				// represents a java keyword.
				if (lastStyle.start + lastStyle.length == start) {
					// Have the white space take on the style before it to
					// minimize the number of style ranges created and the
					// number of font style changes during rendering.
					lastStyle.length += this.scanner.getLength();
				}
			}
			token = this.scanner.nextToken();
		}
		event.styles = new StyleRange[styles.size()];
		styles.copyInto(event.styles);
	}

	public void parseBlockComments(final String text) {
		this.blockComments = new Vector<int[]>();
		final StringReader buffer = new StringReader(text);
		int ch;
		boolean blkComment = false;
		int cnt = 0;
		int[] offsets = new int[2];
		boolean done = false;

		try {
			while (!done) {
				switch (ch = buffer.read()) {
					case -1: {
						if (blkComment) {
							offsets[1] = cnt;
							this.blockComments.addElement(offsets);
						}
						done = true;
						break;
					}
					case '/': {
						ch = buffer.read();
						if ((ch == '*') && (!blkComment)) {
							offsets = new int[2];
							offsets[0] = cnt;
							blkComment = true;
							cnt++;
						} else {
							cnt++;
						}
						cnt++;
						break;
					}
					case '*': {
						if (blkComment) {
							ch = buffer.read();
							cnt++;
							if (ch == '/') {
								blkComment = false;
								offsets[1] = cnt;
								this.blockComments.addElement(offsets);
							}
						}
						cnt++;
						break;
					}
					default: {
						cnt++;
						break;
					}
				}
			}
		} catch (final IOException e) {
			// ignore errors
		}
	}
}
