package org.climatechangemakers.act.common.model

sealed interface Result<out SuccessType, out ErrorType>

class Success<Type>(val data: Type) : Result<Type, Nothing>

class Failure<Type>(val errorData: Type) : Result<Nothing, Type>