/* ViewerBookTree.java created 2007-09-11
 * 
 */
package org.signalml.app.view;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.tree.TreePath;

import org.signalml.app.action.ActivateDocumentAction;
import org.signalml.app.action.CloseDocumentAction;
import org.signalml.app.action.selector.ActionFocusListener;
import org.signalml.app.action.selector.ActionFocusManager;
import org.signalml.app.action.selector.ActionFocusSupport;
import org.signalml.app.action.selector.BookDocumentFocusSelector;
import org.signalml.app.document.BookDocument;
import org.signalml.app.document.Document;
import org.signalml.app.document.DocumentFlowIntegrator;
import org.signalml.app.model.BookTreeModel;
import org.springframework.context.support.MessageSourceAccessor;

/** ViewerBookTree
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class ViewerBookTree extends AbstractViewerTree  implements BookDocumentFocusSelector {

	private static final long serialVersionUID = 1L;

	private ActionFocusSupport afSupport = new ActionFocusSupport(this);
	
	private JPopupMenu bookDocumentPopupMenu;

	private ActionFocusManager actionFocusManager;
	private DocumentFlowIntegrator documentFlowIntegrator;
	
	private ActivateDocumentAction activateDocumentAction;	
	private CloseDocumentAction closeDocumentAction;
	
	private BookDocument activeBookDocument;
	
	public ViewerBookTree(BookTreeModel model, MessageSourceAccessor messageSource) {
		super(model,messageSource);		
		setCellRenderer(new BookTreeCellRenderer());
		expandPath( new TreePath(new Object[] {model.getRoot()}) );
		addMouseListener(new MouseEventHandler());
	}

	@Override
	public BookTreeModel getModel() {
		return (BookTreeModel) super.getModel();
	}

	@Override
	public BookDocument getActiveBookDocument() {
		return activeBookDocument;
	}

	@Override
	public Document getActiveDocument() {
		return activeBookDocument;
	}
	
	@Override
	public void addActionFocusListener(ActionFocusListener listener) {
		afSupport.addActionFocusListener(listener);
	}

	@Override
	public void removeActionFocusListener(ActionFocusListener listener) {
		afSupport.removeActionFocusListener(listener);
	}

	@Override
	public JPopupMenu getComponentPopupMenu() {
		return focus(getSelectionPath());
	}
	
	private JPopupMenu focus(TreePath path) {

		JPopupMenu popupMenu = null;
		
		activeBookDocument = null;
		if( path != null ) {
			Object last = path.getLastPathComponent();
			if( last instanceof BookDocument ) {
				activeBookDocument = (BookDocument) last;
				popupMenu = getBookDocumentPopupMenu();
			}
		}
		
		afSupport.fireActionFocusChanged();
		
		return popupMenu;
		
	}

	private JPopupMenu getBookDocumentPopupMenu() {
		
		if( bookDocumentPopupMenu == null ) {
			bookDocumentPopupMenu = new JPopupMenu();
			
			bookDocumentPopupMenu.add(getActivateDocumentAction());
			bookDocumentPopupMenu.addSeparator();
			bookDocumentPopupMenu.add(getCloseDocumentAction());
		}
				
		return bookDocumentPopupMenu;
		
	}
	
	public ActionFocusManager getActionFocusManager() {
		return actionFocusManager;
	}

	public void setActionFocusManager(ActionFocusManager actionFocusManager) {
		this.actionFocusManager = actionFocusManager;
	}
	
	public DocumentFlowIntegrator getDocumentFlowIntegrator() {
		return documentFlowIntegrator;
	}

	public void setDocumentFlowIntegrator(DocumentFlowIntegrator documentFlowIntegrator) {
		this.documentFlowIntegrator = documentFlowIntegrator;
	}

	public ActivateDocumentAction getActivateDocumentAction() {
		if( activateDocumentAction == null ) {
			activateDocumentAction = new ActivateDocumentAction(messageSource,actionFocusManager,this);
		}
		return activateDocumentAction;
	}

	public CloseDocumentAction getCloseDocumentAction() {
		if( closeDocumentAction == null ) {
			closeDocumentAction = new CloseDocumentAction(messageSource,this);
			closeDocumentAction.setDocumentFlowIntegrator(documentFlowIntegrator);
		}
		return closeDocumentAction;
	}
	
	private class MouseEventHandler extends MouseAdapter {

		// TODO finish
		
		@Override
		public void mousePressed(MouseEvent e) {
			ViewerBookTree tree = (ViewerBookTree) e.getSource();
			if( SwingUtilities.isRightMouseButton(e) && (e.getClickCount() == 1) ) {
				TreePath selPath = tree.getPathForLocation(e.getX(), e.getY());
				tree.setSelectionPath(selPath);
			}
		}
			
	}
	
}
