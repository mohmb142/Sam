package com.mohmb142.sam.agent

enum class RiskLevel { LOW, MEDIUM, HIGH }

data class AgentAction(
    val type: String,
    val parameters: Map<String, String> = emptyMap(),
    val risk: RiskLevel = RiskLevel.LOW
)

data class AgentTask(
    val goal: String,
    val actions: List<AgentAction> = emptyList(),
    val status: String = "PENDING"
)

interface Tool {
    val id: String
    suspend fun execute(action: AgentAction): Result<String>
}

interface PermissionManager {
    fun canExecute(action: AgentAction): Boolean
}

class AgentEngine(
    private val tools: Map<String, Tool>,
    private val permissions: PermissionManager
) {
    suspend fun execute(action: AgentAction): Result<String> {
        if (!permissions.canExecute(action)) {
            return Result.failure(SecurityException("تحتاج هذه العملية إلى صلاحية أو تأكيد."))
        }
        val tool = tools[action.type]
            ?: return Result.failure(IllegalArgumentException("الأداة غير موجودة: ${action.type}"))
        return tool.execute(action)
    }
}
