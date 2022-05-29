
package br.com.webscanner.view.formattedfields;


import java.beans.PropertyChangeSupport;
import java.io.Serializable;

import javax.swing.JFormattedTextField;
import javax.swing.text.DefaultFormatterFactory;

import br.com.webscanner.view.formattedfields.formatters.EmailFormatter;
import br.com.webscanner.view.formattedfields.verifiers.EmailVerifier;


/**
 *
 * @author Mark Pendergast
 * Copyright Mark Pendergast
 */
public class FormattedEmailField  extends JFormattedTextField implements Serializable{
    
    private PropertyChangeSupport propertySupport;

   /**
    * Constructor - creates and sets up formatters for email address
    */
    public FormattedEmailField() 
    {  
      setValue("");
      propertySupport = new PropertyChangeSupport(this);     
      EmailFormatter ef = new EmailFormatter();  
      setInputVerifier(new EmailVerifier());
      DefaultFormatterFactory dff = new DefaultFormatterFactory(ef);
      setFormatterFactory(dff);
    }

}