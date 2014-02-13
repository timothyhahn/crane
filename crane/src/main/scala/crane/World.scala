package crane

/** Crane Exception Imports **/
import crane.exceptions.DuplicateEntityException

/** External Imports **/
import scala.collection.mutable.{ArrayBuffer, HashMap, SynchronizedBuffer}
import scala.collection.concurrent.{Map => ConcurrentMap}
import java.util.concurrent.ConcurrentHashMap
import collection.JavaConversions._

/** Creates world 
 *
 * @constructor creates the world
 * @param delta the delta in integers
 */

object World {
  def apply(delta: Int=1) = new World(delta)
}

class World(var delta: Int=1) {
  // Private Variables 
  private val _entities: ArrayBuffer[Entity] = new ArrayBuffer[Entity] with SynchronizedBuffer[Entity]
  private val _deleted: ArrayBuffer[Entity] = new ArrayBuffer[Entity] with SynchronizedBuffer[Entity]
  private val _added: ArrayBuffer[Entity] = new ArrayBuffer[Entity] with SynchronizedBuffer[Entity]


  private val _systems: HashMap[Int, ArrayBuffer[System]] = HashMap()

  // Public Variables 
  val groups: ConcurrentMap[String, ArrayBuffer[Entity]] = new ConcurrentHashMap[String, ArrayBuffer[Entity]]

  // Accessors 
  def entities = this._entities

  /** Gets the entity by specific tag
   * 
   * @param tag the tag as a String
   */
  def getEntityByTag(tag: String): Option[Entity] = {
    _entities find { case(e) => e.tag == tag }
  }

  /** Gets all entities by Components that are required
   *
   * @param componentTypes the components to match against (the class - not the instance)
   */
  def getEntitiesByComponents[T <: AnyRef](componentTypes: T*): List[Entity] = {
    _entities.filter { entity => 
      val entityComponentTypes: Set[Object]  = entity.components.map(c => c.getClass).toSet
      componentTypes.toSet subsetOf entityComponentTypes
    }.toList
  }

  /** Gets all entities by Components with specific exclusions
   *
   * @param include list of components to ensure are included (the class - not the instance)
   * @param exclude list of components to ensure are excluded (the class - not the instance)
   */
  def getEntitiesWithExclusions[T <: AnyRef](include: List[T], exclude: List[T] = List()) = {
    if (exclude.length > 0) {
      _entities.filter { entity => 
        val entityComponentTypes: Set[Object]  = entity.components.map(c => c.getClass).toSet
        (include.toSet subsetOf entityComponentTypes) && (exclude.toSet intersect entityComponentTypes).isEmpty
      }.toList
    } else {
      getEntitiesByComponents(include: _*)
    }
  }

  /** Get Group
   *
   * @param group the group to get as a string
   */
  def getGroup(group: String): ArrayBuffer[Entity] = { groups(group) }

  // Mutators

  /** Adds entity to world (Probably should not use - for internal use)
   * 
   * @param entity the Entity to add to the world
   * @param second boolean signifying that the Entity has initialized this call - you should not need to use this
   */
  def _addEntity(entity: Entity, second: Boolean = false) {
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

  /** Queues specific entity to be removed from world
   *
   * @param entity the Entity to add
   */
  def addEntity(entity: Entity) {
   if(!(_added contains entity)) {
      _added += entity
    }
  }

  /** Adds system to world
   *
   * @param system the System to add to the world
   * @param tier the tier that the system will run at as an integer - tiers wil be processed in order
   */
  def addSystem(system: System, tier: Int = 0) {
    if(! _systems.contains(tier)) {
      _systems(tier) = new ArrayBuffer[System]
    }
    _systems(tier) += system
    system.world = this
  }

  /** Returns entity that was created (does not add entity to the world)
   *
   * @param tag (optional) tag as a string
   */
  def createEntity(tag: String=""): Entity = {
    new Entity(tag)
  }

  /** Creates group (returns nothing)
  *
  * @param group (optional) group name as a string
  */
  def createGroup(group: String) {
    groups(group) = new ArrayBuffer[Entity]
  }

  /** Registers entity to group
   *
   * @param entity the Entity to add
   * @param group the group name as a string
   */
  def registerEntityToGroup(entity: Entity, group: String): ArrayBuffer[Entity] = {
    if(!(groups.keys.toList contains group)){
      createGroup(group)
    }
    groups(group) += entity
  }

  /** Removes specific entity from world (Probably should not use - for internal use)
   *
   * @param entity the Entity to remove
   * @param second boolean signifying that the Entity has initialized this call - you should not need to use this
   */
  def _removeEntity(entity: Entity, second: Boolean = false){
    if(_entities contains entity) {
      if(second) {
        for(group <- groups) {
          if(group._2 contains entity)
            group._2 -= entity
        }
        _entities -= entity
      } else {
        entity._kill()
      }
    }
  }

  /** Queues specific entity to be removed from world
   *
   * @param entity the Entity to remove
   */
  def removeEntity(entity: Entity) {
    if(!(_deleted contains entity)) {
      _deleted += entity
    }
  }

  /** Force clear the deleted and added queues */
  def clearQueues() {
    _deleted.foreach{ entity =>
      _removeEntity(entity)
    }
    _deleted.clear()
    _added.foreach { entity =>
      _addEntity(entity)
    }
    _added.clear()
  }

  /** Processes the world **/
  def process() {
    
    val tiers = _systems.keys.toList.sortWith(_ < _)
    tiers.foreach{ tier =>
        _systems(tier).foreach{system =>
            clearQueues()
            system.process(delta)}}
  }
  clearQueues()
}
