package vn.toiuu.app.data

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

data class SavedScenario(
    val id: String,
    val title: String,
    val tool: ToolKind,
    val timestamp: Long,
    val values: Map<String, String>
)

object ScenarioStorage {
    private const val PREFS_NAME = "toiuu_saved_scenarios"
    private const val KEY_LIST = "scenarios_json"

    fun getAll(context: Context): List<SavedScenario> {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val raw = prefs.getString(KEY_LIST, null) ?: return emptyList()
        val list = mutableListOf<SavedScenario>()
        try {
            val arr = JSONArray(raw)
            for (i in 0 until arr.length()) {
                val obj = arr.getJSONObject(i)
                val id = obj.optString("id", "")
                val title = obj.optString("title", "Kịch bản")
                val toolName = obj.optString("tool", ToolKind.PRICING.name)
                val tool = runCatching { ToolKind.valueOf(toolName) }.getOrDefault(ToolKind.PRICING)
                val timestamp = obj.optLong("timestamp", System.currentTimeMillis())

                val valObj = obj.optJSONObject("values") ?: JSONObject()
                val map = mutableMapOf<String, String>()
                val keys = valObj.keys()
                while (keys.hasNext()) {
                    val k = keys.next()
                    map[k] = valObj.optString(k, "")
                }
                list.add(SavedScenario(id, title, tool, timestamp, map))
            }
        } catch (_: Exception) {
        }
        return list.sortedByDescending { it.timestamp }
    }

    fun getByTool(context: Context, tool: ToolKind): List<SavedScenario> {
        return getAll(context).filter { it.tool == tool }
    }

    fun save(context: Context, scenario: SavedScenario) {
        val list = getAll(context).filterNot { it.id == scenario.id }.toMutableList()
        list.add(0, scenario)
        saveAll(context, list)
    }

    fun delete(context: Context, id: String) {
        val list = getAll(context).filterNot { it.id == id }
        saveAll(context, list)
    }

    private fun saveAll(context: Context, list: List<SavedScenario>) {
        val arr = JSONArray()
        for (item in list) {
            val obj = JSONObject()
            obj.put("id", item.id)
            obj.put("title", item.title)
            obj.put("tool", item.tool.name)
            obj.put("timestamp", item.timestamp)

            val valObj = JSONObject()
            for ((k, v) in item.values) {
                valObj.put(k, v)
            }
            obj.put("values", valObj)
            arr.put(obj)
        }
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_LIST, arr.toString()).apply()
    }
}
