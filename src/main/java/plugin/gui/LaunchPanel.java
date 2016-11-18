package plugin.gui;

import org.jetbrains.annotations.NotNull;
import plugin.actors.ActorManager;

import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * Created by bdanglot on 9/21/16.
 */
public class LaunchPanel extends JPanel {

	public LaunchPanel() {
		this.setVisible(true);
		this.buildGroupType();
	}

	/**
	 * Add to the Panel a Group of RadioButton for setting up Type
	 */
	private void buildGroupType() {
		ButtonGroup buttonGroup = new ButtonGroup();
		JPanel globalPanel = new JPanel();
		globalPanel.add(buildPanelLocal(buttonGroup));
		globalPanel.add(buildPanelRemoteInria(buttonGroup));
		globalPanel.add(buildPanelCustomRemote(buttonGroup));
		this.add(globalPanel);
	}

	private JPanel buildPanelLocal(ButtonGroup buttonGroup) {
		JPanel panelLocal = new JPanel();
		JRadioButton localButton = new JRadioButton();
		localButton.setSelected(true);
		localButton.addActionListener(event -> {
			ActorManager.runNopolLocally = true;
		});
		panelLocal.add(localButton);
		panelLocal.add(new JLabel("Local"));
		buttonGroup.add(localButton);
		return panelLocal;
	}

	@NotNull
	private JPanel buildPanelCustomRemote(ButtonGroup buttonGroup) {
		JPanel panelCustom = new JPanel();
		JRadioButton customButton = new JRadioButton();
		panelCustom.add(customButton);
		panelCustom.add(new JLabel("custom:"));
		JTextField adrCustom = new JTextField();
		adrCustom.setText("127.0.0.1:2552");
		customButton.addActionListener(event -> {
			ActorManager.stopNopolLocally();
			String[] input = adrCustom.getText().split(":");
			ActorManager.buildRemoteActor(input[0], input[1]);
		});
		adrCustom.addKeyListener(getKeyAdapter(adrCustom));
		buttonGroup.add(customButton);
		panelCustom.add(adrCustom);
		return panelCustom;
	}

	@NotNull
	private JPanel buildPanelRemoteInria(ButtonGroup buttonGroup) {
		JPanel panelInria = new JPanel();
		JRadioButton buttonInria = new JRadioButton();
		buttonInria.addActionListener(event -> {
			ActorManager.stopNopolLocally();
			ActorManager.addressNopol = ActorManager.akkaConfigNoPol.getString("akka.remote.netty.tcp.hostname");
			ActorManager.portNopol = ActorManager.akkaConfigNoPol.getString("akka.remote.netty.tcp.port");
		});
		panelInria.add(buttonInria);
		panelInria.add(new JLabel("Remote Inria"));
		buttonGroup.add(buttonInria);
		return panelInria;
	}

	@NotNull
	private KeyAdapter getKeyAdapter(final JTextField adrCustom) {
		return new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == 1) {
					String[] input = adrCustom.getText().split(":");
					ActorManager.buildRemoteActor(input[0], input[1]);
				}
			}
		};
	}

}
