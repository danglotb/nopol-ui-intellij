package plugin.gui;

import plugin.actors.ActorManager;
import fr.inria.lille.repair.common.config.Config;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import static plugin.Plugin.config;

/**
 * Created by bdanglot on 9/16/16.
 */
public class ConfigPanel extends JPanel {

	public ConfigPanel() {
		this.setLayout(new GridLayout());
		this.buildGroupSynthesis();
		this.buildRemoteManagement();
		this.setVisible(true);
	}

	private void buildRemoteManagement() {
		JPanel panelInria = new JPanel();
		JRadioButton remoteInria = new JRadioButton();
		remoteInria.setSelected(true);
		remoteInria.addActionListener(event -> {
			ActorManager.addressNopol = ActorManager.akkaConfigNoPol.getString("akka.remote.netty.tcp.hostname");
			ActorManager.portNopol = ActorManager.akkaConfigNoPol.getString("akka.remote.netty.tcp.port");
		});
		panelInria.add(remoteInria);
		panelInria.add(new JLabel("Remote"));

		JPanel panelCustom = new JPanel();
		JRadioButton customButton = new JRadioButton();
		panelCustom.add(customButton);
		panelCustom.add(new JLabel("custom:"));
		JTextField adrCustom = new JTextField();
		adrCustom.setText("127.0.0.1:2552");
		adrCustom.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				System.out.println(e.getKeyCode());
				if (e.getKeyCode() == 1) {
					String[] input = adrCustom.getText().split(":");
					ActorManager.buildRemoteActor(input[0], input[1]);
				}
			}
		});
		panelCustom.add(adrCustom);

		ButtonGroup groupAdr = new ButtonGroup();
		groupAdr.add(remoteInria);
		groupAdr.add(customButton);

		JPanel panel = new JPanel();

		panel.add(panelInria);
		panel.add(panelCustom);

		this.add(panel);
	}



	/**
	 * Add to the Panel a Group of RadioButton for setting up Synthesis
	 */
	private void buildGroupSynthesis() {

		JRadioButton dynamothSynthesis = new JRadioButton();
		dynamothSynthesis.setSelected(true);
		dynamothSynthesis.setActionCommand(String.valueOf(Config.NopolSynthesis.BRUTPOL));
		dynamothSynthesis.addActionListener(event -> config.setSynthesis(Config.NopolSynthesis.BRUTPOL));

		JRadioButton smtSynthesis = new JRadioButton();
		smtSynthesis.setActionCommand(String.valueOf(Config.NopolSynthesis.SMT));
		smtSynthesis.addActionListener(event -> config.setSynthesis(Config.NopolSynthesis.SMT));

		ButtonGroup groupSynthesis = new ButtonGroup();
		groupSynthesis.add(smtSynthesis);
		groupSynthesis.add(dynamothSynthesis);

		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(3, 3));

		panel.add(new JLabel("Synthesis: "));
		panel.add(new JLabel(""));
		panel.add(new JLabel(""));

		panel.add(new JLabel("Dynamoth"));
		panel.add(new JLabel(""));
		panel.add(dynamothSynthesis);

		panel.add(new JLabel("SMT"));
		panel.add(new JLabel(""));
		panel.add(smtSynthesis);

		this.add(panel);
	}

}
