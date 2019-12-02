/*
 * Copyright (c) 2013-2016 markiewb.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 * Contributors:
 * markiewb - initial API and implementation and/or initial documentation
 */
package de.funfried.netbeans.plugins.external.formatter.strategies.netbeans;

import de.funfried.netbeans.plugins.external.formatter.strategies.FormatterAdvice;
import de.funfried.netbeans.plugins.external.formatter.strategies.IFormatterStrategy;

import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;

import org.netbeans.modules.editor.indent.api.Reformat;
import org.openide.text.NbDocument;
import org.openide.util.Exceptions;

/**
 *
 * @author markiewb
 * @author bahlef
 */
public class NetBeansFormatterStrategy implements IFormatterStrategy {
	/**
	 * @param fa the {@link FormatterAdvice}
	 */
	@Override
	public void format(FormatterAdvice fa) {
		final int selectionStart = fa.selectionStart;
		final int selectionEnd = fa.selectionEnd;
		final boolean forSave = fa.forSave;
		final StyledDocument document = fa.styledDoc;

		final Reformat rf = Reformat.get(document);
		rf.lock();

		// only care about selection if reformatting on menu action and not on file save
		final int _dot = !forSave ? selectionStart : -1;
		final int _mark = !forSave ? selectionEnd : -1;

		try {
			NbDocument.runAtomicAsUser(document, new NetBeansFormatterRunnable(document, rf, _dot, _mark));
		} catch (BadLocationException ex) {
			Exceptions.printStackTrace(ex);
		} finally {
			rf.unlock();
		}
	}

	@Override
	public boolean canHandle(StyledDocument document) {
		return true;
	}
}
