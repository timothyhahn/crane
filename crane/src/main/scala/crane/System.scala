package crane

/** External Import **/
import com.github.nscala_time.time.Imports._

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

abstract class TimedSystem(milliseconds: Int) extends System {

  var start = DateTime.now

  def processTime(delta: Int)

  override def process(delta: Int) {
    if((start to DateTime.now).millis >= milliseconds) {
      processTime(delta)
      start = DateTime.now
    } 
  }
}

abstract class IntervalSystem(count: Int) extends System {

  var counter = 1

  def processInterval(delta: Int)

  override def process(delta: Int) {
    counter += 1
    if(counter >= count)  {
      processInterval(delta)
      counter = 0
    }
  }
}
