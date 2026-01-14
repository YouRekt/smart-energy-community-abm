package edu.wut.thesis.smart_energy_community_abm.behaviours.base;

import edu.wut.thesis.smart_energy_community_abm.agents.BaseAgent;
import edu.wut.thesis.smart_energy_community_abm.domain.constants.LogSeverity;
import jade.core.behaviours.OneShotBehaviour;

final public class LogBehaviour extends OneShotBehaviour {
    private final BaseAgent agent;
    private final String logMessage;
    private final LogSeverity severity;

    public LogBehaviour(BaseAgent agent, String logMessage) {
        this(agent, logMessage, LogSeverity.DEBUG);
    }

    public LogBehaviour(BaseAgent agent, String logMessage, LogSeverity severity) {
        super(agent);
        this.agent = agent;
        this.logMessage = logMessage;
        this.severity = severity;
    }

    @Override
    public void action() {
        agent.log(logMessage, severity, this);
    }
}
