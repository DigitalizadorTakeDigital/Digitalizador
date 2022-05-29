package br.com.webscanner.view;

import java.awt.Color;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import javax.swing.BorderFactory;
import javax.swing.JTextField;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

import br.com.webscanner.model.domain.Field;
import br.com.webscanner.model.domain.FieldModel;

public class MonetaryTextField extends JTextField {  
	protected static final long serialVersionUID = -7506506392528621022L;  
	protected static final NumberFormat MONETARY_FORMAT = new DecimalFormat("#,##0.00");  
	protected NumberFormat numberFormat;  
	protected int limit = -1;  
	private Field field;

	public MonetaryTextField(int fractionDigits, Field field) {  
		this(new DecimalFormat((fractionDigits == 0 ? "#,##0" : "#,##0.") + makeZeros(fractionDigits)), field);
	}  

//	public MonetaryTextField() {  
//		this(MONETARY_FORMAT);  
//	}  

	public MonetaryTextField(NumberFormat format, Field field) {  
		this.field = field;
		final FieldModel model = this.field.getFieldModel();
		
		numberFormat = format;  
		setHorizontalAlignment(RIGHT);  
		setDocument(new PlainDocument() {  
			protected static final long serialVersionUID = 1L;  

			@Override  
			public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
				String text = new StringBuilder(MonetaryTextField.this.getText().replaceAll("[^0-9]", "")).append(str.replaceAll("[^0-9]", "")).toString();  
				super.remove(0, getLength());  
				if (text.isEmpty()) {  
					text = "0";  
					MonetaryTextField.this.field.setValue("0");
				} else {  
					text = new BigInteger(text).toString();
				}
				
				String value = numberFormat.format(new BigDecimal(getLimit() > 0 == text.length() > getLimit() ? text.substring(0, getLimit()) : text).divide(new BigDecimal(Math.pow(10, numberFormat.getMaximumFractionDigits()))));
				MonetaryTextField.this.field.setValue(value);
				MonetaryTextField.this.field.setValid(true);
				super.insertString(0, value, a);  
			}  

			@Override  
			public void remove(int offs, int len) throws BadLocationException {  
				super.remove(offs, len);  
				if (len != getLength()) {  
					insertString(0, "", null);  
				}  
			}  
		});  

		addCaretListener(new CaretListener() {  
			boolean update = false;  

			@Override  
			public void caretUpdate(CaretEvent e) {  
				if (!update) {  
					update = true;  
					setCaretPosition(getText().length());  
					update = false;  
				}  
			}  
		});  
		addKeyListener(new KeyAdapter() {  
			@Override  
			public void keyPressed(KeyEvent e) {  
				if (e.getKeyCode() == KeyEvent.VK_DELETE) {  
					setText("");  
				}  
			}  
		});
		
		if(!field.getValue().toString().isEmpty()){
			setText(field.getValue().toString());
		}else{
			setText("0");  
		}
		
		if(model.isReadonly() || model.isHidden()){
			this.setEnabled(!model.isReadonly());
			this.setVisible(!model.isHidden());
			field.setValid(true);
		}
		
		if(model.isRequired() && field.getValue().toString().isEmpty()){
			this.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(Color.red, Color.red), model.getDisplayName()));
		}else if(!model.getValidationMethod().isEmpty() && !field.getValue().toString().isEmpty()){
			if(!field.isValid()){
				this.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(Color.red, Color.red), model.getDisplayName()));
			}else{
				this.setBorder(BorderFactory.createTitledBorder(model.getDisplayName()));
			}
		}else{
			this.setBorder(BorderFactory.createTitledBorder(model.getDisplayName()));
			field.setValid(true);
			transferFocus();
		}
		setCaretPosition(getText().length());  
	}  

	/*** 
	 * Define um valor BigDecimal ao campo** 
	 * 
	 * @param value 
	 */  
	public void setValue(BigDecimal value) {  
		super.setText(numberFormat.format(value));  
	}  

	/*** 
	 * Recupera um valor BigDecimal do campo** 
	 * 
	 * @return 
	 */  
	public final BigDecimal getValue() {  
		return new BigDecimal(getText().replaceAll("[^0-9]", "")).divide(new BigDecimal(Math.pow(10, numberFormat.getMaximumFractionDigits())));  
	}  

	/*** 
	 * Recupera o formatador atual do campo** 
	 * 
	 * @return 
	 */  
	public NumberFormat getNumberFormat() {  
		return numberFormat;  
	}  

	/*** 
	 * Define o formatador do campo** @param numberFormat 
	 */  
	public void setNumberFormat(NumberFormat numberFormat) {  
		this.numberFormat = numberFormat;  
	}  

	/*** 
	 * Preenche um StringBuilder com zeros** @param zeros* 
	 * 
	 * @return 
	 */  
	protected static final String makeZeros(int zeros) {  
		if (zeros >= 0) {  
			return String.format("%0" + zeros + "d", 0);
		} else {  
			throw new RuntimeException("Número de casas decimais inválida (" + zeros + ")");  
		}  
	}  

	/*** 
	 * Recupera o limite do campo.** @return 
	 */  
	public int getLimit() {  
		return limit;  
	}  

	/*** 
	 * Define o limite do campo, limit < 0 para deixar livre (default) Ignora os 
	 * pontos e virgulas do formato, conta* somente com os números** @param 
	 * limit
	 */  
	public void setLimit(int limit) {  
		this.limit = limit;  
	}   
} 