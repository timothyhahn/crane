package crane

/** The base System class - must override process **/
abstract class System {
 def process(delta: Int)
 var world: World = null
}

abstract class EntityProcessingSystem[T <: AnyRef](include: List[T], exclude: List[T] = List()) extends System {

  def processEntity(e: Entity, delta: Int)

  override def process(delta: Int) {
    val entities = world.getEntitiesWithExclusions(include, exclude)

    entities.foreach{ entity =>
      processEntity(entity, delta)
    }
  }
}
