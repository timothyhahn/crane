package crane

/** Crane Exception Imports **/
import crane.exceptions.DuplicateEntityException

/** External Imports **/
import scala.collection.mutable.{ArrayBuffer, HashMap}

/** Creates world 
 *
 * @constructor creates the world
 * @param delta the delta in integers
 */
class World(var delta: Int=1) {
  // Private Variables 
  private val _entities: ArrayBuffer[Entity] = ArrayBuffer()
  private val _systems: HashMap[Int, ArrayBuffer[System]] = HashMap()

  // Public Variables 
  val groups: HashMap[String, ArrayBuffer[Entity]] = HashMap()

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
    entities.filter { entity => 
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
      entities.filter { entity => 
        val entityComponentTypes: Set[Object]  = entity.components.map(c => c.getClass).toSet
        (include.toSet subsetOf entityComponentTypes) && !(exclude.toSet subsetOf entityComponentTypes)
      }.toList
    } else {
      getEntitiesByComponents(include: _*)
    }
  }

  // Mutators

  /** Adds entity to world
   * 
   * @param entity the Entity to add to the world
   * @param second boolean signifying that the Entity has initialized this call - you should not need to use this
   */
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

  /** Removes specific entity from world
   *
   * @param entity the Entity to remove
   * @param second boolean signifying that the Entity has initialized this call - you should not need to use this
   */
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

  /** Processes the world **/
  def process() {
    val tiers = _systems.keys.toList.sortWith(_ < _)
    tiers.foreach{ tier =>
        _systems(tier).foreach{system =>
            system.process(delta)}}
  }
}
