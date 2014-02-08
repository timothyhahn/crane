package crane.exceptions

class DeadEntityException(message: String = null, cause: Throwable = null) extends RuntimeException(message, cause)
class DuplicateEntityException(message: String = null, cause: Throwable = null) extends RuntimeException(message, cause)
class DuplicateTagException(message: String = null, cause: Throwable = null) extends RuntimeException(message, cause)
class MissingComponentException(message: String = null, cause: Throwable = null) extends RuntimeException(message, cause)
