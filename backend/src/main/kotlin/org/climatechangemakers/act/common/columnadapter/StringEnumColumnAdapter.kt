package org.climatechangemakers.act.common.columnadapter

import app.cash.sqldelight.ColumnAdapter
import org.climatechangemakers.act.common.serializers.StringEnum

class StringEnumColumnAdapter<EnumType>(
  enumValues: Array<out EnumType>,
) : ColumnAdapter<EnumType, String> where EnumType : StringEnum, EnumType : Enum<EnumType> {
  private val enumMap = enumValues.associateBy { it.value }
  override fun decode(databaseValue: String) = checkNotNull(enumMap[databaseValue])
  override fun encode(value: EnumType): String = value.value
}

inline fun <reified T> StringEnumColumnAdapter(): ColumnAdapter<T, String> where T : StringEnum, T : Enum<T> {
  return StringEnumColumnAdapter(T::class.java.enumConstants!!)
}