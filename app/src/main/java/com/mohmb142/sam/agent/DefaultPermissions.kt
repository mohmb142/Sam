package com.mohmb142.sam.agent

class DefaultPermissions : PermissionManager {
    override fun canExecute(action: AgentAction): Boolean = when (action.risk) {
        RiskLevel.LOW -> true
        RiskLevel.MEDIUM -> false
        RiskLevel.HIGH -> false
    }
}

object ActionTypes {
    const val READ_CALLER = "read_caller"
    const val OPEN_APP = "open_app"
    const val READ_MESSAGE = "read_message"
    const val SEND_MESSAGE = "send_message"
    const val MAKE_CALL = "make_call"
    const val CREATE_REMINDER = "create_reminder"
}
