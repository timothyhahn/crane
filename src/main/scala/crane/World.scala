package crane

import scala.collection.mutable.{ArrayBuffer, HashMap}

class World(var delta: Int=1) {
  private val _entities: ArrayBuffer[Entity] = ArrayBuffer()
  private val _systems: HashMap[Int, ArrayBuffer[System]] = HashMap()

  val groups: HashMap[String, ArrayBuffer[Entity]] = HashMap()

  def entities = this._entities

  def getEntityByTag(tag: String): Option[Entity] = {
    _entities find { case(e) => e.tag == tag }
  }

  def getEntitiesByComponents[T <: AnyRef](componentTypes: T*): List[Entity] = {
    entities.filter { entity => 
      val entityComponentTypes: Set[Object]  = entity.components.map(c => c.getClass).toSet
      componentTypes.toSet subsetOf entityComponentTypes
    }.toList
  }

  def addEntity(entity: Entity, second: Boolean = false) {
    _entities find { case(e) => e == entity} match {
      case Some(e: Entity) =>
        // TODO: RAISE EXCEPTION
        println("BAD")
      case _ =>
        _entities += entity
    }
  }

  def addSystem(system: System, tier: Int = 0) {
    if(! _systems.contains(tier)) {
      _systems(tier) = new ArrayBuffer[System]
    }
    _systems(tier) += system
  }

  def createEntity(tag: String=""): Entity = {
    new Entity(tag)
  }

  def process() {
    val tiers = _systems.keys.toList.sortWith(_ < _)
    tiers.foreach{ tier =>
        _systems(tier).foreach{system =>
            system.process(delta)}}
  }
}
