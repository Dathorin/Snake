package view;

import persistence.GameSettings;
import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class OptionsFrame extends JFrame
{
	// Generated serial version UID
	private static final long serialVersionUID = 4205999002517667339L;

	private GameSettings settings;
	private String toBind;
	private boolean saved;

	// Components
	private JPanel pnlKeybinds;
	private JPanel pnlSettings;
	private JLabel lblUp;
	private JLabel lblLeft;
	private JLabel lblDown;
	private JLabel lblRight;
	private JLabel lblPause;
	private JLabel lblSprint;
	private JLabel lblMouseLimit;
	private JButton btnUp;
	private JButton btnLeft;
	private JButton btnDown;
	private JButton btnRight;
	private JButton btnPause;
	private JButton btnSprint;
	private ArrayList<JButton> buttons;
	private JTextField tfMessage;
	private JTextField tfMouseLimit;

	public OptionsFrame(GameSettings options)
	{
		super("Options");
		this.settings = options;
		toBind = null;
		saved = false;

		// Set up the components
		pnlKeybinds = new JPanel(new GridLayout(6, 2));
		pnlKeybinds.setPreferredSize(new Dimension(200, 120));

		lblUp = new JLabel("Up");
		lblUp.setPreferredSize(new Dimension(80, 20));
		lblLeft = new JLabel("Left");
		lblLeft.setPreferredSize(new Dimension(80, 20));
		lblRight = new JLabel("Right");
		lblRight.setPreferredSize(new Dimension(80, 20));
		lblDown = new JLabel("Down");
		lblDown.setPreferredSize(new Dimension(80, 20));
		lblPause = new JLabel("Pause");
		lblPause.setPreferredSize(new Dimension(80, 20));
		lblSprint = new JLabel("Sprint");
		lblSprint.setPreferredSize(new Dimension(80, 20));

		ActionListener keybindsListener = new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				String act = e.getActionCommand();

				// Disable the buttons
				for(JButton b : buttons)
				{
					b.setEnabled(false);
				}
				tfMouseLimit.setEditable(false);
				tfMouseLimit.setEnabled(false);

				// Identify the calling button
				if(act.equals(KeyEvent.getKeyText(settings.getUpKey())))
				{
					toBind = "Up";
				}
				else if(act.equals(KeyEvent.getKeyText(settings.getLeftKey())))
				{
					toBind = "Left";
				}
				else if(act.equals(KeyEvent.getKeyText(settings.getDownKey())))
				{
					toBind = "Down";
				}
				else if(act.equals(KeyEvent.getKeyText(settings.getRightKey())))
				{
					toBind = "Right";
				}
				else if(act.equals(KeyEvent.getKeyText(settings.getPauseKey())))
				{
					toBind = "Pause";
				}
				else if(act.equals(KeyEvent.getKeyText(settings.getSprintKey())))
				{
					toBind = "Sprint";
				}
				else
				{
					toBind = "Unrecognized";
				}

				// Prompt the user to enter a new keybind
				tfMessage.setText("Press the new key for " + toBind);
				KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(new KeyEventDispatcher()
				{

					@Override
					public boolean dispatchKeyEvent(KeyEvent e)
					{
						if(toBind != null)
						{
							if(e.getID() == KeyEvent.KEY_PRESSED)
							{
								// Set the new keybind
								int code = e.getKeyCode();
								switch(toBind)
								{
									case "Up":
										settings.setUpKey(code);
										btnUp.setText(KeyEvent.getKeyText(code));
										break;
									case "Left":
										settings.setLeftKey(code);
										btnLeft.setText(KeyEvent.getKeyText(code));
										break;
									case "Down":
										settings.setDownKey(code);
										btnDown.setText(KeyEvent.getKeyText(code));
										break;
									case "Right":
										settings.setRightKey(code);
										btnRight.setText(KeyEvent.getKeyText(code));
										break;
									case "Pause":
										settings.setPauseKey(code);
										btnPause.setText(KeyEvent.getKeyText(code));
										break;
									case "Sprint":
										settings.setSprintKey(code);
										btnSprint.setText(KeyEvent.getKeyText(code));
										break;
								}
								toBind = null;

								// Reset the UI
								tfMessage.setText("");
								for(JButton b : buttons)
								{
									b.setEnabled(true);
								}
								tfMouseLimit.setEditable(true);
								tfMouseLimit.setEnabled(true);
							}
							e.consume();
							return true;
						}
						else
						{
							return false;
						}
					}
				});
			}
		};
		buttons = new ArrayList<JButton>();
		btnUp = new JButton(KeyEvent.getKeyText(settings.getUpKey()));
		btnUp.addActionListener(keybindsListener);
		buttons.add(btnUp);
		btnLeft = new JButton(KeyEvent.getKeyText(settings.getLeftKey()));
		btnLeft.addActionListener(keybindsListener);
		buttons.add(btnLeft);
		btnDown = new JButton(KeyEvent.getKeyText(settings.getDownKey()));
		btnDown.addActionListener(keybindsListener);
		buttons.add(btnDown);
		btnRight = new JButton(KeyEvent.getKeyText(settings.getRightKey()));
		btnRight.addActionListener(keybindsListener);
		buttons.add(btnRight);
		btnPause = new JButton(KeyEvent.getKeyText(settings.getPauseKey()));
		btnPause.addActionListener(keybindsListener);
		buttons.add(btnPause);
		btnSprint = new JButton(KeyEvent.getKeyText(settings.getSprintKey()));
		btnSprint.addActionListener(keybindsListener);
		buttons.add(btnSprint);

		for(JButton b : buttons)
		{
			b.setPreferredSize(new Dimension(100, 20));
		}

		pnlKeybinds.add(lblUp);
		pnlKeybinds.add(btnUp);
		pnlKeybinds.add(lblLeft);
		pnlKeybinds.add(btnLeft);
		pnlKeybinds.add(lblDown);
		pnlKeybinds.add(btnDown);
		pnlKeybinds.add(lblRight);
		pnlKeybinds.add(btnRight);
		pnlKeybinds.add(lblPause);
		pnlKeybinds.add(btnPause);
		pnlKeybinds.add(lblSprint);
		pnlKeybinds.add(btnSprint);

		pnlSettings = new JPanel(new GridLayout());
		pnlSettings.setPreferredSize(new Dimension(200, 20));

		lblMouseLimit = new JLabel("Mouse limit");
		lblMouseLimit.setPreferredSize(new Dimension(8, 20));

		tfMouseLimit = new JTextField(settings.getMouseLimit() + "");
		tfMouseLimit.setPreferredSize(new Dimension(80, 20));

		tfMessage = new JTextField();
		tfMessage.setPreferredSize(new Dimension(200, 25));
		tfMessage.setEditable(false);

		pnlSettings.add(lblMouseLimit);
		pnlSettings.add(tfMouseLimit);

		// Setup the layout and add components to the frame
		this.setLayout(new GridBagLayout());

		GridBagConstraints c = new GridBagConstraints();
		c.anchor = GridBagConstraints.CENTER;
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 0.5;
		c.weighty = 0.5;
		add(pnlKeybinds, c);

		c = new GridBagConstraints();
		c.anchor = GridBagConstraints.CENTER;
		c.gridx = 0;
		c.gridy = 1;
		c.weightx = 0.5;
		c.weighty = 0.5;
		add(pnlSettings, c);

		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 2;
		c.weightx = 0.5;
		add(tfMessage, c);

		// Set the frame options
		setSize(240, 300);
		setResizable(false);
		setVisible(true);
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		this.addWindowListener(new WindowListener()
		{
			@Override
			public void windowOpened(WindowEvent e)
			{
				// Unused method
			}

			@Override
			public void windowClosing(WindowEvent e)
			{
				// Attempt to parse input text fields
				if(!saved)
				{
					saved = true;

					try
					{
						int mouseLimit = Integer.parseInt(tfMouseLimit.getText());
						settings.setMouseLimit(mouseLimit);
					}
					catch(NumberFormatException nfe)
					{
						// Disregard malformed input
						return;
					}
				}
			}

			@Override
			public void windowClosed(WindowEvent e)
			{
				// Attempt to parse input text fields
				if(!saved)
				{
					saved = true;

					try
					{
						int mouseLimit = Integer.parseInt(tfMouseLimit.getText());
						settings.setMouseLimit(mouseLimit);
					}
					catch(NumberFormatException nfe)
					{
						// Disregard malformed input
						return;
					}
				}
			}

			@Override
			public void windowIconified(WindowEvent e)
			{
				// Unused method
			}

			@Override
			public void windowDeiconified(WindowEvent e)
			{
				// Unused method
			}

			@Override
			public void windowActivated(WindowEvent e)
			{
				// Unused method
			}

			@Override
			public void windowDeactivated(WindowEvent e)
			{
				// Unused method
			}
		});
	}
}