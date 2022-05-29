package br.com.webscanner.view.handler;

import java.awt.Cursor;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DragSource;

import javax.activation.ActivationDataFlavor;
import javax.activation.DataHandler;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.TransferHandler;

import br.com.webscanner.view.Reorderable;

@SuppressWarnings("serial")
public class TableRowTransferHandler extends TransferHandler {
	private final DataFlavor localObjectFlavor = new ActivationDataFlavor(Integer.class, DataFlavor.javaJVMLocalObjectMimeType, "Integer Row Index");
	private JTable table;
	
	public TableRowTransferHandler(JTable table) {
		this.table = table;
	}
	
	@Override
	   protected Transferable createTransferable(JComponent c) {
	      assert (c == table);
	      return new DataHandler(new Integer(table.getSelectedRow()), localObjectFlavor.getMimeType());
	   }

	   @Override
	   public boolean canImport(TransferHandler.TransferSupport info) {
	      boolean b = info.getComponent() == table && info.isDrop() && info.isDataFlavorSupported(localObjectFlavor);
	      table.setCursor(b ? DragSource.DefaultMoveDrop : DragSource.DefaultMoveNoDrop);
	      return b;
	   }

	   @Override
	   public int getSourceActions(JComponent c) {
	      return TransferHandler.COPY_OR_MOVE;
	   }

	   @Override
	   public boolean importData(TransferHandler.TransferSupport info) {
	      JTable target = (JTable) info.getComponent();

	      JTable.DropLocation dl = (JTable.DropLocation) info.getDropLocation();
	      int rowTo = dl.getRow();
	      
	      try {
	         Integer rowFrom = (Integer) info.getTransferable().getTransferData(localObjectFlavor);

	         table.clearSelection();
	         if(rowFrom > rowTo){
	        	 ((Reorderable)table.getModel()).reorder(rowFrom, rowTo);
	        	 target.getSelectionModel().addSelectionInterval(rowTo, rowTo);
	         }else if(rowFrom < rowTo){
	        	 if(rowFrom != (rowTo -1)){
	        		 rowTo--;
	        		 ((Reorderable)table.getModel()).reorder(rowFrom, rowTo);
		        	 target.getSelectionModel().addSelectionInterval(rowTo, rowTo);
	        	 }
	         }
	         
	         table.repaint();
	         table.revalidate();
	         return true;
	      } catch (Exception e) {
	    	  e.printStackTrace();
	      }
	      return false;
	   }

	   @Override
	   protected void exportDone(JComponent c, Transferable t, int act) {
	      if (act == TransferHandler.MOVE) {
	         table.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	      }
	   }
}
