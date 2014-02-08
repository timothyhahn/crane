package crane

import crane.exceptions.DuplicateEntityException
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

  def getEntitiesWithExclusions[T <: AnyRef](include: List[T], exclude: List[T] = List()) = {
    if (exclude.length > 0) {
      entities.filter { entity => 
        val entityComponentTypes: Set[Object]  = entity.components.map(c => c.getClass).toSet
        (include.toSet subsetOf entityComponentTypes) && !(exclude.toSet subsetOf entityComponentTypes)
      }.toList
    } else {
      getEntitiesByComponents(include: _*)
    }
  }

  def addEntity(entity: Entity, second: Boolean = false) {
    _entities find { case(e) => e == entity} match {
      case Some(e: Entity) =>
        throw new DuplicateEntityException
      case _ =>
        if (second) {
          _entities += entity
        } else {
          entity.world = this
        }
    }
  }

  def addSystem(system: System, tier: Int = 0) {
    if(! _systems.contains(tier)) {
      _systems(tier) = new ArrayBuffer[System]
    }
    _systems(tier) += system
    system.world = this
  }

  def createEntity(tag: String=""): Entity = {
    new Entity(tag)
  }

  def createGroup(group: String) {
    groups(group) = new ArrayBuffer[Entity]
  }


  def removeEntity(entity: Entity, second: Boolean = false){
    if(_entities contains entity){
      if(second) {
        for(group <- groups) {
          if(group._2 contains entity)
            group._2 -= entity
        }
        _entities -= entity
      } else {
        entity.kill()
      }
    }
  }

  def process() {
    val tiers = _systems.keys.toList.sortWith(_ < _)
    tiers.foreach{ tier =>
        _systems(tier).foreach{system =>
            system.process(delta)}}
  }
}
