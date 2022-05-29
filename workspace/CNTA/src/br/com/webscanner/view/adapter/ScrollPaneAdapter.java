/**
 * 
 */
package br.com.webscanner.view.adapter;

import java.awt.Cursor;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JViewport;

import br.com.webscanner.view.ImagePanel;

/**
 * @author Diego
 *
 */
public class ScrollPaneAdapter extends MouseAdapter{
	
	private final Cursor defCursor = Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR);
    private final Cursor hndCursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
    private final Point pp = new Point();
    private ImagePanel imagePanel;
    
    public ScrollPaneAdapter(ImagePanel imagePanel){
    	this.imagePanel = imagePanel;
    }
    
    public void mouseDragged(final MouseEvent e){
        JViewport vport = (JViewport)e.getSource();
        Point cp = e.getPoint();
        Point vp = vport.getViewPosition();
        vp.translate(pp.x-cp.x, pp.y-cp.y);
        imagePanel.scrollRectToVisible(new Rectangle(vp, vport.getSize()));
        pp.setLocation(cp);
    }

    public void mousePressed(MouseEvent e){
        imagePanel.setCursor(hndCursor);
        pp.setLocation(e.getPoint());
    }

    public void mouseReleased(MouseEvent e){
        imagePanel.setCursor(defCursor);
        imagePanel.getParent().repaint();
        //imagePanel.getParent().revalidate();
    }
}