package br.com.webscanner.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;

import br.com.webscanner.controller.MoveToController;
import br.com.webscanner.model.domain.Document;

/**
 * @author Jonathan Camara
 */
@SuppressWarnings("serial")
public class MoveToDialog extends JDialog {
	private MoveToController controller;
	private JTextField moveToTextField;
	private JLabel jLabel;
	private JLabel alertLabel;
	private JTable table;
	private Document document;
	private int row;

	private JPanel dialogPane;
	private JPanel contentPanel;
	private JPanel buttonBar;
	private JButton moveToButton;

	public MoveToDialog(MoveToController controller, final JTable table, final Document document, final int row) {
		this.controller = controller;
		this.table = table;
		this.document = document;
		this.row = row;
		initComponents();
	}

	private void moveToButtonActionPerformed(ActionEvent e) {
		try {
			int integer = Integer.parseInt(moveToTextField.getText());
			if(integer > 0 && integer <= table.getRowCount()) {
				controller.moveTo(table, row, integer-1);
				this.dispose();
			}
		}catch (NumberFormatException ex) {
			// TODO: handle exception
		}
	}

	private void initComponents() {
		dialogPane = new JPanel();
		contentPanel = new JPanel();
		buttonBar = new JPanel();
		alertLabel = new JLabel();
		alertLabel.setText("O número da possição deve ser de 1 até "+table.getRowCount()+".");
		alertLabel.setForeground(Color.RED);
		
		moveToButton = new TranslucentButton("dialogMoveButton.png", "dialogMoveButtonOver.png", "Mover para");
		moveToTextField = new JFormattedTextField();
		moveToTextField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent evk) {
				char vchar = evk.getKeyChar();

				if(!(Character.isDigit(vchar)) || (vchar == KeyEvent.VK_BACK_SPACE) || (vchar == KeyEvent.VK_DELETE)) {
					evk.consume();
				}
				try {
					int inteiro = Integer.parseInt(moveToTextField.getText());
				} catch (NumberFormatException e) {
				}
			}
		});
		jLabel = new JLabel("Mover por qual posição?");

		// ======== this ========
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setTitle("Mover para");
		setModal(true);
		Container contentPane = getContentPane();
		contentPane.setLayout(new BorderLayout());

		// ======== dialogPane ========
		{
			dialogPane.setBackground(Color.white);
			dialogPane.setBorder(new EmptyBorder(12, 12, 12, 12));

			dialogPane.setLayout(new BorderLayout());

			// ======== contentPanel ========
			{
				contentPanel.setBackground(Color.white);
				contentPanel.setLayout(new FormLayout("default:grow", "top:default, $lgap, default,20dlu"));

			}
			contentPanel.add(jLabel, CC.xy(1, 1));
			contentPanel.add(moveToTextField, CC.xy(1, 3));
			contentPanel.add(alertLabel, CC.xy(1, 4));

			dialogPane.add(contentPanel, BorderLayout.CENTER);

			// ======== buttonBar ========
			{
				buttonBar.setBackground(Color.white);
				buttonBar.setBorder(new EmptyBorder(12, 0, 0, 0));
				buttonBar.setLayout(new GridBagLayout());
				((GridBagLayout) buttonBar.getLayout()).columnWidths = new int[] { 0, 80 };
				((GridBagLayout) buttonBar.getLayout()).columnWeights = new double[] { 1.0, 0.0 };

				// ---- moveToButton ----
				moveToButton.setEnabled(true);
				moveToButton.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						moveToButtonActionPerformed(e);
					}
				});
				buttonBar.add(moveToButton, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
						GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
			}
			dialogPane.add(buttonBar, BorderLayout.SOUTH);

			if (!moveToTextField.getText().isEmpty()) {
				moveToButton.setEnabled(true);
			}

		}
		contentPane.add(dialogPane, BorderLayout.CENTER);
		setSize(350, 180);
		setLocationRelativeTo(getOwner());
	}

}
