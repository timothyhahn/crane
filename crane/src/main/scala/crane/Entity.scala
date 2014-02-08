package crane

import crane.exceptions.{DeadEntityException, DuplicateTagException, MissingComponentException}
import scala.collection.mutable.ArrayBuffer
class Entity(var tag:String = "") {
  private var _world = None : Option[World]
  var alive: Boolean = true

  val components = ArrayBuffer.empty[Component]
  val uuid: String = java.util.UUID.randomUUID.toString

  def world = _world

  def world_=(world: World) {
    if(world.getEntityByTag(this.tag) != None && this.tag != "") {
      throw new DuplicateTagException
    } else {
      this._world = Some(world)
      world.addEntity(this, true)
    }
  }

  def getComponent[T <: AnyRef](componentType: T): Option[Component] = {
    if(alive)
      components find { case(component) => component.getClass == componentType }
    else
      throw new DeadEntityException
  }

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

  def kill() {
    if(alive) {
      alive = false
      _world match {
        case Some(w: World) =>
          w.removeEntity(this, true)
        case _ =>
          throw new DeadEntityException
      }
      _world = null
    } else {
      throw new DeadEntityException
    }
  }

}
