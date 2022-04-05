package com.emaintec.lib.db



import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

object JsonHelper {

	@Throws(JSONException::class)
	fun accumulate(jsonObject: JSONObject, name: String, value: Any?) {
		if (!jsonObject.has(name)) {
			jsonObject.accumulate(name, value?.toString() ?: JSONObject.NULL)
		}
	}

	@Throws(JSONException::class)
	fun getStringFromJson(jsonObject: JSONObject, columnName: String, defaultValue: String?): String? {
		if (jsonObject.has(columnName)) {
			var string = jsonObject.getString(columnName)
			if (string.equals("null", ignoreCase = true)) {
				string = ""
			}
			return string
		} else {
			return defaultValue
		}
	}
	@Throws(JSONException::class)
	fun getBitmapFromJson(jsonObject: JSONObject, columnName: String, defaultValue: Bitmap?): Bitmap? {
		var vbitmap : Bitmap? = null
		if (jsonObject.has(columnName)) {
			var string = jsonObject.getString(columnName)
			string?.let {
				var decodedString = Base64.decode(string, Base64.NO_WRAP)
				vbitmap = BitmapFactory.decodeByteArray(
					decodedString,
					0,
					decodedString!!.size
				)
				return vbitmap
			}
		}
			return defaultValue

	}
	@Throws(JSONException::class)
	fun getIntFromJson(jsonObject: JSONObject, columnName: String, defaultValue: Int?): Int? {
		return if (jsonObject.has(columnName)) {
			jsonObject.getInt(columnName)
		} else {
			defaultValue
		}
	}

	@Throws(JSONException::class)
	fun getLongFromJson(jsonObject: JSONObject, columnName: String, defaultValue: Long?): Long? {
		return if (jsonObject.has(columnName)) {
			jsonObject.getLong(columnName)
		} else {
			defaultValue
		}
	}

	@Throws(JSONException::class)
	fun getBooleanFromJson(jsonObject: JSONObject, columnName: String, defaultValue: Boolean?): Boolean? {
		return if (jsonObject.has(columnName)) {
			jsonObject.getBoolean(columnName)
		} else {
			defaultValue
		}
	}

	@Throws(JSONException::class)
	fun getDoubleFromJson(jsonObject: JSONObject, columnName: String, defaultValue: Double?): Double? {
		return if (jsonObject.has(columnName)) {
			jsonObject.getDouble(columnName)
		} else {
			defaultValue
		}
	}

    @Throws(JSONException::class)
    fun getJSONObjectFromJson(jsonObject: JSONObject, columnName: String, defaultValue: JSONObject?): JSONObject? {
        return if (jsonObject.has(columnName)) {
            jsonObject.getJSONObject(columnName)
        } else {
            defaultValue
        }
    }

    @Throws(JSONException::class)
	fun getJSONArrayFromJson(jsonObject: JSONObject, columnName: String, defaultValue: JSONArray?): JSONArray? {
        return if (jsonObject.has(columnName)) {
            jsonObject.getJSONArray(columnName)
        } else {
            defaultValue
        }
    }
}
