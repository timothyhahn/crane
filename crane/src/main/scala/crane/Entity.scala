package crane

/** Crane Exception Imports **/
import crane.exceptions.{DeadEntityException, DuplicateTagException, MissingComponentException}

/** External Imports **/
import scala.collection.mutable.ArrayBuffer

/** An Entity that holds components
 * @constructor create an entity
 * @param tag optional String to identify a specific entity (must be unique)
 */
class Entity(var tag:String = "") {
  // Private Variables
  private var _world = None : Option[World]

  // Public Variables
  var alive: Boolean = true
  val components = ArrayBuffer.empty[Component]
  val uuid: String = java.util.UUID.randomUUID.toString

  override def toString: String = {
    components.map{component =>
      component.toString
    }.foldLeft("Entity: %s, Tag: %s\n".format(uuid, tag)) {_ + _}
  }

  // Accessors 
  def world = _world

  /** Returns component of a classOf[Component]
   *
   * @param componentType the component to get (the class, not the instance)
   */
  def getComponent[T <: AnyRef](componentType: T): Option[Component] = {
    if(alive)
      components find { case(component) => component.getClass == componentType }
    else
      throw new DeadEntityException
  }

  // Mutators

  /** Sets the world to a new world
   *
   * @param world the World to set
   */
  def world_=(world: World) {
    // If this world already has an entity of this tag
    if(world.getEntityByTag(this.tag) != None && this.tag != "") {
      throw new DuplicateTagException
    } else { // Otherwise
      _world = Some(world)
      world._addEntity(this, true)
    }
  }

  /** Removes component of a classOf[Component]
   *
   * @param componentType the component to remove (the class, not the instance)
   */
  def removeComponent[T <: AnyRef](componentType: T) {
    if(alive) {
      val removeEntity: Option[Component] = getComponent(componentType)
      removeEntity match {
        case Some(e: Component) =>
          components -= e
        case None =>
          throw new MissingComponentException
      }
    } else {
      throw new DeadEntityException
    }
  }

  /** Kills the entity and removes it from the world it is in (Probably do not want to use - for internal use)*/
  def _kill() {
    if(alive) {
      _world match {
        case Some(w: World) =>
          w._removeEntity(this, true)
        case _ =>
          throw new DeadEntityException
      }
      _world = None
      alive = false
    } else {
      throw new DeadEntityException
    }
  }

  /** Adds self to be removed from world **/
  def kill() {
    if(alive) {
      _world match { 
        case Some(w: World) =>
          w.removeEntity(this)
        case _ =>
          throw new DeadEntityException
      }
    } else {
      throw new DeadEntityException
    }
  }
}
