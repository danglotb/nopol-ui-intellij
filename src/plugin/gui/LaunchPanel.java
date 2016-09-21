package plugin.gui;

import fr.inria.lille.repair.common.synth.StatementType;

import javax.swing.*;
import java.awt.*;

import static plugin.Plugin.config;

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
		JRadioButton condition = new JRadioButton();
		condition.setSelected(true);
		condition.addActionListener(event -> config.setType(StatementType.CONDITIONAL));

		JRadioButton precondition = new JRadioButton();
		precondition.addActionListener(event -> config.setType(StatementType.PRECONDITION));

		ButtonGroup groupType = new ButtonGroup();
		groupType.add(condition);
		groupType.add(precondition);

		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(3, 3));

		panel.add(new JLabel("Type: "));
		panel.add(new JLabel(""));
		panel.add(new JLabel(""));

		panel.add(new JLabel("condition"));
		panel.add(new JLabel(""));
		panel.add(condition);

		panel.add(new JLabel("pre-condition"));
		panel.add(new JLabel(""));
		panel.add(precondition);

		this.add(panel);
	}

}
