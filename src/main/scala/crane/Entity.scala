package crane

import scala.collection.mutable.ArrayBuffer
class Entity(var tag:String = "") {
  private var _world = None : Option[World]
  val components = ArrayBuffer.empty[Component]
  val uuid = java.util.UUID.randomUUID.toString

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
    components find { case(component) => component.getClass == componentType }
  }


}
