package plugin.gui;

import actors.ActorManager;
import com.intellij.util.ui.BlockBorder;
import fr.inria.lille.repair.common.config.Config;
import fr.inria.lille.repair.common.synth.StatementType;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * Created by bdanglot on 9/16/16.
 */
public class ConfigPanel extends JPanel {

    public ConfigPanel() {
        this.setLayout(new GridLayout(2, 3));
        this.buildGroupSynthesis();
        this.buildGroupType();
        this.buildRemoteManagement();
        this.setVisible(true);
    }

    private void buildRemoteManagement() {
        JPanel panelInria = new JPanel();
        JRadioButton remoteInria = new JRadioButton();
        remoteInria.setSelected(true);
        remoteInria.addActionListener(event -> {
            ActorManager.addressNopol = ActorManager.configNopol.getString("akka.remote.netty.tcp.hostname");
            ActorManager.portNopol = ActorManager.configNopol.getString("akka.remote.netty.tcp.port");
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
                    ActorManager.addressNopol = input[0];
                    ActorManager.portNopol = input[1];
                }
            }
        });
        panelCustom.add(adrCustom);

        ButtonGroup groupAdr = new ButtonGroup();
        groupAdr.add(remoteInria);
        groupAdr.add(customButton);

        JPanel panel = new JPanel();
        panel.setBorder(new BlockBorder());

        panel.add(panelInria);
        panel.add(panelCustom);

        this.add(panel);
    }

    /**
     * Add to the Panel a Group of RadioButton for setting up Type
     */
    private void buildGroupType() {
        JRadioButton condition = new JRadioButton();
        condition.setSelected(true);
        condition.addActionListener(event -> Config.INSTANCE.setType(StatementType.CONDITIONAL));

        JRadioButton precondition = new JRadioButton();
        precondition.addActionListener(event -> Config.INSTANCE.setType(StatementType.PRECONDITION));

        ButtonGroup groupType = new ButtonGroup();
        groupType.add(condition);
        groupType.add(precondition);

        JPanel panel = new JPanel();
        panel.setBorder(new BlockBorder());
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

    /**
     * Add to the Panel a Group of RadioButton for setting up Synthesis
     */
    private void buildGroupSynthesis() {
        JRadioButton smtSynthesis = new JRadioButton();
        smtSynthesis.setSelected(true);
        smtSynthesis.setActionCommand(String.valueOf(Config.NopolSynthesis.SMT));
        smtSynthesis.addActionListener(event -> Config.INSTANCE.setSynthesis(Config.NopolSynthesis.BRUTPOL));

        JRadioButton dynamothSynthesis = new JRadioButton();
        dynamothSynthesis.setActionCommand(String.valueOf(Config.NopolSynthesis.BRUTPOL));
        dynamothSynthesis.addActionListener(event -> Config.INSTANCE.setSynthesis(Config.NopolSynthesis.BRUTPOL));

        ButtonGroup groupSynthesis = new ButtonGroup();
        groupSynthesis.add(smtSynthesis);
        groupSynthesis.add(dynamothSynthesis);

        JPanel panel = new JPanel();
        panel.setBorder(new BlockBorder());
        panel.setLayout(new GridLayout(3, 3));

        panel.add(new JLabel("Synthesis: "));
        panel.add(new JLabel(""));
        panel.add(new JLabel(""));

        panel.add(new JLabel("SMT"));
        panel.add(new JLabel(""));
        panel.add(smtSynthesis);

        panel.add(new JLabel("Dynamoth"));
        panel.add(new JLabel(""));
        panel.add(dynamothSynthesis);

        this.add(panel);
    }

}
