//
// Entity.scala
//
package crane

/** Crane Exception Imports **/
import crane.exceptions.{DeadEntityException, DuplicateTagException, MissingComponentException}

import scala.reflect.ClassTag

/** External Imports **/
import scala.collection.mutable.ArrayBuffer

/** An Entity that holds components
 * @constructor create an entity
 * @param tag optional String to identify a specific entity (must be unique)
 */

object Entity {
  def apply(tag: String = "", components: ArrayBuffer[Component] = ArrayBuffer.empty[Component]): Entity = new Entity(tag, components)
}

class Entity(var tag: String = "", val components: ArrayBuffer[Component] = ArrayBuffer.empty[Component]) {
  // Private Variables
  private var _world = None : Option[World]

  // Public Variables
  var alive: Boolean = true
  val uuid: String = java.util.UUID.randomUUID.toString

  def copy(): Entity = {
    val e: Entity =  new Entity(tag, components)
    e.alive = alive
    e
  }

  override def toString: String = {
    components.map{component =>
      component.toString
    }.foldLeft("Entity: %s, Tag: %s\n".format(uuid, tag)) {_ + _}
  }

  // Accessors
  def world: Option[World] = _world

  /**
   * Returns component of a classOf[Component]
   */
  def getComponent[T <: Component : ClassTag]: Option[T] = {
    if (alive) {
      components find {
        case component: T => true
        case _ => false
      } map (_.asInstanceOf[T])
    } else {
      throw new DeadEntityException
    }
  }

  // Mutators

  /** Sets the world to a new world
   *
   * @param world the World to set
   */
  def world_=(world: World) {
    // If this world already has an entity of this tag
    if (world.getEntityByTag(this.tag) != None && this.tag != "") {
      throw new DuplicateTagException
    } else { // Otherwise
      _world = Some(world)
      world._addEntity(this, true)
    }
  }

  /** Removes component of a classOf[Component]
   *
   */
  def removeComponent[T <: Component : ClassTag]() {
    if (alive) {
      val removeEntity: Option[Component] = getComponent[T]
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
    if (alive) {
      _world match {
        case Some(w: World) =>
          w._removeEntity(this, true)
        case _ =>
          {}
      }
      _world = None
      components.clear()
      alive = false
    } else {
      throw new DeadEntityException
    }
  }

  /** Adds self to be removed from world **/
  def kill() {
    if (alive) {
      _world match {
        case Some(w: World) =>
          w.removeEntity(this)
        case _ =>
          _kill()
      }
    } else {
      throw new DeadEntityException
    }
  }
}
