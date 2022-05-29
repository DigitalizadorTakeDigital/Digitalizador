package br.com.webscanner.view.renderer;

import java.awt.Component;

import javax.swing.JList;
import javax.swing.plaf.basic.BasicComboBoxRenderer;

import br.com.webscanner.model.domain.Item;

public class ItemRenderer extends BasicComboBoxRenderer  
    {  
        public Component getListCellRendererComponent(  
            JList list, Object value, int index,  
            boolean isSelected, boolean cellHasFocus)  
        {  
            super.getListCellRendererComponent(list, value, index,  
                isSelected, cellHasFocus);  
            
            if (value != null)  
            {  
            	if (value instanceof Item){
	                Item item = (Item)value;  
	                setText( item.getText());  
            	}
            }  
//            if (index == -1)  
//            {  
//                Item item = (Item)value;  
//                setText( "" + item.getText());  
//            }  
            return this;  
        }  
    }  