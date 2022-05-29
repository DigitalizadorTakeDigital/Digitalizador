/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.webscanner.view.formattedfields;

import java.beans.PropertyChangeSupport;
import java.io.Serializable;
import java.text.ParseException;

import javax.swing.JFormattedTextField;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.MaskFormatter;

import br.com.webscanner.view.formattedfields.verifiers.MaskVerifier;

/**
 *
 * @author Mark Pendergast
 * Copyright Mark Pendergast 
 */
public class FormattedSSNField  extends JFormattedTextField implements Serializable{
    private PropertyChangeSupport propertySupport;

    public static final String SSNFORMAT = "###-##-####";
    private DefaultFormatterFactory dff;

   /**
    * Constructor for SSN field, creates and sets up formatters for social security numbers
    * @throws java.text.ParseException
    */
    public FormattedSSNField() throws ParseException
    {
      propertySupport = new PropertyChangeSupport(this);
      MaskFormatter mf = new MaskFormatter(SSNFORMAT);
      mf.setValueClass(String.class);
      mf.setPlaceholderCharacter('_');  
      setInputVerifier(new MaskVerifier(SSNFORMAT));
      dff = new DefaultFormatterFactory(mf);
      setFormatterFactory(dff);
    }

}